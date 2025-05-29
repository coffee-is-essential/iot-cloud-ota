import json
import base64
import pymysql
import os
from requests_toolbelt.multipart import decoder

# DB 연결 설정
DB_HOST = os.environ['DB_HOST']
DB_USER = os.environ['DB_USER']
DB_PASSWORD = os.environ['DB_PASSWORD']
DB_NAME = os.environ['DB_NAME']
DB_PORT = int(os.environ['DB_PORT'])
TABLE_NAME = os.environ['TABLE_NAME']

#Multipart/Form-data 요청 파싱
def parse_multipart(event):
    body = event.get('body')

    if event.get('isBase64Encoded'):
        body = base64.b64decode(body)
    else:
        body = body.encode('utf-8')

    content_type = event.get('headers').get('Content-Type') or event.get('headers').get('content-type')

    multipart_data = decoder.MultipartDecoder(body, content_type)

    fields = {}

    for part in multipart_data.parts:
        content_disposition = part.headers.get(b'Content-Disposition', b'').decode()
        if 'name="' in content_disposition:
            name = content_disposition.split('name="')[1].split('"')[0]
            fields[name] = part.text
    
    return fields

# DB 연결
def connect_db():
    return pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT
    )

# RDS에 version, release_note, file_url 저장
def insert_firmware_metadata(version, release_note, file_url):
    try:
        conn = connect_db()

        with conn:
            with conn.cursor() as cursor:
                query = f"INSERT INTO {TABLE_NAME} (version, release_note, file_url) VALUES (%s, %s, %s)"
                cursor.execute(query, (version, release_note, file_url))
            conn.commit()

    except Exception as e:
        print(e)
        raise e

# GET 쿼리 파라미터 추출
def get_query_params(event):
    params = event.get('queryStringParameters') or {}
    page = int(params.get('page', 1))
    limit = int(params.get('limit', 10))
    offset = (page - 1) * limit
    return page, limit, offset

# 총 펌웨어 개수 조회
def get_total_firmware_count(conn):
    with conn.cursor() as cursor:
        query = f"SELECT COUNT(*) FROM {TABLE_NAME}"
        cursor.execute(query)
        return cursor.fetchone()[0]

# 페이지네이션된 펌웨어 조회
def fetch_firmware_page(conn, limit, offset):
    with conn.cursor() as cursor:
        query = f"SELECT id, version, release_note, created_at, updated_at FROM {TABLE_NAME} ORDER BY created_at DESC LIMIT %s OFFSET %s"
        cursor.execute(query, (limit, offset))
        return cursor.fetchall()

# 펌웨어 리스트 포맷팅
def build_firmware_list(rows):
    return [
        {
            'id': row[0],
            'version': row[1],
            'release_note': row[2],
            'created_at': row[3].strftime('%Y-%m-%d %H:%M:%S'),
            'updated_at': row[4].strftime('%Y-%m-%d %H:%M:%S'),
            'device_count': 0
        }
        for row in rows
    ]

# POST 요청 처리
def handle_post_firmware(event):
    try:
        fields = parse_multipart(event)
        version = fields.get('version')
        release_note = fields.get('release_note')
        file_url = fields.get('file_url')

        if not version or not release_note or not file_url:
            return make_response(400, {
                'code': 'MV',
                'message': 'Missing Value'
            })

        insert_firmware_metadata(version, release_note, file_url)
        return make_response(200, {
            'code': 'OK',
            'message': 'Firmware Upload Success'
        })
    
    except Exception as e:
        print(e)
        return make_response(500, {
            'code': 'ERR',
            'message': 'Internal Server Error'
        })

# GET 요청 처리
def handle_get_firmware(event):
    try:
        page, limit, offset = get_query_params(event)
        conn = connect_db()

        with conn:
            total_count = get_total_firmware_count(conn)
            rows = fetch_firmware_page(conn, limit, offset)
        
        firmware_list = build_firmware_list(rows)
        total_page = (total_count + limit - 1) // limit

        return make_response(200, {
            'code': 'OK',
            'data': firmware_list,
            'pagination': {
                'page': page,
                'limit': limit,
                'total_count': total_count,
                'total_page': total_page
            }
        })
    
    except Exception as e:
        print(e)
        return make_response(500, {
            'code': 'ERR',
            'message': 'Internal Server Error'
        })

def lambda_handler(event, context):
    operation = event.get('httpMethod')
    path = event.get('path') or ""

    if operation == 'POST':
        return handle_post_firmware(event)

    elif operation == 'GET':
        normalized_path = path.rstrip("/")

        if normalized_path == "/api/firmware":
            return handle_get_firmware(event)

        elif normalized_path.startswith("/api/firmware/"):
            return handle_get_firmware_by_id(event)

    return make_response(400, {
        'code': 'IV',
        'message': 'Invalid method or path'
    })

def make_response(status_code, body_dict):
    return {
        'statusCode': status_code,
        'headers': {
            'Access-Control-Allow-Origin': '*'
        },
        'body': json.dumps(body_dict)
    }

def fetch_firmware_by_id(conn, firmware_id):
    with conn.cursor() as cursor:
        query = f"""
            SELECT id, version, release_note, created_at, updated_at
            FROM {TABLE_NAME}
            WHERE id = %s
        """
        cursor.execute(query, (firmware_id,))
        return cursor.fetchone()

def build_firmware_detail(row):
    if row is None:
        return None

    return {
        'id': row[0],
        'version': row[1],
        'release_note': row[2],
        'created_at': row[3].strftime('%Y-%m-%d %H:%M:%S'),
        'updated_at': row[4].strftime('%Y-%m-%d %H:%M:%S'),
        'device_count': 0  # 임시 고정값
    }

def handle_get_firmware_by_id(event):
    try:
        path = event.get("path") or ""
        firmware_id = path.rstrip("/").split("/")[-1]

        if not firmware_id.isdigit():
            return make_response(400, {
                'code': 'IV',
                'message': 'Invalid firmware ID'
            })

        conn = connect_db()
        with conn:
            row = fetch_firmware_by_id(conn, int(firmware_id))

        if row is None:
            return make_response(404, {
                'code': 'NF',
                'message': 'Firmware not found'
            })

        return make_response(200, {
            'code': 'OK',
            'data': build_firmware_detail(row)
        })

    except Exception as e:
        print("Error in handle_get_firmware_by_id:", e)
        return make_response(500, {
            'code': 'ERR',
            'message': 'Internal Server Error'
        })

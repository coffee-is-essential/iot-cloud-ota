import json
import boto3
import os

s3 = boto3.client('s3')
BUCKET_NAME = os.environ['BUCKET_NAME']

def make_response(status_code, body_dict):
    return {
        'statusCode': status_code,
        'headers': {
            'Access-Control-Allow-Origin': '*'
        },
        'body': json.dumps(body_dict)
    }

def lambda_handler(event, context):
    try:
        body = json.loads(event['body'])
        version = body.get('version')
        filename = body.get('filename')
        
        if not version or not filename:
            return make_response(400, {
                'code': 'MS',
                'message': 'Missing Value'
            })

        if '/' in version or '/' in filename:
            return make_response(400, {
                'code': 'IS',
                'message': 'Invalid parameters: slash not allowed'
            })
 
        s3_key = f"{version}/{filename}"

        presigned_url = s3.generate_presigned_url(
            'put_object',
            Params={
                'Bucket': BUCKET_NAME,
                'Key': s3_key,
                'ContentType': 'application/zip'
            },
            ExpiresIn=300
        )
        
        return make_response(200, {
            'code': 'OK',
            'presignedUrl': presigned_url,
            's3Key': s3_key
        })

    except Exception as e:
        print('Error: ', e)
        return make_response(500, {
            'code': 'ERR',
            'message': 'Internal Server Error'
        })

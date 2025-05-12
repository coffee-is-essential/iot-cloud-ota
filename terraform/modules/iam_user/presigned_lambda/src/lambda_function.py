import json
import boto3
import os

s3 = boto3.client('s3')
BUCKET_NAME = os.environ['BUCKET_NAME']

def lambda_handler(event, context):
    try:
        body = json.loads(event['body'])
        version = body.get('version')
        filename = body.get('filename')
        content_type = body.get('contentType')

        if not version or not filename or not content_type:
            return {
                'statusCode': 400,
                'body': json.dumps('Missing required parameters')
            }
        
        s3_key = f"firmwares/{version}/{filename}"

        presigned_url = s3.generate_presigned_url(
            'put_object',
            Params={
                'Bucket': BUCKET_NAME,
                'Key': s3_key,
                'ContentType': content_type
            },
            ExpiresIn=300
        )
        
        return{
            'statusCode':200,
            'headers': {'Access-Control-Allow-Origin': '*'},
            'body': json.dumps({
                'presignedUrl':presigned_url,
                's3Key':s3_key
            })
        }

    except Exception as e:
        print('Error: ', e)
        return{
            'statusCode':500,
            'headers': {'Access-Control-Allow-Origin': '*'},
            'body': json.dumps('Internal Server Error')
        }
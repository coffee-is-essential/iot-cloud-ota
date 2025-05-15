module "lambda_presigner" {
  source            = "./modules/lambda"
  function_name     = "generate_firmware_presigned_url"
  description       = "Lambda function to generate presigned S3 upload URLs for firmware files"
  handler           = "lambda_function.lambda_handler"
  runtime           = "python3.10"
  timeout           = 30
  memory_size       = 128
  source_path       = "./src/presignedURL"
  policy_statements = [
    {
        effect    = "Allow"
        actions   = ["s3:ListBucket"]
        resources = [format("arn:aws:s3:::%s", aws_s3_bucket.firmware_bucket.bucket)]
    },
    {
        effect    = "Allow"
        actions   = ["s3:GetObject"]
        resources = [format("arn:aws:s3:::%s/*", aws_s3_bucket.firmware_bucket.bucket)]
    }
  ]
  environment_variables = {
        BUCKET_NAME = aws_s3_bucket.firmware_bucket.bucket
  }

  tags = {
    Environment = "Dev"
  }
}

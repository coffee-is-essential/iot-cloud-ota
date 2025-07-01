module "upload_firmware_metadata_lambda" {
  source        = "./modules/lambda"
  source_path   = "./src/upload_firmware_metadata"
  function_name = "upload_firmware_metadata"
  description   = "Lambda function to upload firmware metadata to MySQL"
  handler       = "main.lambda_handler"
  runtime       = "python3.13"
  timeout       = 30
  memory_size   = 128
  layers        = [module.firmware_upload_layer.arn]

  environment_variables = {
    DB_HOST     = local.db_credentials.host
    DB_USER     = local.db_credentials.username
    DB_PASSWORD = local.db_credentials.password
    DB_PORT     = local.db_credentials.port
    DB_NAME     = local.db_credentials.dbname
  }

  vpc_config = {
    subnet_ids         = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }
}

module "lambda_presigner" {
  source        = "./modules/lambda"
  function_name = "generate_firmware_presigned_url"
  description   = "Lambda function to generate presigned S3 upload URLs for firmware files"
  handler       = "lambda_function.lambda_handler"
  runtime       = "python3.10"
  timeout       = 30
  memory_size   = 128
  source_path   = "./src/presigned_url"
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

  vpc_config = {
    subnet_ids         = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  tags = {
    Environment = "Dev"
  }
}

module "firmware_upload_lambda" {
  source        = "./modules/lambda"
  function_name = "firmware_upload"
  description   = "Handles firmware metadata storage and retrieval in the database"
  handler       = "lambda_function.lambda_handler"
  runtime       = "python3.10"
  timeout       = 30
  memory_size   = 128
  source_path   = "./src/firmware_upload"
  layers        = [module.firmware_upload_layer.arn]

  environment_variables = {
    DB_HOST     = local.db_credentials.host
    DB_USER     = local.db_credentials.username
    DB_PASSWORD = local.db_credentials.password
    DB_PORT     = local.db_credentials.port
    DB_NAME     = local.db_credentials.dbname
    TABLE_NAME  = "Firmware"
  }

  vpc_config = {
    subnet_ids         = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  tags = {
    Environment = "Dev"
  }
}

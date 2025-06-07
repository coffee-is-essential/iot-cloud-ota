module "firmware_upload_layer" {
  source              = "./modules/lambda_layer"
  layer_name          = "pymysql-requests-layer"
  compatible_runtimes = ["python3.10"]
  layer_zip_path      = "./src/layer/python.zip"
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

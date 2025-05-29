module "firmware_upload_layer" {
  source              = "./modules/lambda_layer"
  layer_name          = "Libs"
  compatible_runtimes = ["python3.10"]
  layer_zip_path      = "./src/layer/python.zip"
}

module "firmware_upload_lambda" {
  source            = "./modules/lambda"
  function_name     = "firmware_upload"
  description       = "Handles firmware metadata storage and retrieval in the database"
  handler           = "lambda_function.lambda_handler"
  runtime           = "python3.10"
  timeout           = 30
  memory_size       = 128
  source_path       = "./src/firmware_upload"
  layers            = [module.firmware_upload_layer.arn]
  
  environment_variables = {
    DB_HOST = data.aws_ssm_parameter.db_host.value
    DB_USER = data.aws_ssm_parameter.db_username.value
    DB_PASSWORD = data.aws_ssm_parameter.db_password.value
    DB_PORT = "3306"
    DB_NAME = "applicationdb"
    TABLE_NAME = "Firmware"
  }

  tags = {
    Environment = "Dev"
  }
}

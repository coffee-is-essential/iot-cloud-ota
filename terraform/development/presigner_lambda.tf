module "lambda_presigner" {
  source                = "../modules/iam_user/presigned_lambda"
  lambda_function_name  = "Generate-PresignedURL"
  lambda_handler        = "lambda_function.lambda_handler"
  lambda_runtime        = "python3.10"
  lambda_source_path    = "../modules/iam_user/presigned_lambda/src/lambda_function.py"
  lambda_role_name      = "Generate-PresingedURL-Role"
  environment_variables = {
    BUCKET_NAME         = "iot-cloud-ota-firmware-bucket"
  }
  lambda_layer_arns     = []
}
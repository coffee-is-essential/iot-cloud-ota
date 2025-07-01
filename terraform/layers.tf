module "firmware_upload_layer" {
  source              = "./modules/lambda_layer"
  layer_name          = "pymysql-requests-layer"
  compatible_runtimes = ["python3.10"]
  layer_zip_path      = "./src/layer/python.zip"
}

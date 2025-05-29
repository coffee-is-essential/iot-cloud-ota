resource "aws_lambda_layer_version" "this" {
  layer_name          = var.layer_name
  filename            = var.layer_zip_path
  compatible_runtimes = var.compatible_runtimes
}

resource "aws_s3_bucket" "remote_state" {
  bucket = "iot-cloud-ota-tf-state-bucket"

  tags = {
    "Name"        = "iot-cloud-ota-tf-state-bucket"
    "Description" = "S3 bucket for storing Terraform remote state"
  }
}

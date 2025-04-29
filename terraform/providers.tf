provider "aws" {
  region = "ap-northeast-2"
}

terraform {
  backend "s3" {
    bucket       = "iot-cloud-ota-tf-state-bucket"
    key          = "terraform/state"
    region       = "ap-northeast-2"
    use_lockfile = true
  }
}

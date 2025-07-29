provider "aws" {
  region = "ap-northeast-2"
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.6.0"
    }
  }
  backend "s3" {
    bucket       = "iot-cloud-ota-tf-state-bucket"
    key          = "terraform/state"
    region       = "ap-northeast-2"
    use_lockfile = true
  }
}

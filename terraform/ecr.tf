resource "aws_ecr_repository" "private_ca" {
  name                 = "private-ca"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "iot-cloud-ota-private-ca-ecr-repo"
    Description = "ECR repository for Private CA in iot-cloud-ota"
  }
}

resource "aws_ecr_lifecycle_policy" "iot_cloud_ota_lifecycle_policy" {
  repository = aws_ecr_repository.private_ca.name

  policy = <<-EOF
    {
      "rules": [
        {
          "rulePriority": 1,
          "description": "Expire untagged images older than 30 days",
          "selection": {
            "tagStatus": "untagged",
            "countType": "sinceImagePushed",
            "countUnit": "days",
            "countNumber": 30
          },
          "action": {
            "type": "expire"
          }
        }
      ]
    }
  EOF
}

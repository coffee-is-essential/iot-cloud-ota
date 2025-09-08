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

resource "aws_ecr_repository" "emqx" {
  name                 = "emqx"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "iot-cloud-ota-emqx-ecr-repo"
    Description = "ECR repository for EMQX in iot-cloud-ota"
  }
}

resource "aws_ecr_lifecycle_policy" "emqx" {
  repository = aws_ecr_repository.emqx.name

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

resource "aws_ecr_repository" "backend" {
  name                 = "backend"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "iot-cloud-ota-backend-ecr-repo"
    Description = "ECR repository for Backend in iot-cloud-ota"
  }
}

resource "aws_ecr_lifecycle_policy" "backend" {
  repository = aws_ecr_repository.backend.name

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

resource "aws_ecr_repository" "mqtt_handler" {
  name                 = "mqtt-handler"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "iot-cloud-ota-mqtt-handler-ecr-repo"
    Description = "ECR repository for MQTT Handler in iot-cloud-ota"
  }
}

resource "aws_ecr_lifecycle_policy" "mqtt_handler" {
  repository = aws_ecr_repository.mqtt_handler.name

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


resource "aws_ecr_repository" "grafana" {
  name                 = "grafana"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "iot-cloud-ota-grafana-ecr-repo"
    Description = "ECR repository for Grafana in iot-cloud-ota"
  }
}

resource "aws_ecr_lifecycle_policy" "grafana" {
  repository = aws_ecr_repository.grafana.name

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

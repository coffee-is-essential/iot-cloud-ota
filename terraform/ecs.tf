resource "aws_ecs_cluster" "main" {
  name = "iot-cloud-ota-cluster"

  tags = {
    Name = "iot-cloud-ota-cluster"
  }
}

resource "aws_iam_role" "ecs_task_execution" {
  name = "iot-cloud-ota-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Principal = { Service = "ecs-tasks.amazonaws.com" }
      }
    ]
  })
}

# NOTE: ECS 컨테이너에 접속하기 위해서 SSM을 사용합니다.
resource "aws_iam_policy" "ecs_exec_ssm" {
  name        = "iot-cloud-ota-ecs-exec-ssm-policy"
  description = "Policy to allow ECS tasks to use SSM for exec"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssmmessages:CreateControlChannel",
          "ssmmessages:CreateDataChannel",
          "ssmmessages:OpenControlChannel",
          "ssmmessages:OpenDataChannel"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_exec_ssm" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = aws_iam_policy.ecs_exec_ssm.arn
}

resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "internal"
  vpc         = aws_vpc.main.id
  description = "Private DNS namespace for iot-cloud-ota"
}

resource "aws_iam_role" "backend" {
  name = "backend-app-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = { Service = "ecs-tasks.amazonaws.com" },
        Action    = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "backend" {
  name = "backend-app-policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket",
        ],
        Resource = [
          "*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "backend" {
  role       = aws_iam_role.backend.name
  policy_arn = aws_iam_policy.backend.arn
}

resource "aws_iam_role_policy_attachment" "backend_ssm" {
  role       = aws_iam_role.backend.name
  policy_arn = aws_iam_policy.ecs_exec_ssm.arn
}

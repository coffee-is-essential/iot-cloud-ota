resource "aws_cloudwatch_log_group" "emqx" {
  name              = "/ecs/emqx"
  retention_in_days = 14

  tags = {
    Name        = "iot-cloud-ota-emqx-log-group"
    Description = "CloudWatch log group for EMQX in iot-cloud-ota"
  }
}

resource "aws_ecs_task_definition" "emqx" {
  family                   = "emqx"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "emqx"
      image     = "emqx/emqx:latest"
      essential = true

      portMappings = [
        { containerPort = 1883, protocol = "tcp" },
        { containerPort = 8883, protocol = "tcp" },
        { containerPort = 18083, protocol = "tcp" }
      ]

      environment = [
        { name = "EMQX_NAME", value = "emqx" },
        { name = "EMQX_HOST", value = "127.0.0.1" }
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.emqx.name,
          "awslogs-region"        = "ap-northeast-2"
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "emqx" {
  name                   = "emqx"
  cluster                = aws_ecs_cluster.main.id
  task_definition        = aws_ecs_task_definition.emqx.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets          = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_groups  = [aws_security_group.emqx_sg.id]
    assign_public_ip = true
  }

  tags = {
    Name        = "iot-cloud-ota-emqx"
    Description = "EMQX broker service for iot-cloud-ota"
  }
}

resource "aws_cloudwatch_log_group" "private_ca" {
  name              = "/ecs/private-ca"
  retention_in_days = 14

  tags = {
    Name        = "iot-cloud-ota-private-ca-log-group"
    Description = "CloudWatch log group for Private CA in iot-cloud-ota"
  }
}

resource "aws_ecs_task_definition" "private_ca" {
  family                   = "private-ca"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "private-ca"
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/private-ca:latest"
      essential = true

      portMappings = [
        { containerPort = 80, protocol = "tcp" }
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.private_ca.name,
          "awslogs-region"        = "ap-northeast-2",
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_service_discovery_service" "private_ca" {
  name = "private-ca"
  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.main.id
    dns_records {
      type = "A"
      ttl  = 10
    }
    routing_policy = "MULTIVALUE"
  }
  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "private_ca" {
  name                   = "private-ca"
  cluster                = aws_ecs_cluster.main.id
  task_definition        = aws_ecs_task_definition.private_ca.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets          = [aws_subnet.private_a.id, aws_subnet.private_b.id]
    security_groups  = [aws_security_group.private_ca_sg.id]
    assign_public_ip = false
  }

  service_registries {
    registry_arn = aws_service_discovery_service.private_ca.arn
  }

  tags = {
    Name        = "iot-cloud-ota-private-ca-service"
    Description = "ECS service for Private CA in iot-cloud-ota"
  }
}

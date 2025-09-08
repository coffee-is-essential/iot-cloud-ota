resource "aws_lb" "grafana_alb" {
  name               = "grafana-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]
  security_groups    = [aws_security_group.grafana_alb.id]
}

resource "aws_lb_target_group" "grafana_tg" {
  name        = "grafana-tg"
  port        = 3000
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    protocol = "HTTP"
    port     = "3000"
    path     = "/api/health"
  }
}

resource "aws_lb_listener" "grafana_listener" {
  load_balancer_arn = aws_lb.grafana_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.grafana_tg.arn
  }
}

resource "aws_cloudwatch_log_group" "grafana" {
  name              = "/ecs/grafana"
  retention_in_days = 14

  tags = {
    Name        = "iot-cloud-ota-grafana-log-group"
    Description = "CloudWatch log group for Grafana in iot-cloud-ota"
  }
}

resource "aws_ecs_task_definition" "grafana" {
  family                   = "grafana"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.grafana.arn

  container_definitions = jsonencode([
    {
      name      = "grafana"
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/grafana:${var.grafana_image_tag}"
      essential = true

      portMappings = [
        { containerPort = 3000, protocol = "tcp" }
      ]

      environment = [
        { name = "GF_SECURITY_ALLOW_EMBEDDING", value = "true" },
        { name = "GF_AUTH_ANONYMOUS_ENABLED", value = "true" },
        { name = "GF_AUTH_ANONYMOUS_ORG_ROLE", value = "Viewer" },
        { name = "QUESTDB_URL", value = aws_instance.questdb.private_ip },
        { name = "QUESTDB_PORT", value = "8812" },
        { name = "QUESTDB_USER", value = local.questdb_credentials.username },
        { name = "QUESTDB_PASSWORD", value = local.questdb_credentials.password },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.grafana.name,
          "awslogs-region"        = data.aws_region.current.name,
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_service_discovery_service" "grafana" {
  name = "grafana"

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

resource "aws_ecs_service" "grafana" {
  name                   = "grafana"
  cluster                = aws_ecs_cluster.main.id
  task_definition        = aws_ecs_task_definition.grafana.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets          = [aws_subnet.private_a.id, aws_subnet.private_b.id]
    security_groups  = [aws_security_group.grafana.id, aws_security_group.ecs_internal_services.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.grafana_tg.arn
    container_name   = "grafana"
    container_port   = 3000
  }

  service_registries {
    registry_arn = aws_service_discovery_service.grafana.arn
  }

  tags = {
    Name        = "iot-cloud-ota-grafana-service"
    Description = "Grafana service for iot-cloud-ota"
  }
}

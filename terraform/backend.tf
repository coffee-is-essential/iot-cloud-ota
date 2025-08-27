resource "aws_lb" "backend_alb" {
  name               = "backend-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]
  security_groups    = [aws_security_group.backend_alb.id]
}

resource "aws_lb_target_group" "backend_tg" {
  name        = "backend-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    protocol = "HTTP"
    path     = "/actuator/health"
    port     = "traffic-port"
    matcher  = "200"
  }
}

resource "aws_lb_listener" "backend_listener" {
  load_balancer_arn = aws_lb.backend_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend_tg.arn
  }
}

resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/backend"
  retention_in_days = 14

  tags = {
    Name        = "iot-cloud-ota-backend-log-group"
    Description = "CloudWatch log group for Backend in iot-cloud-ota"
  }
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.backend.arn

  container_definitions = jsonencode([
    {
      name      = "backend"
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/backend:${var.backend_image_tag}"
      essential = true

      portMappings = [
        { containerPort = 8080, protocol = "tcp" }
      ]

      environment = [
        { name = "AWS_REGION", value = data.aws_region.current.name },
        { name = "CLOUD_AWS_S3_BUCKET", value = aws_s3_bucket.firmware_bucket.bucket },
        { name = "CLOUD_AWS_STACK_AUTO", value = tostring(false) },
        { name = "CLOUD_AWS_REGION_STATIC", value = data.aws_region.current.name },
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:mysql://${local.db_credentials.host}:${local.db_credentials.port}/${local.db_credentials.dbname}" },
        { name = "SPRING_DATASOURCE_USERNAME", value = local.db_credentials.username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = local.db_credentials.password },
        { name = "CLOUDFRONT_KEY_ID", value = aws_cloudfront_public_key.signing_key.id },
        { name = "CLOUDFRONT_DOMAIN", value = aws_cloudfront_distribution.firmware_distribution.domain_name },
        { name = "CLOUDFRONT_SECRET", value = data.aws_secretsmanager_secret.private_signing_key.name },
        { name = "CORS_ALLOWED_ORIGINS", value = "http://${aws_s3_bucket_website_configuration.frontend_bucket.website_endpoint}" },
        { name = "QUESTDB_DATASOURCE_URL", value = "jdbc:postgresql://${aws_instance.questdb.private_ip}:8812/qdb" },
        { name = "QUESTDB_DATASOURCE_USERNAME", value = local.questdb_credentials.username },
        { name = "QUESTDB_DATASOURCE_PASSWORD", value = local.questdb_credentials.password },
        { name = "MQTT_HANDLER_BASE_URL", value = "http://${aws_service_discovery_service.mqtt_handler.name}.${aws_service_discovery_private_dns_namespace.main.name}:8080" },
        { name = "REDIS_HOST", value = aws_elasticache_cluster.redis_cluster.cache_nodes[0].address },
        { name = "REDIS_PORT", value = tostring(aws_elasticache_cluster.redis_cluster.cache_nodes[0].port) },
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.backend.name,
          "awslogs-region"        = data.aws_region.current.name,
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_service_discovery_service" "backend" {
  name = "backend"

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

resource "aws_ecs_service" "backend" {
  name                              = "backend"
  cluster                           = aws_ecs_cluster.main.id
  task_definition                   = aws_ecs_task_definition.backend.arn
  desired_count                     = 1
  launch_type                       = "FARGATE"
  enable_execute_command            = true
  health_check_grace_period_seconds = 300 // NOTE: Spring Boot가 실행되기까지 오래 걸려서 기다리지 않으면 health check에 실패합니다

  network_configuration {
    subnets          = [aws_subnet.private_a.id, aws_subnet.private_b.id]
    security_groups  = [aws_security_group.backend.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend_tg.arn
    container_name   = "backend"
    container_port   = 8080
  }

  service_registries {
    registry_arn = aws_service_discovery_service.backend.arn
  }

  tags = {
    Name        = "iot-cloud-ota-backend-service"
    Description = "Backend service for iot-cloud-ota"
  }
}

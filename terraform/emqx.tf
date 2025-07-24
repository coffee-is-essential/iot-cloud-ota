resource "aws_lb" "emqx_nlb" {
  name               = "emqx-nlb"
  internal           = false
  load_balancer_type = "network"
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]
}

// NOTE: 개발 편의를 위해 1883 포트를 임시로 열고, 배포 전 닫습니다.
resource "aws_lb_target_group" "nlb_tg_mqtt" {
  name        = "emqx-mqtt-tg"
  port        = 1883
  protocol    = "TCP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    protocol = "TCP"
    port     = "1883"
  }
}

resource "aws_lb_target_group" "nlb_tg_mqtt_tls" {
  name        = "emqx-tg"
  port        = 8883
  protocol    = "TCP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    protocol = "TCP"
    port     = "8883"
  }
}

resource "aws_lb_target_group" "nlb_tg_dashboard" {
  name        = "emqx-dashboard-tg"
  port        = 18083
  protocol    = "TCP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    protocol = "TCP"
    port     = "18083"
  }
}

resource "aws_lb_listener" "nlb_listener_mqtt" {
  load_balancer_arn = aws_lb.emqx_nlb.arn
  port              = 1883
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.nlb_tg_mqtt.arn
  }
}

resource "aws_lb_listener" "nlb_listener_mqtt_tls" {
  load_balancer_arn = aws_lb.emqx_nlb.arn
  port              = 8883
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.nlb_tg_mqtt_tls.arn
  }
}

resource "aws_lb_listener" "nlb_listener_dashboard" {
  load_balancer_arn = aws_lb.emqx_nlb.arn
  port              = 18083
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.nlb_tg_dashboard.arn
  }
}

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
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/emqx:${var.emqx_image_tag}"
      essential = true

      portMappings = [
        { containerPort = 1883, protocol = "tcp" },
        { containerPort = 8883, protocol = "tcp" },
        { containerPort = 18083, protocol = "tcp" }
      ]

      environment = [
        { name = "EMQX_NAME", value = "emqx" },
        { name = "EMQX_HOST", value = "127.0.0.1" },
        { name = "NLB_HOST", value = aws_lb.emqx_nlb.dns_name },
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

resource "aws_service_discovery_service" "emqx" {
  name = "emqx"
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

resource "aws_ecs_service" "emqx" {
  name                   = "emqx"
  cluster                = aws_ecs_cluster.main.id
  task_definition        = aws_ecs_task_definition.emqx.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets          = [aws_subnet.private_a.id, aws_subnet.private_b.id]
    security_groups  = [aws_security_group.emqx_sg.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.nlb_tg_mqtt.arn
    container_name   = "emqx"
    container_port   = 1883
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.nlb_tg_mqtt_tls.arn
    container_name   = "emqx"
    container_port   = 8883
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.nlb_tg_dashboard.arn
    container_name   = "emqx"
    container_port   = 18083
  }

  service_registries {
    registry_arn = aws_service_discovery_service.emqx.arn
  }

  tags = {
    Name        = "iot-cloud-ota-emqx"
    Description = "EMQX broker service for iot-cloud-ota"
  }
}

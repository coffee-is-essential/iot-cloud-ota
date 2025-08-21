resource "aws_cloudwatch_log_group" "mqtt_handler" {
  name              = "/ecs/mqtt-handler"
  retention_in_days = 7

  tags = {
    Name        = "iot-cloud-ota-mqtt-handler-log-group"
    Description = "CloudWatch log group for MQTT handler in iot-cloud-ota"
  }
}

resource "aws_ecs_task_definition" "mqtt_handler" {
  family                   = "mqtt-handler"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "mqtt-handler"
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ap-northeast-2.amazonaws.com/mqtt-handler:${var.mqtt_handler_image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = 1883
          hostPort      = 1883
          protocol      = "tcp"
        },
        {
          containerPort = 8883
          hostPort      = 8883
          protocol      = "tcp"
        },
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "SERVER_PORT", value = ":8080" },
        { name = "MQTT_BROKER_URL", value = "${aws_lb.emqx_nlb.dns_name}:1883" },
        { name = "MQTT_CLIENT_ID", value = "mqtt-handler" },
        { name = "QUESTDB_CONF", value = "http::addr=${aws_instance.questdb.private_ip}:9000" },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.mqtt_handler.name,
          "awslogs-region"        = data.aws_region.current.name,
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_service_discovery_service" "mqtt_handler" {
  name = "mqtt-handler"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.main.id
    dns_records {
      type = "A"
      ttl  = 10
    }
  }
}

resource "aws_ecs_service" "mqtt_handler" {
  name                   = "mqtt-handler"
  cluster                = aws_ecs_cluster.main.id
  task_definition        = aws_ecs_task_definition.mqtt_handler.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets          = [aws_subnet.private_a.id, aws_subnet.private_b.id]
    security_groups  = [aws_security_group.mqtt_handler.id]
    assign_public_ip = false
  }

  service_registries {
    registry_arn = aws_service_discovery_service.mqtt_handler.arn
  }

  tags = {
    Name        = "iot-cloud-ota-mqtt-handler-service"
    Description = "ECS service for MQTT Handler in iot-cloud-ota"
  }
}

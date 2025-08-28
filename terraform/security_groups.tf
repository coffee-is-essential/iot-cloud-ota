resource "aws_security_group" "mysql_sg" {
  name        = "iot-cloud-ota-mysql-sg"
  description = "Security group for MySQL access"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
    security_groups = [
      aws_security_group.lambda_sg.id,
      aws_security_group.bastion_sg.id,
      aws_security_group.backend.id,
    ]
    description = "MySQL access from trusted security groups"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "iot-cloud-ota-mysql-sg"
    Description = "Security group for MySQL RDS instance in iot-cloud-ota"
  }
}

resource "aws_security_group" "lambda_sg" {
  name        = "iot-cloud-ota-lambda-sg"
  description = "Security group for Lambda functions"
  vpc_id      = aws_vpc.main.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name        = "iot-cloud-ota-lambda-sg"
    Description = "Security group for Lambda functions in iot-cloud-ota"
  }
}

resource "aws_security_group" "bastion_sg" {
  name        = "iot-cloud-ota-bastion-sg"
  description = "Security group for Bastion host"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access from anywhere"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name        = "iot-cloud-ota-bastion-sg"
    Description = "Security group for Bastion host in iot-cloud-ota"
  }
}

resource "aws_security_group" "emqx_sg" {
  name        = "iot-cloud-ota-emqx-sg"
  description = "Security group for EMQX broker"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 1883 # MQTT without TLS
    to_port     = 1883
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8883 # MQTT with TLS
    to_port     = 8883
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 18083 # EMQX Dashboard
    to_port     = 18083
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "private_ca_sg" {
  name        = "iot-cloud-ota-private-ca-sg"
  description = "Security group for Private CA service"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.emqx_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "backend_alb" {
  name        = "iot-cloud-ota-backend-alb-sg"
  description = "Security group for Backend ALB"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "backend" {
  name        = "iot-cloud-ota-backend-sg"
  description = "Security group for Backend service"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.backend_alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "grafana_alb" {
  name        = "iot-cloud-ota-grafana-alb-sg"
  description = "Security group for Grafana ALB"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_security_group" "grafana" {
  name        = "iot-cloud-ota-grafana-sg"
  description = "Security group for Grafana service"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 3000
    to_port         = 3000
    protocol        = "tcp"
    security_groups = [aws_security_group.grafana_alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "questdb" {
  name        = "iot-cloud-ota-questdb-sg"
  description = "Security group for Quest DB service"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  ingress {
    from_port       = 8812
    to_port         = 8812
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id, aws_security_group.backend.id]
  }

  ingress {
    from_port       = 9000
    to_port         = 9000
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id, aws_security_group.mqtt_handler.id, ]
  }

  ingress {
    from_port       = 9003
    to_port         = 9003
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  ingress {
    from_port       = 9009
    to_port         = 9009
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "nat" {
  name        = "iot-cloud-ota-nat-instance-sg"
  description = "Security group for NAT instance"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["10.0.0.0/16"] # 현재 내 VPC의 CIDR 블록
    security_groups = [aws_security_group.backend.id]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "iot-cloud-ota-nat-instance-sg"
    Description = "Security group for NAT instance in iot-cloud-ota"
  }
}

resource "aws_security_group" "mqtt_handler" {
  name        = "iot-cloud-ota-mqtt-handler-sg"
  description = "Security group for MQTT Handler service"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.backend_alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "elasticache" {
  name        = "iot-cloud-ota-elasticache-sg"
  description = "Security group for ElastiCache Redis"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.backend.id]
    description     = "Redis access from Backend and Lambda"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_internal_services" {
  name        = "iot-cloud-ota-ecs-internal-services-sg"
  description = "Allow communication between internal ECS services"
  vpc_id      = aws_vpc.main.id

  # 같은 보안 그룹 내에서의 모든 인바운드 트래픽 허용
  ingress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    self      = true
  }

  # 모든 아웃바운드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "iot-cloud-ota-ecs-internal-services-sg"
  }
}

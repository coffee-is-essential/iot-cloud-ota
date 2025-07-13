resource "aws_security_group" "mysql_sg" {
  name        = "iot-cloud-ota-mysql-sg"
  description = "Security group for MySQL access"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.lambda_sg.id]
    description     = "MySQL access from public subnets"
  }

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion_sg.id]
    description     = "MySQL access from Bastion host"
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

resource "aws_security_group" "cloudwatch_logs_sg" {
  name        = "iot-cloud-ota-cloudwatch-logs-sg"
  description = "Security group for CloudWatch Logs VPC endpoint"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.private_ca_sg.id]
    description     = "HTTPS access for CloudWatch Logs"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecr_endpoint_sg" {
  name        = "iot-cloud-ota-ecr-endpoint-sg"
  description = "Security group for ECR VPC endpoints"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.private_ca_sg.id]
    description     = "HTTPS access from Private CA service"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "iot-cloud-ota-ecr-endpoint-sg"
  }
}

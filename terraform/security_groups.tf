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

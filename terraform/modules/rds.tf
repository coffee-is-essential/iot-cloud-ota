module "rds" {
  source           = "../modules/iam_user/rds"
  db_name          = "applicationdb"
  db_identifier    = "iot-cloud-ota-rds"
  db_username      = data.aws_ssm_parameter.db_username.value
  db_password      = data.aws_ssm_parameter.db_password.value
  vpc_id           = aws_default_vpc.default.id
  web_sg_id        = aws_security_group.web_sg.id
}

resource "aws_default_vpc" "default" {
  tags = {
    Name = "default vpc"
  }
}

resource "aws_security_group" "web_sg" {
  name        = "webserver security group"
  description = "Enable HTTP access on port 80"
  vpc_id      = aws_default_vpc.default.id

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

  tags = {
    Name = "webserver security group"
  }
}

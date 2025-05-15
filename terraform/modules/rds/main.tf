resource "aws_security_group" "database_sg" {
  name        = "database security group"
  description = "Enable MySQL access on port 3306"
  vpc_id      = var.vpc_id

  ingress {
    description     = "MySQL access from anywhere"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "database security group"
  }
}

resource "aws_db_instance" "db" {
  identifier             = var.db_identifier
  engine                 = "mysql"
  engine_version         = "8.0.41"
  instance_class         = var.instance_class
  allocated_storage      = var.allocated_storage
  max_allocated_storage  = 1000
  storage_encrypted      = true
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  vpc_security_group_ids = [aws_security_group.database_sg.id]
  skip_final_snapshot    = true
  multi_az               = false
  publicly_accessible    = true

  tags = {
    Name = var.db_identifier
  }
}

data "aws_secretsmanager_secret" "mysql_credentials" {
  name = "iot-cloud-ota-mysql-credentials"
}

data "aws_secretsmanager_secret_version" "mysql_credentials" {
  secret_id = data.aws_secretsmanager_secret.mysql_credentials.id
}

locals {
  db_credentials = jsondecode(data.aws_secretsmanager_secret_version.mysql_credentials.secret_string)
  // db_credentials has the structure:
  // {
  //    "username": "USERNAME",
  //    "password": "PASSWORD",
  //    "engine": "ENGINE",
  //    "host": "DB_HOST",
  //    "port": "PORT",
  //    "dbname": "DB_NAME",
  //    "dbInstanceIdentifier": "DB_IDENTIFIER"
}

resource "aws_db_instance" "mysql" {
  # identifier           = local.db_credentials.dbInstanceIdentifier
  identifier           = "iot-cloud-ota-mysql"
  allocated_storage    = 10
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "8.0.41"
  instance_class       = "db.t3.micro"
  parameter_group_name = "default.mysql8.0"
  skip_final_snapshot  = true
  publicly_accessible  = false
  # db_name              = local.db_credentials.dbname
  db_name = "iot_cloud_ota"
  # username = local.db_credentials.username
  username = "admin"
  # password = local.db_credentials.password
  password = "iot-cloud-ota-admin-password"

  db_subnet_group_name   = aws_db_subnet_group.mysql_subnet_group.name
  vpc_security_group_ids = [aws_security_group.mysql_sg.id]
}

resource "aws_db_subnet_group" "mysql_subnet_group" {
  name       = "iot-cloud-ota-mysql-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_b.id]

  tags = {
    Name        = "iot-cloud-ota-mysql-subnet-group"
    Description = "Subnet group for MySQL RDS instance"
  }
}

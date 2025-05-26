module "rds" {
  source           = "./modules/rds"
  db_name          = "applicationdb"
  db_identifier    = "iot-cloud-ota"
  db_username      = data.aws_ssm_parameter.db_username.value
  db_password      = data.aws_ssm_parameter.db_password.value
  vpc_id           = aws_default_vpc.default.id
  web_sg_id        = aws_security_group.web_sg.id
}

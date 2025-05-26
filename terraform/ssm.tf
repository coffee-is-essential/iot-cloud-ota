# aws ssm parameter store에서 값 가져옴
data "aws_ssm_parameter" "db_username" {
  name = "/myapp/config/db_username"
}

output "db_username_value" {
  value = data.aws_ssm_parameter.db_username.value
  sensitive = true
}

data "aws_ssm_parameter" "account_id" {
  name = "/myapp/config/account_id"
}

output "account_id_value" {
  value = data.aws_ssm_parameter.account_id.value
  sensitive = true
}

data "aws_ssm_parameter" "db_password" {
  name = "/myapp/config/db_password"
  with_decryption = true
}

output "db_password_value" {
  value = data.aws_ssm_parameter.db_password.value
  sensitive = true
}

data "aws_ssm_parameter" "db_host" {
  name = "/myapp/config/db_host"
}

output "db_host_value" {
  value = "/myapp/config/db_host"
  sensitive = true
}

data "aws_ssm_parameter" "s3_bucketname" {
  name = "/myapp/config/s3_bucketname"
}

output "s3_bucketname_value" {
  value = "/myapp/config/s3_bucketname"
  sensitive = true
}

output "db_endpoint" {
  value = aws_db_instance.db.endpoint
}

output "db_security_group_id" {
  value = aws_security_group.database_sg.id
}

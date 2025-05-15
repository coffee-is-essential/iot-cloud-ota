variable "db_name" {
  description = "RDS database name"
  type        = string
}

variable "db_identifier" {
  description = "RDS instance identifier"
  type        = string
}

variable "db_username" {
  description = "RDS master username"
  type        = string
}

variable "db_password" {
  description = "RDS master password"
  type        = string
  sensitive   = true
}

variable "instance_class" {
  description = "RDS instance type"
  type        = string
  default     = "db.t4g.micro"
}

variable "allocated_storage" {
  description = "Allocated storage size in GB"
  type        = number
  default     = 20
}

variable "vpc_id" {
  description = "VPC ID for RDS"
  type        = string
}

variable "web_sg_id" {
  description = "Web server security group ID for inbound access"
  type        = string
}

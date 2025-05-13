variable "function_name" {
  description = "Lambda Function Name"
  type        = string
}

variable "description" {
  description = "Lambda Function Description"
  type        = string
  default     = ""
}

variable "handler" {
  description = "Lambda Handler Function (e.g., index.handler)"
  type        = string
}

variable "runtime" {
  description = "Lambda Runtime Environment"
  type        = string
  default     = "nodejs16.x"
}

variable "timeout" {
  description = "Lambda Function Timeout (seconds)"
  type        = number
  default     = 30
}

variable "memory_size" {
  description = "Lambda Function Memory Size (MB)"
  type        = number
  default     = 128
}

variable "source_path" {
  description = "Lambda Function Source Path (ZIP file or directory)"
  type        = string
}

variable "environment_variables" {
  description = "Lambda Function Environment Variables"
  type        = map(string)
  default     = {}
}

variable "vpc_config" {
  type = object({
    subnet_ids         = list(string)
    security_group_ids = list(string)
  })
  description = "VPC Configuration for the Lambda function"
  default     = null
}

variable "tags" {
  description = "A map of tags to assign to the Lambda function."
  type        = map(string)
  default     = {}
}

variable "log_retention_days" {
  description = "Number of days to retain logs in CloudWatch"
  type        = number
  default     = 14
}

variable "layers" {
  type        = list(string)
  description = "List of Lambda Layer ARNs to attach to the function"
  default     = []
}

variable "reserved_concurrent_executions" {
  type        = number
  description = "The number of concurrent executions reserved for the function."
  default     = -1
}

variable "policy_statements" {
  type = list(object({
    effect    = string
    actions   = list(string)
    resources = list(string)
  }))
  description = <<-EOT
    List of policy statements to attach to the Lambda function's execution role.
    Each statement should include the effect (Allow/Deny), actions, and resources.
    Example:
    [
      {
        effect    = "Allow"
        actions   = ["s3:GetObject"]
        resources = ["arn:aws:s3:::example-bucket/*"]
      }
    ]
    This will create an inline policy for the Lambda function's execution role.
  EOT
  default     = []
}

variable "managed_policy_arns" {
  type        = list(string)
  description = <<-EOT
    List of managed policy ARNs to attach to the Lambda function's execution role.
    Example:
    [
      "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole",
      "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
    ]
    This will attach the specified managed policies to the Lambda function's execution role.
  EOT
  default     = []
}

variable "lambda_function_name" {
  description = "Name for the Lambda function"
  type        = string
}

variable "lambda_handler" {
  description = "The handler of the Lambda function"
  type        = string
}

variable "lambda_runtime" {
  description = "The runtime of the Lambda function"
  type        = string
}

variable "lambda_source_path" {
  description = "Path to the source file"
  default     = null
  type        = string
}

variable "lambda_role_name" {
  description = "Name of the IAM role for Lambda"
  type        = string
}

variable "environment_variables" {
  description = "Environment variables to pass to Lambda"
  default     = {}
  type        = map(string)
}

variable "lambda_layer_arns" {
  description = "List of Lambda Layer ARNs"
  type        = list(string)
  default     = []
}
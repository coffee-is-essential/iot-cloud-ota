output "function_arn" {
  value       = aws_lambda_function.lambda.arn
  description = "The ARN of the Lambda function"
}

output "function_name" {
  value       = aws_lambda_function.lambda.function_name
  description = "The name of the Lambda function"
}

output "invoke_arn" {
  value       = aws_lambda_function.lambda.invoke_arn
  description = "The ARN used to invoke the Lambda function"
}

output "log_group_name" {
  value       = aws_cloudwatch_log_group.lambda_log_group.name
  description = "The name of the CloudWatch log group for the Lambda function"
}

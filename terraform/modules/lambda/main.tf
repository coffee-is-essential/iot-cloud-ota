data "archive_file" "source_zip" {
  type        = "zip"
  source_dir  = var.source_path
  output_path = "${path.module}/files/${var.function_name}.zip"
}

resource "aws_lambda_function" "lambda" {
  function_name = var.function_name
  description   = var.description
  handler       = var.handler
  runtime       = var.runtime
  timeout       = var.timeout
  memory_size   = var.memory_size
  role          = aws_iam_role.lambda_role.arn

  filename         = data.archive_file.source_zip.output_path
  source_code_hash = data.archive_file.source_zip.output_base64sha256

  dynamic "environment" {
    for_each = length(var.environment_variables) > 0 ? [1] : []
    content {
      variables = var.environment_variables
    }
  }

  dynamic "vpc_config" {
    for_each = var.vpc_config != null ? [var.vpc_config] : []
    content {
      subnet_ids         = var.vpc_config.subnet_ids
      security_group_ids = var.vpc_config.security_group_ids
    }
  }

  tags = merge(
    {
      Name        = var.function_name
      Description = var.description
    },
    var.tags
  )
}

resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/${var.function_name}"
  retention_in_days = var.log_retention_days
  tags              = var.tags
}

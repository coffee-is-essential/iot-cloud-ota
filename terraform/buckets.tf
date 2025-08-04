locals {
  s3_origin_id = "S3-${aws_s3_bucket.firmware_bucket.id}"
}

resource "aws_s3_bucket" "firmware_bucket" {
  bucket = "iot-cloud-ota-firmware-bucket"

  tags = {
    "Name"        = "iot-cloud-ota-firmware-bucket"
    "Description" = "S3 bucket for storing firmware files"
  }
}

# NOTE: 개발 환경에서의 임시 CORS 설정입니다.
resource "aws_s3_bucket_cors_configuration" "public_access" {
  bucket = aws_s3_bucket.firmware_bucket.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT", "POST", "HEAD"]
    allowed_origins = ["*"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}

resource "aws_cloudfront_origin_access_control" "firmware_oac" {
  name                              = "firmware-oac"
  description                       = "Origin Access Control for CloudFront to access S3 bucket"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_distribution" "firmware_distribution" {
  enabled         = true
  is_ipv6_enabled = true
  price_class     = "PriceClass_200"
  # TODO: Set the default root ojbect if needed

  origin {
    domain_name              = aws_s3_bucket.firmware_bucket.bucket_regional_domain_name
    origin_id                = local.s3_origin_id
    origin_access_control_id = aws_cloudfront_origin_access_control.firmware_oac.id
  }

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = local.s3_origin_id
    viewer_protocol_policy = "redirect-to-https"
    trusted_key_groups     = [aws_cloudfront_key_group.signing_key_group.id]

    cache_policy_id          = "658327ea-f89d-4fab-a63d-7e88639e58f6" # CachingOptimized
    origin_request_policy_id = "88a5eaf4-2fd4-4709-b370-b4c650ea3fcf" # CORS-S3Origin
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name        = "iot-cloud-ota-firmware-distribution"
    Description = "CloudFront distribution for firmware files"
  }
}

resource "aws_s3_bucket_policy" "allow_cloudfront_access" {
  bucket = aws_s3_bucket.firmware_bucket.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action   = "s3:GetObject"
        Resource = "${aws_s3_bucket.firmware_bucket.arn}/*"
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.firmware_distribution.arn
          }
        }
      }
    ]
  })
}

resource "aws_s3_bucket" "frontend_bucket" {
  bucket = "iot-cloud-ota-frontend-bucket"

  tags = {
    "Name"        = "iot-cloud-ota-frontend-bucket"
    "Description" = "S3 bucket for hosting the frontend application"
  }
}

resource "aws_s3_bucket_website_configuration" "frontend_bucket" {
  bucket = aws_s3_bucket.frontend_bucket.id

  index_document {
    suffix = "index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend_bucket" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "frontend_public_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = ["s3:GetObject"]
        Resource  = "${aws_s3_bucket.frontend_bucket.arn}/*"
      }
    ]
  })
}

resource "aws_s3_bucket_cors_configuration" "frontend_bucket_cors" {
  bucket = aws_s3_bucket.frontend_bucket.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT", "POST", "HEAD"]
    allowed_origins = ["http://iot-cloud-ota-frontend-bucket.s3-website.ap-northeast-2.amazonaws.com"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}

data "aws_secretsmanager_secret" "public_signing_key" {
  name = "cloudfront/signed_url/public_key"
}

data "aws_secretsmanager_secret_version" "public_signing_key" {
  secret_id = data.aws_secretsmanager_secret.public_signing_key.id
}

data "aws_secretsmanager_secret" "private_signing_key" {
  name = "cloudfront/signed_url/private_key"
}

data "aws_secretsmanager_secret_version" "private_signing_key" {
  secret_id = data.aws_secretsmanager_secret.private_signing_key.id
}

resource "aws_cloudfront_public_key" "signing_key" {
  name        = "cloudfront-signing-key"
  comment     = "Public key for CloudFront signed URLs"
  encoded_key = data.aws_secretsmanager_secret_version.public_signing_key.secret_string
}

resource "aws_cloudfront_key_group" "signing_key_group" {
  name  = "cloudfront-signing-key-group"
  items = [aws_cloudfront_public_key.signing_key.id]
}

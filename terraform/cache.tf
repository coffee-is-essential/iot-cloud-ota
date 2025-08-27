resource "aws_elasticache_subnet_group" "elasticache" {
  name       = "iot-cloud-ota-elasticache-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_b.id]

  tags = {
    Name        = "iot-cloud-ota-elasticache-subnet-group"
    Description = "Subnet group for ElastiCache Redis"
  }
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "iot-cloud-ota-redis"
  engine               = "redis"
  engine_version       = "7.0"
  node_type            = "cache.t2.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  port                 = 6379

  subnet_group_name  = aws_elasticache_subnet_group.elasticache.name
  security_group_ids = [aws_security_group.elasticache.id]

  tags = {
    Name        = "iot-cloud-ota-redis"
    Description = "ElastiCache Redis instance for iot-cloud-ota"
  }
}

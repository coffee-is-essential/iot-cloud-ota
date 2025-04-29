variable "name" {
  type        = string
  description = "The name of the IAM user."
}

variable "path" {
  type        = string
  description = "The path for the IAM user."
  default     = "/"
}

variable "description" {
  type        = string
  description = "The description of the IAM user."
}

variable "tags" {
  type        = map(string)
  description = "A map of tags to assign to the IAM user."
  nullable    = true
}

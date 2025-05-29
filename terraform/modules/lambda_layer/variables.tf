variable "layer_name" {
  description = "Name of the Lambda Layer"
  type        = string
}

variable "compatible_runtimes" {
  description = "Runtimes this layer is compatible with"
  type        = list(string)
}

variable "layer_zip_path" {
  description = "Path to the zipped layer content"
  type        = string
}

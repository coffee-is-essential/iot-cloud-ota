variable "backend_image_tag" {
  description = "The tag of the backend image to use."
  type        = string
  default     = "latest"
}

variable "emqx_image_tag" {
  description = "The tag of the frontend image to use."
  type        = string
  default     = "latest"
}

variable "private_ca_image_tag" {
  description = "The tag of the private CA image to use."
  type        = string
  default     = "latest"
}

locals {
  admin_users = {
    "seungwoo" = {
      name        = "seungwoo"
      path        = "/"
      description = "seungwoo's admin account."
      tags = {
        Name = "seungwoo"
      }
    },

    "minjun" = {
      name        = "minjun"
      path        = "/"
      description = "minjun's admin account."
      tags = {
        Name = "minjun"
      }
    }
  }
}

module "admin_user" {
  source   = "./modules/admin_user"
  for_each = local.admin_users

  name        = each.value.name
  path        = each.value.path
  description = each.value.description
  tags        = each.value.tags
}

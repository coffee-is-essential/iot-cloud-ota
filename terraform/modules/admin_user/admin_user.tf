resource "aws_iam_user" "admin_user" {
  name = var.name
  path = var.path
  tags = var.tags
}

resource "aws_iam_user_policy_attachment" "admin_user" {
  user       = aws_iam_user.admin_user.name
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
}

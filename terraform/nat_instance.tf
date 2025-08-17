data "aws_ami" "nat" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "owner-alias"
    values = ["amazon"]
  }
}

resource "aws_instance" "nat" {
  ami           = data.aws_ami.nat.id
  instance_type = "t3.nano"
  subnet_id     = aws_subnet.public_a.id

  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.nat.id]

  // NAT로 동작하기 위해 Source/Destination Check를 비활성화 합니다.
  source_dest_check = false

  tags = {
    Name        = "iot-cloud-ota-nat-instance"
    Description = "NAT instance for iot-cloud-ota"
  }

  user_data = <<-EOF
    #!/bin/bash
    
    sudo yum install iptables-services -y
    sudo systemctl enable iptables
    sudo systemctl start iptables

    # Enable IP forwarding
    touch /etc/sysctl.d/custom-ip-forwarding.conf
    echo "net.ipv4.ip_forward = 1" | sudo tee -a /etc/sysctl.d/custom-ip-forwarding.conf
    sudo sysctl -p /etc/sysctl.d/custom-ip-forwarding.conf

    # Configure iptables for NAT
    sudo /sbin/iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
    sudo /sbin/iptables -F FORWARD
    sudo service iptables save
  EOF
}

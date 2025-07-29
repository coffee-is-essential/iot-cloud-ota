resource "aws_ebs_volume" "questdb_volume" {
  availability_zone = aws_subnet.private_a.availability_zone
  size              = 20
  type              = "gp3"

  tags = {
    Name = "questdb-volume"
  }
}

resource "aws_instance" "questdb" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = "t3.small"
  subnet_id                   = aws_subnet.private_a.id
  vpc_security_group_ids      = [aws_security_group.questdb.id]
  associate_public_ip_address = false
  key_name                    = data.aws_key_pair.ssh_key.key_name

  user_data = <<-EOF
    #!/bin/bash
    mkfs -t ext4 /dev/nvme1n1
    mkdir -p /data
    UUID=$(blkid -s UUID -o value /dev/nvme1n1)
    sed -i "\|/data|d" /etc/fstab
    echo "UUID=$UUID /data ext4 defaults,nofail 0 2" | tee -a /etc/fstab

    mount -a

    chown ubuntu:ubuntu /data
    chmod 755 /data

    apt-get update -y
    apt-get install -y apt-transport-https ca-certificates curl software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
    add-apt-repository \
      "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable"
    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io
    usermod -aG docker ubuntu
    systemctl enable --now docker

    docker run -d --name questdb \
      -p 9000:9000 -p 9009:9009 -p 8812:8812 -p 9003:9003 \
      -v /data:/var/lib/questdb \
      questdb/questdb:latest
  EOF

  tags = {
    Name = "questdb-instance"
  }
}

resource "aws_volume_attachment" "questdb_attachment" {
  device_name = "/dev/xvdb"
  volume_id   = aws_ebs_volume.questdb_volume.id
  instance_id = aws_instance.questdb.id
}

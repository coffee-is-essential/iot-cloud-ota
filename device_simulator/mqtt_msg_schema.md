# 현재 사용중인 MQTT 메시지 스키마

- v1/{기기 ID}/update/request/firmware

```
{
    "command_id": "550e8400-e29b-41d4-a716-446655440000",
    "content": {
		    "signed_url": {
			      "url": "https://firmware.example.com/v1.2.3.bin?token=...",
					  "timeout": 10
			  },
			  "file_info": {
					  "id": 18
					  "file_hash": "abcd1234...", // SHA-256
					  "size": 1048576
			  }
		},
	  "timestamp": "2025-07-03T10:30:45Z" // Zulu Time == UTC 기준
}
```

- v1/{기기 ID}/update/request/ack

```
{
	  "status": "ACKNOWLEDGED",
	  "command_id": "550e8400-e29b-41d4-a716-446655440000",
	  "timestamp": "2025-07-03T10:30:46Z"
}
```

- v1/{기기 ID}/update/progress

```
{
	  "command_id": "550e8400-e29b-41d4-a716-446655440000",
	  "progress": 65, // %
	  "downloaded_bytes": 681574,
	  "total_bytes": 1048576,
	  "speed_kbps": 256,
	  "timestamp": "2025-07-03T10:31:20Z"
}
```

- v1/{기기 ID}/update/result

```
{
  "command_id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SUCCESS|ERROR|TIMEOUT",
  "message": "Download completed successfully",
  "checksum_verified": true,
  "download_seconds": 180,
  "timestamp": "2025-07-03T10:33:45Z"
}
```

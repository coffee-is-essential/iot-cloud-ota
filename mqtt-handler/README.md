# mqtt-handler

## 🥵 실행 방법
### 1. `init` 디렉토리로 이동
```azure
cd init
```
### 
### 2. `config.toml` 파일 생성
- 아래와 같이 config.toml 파일을 만들어주세요.
```azure
[server]
port = ":8080"
```
### 3. 빌드 또는 실행
- 빌드 후 실행
```azure
go build main.go
./main
```
- 바로 실행
```azure
go run main.go
```
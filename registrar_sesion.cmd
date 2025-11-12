curl -X POST "http://localhost:8080/api/v1/authentication/sign-up" -H "Content-Type: application/json" -d "{\"name\":\"Propietario Curl\",\"email\":\"propietario-curl@test.com\",\"password\":\"MiPassword123\",\"role\":\"arrendador\"}"

curl -X POST "http://localhost:8080/api/v1/authentication/sign-up" -H "Content-Type: application/json" -d "{\"name\":\"Inquilino Curl\",\"email\":\"inquilino-curl@test.com\",\"password\":\"MiPassword123\",\"role\":\"arrendatario\"}"

@echo off
REM Script para probar el endpoint GET /api/v1/bookings/{id}
REM Uso: test_booking_endpoint.cmd <booking_id> <jwt_token>

echo ========================================
echo Probando endpoint GET /api/v1/bookings/%1
echo ========================================
echo.

if "%1"=="" (
    echo ERROR: Falta el booking ID
    echo Uso: test_booking_endpoint.cmd [booking_id] [jwt_token]
    echo Ejemplo: test_booking_endpoint.cmd 1 eyJhbGciOiJIUzI1Ni...
    exit /b 1
)

if "%2"=="" (
    echo ERROR: Falta el JWT token
    echo Uso: test_booking_endpoint.cmd [booking_id] [jwt_token]
    echo.
    echo Primero obtén el token con iniciar_arrendatario.cmd o iniciar_arrendador.cmd
    exit /b 1
)

echo Booking ID: %1
echo Token: %2...
echo.

curl -X GET "http://localhost:8080/api/v1/bookings/%1" ^
  -H "Authorization: Bearer %2" ^
  -H "Content-Type: application/json" ^
  -v

echo.
echo ========================================
echo Prueba completada
echo ========================================


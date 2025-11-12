# JWT Authentication Configuration Guide

## ✅ Estado Actual: JWT ACTIVADO

La autenticación JWT está completamente activada y funcionando en la aplicación.

## 🔐 Configuración de Seguridad

### Endpoints Públicos (No requieren autenticación):
- `/` - Página de bienvenida
- `/api/v1/authentication/**` - Registro y login
- `/v3/api-docs/**` - Documentación OpenAPI
- `/swagger-ui.html` - Swagger UI
- `/swagger-ui/**` - Recursos de Swagger
- `/swagger-resources/**` - Recursos de Swagger
- `/webjars/**` - Librerías web

### Endpoints Protegidos (Requieren JWT):
- `/api/v1/bookings/**` - Gestión de reservas
- `/api/v1/vehicles/**` - Gestión de vehículos
- `/api/v1/reviews/**` - Gestión de reseñas
- `/api/v1/telemetry/**` - Datos de telemetría IoT
- `/api/v1/users/**` - Gestión de usuarios

## 🚀 Cómo Usar la Autenticación JWT

### 1. Registrar un nuevo usuario
```bash
POST http://localhost:8080/api/v1/authentication/sign-up
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "name": "Nombre Usuario",
  "roles": ["ROLE_ARRENDADOR"] // o ["ROLE_ARRENDATARIO"]
}
```

### 2. Iniciar sesión
```bash
POST http://localhost:8080/api/v1/authentication/sign-in
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 604800 // 7 días en segundos
}
```

### 3. Usar el token en requests protegidos
```bash
GET http://localhost:8080/api/v1/vehicles
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## ⚙️ Configuración Técnica

### application.properties
```properties
# JWT Secret Key (debe ser al menos 256 bits para HS256)
authorization.jwt.secret=MyVerySecureSecretKeyForJWTTokenSigningThatMustBeAtLeast256BitsLongForHS256Algorithm2025

# Expiración del token (en días)
authorization.jwt.expiration.days=7
```

### Roles Disponibles
- `ROLE_ARRENDADOR` - Propietario de vehículos (puede listar vehículos)
- `ROLE_ARRENDATARIO` - Inquilino (puede alquilar vehículos)

## 🔧 Configuración de Seguridad Spring

La clase `WebSecurityConfiguration` implementa:
- ✅ CORS habilitado para todos los orígenes
- ✅ CSRF deshabilitado (apropiado para APIs REST con JWT)
- ✅ Sesiones STATELESS (sin cookies de sesión)
- ✅ Filtro JWT (`BearerAuthorizationRequestFilter`)
- ✅ Manejo de errores de autenticación
- ✅ Encriptación de contraseñas con BCrypt

## 📝 Notas Importantes

1. **Secret Key**: La clave secreta actual es segura para desarrollo. Para producción, considera:
   - Usar una clave más larga (512 bits)
   - Almacenarla en variables de entorno
   - Rotar la clave periódicamente

2. **Expiración**: Los tokens expiran en 7 días. Después de eso, los usuarios deben volver a iniciar sesión.

3. **CORS**: Actualmente permite todos los orígenes (`*`). Para producción, especifica los dominios permitidos.

4. **Base de Datos**: Asegúrate de que MySQL esté corriendo y las credenciales sean correctas:
   - Host: localhost:3306
   - Database: renticar_db
   - User: root
   - Password: admin

## 🧪 Probar con Postman/Thunder Client

1. Crear una request POST a `/api/v1/authentication/sign-up`
2. Copiar el token de la respuesta
3. En las siguientes requests, añadir header:
   - Key: `Authorization`
   - Value: `Bearer <tu-token-aquí>`

## 📚 Recursos Adicionales

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs
- Welcome: http://localhost:8080/

---
**Última actualización**: 2025-11-12
**Estado**: JWT Authentication ENABLED ✅


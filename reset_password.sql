-- Script para verificar y restablecer contraseña manualmente
-- Ejecuta esto en MySQL Workbench o tu cliente MySQL

-- 1. Ver todos los usuarios y sus IDs
SELECT id, name, email, password
FROM users;

-- 2. Verificar un usuario específico (cambia el email)
SELECT id, name, email, password
FROM users
WHERE email = 'tu_email@example.com';

-- 3. Restablecer contraseña a "password123" (BCrypt hash)
-- IMPORTANTE: Cambia el ID al de tu usuario
UPDATE users
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE id = 6;

-- 4. Verificar que cambió
SELECT id, name, email, password
FROM users
WHERE id = 6;

-- 5. Ahora puedes hacer login con:
--    Email: tu_email@example.com
--    Password: password123

-- NOTAS:
-- - El hash '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' corresponde a "password123"
-- - Después de restablecer, intenta login desde el frontend
-- - Una vez que entres, podrás cambiar la contraseña desde el perfil


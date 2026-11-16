-- Tarea #81. Ejecutar solo de forma optativa en una base local de demostracion.
-- No modifica data.sql ni sobrescribe registros existentes.
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO usuarios (nombre,correo,password,rol,activo) SELECT '[DEMO VISUAL] Estudiante', 'demo.visual.estudiante@ufound.invalid', SHA2(CONCAT(UUID(),UUID()),256), 'ESTUDIANTE', false WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo='demo.visual.estudiante@ufound.invalid');
INSERT INTO usuarios (nombre,correo,password,rol,activo) SELECT '[DEMO VISUAL] Seguridad', 'demo.visual.seguridad@ufound.invalid', SHA2(CONCAT(UUID(),UUID()),256), 'SEGURIDAD', false WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo='demo.visual.seguridad@ufound.invalid');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 01', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 0 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-001 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-001 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 02', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-002 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-002 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 03', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-003 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/usb-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-003 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 04', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-004 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-004 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 05', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-005 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cartuchera-rosada.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-005 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 06', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 8 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-006 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-006 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 07', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 10 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-007 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-007 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 08', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 11 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-008 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-008 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 09', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 13 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-009 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-009 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 10', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 15 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-010 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/folder-azul.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-010 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 11', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 17 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-011 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-011 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 12', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 18 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-012 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cargador-blanco.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-012 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 13', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 20 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-013 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/laptop-negra.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-013 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 14', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 22 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-014 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-014 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 15', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 23 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-015 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-015 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 16', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 25 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-016 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-016 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 17', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 27 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-017 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cartuchera-rosada.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-017 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 18', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 29 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-018 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-018 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 19', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-019 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-019 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 20', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 33 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-020 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/reloj-dorado.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-020 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 21', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 36 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-021 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-021 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 22', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 39 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-022 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-022 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 23', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 42 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-023 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-023 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 24', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 46 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-024 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cargador-blanco.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-024 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 25', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 49 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-025 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/laptop-negra.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-025 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 26', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 52 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-026 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-026 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 27', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 55 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-027 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/usb-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-027 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 28', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 59 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-028 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-028 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 29', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 60 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-029 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-029 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 30', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 64 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-030 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-030 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 31', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 68 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-031 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-031 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 32', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 72 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-032 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/reloj-dorado.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-032 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 33', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 76 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-033 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-033 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 34', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 80 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-034 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/folder-azul.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-034 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 35', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 84 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-035 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-035 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 36', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 89 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-036 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-036 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 37', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 90 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-037 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/laptop-negra.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-037 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 38', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 95 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-038 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-038 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 39', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 101 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-039 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/usb-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-039 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 40', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 107 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-040 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-040 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 41', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 113 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-041 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cartuchera-rosada.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-041 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 42', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 119 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-042 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-042 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 43', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 120 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-043 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-043 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 44', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 129 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-044 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/reloj-dorado.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-044 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 45', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 139 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-045 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-045 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 46', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 149 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-046 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/folder-azul.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-046 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 47', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 150 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-047 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-047 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_perdidos (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 48', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 179 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:PER-048 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cargador-blanco.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_perdidos WHERE descripcion='UFOUND_DEMO_VISUAL_V1:PER-048 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO historial_estados (tipo_objeto,id_objeto,estado_anterior,estado_nuevo,fecha_cambio,id_usuario_responsable)
SELECT 'PERDIDO', o.id_objeto, NULL, o.estado, o.fecha_hora, o.id_usuario FROM objetos_perdidos o JOIN usuarios u ON u.id_usuario=o.id_usuario
WHERE u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND o.descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' AND NOT EXISTS (SELECT 1 FROM historial_estados h WHERE h.tipo_objeto='PERDIDO' AND h.id_objeto=o.id_objeto);
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 01', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 0 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-001 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-001 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 02', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-002 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-002 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 03', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-003 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/usb-azul.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-003 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 04', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-004 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-004 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 05', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 10 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-005 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cartuchera-rosada.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-005 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 06', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 13 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-006 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-006 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 07', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 15 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-007 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-007 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 08', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 18 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-008 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-008 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 09', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 21 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-009 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-009 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 10', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 23 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-010 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/folder-azul.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-010 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 11', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 26 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-011 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-011 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 12', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 29 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-012 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cargador-blanco.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-012 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 13', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-013 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/laptop-negra.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-013 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 14', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 34 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-014 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-014 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 15', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 38 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-015 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-015 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 16', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 42 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-016 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-016 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 17', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 46 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-017 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cartuchera-rosada.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-017 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 18', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 50 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-018 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-018 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 19', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 54 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-019 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-019 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 20', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 59 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-020 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/reloj-dorado.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-020 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 21', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 60 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-021 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-021 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 22', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 65 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-022 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-022 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 23', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 71 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-023 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-023 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 24', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 77 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-024 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cargador-blanco.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-024 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Laptop negra 25', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 83 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-025 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/laptop-negra.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-025 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Audifonos blancos 26', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 89 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-026 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/audifonos-blancos.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-026 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] USB azul 27', 'TECNOLOGIA', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 90 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-027 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/usb-azul.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-027 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cuaderno rojo 28', 'UTILES', 'CAFETERIA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 99 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-028 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/cuaderno-rojo.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-028 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cartuchera rosada 29', 'UTILES', 'E2', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 109 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-029 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-029 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Tomatodo azul 30', 'BOTELLAS', 'LABORATORIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 119 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-030 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/tomatodo-azul.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-030 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Billetera azul 31', 'ACCESORIOS', 'BIBLIOTECA', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 120 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-031 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/billetera-azul.jpg', 'REPORTADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-031 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Reloj dorado 32', 'ACCESORIOS', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 129 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-032 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/reloj-dorado.jpg', 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-032 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Casaca azul 33', 'ROPA', 'GIMNASIO_F1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 139 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-033 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/casaca-azul.jpg', 'EN_REVISION', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-033 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Folder azul 34', 'DOCUMENTOS', 'SALAS_ESTUDIO', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 149 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-034 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/folder-azul.jpg', 'ENTREGADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-034 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Llaves plateadas 35', 'OTROS', 'PATIOS', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 150 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-035 | Registro ficticio para la exposicion. No corresponde a un objeto real.', '/fotos/llaves-plateadas.jpg', 'ARCHIVADO', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-035 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO objetos_encontrados (nombre,categoria,ubicacion,fecha_hora,descripcion,imagen_url,estado,id_usuario)
SELECT '[DEMO] Cargador blanco 36', 'TECNOLOGIA', 'A1', TIMESTAMP(DATE_SUB(CURRENT_DATE(), INTERVAL 179 DAY), '10:00:00'), 'UFOUND_DEMO_VISUAL_V1:ENC-036 | Registro ficticio para la exposicion. No corresponde a un objeto real.', NULL, 'DISPONIBLE_OFICINA', u.id_usuario
FROM usuarios u WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM objetos_encontrados WHERE descripcion='UFOUND_DEMO_VISUAL_V1:ENC-036 | Registro ficticio para la exposicion. No corresponde a un objeto real.');
INSERT INTO historial_estados (tipo_objeto,id_objeto,estado_anterior,estado_nuevo,fecha_cambio,id_usuario_responsable)
SELECT 'ENCONTRADO', o.id_objeto, NULL, o.estado, o.fecha_hora, o.id_usuario FROM objetos_encontrados o JOIN usuarios u ON u.id_usuario=o.id_usuario
WHERE u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND o.descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' AND NOT EXISTS (SELECT 1 FROM historial_estados h WHERE h.tipo_objeto='ENCONTRADO' AND h.id_objeto=o.id_objeto);
COMMIT;
SELECT 'Perdidos demo' AS conjunto, COUNT(*) AS total FROM objetos_perdidos WHERE descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' UNION ALL SELECT 'Encontrados demo', COUNT(*) FROM objetos_encontrados WHERE descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%';

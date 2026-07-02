INSERT INTO usuarios (id_usuario, nombre, correo, password, rol, activo) VALUES
(1, 'Marcelo Loayza', '20235694@aloe.ulima.edu.pe', '123456', 'ESTUDIANTE', true),
(2, 'Seguridad ULima', 'seguridad@ulima.edu.pe', '123456', 'SEGURIDAD', true),
(3, 'Oficina de Objetos Perdidos', 'oficina@ulima.edu.pe', '123456', 'OFICINA', true)
ON DUPLICATE KEY UPDATE
nombre = VALUES(nombre),
correo = VALUES(correo),
password = VALUES(password),
rol = VALUES(rol),
activo = VALUES(activo);


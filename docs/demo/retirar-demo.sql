-- Retira solo los objetos demo que no se vincularon a coincidencias ni fueron gestionados por otra cuenta.
SET NAMES utf8mb4;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS demo_perdidos_retirables;
DROP TEMPORARY TABLE IF EXISTS demo_encontrados_retirables;
CREATE TEMPORARY TABLE demo_perdidos_retirables AS SELECT o.id_objeto FROM objetos_perdidos o JOIN usuarios u ON u.id_usuario=o.id_usuario
WHERE o.descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' AND u.correo='demo.visual.estudiante@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM coincidencias c WHERE c.id_objeto_perdido=o.id_objeto)
AND NOT EXISTS (SELECT 1 FROM historial_estados h WHERE h.tipo_objeto='PERDIDO' AND h.id_objeto=o.id_objeto AND h.id_usuario_responsable<>u.id_usuario);
DELETE h FROM historial_estados h JOIN demo_perdidos_retirables d ON h.id_objeto=d.id_objeto WHERE h.tipo_objeto='PERDIDO';
DELETE o FROM objetos_perdidos o JOIN demo_perdidos_retirables d ON o.id_objeto=d.id_objeto;
CREATE TEMPORARY TABLE demo_encontrados_retirables AS SELECT o.id_objeto FROM objetos_encontrados o JOIN usuarios u ON u.id_usuario=o.id_usuario
WHERE o.descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' AND u.correo='demo.visual.seguridad@ufound.invalid' AND u.activo=false AND u.nombre LIKE '[DEMO VISUAL] %'
AND NOT EXISTS (SELECT 1 FROM coincidencias c WHERE c.id_objeto_encontrado=o.id_objeto)
AND NOT EXISTS (SELECT 1 FROM historial_estados h WHERE h.tipo_objeto='ENCONTRADO' AND h.id_objeto=o.id_objeto AND h.id_usuario_responsable<>u.id_usuario);
DELETE h FROM historial_estados h JOIN demo_encontrados_retirables d ON h.id_objeto=d.id_objeto WHERE h.tipo_objeto='ENCONTRADO';
DELETE o FROM objetos_encontrados o JOIN demo_encontrados_retirables d ON o.id_objeto=d.id_objeto;
DELETE u FROM usuarios u WHERE u.correo IN ('demo.visual.estudiante@ufound.invalid','demo.visual.seguridad@ufound.invalid') AND u.nombre LIKE '[DEMO VISUAL] %' AND u.activo=false AND NOT EXISTS (SELECT 1 FROM objetos_perdidos p WHERE p.id_usuario=u.id_usuario) AND NOT EXISTS (SELECT 1 FROM objetos_encontrados e WHERE e.id_usuario=u.id_usuario) AND NOT EXISTS (SELECT 1 FROM historial_estados h WHERE h.id_usuario_responsable=u.id_usuario) AND NOT EXISTS (SELECT 1 FROM notificaciones n WHERE n.id_usuario=u.id_usuario);
COMMIT;
SELECT 'Perdidos demo conservados por vinculos o gestion' AS conjunto,COUNT(*) AS total FROM objetos_perdidos WHERE descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%' UNION ALL SELECT 'Encontrados demo conservados por vinculos o gestion',COUNT(*) FROM objetos_encontrados WHERE descripcion LIKE 'UFOUND_DEMO_VISUAL_V1:%';

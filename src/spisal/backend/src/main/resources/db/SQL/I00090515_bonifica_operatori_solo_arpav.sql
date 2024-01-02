UPDATE verifica_imp SET operatore_id = NULL WHERE operatore_id IS NOT NULL;
DELETE FROM operatore WHERE created_by NOT LIKE '%importer%' 
AND (service_delivery_location_id IS NULL or EXISTS (SELECT 1 FROM service_delivery_location sdl WHERE sdl.internal_id=service_delivery_location_id AND sdl.code LIKE '%arpav%')) 
AND  EXISTS (SELECT 1 FROM code_value cv WHERE cv.id=ente AND cv.code = 'ARPAV');
DELETE FROM z_operatore WHERE created_by NOT LIKE '%importer%' 
AND (service_delivery_location_id IS NULL or EXISTS (SELECT 1 FROM service_delivery_location sdl WHERE sdl.internal_id=service_delivery_location_id AND sdl.code LIKE '%arpav%')) 
AND  EXISTS (SELECT 1 FROM code_value cv WHERE cv.id=ente AND cv.code = 'ARPAV');
-- ATTENZIONE: script limitato all'anno 2020 perché negli anni precedenti
-- il campo d'appoggio numero_old potrebbe essere valorizzato

UPDATE procpratiche pr set pr.numero_old = (SELECT min(zp.REV) from z_procpratiche zp where zp.internal_id = pr.internal_id and zp.serviceDeliveryLocation is not NULL) WHERE pr.`data`> '2020-01-01 00:00:00';
UPDATE procpratiche pr set pr.sdl_start_id =
(SELECT zp.serviceDeliveryLocation from z_procpratiche zp where zp.internal_id = pr.internal_id and zp.serviceDeliveryLocation is not NULL and zp.REV = pr.numero_old ) WHERE pr.`data`> '2020-01-01 00:00:00';
UPDATE procpratiche pr set pr.numero_old = NULL WHERE pr.`data`> '2020-01-01 00:00:00';
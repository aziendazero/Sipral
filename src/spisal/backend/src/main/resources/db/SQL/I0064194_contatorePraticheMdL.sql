/*
 * Aggiornamento contatore pratiche di mdl
 * ESEGUITO IN PRODUZIONE IL 23/11/2018
 */
insert into persone_pratiche (is_active, created_by, cf, pratica_id, sdl_id) select distinct 1, 'phi-esb', persona.fiscal_code, p.internal_id, s.internal_id from person persona
join medicina_lavoro m on m.patient_id=persona.internal_id
join procpratiche p on (p.medicina_lavoro_id=m.internal_id and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where s.area = 'phidic.spisal.pratiche.type.workmedicine_V0' and m.is_active=1 and persona.fiscal_code is not null;
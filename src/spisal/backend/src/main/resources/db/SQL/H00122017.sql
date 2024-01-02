insert into persone_provvedimenti (is_active, created_by, cf, pratica_id, sdl_id, prov_id) 
select distinct 1, 'phi-esb', persona.fiscal_code, p.internal_id, sdl.internal_id, prov.internal_id from sedi_persone persona
join cariche car on (car.sedi_persone_id = persona.internal_id and car.is_active=1)
join provvedimenti prov on (car.internal_id=prov.carica_id and prov.is_active=1)
join soggetto sogg on (sogg.code='phidic.spisal.segnalazioni.targetsource.ditta_V0' and sogg.is_active=1)
join attivita a on (a.internal_id=prov.attivita_id and a.is_active=1)
join procpratiche p on (a.procpratiche_id=p.internal_id and p.is_active=1 and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area is not null and persona.fiscal_code is not null;
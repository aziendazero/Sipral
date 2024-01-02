insert into persone_pratiche (is_active, created_by, cf, pratica_id, sdl_id) select distinct 1, 'phi-esb', persona.fiscal_code, p.internal_id, s.internal_id from person persona
join infortuni i on i.person_id=persona.internal_id
join procpratiche p on (i.idprocpratica=p.internal_id and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where s.area = 'phidic.spisal.pratiche.type.workaccident_V0' and i.is_active=1 and persona.fiscal_code is not null and;

insert into persone_pratiche (is_active, created_by, cf, pratica_id, sdl_id) select distinct 1, 'phi-esb', persona.fiscal_code, p.internal_id, s.internal_id from person persona
join malattie_professionali mal on (mal.riferimento_utente_id=persona.internal_id and mal.riferimento='phidic.spisal.segnalazioni.targetsource.utente_V0')
join procpratiche p on (mal.internal_id=p.malattia_professionale_id and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where s.area = 'phidic.spisal.pratiche.type.workddisease_V0' and mal.is_active=1 and persona.fiscal_code is not null;

insert into persone_provvedimenti (is_active, created_by, cf, pratica_id, sdl_id, prov_id) select distinct 1, 'phi-esb', persona.fiscal_code, p.internal_id, sdl.internal_id, prov.internal_id from person persona
join soggetto sogg on (sogg.code='phidic.spisal.segnalazioni.targetsource.utente_V0' and sogg.utente_id=persona.internal_id and sogg.is_active=1)
join provvedimenti prov on (sogg.internal_id=prov.soggetto_id and prov.is_active=1)
join attivita a on (a.internal_id=prov.attivita_id and a.is_active=1)
join procpratiche p on (a.procpratiche_id=p.internal_id and p.is_active=1 and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area is not null and persona.fiscal_code is not null;

/*
 * Aggiornamento contatore pratiche di mdl
 * ESEGUITO IN PRODUZIONE IL 23/11/2018
 */
delete from persone_pratiche where pratica_id in (select p.internal_id from procpratiche p where p.status_code = 'status.generic.nullified_V0');
delete from ditte_pratiche where pratica_id in (select p.internal_id from procpratiche p where p.status_code = 'status.generic.nullified_V0');

delete from persone_provvedimenti where pratica_id in (select p.internal_id from procpratiche p where p.status_code = 'status.generic.nullified_V0');
delete from ditte_provvedimenti where pratica_id in (select p.internal_id from procpratiche p where p.status_code = 'status.generic.nullified_V0');
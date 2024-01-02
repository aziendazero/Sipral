delete from persone_associazioni;

/*--INFORTUNI SUL LAVORO--*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.painfortuni1_V0' 
from person pers
join infortuni i on i.person_id=pers.internal_id
join procpratiche p on (i.idprocpratica=p.internal_id)
join service_delivery_location s on p.serviceDeliveryLocation = s.internal_id
where i.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.workaccident_V0');


/*MALATTIA PROFESSIONALE*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.pamalattia1_V0' 
from person pers
join malattie_professionali mal on (mal.riferimento_utente_id=pers.internal_id and mal.riferimento='phidic.spisal.segnalazioni.targetsource.utente_V0')
join procpratiche p on (mal.internal_id=p.malattia_professionale_id and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location s on p.serviceDeliveryLocation = s.internal_id
where mal.is_active=1 and p.is_active=1
and s.area in ('phidic.spisal.pratiche.type.workddisease_V0');


/*VIGILANZA CANTIERE*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.pavigicantieri2_V0' 
from procpratiche prat
join vigilanza v on v.internal_id = prat.vigilanza_id
join cantieri c on c.internal_id=v.cantiere_id
join committenti comm on comm.cantiere_id = c.internal_id
join person pers on pers.internal_id=comm.person_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where v.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0' and c.is_active=1 and comm.is_active=1 
and s.area in ('phidic.spisal.pratiche.type.supervision_V0') and v.`type`='phidic.spisal.segnalazioni.supervision.types.yard_V0';


/*VIGILANZA AMIANTO*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.pavigiamianto2_V0' 
from procpratiche prat
join vigilanza v on v.internal_id = prat.vigilanza_id
join committente_vigilanza cv on cv.vigilanza_id=v.internal_id
join person pers on pers.internal_id=cv.committente_utente_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where v.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and cv.committente = 'phidic.spisal.segnalazioni.targetsource.utente_V0'
and s.area in ('phidic.spisal.pratiche.type.supervision_V0') and v.`type`='phidic.spisal.segnalazioni.supervision.types.asbestos_V0';


/*ESPRESSIONE PARERI TECNICI*//*RICHIEDENTE UTENTE DELLA PRATICA*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.paespressione1_V0' 
from procpratiche prat
join pratiche_riferimenti pr on (pr.internal_id = prat.pratiche_riferimenti_id and pr.richiedente='phidic.spisal.segnalazioni.targetsource.utente_V0')
join person pers on pers.internal_id=pr.richiedente_utente_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.technicaladvice_V0') ;


/*ESPRESSIONE PARERI TECNICI*//*ATTIVITÀ GENERALE*//*PROMOZIONE SALUTE VITA*//*INFORMAZIONE*//*FORMAZIONE*//*SPORTELLO*//*RIFERITO A DELLA PRATICA*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.pavigiespressione2_V0' 
from procpratiche prat
join pratiche_riferimenti pr on (pr.internal_id = prat.pratiche_riferimenti_id and pr.riferimento='phidic.spisal.segnalazioni.targetsource.utente_V0')
join person pers on pers.internal_id=pr.riferimento_utente_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 
'phidic.spisal.pratiche.type.information_V0', 'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0') ;


/*MEDICINA DEL LAVORO*//*ATTIVITÀ GENERALE*//*PROMOZIONE SALUTE VITA*//*INFORMAZIONE*//*FORMAZIONE*//*SPORTELLO*//* SOGGETTO ATTIVITA ELEMENTARE*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type)
select distinct 1, 'phi-esb', pers.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.paattivita2_V0' 
from person pers
join soggetto sogg on (sogg.utente_id=pers.internal_id and sogg.code='phidic.spisal.segnalazioni.targetsource.utente_V0')
join attivita a on a.internal_id=sogg.attivita_id
join procpratiche p on a.procpratiche_id=p.internal_id 
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where a.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0' and sogg.is_active=1
and sdl.area is not null and sdl.area in ('phidic.spisal.pratiche.type.workmedicine_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0');

/* MEDICINA DEL LAVORO*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pers.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.pamedicina1_V0' 
from person pers
join medicina_lavoro m on m.patient_id=pers.internal_id
join procpratiche p on p.medicina_lavoro_id=m.internal_id
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where p.status_code != 'status.generic.nullified_V0' and s.area = 'phidic.spisal.pratiche.type.workmedicine_V0' and m.is_active=1;


/*SOGGETTI DI PROVVEDIMENTI*/
insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) select distinct 1, 'phi-esb', pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.painfortuni2_V0' 
from provvedimenti prov
join attivita a on (a.internal_id=prov.attivita_id and a.is_active=1)
join procpratiche prat on (prat.internal_id=prov.procpratiche_id and prat.status_code != 'status.generic.nullified_V0')
join soggetto sogg on (sogg.internal_id=prov.soggetto_id and sogg.code='phidic.spisal.segnalazioni.targetsource.utente_V0')
join person pers on pers.internal_id=sogg.utente_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where sogg.is_active=1 and prov.is_active=1 and prat.is_active=1 
and s.area in ('phidic.spisal.pratiche.type.workaccident_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 'phidic.spisal.pratiche.type.supervision_V0');


/*DESTINATARI DI PROVVEDIMENTI*/
insert into persone_associazioni (is_active, created_by, sedi_persone_id, procpratiche_id, type)
select distinct 1, 'phi-esb', sede_pers.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.personeassociation.painfortuni2_V0' from sedi_persone sede_pers
join cariche car on (car.sedi_persone_id = sede_pers.internal_id)
join provvedimenti prov on (car.internal_id=prov.carica_id)
join soggetto sogg on (sogg.code='phidic.spisal.segnalazioni.targetsource.ditta_V0')
join attivita a on (a.internal_id=prov.attivita_id)
join procpratiche prat on (a.procpratiche_id=prat.internal_id and prat.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on prat.servicedeliverylocation = sdl.internal_id
where sogg.is_active=1 and prov.is_active=1 and prat.is_active=1 and car.is_active=1 and a.is_active=1
and sdl.area in ('phidic.spisal.pratiche.type.workaccident_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 'phidic.spisal.pratiche.type.supervision_V0');




delete from ditte_associazioni;


/*--INFORTUNI SUL LAVORO--*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type)
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni1_V0' 
from persone_giuridiche pg
join infortuni i on i.persone_giuridiche_id=pg.internal_id
join procpratiche p on i.idprocpratica=p.internal_id
join service_delivery_location s on p.serviceDeliveryLocation = s.internal_id
where i.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0' 
and s.area in ('phidic.spisal.pratiche.type.workaccident_V0');

insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type)
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni2_V0' 
from persone_giuridiche pg
join infortuni i on ((i.persone_giuridiche_id=pg.internal_id and i.place='phidic.spisal.segnalazioni.place.owncompany_V0') or (i.pg_ext_id=pg.internal_id and i.place='phidic.spisal.segnalazioni.place.company_V0'))
join procpratiche p on i.idprocpratica=p.internal_id
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where i.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0'
and sdl.area = 'phidic.spisal.pratiche.type.workaccident_V0';


/*RIFERTO A DELLA PRATICA*/
/*ESPRESSIONE PARERI TECNICI*//*ATTIVITÀ GENERALE*//*PROMOZIONE SALUTE VITA*//*INFORMAZIONE*//*FORMAZIONE*//*SPORTELLO*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.daespressione2_V0' 
from procpratiche prat
join pratiche_riferimenti pr on (pr.internal_id = prat.pratiche_riferimenti_id and pr.riferimento='phidic.spisal.segnalazioni.targetsource.ditta_V0')
join persone_giuridiche pg on pg.internal_id=pr.riferimento_ditta_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0') ;


/*MALATTIA PROFESSIONALE*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.damalattia1_V0' 
from persone_giuridiche pg
join ditte_malattie dmal on (dmal.persone_giuridiche_id=pg.internal_id) 
join malattie_professionali mal on (mal.internal_id=dmal.malattia_professionale_id)
join procpratiche p on (mal.internal_id=p.malattia_professionale_id )
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where pg.is_active=1 and dmal.is_active=1 and mal.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0' and
sdl.area = 'phidic.spisal.pratiche.type.workddisease_V0';


/*DITTA CONTROLLATE VIGILANZA CANTIERI*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.davigicantieri1_V0' 
from persone_giuridiche pg
join persona_giuridica_sede pgs on (pgs.persona_giuridica_id=pg.internal_id and pgs.is_active=1)
join vigilanza vig on (vig.internal_id=pgs.vigilanza_id )
join procpratiche p on (vig.internal_id=p.vigilanza_id )
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where p.is_active=1 and p.status_code != 'status.generic.nullified_V0' and vig.is_active=1 
and vig.type='phidic.spisal.segnalazioni.supervision.types.yard_V0' and pgs.checked=1 
and sdl.area = 'phidic.spisal.pratiche.type.supervision_V0';
					
					
/*DITTA VIGILATE VIGILANZA AZIENDA*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.davigiazienda1_V0' 
from persone_giuridiche pg
join persona_giuridica_sede pgs on (pgs.persona_giuridica_id=pg.internal_id )
join vigilanza vig on (vig.internal_id=pgs.vigilanza_id)
join procpratiche p on (vig.internal_id=p.vigilanza_id)
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where vig.type='phidic.spisal.segnalazioni.supervision.types.generic_V0' 
and vig.is_active=1 and pgs.is_active=1 and p.is_active=1 and p.status_code != 'status.generic.nullified_V0' 
and sdl.area = 'phidic.spisal.pratiche.type.supervision_V0';

	
/*DITTA LAVORI VIGILANZA AMIANTO*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto1_V0' 
from persone_giuridiche pg
join persona_giuridica_sede pgs on (pgs.persona_giuridica_id=pg.internal_id and pgs.is_active=1)
join vigilanza vig on (vig.internal_id=pgs.vigilanza_id and vig.type is not null)
join procpratiche p on (vig.internal_id=p.vigilanza_id and p.is_active=1 and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area = 'phidic.spisal.pratiche.type.supervision_V0'
and vig.type='phidic.spisal.segnalazioni.supervision.types.asbestos_V0' and vig.is_active=1;


/*SITO DELLA BONIFICA VIGILANZA AMIANTO*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto2_V0' 
from persone_giuridiche pg
join vigilanza vig on (vig.sito_bonifica_ditta_id=pg.internal_id and vig.type is not null)
join procpratiche p on (vig.internal_id=p.vigilanza_id and p.is_active=1 and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area = 'phidic.spisal.pratiche.type.supervision_V0'
and vig.type='phidic.spisal.segnalazioni.supervision.types.asbestos_V0' and vig.is_active=1;


/*COMMITTENTE DI BONIFICA VIGILANZA AMIANTO */ 
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto3_V0' 
from procpratiche prat
join vigilanza v on v.internal_id = prat.vigilanza_id
join committente_vigilanza cv on cv.vigilanza_id=v.internal_id
join persone_giuridiche pg on pg.internal_id=cv.committente_ditta_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where v.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and cv.committente = 'phidic.spisal.segnalazioni.targetsource.ditta_V0'
and s.area in ('phidic.spisal.pratiche.type.supervision_V0') and v.`type`='phidic.spisal.segnalazioni.supervision.types.asbestos_V0';


/*ESPRESSIONE PARERI TECNICI*//*RICHIEDENTE DITTA*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.daespressione1_V0' 
from procpratiche prat
join pratiche_riferimenti pr on (pr.internal_id = prat.pratiche_riferimenti_id and pr.richiedente='phidic.spisal.segnalazioni.targetsource.ditta_V0')
join persone_giuridiche pg on pg.internal_id=pr.richiedente_ditta_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.technicaladvice_V0') ;


/*FORMAZIONE/INFORMAZIONE /CONSOULTING*//* UBICAZIONE DELLA PRATICA*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.daformazione2_V0' 
from procpratiche prat
join pratiche_riferimenti pr on pr.internal_id = prat.pratiche_riferimenti_id
join persone_giuridiche pg on pg.internal_id=pr.ubicazione_ditta_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 'phidic.spisal.pratiche.type.counseling_V0') ;


/*MEDICINA LAVORO*//* DITTA APPARTENENZA*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.damedicina1_V0' 
from persone_giuridiche pg
join medicina_lavoro m on m.ditta_attuale_id=pg.internal_id
join procpratiche p on p.medicina_lavoro_id=m.internal_id
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where p.is_active=1 and p.status_code != 'status.generic.nullified_V0'
and s.area = 'phidic.spisal.pratiche.type.workmedicine_V0' and m.is_active=1;


/*MEDICINA LAVORO*//* DITTA ATTUALE*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.damedicina2_V0' 
from persone_giuridiche pg
join protocollo prot on prot.ditta_attuale_utente_id=pg.internal_id
join procpratiche p on prot.IDPRATICAASS=p.internal_id
join service_delivery_location s on p.servicedeliverylocation = s.internal_id
where p.is_active=1 and p.status_code != 'status.generic.nullified_V0' and s.area = 'phidic.spisal.pratiche.type.workmedicine_V0' and prot.is_active=1;


/*SOGGETTI DI PROVVEDIMENTI*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni4_V0' 
from provvedimenti prov
join attivita a on (a.internal_id=prov.attivita_id and a.is_active=1)
join procpratiche prat on (prat.internal_id=prov.procpratiche_id )
join soggetto sogg on (sogg.internal_id=prov.soggetto_id and sogg.code='phidic.spisal.segnalazioni.targetsource.ditta_V0')
join persone_giuridiche pg on pg.internal_id=sogg.ditta_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where sogg.is_active=1 and prov.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0' and sogg.is_active=1
and s.area in ('phidic.spisal.pratiche.type.workaccident_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 'phidic.spisal.pratiche.type.supervision_V0');


/*ATTIVITA ELEMENTARE-SOGGETTO*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni3_V0' 
from persone_giuridiche pg
join soggetto s on (s.ditta_id=pg.internal_id and s.code='phidic.spisal.segnalazioni.targetsource.ditta_V0')
join attivita a on (a.internal_id=s.attivita_id )
join procpratiche p on (a.procpratiche_id=p.internal_id)
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where p.status_code != 'status.generic.nullified_V0' and p.is_active=1 and s.is_active=1 and a.is_active=1 and 
sdl.area in('phidic.spisal.pratiche.type.workmedicine_V0', 'phidic.spisal.pratiche.type.supervision_V0', 'phidic.spisal.pratiche.type.workaccident_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 
'phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 
'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0') ;


/*ATTIVITA ELEMENTARE-LUOGO*/
insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', pg.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni3_V0' 
from persone_giuridiche pg
join attivita a on (a.luogo='phidic.spisal.segnalazioni.targetsource.ditta_V0' and a.luogo_ditta_id=pg.internal_id )
join procpratiche p on (a.procpratiche_id=p.internal_id)
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where p.status_code != 'status.generic.nullified_V0' and p.is_active=1 and a.is_active=1 and 
sdl.area in('phidic.spisal.pratiche.type.workmedicine_V0', 'phidic.spisal.pratiche.type.supervision_V0', 'phidic.spisal.pratiche.type.workaccident_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 
'phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 
'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0') ;




delete from cantieri_associazioni;


/*--INFORTUNI SUL LAVORO--*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type)
select distinct 1, 'phi-esb', cant.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.cainfortuni1_V0' 
from cantieri cant
join infortuni i on (i.cantiere_id=cant.internal_id and i.place='phidic.spisal.segnalazioni.place.yard_V0')
join procpratiche p on i.idprocpratica=p.internal_id
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where p.is_active=1 and p.status_code != 'status.generic.nullified_V0' and i.is_active=1 
and sdl.area = 'phidic.spisal.pratiche.type.workaccident_V0';


/*CANTIERI CONTROLLATE VIGILANZA CANTIERI*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', cant.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.cavigicantieri1_V0'
from cantieri cant
join vigilanza vig on (vig.cantiere_id=cant.internal_id)
join procpratiche p on (vig.internal_id=p.vigilanza_id)
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area = 'phidic.spisal.pratiche.type.supervision_V0' and p.is_active=1 and p.status_code != 'status.generic.nullified_V0'
and vig.type='phidic.spisal.segnalazioni.supervision.types.yard_V0' and vig.is_active=1;


/*CANTIERE SITO DELLA BONIFICA*//*VIGILANZA AMIANTO*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', cant.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.cavigiamianto1_v0'
from cantieri cant
join vigilanza vig on (vig.cantiere_id=cant.internal_id and vig.type is not null)
join procpratiche p on (vig.internal_id=p.vigilanza_id and p.is_active=1 and p.status_code != 'status.generic.nullified_V0')
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where sdl.area = 'phidic.spisal.pratiche.type.supervision_V0'
and vig.type='phidic.spisal.segnalazioni.supervision.types.asbestos_V0' and vig.is_active=1;


/*ESPRESSIONE PARERI TECNICI*//*ATTIVITÀ GENERALE*//*PROMOZIONE SALUTE VITA*//*INFORMAZIONE*//*FORMAZIONE*//*SPORTELLO*//*RIFERITO A DELLA PRATICA*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', cant.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.caespressione1_V0' 
from procpratiche prat
join pratiche_riferimenti pr on (pr.internal_id = prat.pratiche_riferimenti_id and pr.riferimento='phidic.spisal.segnalazioni.targetsource.cantiere_V0')
join cantieri cant on cant.internal_id=pr.riferimento_cantiere_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0') ;


/*FORMAZIONE/INFORMAZIONE /CONSOULTING*//* UBICAZIONE DELLA PRATICA*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', cant.internal_id, prat.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.caattivita2_V0' 
from procpratiche prat
join pratiche_riferimenti pr on pr.internal_id = prat.pratiche_riferimenti_id
join cantieri cant on cant.internal_id=pr.ubicazione_cantiere_id
join service_delivery_location s on prat.serviceDeliveryLocation = s.internal_id
where pr.is_active=1 and prat.is_active=1 and prat.status_code != 'status.generic.nullified_V0'
and s.area in ('phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 'phidic.spisal.pratiche.type.generic_V0') ;


/*ATTIVITA ELEMENTARE*/
insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) 
select distinct 1, 'phi-esb', cant.internal_id, p.internal_id, 'phidic.spisal.tipoassociazioni.cantieriassociation.camalattia1_V0' 
from cantieri cant
join attivita a on (a.luogo_cantiere_id=cant.internal_id and a.luogo='phidic.spisal.segnalazioni.targetsource.cantiere_V0')
join procpratiche p on (a.procpratiche_id=p.internal_id)
join service_delivery_location sdl on p.servicedeliverylocation = sdl.internal_id
where cant.is_active=1 and p.status_code != 'status.generic.nullified_V0' and p.is_active=1 and a.is_active=1 and 
sdl.area in('phidic.spisal.pratiche.type.workmedicine_V0', 'phidic.spisal.pratiche.type.supervision_V0', 'phidic.spisal.pratiche.type.workddisease_V0', 
'phidic.spisal.pratiche.type.technicaladvice_V0', 'phidic.spisal.pratiche.type.generic_V0', 'phidic.spisal.pratiche.type.training_V0', 'phidic.spisal.pratiche.type.information_V0', 
'phidic.spisal.pratiche.type.lifestyle_V0', 'phidic.spisal.pratiche.type.counseling_V0');
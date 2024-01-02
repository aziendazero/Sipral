/* comunicazioni con data di protocollo e configurazione regionale */
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data_protocollo,interval scad.score day)
where prot.data_protocollo is not null and data_scadenza is null and scad.score is not null;

/* comunicazioni con data di arrivo in asl e configurazione regionale*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data_asl,interval scad.score day)
where prot.data_protocollo is null and prot.data_asl is not null and data_scadenza is null and scad.score is not null;

/* comunicazioni con data di inserimento e configurazione regionale*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data,interval scad.score day)
where prot.data_protocollo is null and prot.data_asl is null and prot.data is not null and data_scadenza is null and scad.score is not null;

/* comunicazioni senza date e configurazione regionale*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(sysdate(),interval scad.score day)
where prot.data_protocollo is null and prot.data_asl is null and prot.data is null and data_scadenza is null and scad.score is not null;

/* comunicazioni con data di protocollo e senza configurazione regionale ma con configurazione a livello di tipo*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data_protocollo,interval cv.score day)
where prot.data_protocollo is not null and data_scadenza is null and scad.internal_id is null and cv.score is not null;

/* comunicazioni con data di arrivo in asl e senza configurazione regionale ma con configurazione a livello di tipo*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data_asl,interval cv.score day)
where prot.data_protocollo is null and prot.data_asl is not null and data_scadenza is null and scad.internal_id is null and cv.score is not null;

/* comunicazioni con data di inserimento e senza configurazione regionale ma con configurazione a livello di tipo*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.data,interval cv.score day)
where prot.data_protocollo is null and prot.data_asl is null and prot.data is not null and data_scadenza is null and scad.internal_id is null and cv.score is not null;

/* comunicazioni senza date e senza configurazione regionale ma con configurazione a livello di tipo*/
update protocollo prot
join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=date_add(prot.creation_date,interval cv.score day)
where prot.data_protocollo is null and prot.data_asl is null and prot.data is null and data_scadenza is null and scad.internal_id is null and cv.score is not null;

/* comunicazioni con data di protocollo e senza configurazione regionale e senza configurazione a livello di tipo*/
update protocollo prot
left join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=prot.data_protocollo
where prot.data_protocollo is not null and data_scadenza is null and scad.internal_id is null and cv.score is null;

/* comunicazioni con data di arrivo in asl e senza configurazione regionale e senza configurazione a livello di tipo*/
update protocollo prot
left join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=prot.data_asl
where prot.data_protocollo is null and prot.data_asl is not null and data_scadenza is null and scad.internal_id is null and cv.score is null;

/* comunicazioni con data di inserimento e senza configurazione regionale e senza configurazione a livello di tipo*/
update protocollo prot
left join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=prot.data
where prot.data_protocollo is null and prot.data_asl is null and prot.data is not null and data_scadenza is null and scad.internal_id is null and cv.score is null;

/* comunicazioni senza date e senza configurazione regionale e senza configurazione a livello di tipo*/
update protocollo prot
left join code_value cv on cv.id = prot.code
join service_delivery_location sdl on sdl.internal_id = prot.serviceDeliveryLocation
join service_delivery_location parent on parent.internal_id = sdl.Parent_SDL
left join scadenza_tipo_com scad on (scad.ulss_id = parent.internal_id and scad.code = cv.id)
set prot.data_scadenza=prot.creation_date
where prot.data_protocollo is null and prot.data_asl is null and prot.data is null and data_scadenza is null and scad.internal_id is null and cv.score is null;
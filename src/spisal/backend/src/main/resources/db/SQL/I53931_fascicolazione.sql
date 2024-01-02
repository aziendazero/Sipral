
/* aggiorna fascicoli esistenti impostandoli a di tipo distrettuale */
update tag_fascicolo set tagType = 'phidic.spisal.pratiche.tagtype.distr_V0' where tagType is null;

/* imposta relazione molti a molti, a partire da informazioni precedenti, per distretti */
insert into TagFascicolo_ServiceDeliveryLocation (TagFascicolo_id, ServiceDeliveryLocation_id) 
select internal_id, ServiceDeliveryLocation from tag_fascicolo where serviceDeliveryLocation is not null ;

/* imposta relazione molti a molti, a partire da informazioni precedenti, per linee */
insert into TagFascicolo_SDL_linee (TagFascicolo_id, ServiceDeliveryLocation_id) 
select internal_id, uos from tag_fascicolo where uos is not null;
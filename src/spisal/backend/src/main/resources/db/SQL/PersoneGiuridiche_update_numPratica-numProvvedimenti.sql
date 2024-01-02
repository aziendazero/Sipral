// setta su PERSONE GIURIDICHE il numero di PRATICHE
update persone_giuridiche pg 
inner join pratiche_riferimenti pr on pg.internal_id = pr.riferimento_ditta_id 
inner join procpratiche proc on (pr.internal_id=proc.pratiche_riferimenti_id and proc.status_code != 'status.generic.nullified_V0')
set pg.num_pratiche = (select count(*) from pratiche_riferimenti pr where pr.riferimento_ditta_id = pg.internal_id
inner join procpratiche proc on (pr.internal_id=proc.pratiche_riferimenti_id and proc.status_code != 'status.generic.nullified_V0'))

//setta su PERSONE GIURIDICHE il numero di PROVVEDIMENTI
update persone_giuridiche perz set perz.num_provvedimenti = 0;
update persone_giuridiche perz set perz.num_provvedimenti = 
(select count(distinct proc.numero) from procpratiche proc
inner join attivita a on a.procpratiche_id=proc.internal_id
inner join provvedimenti prov on prov.attivita_id=a.internal_id
inner join soggetto s on s.internal_id=prov.soggetto_id
where s.ditta_id = perz.internal_id
and a.is_active=true 
and prov.is_active=true and proc.status_code != 'status.generic.nullified_V0')
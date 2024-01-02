--questo script incrementa la lunghezza della colonna "testo" nella tabella "procpratiche_event"
alter table procpratiche_event modify testo varchar(2000);
alter table z_procpratiche_event modify testo varchar(2000);
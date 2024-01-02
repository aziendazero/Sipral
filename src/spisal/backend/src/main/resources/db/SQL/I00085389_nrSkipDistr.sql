alter table procpratiche add nr_pratica int;
update procpratiche set nr_pratica = substring_index(numero,'_',-1);

alter table z_procpratiche add nr_pratica int;
update z_procpratiche set nr_pratica = substring_index(numero,'_',-1);

create index IX_Procpratiche_nr_pratica USING BTREE ON procpratiche (nr_pratica);
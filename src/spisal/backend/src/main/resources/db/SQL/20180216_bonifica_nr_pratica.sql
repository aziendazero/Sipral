--questo script effettua la bonifica dei nr pratica, provvedimenti del 2018, modificando il codice ulss e aggiungendo per le pratiche il distretto
alter table procpratiche add column old_numero varchar(255);
alter table z_procpratiche add column old_numero varchar(255);
update procpratiche set old_numero = numero where numero like '%_2018_%';
update procpratiche set numero = replace(numero,'050112_','050503_ME_') where numero like '%_2018_%';
update provvedimenti set numero = replace(numero,'050112_','050503_') where numero like '%_2018_%';
/*
 * Aggiornamento contatore pratiche di mdl
 * ESEGUITO IN PRODUZIONE IL 23/11/2018
 */
update sedi_addebito sa
join sedi s on (s.persona_giuridica_id=sa.persona_giuridica_id and s.pec=sa.telecom_mc and sa.is_active=1)
set sa.telecom_tmp = sa.telecom_mail, 
sa.telecom_mail = sa.telecom_mc, 
sa.telecom_mc = sa.telecom_tmp, 
sa.telecom_tmp = null;
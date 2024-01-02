update procpratiche prat set prat.protocollo_master_number = null;
update procpratiche prat left join protocollo prot on prat.internal_id = prot.IDPRATICAASS
set prat.protocollo_master_number = (select p.NPROTOCOLLO from protocollo p where p.is_master and p.NPROTOCOLLO is not null and p.IDPRATICAASS=prot.IDPRATICAASS)
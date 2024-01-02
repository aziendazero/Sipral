UPDATE persone_giuridiche pg 
set data_canc_ri = (SELECT MAX(sedi.data_cancellazione) from sedi where sedi.persona_giuridica_id = pg.internal_id and sedi.is_active = TRUE and data_cancellazione is not null)
where pg.is_active = TRUE;

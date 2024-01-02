CREATE or REPLACE
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `V_SegnalazioniPersone` AS
    select 
        `p`.`internal_id` AS `segnalazione`,
        `p`.`richiedente_utente_id` AS `persona`
    from
        `protocollo` `p`
    where
        ((`p`.`richiedente_utente_id` is not null)
            and isnull(`p`.`malattia_professionale_id`)
            and isnull(`p`.`infortunio_id`)) 
    union select 
        `p2`.`internal_id` AS `segnalazione`,
        `p2`.`riferimento_utente_id` AS `persona`
    from
        `protocollo` `p2`
    where
        ((`p2`.`riferimento_utente_id` is not null)
            and ((`p2`.`infortunio_id` is not null)
            or (`p2`.`malattia_professionale_id` is not null)))
    order by `segnalazione`
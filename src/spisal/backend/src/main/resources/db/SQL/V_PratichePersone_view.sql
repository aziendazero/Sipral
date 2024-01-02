CREATE or REPLACE
    ALGORITHM = UNDEFINED 
    DEFINER = `spisal`@`%` 
    SQL SECURITY DEFINER
VIEW `V_PratichePersone` AS
    select 
        `p`.`internal_id` AS `pratica`,
        `r`.`richiedente_utente_id` AS `persona`
    from
        (`procpratiche` `p`
        left join `pratiche_riferimenti` `r` ON ((`p`.`pratiche_riferimenti_id` = `r`.`internal_id`)))
    where
        (`r`.`richiedente_utente_id` is not null) 
    union select 
        `i`.`IDPROCPRATICA` AS `pratica`,
        `i`.`person_id` AS `persona`
    from
        `infortuni` `i`
    where
        (`i`.`person_id` is not null) 
    union select 
        `pp`.`internal_id` AS `pratica`,
        `wd`.`riferimento_utente_id` AS `persona`
    from
        (`malattie_professionali` `wd`
        join `procpratiche` `pp` ON ((`pp`.`malattia_professionale_id` = `wd`.`internal_id`)))
    where
        (`wd`.`riferimento_utente_id` is not null)
    order by `pratica`
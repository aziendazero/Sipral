-- preparazione colonna d'appoggio code_value_law
ALTER TABLE code_value_law add column sanzione_bk varchar(255);
ALTER TABLE z_code_value_law add column sanzione_bk varchar(255);

UPDATE code_value_law set sanzione_bk = sanzione WHERE 1;

-- bonifica code_value_law vera e propria
UPDATE code_value_law set sanzione = (concat('Ammenda da ', REPLACE(importo_min, '.', ','), ' a ', REPLACE(importo_max, '.', ','))) 
WHERE importo_min is not null and sanzione_bk LIKE '%ammenda%' and oid LIKE 'Legge81.2019%';

UPDATE code_value_law set sanzione = (concat('Sanzione amministrativa pecuniaria da ', REPLACE(importo_min, '.', ','), ' a ', REPLACE(importo_max, '.', ','))) 
WHERE importo_min is not null and sanzione_bk LIKE '%sanzione%' and oid LIKE 'Legge81.2019%';


-- bonifica righe vuote situazione_professionale 

UPDATE person p set p.professional_situation_id=NULL WHERE EXISTS(SELECT 1 from situazione_professionale sp
where sp.internal_id = p.professional_situation_id and sp.note is null and sp.`type` is null
and sp.valid_from is null and sp.valid_to is null);

DELETE from situazione_professionale where note is null and `type` is null
and valid_from is null and valid_to is null;

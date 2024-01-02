/*
 * Update Price + 1,9%, see: intervento: I0066078 ADEGUAMNETO TARIFFE SANZIANATORIE ART 81
 * ESEGUITO IN PRODUZIONE IL 01/07/2018
 */

INSERT INTO `code_value_law`
(`id`,
`change_reason`,
`code`,
`creator`,
`default_child`,
`description`,
`display_name`,
`keywords`,
`lang_de`,
`lang_en`,
`lang_it`,
`oid`,
`revised_date`,
`sequence_number`,
`statusDB`,
`typeDB`,
`valid_from`,
`valid_to`,
`version`,
`code_system`,
`arresto`,
`classificazione`,
`codice_contravventore`,
`comma`,
`corpo`,
`descrizione_contravventore`,
`importo_max`,
`importo_min`,
`lettera`,
`nota_contravventore`,
`nota_prescrizione`,
`nota_violazione`,
`sanzionato_da`,
`sanzione`,
`code_value_parent`,
`numero`)
SELECT /*cv.id,*/ CONCAT(SUBSTRING_INDEX(cv.id,'_V',1),'_V', cv.version + 1),
/*cv.change_reason,*/ 'price + 1,9%',
cv.code,
cv.creator,
cv.default_child,
cv.description,
cv.display_name,
cv.keywords,
cv.lang_de,
cv.lang_en,
cv.lang_it,
cv.oid,
cv.revised_date,
cv.sequence_number,
cv.statusDB,
cv.typeDB,
/*cv.valid_from,*/ STR_TO_DATE('01/07/2018', '%d/%m/%Y'),
/*cv.valid_to,*/ null,
/*cv.version,*/ cv.version+1,
cv.code_system,
cv.arresto,
cv.classificazione,
cv.codice_contravventore,
cv.comma,
cv.corpo,
cv.descrizione_contravventore,
/*cv.importo_max,*/ ROUND(cast(replace(importo_max, ',', '.') as decimal(9,2)) * 1.019, 2),
/*cv.importo_min,*/ ROUND(cast(replace(importo_min, ',', '.') as decimal(9,2)) * 1.019, 2),
cv.lettera,
cv.nota_contravventore,
cv.nota_prescrizione,
cv.nota_violazione,
cv.sanzionato_da,
cv.sanzione,
cv.code_value_parent,
cv.numero
FROM code_value_law cv
WHERE cv.code_value_parent like 'Legge81%'
AND cv.sanzione is not null and cv.sanzione <> ''
AND cv.importo_min is not null and cv.importo_min <> ''
AND cv.importo_max is not null and cv.importo_min <> '';

UPDATE code_value_law
SET valid_to = STR_TO_DATE('30/06/2018 23:59:59', '%d/%m/%Y %T')
WHERE code_value_parent like 'Legge81%'
AND sanzione is not null and sanzione <> ''
AND importo_min is not null and importo_min <> ''
AND importo_max is not null and importo_min <> ''
AND version = 0;
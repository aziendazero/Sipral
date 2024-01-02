select numero, tmp from procpratiche;

select numero, tmp from procpratiche where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=1;

UPDATE procpratiche set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 3),
  '_0000',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=1;


select numero, tmp from procpratiche where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=2;

UPDATE procpratiche set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 3),
  '_000',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=2;


select numero, tmp from procpratiche where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=3;

UPDATE procpratiche set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 3),
  '_00',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=3;

select numero, tmp from procpratiche where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=4;

UPDATE procpratiche set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 3),
  '_0',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=5;


select numero, tmp from procpratiche where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=5;

UPDATE procpratiche set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 3),
  '_',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=5;

select numero, tmp from procpratiche;
update procpratiche set numero=tmp;



-----------------------------------------------------------------

Sistemazione numero provvedimento

SELECT numero, tmp FROM provvedimenti p;



UPDATE provvedimenti set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 2),
  '_0000',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=1;


  UPDATE provvedimenti set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 2),
  '_000',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=2;

  UPDATE provvedimenti set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 2),
  '_00',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=3;

  UPDATE provvedimenti set tmp =
  CONCAT(SUBSTRING_INDEX(numero, '_', 2),
  '_0',
  SUBSTRING_INDEX(numero, '_', -1)
  ) where LENGTH(SUBSTRING_INDEX(numero, '_', -1))=4;


SELECT numero, tmp FROM provvedimenti p;
update provvedimenti set numero=tmp where tmp is not null;






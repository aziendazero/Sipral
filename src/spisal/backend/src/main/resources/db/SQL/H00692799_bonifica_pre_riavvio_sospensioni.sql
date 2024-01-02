alter table provvedimenti add column old_numero varchar(255);
alter table z_provvedimenti add column old_numero varchar(255);
UPDATE provvedimenti set old_numero = numero where `type` = 'phidic.spisal.pratiche.pev.pevtype.ex14_V0';

--bonifica effettuata a mano visto il ridotto numero di provvedimenti interessati
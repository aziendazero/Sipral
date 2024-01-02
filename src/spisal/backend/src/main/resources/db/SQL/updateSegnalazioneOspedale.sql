/* 

istruzioni per spostare "phidic.spisal.segnalazioni.complextype.2.2" in "phidic.spisal.segnalazioni.complextype.14.13"


1) nel dictionary manager importare la foglia "phidic.spisal.segnalazioni.complextype.14.13" da l file csv "complexTypeMod.csv"
2) eseguire lo script sql di update che si trova in "updateSegnalazioneOspedale.sql"
3) nel dictionary manager creare la relazione tra "phidic.spisal.pratiche.type.workddisease" e "phidic.spisal.segnalazioni.complextype.14.13"
4) cancellare la foglia "phidic.spisal.segnalazioni.complextype.2.2"

*/

SET foreign_key_checks = 0;
update malattie_professionali set code='phidic.spisal.segnalazioni.complextype.14.13_V0' where code='phidic.spisal.segnalazioni.complextype.2.2';
update protocollo set code='phidic.spisal.segnalazioni.complextype.14.13_V0' where code='phidic.spisal.segnalazioni.complextype.2.2';
SET foreign_key_checks = 1;
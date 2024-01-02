/* Script di adeguamento delle precedenti sedi SPISAL alla mappatura indirizzi mail
*  in stile ARPAV dove la mail è bindata su telecom.mail e la PEC su telecom.mc
*  adeguamento necessario per evitare di rigirare tutti i binding in ARPAV
*/
update sedi se 
set se.tmp=se.pec, se.sede_addebito=false
where se.sede_addebito is null or se.sede_addebito=false;

update sedi se 
set se.pec=se.mc, se.mc=se.tmp, se.sede_addebito=false
where se.sede_addebito is null or se.sede_addebito=false;
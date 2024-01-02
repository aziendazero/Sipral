-- Popopolamento tabella
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.1','01');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.2','01');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.13','01');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.14','01');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'7.10','01');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.3','02');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'6','03');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.15','04');
INSERT into mdlsub_prot (is_active, codice_prot, codice_subtype) values (1,'14.4','05');

-- Update visite forestali
UPDATE code_equivalent_phi_phi set cv_phi2 = 'phidic.spisal.pratiche.type.workmedicine_V0' where cv_phi1 = 'phidic.spisal.segnalazioni.complextype.14.4';
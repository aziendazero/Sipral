update patient_encounter set fall_risk='0' where fall_risk is null;
update z_patient_encounter set fall_risk='0' where fall_risk is null;
update sdl_conf_ci set rehab_proc='0' where rehab_proc is null;
update z_sdl_conf_ci set rehab_proc='0' where rehab_proc is null;
update procedure_definition set exclusive='0' where exclusive is null;
update z_procedure_definition set exclusive='0' where exclusive is null;

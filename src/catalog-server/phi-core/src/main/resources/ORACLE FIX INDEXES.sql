--CS:
create index IX_patient_name_fam_lower on patient (Lower(name_fam));
create index IX_patient_name_giv_lower on patient (Lower(name_giv));

EXEC DBMS_STATS.gather_table_stats('phi2_stressTest', 'patient');

DROP INDEX IX_patient_serviceDeliveryLoca;
create index IX_patient_serviceDeliveryLoca on patient (serviceDeliveryLocation,'1');

EXEC DBMS_STATS.gather_table_stats('phi2_stressTest', 'patient');

--AMB:
DROP INDEX IX_APPOINTMENT_GROUPER;
CREATE INDEX IX_APPOINTMENT_GROUPER ON APPOINTMENT_GROUPER(availability_time,SERVICEDELIVERYLOCATION,status_code,'1');

EXEC DBMS_STATS.gather_table_stats('phi2_stressTest', 'APPOINTMENT_GROUPER');

DROP INDEX IX_APPOINTMENT_SDLOC;
create index IX_APPOINTMENT_SDLOC on appointment (serviceDeliveryLocation,'1');

EXEC DBMS_STATS.gather_table_stats('phi2_stressTest', 'appointment');
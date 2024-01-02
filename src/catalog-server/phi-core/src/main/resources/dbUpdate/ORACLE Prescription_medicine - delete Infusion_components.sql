UPDATE substanceadmin
SET planned_quantity_unit =
  (SELECT cv.code
  FROM code_value cv
  WHERE cv.oid=substanceadmin.planned_quantity_unit
  );
  
UPDATE infusion_components
SET quantity_unit =
  (SELECT cv.code
  FROM code_value cv
  WHERE cv.oid=infusion_components.quantity_unit
  );
  
--future update
/*UPDATE prescription
SET maxquant24h_unit =
  (SELECT cv.code
  FROM code_value cv
  WHERE cv.oid=prescription.maxquant24h_unit
  );*/


----------ATTENZIONE!!!----------
--
-- E' necessario cancellare a mano da prescription_medicine e da z_prescriprion_medicine il vincolo che ha
-- search_condition = '"MEDICINE_ID" IS NOT NULL'
-- Inoltre è necessario cancellare a mano da prescription_medicine e da z_prescriprion_medicine la primary key esistente
--
----------FINE ATTENZIONE!!!----------


--update prescription_medicine:
create sequence PrescriptionMedicine_sequence;

alter table prescription_medicine add ( internal_id number(19,0)); 
update      prescription_medicine set internal_id=PrescriptionMedicine_sequence.nextval;
alter table prescription_medicine modify (internal_id number(19,0) NOT NULL); 
alter table prescription_medicine add primary key (internal_id);

alter table prescription_medicine add ( created_by varchar2(255 char)); 
alter table prescription_medicine add ( creation_date timestamp); 

alter table prescription_medicine add ( is_active number(1,0));
update      prescription_medicine set is_active=1;
alter table prescription_medicine modify (is_active number(1,0) NOT NULL); 

alter table prescription_medicine add ( version number(19,0));
update      prescription_medicine set version=0;
alter table prescription_medicine add ( dosage varchar2(4000 char)); 
alter table prescription_medicine add ( template_dosage_key varchar2(255 char)); 
alter table prescription_medicine add ( serviceDeliveryLocation number(19,0));


create index IX_Prescription_Medicine on prescription_medicine (prescription_id);
create index IX_PrescriptionMedicine_sdloc on prescription_medicine (serviceDeliveryLocation);
create index IX_Medicine_Prescription on prescription_medicine (medicine_id);


alter table z_prescription_medicine add ( internal_id number(19,0)); 
update      z_prescription_medicine set internal_id =(select internal_id from prescription_medicine where medicine_id = z_prescription_medicine.medicine_id AND prescription_id = z_prescription_medicine.prescription_id);
alter table z_prescription_medicine modify (internal_id number(19,0) NOT NULL); 
alter table z_prescription_medicine add primary key (internal_id, REV);

alter table z_prescription_medicine add ( created_by varchar2(255 char)); 
alter table z_prescription_medicine add ( creation_date timestamp); 
alter table z_prescription_medicine add ( is_active number(1,0));
alter table z_prescription_medicine add ( dosage varchar2(4000 char)); 
alter table z_prescription_medicine add ( template_dosage_key varchar2(255 char)); 
--alter table z_prescription_medicine rename column dosage_key to template_dosage_key;
alter table z_prescription_medicine add ( serviceDeliveryLocation number(19,0));


alter table templatedosage add ( key varchar2(255 char));


--Move infusions to prescription_medicine:
Insert into prescription_medicine (internal_id, is_active, created_by, creation_date, 'version', dosage, template_dosage_key, medicine_id, prescription_id) 
(SELECT PRESCRIPTIONMEDICINE_SEQUENCE.NEXTVAL, infu.* FROM 
(SELECT
ic.is_active,
ic.created_by, 
ic.creation_date, 
1,
LISTAGG (d.day_interval || '@' || to_char(d.daytime, 'HH24:MI') || '=' || ic.quantity_value || '[' || ic.quantity_unit || ']', '|') WITHIN GROUP (ORDER BY d.daytime),
s.internal_id || ';P=' || LISTAGG (d.day_interval || '@' || to_char(d.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY d.daytime),
ic.medicine_id,
ic.prescription_id
from INFUSION_COMPONENTS ic 
join  prescription p on ic.prescription_id = p.INTERNAL_ID
join  dosage d on d.prescription = p.INTERNAL_ID
join therapy t on t.INTERNAL_ID = p.therapy
join patient_encounter pe on pe.INTERNAL_ID = t.patient_encounter
join service_delivery_location s on s.INTERNAL_ID = pe.assignedsdl
GROUP BY
ic.is_active,
ic.created_by, 
ic.creation_date,
ic.medicine_id,
ic.prescription_id,
s.internal_id) infu );

--update dosage of existing prescription_medicine, taking dosage from prescription->dosageList
UPDATE prescription_medicine pmdc SET (dosage, template_dosage_key) = (
SELECT
CASE   
    WHEN p.needsbased = 1 THEN LISTAGG (d.quantity_value || '[' || (SELECT cv.code FROM code_value cv WHERE cv.oid=d.quantiy_unit) || ']', '|') WITHIN GROUP (ORDER BY d.daytime)
    ELSE LISTAGG (d.day_interval || '@' || to_char(d.daytime, 'HH24:MI') || '=' || d.quantity_value || '[' || (SELECT cv.code FROM code_value cv WHERE cv.oid=d.quantiy_unit) || ']', '|') WITHIN GROUP (ORDER BY d.daytime)
END,
CASE   
    WHEN p.needsbased = 1 THEN NULL
    ELSE s.internal_id || ';P=' || LISTAGG (d.day_interval || '@' || to_char(d.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY d.daytime)
END
from prescription_medicine pm 
join  prescription p on pm.prescription_id = p.INTERNAL_ID
join  dosage d on d.prescription = p.INTERNAL_ID
join therapy t on t.INTERNAL_ID = p.therapy
join patient_encounter pe on pe.INTERNAL_ID = t.patient_encounter
join service_delivery_location s on s.INTERNAL_ID = pe.assignedsdl
WHERE pm.prescription_id = pmdc.prescription_id
GROUP BY
p.internal_id, 
p.needsbased,
s.internal_id
) WHERE pmdc.dosage is null;
--ONLY 4 PHI_CI:
--Create templateDosage.key to join with prescription_medicine.template_dosage_key
UPDATE templatedosage tempDos SET (key) = (
select
tds.sdloc_id || ';' || SUBSTR(cv.code,0,1) || '=' || LISTAGG (d.day_interval || '@' || to_char(d.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY d.daytime) 
from templatedosage td 
join templateDosage_dosage tdd on td.internal_id = tdd.templatedosage_id 
join dosage d on d.internal_id = tdd.dosage_id
join templatedosage_sdloc tds on tds.templatedosage_id = td.internal_id
join code_value cv on cv.id = td.code
WHERE tempDos.internal_id = td.internal_id
GROUP BY td.internal_id,
tds.sdloc_id,
cv.code);


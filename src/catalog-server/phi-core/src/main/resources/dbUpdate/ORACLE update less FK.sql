/*Number of tables:
SELECT COUNT(*) FROM USER_TABLES; --912
SELECT COUNT(*) FROM USER_TABLES where TABLE_NAME like 'Z_%'; --445
*/
/*CHECK fk count:
    SELECT distinct(c_pk.table_name), count(*)
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
    and c.owner = 'PHI_CI_TEST' group by c_pk.table_name order by  count(*) desc;
*/ 
/*CI START SITUATION: 
CODE_VALUE	1443
REVINFO	443
SERVICE_DELIVERY_LOCATION	295
EMPLOYEE	222
PATIENT_ENCOUNTER	104
PATIENT	72
APPOINTMENT_GROUPER	37
CODE_VALUE_ICD9	30
CODE_VALUE_CITY	25
ASSESSMENT_SCALE	18
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10
*/
    


--Remove columns of act/role/entity:

alter table annotation drop (uncertainty_code, status_code, mood_code, language_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr/*, thrombolysisby, arrival, ward_arrival_time, timi_risk_score, thrombolysistime, arrival_time, angor_time*/ ) CASCADE CONSTRAINTS;
alter table patient_encounter drop (title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, discharge, uncertainty_code, mood_code, level_code, language_code, class_code ) CASCADE CONSTRAINTS;
alter table therapy drop (uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time ) CASCADE CONSTRAINTS;
alter table organization drop (status_code, determiner_code, code, class_code, existence_lowclosed, existence_time_low, existence_highclosed, existence_time_high, desc_string, desc_bytes ) CASCADE CONSTRAINTS;
alter table employee drop (status_code, scoperinternalid, playerinternalid, class_code, quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, salaryquantity_currency, jobtitlename_code, salaryquantity_value, jobtitlename_data ) CASCADE CONSTRAINTS;
alter table service_delivery_location drop (certificatetext_string, certificatetext_bytes, status_code, scoperinternalid, playerinternalid, quantity_numerator, quantity_denominator, negation_ind ) CASCADE CONSTRAINTS;

alter table z_annotation drop (uncertainty_code, status_code, mood_code, language_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr/*, thrombolysisby, arrival, ward_arrival_time, timi_risk_score, thrombolysistime, arrival_time, angor_time*/ ) CASCADE CONSTRAINTS;
alter table z_patient_encounter drop (discharge, uncertainty_code, mood_code, level_code, language_code, class_code, title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr ) CASCADE CONSTRAINTS;
alter table z_therapy drop (uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time ) CASCADE CONSTRAINTS;
alter table z_organization drop (status_code, determiner_code, code, class_code, existence_lowclosed, existence_time_low, existence_highclosed, existence_time_high, desc_string, desc_bytes ) CASCADE CONSTRAINTS;
alter table z_employee drop (status_code, scoperinternalid, playerinternalid, class_code, quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, salaryquantity_currency, jobtitlename_code, salaryquantity_value, jobtitlename_data ) CASCADE CONSTRAINTS;
alter table z_service_delivery_location drop (quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, status_code, scoperinternalid, playerinternalid ) CASCADE CONSTRAINTS;

/*PHI CI & AMB

alter table attachment drop (storage_code, completion_code, copy_time, version_number, root, extension, uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, patient_encounter_id ) CASCADE CONSTRAINTS;
alter table macro_text_suggestion drop (uncertainty_code, status_code, mood_code, level_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time ) CASCADE CONSTRAINTS;

alter table z_attachment drop (storage_code, completion_code, copy_time, version_number, root, extension, uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, patient_encounter_id ) CASCADE CONSTRAINTS;
alter table z_macro_text_suggestion drop (negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time, uncertainty_code, status_code, mood_code, level_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high ) CASCADE CONSTRAINTS;
*/


/*PHI CI only:

alter table self_perception_check drop (uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time ) CASCADE CONSTRAINTS;
alter table z_self_perception_check drop (uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time ) CASCADE CONSTRAINTS;
*/

    

--REMOVE HL7: WARNING IF YOU HAVE REIMPLEMENTED SOME HL7 CLASSES INTO BACKEND; DON'T DROP THEM...

drop table access_db;
drop table account;
drop table act;
drop table act_activitytime;
drop table act_confidentiality_code;
drop table act_effectivetime;
drop table act_id;
drop table act_priority_code;
drop table act_reason_code;
drop table actrelationship;
drop table measurealarminformation;
drop table alarmitem;
drop table citizen;
drop table code_equivalent_phi_status; --if exsists, now was removed
drop table container;
drop table context_structure;
drop table control_act;
drop table device;
drop table device_task;
drop table diagnostic_image;
drop table diet;
drop table discharge;
drop table document;
drop table document_text;
drop table entity;
drop table entity_handling_code;
drop table entity_id;
drop table entity_quantity;
drop table entity_risk_code;
drop table equipe;
drop table exposure;
drop table financial_contract;
drop table financial_transaction;
drop table genericrequest; --if exsists, now is abstract
drop table invoice_element_modifiercode;
drop table invoice_element;
drop table licensed_entity;
drop table living_subject;
drop table managed_participation_id;
drop table managed_participation;
drop table manufactured_material;
drop table material;
drop table medicalhistory; --if exsists, now is abstract
drop table person;
drop table member;
drop table non_person;
drop table obs_interpretation_code;
drop table obs_method_code;
drop table obs_target_site_code;
drop table observation;
drop table participation;
drop table person_disability_code;
drop table person_ethnic_group_code;
drop table person_race_code;
drop table place;
drop table public_health_case;
drop table qualified_entity;
drop table role;
drop table role_id;
drop table role_positionnumber;
drop table role_servicedeliverylocation;
drop table rolelink;
drop table scheduleitem;
drop table sdlconf; --if exsists, now is abstract
drop table subadmin_dosequantity;
drop table subadmin_maxdosequantity;
drop table supply;
drop table widget_rule;
drop table working_list;



/*Used for check of FK
SELECT a.table_name, a.column_name, a.constraint_name, c.owner, 
       -- referenced pk
       c.r_owner, c_pk.table_name r_table_name, c_pk.constraint_name r_pk
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
   AND c_pk.table_name = 'PERSON';*/

--History
drop table z_access_db;
drop table z_account;
drop table z_act;
drop table z_act_activitytime;
drop table z_act_activitytime_jt;
drop table z_act_confidentiality_code;
drop table z_act_effectivetime;
drop table z_act_effectivetime_jt;
drop table z_act_id;
drop table z_act_id_jt;
drop table z_act_priority_code;
drop table z_act_reason_code;
drop table z_actrelationship;
drop table z_alarmitem;
drop table z_citizen;
drop table z_code_equivalent_phi_status; --if exsists, now was removed
drop table z_contact_patient_jt;
drop table z_container;
drop table z_context_structure;
drop table z_control_act;
drop table z_device;
drop table z_device_task;
drop table z_diagnostic_image;
drop table z_diet;
drop table z_discharge;
drop table z_document;
drop table z_document_text;
drop table z_document_text_jt;
drop table z_entity;
drop table z_entity_handling_code;
drop table z_entity_id;
drop table z_entity_id_jt;
drop table z_entity_quantity;
drop table z_entity_quantity_jt;
drop table z_entity_risk_code;
drop table z_equipe;
drop table z_exposure;
drop table z_financial_contract;
drop table z_financial_transaction;
drop table z_invoice_element;
drop table z_invoice_element_modifiercode;
drop table z_licensed_entity;
drop table z_living_subject;
drop table z_managed_participation;
drop table z_managed_participation_id;
drop table z_managed_participation_id_jt;
drop table z_manufactured_material;
drop table z_material;
drop table z_measurealarminformation;
drop table z_member;
drop table z_non_person;
drop table z_obs_interpretation_code;
drop table z_obs_method_code;
drop table z_obs_target_site_code;
drop table z_observation;
drop table z_participation;
drop table z_person;
drop table z_person_disability_code;
drop table z_person_ethnic_group_code;
drop table z_person_race_code;
drop table z_place;
drop table z_public_health_case;
drop table z_qualified_entity;
drop table z_role;
drop table z_role_id;
drop table z_role_id_jt;
drop table z_role_positionnumber;
drop table z_role_servicedeliverylocation;
drop table z_rolelink;
drop table z_scheduleitem;
drop table z_subadmin_dosequantity;
drop table z_subadmin_maxdosequantity;
drop table z_supply;
drop table z_working_list;

/*CI SITUATION: 
SELECT COUNT(*) FROM USER_TABLES; --769
SELECT COUNT(*) FROM USER_TABLES where TABLE_NAME like 'Z_%'; --371

CODE_VALUE	1136
REVINFO	370
SERVICE_DELIVERY_LOCATION	253
EMPLOYEE	217
PATIENT_ENCOUNTER	96
PATIENT	71
APPOINTMENT_GROUPER	36
CODE_VALUE_ICD9	30
ASSESSMENT_SCALE	18
CODE_VALUE_CITY	16
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10
*/




/* Drop FK of role code to code_value:
select count(*)
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner
                        AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner
                           AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
   AND c_pk.table_name = 'CODE_VALUE'
   and a.column_name like '%ROLE%'
   order by a.table_name;*/
   
--CI INITIAL 154 fk to CODE_VALUE

--Create script for Drop FK of role code to code_value:
select 'alter table '||a.table_name||' drop constraint '||a.constraint_name||';'
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner
                        AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner
                           AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
   AND c_pk.table_name = 'CODE_VALUE'
   and a.column_name like '%ROLE%'
   order by a.table_name;
   
--Execute!!!
   
   
alter table EMPLOYEE_ROLE drop constraint FK_EMPLOYEEROLE_CODE;
   

   
/*CI SITUATION: 

CODE_VALUE	982
REVINFO	370
SERVICE_DELIVERY_LOCATION	253
EMPLOYEE	217
PATIENT_ENCOUNTER	96
PATIENT	71
APPOINTMENT_GROUPER	36
CODE_VALUE_ICD9	30
ASSESSMENT_SCALE	18
CODE_VALUE_CITY	16
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10
*/




--CREATE CODE_VALUE_ROLE:
create table code_value_role (id varchar2(255 char) not null, change_reason varchar2(255 char), code varchar2(65 char) not null, creator varchar2(65 char), default_child number(1,0) not null, description varchar2(765 char), display_name varchar2(255 char) not null, keywords varchar2(65 char), lang_de varchar2(255 char), lang_en varchar2(255 char), lang_it varchar2(255 char), oid varchar2(255 char) not null, revised_date timestamp, sequence_number number(10,0) not null, statusDB number(10,0) not null, typeDB varchar2(1 char) not null, valid_from timestamp, valid_to timestamp, version number(10,0) not null, code_system number(10,0) not null, abbreviation varchar2(255 char), code_value_parent varchar2(255 char), primary key (id), unique (oid, version));
alter table code_value_role add constraint FK_code_value_role_Parent foreign key (code_value_parent) references code_value_role;
alter table code_value_role add constraint FK_code_value_role_code_system foreign key (code_system) references code_system;
create index IX_code_value_role_code on code_value_role (code);
create index IX_code_value_role_code_system on code_value_role (code_system);
create index IX_code_value_role_Parent on code_value_role (code_value_parent);
create index IX_Code_Value_Role on code_value_role (id, version, typeDB, statusDB);

create table z_code_value_role (id varchar2(255 char) not null, REV number(10,0) not null, REVTYPE number(3,0), change_reason varchar2(255 char), code varchar2(65 char), creator varchar2(65 char), default_child number(1,0), description varchar2(765 char), display_name varchar2(255 char), keywords varchar2(65 char), lang_de varchar2(255 char), lang_en varchar2(255 char), lang_it varchar2(255 char), oid varchar2(255 char), revised_date timestamp, sequence_number number(10,0), statusDB number(10,0), typeDB varchar2(1 char), valid_from timestamp, valid_to timestamp, version number(10,0), code_system number(10,0), abbreviation varchar2(255 char), code_value_parent varchar2(255 char), primary key (id, REV));
alter table z_code_value_role add constraint FK_z_code_value_ro_REV foreign key (REV) references revinfo;





/*
select * from code_value where DISPLAY_NAME = 'EmployeeFunction';
select * from code_value cv  join code_value parent on cv.CODE_VALUE_PARENT = parent.id where cv.DISPLAY_NAME = 'EmployeeFunction' OR parent.DISPLAY_NAME = 'EmployeeFunction';
select * from code_value where oid  like '2.16.840.1.113883.3.20.11.1%';
select * from CODE_SYSTEM;
select * from z_CODE_SYSTEM;

select * from CODE_VALUE_ROLE where oid  like '2.16.840.1.113883.3.20.11.1%';
select * from z_CODE_VALUE_ROLE;-- where oid  like '2.16.840.1.113883.3.20.11.1%';



drop table code_value_role;
drop table z_code_value_role;
delete from CODE_SYSTEM where name = 'ROLES';
delete from z_CODE_SYSTEM where name = 'ROLES';
*/


--Copy EmployeeFunction from CODE_VALUE TO CODE_VALUE_ROLE:
DECLARE
    code_system_sequence_val PLS_INTEGER;
    revinfo_sequence_val PLS_INTEGER;
BEGIN
    code_system_sequence_val  := code_system_sequence.NEXTVAL;
    revinfo_sequence_val      := revinfo_sequence.NEXTVAL;


  --create envers revision
  INSERT INTO REVINFO (ID, TIMESTAMPDB, USERNAME)
  VALUES (revinfo_sequence_val, ROUND((sysdate - to_date('01-01-1970', 'DD-MM-YYYY')) * (86400) + (28800)) , 'PHI-DEV');


	--Create ROLES codeSystem
    Insert into CODE_SYSTEM (ID,AUTHORITY_DESCRIPTION,AUTHORITY_NAME,CODE_VALUE_CLASS,DISPLAY_NAME,NAME,CONTACT_INFORMATION,CREATION_DATE,DESCRIPTION,OID,ISO_CODE_LANG,REVISED_DATE,STATUSDB,VALID_FROM,VALID_TO,VERSION) 
    values (code_system_sequence_val,'Insiel Mercato','Insiel Mercato','CodeValueRole','Roles','ROLES','Insiel Mercato',to_timestamp('18-LUG-14 00:00:01,000000000','DD-MON-RR HH24:MI:SSXFF'),
			'Employee roles','2.16.840.1.113883.3.20.11','it',null,'1',to_timestamp('01-GEN-10 00:00:01,000000000','DD-MON-RR HH24:MI:SSXFF'),null,'0');
  --Create ROLES z_codeSystem History
    Insert into Z_CODE_SYSTEM (ID,REV,REVTYPE,AUTHORITY_DESCRIPTION,AUTHORITY_NAME,CODE_VALUE_CLASS,CONTACT_INFORMATION,CREATION_DATE,DESCRIPTION,DISPLAY_NAME,OID,ISO_CODE_LANG,NAME,REVISED_DATE,STATUSDB,VALID_FROM,VALID_TO,VERSION)
    values (code_system_sequence_val, revinfo_sequence_val,'0','Insiel Mercato','Insiel Mercato','CodeValueRole','Insiel Mercato',to_timestamp('18-LUG-14 00:00:01,000000000','DD-MON-RR HH24:MI:SSXFF'),'Employee roles','Roles','2.16.840.1.113883.3.20.11','it','ROLES',null,'1',to_timestamp('01-GEN-10 00:00:01,000000000','DD-MON-RR HH24:MI:SSXFF'),null,'0');



--Move EmployeeFunction EmployeeRole to CodeValueRole: (x bolzano: AMBBolzanoUserRoles)
    Insert into CODE_VALUE_ROLE   (ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION) 
    (
      SELECT ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,code_system_sequence_val,null,ABBREVIATION --null parent to detach parent
      from code_value where DISPLAY_NAME = 'EmployeeFunction'
    );
--Move EmployeeFunction childs  EmployeeRole to CodeValueRole: 
    Insert into CODE_VALUE_ROLE   (ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION) 
    (
      SELECT cv.ID,cv.CHANGE_REASON,cv.CODE,cv.CREATOR,cv.DEFAULT_CHILD,cv.DESCRIPTION,cv.DISPLAY_NAME,cv.KEYWORDS,cv.LANG_DE,cv.LANG_EN,cv.LANG_IT,cv.OID,cv.REVISED_DATE,cv.SEQUENCE_NUMBER,cv.STATUSDB,cv.TYPEDB,cv.VALID_FROM,cv.VALID_TO,cv.VERSION,code_system_sequence_val,cv.CODE_VALUE_PARENT,cv.ABBREVIATION
      from code_value cv join code_value parent on cv.CODE_VALUE_PARENT = parent.id where parent.DISPLAY_NAME = 'EmployeeFunction'
    );
    
    
    
--Move EmployeeRole HISTORY to z_CodeValueRole: 
    Insert into Z_CODE_VALUE_ROLE (ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION)
    (
      SELECT ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,code_system_sequence_val,null,ABBREVIATION
      from z_code_value where DISPLAY_NAME = 'EmployeeFunction'
    );
--Move EmployeeRole childs HISTORY to z_CodeValueRole: 
    Insert into Z_CODE_VALUE_ROLE (ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION)
    (
      SELECT cv.ID,cv.REV,cv.REVTYPE,cv.CHANGE_REASON,cv.CODE,cv.CREATOR,cv.DEFAULT_CHILD,cv.DESCRIPTION,cv.DISPLAY_NAME,cv.KEYWORDS,cv.LANG_DE,cv.LANG_EN,cv.LANG_IT,cv.OID,cv.REVISED_DATE,cv.SEQUENCE_NUMBER,cv.STATUSDB,cv.TYPEDB,cv.VALID_FROM,cv.VALID_TO,cv.VERSION,code_system_sequence_val,cv.CODE_VALUE_PARENT,cv.ABBREVIATION
      from z_code_value cv where cv.id like '2.16.840.1.113883.3.20.11.1.%' --(cambiare x AMBBolzanoUserRoles)
    );
	
END;
/

/*NOW LAUNCH ORACLE SCHEMA UPDATE ANT TASK OF CS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/



--Delete EmployeeFunction from CODE_VALUE
delete from code_value where ID like '2.16.840.1.113883.3.20.11.1.%';
delete from code_value where DISPLAY_NAME = 'EmployeeFunction';
delete from code_value where DISPLAY_NAME = 'ModEmployee';

delete from z_code_value where ID like '2.16.840.1.113883.3.20.11.1.%';
delete from z_code_value where DISPLAY_NAME = 'EmployeeFunction';
delete from z_code_value where DISPLAY_NAME = 'ModEmployee';







/* Final test:
    select
        this_.id as y0_,
        this_.oid as y1_,
        this_.version as y2_,
        this_.code as y3_,
        this_.display_name as y4_,
        this_.typeDB as y5_,
        this_.sequence_number as y6_,
        this_.default_child as y7_,
        this_.statusDB as y8_,
        this_.description as y9_,
        this_.keywords as y10_,
        this_.valid_from as y11_,
        this_.valid_to as y12_,
        this_.revised_date as y13_,
        this_.creator as y14_,
        this_.change_reason as y15_,
        this_.lang_en as y16_,
        this_.lang_de as y17_,
        this_.lang_it as y18_ 
    from
        phi_ci_test.code_value_role this_ 
    where
        this_.code_system=283
        and this_.code_value_parent is null 
    order by
        y4_ asc;
        
        
            select
        count(*) as y0_ 
    from
        phi_ci_test.code_value_role this_ 
    where
        this_.code_value_parent='2.16.840.1.113883.3.20.11.1_V0';
        
        
           select
        count(*) as y0_ 
    from
        phi_ci_test.code_value_role this_ 
    where
        this_.code_value_parent='2.16.840.1.113883.3.20.11_V0';
        
        */
        
        -----------NEWZZZZ Remove ServiceDelivery Location
        /*
CODE_VALUE	981
REVINFO	371
SERVICE_DELIVERY_LOCATION	253
EMPLOYEE	217
CODE_VALUE_ROLE	156
PATIENT_ENCOUNTER	96
PATIENT	71
APPOINTMENT_GROUPER	36
CODE_VALUE_ICD9	30
ASSESSMENT_SCALE	18
CODE_VALUE_CITY	16
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10*/
        
select 'alter table '||a.table_name||' drop constraint '||a.constraint_name||';'
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner
                        AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner
                           AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
   AND c_pk.table_name = 'SERVICE_DELIVERY_LOCATION'
   and a.column_name like 'SERVICEDELIVERYLOCATION'
   order by a.table_name;
   
select 'drop index '||i.index_name||';'
  FROM USER_IND_COLUMNS i  
  WHERE i.column_name like 'SERVICEDELIVERYLOCATION';


/*

CODE_VALUE	981
REVINFO	371
EMPLOYEE	217
CODE_VALUE_ROLE	156
PATIENT_ENCOUNTER	96
PATIENT	71
SERVICE_DELIVERY_LOCATION	54
APPOINTMENT_GROUPER	36
CODE_VALUE_ICD9	30
ASSESSMENT_SCALE	18
CODE_VALUE_CITY	16
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10
CODE_SYSTEM	10
*/


/*NOW LAUNCH ORACLE SCHEMA UPDATE ANT TASK OF CS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/

/*
CODE_VALUE	982
REVINFO	371
EMPLOYEE	219
CODE_VALUE_ROLE	158
PATIENT_ENCOUNTER	97
PATIENT	72
SERVICE_DELIVERY_LOCATION	66
APPOINTMENT_GROUPER	37
CODE_VALUE_ICD9	30
ASSESSMENT_SCALE	18
CODE_VALUE_CITY	16
APPOINTMENT	16
CODE_VALUE_CODIFA	14
CODE_VALUE_NANDA	13
ANESTH_MED_HISTORY_VCO	12
LESION_MANAGMENT	11
MEDICINE	10
CODE_SYSTEM	10
*/





SELECT distinct(c_pk.table_name), count(*)
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
    and c.owner = 'PHI_CI_TEST' 
    group by c_pk.table_name 
    order by  count(*) desc;
    
 
    
SELECT distinct(a.table_name), count(*)
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
    and c.owner = 'PHI_CI_TEST' 
    and c_pk.table_name = 'CODE_VALUE'
    group by a.table_name 
    order by  count(*) desc;
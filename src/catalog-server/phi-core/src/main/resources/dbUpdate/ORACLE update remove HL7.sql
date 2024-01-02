create table employee_id (id number(19,0) not null, extension varchar2(255 char), root varchar2(255 char), version number(19,0), employee_id number(19,0), primary key (id));
create table exemption_id (id number(19,0) not null, extension varchar2(255 char), root varchar2(255 char), version number(19,0), exemption_id number(19,0), primary key (id));
create table patient_encounter_id (id number(19,0) not null, extension varchar2(255 char), root varchar2(255 char), version number(19,0), pat_enc_id number(19,0), primary key (id));
create table service_delivery_location_id (id number(19,0) not null, extension varchar2(255 char), root varchar2(255 char), version number(19,0), sdloc_id number(19,0), primary key (id));


create table z_employee_id (id number(19,0) not null, REV number(10,0) not null, REVTYPE number(3,0), extension varchar2(255 char), root varchar2(255 char), primary key (id, REV));
create table z_employee_id_jt (REV number(10,0) not null, employee_id number(19,0) not null, id number(19,0) not null, REVTYPE number(3,0), primary key (REV, employee_id, id));

create table z_exemption_id (id number(19,0) not null, REV number(10,0) not null, REVTYPE number(3,0), extension varchar2(255 char), root varchar2(255 char), primary key (id, REV));
--Already exist, mapping was wrong: create table z_exemption_id_jt (REV number(10,0) not null, exemption_id number(19,0) not null, id number(19,0) not null, REVTYPE number(3,0), primary key (REV, exemption_id, id));

create table z_pat_enc_id_jt (REV number(10,0) not null, pat_enc_id number(19,0) not null, id number(19,0) not null, REVTYPE number(3,0), primary key (REV, pat_enc_id, id));
create table z_patient_encounter_id (id number(19,0) not null, REV number(10,0) not null, REVTYPE number(3,0), extension varchar2(255 char), root varchar2(255 char), primary key (id, REV));

create table z_sdloc_id_jt (REV number(10,0) not null, sdloc_id number(19,0) not null, id number(19,0) not null, REVTYPE number(3,0), primary key (REV, sdloc_id, id));
create table z_service_delivery_location_id (id number(19,0) not null, REV number(10,0) not null, REVTYPE number(3,0), extension varchar2(255 char), root varchar2(255 char), primary key (id, REV));


alter table employee_id add constraint FK_Employee_id foreign key (employee_id) references employee;
create index IX_Employee_id on employee_id (employee_id);

alter table exemption_id add constraint FK_Exemption_id foreign key (exemption_id) references exemption;
create index IX_Exemption_id on exemption_id (exemption_id);

alter table patient_encounter_id add constraint FK_Pat_Enc_id foreign key (pat_enc_id) references patient_encounter;
create index IX_Pat_Enc_id on patient_encounter_id (pat_enc_id);

alter table service_delivery_location_id add constraint FK_SDL_id foreign key (sdloc_id) references service_delivery_location;
create index IX_SDL_id on service_delivery_location_id (sdloc_id);


alter table z_employee_id add constraint FKBBD95C0795FEBAF foreign key (REV) references revinfo;
alter table z_employee_id_jt add constraint FK335DC48295FEBAF foreign key (REV) references revinfo;

alter table z_exemption_id add constraint FKA10B64F695FEBAF foreign key (REV) references revinfo;
--Already exist, mapping was wrong: alter table z_exemption_id_jt add constraint FKA10B34F695FEBAF foreign key (REV) references revinfo;

alter table z_pat_enc_id_jt add constraint FK453FDBA895FEBAF foreign key (REV) references revinfo;
alter table z_patient_encounter_id add constraint FK708FF14695FEBAF foreign key (REV) references revinfo;

alter table z_sdloc_id_jt add constraint FK896715B995FEBAF foreign key (REV) references revinfo;
alter table z_service_delivery_location_id add constraint FK5C32830995FEBAF foreign key (REV) references revinfo;


alter table ROLE_CONFIDENTIALITY_CODE rename to sdl_confidentiality_code
alter table z_role_confidentiality_code rename to z_sdl_confidentiality_code;


create sequence Annotation_sequence;
create sequence ServiceDeliveryLoc_sequence;
create sequence PatientEncounter_sequence;
create sequence Therapy_sequence;
create sequence Organization_sequence;
create sequence Employee_sequence;

/*PHI CI
create sequence Attachment_sequence;
create sequence MacroTextSuggestion_sequence;
create sequence SelfPerceptionCheck_sequence;
*/

/*PHI AMB
create sequence Attachment_sequence;
create sequence MacroTextSuggestion_sequence;
*/

--Organization extends Entity -> extends BaseEntity AND id from SET[II] to String
alter table organization add id varchar2(255 char);
alter table z_organization add id varchar2(255 char);

UPDATE organization set (id) = (SELECT entity_id.extension from entity_id where entity_id.entity_id = organization.internal_id);
UPDATE z_organization set (id) = (SELECT z_entity_id.extension from z_entity_id inner join z_entity_id_jt on z_entity_id.id = z_entity_id_jt.id where z_entity_id_jt.entity_id = z_organization.internal_id AND z_entity_id.REV = z_organization.REV);

/*Movedata from role id to ...*/

insert into employee_id (select * from role_id where role_id in (select internal_id from employee));
insert into z_employee_id (select z_role_id.* from z_role_id inner join z_role_id_jt on z_role_id.id = z_role_id_jt.id where z_role_id_jt.role_id in (select internal_id from z_employee));
insert into z_employee_id_jt (select z_role_id_jt.* from z_role_id inner join z_role_id_jt on z_role_id.id = z_role_id_jt.id where z_role_id_jt.role_id in (select internal_id from z_employee));

insert into exemption_id (select entity_id.id, entity_id.extension, entity_id.root, entity_id.version, entity_id.exemption_id from entity_id where exemption_id in (select internal_id from exemption));
insert into z_exemption_id (select z_entity_id.* from z_entity_id inner join z_entity_id_jt on z_entity_id.id = z_entity_id_jt.id where z_entity_id_jt.entity_id in (select internal_id from z_exemption));
--Already exist, mapping was wrong: insert into z_exemption_id_jt (select z_role_id_jt.* from z_role_id inner join z_role_id_jt on z_role_id.id = z_role_id_jt.id where z_role_id_jt.role_id in (select internal_id from z_exemption));

insert into patient_encounter_id (select * from act_id where act_id in (select internal_id from patient_encounter));
insert into z_patient_encounter_id (select z_act_id.* from z_act_id inner join z_act_id_jt on z_act_id.id = z_act_id_jt.id where z_act_id_jt.act_id in (select internal_id from z_patient_encounter));
insert into z_pat_enc_id_jt (select z_act_id_jt.* from z_act_id inner join z_act_id_jt on z_act_id.id = z_act_id_jt.id where z_act_id_jt.act_id in (select internal_id from z_patient_encounter));

insert into service_delivery_location_id (select * from role_id where role_id in (select internal_id from service_delivery_location));
insert into z_service_delivery_location_id (select z_role_id.* from z_role_id inner join z_role_id_jt on z_role_id.id = z_role_id_jt.id where z_role_id_jt.role_id in (select internal_id from z_service_delivery_location));
insert into z_sdloc_id_jt (select z_role_id_jt.* from z_role_id inner join z_role_id_jt on z_role_id.id = z_role_id_jt.id where z_role_id_jt.role_id in (select internal_id from z_service_delivery_location));


/*Update sequencesss*/

declare
    seqVal INTEGER;
begin

   select max(internal_id) + 1 into seqVal from annotation;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Annotation_sequence';
    execute immediate 'Create sequence Annotation_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from service_delivery_location;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence ServiceDeliveryLoc_sequence';
    execute immediate 'Create sequence ServiceDeliveryLoc_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from patient_encounter;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence PatientEncounter_sequence';
    execute immediate 'Create sequence PatientEncounter_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from therapy;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Therapy_sequence';
    execute immediate 'Create sequence Therapy_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from organization;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Organization_sequence';
    execute immediate 'Create sequence Organization_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from employee;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Employee_sequence';
    execute immediate 'Create sequence Employee_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   /*PHI_CI */
   select max(internal_id) + 1 into seqVal from attachment;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Attachment_sequence';
    execute immediate 'Create sequence Attachment_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from macro_text_suggestion;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence MacroTextSuggestion_sequence';
    execute immediate 'Create sequence MacroTextSuggestion_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from self_perception_check;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence SelfPerceptionCheck_sequence';
    execute immediate 'Create sequence SelfPerceptionCheck_sequence start with ' || seqVal || ' increment by 1';
   END IF;
  
   
   /*PHI_AMB
   select max(internal_id) + 1 into seqVal from attachment;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence Attachment_sequence';
    execute immediate 'Create sequence Attachment_sequence start with ' || seqVal || ' increment by 1';
   END IF;
   
   select max(internal_id) + 1 into seqVal from macro_text_suggestion;
   IF seqVal IS NOT NULL then
   execute immediate 'drop sequence MacroTextSuggestion_sequence';
    execute immediate 'Create sequence MacroTextSuggestion_sequence start with ' || seqVal || ' increment by 1';
   END IF;

   */

   end;
/


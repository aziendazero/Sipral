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
    

CREATE PROCEDURE[dbo].[Utils_DeleteAllIndexesOnTable]
-- Add the parameters for the stored procedure here
@TableName VarChar(100),
@ColumnName VarChar(100)
AS
BEGIN
Declare @IndexName varchar(100)


DECLARE index_cursor CURSOR FOR
select /*'drop index ['+s.name+'].['+o.name+'].['+i.name+'];'*/ i.name
  from sys.indexes i 
  join sys.objects o on i.object_id=o.object_id 
  join sys.schemas s on o.schema_id=s.schema_id
  INNER JOIN sys.index_columns AS ic
ON (ic.column_id > 0 and (ic.key_ordinal > 0
OR ic.partition_ordinal = 0
OR ic.is_included_column != 0))
AND (ic.index_id=CAST(i.index_id AS int)
AND ic.object_id=i.object_id)
  join sys.columns c on o.object_id = c.object_id 
  AND c.column_id = ic.column_id
  where i.is_primary_key<>1 and i.index_id>0  and o.type_desc='USER_TABLE' and s.name<>'sys' and o.name=@TableName and c.name=@ColumnName;
/*SELECT name FROM sysindexes where id = object_id(@TableName)
AND NAME IS NOT NULL and ROWS > 0*/

OPEN index_cursor

-- Perform the first fetch.
FETCH NEXT FROM index_cursor into @IndexName

WHILE @@FETCH_STATUS = 0
BEGIN

if left(@IndexName,2) = 'PK'
BEGIN
print 'drop constraint ' + @IndexName + ' on ' + @TableName
Exec( 'ALTER TABLE ' + @TableName +
'DROP CONSTRAINT ' + @IndexName )

END
ELSE
BEGIN
-- This is executed as long as the previous fetch succeeds.
print 'drop index ' + @IndexName + ' on ' + @TableName


Exec('drop index ' + @IndexName + ' on ' + @TableName)
END

FETCH NEXT FROM index_cursor into @IndexName

END

CLOSE index_cursor
DEALLOCATE index_cursor
END
   
	  
create procedure [dbo].[DropAllColumnConstraints]
      @tableName varchar(128),
      @columnName varchar(128)
      as
   set nocount on
   set xact_abort on
   while 0=0 begin
      declare @constraintName varchar(128)
      set @constraintName = (
         select top 1 constraint_name
            from information_schema.constraint_column_usage
            where table_name = @tableName and column_name = @columnName )
      if @constraintName is null break
      exec ('alter table "'+@tableName+'" drop constraint "'+@constraintName+'"')
      end
	  
--Remove columns of act/role/entity:
USE [PHI];
GO
execute [dbo].[DropAllColumnConstraints] 'annotation', 'uncertainty_code';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'status_code';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'mood_code';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'language_code';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'class_code';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'repeat_lowclosed';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'repeat_number_low';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'repeat_highclosed';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'repeat_number_high';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'negation_ind';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'interruptible_ind';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'interruptible_ind';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'derivation_expr';
execute [dbo].[DropAllColumnConstraints] 'annotation', 'derivation_expr';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'uncertainty_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'status_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'mood_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'language_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'class_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'repeat_lowclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'repeat_number_low';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'repeat_highclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'repeat_number_high';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'negation_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'interruptible_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'interruptible_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'derivation_expr';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'annotation', 'derivation_expr';
alter table annotation drop column uncertainty_code, status_code, mood_code, language_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr/*, thrombolysisby, arrival, ward_arrival_time, timi_risk_score, thrombolysistime, arrival_time, angor_time*/;
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'title_string';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'title_bytes';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'repeat_lowclosed';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'repeat_number_low';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'repeat_highclosed';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'repeat_number_high';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'negation_ind';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'interruptible_ind';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'independent_ind';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'derivation_expr';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'discharge';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'uncertainty_code';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'mood_code';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'level_code';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'language_code';
execute [dbo].[DropAllColumnConstraints] 'patient_encounter', 'class_code';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'uncertainty_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'title_bytes';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'repeat_lowclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'repeat_number_low';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'repeat_highclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'repeat_number_high';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'negation_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'interruptible_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'independent_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'derivation_expr';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'discharge';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'uncertainty_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'mood_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'level_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'language_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'patient_encounter', 'class_code';
alter table patient_encounter drop column title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, discharge, uncertainty_code, mood_code, level_code, language_code, class_code ;
execute [dbo].[DropAllColumnConstraints] 'therapy', 'uncertainty_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'status_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'mood_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'level_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'language_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'class_code';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'title_string';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'title_bytes';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'text_string';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'text_bytes';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'repeat_lowclosed';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'repeat_number_low';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'repeat_highclosed';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'repeat_number_high';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'negation_ind';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'interruptible_ind';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'independent_ind';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'derivation_expr';
execute [dbo].[DropAllColumnConstraints] 'therapy', 'availability_time';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'uncertainty_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'status_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'mood_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'level_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'language_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'class_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'title_string';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'title_bytes';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'text_string';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'text_bytes';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'repeat_lowclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'repeat_number_low';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'repeat_highclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'repeat_number_high';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'negation_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'interruptible_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'independent_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'derivation_expr';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'therapy', 'availability_time';
alter table therapy drop column uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time;
execute [dbo].[DropAllColumnConstraints] 'organization', 'status_code';
execute [dbo].[DropAllColumnConstraints] 'organization', 'determiner_code';
execute [dbo].[DropAllColumnConstraints] 'organization', 'code';
execute [dbo].[DropAllColumnConstraints] 'organization', 'class_code';
execute [dbo].[DropAllColumnConstraints] 'organization', 'existence_lowclosed';
execute [dbo].[DropAllColumnConstraints] 'organization', 'existence_time_low';
execute [dbo].[DropAllColumnConstraints] 'organization', 'existence_highclosed';
execute [dbo].[DropAllColumnConstraints] 'organization', 'existence_time_high';
execute [dbo].[DropAllColumnConstraints] 'organization', 'desc_string';
execute [dbo].[DropAllColumnConstraints] 'organization', 'desc_bytes';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'status_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'determiner_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'class_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'existence_lowclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'existence_time_low';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'existence_highclosed';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'existence_time_high';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'desc_string';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'organization', 'desc_bytes';
alter table organization drop column status_code, determiner_code, code, class_code, existence_lowclosed, existence_time_low, existence_highclosed, existence_time_high, desc_string, desc_bytes;
execute [dbo].[DropAllColumnConstraints] 'employee', 'status_code'; 
execute [dbo].[DropAllColumnConstraints] 'employee', 'scoperinternalid';
execute [dbo].[DropAllColumnConstraints] 'employee', 'playerinternalid';
execute [dbo].[DropAllColumnConstraints] 'employee', 'class_code';
execute [dbo].[DropAllColumnConstraints] 'employee', 'quantity_numerator';
execute [dbo].[DropAllColumnConstraints] 'employee', 'quantity_denominator';
execute [dbo].[DropAllColumnConstraints] 'employee', 'negation_ind';
execute [dbo].[DropAllColumnConstraints] 'employee', 'certificatetext_string';
execute [dbo].[DropAllColumnConstraints] 'employee', 'certificatetext_bytes';
execute [dbo].[DropAllColumnConstraints] 'employee', 'salaryquantity_currency';
execute [dbo].[DropAllColumnConstraints] 'employee', 'jobtitlename_code';
execute [dbo].[DropAllColumnConstraints] 'employee', 'salaryquantity_value';
execute [dbo].[DropAllColumnConstraints] 'employee', 'jobtitlename_data';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'status_code'; 
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'scoperinternalid';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'playerinternalid';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'class_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'quantity_numerator';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'quantity_denominator';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'negation_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'certificatetext_string';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'certificatetext_bytes';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'salaryquantity_currency';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'jobtitlename_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'salaryquantity_value';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'employee', 'jobtitlename_data';
alter table employee drop column status_code, scoperinternalid, playerinternalid, class_code, quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, salaryquantity_currency, jobtitlename_code, salaryquantity_value, jobtitlename_data;
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'certificatetext_string';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'certificatetext_bytes';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'status_code';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'scoperinternalid';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'playerinternalid';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'quantity_numerator'; 
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'quantity_denominator';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'negation_ind';
execute [dbo].[DropAllColumnConstraints] 'service_delivery_location', 'quantity_numerator';
--remove index
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'certificatetext_string';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'certificatetext_bytes';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'status_code';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'scoperinternalid';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'playerinternalid';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'quantity_numerator'; 
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'quantity_denominator';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'negation_ind';
execute [dbo].[Utils_DeleteAllIndexesOnTable] 'service_delivery_location', 'quantity_numerator';
alter table service_delivery_location drop column certificatetext_string, certificatetext_bytes, status_code, scoperinternalid, playerinternalid, quantity_numerator, quantity_denominator, negation_ind;

alter table z_annotation drop column uncertainty_code, status_code, mood_code, language_code, class_code, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr/*, thrombolysisby, arrival, ward_arrival_time, timi_risk_score, thrombolysistime, arrival_time, angor_time*/ ;
alter table z_patient_encounter drop  column discharge, uncertainty_code, mood_code, level_code, language_code, class_code, title_string, title_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr ;
alter table z_therapy drop column uncertainty_code, status_code, mood_code, level_code, language_code, code, class_code, title_string, title_bytes, text_string, text_bytes, repeat_lowclosed, repeat_number_low, repeat_highclosed, repeat_number_high, negation_ind, interruptible_ind, independent_ind, derivation_expr, availability_time;
alter table z_organization drop column status_code, determiner_code, code, class_code, existence_lowclosed, existence_time_low, existence_highclosed, existence_time_high, desc_string, desc_bytes;
alter table z_employee drop column status_code, scoperinternalid, playerinternalid, class_code, quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, salaryquantity_currency, jobtitlename_code, salaryquantity_value, jobtitlename_data;
alter table z_service_delivery_location drop column quantity_numerator, quantity_denominator, negation_ind, certificatetext_string, certificatetext_bytes, status_code, scoperinternalid, playerinternalid;

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
/*REMOVE FK*/
alter table INTERVENTO  drop constraint FK_INTERVENTO_authorRole;
alter table INTERVENTO drop constraint FK_INTERVENTO_cancelledByRole;
alter table ProcSecurity_roles drop constraint FK_ProcSecurity_roles;
alter table agenda_annotation drop constraint FK_agenda_annotati_authorRole;
alter table agenda_annotation drop constraint FK_agenda_annotati_cancelledBy;
alter table allergy drop constraint FK_Allergy_authorRole;
alter table allergy drop constraint FK_llrgy_cnclld_by_rl;
alter table ambulatory_diary drop constraint FK_ambulatory_diar_authorRole;
alter table ambulatory_diary drop constraint FK_ambulatory_diar_cancelledBy;
alter table annotation drop constraint FK_Annotation_authorRole;
alter table annotation drop constraint FK_nnttn_cnclld_by_rl;
alter table assessment_scale drop constraint FK_ssscl_authorRole;
alter table assessment_scale drop constraint FK_ssscl_cnclld_by_rl;
alter table call_annotation drop constraint FK_call_annotation_authorRole;
alter table call_annotation drop constraint FK_call_annotation_cancelledBy;
alter table cert_npi_rep drop constraint FK_cert_npi_rep_cancelledByRol;
alter table cert_npi_rep drop constraint FK_cert_npi_rep_authorRole;
alter table diagnosis drop constraint FK_diagnosis_authorRole;
alter table diagnosis drop constraint FK_diagnosis_cancelledByRole;
alter table discharge_data drop constraint FK_discharge_data_cancelledByR;
alter table discharge_data drop constraint FK_discharge_data_authorRole;
alter table employee_role drop constraint FK_EmployeeRole_code;
alter table encounter_procedure drop constraint FK_encounter_proce_cancelledBy;
alter table encounter_procedure drop constraint FK_encounter_proce_authorRole;
alter table endo_crin_rep drop constraint FK_endo_crin_rep_cancelledByRo;
alter table endo_crin_rep drop constraint FK_endo_crin_rep_authorRole;
alter table evaluation_report drop constraint FK_EvRprt_authorRole;
alter table evaluation_report drop constraint FK_EvRprt_cancelledByRole;
alter table familiar_med_hist drop constraint FK_familiar_med_hi_cancelledBy;
alter table familiar_med_hist drop constraint FK_familiar_med_hi_authorRole;
alter table general_report_anamnesis drop constraint RprtAna_authorRole;
alter table general_report_anamnesis drop constraint FK_RprtAna_cancelledByRole;
alter table generic_report drop constraint FK_generic_report_cancelledByR;
alter table generic_report drop constraint FK_generic_report_authorRole;
alter table growth_med_hist drop constraint FK_growth_med_hist_authorRole;
alter table growth_med_hist drop constraint FK_growth_med_hist_cancelledBy;
alter table i_cd10_multi_ax drop constraint FK_i_cd10_multi_ax_cancelledBy;
alter table i_cd10_multi_ax drop constraint FK_i_cd10_multi_ax_authorRole;
alter table identification drop constraint FK_Iden_authorRole;
alter table internal_activity drop constraint FK_itrnAct_cancelledByRole;
alter table internal_activity drop constraint FK_itrnAct_authorRole;
alter table lep_activity drop constraint FK_LEPAct_cancelledByRole;
alter table lep_activity drop constraint FK_Lep_Support;
alter table lep_activity drop constraint FK_LEPAct_authorRole;
alter table lep_activity drop constraint FK_Lep_Responsible;
alter table lep_execution drop constraint FK_lep_execution_authorRole;
alter table lep_execution drop constraint FK_lep_execution_cancelledByRo;
alter table mammo_carcinoma drop constraint FK_mammo_carcinoma_cancelledBy;
alter table mammo_carcinoma drop constraint FK_mammo_carcinoma_localizatio;
alter table mammo_carcinoma drop constraint FK_mammo_carcinoma_authorRole;
alter table nanda drop constraint FK_nanda_authorRole;
alter table nanda drop constraint FK_nanda_cancelledByRole;
alter table neoN_medHis drop constraint FK_neoN_medHis_cancelledByRole;
alter table neoN_medHis drop constraint FK_neoN_medHis_authorRole;
alter table note_medical_hist drop constraint FK_note_medical_hi_authorRole;
alter table note_medical_hist drop constraint FK_note_medical_hi_cancelledBy;
alter table note_post_op_card drop constraint FK_note_post_op_ca_authorRole;
alter table note_post_op_card drop constraint FK_note_post_op_ca_cancelledBy;
alter table npi_check drop constraint FK_npi_check_cancelledByRole;
alter table npi_check drop constraint FK_npi_check_authorRole;
alter table objective_nanda drop constraint FK_ObjNnd_authorRole;
alter table objective_nanda drop constraint FK_ObjNnd_cancelledByRole;
alter table pathological_med_hist drop constraint FK_pathological_me_cancelledBy;
alter table pathological_med_hist drop constraint FK_pathological_me_authorRole;
alter table post_op_ext_info drop constraint FK_PostOpExtInfo_authRole;
alter table post_op_ext_info drop constraint FK_PostOpExtInfo_cancByRole;
alter table post_operative_card drop constraint FK_PostOpCard_authorRole;
alter table post_operative_card drop constraint FK_PostOpCard_cancelledByRole;
alter table prescription drop constraint FK_prescription_cancelledByRol;
alter table prescription drop constraint FK_prescription_authorRole;
alter table prescription_dschrg drop constraint FK_prescription_ds_authorRole;
alter table prescription_dschrg drop constraint FK_prescription_ds_cancelledBy;
alter table procedure_db drop constraint FK_procedure_db_cancelledByRol;
alter table procedure_db drop constraint FK_procedure_db_authorRole;
alter table imaging_request drop constraint FK_RdlgclRqst_ptntEncuntr;
alter table imaging_request drop constraint FK_RdlgclRqst_Role;
alter table imaging_request drop constraint FK_RdlgclRqst_author;
alter table imaging_request drop constraint FK_RdiologiclRequest_ptient;
alter table sens_medHis drop constraint FK_sens_medHis_cancelledByRole;
alter table sens_medHis drop constraint FK_sens_medHis_authorRole;
alter table employee drop constraint FK_SDL_code;
alter table social_medical_hist drop constraint FK_social_medical__cancelledBy;
alter table social_medical_hist drop constraint FK_social_medical__authorRole;
alter table substanceadmin drop constraint FK_substanceadmin_authorRole;
alter table substanceadmin drop constraint FK_substanceadmin_cancelledByR;
alter table ther_init_eval drop constraint FK_ther_init_eval_cancelledByR;
alter table ther_init_eval drop constraint FK_ther_init_eval_authorRole;
alter table ther_post_eval drop constraint FK_ther_post_eval_cancelledByR;
alter table ther_post_eval drop constraint FK_ther_post_eval_authorRole;
alter table therap_final drop constraint FK_therap_final_cancelledByRol;
alter table therap_final drop constraint FK_therap_final_authorRole;
alter table therap_plan drop constraint FK_therap_plan_cancelledByRole;
alter table therap_plan drop constraint FK_therap_plan_authorRole;
alter table therap_results drop constraint FK_therap_results_cancelledByR;
alter table therap_results drop constraint FK_therap_results_authorRole;
alter table vital_sign drop constraint FK_VitalSign_authorRole;
alter table vital_sign drop constraint FK_VitalSign_cancelledByRole;
    

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
   order by a.table_name;
   
--CI INITIAL 154 fk to CODE_VALUE

--Create script for Drop FK of role code to code_value:
/*select 'alter table '||a.table_name||' drop constraint '||a.constraint_name||';'
  FROM all_cons_columns a
  JOIN all_constraints c ON a.owner = c.owner
                        AND a.constraint_name = c.constraint_name
  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner
                           AND c.r_constraint_name = c_pk.constraint_name
 WHERE c.constraint_type = 'R'
   AND c_pk.table_name = 'CODE_VALUE'
   and a.column_name like '%ROLE%'
   order by a.table_name;*/
   
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
create table code_value_role (id nvarchar(255) not null, change_reason nvarchar(255) null, code nvarchar(65) not null, creator nvarchar(65) null, default_child bit not null, description nvarchar(765) null, display_name nvarchar(255) not null, keywords nvarchar(65) null, lang_de nvarchar(255) null, lang_en nvarchar(255) null, lang_it nvarchar(255) null, oid nvarchar(255) not null, revised_date datetime2 null, sequence_number int not null, statusDB int not null, typeDB nvarchar(1) not null, valid_from datetime2 null, valid_to datetime2 null, version int not null, code_system int not null, abbreviation nvarchar(255) null, code_value_parent nvarchar(255) null, primary key (id), unique (oid, version));
alter table code_value_role add constraint FK_code_value_role_Parent foreign key (code_value_parent) references code_value_role;
alter table code_value_role add constraint FK_code_value_role_code_system foreign key (code_system) references code_system;
create index IX_code_value_role_code on code_value_role (code);
create index IX_code_value_role_code_system on code_value_role (code_system);
create index IX_code_value_role_Parent on code_value_role (code_value_parent);
create index IX_Code_Value_Role on code_value_role (id, version, typeDB, statusDB);

create table z_code_value_role (id nvarchar(255) not null, REV int not null, REVTYPE tinyint null, change_reason nvarchar(255) null, code nvarchar(65) null, creator nvarchar(65) null, default_child bit null, description nvarchar(765) null, display_name nvarchar(255) null, keywords nvarchar(65) null, lang_de nvarchar(255) null, lang_en nvarchar(255) null, lang_it nvarchar(255) null, oid nvarchar(255) null, revised_date datetime2 null, sequence_number int null, statusDB int null, typeDB nvarchar(1) null, valid_from datetime2 null, valid_to datetime2 null, version int null, code_system int null, abbreviation nvarchar(255) null, code_value_parent nvarchar(255) null, primary key (id, REV));
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
create table appoggio (id int not null, description nvarchar(255) not null)
insert into appoggio values (NEXT VALUE FOR code_system_sequence,'code_system_sequence_val')
insert into appoggio values (NEXT VALUE FOR revinfo_sequence,'revinfo_sequence_val')
/*DECLARE @code_system_sequence_val as  bigint 
DECLARE @revinfo_sequence_val as bigint 
SET @code_system_sequence_val =  NEXT VALUE FOR code_system_sequence ;
SET @revinfo_sequence_val =  NEXT VALUE FOR revinfo_sequence ;*/

  --create envers revision
  INSERT INTO REVINFO (ID, TIMESTAMPDB, USERNAME)
  VALUES ((select a.id from appoggio a where a.description='revinfo_sequence_val'), 1 , 'PHI-DEV');


	--Create ROLES codeSystem
    Insert into CODE_SYSTEM (ID,AUTHORITY_DESCRIPTION,AUTHORITY_NAME,CODE_VALUE_CLASS,DISPLAY_NAME,NAME,CONTACT_INFORMATION,CREATION_DATE,DESCRIPTION,OID,ISO_CODE_LANG,REVISED_DATE,STATUSDB,VALID_FROM,VALID_TO,VERSION) 
    values ((select id from appoggio where description='code_system_sequence_val'),'Insiel Mercato','Insiel Mercato','CodeValueRole','Roles','ROLES','Insiel Mercato',GETDATE ( ),
			'Employee roles','2.16.840.1.113883.3.20.11','it',null,'1',GETDATE ( ),null,'0');
  --Create ROLES z_codeSystem History
    Insert into Z_CODE_SYSTEM (ID,REV,REVTYPE,AUTHORITY_DESCRIPTION,AUTHORITY_NAME,CODE_VALUE_CLASS,CONTACT_INFORMATION,CREATION_DATE,DESCRIPTION,DISPLAY_NAME,OID,ISO_CODE_LANG,NAME,REVISED_DATE,STATUSDB,VALID_FROM,VALID_TO,VERSION)
    values ((select id from appoggio where description='code_system_sequence_val'),(select id from appoggio where description='revinfo_sequence_val'),'0','Insiel Mercato','Insiel Mercato','CodeValueRole','Insiel Mercato',GETDATE(),'Employee roles','Roles','2.16.840.1.113883.3.20.11','it','ROLES',null,'1',GETDATE(),null,'0');



--Move EmployeeFunction EmployeeRole to CodeValueRole: (x bolzano: AMBBolzanoUserRoles)
    Insert into CODE_VALUE_ROLE   (ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION) 
    
      SELECT ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,(select id from appoggio where description='code_system_sequence_val'),null,ABBREVIATION --null parent to detach parent
      from code_value where DISPLAY_NAME = 'PHIAMBModEmployee'
    ;
	Insert into CODE_VALUE_ROLE   (ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION) 
    
      SELECT cv.ID,cv.CHANGE_REASON,cv.CODE,cv.CREATOR,cv.DEFAULT_CHILD,cv.DESCRIPTION,cv.DISPLAY_NAME,cv.KEYWORDS,cv.LANG_DE,cv.LANG_EN,cv.LANG_IT,cv.OID,cv.REVISED_DATE,cv.SEQUENCE_NUMBER,cv.STATUSDB,cv.TYPEDB,cv.VALID_FROM,cv.VALID_TO,cv.VERSION,(select id from appoggio where description='code_system_sequence_val'),cv.CODE_VALUE_PARENT,cv.ABBREVIATION
      from code_value cv join code_value parent on cv.CODE_VALUE_PARENT = parent.id where parent.DISPLAY_NAME = 'PHIAMBModEmployee'
    ;
--Move EmployeeFunction childs  EmployeeRole to CodeValueRole: 
    Insert into CODE_VALUE_ROLE   (ID,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION) 
    
      SELECT cv.ID,cv.CHANGE_REASON,cv.CODE,cv.CREATOR,cv.DEFAULT_CHILD,cv.DESCRIPTION,cv.DISPLAY_NAME,cv.KEYWORDS,cv.LANG_DE,cv.LANG_EN,cv.LANG_IT,cv.OID,cv.REVISED_DATE,cv.SEQUENCE_NUMBER,cv.STATUSDB,cv.TYPEDB,cv.VALID_FROM,cv.VALID_TO,cv.VERSION,(select id from appoggio where description='code_system_sequence_val'),cv.CODE_VALUE_PARENT,cv.ABBREVIATION
      from code_value cv join code_value parent on cv.CODE_VALUE_PARENT = parent.id where parent.DISPLAY_NAME = 'AMBBolzanoUserRoles'
    ;
    
    
    
--Move EmployeeRole HISTORY to z_CodeValueRole: 
    Insert into Z_CODE_VALUE_ROLE (ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION)
    
      SELECT ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,(select id from appoggio where description='code_system_sequence_val'),null,ABBREVIATION
      from z_code_value where DISPLAY_NAME = 'PHIAMBModEmployee';
--Move EmployeeRole childs HISTORY to z_CodeValueRole: 
    Insert into Z_CODE_VALUE_ROLE (ID,REV,REVTYPE,CHANGE_REASON,CODE,CREATOR,DEFAULT_CHILD,DESCRIPTION,DISPLAY_NAME,KEYWORDS,LANG_DE,LANG_EN,LANG_IT,OID,REVISED_DATE,SEQUENCE_NUMBER,STATUSDB,TYPEDB,VALID_FROM,VALID_TO,VERSION,CODE_SYSTEM,CODE_VALUE_PARENT,ABBREVIATION)
   
      SELECT cv.ID,cv.REV,cv.REVTYPE,cv.CHANGE_REASON,cv.CODE,cv.CREATOR,cv.DEFAULT_CHILD,cv.DESCRIPTION,cv.DISPLAY_NAME,cv.KEYWORDS,cv.LANG_DE,cv.LANG_EN,cv.LANG_IT,cv.OID,cv.REVISED_DATE,cv.SEQUENCE_NUMBER,cv.STATUSDB,cv.TYPEDB,cv.VALID_FROM,cv.VALID_TO,cv.VERSION,(select id from appoggio where description='code_system_sequence_val'),cv.CODE_VALUE_PARENT,cv.ABBREVIATION
      from z_code_value cv where cv.id like '2.16.840.1.113883.3.20.5.3.%' --( x AMBBolzanoUserRoles)
    ;
    
    drop table appoggio
/*NOW LAUNCH ORACLE SCHEMA UPDATE ANT TASK OF CS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/


--disable all FK
EXEC sp_msforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT all"
--Delete EmployeeFunction from CODE_VALUE
delete from code_value where id='2.16.840.1.113883.3.20.11.1_V0';
delete from code_value where code_value_parent='2.16.840.1.113883.3.20.11.1_V0';
delete from code_value where ID like '2.16.840.1.113883.3.20.5.3.%';
delete from code_value where DISPLAY_NAME = 'AMBBolzanoUserRoles';
delete from code_value where DISPLAY_NAME = 'PHIAMBModEmployee';
delete from code_value where DISPLAY_NAME = 'ModEmployee';

delete from Z_code_value where id='2.16.840.1.113883.3.20.11.1_V0';
delete from z_code_value where code_value_parent='2.16.840.1.113883.3.20.11.1_V0';
delete from z_code_value where ID like '2.16.840.1.113883.3.20.5.3.%';
delete from z_code_value where DISPLAY_NAME = 'AMBBolzanoUserRoles';
delete from z_code_value where DISPLAY_NAME = 'PHIAMBModEmployee';
delete from z_code_value where DISPLAY_NAME = 'ModEmployee';
-- Enable all the constraint in database
EXEC sp_msforeachtable "ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all"






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
        
        
        
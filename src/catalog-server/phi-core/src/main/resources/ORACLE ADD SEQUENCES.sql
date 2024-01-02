drop sequence act_sequence;
drop sequence entity_sequence;
drop sequence role_sequence;
drop sequence rolelink_sequence;
drop sequence participation_sequence;
drop sequence actrelationship_sequence;

declare
    act_id INTEGER;
    entity_id INTEGER;
    role_id INTEGER;
    rolelink_id INTEGER;
    participation_id INTEGER;
    actrelationship_id INTEGER;
begin
   SELECT SEQUENCE_NEXT_HI_VALUE INTO act_id FROM hibernate_sequences WHERE sequence_name = 'ACT_ID';
   SELECT SEQUENCE_NEXT_HI_VALUE INTO entity_id FROM hibernate_sequences WHERE sequence_name = 'ENTITY_ID';
   SELECT SEQUENCE_NEXT_HI_VALUE INTO role_id FROM hibernate_sequences WHERE sequence_name = 'ROLE_ID';
   SELECT SEQUENCE_NEXT_HI_VALUE INTO rolelink_id FROM hibernate_sequences WHERE sequence_name = 'ROLELINK_ID';
   SELECT SEQUENCE_NEXT_HI_VALUE INTO participation_id FROM hibernate_sequences WHERE sequence_name = 'PARTICIPATION_ID';
   SELECT SEQUENCE_NEXT_HI_VALUE INTO actrelationship_id FROM hibernate_sequences WHERE sequence_name = 'ACTRELATIONSHIP_ID';
   

    execute immediate 'create sequence act_sequence START WITH ' || act_id;
    execute immediate 'create sequence entity_sequence START WITH ' || entity_id;
    execute immediate 'create sequence role_sequence START WITH ' || role_id;
    execute immediate 'create sequence rolelink_sequence START WITH ' || rolelink_id;
    execute immediate 'create sequence participation_sequence START WITH ' || participation_id;
    execute immediate 'create sequence actrelationship_sequence START WITH ' || actrelationship_id;
end;
/

--drop table hibernate_sequences;

commit;


--from sequence to table:
/*
SELECT act_sequence.NEXTVAL from Dual;
update hibernate_sequences set sequence_next_hi_value = 3240362 WHERE sequence_name = 'ACT_ID';
SELECT entity_sequence.NEXTVAL from Dual;
update hibernate_sequences set sequence_next_hi_value = 261000 WHERE sequence_name = 'ENTITY_ID';
SELECT role_sequence.NEXTVAL from Dual;
insert into hibernate_sequences values ('ROLE_ID',264185);
*/
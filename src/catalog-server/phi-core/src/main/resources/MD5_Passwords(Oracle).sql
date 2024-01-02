create or replace function my_own_md5 (my_input IN varchar2) return varchar2 is
my_out varchar2(16);
BEGIN
dbms_obfuscation_toolkit.md5(INPUT_STRING=>my_input, CHECKSUM_STRING=>my_out);
RETURN my_out;
END;
/

update employee set password=LOWER( RAWTOHEX( UTL_RAW.CAST_TO_RAW( my_own_md5(password)))) where password is not null;
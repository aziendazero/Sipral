declare @nameIndex nvarchar(255)
declare @nameColumn nvarchar(255)
declare @isUniqueConstraint bit
declare @extraColumn nvarchar(255)
DECLARE index_cursor CURSOR FOR
SELECT COL_NAME(ic.OBJECT_ID,ic.column_id),i.name,i.is_unique_constraint
FROM sys.indexes AS i
INNER JOIN sys.index_columns AS ic
ON i.OBJECT_ID = ic.OBJECT_ID
AND i.index_id = ic.index_id
WHERE /*i.is_primary_key = 1 and*/ OBJECT_NAME(ic.OBJECT_ID)='patient_encounter' and 
COL_NAME(ic.OBJECT_ID,ic.column_id)in ('g2_rico','g2_epsd','g2_epis','g2_codsdo','g2_aasdo','visit_number_root','preadmit_number_root');

OPEN index_cursor

-- Perform the first fetch.
FETCH NEXT FROM index_cursor into @nameColumn,@nameIndex,@isUniqueConstraint

WHILE @@FETCH_STATUS = 0
BEGIN

	if @isUniqueConstraint=0
	 begin
		print 'DROP INDEX ['+@nameIndex+'] ON [dbo].[patient_encounter]'
		exec ('DROP INDEX ['+@nameIndex+'] ON [dbo].[patient_encounter]')
	 end
	else
	  begin
		print 'alter table [dbo].[patient_encounter] drop constraint '+@nameIndex
		exec ('alter table [dbo].[patient_encounter] drop constraint '+@nameIndex)
	  end

	set @extraColumn=case when @nameColumn='visit_number_root' then ',[visit_number_extension] ASC' else '' end +
				 case when @nameColumn='preadmit_number_root' then ',[preadmit_number_extension] ASC' else '' end +
				 case when @nameColumn!='preadmit_number_root' and @nameColumn!='visit_number_root' then '' else ''  end;
    print 'result from concatenation ' + @extraColumn
	print 'CREATE UNIQUE NONCLUSTERED INDEX ['+@nameIndex+'] ON [dbo].[patient_encounter]
	(
		['+@nameColumn+'] ASC ' +@extraColumn +'
	)
	WHERE (['+@nameColumn+'] IS NOT NULL)
	WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]'	
 
	 exec ('CREATE UNIQUE NONCLUSTERED INDEX ['+@nameIndex+'] ON [dbo].[patient_encounter]
	(
		['+@nameColumn+'] ASC ' +@extraColumn +'
	)
	WHERE (['+@nameColumn+'] IS NOT NULL)
	WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]')
FETCH NEXT FROM index_cursor into @nameColumn,@nameIndex,@isUniqueConstraint
END

CLOSE index_cursor
DEALLOCATE index_cursor
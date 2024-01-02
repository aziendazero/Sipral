package com.phi.cs.catalog.adapter.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.id.SequenceGenerator;

/**
 * SqlServer 2012 dialect
 * 
 * @author Alex Zupan
 */

public class SQLServer2012Dialect extends SQLServer2008Dialect {
	
	//See: http://www.componentix.com/blog/5/improved-hibernate-dialect-for-microsoft-sql-server
	public SQLServer2012Dialect() {
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.CHAR, "nchar(1)");
        registerColumnType(Types.VARCHAR, 4000, "nvarchar($l)");
        registerColumnType(Types.VARCHAR, "nvarchar(max)");
        //registerColumnType(Types.NVARCHAR, 4000, "nvarchar($l)");
        //registerColumnType(Types.NVARCHAR, "nvarchar(max)");
        //registerColumnType(Types.VARBINARY, 4000, "varbinary($1)"); produces wrong sql
        registerColumnType(Types.VARBINARY, "varbinary(max)");
        registerColumnType(Types.BLOB, "varbinary(max)");
        registerColumnType(Types.CLOB, "nvarchar(max)");
        
        registerHibernateType(Types.NVARCHAR, Hibernate.STRING.getName());
	}
	
	//Copied from hibernate 4.3 for seduences:
	
    @Override
    public boolean supportsSequences() {
            return true;
    }

    @Override
    public boolean supportsPooledSequences() {
            return true;
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
            return "create sequence " + sequenceName + " start with 1"; //start with 1 added by AZ to avoid deep negative ids
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
            return "drop sequence " + sequenceName;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
            return "next value for " + sequenceName;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
            return "select " + getSelectSequenceNextValString( sequenceName );
    }

    @Override
    public String getQuerySequencesString() {
            return "select name from sys.sequences";
    }
    
    //End Copied from hibernate 4.3
    
    //Added to use sequences as NativeIdentifierGenerator, avoid Exception:
    //Cannot use identity column key generation with <union-subclass> mapping for: com.phi.entities.baseEntity...
    
    public Class getNativeIdentifierGeneratorClass() {
    	return SequenceGenerator.class;
    }
    
}
package com.phi.db.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

/**
 * Read db properties from mySql.build.properties and oracle.build.properties and
 * builds an update script: difference between entities and current db
 * @author Zupan
 */

public class DbScriptUpdateGenerator {

	private static enum SupportedDb { mySql, oracle, sqlServer };
	
	private static SupportedDb selectedDb = SupportedDb.oracle;
	
	private static enum GenerationType {dropUnused, update};
	
	private static GenerationType selectedGeneration = GenerationType.update;
	
	private static final String regexIX = "^create index (.*)( on [^\\.]*.([^\\(]*) \\(([^\\)]*)\\))$";
	private static final Pattern ptnIX = Pattern.compile(regexIX, Pattern.CASE_INSENSITIVE);
	
	private static final String regexFK = "^(alter table [^\\.]*\\.([^ ]*) add constraint )(.*)( foreign key \\(([^\\)]*)\\) .*)$";
	private static final Pattern ptnFK = Pattern.compile(regexFK, Pattern.CASE_INSENSITIVE);
	
	public static void main (String[] args) {
		
//		if (args.length == 0){
//			//4 debug
//			args = new String[3];
//			args[0] = "oracle";
//			args[1] = "update";
//			args[2] = "com.phi.entities.baseEntity.AaaExtendedTestz";
//		}

		if (args.length >= 2) {
			selectedDb = SupportedDb.valueOf(args[0]);
			selectedGeneration = GenerationType.valueOf(args[1]);
		}

		
//		//Generate script only for selected entities:
//		List<String> entityClasses = null;
//		
//		if (args.length >= 3) {
//			entityClasses = new ArrayList<String>();
//			for (int z = 2; z < args.length; z++ ) {
//				entityClasses.add(args[z]);
//			}
//		}

		try {
			
			// Read db properties from mySql.build.properties and oracle.build.properties
			Properties properties = new Properties();

			String dbPropertiesFileName = selectedDb + ".build.properties";
			
			System.out.println("Reading db config from: " + dbPropertiesFileName);
			
			properties.load(new FileInputStream(dbPropertiesFileName));
			
			String connetionDriVerClass = properties.getProperty("driver-class");
			String connetionUrl = properties.getProperty("connection-url");
			String user = properties.getProperty("user-name");
			String pass = properties.getProperty("password");

			Map<String, Object> configOverrides = new HashMap<String, Object>();
			configOverrides.put("hibernate.connection.url", connetionUrl);
			configOverrides.put("hibernate.connection.driver_class", connetionDriVerClass);
			configOverrides.put("hibernate.connection.username", user);
			configOverrides.put("hibernate.connection.password", pass);
			
			if (selectedDb.equals(SupportedDb.oracle)) {
				configOverrides.put("hibernate.default_schema",user);
			}
			
			Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
			
			AnnotationConfiguration config = ejb3Configuration.getHibernateConfiguration();
			
//			if (entityClasses != null) {
//				for (int z = 0; z < entityClasses.size(); z++ ) {
//					Class clazz;
//					try {
//						clazz = Class.forName(entityClasses.get(z));
//						config.addAnnotatedClass(clazz);
//					} catch (Exception e) {
//						System.err.println(entityClasses.get(z) + "not found " + e.getMessage());
//					}
//				}
//			}
			
			ejb3Configuration.configure(selectedDb.name(), configOverrides);

			
			//Enable envers
			AuditConfiguration.getFor(config);

			// Loading database driver
			try {
				Class.forName(connetionDriVerClass);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			//
			System.out.println("Connecting to : " + connetionUrl + " user: " + user);
			
			// Connecting to the database
			Connection conn = DriverManager.getConnection(connetionUrl, user, pass);
			
			Dialect dialect = Dialect.getDialect(properties);//config.getProperties());
			
			DatabaseMetadata databaseMetadata = new DatabaseMetadata(conn, dialect);

			if (selectedGeneration.equals(GenerationType.dropUnused)) {
				//Generation of drop unused scripts, drop unmapped tables and drop unmapped columns
				String script = deleteUnusedTables(conn, config, databaseMetadata, properties.getProperty("db-update-get-all-tables"), properties.getProperty("db-update-get-all-columns"));
				
				String fileName = "src/main/resources/" + selectedDb.name().toUpperCase() + "_SCHEMA_DROP_UNMAPPED.sql";
				
				File file = new File(fileName);
				
				PrintWriter out = new PrintWriter(new FileWriter(file));  
				  
				out.println(script);

				out.close(); 
				//End Generation of drop unised scripts
			}

			if (selectedGeneration.equals(GenerationType.update)) {
			
				String fileNamez = "src/main/resources/" + selectedDb.name().toUpperCase() + "_SCHEMA_UPDATE.sql";
				
				System.out.println("Generating schema update script for : " + connetionUrl + " user: " + user);
				System.out.println("Into: " + fileNamez);
				
				String[] createSQL = config.generateSchemaUpdateScript(dialect, databaseMetadata);
				//String[] createSQL = ejb3Configuration.getHibernateConfiguration().generateSchemaCreationScript(dialect);

				File filez = new File(fileNamez);
				
				PrintWriter outz = new PrintWriter(new FileWriter(filez));  
				
				outz.println("/*Update script for db: " + connetionUrl + " user: " + user + "*/");

				String indexName;
				String onName;
				String tableName;
				String columnName;
				
				String fkName;
				String alterTblAddCstr;
				String references;
				
				// Write each string in the array on a separate line  
				for (String s : createSQL) {
					if (selectedDb.equals(SupportedDb.oracle)) {
						
						//ORACLE: MAX 30 char for FK and indexes:
						
						Matcher matcher = ptnIX.matcher(s);

						if (matcher.matches()) { //IF INDEX
							
							indexName = matcher.group(1);
							onName = matcher.group(2);
							tableName = matcher.group(3);
							columnName = matcher.group(4);
					
							if (indexName.length() > 30) {
					
								indexName = "IX_";
								if (tableName.length() > 15) {
									indexName += tableName.substring(0, 15);
								} else {
									indexName += tableName;
								}
								
								indexName += "_" + columnName;
								if (indexName.length() > 30) {
									indexName = indexName.substring(0, 30);
								}
	
								s = "/* " + s + " */" + System.getProperty("line.separator");
								
								s += "create index " + indexName + onName;
							}
							
						}
						
						Matcher matcherFK = ptnFK.matcher(s);

						
						if (matcherFK.matches()) {

							alterTblAddCstr = matcherFK.group(1);
							fkName = matcherFK.group(3);
							tableName = matcherFK.group(2);
							columnName = matcherFK.group(5);
							references = matcherFK.group(4);
							
							if (fkName.length() > 30) {
					
								fkName = "FK_";
								if (tableName.length() > 15) {
									fkName += tableName.substring(0, 15);
								} else {
									fkName += tableName;
								}
								
								fkName += "_" + columnName;
								if (fkName.length() > 30) {
									fkName = fkName.substring(0, 30);
								}
								
								s = "/* " + s + " */" + System.getProperty("line.separator");
	
								s += alterTblAddCstr + fkName + references;
								
							}
							
						}
					}
				    outz.println(s + ";");
				}  
				  
				outz.close(); 
				
				System.out.println("Generated: " + fileNamez);
			}
			
		} catch (Exception e) {
			System.err.println("GENERATION FAILED!");
			e.printStackTrace();
		}

		System.out.println("Script execution completed.");
	}
	

	
	private static String deleteUnusedTables(Connection connection, AnnotationConfiguration config, DatabaseMetadata databaseMetadata, String getAllTablesSql, String getAllColumnsSql) {
		
		StringBuffer sb  = new StringBuffer();
		String eol = System.getProperty("line.separator");
		
//		String defaultCatalog = config.getProperty( Environment.DEFAULT_CATALOG );
//		String defaultSchema =  config.getProperty( Environment.DEFAULT_SCHEMA );
		
		try {
			
			Statement statement = connection.createStatement();
	
			
			//FIND UNUSED TABLES
			ResultSet rs = statement.executeQuery(getAllTablesSql);
			
			List<String> dbTableNames = new ArrayList<String>();
			
			while (rs.next()) {
				dbTableNames.add(rs.getString(1).toLowerCase());
			}
			
			rs.close();
			statement.close();
			
			List<String> mappedTableNames = new ArrayList<String>();
			Map<String, Table> mappedTables = new HashMap<String, Table>();
			
			Iterator<Table> tblclasses = config.getTableMappings();
			while (tblclasses.hasNext()) {

				Table tbl = tblclasses.next();
				if ( tbl.isPhysicalTable() ) {
					mappedTableNames.add(tbl.getName().toLowerCase());
					mappedTables.put(tbl.getName().toLowerCase(), tbl);
				} /*else {
					System.out.println("Skipping: " + tbl.getName() + " abstract!");
				}*/
			}
			List<String> emptyTableNames = new ArrayList<String>();
			
			List<String> notMappedTableNames = new ArrayList<String>();
			
			for (String dbTableName : dbTableNames) {
				if (dbTableName.equals("hibernate_sequences")) {
					continue; //Skip hibernate tables
				}
				if (dbTableName.startsWith("qrtz_")) {
					continue; //Skip Quartz tables
				}
				
				//IS empty?
				Statement statementE = connection.createStatement();
				ResultSet rsE = statementE.executeQuery("SELECT count(*) FROM " + dbTableName);
				
				rsE.next();
				Long nOfRows = rsE.getLong(1);
				
				rsE.close();
				statementE.close();
				
				if (!mappedTableNames.contains(dbTableName)) {
					notMappedTableNames.add(dbTableName);
					continue;
				}
				
				if (nOfRows.equals(0l)) {
					emptyTableNames.add(dbTableName);
				}
				

			}
			
			Collections.sort(notMappedTableNames);
			Collections.sort(emptyTableNames);
			
			sb.append("/*Drop unused script for db: " + connection.getMetaData().getURL() + " user: " + connection.getMetaData().getUserName() + "*/");
			sb.append(eol);
			sb.append("/*UnMapped tables:*/");
			sb.append(eol);
			System.out.println("UnMapped tables: check the content, and if you are sure, drop them!!!");
			for (String notMappedTableName : notMappedTableNames) {
				sb.append("drop table " + notMappedTableName + ";");
				sb.append(eol);
				System.out.println(notMappedTableName);
			}
			
			sb.append("/*Mapped but empty tables:*/");
			sb.append(eol);
			System.out.println("Mapped but empty tables: check the content, and if you are sure, unmap and drop them!!!");
			for (String emptyTableName : emptyTableNames) {
				sb.append("drop table " + emptyTableName + ";");
				sb.append(eol);
				System.out.println(emptyTableName);
			}
			
			
			
			
			//FIND UNUSED COLUMNS
			sb.append("/*UnMapped columns:*/");
			sb.append(eol);
			System.out.println("UnMapped columns : " );
			for (String mappedTableName : mappedTableNames) {
				//Loop db columns
				
				//TENTATIVO
				//TableMetadata tableInfo = databaseMetadata.getTableMetadata(mappedTableName, defaultSchema, defaultCatalog, false);
				//tableInfo.
				//FINE TENTATIVO
				PreparedStatement preparedStatement = connection.prepareStatement(getAllColumnsSql);
				preparedStatement.setString(1,mappedTableName.toUpperCase());
				
				ResultSet rsCols = preparedStatement.executeQuery();;
				List<String> dbColumnNames = new ArrayList<String>();
				
				while (rsCols.next()) {
					dbColumnNames.add(rsCols.getString(1).toLowerCase());
				}
				
				
				//Loop mapped columns
				Iterator<Column> mappedColumnsIte = mappedTables.get(mappedTableName).getColumnIterator();
				
				List<String> mappedColumnNames = new ArrayList<String>();

				while (mappedColumnsIte.hasNext()) {
					
					Column mappedColumn = mappedColumnsIte.next();
					mappedColumnNames.add(mappedColumn.getName().toLowerCase());
				}
				
				List<String> notMappedColumnNames = new ArrayList<String>();
				
				for (String dbColumnName : dbColumnNames) {
					if (!mappedColumnNames.contains(dbColumnName)) {
						notMappedColumnNames.add(dbColumnName);
					}
				}
				
				if (!notMappedColumnNames.isEmpty()) {
					System.out.println(mappedTableName + " unMapped columns: ");
					sb.append("alter table " + mappedTableName + " drop (");
					for (String notMappedColumnName : notMappedColumnNames) {
						
						sb.append(notMappedColumnName);
						sb.append(", ");
						System.out.print(notMappedColumnName + ", ");
						//System.out.println(notMappedColumnName);
					}
					sb.deleteCharAt(sb.length()-2);
					sb.append(") CASCADE CONSTRAINTS;");
					sb.append(eol);
					System.out.println("");
					//System.out.println("UnMapped columns of " + mappedTableName + " : " );
				}
				
				rsCols.close();
				preparedStatement.close();				
			}

		} 
		catch (Exception ex) {
			System.out.println("Unable to connect to batabase.");
			ex.printStackTrace();
		}
		return sb.toString();
		
	}
	
}
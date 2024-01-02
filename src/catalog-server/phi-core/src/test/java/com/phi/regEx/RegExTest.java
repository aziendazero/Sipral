package com.phi.regEx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTest {

	
	public static void main (String[] args) {
		
		String inIX = 
				"<table:table-cell table:style-name=\"Tabella3.A1\" office:value-type=\"string\">" +
					"<text:p text:style-name=\"P32\">" +
						"[#list Procpratiche.operatori as op]" +
							"[#if op.employee.internalId==Procpratiche.rfp.internalId]" +
								"${<text:p text:style-name=\"P2\">(op.code)!</text:p>}" +
								"[#break]" +
							"[/#if]" +
						"[/#list]" +
					"</text:p>" +
					"[#if </text:span><text:soft-page-break></text:soft-page-break><text:span text:style-name=\"T9\">Infortuni.deceasedTime??]" +
						"<text:p text:style-name=\"P2\">${(Procpratiche.rfp.name)!}</text:p>" +
					"[/#if]" +
				"</table:table-cell>";
		
		//String freeMarkerVariablePattern = "(\\$\\{[^\\}]*\\})";
		String freeMarkerVariablePattern = "\\$\\{([^\\}]*)\\}";
		String freeMarkerCodePattern = "(\\[/?#[^\\]]*\\])";
		//Pattern xmlPattern = Pattern.compile("<[^>]*>");
		
		Pattern ptnv = Pattern.compile(freeMarkerVariablePattern, Pattern.CASE_INSENSITIVE);
		Pattern ptnc = Pattern.compile(freeMarkerCodePattern, Pattern.CASE_INSENSITIVE);
		
		Matcher matcherv = ptnv.matcher(inIX);
		
		StringBuffer sb = new StringBuffer();

		 while (matcherv.find()) {
			   String code = matcherv.group(1);
			   String fixedCode = code.replaceAll("<[^>]*>", "");
			   //String fixedCode = "cicco";
			   matcherv.appendReplacement(sb, "\\$\\{" + fixedCode + "\\}");
			   System.out.println(code + " -> " + fixedCode);
			 }
		 matcherv.appendTail(sb);
		 
		 //System.out.println(sb.toString());
		
		Matcher matcherc = ptnc.matcher(sb.toString());
		sb = new StringBuffer();
		 while (matcherc.find()) {
			   String code = matcherc.group(1);
			   String fixedCode = code.replaceAll("<[^>]*>", "");
			   matcherc.appendReplacement(sb, fixedCode);
			   System.out.println(code + " -> " + fixedCode);

			 }
		 matcherc.appendTail(sb);
		 System.out.println(sb.toString());
		
//		 InputStream stream = new ByteArrayInputStream(inIX.getBytes(StandardCharsets.UTF_8));
//		   Scanner sc = new Scanner(stream);
//		   while ((sc.hasNext())) {
//			   System.out.println(sc.next(ptnv));
//	       }
//	       sc.close();
//		   sc = new Scanner(stream);
//		   while ((sc.hasNext())) {
//			   System.out.println(sc.next(ptnc));
//	       }
//	       sc.close();

		//Short FK and indexes in patched hibernate
		
		//MODIFIED BY PHI TEAM 4 SHORT FK
//		String tableName = getName();
//		if (tableName.length() > 15) {
//			tableName = tableName.substring(0, 15);
//		}
//		String fkName = "FK_" + tableName + "_" + ((Column)fk.getColumns().get(0)).getName();
//		if (fkName.length() > 30) {
//			fkName = fkName.substring(0, 30);
//		}
		
		
		//MODIFIED BY PHI TEAM 4 SHORT Index
//		String tableName = getName();
//		if (tableName.length() > 15) {
//			tableName = tableName.substring(0, 15);
//		}
//		String ixName = "IX_" + tableName + "_" + ((Column)parentIndex.getColumnIterator().next()).getName();
//		if (ixName.length() > 30) {
//			ixName = ixName.substring(0, 30);
//		}
//		index.setName( ixName );
		//index.setName( getName() + parentIndex.getName() );
		
		
		
		inIX = "create index IX_aaa_extended_te_serviceDelizzzzz on phi_ci_142.aaa_extended_testz (serviceDeliveryLocation)";
		
		String outIX = "";
		
		String indexName;
		String onName;
		String tableName;
		String columnName;
		
		String regex = "^create index (.*)( on [^\\.]*.([^\\(]*) \\(([^\\)]*)\\))$";
		Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = ptn.matcher(inIX);

		
		if (matcher.matches()) {

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
	
				outIX = "create index " + indexName + onName;
			
			}
			
		}
		
		System.out.println(inIX);
		System.out.println(outIX);
		
		//FK
		
		String inFK = "alter table phi_ci_142.aaa_extended_testz add constraint FK_aaa_extended_te_serviceDelizzzzzzzz foreign key (serviceDeliveryLocation) references phi_ci_142.service_delivery_location;";
		String outFK = "";	
		
		String fkName;
		String alterTblAddCstr;
		String references;
//		String onName;
//		String tableName;
//		String columnName;
		
		//"alter table phi_ci_142.contact add constraint FK_Contact_parentalCode foreign key (parental_code) references phi_ci_142.code_value;";
		String regexFK = "^(alter table [^\\.]*\\.([^ ]*) add constraint )(.*)( foreign key \\(([^\\)]*)\\) .*)$";
		Pattern ptnFK = Pattern.compile(regexFK, Pattern.CASE_INSENSITIVE);
		Matcher matcherFK = ptnFK.matcher(inFK);

		
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
	
				outFK = alterTblAddCstr + fkName + references;
				
			}
			
		}

		System.out.println(inFK);
		System.out.println(outFK);
	}
}

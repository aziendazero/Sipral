package com.phi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a PHI entity into a flex entity
 * 
 * @author Alex
 */
public class Java2FlexEntity {
	
	private static String javaPath = System.getProperty("user.dir")+File.separator+File.separator+"src/main/java/com/phi/entities/baseEntity/";
	private static String javaClassFile = "OperationSequence";
//	private static String javaClassFile = "OperationStep";
//	private static String javaClassFile = "Session";
//	private static String javaClassFile = "Facility";
//	private static String javaClassFile = "AppointmentConsumer";
//	private static String javaClassFile = "AbstractOperationSequence";
//	private static String javaClassFile = "AbstractOperationStep";
//	private static String javaClassFile = "PathwaySequence";
//	private static String javaClassFile = "PathwayStep";
	
	
	
	
	private static String flexPath = System.getProperty("user.dir")+File.separator+File.separator+"src/main/java/com/phi/entities/baseEntity/";
	
	public static void main (String args[]) throws IOException {
		
		
		List<String> fileContent = parseFile(javaPath+javaClassFile+ ".java");

		// Couple of regex first math second replace
		String [] patternReplaceCouples = new String[] {
				"package ([^;]+);",
				"package $1 {",
				
				"import java.util.ArrayList;",
				"import mx.collections.ArrayCollection;",
				
				"import java.*",
				"",
				
				"import org.*",
				"",
				
				"import com\\.fasterxml.*",
				"",
				
				"@.*",
				"",
				
				".*serialVersionUID.*",
				"",
				
				"public ([^\\s]+) set([^\\s]+)\\(([^\\s\\)]+) ([^\\)]+)\\)\\s*\\{",   
				"public function set $2($4:$3):$1 {",
				
				"public ([^\\s]+) get([^\\s]+)\\(\\)\\s*\\{",
				"public function get $2():$1 {",
				
				"(.*) (get|set) (.*)",
				"$1 $2",//"$1 \\L$2",
				
				"(protected|private) ([^\\s\\)]+) ([^\\)]+)\\s*\\;",
				"$1 var _$3:$2;",
				
				"this\\.",
				"this._",
				
				"return[\\s)]+",
				"return _",
				
				"long",
				"Number",
				
				"Boolean[\\s]+[=][\\s]+[false|true]",
				"Object", //FIXME
				
				"Integer",
				"int",
				
				"ArrayList<.*>",
				"ArrayCollection",
				
				"List<.*>",
				"ArrayCollection"
				
				
		};
		
		
		List<String> linePatterns = new ArrayList<String>();
		for (int i=0; i<patternReplaceCouples.length; i+=2) {
			linePatterns.add(patternReplaceCouples[i]);
		}
		
//		List<String> fileResults = new ArrayList<String>();
		
		//for (String line : fileContent) {
		for (int z=0; z<fileContent.size(); z++) {
			for (int i=0; i<patternReplaceCouples.length; i+=2) {  //solo i pari: 0,2,...
				
				String searchPattern= patternReplaceCouples[i];
				String replacePattern = patternReplaceCouples[i+1];
				
				Pattern p = Pattern.compile(searchPattern);
				Matcher m = p.matcher(fileContent.get(z));
				if (m.find()) {
					String line;
					if (searchPattern.equals("(.*) (get|set) (.*)")) { //FIXME
						String methodName = m.group(3);
						char[] stringArray = methodName.toCharArray();
						stringArray[0] = Character.toLowerCase(stringArray[0]);
						methodName = new String(stringArray);
						line = m.group(1) + " " + m.group(2) + " " + methodName;
					} else {
						line = m.replaceFirst(replacePattern);  
					}
					fileContent.set(z, line);
					//line matching only one pattern.
					//break;
				}
			}
			//fileResults.add(line);
		}
		
		
		//ora scrivo..
		FileWriter writer = new FileWriter(new File (flexPath+javaClassFile+".as"));
        writer.write(org.apache.commons.lang.StringUtils.join(fileContent, "\n"));
        writer.close();
        
	}

	private static List<String> parseFile(String f) {
		
		List<String> ret = new ArrayList<String>();
		
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				ret.add(line);
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return ret;
	}

}
package com.phi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyze PHI entities table names, columns name, FK and IX 
 * 
 * @author Daniele
 */

public class DBnameChecker {
	
//	private static String javaPath   = System.getProperty("user.dir") + File.separator + "src\\main\\java\\com\\phi\\entities\\baseEntity\\";
	private static String javaPath   = "C:\\PHI2\\WORKSPACE_DSC_TRUNK\\phi-klinik\\backend\\src\\main\\java\\com\\phi\\entities\\baseEntity\\";
	
	//Elements names with 30 or more characters
	private static String table = "(^.*@(Table)\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{29,})(\\\"\\s*\\))";  // length grater than 29 because the history tables adds 2 chars "Z_"  			  
	private static String joinTable = "(^.*@(JoinTable)\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{29,})(\\\")";  // length grater than 29 because the history tables adds 2 chars "Z_"
	private static String joinColumn = "(^.*@(JoinColumn)\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\"\\s*\\))"; 
	private static String foreignKey = "(^.*@(ForeignKey)\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\")";
	private static String inverseForeignKey = "(^.*@(ForeignKey).*\\s*inverseName\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\")";
	private static String index = "(^.*@(Index)\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\"\\s*\\))";
	
	public static void main (String args[]) throws IOException {
		File dir = new File(javaPath);
		String[] entityFileNames = dir.list(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".java");
		    }
		});
		
		Boolean found;
		
		for (String entityFileName : entityFileNames) {
			found = false;
			List<String> fileContent = parseFile(javaPath + entityFileName);
			for (int z=0; z<fileContent.size(); z++) {
				found |= finder(fileContent.get(z),table,28);
				found |= finder(fileContent.get(z),joinTable,28);
				found |= finder(fileContent.get(z),joinColumn,30);
				found |= finder(fileContent.get(z),foreignKey,30);
				found |= finder(fileContent.get(z),inverseForeignKey,30);
				found |= finder(fileContent.get(z),index,30);
			}
			if (found) {
				System.out.println(javaPath + entityFileName + "\n");
			}
		}
		System.out.println("Check complete.");
	}

	private static Boolean finder(String source, String regExp, Integer maxLength) {
		Boolean found = false;
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(source);
		if (m.find()) {
			found = true;
			System.out.println(m.group(2) + ": "+ m.group(3) +" ("+m.group(3).length()+" chars)");
		}
		return found;
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
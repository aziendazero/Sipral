package com.phi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.phi.cs.util.CsConstants;

/**
 * Analyze PHI entities table names, columns name, FK and IX 
 * 
 * @author Daniele
 */

public class DBnamePatcher {

	private static String javaPath   = System.getProperty("user.dir") + File.separator + "src\\main\\java\\com\\phi\\entities\\baseEntity\\";
	private static String outputPath   = System.getProperty("user.dir") + File.separator + "src\\main\\java\\com\\phi\\entities\\baseEntity\\";

/*
	private static String javaPath   = "D:\\PHI2\\workspace_amb_trunk\\PHI_AMB\\backend\\src\\main\\java\\com\\phi\\entities\\baseEntity\\";
	private static String outputPath   = "D:\\PHI2\\workspace_amb_trunk\\PHI_AMB\\backend\\src\\main\\java\\com\\phi\\entities\\baseEntity\\";
*/
	private static final String NL = System.getProperty("line.separator");
	 
	//Elements names with 30 or more characters
	private static String table = "(^.*@Table\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{29,})(\\\"\\s*\\))"; 			// length grater than 29 because the history table names have 2 more chars: "Z_"   
	private static String joinTable = "(^.*@JoinTable\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{29,})(\\\".*)"; 		// length grater than 29 because the history table names have 2 more chars: "Z_" 
	private static String joinColumn = "(^.*@JoinColumn\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\"\\s*\\))"; 
	private static String foreignKey = "(^.*@ForeignKey\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\".*)";
	private static String inverseForeignKey = "(^.*@ForeignKey.*\\s*inverseName\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\".*)";
	private static String index = "(^.*@Index\\s*\\(\\s*name\\s*=\\s*\\\")([0-9a-zA-Z_]{31,})(\\\"\\s*\\))";
	
	public static void main (String args[]) throws IOException {
		File dir = new File(javaPath);
		String[] entityFileNames = dir.list(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".java");
		    }
		});
		List<String> fileContent;
		String fileContentMerged;
		for (String entityFileName : entityFileNames) {
			fileContent = parseFile(javaPath + entityFileName);
			for (int z=0; z<fileContent.size(); z++) {
				fileContent.set(z,finder(fileContent.get(z),table,28));
				fileContent.set(z,finder(fileContent.get(z),joinTable,28));
				fileContent.set(z,finder(fileContent.get(z),joinColumn,30));
				fileContent.set(z,finder(fileContent.get(z),foreignKey,30));
				fileContent.set(z,finder(fileContent.get(z),inverseForeignKey,30));
				fileContent.set(z,finder(fileContent.get(z),index,30));
			}
			//scrivi il file
			System.out.println(javaPath + entityFileName + " --> " + outputPath + entityFileName);
			FileWriter writer = new FileWriter(new File (outputPath + entityFileName));
	        writer.write(StringUtils.join(fileContent,NL)+NL);
	        writer.close();
		}
		System.out.println("Patch completed.");
	}

	private static String finder(String source, String regExp, Integer maxLength) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(source);
		if (m.find()) {
			String originalName = m.group(2);
			String patchedName = patchName(originalName, maxLength);
			source = m.group(1) + patchedName + m.group(3);  
			System.out.println(m.group(2) + "(" + m.group(2).length() + " chars) --> " + patchedName);			
		}
		return source;
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
	
	private static String patchName(String originalName, Integer maxLength) {
		if (originalName != null) {
			while (originalName.length() > maxLength) {
				originalName = removeLetter(originalName);
			}
		}
		return originalName;
	}
	
	private static String[] charsDictionary = new String [] {"a","e","i","o","u","y","x","z","c"};
	
	private static String removeLetter(String s) {
		Boolean removed = false;
		Integer chCounter = 0;
		String ch = charsDictionary[0];
		while (!removed) {
			Integer lio = s.lastIndexOf(ch);
			if (lio != -1) {
				s = s.substring(0, lio) + s.substring(lio + 1);
				removed = true;
			} else {
				chCounter++;
				ch = charsDictionary[chCounter];
			}
		}
		return s;
	}
}
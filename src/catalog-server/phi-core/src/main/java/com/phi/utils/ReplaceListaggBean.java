package com.phi.utils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.phi.cs.repository.RepositoryManager;

public class ReplaceListaggBean {
	
	
	private static final String listaggSimpleRegEx = "LISTAGG\\s*\\(([^,]*?)\\)\\s*WITHIN\\s*GROUP\\s*\\(\\s*ORDER\\s*BY\\s*.*?\\)";
	private static final String listaggComplexRegEx ="LISTAGG\\s*\\(((?:.(?!WITHIN))*),\\s*('(?:.(?!WITHIN))*')\\)\\s*WITHIN\\s*GROUP\\s*\\(\\s*ORDER\\s*BY\\s*.*?\\)";
		
	private static final String WMCONCAT = "WM_CONCAT";
	private static final String REPLACE = "REPLACE";
	private static final String FAKESEPARATOR = "'§'";
	private static final String REALSEPARATOR = "'§,'";
	
	
	static public String replaceListagg(String oldQuery) {
		
		if ("false".equals(RepositoryManager.instance().getSeamProperty("useLISTAGG"))) {
			
			String newQuery = oldQuery.replace("\n", "\\n");		
					
			Pattern listaggSimplePattern = Pattern.compile(listaggSimpleRegEx, Pattern.CASE_INSENSITIVE);
			Pattern listaggComplexPattern = Pattern.compile(listaggComplexRegEx, Pattern.CASE_INSENSITIVE);
			
			Matcher matcherSimple = listaggSimplePattern.matcher(newQuery);
			
			while (matcherSimple.find()) {
				MatchResult result = matcherSimple.toMatchResult();	
				String listaggExpression = result.group(0);			
				String wmconcatExpression = "";
				
				wmconcatExpression = WMCONCAT + "(" + result.group(1) + ")";
							
				newQuery = newQuery.replace(listaggExpression, wmconcatExpression);	
				matcherSimple.reset(newQuery);
			}
							
			Matcher matcherComplex = listaggComplexPattern.matcher(newQuery);
			
			while (matcherComplex.find()) {
				MatchResult result = matcherComplex.toMatchResult();	
				String listaggExpression = result.group(0);			
				String wmconcatExpression = "";
				
				wmconcatExpression = REPLACE + "(";
					wmconcatExpression += REPLACE + "(";
						wmconcatExpression += WMCONCAT + "(" + result.group(1) + " || " + FAKESEPARATOR + ")";
					wmconcatExpression += ", " + REALSEPARATOR + ", " + result.group(2) + ")";
				wmconcatExpression += ", " + FAKESEPARATOR + ", '')";
				
				newQuery = newQuery.replace(listaggExpression, wmconcatExpression);	
				matcherComplex.reset(newQuery);
			}
		
			return newQuery.replace("\\n", "\n"); 	
		}
		else 
		{
			return oldQuery;
		}
	}

}
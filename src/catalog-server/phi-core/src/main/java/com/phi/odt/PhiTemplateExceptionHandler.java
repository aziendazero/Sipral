package com.phi.odt;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


public class PhiTemplateExceptionHandler implements TemplateExceptionHandler {
	
	/*
	 * SHOW_SYNTAX_ERRORS 		mostra un errore se si scrive un binding che non esiste, tipo Perzon.name o Person.mame (default true)
	 * SHOW_NOT_INJECTED_ERRORS	mostra un errore se il binding richiesto esiste ma è vuoto/non injettato (default false)
	*/
	private boolean SHOW_SYNTAX_ERRORS=false;
	private boolean SHOW_NOT_INJECTED_ERRORS=false;

	private static int ERROR_SYNTAX = 0;
	private static int ERROR_NOT_INJECTED = 1;
	
	
	public void handleTemplateException(TemplateException te, Environment env,
			java.io.Writer out) throws TemplateException {
		
		
		try {

			String binding = te.getFTLInstructionStack();
			int beginIndex = binding.indexOf("{") + 1;
			int endIndex = binding.indexOf("}");
			// distinguiamo se è un binding semplice tipo ${qualcosa.qualcos.altro} oppure se è una condizione/lista [#if qualcosa] / [#list qualcosa]
			boolean simpleVariabile = false;
			if (beginIndex > -1 && endIndex > beginIndex) {
				binding = binding.substring(beginIndex, endIndex);
				simpleVariabile = true;
			}

			if (!simpleVariabile) {

				Pattern ptrn = Pattern.compile("[\\w\\.*]+");
				Matcher mtchr = ptrn.matcher(binding);

				List<String> allMatches = new ArrayList<String>();
				while (mtchr.find()) {
					allMatches.add(mtchr.group());
				}

				binding = allMatches.get(1);

			}

			

			String[] parts = binding.split("\\.");

			Class<?> clazz = null;
			Class<?> prevClazz = null;
			clazz = searchClassWithName(parts[0]);

			if (clazz != null) {
				prevClazz = clazz;
			} else {
				//showError(binding,parts[0]+"!2!",ERROR_SYNTAX,out);
				return;
			}

			if (prevClazz != null) {
				if (parts.length > 1) {

					for (int i = 1; i < parts.length; i++) {

						Field[] fieldList = prevClazz.getDeclaredFields();
						boolean fieldFound = false;
						for (Field f : fieldList) {

							if (f.getName().equalsIgnoreCase(parts[i])) {
								fieldFound = true;
								// Type type = f.getGenericType();
								if (f != null) {
									//showError(binding,parts[0]+"!3!",ERROR_NOT_INJECTED,out); 
									Class<?> theclass = f.getType();
									prevClazz = theclass; 
									break;
								}
							}
						}

						if (fieldFound) {
							showError(binding,parts[0],ERROR_NOT_INJECTED,out); 
							return;
						}else{
							showError(binding,parts[0],ERROR_SYNTAX,out);
							return;
						}
					}

				} else {
					showError(binding,parts[0],ERROR_NOT_INJECTED,out);
				}
			}

		} catch (IOException e) {
			throw new TemplateException("Failed to print error message. Cause: " + e, env);
		}
	}

	private Class<?> searchClassWithName(String className) {

		Class<?> clazz = null;

		for (Package pkg : Package.getPackages()) {
			if (pkg.getName().startsWith("com.phi.entities.")) {

				try {
					clazz = Class.forName(pkg.getName() + "." + className);
					return clazz;
				} catch (Exception e) {

				}

			}
		}
		return null;
	}
	
	private void showError(String fullBinding, String part, int errorType, Writer writer) throws IOException{

		if(errorType==ERROR_NOT_INJECTED){
			String error ="['" + fullBinding + "', "+ "l'oggetto '" + part + "' ";
			if(errorType==ERROR_NOT_INJECTED) error+="non è stato selezionato";
			error+="]";
			if(SHOW_NOT_INJECTED_ERRORS) writer.write(error);
		}

		
		if(errorType==ERROR_SYNTAX){
			String error ="['" + fullBinding + "', "+ "l'oggetto '" + part + "' ";
			if(errorType==ERROR_SYNTAX) error+="è scritto male";
			error+="]";
			if(SHOW_SYNTAX_ERRORS) writer.write(error);
		}

		
	}

}
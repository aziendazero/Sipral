package com.phi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PhiLogParser {
	
	
	private static String logPath = "I:/tmp/jb1/";
	private static String defaultLogfileName = "server.log.2014-08-05";
	private static List<String> logfileToBeParsedNames = null;
	private static String configFileName = "config.txt";
	
	private static  String defaultPattern = "yyyy-MM-dd HH:mm:ss,SS";
	private static Log log = startLog();
	private static Date initDate = null;
	private static long sessionTimeOutms=3600000;
	
	private static Date lineDate = null;
	private static String prvLine = "";
	private static List<String> lines = new ArrayList<String>(); 
	
	//User {  SessionsId {Inizio,used[], Fine} }
	private static HashMap<String,HashMap<String,Object[]>> userSessionDate = new HashMap<String,HashMap<String,Object[]>>();
	private static HashMap<String,String> sessionUser = new HashMap<String,String>();
	private static HashMap<String,List<String>> userSessions = new HashMap<String,List<String>>();
	private static HashMap<String,String> userCurrSession = new HashMap<String,String>();
	
	private static HashMap<String,List<String>> sessionCids = new HashMap<String,List<String>>();
	private static HashMap<String,Date> unassignedSessions = new HashMap<String,Date>();
	private static HashMap<String,List<Date>> startedSessions = new HashMap<String,List<Date>>();
	private static HashMap<String,List<Date>> killedExpiredDestroyedSessions = new HashMap<String,List<Date>>();
	private static List<String> loggedOutSession = new ArrayList<String>();
	private static List<String> activeSession = new ArrayList<String>();
	//logoff?? sessionKillers?
	
	private HashMap<String, Object> configuration = new HashMap<String,Object>();
	
	
	public static void main (String args[]) {
		
		
		log.info("main of "+new Object() { }.getClass().getEnclosingClass()+ " Started");
		
		if (args!= null && args.length >0) {
			logfileToBeParsedNames = Arrays.asList(args);
		}
		
		if (logfileToBeParsedNames == null || logfileToBeParsedNames.isEmpty()) {
			logfileToBeParsedNames = new ArrayList<String>();
			logfileToBeParsedNames.add(logPath+defaultLogfileName);
		}
		
		List<String> fileLines = new ArrayList<String>();
		for (String currentFile : logfileToBeParsedNames) {
			 fileLines.addAll(parseFile(currentFile));
		}
		
		lines = fileLines;
		calculateSessions(fileLines);
		
		calculateClinicalDiaryInterval(fileLines);
		
	}
	
	private static HashMap <String,String> userStatus = new HashMap<String, String>();
	private static HashMap <String,String> userCid    = new HashMap<String, String>();
	private static HashMap <String,String> userEnc    = new HashMap<String, String>();
	private static List<String> corretStateSequence = Arrays.asList("CLEAN","PAT","ENC","START", /*"SEARCH" "LIST"*/  "FORM", "LINK", "CREATE", "HOME");
	
	private static void calculateClinicalDiaryInterval (List<String> fileLines) {
		
		/**
		 * 
		 * 
			tripletta
			Patient injected/ejected: cleaning conversation [CLEAN] 
			Object injected: Patient, id:          [PAT]
			Object injected: PatientEncounter, id: [ENC]
			
			Starting process: MOD_Medical_Diary/customer_VCO/PROCESSES/Medical_diary [START]
			**Starting subproc: MOD_Patients/CORE/PROCESSES/Search_Patients  --> patient+tripletta [SEARCH]
			
			loading form /MOD_Annotations/cutomer_VCO/FORMS/f_clinical_diary_list.xhtml [LIST]
			loading form /MOD_Annotations/CORE/FORMS/f_clinical_diary_create.xhtml    [FORM]
			
			Linked class com.phi.entities.act.PatientEncounter [internalId=3319514] to relation patientEncounter of class com.phi.entities.act.ClinicalDiary [internalId=0]  [LINK]
			Created object of Class com.phi.entities.act.ClinicalDiary with internal_id: 3327551 in: 2 ms  [CREATE]
			going to home directly.  [HOME]
			
			
			[CLEAN] [PAT] [ENC] [START] [SEARCH] [LIST]  [FORM] [LINK] [CREATE] [HOME]
			
			
		 * 
		 * 
		 */
		
		
		
		SimpleDateFormat sdf =new SimpleDateFormat(defaultPattern); 
		int count=0;
		
		for (String line : fileLines) {
			count++;
			
			if (skipLine(line))
				continue;
			
			String ts=line.substring(0,23);
			Date lineTimestamp;
			try {
				lineTimestamp = sdf.parse(ts);
			} catch (ParseException e) {
				log.info("ERROR parsing date in line: "+line);
				continue;
			}
			
			
			String[] cidUser = getCidAndUser(line);
			if (cidUser == null) {
				log.info("skipped line withoud cid/user: " +line);
				continue;
			}
			
			
			String cid=cidUser[0];
			String user=cidUser[1];
			String prvCid    = userCid.get(user);
			
			
			
			//State machine:  triplette - START 
			
			if(line.contains("Patient injected/ejected: cleaning conversation")) {
				String status = "CLEAN";
				
			}
			
			if (line.contains("Object injected: Patient, id")) {
				String status = "PAT";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}	
			
			if (line.contains("Object injected: PatientEncounter, id")) {
				String status = "ENC";
				String encId = line.substring(line.indexOf("id:")+3,line.length());
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			if (line.contains("MOD_Medical_Diary/customer_VCO/PROCESSES/Medical_diary")) {
				String status = "START";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
//			if (line.contains("MOD_Patients/CORE/PROCESSES/Search_Patients")) {
//				String status = "SEARCH";
//				String expectedPrv= corretStateSequence.get(corretStateSequence.indexOf(status)-1);
//			}
			
			if (line.contains("MOD_Annotations/cutomer_VCO/FORMS/f_clinical_diary_list.xhtml")) {
				String status = "LIST";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			if (line.contains("MOD_Annotations/CORE/FORMS/f_clinical_diary_create.xhtml")) {
				String status = "FORM";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			if (line.contains("to relation patientEncounter of class com.phi.entities.act.ClinicalDiary")) {
				
				String encId = line.substring(line.indexOf("internalId=")+10, line.indexOf("]") );
				String status = "LINK";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			if (line.contains("Created object of Class com.phi.entities.act.ClinicalDiary")) {
				String status = "CREATE";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			if (line.contains("going to home directly.")) {
				String status = "HOME";
				if (!checkSequence(user,status)) {
					log.info(line);
				}
				userStatus.put(user,status);
				continue;
			}
			
			
			
		}
	}
	
	private static boolean checkSequence(String user, String status) {
		String prvStatus = userStatus.get(user);
		String expectedPrv= corretStateSequence.get(corretStateSequence.indexOf(status)-1);
		if (!prvStatus.equals(expectedPrv)) {
			return false;
		}
		return true;
	}
	
	private static String[] getCidAndUser(String line) {
		
		String exp_cidUser = "\\[cid=(\\d+)\\|(\\w+)\\]";  //\\s+\\]
		Pattern p_cidUser = Pattern.compile(exp_cidUser);
		if (!p_cidUser.matcher(line).find() && !line.contains("ession")) {
			log.info(" CHECK LINE 1 ------>  "+line);
			return null;
		}
		
		Matcher m = p_cidUser.matcher(line);
		if (m.find()) {
			String cid = m.group(1);
			String user = m.group(2);
			return new String[]{cid,user};
		}
		
		return null;
	}
	
	private static boolean skipLine(String line) {
		
		String exp_cidUser = "\\[cid=(\\d+)\\|(\\w+)\\]";  //\\s+\\]
		String exp_date ="^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		String exp_stack="^\\tat |Caused by: ";
		
		Pattern p_cidUser = Pattern.compile(exp_cidUser);
		Pattern p_date = Pattern.compile(exp_date); 
		Pattern p_stack = Pattern.compile(exp_stack);

		List<String> text_to_beSkipped = Arrays.asList( 
				"Session.instance().invalidate()",
				"Can't save the object because is a DUPLICATE",
				"Please end the HttpSession via ",
				"Exception: /home.xhtml:",
				" OAUTH marshaling failure",
				"Error Going to home",
				"com.phi.his.bl.SDLocBLBean",
				"com.phi.pk.PdfExporter",
				"Unable to find component with ID ",
				"so THE PROCESS won't go ahead",
				"[com.phi.his.bl.LisConnectionManager] Inserted into DNWEB request",
				"com.phi.entities.actions.LabRequestGroupAction] Created request with ",
				"jboss-el.jar not patched!",
				"Target component for id ",
				"Error setCodeValue: no entity in conversation",
				"com.phi.bean.ErogationJob",
				"WARNING: FacesMessage(s) have been en",
				"handled and logged exception",
				"[facelets.viewhandler] Error Rendering View",
				"JBossManagedConnectionPool",
				"Error to calculate bmi data",
				"[STDOUT] Rule fired",
				"Error flushing after super state",
				"@Destroy method: rimPdm2CA",
				"Error flushing after super state: null",
				"AbstractFlushingEventListener",
				"org.apache.catalina.session.ManagerBase] Session not found",
				"org.apache.catalina.core.ContainerBase] Session event listener threw exce",
				"arjLoggerI18N",
				"JSF1054"
				);
		
		String exp_skip_validator = "severity=.*summary=.*detail=";
		Pattern p_skip_validator = Pattern.compile(exp_skip_validator);
		
		//Skip line so short which doesn't have a date in front. 
		if (line==null || line.length() < 23) {
			//log.info("line "+count+" skipped:" +line);
			return true;
		}
		
		//Skip stack trace lines
		if (p_stack.matcher(line).find()) {
			//log.info("stack line "+count+" skipped:" +line);
			return true;
		}
		
		//Skip line containing specific text
		boolean skip = false;
		for (String skipText : text_to_beSkipped) {
			if (line.contains(skipText)) {
				//log.info("stack line "+count+" skipped:" +line);
				skip=true;
				return true;
			}
		}
		if (skip) {
			return true;
		}
		
		//skip validators lines
		if (p_skip_validator.matcher(line).find()) {
			//log.info("skipped validator "+line);
			return true;
		}
		
		//get timestamp (skip lines which does not begin with date)
		
		//skip line which does not contains date. 
		if (!p_date.matcher(line).find()) {
			//log.info("----------[line "+count+"]" +line);
			return true;
		}
		
		
		return false;
	}
	
	
	public static void calculateSessions( List<String> fileLines)  {
		//date Format: 2014-08-28 23:49:27,897
		
		SimpleDateFormat sdf =new SimpleDateFormat(defaultPattern); 
		
		int count=0;
		
		for (String line : fileLines) {
			count++;
			
			if (skipLine(line))
				continue;
			
			String ts=line.substring(0,23);
			Date lineTimestamp;
			try {
				lineTimestamp = sdf.parse(ts);
			} catch (ParseException e) {
				log.info("ERROR parsing date in line: "+line);
				continue;
			}
			
			if (initDate == null) {
				initDate=lineTimestamp;
			}
			
			//LOGIC FOR SESSION
			//NOTE: all line here are already filtered.
			//line must be only with [cid= or containing word session.
			
			String exp_cidUser = "\\[cid=(\\d+)\\|(\\w+)\\]";  //\\s+\\]
			Pattern p_cidUser = Pattern.compile(exp_cidUser);
			if (!p_cidUser.matcher(line).find() && !line.contains("ession")) {
				log.info(" CHECK LINE 1 ------>  "+line);
			}
			
			//==========================================
			//   line containing cid=, ession or both
			//==========================================
			
			
			//====================================
			//   line containing cid=
			//====================================
			Matcher m = p_cidUser.matcher(line);
			if (m.find()) {
				String cid = m.group(1);
				String user = m.group(2);
				
				if (line.contains("ession") && line.contains("Previous Session has been killed")){
					//killed previous user session
					String session = line.substring(line.indexOf("session id = ")+13,line.indexOf("session id = ")+65);
					updateMap(user, session, cid, lineTimestamp, "killed", line, count-1);
					continue;
				}
				
				if (line.contains("ession") && line.contains("Authenticated")){
					//logged user in a new session
					String session = line.substring(line.indexOf("http session")+13,line.indexOf("http session")+65);
					updateMap(user, session, cid, lineTimestamp, "auth", line, count-1);
					continue;
				}
				
				updateMap(user, null, cid, lineTimestamp, "operation", line, count-1);
				continue;
				//log.info(">"+cid + " -- "+user);
					
				
			}
			
			//==========================================
			// line contains word session but not cid=
			//==========================================2
			else {
				

				String session = line.substring(line.indexOf("session id = ")+13,line.indexOf("session id = ")+65);

				if (line.contains("created")) {
					updateMap(null, session, null, lineTimestamp, "created2", line, count-1);
					continue;
				}
				
				if (line.contains("destroyed")) {
					updateMap(null, session, null, lineTimestamp, "killed2", line, count-1);
					continue;
				}
				
				
				if (line.contains("expired")) {
					String user = (line.split("\\[")[2]).split("\\]")[0];
					updateMap(user, null, null, lineTimestamp, "killed3", line, count-1);
					continue;
				}
				
				log.info("CHECK LINE 2: "+line);
			}
			
			log.info(" CHECK LINE 3 ------>  "+line);
			
		}
		
	}
	
	
	private static void updateMap(String user, String session, String cid, Date date, String lineType, String line, int lineNum) {
		lineDate = date;
		String warn = " [Short Session duration !!] ";
		boolean showWarn = false;
		//Session: created, destroyed, killed
		if (session == null) {
			
			if (line.contains("] Logged out")) {
				String sess= userCurrSession.get(user);
				loggedOutSession.add(sess);
				userCurrSession.remove(user);
				log.info("logged out user "+user+" from session "+sess );
			}
			//look if 
			//log.info("null sess");
			//log.info("ToDo            "+line); // <<<<<<<<<<<<<<<<<<<<<<<--------------------------------
			
		}
		else {
			
			//session is is present in line.
			if (lineType.contains("killed")) {
				if (!startedSessions.containsKey(session) && date.getTime()-initDate.getTime() < sessionTimeOutms ) {
					//termine di una sessione, assente in startedSession, ma all'inizio del file.
					log.info("FILE START: killed session "+session+ (user == null? "" : " of user "+user+ "."));
					ArrayList<Date> l = (new ArrayList<Date>());
					l.add(date);
					killedExpiredDestroyedSessions.put(session,l);
					
					if (activeSession.contains(session)) { activeSession.remove(session); }
					return;
				}
				
				if (!startedSessions.containsKey(session)){
					if (!line.contains("Previous Session has been killed")) {
						log.info("-->"+ lines.get(lineNum-1));
						log.info("KILLED UNKNOW SESSION! "+session);
					}
					
					ArrayList<Date> l = (new ArrayList<Date>());
					l.add(date);
					killedExpiredDestroyedSessions.put(session,l);
					if (activeSession.contains(session)) { activeSession.remove(session); }
					return;
				}
				
				//startedSession contains session..
				
				if (killedExpiredDestroyedSessions.containsKey(session)) {
					//session was already killed before!!
					List<Date> startedDates = startedSessions.get(session);
					Long sessionDuration =  (date.getTime() - startedDates.get(startedDates.size()-1).getTime())/1000; //last start
					
					//Long difference =  (date.getTime() - killedDates.get(killedDates.size()-1).getTime())/1000;
					if (loggedOutSession.contains(session)) {
						loggedOutSession.remove(session);
						log.info("Killed Session properly logged out "+session+ " ["+sessionUser.get(session)+"] Duration: "+sessionDuration );
					}
					else {
						if (sessionUser.get(session) == null) {
							//killed session, started, but never logged.
							log.info("Killed never used Session "+session+ " Duration: "+sessionDuration+ (sessionDuration < 3600 ? warn : "") );
						}
						else {
							log.info("Killed Session "+session+ " ["+sessionUser.get(session)+"] Duration: "+sessionDuration+ (sessionDuration < 3600 ? warn : "") );
						}
					}
					
					List<Date> killedDates = killedExpiredDestroyedSessions.get(session);
					killedDates.add(date);
					killedExpiredDestroyedSessions.put(session,killedDates);
					if (activeSession.contains(session)) { activeSession.remove(session); }
					return;
				}
				else {
					//expired/killed session which was properly started, and never killed before.
					
					List<Date> dlist = new ArrayList<Date>();
					dlist.add(date);
					killedExpiredDestroyedSessions.put(session,dlist);
					activeSession.remove(session);
					
					Long sessionDuration =  (date.getTime() - startedSessions.get(session).get(0).getTime())/1000; //last start
					if (loggedOutSession.contains(session) ) {
						loggedOutSession.remove(session);
						log.info("Expired loggedout session "+session+ " ["+sessionUser.get(session)+"] duration: "+sessionDuration +" s " ); 
					}
					else {
						if (sessionDuration < 3600) {
							
							log.info("---->> Expired session "+session+ " ["+sessionUser.get(session)+"] duration: "+sessionDuration +" s \n" +lines.get(lineNum-1)+"\n");
							
						}
						else {
							//Expired session
							if (sessionUser.get(session) == null) {
								log.info("Expired never used session "+session+ " duration: "+sessionDuration +" s " );
							}
							else {
								log.info("Expired session "+session+ " ["+sessionUser.get(session)+"] duration: "+sessionDuration +" s " );
							}
						}
					}
					if (activeSession.contains(session)) { activeSession.remove(session); }
					return;
				}
			}
			else if (lineType.contains("created")) {
//				if (killedExpiredDestroyedSessions.containsKey(session)) {
//					log.info("Session "+session+ " was killed and now created again ["+sessionUser.get(session)+"]");
//				}
				
				if (startedSessions.containsKey(session)) {
					List<Date> dlist = startedSessions.get(session);
					Long difference =  (date.getTime() - dlist.get(dlist.size()-1).getTime())/1000;
					log.info("Session "+session+ " ["+sessionUser.get(session)+"] created. It was already created "+dlist.size()+" times, last one "+ difference + " seconds ago.");
					dlist.add(date);
					startedSessions.put(session,dlist);
					if (!activeSession.contains(session)) { activeSession.add(session); }
					return;
					//TODO: check on session history
				}
				else {
					log.info("Session "+session+" created (never seeen before)");
					ArrayList<Date> l = (new ArrayList<Date>());
					l.add(date);
					startedSessions.put(session,l);
					if (!activeSession.contains(session)) { activeSession.add(session); }
					return;
				}

			}
			
			else if (lineType.equals("auth")) {
				if (sessionUser.containsKey(session)){
					if(!sessionUser.get(session).equals(user)) {
						log.info("session "+session + " was of another user! Actual:"+user+ " previous: "+sessionUser.get(session));
					}
				}
				sessionUser.put(session,user);
				List<String> sessions = null;
				if (userSessions.containsKey(user)) {
					sessions = userSessions.get(user);
				}
				else {
					sessions = new ArrayList<String>();
				}
				
				sessions.add(session);
				userSessions.put(user,sessions);
				userCurrSession.put(user,session);
				return;
			}
			
			//add sessInfo...
			/*
			HashMap<String,Object[]> sessInfo = null;
			if (!userSessionDate.keySet().contains(user)) {
				sessInfo = new HashMap<String,Object[]>();
			}
			else {
				sessInfo = userSessionDate.get(user);
			}
			*/
			log.info("????");
			
		}
		
	}
	
	private static HashMap getConfig() {
		File configFile = new File(configFileName);
		HashMap<String,Object> conf = new HashMap<String,Object>();
		if (configFile.exists()) {
			//parse configuration.
		}
		else {
			//set default configurations
		}
		return conf;
		
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
	
	private static Log startLog() {
		
		Log log = new Log();
		return log;
	}
	
	private static class Log {
		
		private static String pattern ="";
		private static SimpleDateFormat sdf ;
		
		public  Log(String pat) {
			pattern = defaultPattern;
			if (pat != null && pat.isEmpty()) 
				pattern = pat;
			sdf = new SimpleDateFormat(pattern);
		}
		
		public Log() {
			new Log(defaultPattern);
		}
		
		public static void info(String s) {
			Date d = lineDate == null ? new Date() : lineDate;  
			//System.out.println(String.format("%23s",sdf.format(new Date()))+ " | "+activeSession.size()+ " | " +s);
			System.out.println(String.format("%23s",sdf.format(d))+ " | "+String.format("%3s",activeSession.size())+ " | " +s);

		}
		
		public static void infoClean(String s) {
			System.out.println(s);
		}
	}
}

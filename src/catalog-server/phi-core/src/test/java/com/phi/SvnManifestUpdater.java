package com.phi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


 /**
  * 
  * This class is used to automatically retrieves svn information from local project files, and update manifest.
  * main is lauched by ANT tasks.
  * 
  * This class uses SVN kit library and some dependecies:
  *  - svnkit-1.7.8.jar
  *  - sqljet-1.1.10.jar
  *  - sequence-library-1.0.3.jar
  *  - antlr-4.4-complete.jar
  *  
  *  SVN kit source code and dependency libraries can be downloaded at www.svnkit.com  *  
  *  
  * @author Bragagna
  *
  */

public class SvnManifestUpdater {
	public static void main (String[] args) {
		
		String vendorString = "Implementation-Vendor: TBS Group SpA - SVN PHI repository revision ";
		
		String projectPath="";
		try {
			projectPath = (new File( "." )).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (projectPath == null || projectPath.isEmpty())
			return;
		
		System.out.println("current="+projectPath);
		String manifestUrl= projectPath+"/src/main/resources/META-INF/MANIFEST.MF";
		Long currentRevNum=getWorkingCopyRevisionNumber(projectPath);
		
		replaceLinesMatching(new File(manifestUrl), "Implementation-Vendor:",  vendorString+ currentRevNum );
		
	}
	
	
	private static long getHeadRevNum (String svnPath, String svnUsername, String svnPassword) {
		Long revNum=0L;
		try {
			SVNRepository repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( svnPath ) );
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( svnUsername, svnPassword );
			repository.setAuthenticationManager( authManager );
			SVNDirEntry entry = repository.info(".", -1);
			revNum=entry.getRevision();
		}
		catch (SVNException e) {
			System.out.println("Error getting revision number for "+svnPath );
			e.printStackTrace();
		}
		return revNum;
	}
	
	
	
	public static long getWorkingCopyRevisionNumber (String localpath)  {
		
		File f = new File(localpath);
		if (!f.exists()) {
			System.out.println("Error gettin working copy revision number, wrong path: "+localpath);
			return -1L;
		}
		
		SVNStatus status = getWorkingStatus (localpath);
		
		if (status == null) {
			System.out.println("Error gettin working copy revision number, null status: "+localpath);
			return -1L;
		}
		Long revNum = status.getCommittedRevision().getNumber();
		return revNum;
	}
	
	
	
	public static SVNStatus getWorkingStatus (String localpath)  {
		
		if (localpath == null || localpath.isEmpty()) {
			System.out.println("Error getting svn status for null or empty localpath");
			return null;
		}
		
		SVNStatusClient statusClient = SVNClientManager.newInstance().getStatusClient();
		File f = new File(localpath);
		if (!f.exists()) {
			System.out.println("Error getting svn status, wrong path: "+localpath);
			return null;
		}
		try {
			SVNStatus status = statusClient.doStatus(  f, false);
			return status;
//			Long revision =  statusClient.doStatus( 
//			f,
//			SVNRevision.HEAD /* status against latest revision of repository */, 
//			SVNDepth.UNKNOWN /* use depth of the working copy 
//			                   (infinity is default) */, 
//			true  /* remote */, 
//			true  /* report both modified and unmodified files */, 
//			false /* skip ignored files */, 
//			false /* obsolete */, 
//			/* callback to collect statuses */ 
//			new ISVNStatusHandler() { 
//				public void handleStatus(SVNStatus status) throws SVNException { 
//					SVNStatusType remoteContents = status.getRemoteContentsStatus(); 
//					if (remoteContents != SVNStatusType.STATUS_NONE && 
//							remoteContents != SVNStatusType.STATUS_NORMAL) { 
//						// file contents or one of the directory 
//						// children is modified remotely 
//						System.out.print("* "); 
//					} 
//					System.out.println(status.getFile()); 
//				}
//			},
//			null /* do not filter by changelist name */ 
//			); 
			
		} catch (SVNException e) {
			System.out.println("Error getting svn status for path: "+localpath);
			e.printStackTrace();
		}
		return null;
	}
	
	public static void replaceLinesMatching(File f, String patternLineMatching,	String replaceLine) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			String newtext = "";
			while ((line = reader.readLine()) != null) {
				if (line.contains(patternLineMatching)) {
					System.out.println("Replacing \""+line +"\" with \""+ replaceLine+"\"" );
					newtext += replaceLine;
				} else {
					newtext += line;
				}
				newtext+= "\r\n";
			}
			reader.close();

			FileWriter writer = new FileWriter(f);
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}

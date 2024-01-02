package com.phi.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class ChromeFrameInstaller extends JApplet 
implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4752349921713911530L;
	
	private int VERSION = 105;
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("ActionEvent ChromeFrameInstaller versione"+VERSION);
	
	}
	
	public static void main(String[] args) {
		System.out.println("main start...");
		//updateCf();
	}
	
	private void updateCf() {
		
		//to enable system property and file system access, the applet need to be signed
		//http://docs.oracle.com/javase/tutorial/deployment/applet/security.html
		
		
		/*
		 * HOW TO SIGN applet:
		 * 
		 * 0) generate a key couple or used a key store in a keystore. 
		 *    Keystore and certificate can be produced/managed by software like KeyStore Explorer or Portecle
		 *    
		 * 1) Generate a not signed applet: right click on the class/folder -> export -> jar  
		 *    
		 * 2) Use the key sign the applet. example:  
		 * 		keystore file:  imkeystore (the name of the file representing the keystore)
		 * 		keystore password:  13245678  (keystore is normally password protected)
		 * 		key password: 12345678  (each key is protected by a password to be used)
		 *      key name:  imkey (the name of the key in the store, you can have multiple key stored)
		 *    
		 *    use the jarsigner java jdk utility to sign the jar: put the keystore and the jar to be signed (in this example ChromeFrameInstaller.jar ) in a folder enter in that folder with command line:
		 *    
		 *    command example:
		 *    
		 *    C:\Users\510885\Desktop>"C:\Program Files\Java\jdk1.7.0\bin\jarsigner" -keystore imkeystore -storepass 12345678 -keypass 12345678 -signedjar SignedChromeFrameInstaller.jar ChromeFrameInstaller.jar imkey
		 * 
		 */
		
		String userFolderPath =  System.getProperty("user.home");
		//String userFolderPath = "C:\\Users\\510885";
		
		if (userFolderPath == null || userFolderPath.isEmpty()) {
			System.out.println("unable to find user.home System property");
			return;
		}
		String getUrl = "";
		String cfExecutableName = "GoogleChromeframeStandaloneEnterprise.msi";
		
		if (appUrl == null || appUrl.isEmpty()) {
			System.out.println("missing application url.");
			return;
		}
		else {
			System.out.println("appUrl ="+appUrl);
			//FIXME: change url if needed.
			getUrl = appUrl.replace("login.seam",cfExecutableName);  //place .msi executable into same login.xhtml path.
		}
		
		//String getUrl = "http://localhost:8080/SSA/";
		
		
		
		File userFolder = new File(userFolderPath);
		if (!userFolder.exists()) {
			System.out.println("unable to access to folder"+userFolder);
		}
		
		String installerPath = userFolderPath+File.separator+cfExecutableName;
		File installerFile = new File(installerPath);  
		URL installerUrl = null;
		try {
			installerUrl = new URL(getUrl);
		} catch (MalformedURLException e1) {
			System.out.println("Malformed url: "+installerUrl);
			return;
		}
		
		
		try {
			System.out.println("Getting installer from "+installerUrl+" writing file to: "+installerPath);
			//FileUtils.copyURLToFile(installerUrl, installerFile);
			URLConnection connection = installerUrl.openConnection();
			
			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[4096];
			int n = - 1;

			OutputStream output = new FileOutputStream( installerFile );
			while ( (n = input.read(buffer)) != -1) {
			    if (n > 0) {
			        output.write(buffer, 0, n);
			    }
			}
			output.close();
			
			
		} catch (IOException e) {
			System.out.println("error getting/writing installer. Exception:"+e);
			e.printStackTrace();
			return;
		}
		
		if (!installerFile.exists()) {
			System.out.println("Missing installer.");
			return;
		}
		
		System.out.println("Executing Chrome Frame Installer from "+installerPath);
		
		//String command = "runas /user:administrator msiexec /i "+installerPath+" /qb";
		String command = "msiexec /i "+installerPath+" /qb";
		command = command.replace("Users","Utenti");  //strange: with win8, the folder looks like C:\Users, but the execution works onli with C:\Utenti
		Process process;
		try {
			System.out.println("Executing command: "+command);
			process = Runtime.getRuntime().exec(command);
			
			System.out.println("Waiting for execution completion");
			process.waitFor();
		} catch (IOException e) {
			System.out.println("IoException: "+e);
			return;
		} catch (InterruptedException e) {
			System.out.println("InterruptedException: "+e);
			return;
		}
		
		System.out.println("Installation completed");
		
		
	}
	
	private String appUrl;
	
    public void init() {
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
    	
    	appUrl = getParameter("appUrl"); 
    	
        try { 
        	SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	System.out.println("Init version: "+VERSION);
                    createGUI();
                    updateCf();
                }
            });
        } catch (Exception e) {
            System.err.println("Init fail: "+e.getMessage()+"\n");
            e.printStackTrace();
        }
    }

    private void createGUI() {
    	
    	System.out.println("Creating gui");
//    	//Lay out the buttons from left to right.
//    	JPanel buttonPane = new JPanel();
//    	buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
//    	buttonPane.setBackground(Color.WHITE);
//    	
//    	
//    	JButton button = new JButton("Upgrade");
//
//    	button.setActionCommand("Upgrade");
//        button.addActionListener(this);
//        buttonPane.add(button);
//        System.out.println("Button added to pane.");
//    	
//    	//Put everything together, using the content pane's BorderLayout.
//        Container contentPane = getContentPane();
//        contentPane.add(buttonPane, BorderLayout.BEFORE_FIRST_LINE);
//        System.out.println("Created.");
//        JFrame.setDefaultLookAndFeelDecorated(true);
        System.out.println("Gui creation completed.");
     }

    public void destroy() {
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    destroyGUI();
                }
            });
        } catch (Exception e) { }
    }
    
    public void stop() {
    	System.out.println("Applet stopped!");
    }
     
    private void destroyGUI() {
    	System.out.println("Applet destroyed!");
    }

    
    
   
    
}

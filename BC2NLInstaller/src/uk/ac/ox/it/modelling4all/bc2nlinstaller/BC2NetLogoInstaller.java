/**
 * 
 */
package uk.ac.ox.it.modelling4all.bc2nlinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;

/**
 * This class launches BC2NetLogo
 * 
 * @author Ken Kahn
 *
 */
public class BC2NetLogoInstaller {

//    private static final int CURRENT_VERSION = 7;
    private static final Boolean epidemicGameMaker = false; // generate two versions of the JAR with each value
    public static final String INSTALLATION_PROBLEM_PLEASE_REPORT_THIS_TO_INFO_MODELLING4ALL_ORG = 
	    "Installation is missing required files. Sorry. Please report this to info@modelling4all.org";
    public static final String ERROR_PLEASE_REPORT_THIS_TO_INFO_MODELLING4ALL_ORG =
	    "Error encountered. Sorry. Please report this to info@modelling4all.org";
    public static JFrame frame = null;
    private static String bc2NetLogoFolder;

    public static void main(String[] args) {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    Utilities.reportException(e);
	}
	frame = new JFrame("Start BC2NetLogo");
	Settings.initialise();
	bc2NetLogoFolder = Settings.getPreference("BC2NetLogoDirectory", ".");
	// while debugging
//	bc2NetLogoFolder = "C:\\bin\\BC2NetLogo";
	installAndLaunch();
    }

    protected static void installAndLaunch() {
	String bc2NetLogoJarFile = bc2NetLogoFolder + "/support.jar";
	boolean bc2NetLogoJarFileExists = new File(bc2NetLogoJarFile).exists();
	if (!bc2NetLogoJarFileExists) {
	    bc2NetLogoFolder = "."; // assume start BC2NetLogo jar is in the same folder
	    bc2NetLogoJarFile = bc2NetLogoFolder + "/support.jar";
	    bc2NetLogoJarFileExists = new File(bc2NetLogoJarFile).exists();
	}
	if (!bc2NetLogoJarFileExists) {
	    // may have been launched from a command line where the current directory is not where the JAR running this is
	    String path = BC2NetLogoInstaller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    int lastSlash = path.lastIndexOf('/');
	    if (lastSlash >= 0) {
		bc2NetLogoFolder = path.substring(0, lastSlash);
		bc2NetLogoJarFile = bc2NetLogoFolder + "/support.jar";
		bc2NetLogoJarFileExists = new File(bc2NetLogoJarFile).exists();
	    }
	}
	String jreFolderFile = bc2NetLogoFolder + "/jre";
	boolean jreFolderExists = new File(jreFolderFile).exists();
	boolean previouslyInstalled = true;
	if (!bc2NetLogoJarFileExists || !jreFolderExists) {
//	    previouslyInstalled = jreFolderExists;
//	    if (previouslyInstalled) {
//		installUpdates();
//	    }
//	    if (!previouslyInstalled || !bc2NetLogoJarFileExists) {
//		// if update was installed test again and if still a problem do full install
//		if (Settings.getPreference("BC2NetLogoDirectory", null) == null) {
//		    bc2NetLogoFolder = Utilities.chooseFolder(frame);
//		}
//		if (bc2NetLogoFolder == null) {
//		    // user cancelled download
//		    System.exit(0);
//		}
//		installBC2NetLogo();
//		bc2NetLogoJarFile = bc2NetLogoFolder + "/support.jar";
//		bc2NetLogoJarFileExists = new File(bc2NetLogoJarFile).exists();
//		if (!bc2NetLogoJarFileExists) {
		    JOptionPane.showMessageDialog(frame, INSTALLATION_PROBLEM_PLEASE_REPORT_THIS_TO_INFO_MODELLING4ALL_ORG + 
			                                 " Could not find " + new File(bc2NetLogoJarFile).getAbsolutePath() + " and/or " + 
			                                                      new File(jreFolderFile).getAbsolutePath());
		    System.exit(0);
//		}
//	    }
//	} else {
//	    installUpdates();
	}
	// https to avoid ISP JavaScript injection (e.g. by T-Mobile) and https://m.modelling4all.org doesn't work
	String defaultServer = "https://m4a-gae.appspot.com";
	String server = Settings.getPreference("server", defaultServer);
	if (!previouslyInstalled) {
	    try {
		// store it for BC2NetLogo to read
		if (Settings.getPreference("server", null) == null) {
		    Settings.putPreference("server", defaultServer);
		}
		if (Settings.getPreference("sessionGuid", null) == null) {
		    Settings.putPreference("sessionGuid", "new");
		}
		if (Settings.getPreference("userGuid", null) == null) {
		    Settings.putPreference("userGuid", "new");
		}
	    } catch (Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		JOptionPane.showMessageDialog(frame, "Attempting to update Java preferences caused the following exception: " + stringWriter.toString());
	    };
	}
	// don't bother the user about this
	// TODO: instead use a file in installation folder 
//	if (Settings.getPreference("userGuid", null) == null) {
//	    JOptionPane.showMessageDialog(frame, "Attempting to update Java preferences so that subsequent launches of BC2NetLogo recall the current session and user ids failed. Please report this to info@modelling4all.org. To work around this problem you will need to download NetLogo files from the Behaviour Composer 'manually' into NetLogo.");
//	}
	if (!epidemicGameMaker) {
	    // EGM just launches without all this
	    Object[] options = {"Yes, launch now", "No, show advanced settings"};
	    int answer = JOptionPane.showOptionDialog(frame,
		    "Ready to launch the Behaviour Composer to NetLogo application?",
		    "BC2NetLogo launcher",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[0]);
	    if (answer == JOptionPane.NO_OPTION) {
		String sessionGuid = Settings.getPreference("sessionGuid", "new");
		String userGuid = Settings.getPreference("userGuid", "new");
		String parameters = Settings.getPreference("parameters", "");
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		String modelGuid = "";
		// guids are 22 characters long - one extra space
		TextOption modelPanel = new TextOption(modelGuid,               "Model ID:         ", " ID or URL with frozen=ID  ", 23);
		panel.add(modelPanel);
		TextOption sessionPanel = new TextOption(sessionGuid,           "Session ID:       ", " ID, 'new', or session URL ", 23);
		panel.add(sessionPanel);
		TextOption userPanel = new TextOption(userGuid,                 "User ID:          ", " ID, 'new', or session URL ", 23);
		panel.add(userPanel);
		TextOption parametersPanel = new TextOption(parameters,         "Extra parameters: ", " URL parameters for BC     ", 23);
		panel.add(parametersPanel);
		TextOption serverPanel = new TextOption(server,                 "Server:           ", "", 0);
		panel.add(serverPanel);
		TextOption folderPanel = new TextOption(bc2NetLogoFolder,       "Folder:           ", " Installation location     ", 0);
		panel.add(folderPanel);
		final JCheckBox checkBox3D = new JCheckBox("Tick if you want to use the 3D version of NetLogo.");
		checkBox3D.setFont(new Font("Arial", Font.BOLD, 14));
		checkBox3D.setSelected(Settings.getPreferenceBoolean("3D", false));
		checkBox3D.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(checkBox3D);
		final JCheckBox checkBoxTranslate = new JCheckBox("Tick if you want to use a language other than English.");
		checkBoxTranslate.setFont(new Font("Arial", Font.BOLD, 14));
		checkBoxTranslate.setSelected(Settings.getPreferenceBoolean("translate", false));
		checkBoxTranslate.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(checkBoxTranslate);
		//	    JButton saveButton = new JButton("Save settings");
		//	    ActionListener saveButtonListener = new ActionListener() {
		//
		//		@Override
		//		public void actionPerformed(ActionEvent arg0) {
		//		    if (checkBox3D.isSelected() != Settings.getPreferenceBoolean("3D", false)) {
		//			Settings.putPreferenceBoolean("3D", checkBox3D.isSelected());
		//		    } 
		//		}
		//		
		//	    };
		//	    saveButton.addActionListener(saveButtonListener);
		//	    panel.add(saveButton);
		int result = JOptionPane.showConfirmDialog(null, panel, "Save your settings", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
		    if (checkBox3D.isSelected() != Settings.getPreferenceBoolean("3D", false)) {
			Settings.putPreferenceBoolean("3D", checkBox3D.isSelected());
		    }
		    if (checkBoxTranslate.isSelected() != Settings.getPreferenceBoolean("translate", false)) {
			Settings.putPreferenceBoolean("translate", checkBoxTranslate.isSelected());
		    }
		    String newSessionGuid = sessionPanel.getValue();
		    if (newSessionGuid.trim().isEmpty()) {
			Settings.removePreference("sessionGuid");
		    } else {
			newSessionGuid = extractGuid(newSessionGuid, "share=");
			Settings.putPreference("sessionGuid", newSessionGuid);
		    }
		    String newUserGuid = userPanel.getValue();
		    if (newUserGuid.trim().isEmpty()) {
			Settings.removePreference("userGuid");
		    } else {
			newUserGuid = extractGuid(newUserGuid, "user=");
			Settings.putPreference("userGuid", newUserGuid);
		    }
		    String newParameters = parametersPanel.getValue();
		    if (newParameters.trim().isEmpty()) {
			Settings.removePreference("parameters");
		    } else {
			Settings.putPreference("parameters", newParameters.trim());
		    }
		    String newModelGuid = modelPanel.getValue();
		    if (newModelGuid.trim().isEmpty()) {
			Settings.removePreference("modelGuid");
		    } else {
			newModelGuid = extractGuid(newModelGuid, "frozen=");
			Settings.putPreference("modelGuid", newModelGuid);
		    }
		    String newServer = serverPanel.getValue();
		    if (newServer.trim().isEmpty()) {
			Settings.removePreference("server");
		    } else {
			Settings.putPreference("server", newServer);
		    }
		    String newFolder = folderPanel.getValue();
		    if (!newFolder.equals(bc2NetLogoFolder)) {
			Settings.putPreference("BC2NetLogoDirectory", newFolder);
			bc2NetLogoFolder = newFolder;
			installAndLaunch();
			return;
		    }
		} else {
		    System.exit(0); // cancelled
		}
		//	    frame.add(panel);
		// make frame visible when there are options to choose	
		//	    frame.setVisible(true);
	    } else if (answer != JOptionPane.YES_OPTION) {
		System.exit(0); // cancelled
	    }
	}
	Settings.writePreferences();
	boolean threeD = Settings.getPreferenceBoolean("3D", false);
	String libPath = bc2NetLogoFolder.replaceAll("\\\\", "/") + "/lib";
	String specialLibPath = null;
	String java;
	String osName = System.getProperty("os.name");
	if (osName.toLowerCase().contains("windows")) {
	    java = bc2NetLogoFolder + "/jre/bin/java.exe";
	    specialLibPath = libPath + "/Windows";
	} else {
	    java = "java"; // trust the environment variables are set up correctly
	    // TODO: experiment with the following being null
	    specialLibPath = libPath;
	}
	ProcessBuilder processBuilder;
	String libParameter = specialLibPath == null ? "" : "-Djava.library.path=" + specialLibPath;
	// tried adding "-classpath", libPath,  to the following
	// but no effect -- idea was to avoid having the lib in the BC2NetLogo folder
	String epidemicGameFlag = epidemicGameMaker ? "1" : "0";
	if (threeD && !epidemicGameMaker) {
	    processBuilder = new ProcessBuilder(java, "-Dorg.nlogo.is3d=true", "-Xfuture", "-Xmx1024m", "-Dfile.encoding=UTF-8", "-XX:MaxPermSize=128m", "-Djava.ext.dir=", libParameter, "-jar", "support.jar");
	} else {
	    processBuilder = new ProcessBuilder(java, "-Xfuture", "-Xmx1024m", "-Dfile.encoding=UTF-8", "-XX:MaxPermSize=128m", "-Djava.ext.dir=", libParameter, "-jar", "support.jar", "-EpidemicGameMaker", epidemicGameFlag);
	}
//	processBuilder.redirectOutput(); // not available in Java 6
	processBuilder.directory(new File(bc2NetLogoFolder));
	try {
//	    Process process = 
            processBuilder.start();
//	    while (true) {
//		InputStream errorStream = process.getErrorStream();
//		byte[] bytes = new byte[1024];
//		int read = errorStream.read(bytes);
//		if (read > 0) {
//		    String line = new String(bytes, 0, read);
//		    if (line.contains("Encountered an error communicating with the server")) {
//			// TODO: deprecate or remove this branch
//			// FIXME: remove the following once confirmed it is working reliably?
//			JOptionPane.showMessageDialog(frame, "About to relaunch BC2NetLogo due to communication errors.");
//			// relaunch
//			// and maybe should also tell the server to 
//			// tell the open Behaviour Composer to put up a message that the other tab is the current one
//			process = processBuilder.start();
//		    } else {
//			while (read > 0) {
//			    read = errorStream.read(bytes);
//			    if (read > 0) {
//				line += new String(bytes, 0, read);
//			    }
//			}
//			JOptionPane.showMessageDialog(frame, "BC2NetLogo reported this error:\n" + line);
//			System.exit(0);
//		    }
//		} else if (read < 0) {
//		    // terminated
//		    System.exit(0);
//		} else {
//		    process.wait(1000);
//		}
//	    }
	} catch (Exception e) {
	    Utilities.reportException(e);
	}
	System.exit(0);
    }

    private static String extractGuid(String url, String parameterName) {
	// return URL parameter name if exists otherwise url (which might just be a guid)
	int parameterStart = url.indexOf(parameterName);
	if (parameterStart < 0) {
	    return url;
	} else {
	    int startIndex = parameterStart+parameterName.length();
	    int endIndex = url.indexOf("&", startIndex);
	    if (endIndex < 0) {
		endIndex = url.indexOf("#", startIndex);
	    }
	    if (endIndex < 0) {
		endIndex = url.length();
	    }
	    return url.substring(startIndex, endIndex);
	}
    }

//    private static void installUpdates() {
//	String updates = Utilities.urlContents("https://modelling4all.googlecode.com/svn/trunk/bc-netlogo/updates/updateInfo" + CURRENT_VERSION + ".txt");
//	if (updates == null) {
//	    return;
//	}
//	String[] parts = updates.split(";");
//	ArrayList<String> updatesNeeded = new ArrayList<String>();
//	int latestInstallerVersionNumber = 0;
//	try {
//	    latestInstallerVersionNumber = Integer.parseInt(parts[0]);
//	} catch (NumberFormatException e) {
//	    // warn?? 
//	}
//	if (CURRENT_VERSION < latestInstallerVersionNumber) {
//	    JOptionPane.showMessageDialog(frame, "This version of the launcher is too old. Please download the new version.");
//	    try {
//		Desktop.getDesktop().browse(new URI("http://resources.modelling4all.org/guides/behaviour-composer-direct-to-netlogo-guide"));
//	    } catch (Exception e) {
//		Utilities.reportException(e);
//	    }
//	    System.exit(0);
//	}
//	if (parts.length < 4) {
//	    // nothing to update
//	    return;
//	}
//	for (int i = 1; i < parts.length; i += 3) {
//	    String fileName = bc2NetLogoFolder + "/" + parts[i].trim();
//	    // following obtained from http://www.whatsmyip.org/string-timestamp-converter (and Eclipse's file property of support.jar)
//	    // Note needed to adjust time by -5 hours (time zone issue)
//	    String mostRecentVersion = parts[i+1].trim();
//	    String updateZipFile = parts[i+2].trim();
//	    File file = new File(fileName);
//	    boolean updateNeeded = !file.exists();
//	    if  (!updateNeeded)  {
//		long lastModifiedInMilliseconds = file.lastModified();
//		long mostRecentTimeStampInSeconds = Long.parseLong(mostRecentVersion);
//		// differs by more than a minute (clocks may differ)
//		double difference = mostRecentTimeStampInSeconds - lastModifiedInMilliseconds*.001;
//		updateNeeded = difference > 60;
//	    }
//	    if (updateNeeded && !updatesNeeded.contains(updateZipFile)) {
//		updatesNeeded.add(updateZipFile);
//	    }
//	}
//	int size = updatesNeeded.size();
//	for (int i = 0; i < size; i++) {
//	    unpackURLContents(updatesNeeded.get(i), bc2NetLogoFolder, "Installing update " + (i+1) + " of " + size, "Downloading");
//	}
//    }
//
//    private static void installBC2NetLogo() {
//	// first see if the zip installation is on the local disk already
//	File localFile = new File("BC2NetLogoRelease" + CURRENT_VERSION + ".zip");
//	String title = "Installing BC2NetLogo to " + bc2NetLogoFolder;
//	if (localFile.exists()) {
//	    ProgressMonitor progressMonitor = new ProgressMonitor(frame, title, "Please wait", 0, 100);
//	    File targetDir = new File(bc2NetLogoFolder);
//	    if (!targetDir.exists()) {
//		targetDir.mkdirs();
//	    }
//	    try {
//		ZipUtilities.unpackArchive(localFile, targetDir, progressMonitor);
//	    } catch (IOException e) {
//		Utilities.reportException(e);
//	    }
//	    return;
//	}
//	String url = "https://googledrive.com/host/0B1x0K1GlelzYNjBWQVBLbUpRZ1U";
//	String note = "Downloading files";
//	if (unpackURLContents(url, bc2NetLogoFolder, title, note)) {
//	    Settings.putPreference("BC2NetLogoDirectory", bc2NetLogoFolder);
//	} else {
//	    JOptionPane.showMessageDialog(frame, "BC2NetLogoDirectory removed. Was '" + bc2NetLogoFolder + "'");
//	    // if the user tries again they will need to specify the folder
//	    Settings.removePreference("BC2NetLogoDirectory");
//	    System.exit(0);
//	}
//    }

    protected static boolean unpackURLContents(String url, String destinationFolder, String title, String note) {
	ProgressMonitor progressMonitor = new ProgressMonitor(frame, title, note, 0, 100);
	try {
	    ZipUtilities.unpackArchive(new URL(url), 
		                       new File(destinationFolder),
		                       frame,
		                       progressMonitor);
	    return true;
	} catch (Exception e) {
	    Utilities.reportException(e);
	    return false;
	}
    }
    
}

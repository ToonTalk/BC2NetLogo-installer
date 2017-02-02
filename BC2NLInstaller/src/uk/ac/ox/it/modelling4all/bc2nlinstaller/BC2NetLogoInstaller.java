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
 * This class used to launch BC2NetLogo but security settings on the MacOS caused problems
 *  so this just updates settings used by BC2NetLogo
 * 
 * @author Ken Kahn
 *
 */
public class BC2NetLogoInstaller {

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
    	boolean previouslyInstalled = true;
    	// using 800 for NetLogo 6.0 while NetLogo Web uses 791 which is still 5.3.1
    	String defaultServer = "https://800-dot-m4a-gae-hrd.appspot.com"; // "https://m4a-gae.appspot.com";
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
    			String sessionGuid = Settings.getPreference("sessionGuid", "new");
    			String userGuid = Settings.getPreference("userGuid", "new");
    			String parameters = Settings.getPreference("parameters", "");
    			JPanel panel = new JPanel(new BorderLayout());
    			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    			String modelGuid = "";
    			// guids are 22 characters long - one extra space
    			TextOption modelPanel = new TextOption(modelGuid,               "Model ID:         ", " Blank, ID, or URL with frozen=ID ", 23);
    			panel.add(modelPanel);
    			TextOption sessionPanel = new TextOption(sessionGuid,           "Session ID:       ", " Blank, ID, 'new', or session URL ", 23);
    			panel.add(sessionPanel);
    			TextOption userPanel = new TextOption(userGuid,                 "User ID:          ", " Blank, ID, 'new', or session URL ", 23);
    			panel.add(userPanel);
    			TextOption parametersPanel = new TextOption(parameters,         "Extra parameters: ", " Blank or URL parameters for BC   ", 23);
    			panel.add(parametersPanel);
    			TextOption serverPanel = new TextOption(server,                 "Server:           ", "", 0);
    			panel.add(serverPanel);
    			TextOption folderPanel = new TextOption(bc2NetLogoFolder,       "Folder:           ", " Installation location     ", 0);
    			panel.add(folderPanel);
    			final JCheckBox checkBoxTranslate = new JCheckBox("Tick if you want to use a language other than English.");
    			checkBoxTranslate.setFont(new Font("Arial", Font.BOLD, 14));
    			checkBoxTranslate.setSelected(Settings.getPreferenceBoolean("translate", false));
    			checkBoxTranslate.setAlignmentX(Component.LEFT_ALIGNMENT);
    			panel.add(checkBoxTranslate);
    			int result = JOptionPane.showConfirmDialog(null, panel, "Change your Behaviour Composer launch settings", JOptionPane.OK_CANCEL_OPTION);
    			if (result == JOptionPane.OK_OPTION) {
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
    					return;
    				}
    			} else {
    				System.exit(0); // cancelled
    			}
    	Settings.writePreferences();
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

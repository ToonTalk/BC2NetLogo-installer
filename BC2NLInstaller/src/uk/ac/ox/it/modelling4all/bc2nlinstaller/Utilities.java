package uk.ac.ox.it.modelling4all.bc2nlinstaller;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Utilities {

    public static String chooseFolder(Component parent) {
	 JFileChooser chooser = new JFileChooser();
	 chooser.setDialogTitle("Choose a folder where the BC2NetLogo files will be kept.");
	 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	 int dialogResponse = chooser.showDialog(parent, "Set installation folder");
	 if (dialogResponse == JFileChooser.APPROVE_OPTION) {
	     return chooser.getSelectedFile().getAbsolutePath();
	 } else {
	     return null;
	 }
    }
    
    public static String urlContents(String urlString) {
	try {
	    URL url = new URL(urlString);
	    URLConnection connection = url.openConnection();
	    // following needed?
//	    connection.setRequestProperty("User-Agent", ???);
	    connection.setConnectTimeout(30000);
	    connection.setReadTimeout(30000);
	    return bufferedReaderToString(new BufferedReader(new InputStreamReader(connection.getInputStream())));
	} catch (Exception e) {	    
	    e.printStackTrace();
	    return null;
	}
    }
    
    private static String bufferedReaderToString(BufferedReader in) throws IOException {
	StringBuilder contents = new StringBuilder();
	String line;
	while ((line = in.readLine()) != null) {
	    contents.append(line + "\r");
	}
	in.close();
	return contents.toString();
    }

    public static void reportException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(BC2NetLogoInstaller.frame, BC2NetLogoInstaller.ERROR_PLEASE_REPORT_THIS_TO_INFO_MODELLING4ALL_ORG + "\n" + e.getMessage() + "\n" + stringWriter.toString());
    }

}

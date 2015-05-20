package uk.ac.ox.it.modelling4all.bc2nlinstaller;

// based upon http://www.java2s.com/Tutorial/Java/0180__File/UnpackanarchivefromaURL.htm

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

public class ZipUtilities {

    /**
     * Unpack an archive from a URL
     * 
     * @param url
     * @param targetDir
     * @param frame
     * @param progressMonitor 
     * @throws IOException
     */
  public static void unpackArchive(URL url, File targetDir, JFrame frame, ProgressMonitor progressMonitor) throws IOException {
      if (!targetDir.exists()) {
          targetDir.mkdirs();
      }
      InputStream in = new BufferedInputStream(url.openStream(), 1024);
      // following just pops up the progress bar for a second
//      InputStream inWithProgressMonitor = new ProgressMonitorInputStream(frame, "Downloading files", in);
      File zip = File.createTempFile("temp", ".zip", targetDir);
      zip.deleteOnExit();
      FileOutputStream fileOutputStream = new FileOutputStream(zip);
      OutputStream out = new BufferedOutputStream(fileOutputStream);
      if (progressMonitor != null) {
	  // multiply by 3/2 because empirically that is about how much is needed. 
	  // 194677 / 132529 is 1.4689388737559326
	  int blocksToRead = (3*url.openConnection().getContentLength()/1024)/2;
	  progressMonitor.setMaximum(blocksToRead);
      }
      copyInputStream(in, out, progressMonitor);
      out.close();
      if (progressMonitor != null) {
	  progressMonitor.setNote("Download complete. Installing.");
      }
      unpackArchive(zip, targetDir, progressMonitor);
  }
  
  /**
   * Unpack a zip file
   * 
   * @param theFile
   * @param targetDir
   * @return the file
   * @throws IOException
   */
  public static void unpackArchive(File theFile, File targetDir, ProgressMonitor progressMonitor) throws IOException {
      if (!theFile.exists()) {
          throw new IOException(theFile.getAbsolutePath() + " does not exist");
      }
      if (!buildDirectory(targetDir)) {
          throw new IOException("Could not create directory: " + targetDir);
      }
      ZipFile zipFile = new ZipFile(theFile);
      int counter = 1;
      if (progressMonitor != null) {
	  progressMonitor.setMaximum(zipFile.size());
      }
      try {
	  for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements();) {
	      ZipEntry entry = (ZipEntry) entries.nextElement();
	      File file = new File(targetDir, File.separator + entry.getName());
	      if (!buildDirectory(file.getParentFile())) {
		  throw new IOException("Could not create directory: " + file.getParentFile());
	      }
	      if (!entry.isDirectory()) {
		  copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)), null);
		  file.setLastModified(entry.getTime());
	      } else {
		  if (!buildDirectory(file)) {
		      throw new IOException("Could not create directory: " + file);
		  }
	      }
	      if (progressMonitor != null) {
		  if (progressMonitor.isCanceled()) {
		      System.exit(0);
		  }
		  progressMonitor.setProgress(counter++);
	      }
	  }
      } finally {
	  if (progressMonitor != null) {
	      progressMonitor.close();
	  }
	  zipFile.close();
      }
  }

  public static void copyInputStream(InputStream in, OutputStream out, ProgressMonitor progressMonitor) throws IOException {
      byte[] buffer = new byte[1024];
      int len = in.read(buffer);
      int counter = 0;
      while (len >= 0) {
          out.write(buffer, 0, len);
          len = in.read(buffer);
          if (progressMonitor != null) {
              if (progressMonitor.isCanceled()) {
        	  System.exit(0);
              }
              progressMonitor.setProgress(counter++);
          }
      }
      in.close();
      out.close();
  }

  public static boolean buildDirectory(File file) {
      return file.exists() || file.mkdirs();
  }

}
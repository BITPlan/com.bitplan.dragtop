/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.dragtop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.dragtop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.filechooser.FileSystemView;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * get Icons for files
 * 
 * @author wf
 *
 */
public class FileIcon {
  public static boolean debug=true;
  static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();

  /**
   * get the file extension for the given filename
   * e.g. "pdf" 
   * 
   * @param fname
   * @return the extension
   */
  private static String getFileExt(String fname) {
    String ext = "";
    int p = fname.lastIndexOf('.');
    if (p >= 0) {
      ext = fname.substring(p+1);
    }
    return ext.toLowerCase();
  }

  /**
   * get the Swing Icon from the file system
   * @param file
   * @return the icon
   */
  private static javax.swing.Icon getJSwingIconFromFileSystem(File file) {
    javax.swing.Icon icon = null;
    switch (OsCheck.getOperatingSystemType()) {

    case MacOS:
      final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
      icon = fc.getUI().getFileView(fc).getIcon(file);
      break;
    default:
      // case Windows:
      FileSystemView view = FileSystemView.getFileSystemView();
      icon = view.getSystemIcon(file);
      break;
    }
    return icon;
  }

  /**
   * get the file icon for the given file
   * 
   * @param file
   * @return the icon
   */
  public static Image getFileIcon(File file) {
    final String ext = getFileExt(file.getName());
    Image image = getFileIcon(ext);
    return image;
  }

  /**
   * get the Icon image for the given file extension
   * 
   * @param ext
   * @return - the Image
   * @throws Exception
   */
  public static Image getFileIcon(String ext) {

    Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
    if (fileIcon == null) {
      try {
        // first try getting icon from jar file
        URL url = FileIcon.class.getResource(ext + "32x32.png");
        // BufferedImage awtImg = ImageIO.read(url);
        // Image fxImg = SwingFXUtils.toFXImage(awtImg, null);
        fileIcon = new Image(url.openStream());
      } catch (Exception e) {

      }
      if (fileIcon == null) {
        javax.swing.Icon jswingIcon = null;

        File tempFile = null;
        try {
          tempFile = File.createTempFile("icon", "."+ext);
          jswingIcon = getJSwingIconFromFileSystem(tempFile);
        } catch (IOException ignored) {
          // Cannot create temporary file.
        } finally {
          if (tempFile != null)
            tempFile.delete();
        }

        if (jswingIcon != null) {
          fileIcon = jswingIconToImage(jswingIcon);
          mapOfFileExtToSmallIcon.put(ext, fileIcon);
        }
      }
    }

    return fileIcon;
  }

  /**
   * convert a swing Icon to an image
   * 
   * @param jswingIcon
   * @return the Image
   */
  private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
    BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(),
        jswingIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }
}

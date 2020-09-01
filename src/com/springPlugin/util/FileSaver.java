/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.util;

/**
 *
 * @author Sandipan
 */
import javax.swing.JFileChooser;

public class FileSaver {
  public static String doSave() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("Select Directory");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {       
      return chooser.getSelectedFile().getAbsolutePath();
    } else {
      return null ;
    }
  }
}


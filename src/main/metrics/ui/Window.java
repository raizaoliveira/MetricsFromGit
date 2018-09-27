package main.metrics.ui;

import javax.swing.*;

import main.metrics.git.ExtractDataGit;

import java.awt.event.*;
import java.awt.*;
import java.util.*;


public class Window extends JPanel
   implements ActionListener {
   JButton btnprojectpath;
   JButton btnsavemetrics;
   JButton btnrun;
   
   JFileChooser chooserprojectpath;
   JFileChooser choosersavepath;
   String choosertitle;
   
   private String pathToSave;
   private String pathToSource;
   
  public Window() {
    btnprojectpath = new JButton("Project Path");
    btnprojectpath.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        	pathToProject();
        }
    });
    add(btnprojectpath);
    
    btnsavemetrics = new JButton("Save Path");
    btnsavemetrics.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        	savePathToMetrics();
        }
    });
    add(btnsavemetrics);
    
    btnrun = new JButton("Extract");
    btnrun.addActionListener(this);
    add(btnrun);
    
   }

  public void pathToProject() {
      
	    chooserprojectpath = new JFileChooser(); 
	    chooserprojectpath.setCurrentDirectory(new java.io.File("."));
	    chooserprojectpath.setDialogTitle(choosertitle);
	    chooserprojectpath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooserprojectpath.setAcceptAllFileFilterUsed(false);
	    //    
	    if (chooserprojectpath.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	      System.out.println("getSelectedFile() : " 
	         +  chooserprojectpath.getSelectedFile());
	      pathToSource =  chooserprojectpath.getSelectedFile().toString();
	      }
	    else {
	      System.out.println("No Selection ");
	      }
	     } 
  
  public void actionPerformed(ActionEvent e) {
        
	  System.out.println("click");
      ExtractDataGit gitMetrics = new ExtractDataGit(pathToSource, pathToSave);
     }
  
  public void savePathToMetrics() {
     
	    choosersavepath = new JFileChooser(); 
	    choosersavepath.setCurrentDirectory(new java.io.File("."));
	    choosersavepath.setDialogTitle(choosertitle);
	    choosersavepath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    choosersavepath.setAcceptAllFileFilterUsed(false);
	    //    
	    if (choosersavepath.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	      System.out.println("getSelectedFile() : " 
	         +  choosersavepath.getSelectedFile());
	      pathToSave = choosersavepath.getSelectedFile().toString();
	      }
	    else {
	      System.out.println("No Selection ");
	      }
	     }
   
  public Dimension getPreferredSize(){
    return new Dimension(300, 300);
    }
    
  public static void main(String s[]) {
    JFrame frame = new JFrame("");
    Window panel = new Window();
    frame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
          }
        }
      );
    frame.getContentPane().add(panel,"Center");
    frame.setSize(panel.getPreferredSize());
    frame.setVisible(true);
	frame.setLayout(null);
	frame.setLocationRelativeTo(null);
    }
}

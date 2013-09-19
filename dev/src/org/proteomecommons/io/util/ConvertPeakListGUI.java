/*
 *    Copyright 2005 The Regents of the University of Michigan
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
package org.proteomecommons.io.util;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import org.proteomecommons.io.*;
import org.proteomecommons.io.bruker.CantLocateCompassXportExecutableException;
import org.proteomecommons.io.bruker.CompassXportFactory;
import org.proteomecommons.io.raw.CantLocateReAdWExecutableException;
import org.proteomecommons.io.raw.RawViaReadwFactory;

/**
 * This is a simple program that converts peak lists from one format to another
 * using a GUI.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class ConvertPeakListGUI extends JFrame implements MouseListener {
    JTextArea in = new JTextArea();
    // get a list of all the writers
    PeakListWriterFactory[] writers = GenericPeakListWriter.getRegisteredWriters();
    
    JTextField out = new JTextField("Default - Don't Merge");
    
    JButton convert = new JButton("Convert!");
    
    File outputFile = null;
    
    File[] inputFiles = new File[0];
    
    JComboBox writerFormats = null;
    
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
    public void mouseClicked(MouseEvent e) {
        // handle the event
        Object o = e.getSource();
        // check for new input
        if (o.equals(in)) {
            System.out.println("Getting input files.");
            getInputFiles();
        }
        // check for new output
        if (o.equals(out)) {
            getOutputFile();
        }
        // check for conversion
        if (o.equals(convert)) {
            convert();
        }
        
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Public constructor. This will build the GUI.
     *
     */
    public ConvertPeakListGUI() {
        super("Peak List Conversion Tool");
        // close on exit
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // set to gridbag layout
        GridBagLayout gbl = new GridBagLayout();
        getContentPane().setLayout(gbl);
        // make constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        
        // make the top list
        getContentPane().add(new JLabel("Files to Convert"), gbc);
        gbc.weighty = 1;
        JScrollPane jsp = new JScrollPane(in);
        getContentPane().add(jsp, gbc);
        
        // describe all the writers
        String[] writerNames = new String[writers.length];
        for (int i = 0; i < writers.length; i++) {
            writerNames[i] = writers[i].getName();
        }
        // make a combo box with all the names of formats
        writerFormats = new JComboBox(writerNames);
        
        // add the select file format box
        gbc.weighty = 0;
        getContentPane().add(new JLabel("Output Format"), gbc);
        getContentPane().add(writerFormats, gbc);
        
        //
        
        // add merge option
        gbc.weighty = 0;
        gbc.weightx = 0;
        getContentPane()
        .add(
                new JLabel(
                "Merge Option. Concatinates input files to single output file."),
                gbc);
        gbc.weightx = 1;
        getContentPane().add(out, gbc);
        
        // add the conversion button
        getContentPane().add(convert);
        
        // add the mouse listeners
        in.addMouseListener(this);
        out.addMouseListener(this);
        convert.addMouseListener(this);
        
        // toggle read-only options
        in.setEditable(false);
        out.setEditable(false);
        writerFormats.setSelectedIndex(0);
    }
    
    /**
     * Helper method to query user for input files.
     */
    private void getInputFiles() {
        // open a JFileChooser
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select File(s) to Convert");
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.showOpenDialog(this);
        
        // get the files
        File[] files = jfc.getSelectedFiles();
        if (files == null) {
            return;
        }
        inputFiles = files;
        
        // clear the old files
        in.setText("");
        
        // add the files
        for (int i = 0; i < files.length; i++) {
            in.append(files[i].getAbsolutePath() + "\n");
        }
    }
    
    /**
     * Helper method to query user for output file.
     */
    private void getOutputFile() {
        // open a JFileChooser
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select Output File or Directory");
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.showOpenDialog(this);
        
        // get the files
        File file = jfc.getSelectedFile();
        if (file == null) {
            return;
        }
        
        // set the output file
        outputFile = file;
        // clear the old files
        out.setText(file.getAbsolutePath());
    }
    
    /**
     * Helper method to conver files.
     *
     */
    private void convert() {
        // check for output directory
        String outputDirectory = null;
        if (outputFile != null && outputFile.isDirectory()) {
            outputDirectory = outputFile.getAbsolutePath();
        }
        
        // get the extension
        String extension = (String) writerFormats.getSelectedItem();
        for (int i = 0; i < writers.length; i++) {
            if (writers[i].getName().equals(extension)) {
                extension = writers[i].getFileExtension();
                break;
            }
        }
        
        // make the converter
        ConvertPeakList cpl = new ConvertPeakList();
        try {
            // if dir, convert individually
            if (outputFile == null) {
                for (int i = 0; i < inputFiles.length; i++) {
                    // if no directory, reuse given
                    if (outputDirectory == null) {
                        // catch RAW exceptions
                        try {
                            cpl.convertPeakList(inputFiles[i].getAbsolutePath(), inputFiles[i].getAbsolutePath() + extension);
                        }
                        // handle if the ReAdW.exe binary can't be found
                        catch (CantLocateReAdWExecutableException e){
                            int choice = JOptionPane.showConfirmDialog(this, "Can't located ReAdW.exe. You can not convert RAW files without this program and the XCalibur binary from Thermo Finnigan. If you have ReAdW.exe installed, please click 'OK' to select the location of the program. Otherwise click 'Cancel'.");
                            if (choice == JOptionPane.CANCEL_OPTION) {
                                JOptionPane.showMessageDialog(this, "File conversion failed! If you need a copy of the ReAdW.exe program, you may download it from You can download ReAdW.exe from http://tools.proteomecenter.org/ReAdW.php.");
                                return;
                            }
                            // show  file chooser
                            JFileChooser jfc = new JFileChooser();
                            jfc.setFileSelectionMode(jfc.FILES_ONLY);
                            jfc.setMultiSelectionEnabled(false);
                            jfc.setApproveButtonText("Use selected ReAdW.exe binary.");
                            jfc.showOpenDialog(this);
                            // get/check the file
                            File selectedFile = jfc.getSelectedFile();
                            if (selectedFile == null || !selectedFile.exists()) {
                                JOptionPane.showMessageDialog(this, "You chose an invalid ReAdW.exe binary. Conversion failed.");
                                return;
                            }
                            // set the file
                            RawViaReadwFactory.setReadwLocation(selectedFile);
                            // do the conversion
                            cpl.convertPeakList(inputFiles[i].getAbsolutePath(), inputFiles[i].getAbsolutePath() + extension);
                        }
                        // handle if the CompassXport binary can't be found
                        catch (CantLocateCompassXportExecutableException e){
                            int choice = JOptionPane.showConfirmDialog(this, "Can't located CompassXport.exe. You can not convert Bruker file formats without this program. If you have CompassXport.exe installed, please click 'OK' to select the location of the program. Otherwise click 'Cancel'.");
                            if (choice == JOptionPane.CANCEL_OPTION) {
                                JOptionPane.showMessageDialog(this, "File conversion failed! If you need a copy of the CompassXport.exe program, please see the on-line instructions http://www.proteomecommons.org/current/531/.");
                                return;
                            }
                            // show  file chooser
                            JFileChooser jfc = new JFileChooser();
                            jfc.setFileSelectionMode(jfc.FILES_ONLY);
                            jfc.setMultiSelectionEnabled(false);
                            jfc.setApproveButtonText("Use selected CompassXport.exe binary.");
                            jfc.showOpenDialog(this);
                            // get/check the file
                            File selectedFile = jfc.getSelectedFile();
                            if (selectedFile == null || !selectedFile.exists()) {
                                JOptionPane.showMessageDialog(this, "You chose an invalid CompassXport.exe binary. Conversion failed.");
                                return;
                            }
                            // set the file
                            CompassXportFactory.setCompassXportLocation(selectedFile);
                            // do the conversion
                            cpl.convertPeakList(inputFiles[i].getAbsolutePath(), inputFiles[i].getAbsolutePath() + extension);
                        }
                    }
                    // if directory specified, use it
                    else {
                        cpl.convertPeakList(inputFiles[i].getAbsolutePath(),
                                outputDirectory + inputFiles[i].getName() + "."
                                + extension);
                    }
                }
            }
            
            // if file, concat
            if (outputFile != null && outputDirectory == null) {
                // get all the input files
                String[] names = new String[inputFiles.length];
                for (int i = 0; i < inputFiles.length; i++) {
                    names[i] = inputFiles[i].getAbsolutePath();
                }
                
                // convert and concat
                cpl.mergePeakLists(names, outputFile.getAbsolutePath());
            }
            
            // say you are done
            JOptionPane.showMessageDialog(this, "Conversion Complete");
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage()+"\nCould not perform the conversion.");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
        ConvertPeakListGUI gui = new ConvertPeakListGUI();
        gui.setSize(500, 500);
        gui.setVisible(true);
    }
    //		// flags
    //		boolean normalize = false;
    //		boolean sort = false;
    //
    //		// open a JFileChooser
    //		JFileChooser jfc = new JFileChooser();
    //		jfc.setDialogTitle("Select File(s) to Convert");
    //		jfc.setMultiSelectionEnabled(true);
    //		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    //		jfc.showOpenDialog(null);
    //
    //		// get the files
    //		File[] files = jfc.getSelectedFiles();
    //		if (files == null) {
    //			return;
    //		}
    //
    //		// open up the convert frame
    //		ChooseFormat cf = new ChooseFormat();
    //		cf.setVisible(true);
    //		// loop while it is visible
    //		while (cf.isVisible()) {
    //			try {
    //				Thread.sleep(500);
    //			} catch (Exception e) {
    //				// noop
    //			}
    //		}
    //
    //		// get format
    //		String extension = (String) PeakListWriter.getFormats().get(cf.format);
    //
    //		// track the files that have been made
    //		StringWriter status = new StringWriter();
    //		status.write("Conversion complete. Files made:\n");
    //
    //		// handle concats
    //		if (cf.concat) {
    //			try {
    //				// make the writer
    //				String filename = files[0].getParentFile().getAbsolutePath()
    //						+ File.separatorChar + "concat" + extension;
    //				PeakListWriter writer =
    // GenericPeakListWriter.getPeakListWriter(filename);
    //				// convert each file
    //				for (int i = 0; i < files.length; i++) {
    //					PeakList p = GenericPeakListReader
    //							.read(files[i].getAbsolutePath());
    //					writer.write(p);
    //				}
    //				writer.finish();
    //				// add the filename
    //				status.write(filename+"\n");
    //			} catch (Exception e) {
    //				// pop up a dialog
    //				Finish finish = new Finish("Conversion Failed.");
    //				finish.setVisible(true);
    //				return;
    //			}
    //		} else {
    //			// convert each file
    //			for (int i = 0; i < files.length; i++) {
    //				PeakList p = GenericPeakListReader.read(files[i].getAbsolutePath());
    //				String filename = files[i].getAbsolutePath() + extension;
    //				PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(filename);
    //				plw.write(p);
    //				// track the file
    //				status.write(filename+"\n");
    //			}
    //		}
    //
    //		// pop up a dialog
    //		Finish finish = new Finish(status.toString());
    //		finish.setVisible(true);
    //	}
    //}
    //
    //class Finish extends JFrame {
    //	public Finish(String text) {
    //		// use grid layout
    //		GridBagLayout gbl = new GridBagLayout();
    //		setLayout(gbl);
    //		GridBagConstraints gbc = new GridBagConstraints();
    //		gbc.gridwidth = GridBagConstraints.REMAINDER;
    //		gbc.insets= new Insets(10,10,10,10);
    //
    //		// text
    //		JTextArea jta = new JTextArea();
    //		jta.setEditable(false);
    //		jta.setOpaque(false);
    //		jta.setText(text);
    //		getContentPane().add(jta, gbc);
    //		pack();
    //		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //
    //		// center it
    //		Toolkit t = Toolkit.getDefaultToolkit();
    //		Dimension d = t.getScreenSize();
    //		setLocation((int)(d.width/2-getWidth()/2),
    // (int)(d.height/2-getHeight()/2));
    //	}
    //}
    //
    //// helper class for choosing formats
    //class ChooseFormat extends JFrame implements ActionListener {
    //	boolean concat = false;
    //
    //	String format = null;
    //
    //	/*
    //	 * (non-Javadoc)
    //	 *
    //	 * @see
    // java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    //	 */
    //	public void actionPerformed(ActionEvent e) {
    //		// catch button events
    //		Object o = e.getSource();
    //		// look for toggle button clicks
    //		if (o instanceof JToggleButton) {
    //			concat = !concat;
    //		}
    //		// look for normal button clicks
    //		if (o instanceof JButton) {
    //			JButton jb = (JButton) o;
    //			format = jb.getText();
    //			setVisible(false);
    //			dispose();
    //		}
    //
    //	}
    //
    //	public ChooseFormat() {
    //		// use grid layout
    //		GridBagLayout gbl = new GridBagLayout();
    //		setLayout(gbl);
    //		GridBagConstraints gbc = new GridBagConstraints();
    //		gbc.gridwidth = GridBagConstraints.REMAINDER;
    //
    //		// add some text
    //		JTextArea text = new JTextArea(
    //				"Please select the format you'd like to convert to and optionally select
    // if you'd like all files to be concatenated in to a single peak list.");
    //		text.setWrapStyleWord(true);
    //		text.setLineWrap(true);
    //		text.setEditable(false);
    //		text.setBackground(getBackground());
    //		gbc.fill = GridBagConstraints.BOTH;
    //		gbc.insets = new Insets(10, 10, 10, 10);
    //		gbc.weighty = 1;
    //		gbc.weightx = 0;
    //		getContentPane().add(text, gbc);
    //
    //		// add a button for each format
    //		gbc.fill = GridBagConstraints.HORIZONTAL;
    //		gbc.weightx = 1;
    //		gbc.weighty = 1;
    //		gbc.insets = new Insets(0, 0, 0, 0);
    //		Map formats = PeakListWriter.getFormats();
    //		Set keys = formats.keySet();
    //		for (Iterator i = keys.iterator(); i.hasNext();) {
    //			String name = (String) i.next();
    //			String extension = (String) formats.get(name);
    //			// make a button
    //			JButton button = new JButton(name);
    //			button.addActionListener(this);
    //			gbc.ipady = 4;
    //			getContentPane().add(button, gbc);
    //		}
    //
    //		gbc.insets = new Insets(5, 5, 5, 5);
    //		// add an option for concat to single peak list
    //		JToggleButton concat = new JToggleButton("Convert to single peak list");
    //		concat.addActionListener(this);
    //		getContentPane().add(concat, gbc);
    //
    //		// kill on exit
    //		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //
    //		// pack
    //		pack();
    //		// can't resize
    //		setResizable(false);
    //
    //		// center it
    //		Toolkit t = Toolkit.getDefaultToolkit();
    //		Dimension d = t.getScreenSize();
    //		setLocation((int)(d.width/2-getWidth()/2),
    // (int)(d.height/2-getHeight()/2));
    //	}
}

class StepPanel extends JPanel {
    public StepPanel(int step, StepOptionPanel option) {
        add(new JLabel("Step " + step + ":"));
    }
}

class StepOptionPanel extends JPanel {
    
}
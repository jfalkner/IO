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
package org.proteomecommons.io.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.IntermediateFilePeakListReader;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriterFactory;
import org.proteomecommons.io.bruker.CantLocateCompassXportExecutableException;
import org.proteomecommons.io.bruker.CompassXportFactory;
import org.proteomecommons.io.raw.CantLocateReAdWExecutableException;
import org.proteomecommons.io.raw.RawViaReadwFactory;
import org.proteomecommons.io.util.ConvertPeakList;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ConvertPeakListGUIPanel extends JPanel {
    // the default properties file
    public final String DEFAULT_PROPERTIES_FILE = "/Program Files/ProteomeCommons.org/IO/default.properties";
    // the background image
    BufferedImage backgroundImage = null;
    // the background
    BufferedImage screenShot = null;
    // a buffer for drawnig
    BufferedImage buffer = null;
    
    OptionsPanel optionsPanel = null;
    
    // input files to use
    File[] inputFiles = new File[0];
    // the output file to use
    File outputFile = null;
    
    // angle of the spinning box
    int angle = 0;
    
    // flag for the helper thread
    private boolean runHelperThread = false;
    
    // the thread
    Thread t = new Thread() {
        public void run() {
            for (int i=0;i<=360*1000;i+=1) {
                try {
                    do {
                        Thread.sleep(250);
                    }
                    while(!ConvertPeakListGUIPanel.this.isSpinBackground());
                } catch (Exception e) {
                    // noop
                }
                ConvertPeakListGUIPanel.this.angle = i;
                
                // pause a little
                Thread.yield();
                
                ConvertPeakListGUIPanel.this.repaint();
            }
        }
    };
    
    // spawn off the instruction thread -- this flashes the "Click Here"
    Thread stepsThread = new Thread() {
        public void run() {
            // step 1
            while (ConvertPeakListGUIPanel.this.stepsIndex < steps.length) {
                steps[stepsIndex].repaint();
                try {
                    Thread.sleep(750);
                } catch (Exception e){
                    // noop
                }
            }
        }
    };
    
    private boolean spinBackground = false;
    
    private boolean highlightPanel = true;
    private long lastHighlighted = System.currentTimeMillis();
    
    private JComboBox writerFormats;
    private JCheckBox merge = null;
    private JCheckBox keepIntermediateFile = null;
    // output directory
    private OutputDirectoryField outputDirectoryField;
    
    // the translucent pane
    JPanel[] steps = new JPanel[4];
    int stepsIndex = 0;
    
    // the output directory
    File outputDirectory = new File(".");
    File inputDirectory = new File(".");
    
    public ConvertPeakListGUIPanel() {
        try {
            // load the pic
//            FileInputStream is = new FileInputStream("C:/Documents and Settings/Jayson/Desktop/netbeans_projects_backup/IO/build/classes/images/background.png");
            InputStream is = getClass().getResourceAsStream("/images/background.png");
            backgroundImage = ImageIO.read(is);
            updateBackground();
        } catch (Exception ex) {
            throw new RuntimeException("Can't load the background image!", ex);
        }
        
        setOpaque(true);
        setBackground(new Color(175, 175, 175, 175));
        
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        // constraint
        GridBagConstraints gbc = new GridBagConstraints();
        
        // style the layout for the label
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = gbc.NORTHWEST;
        gbc.insets = new Insets(80, 120, 0, 0);
        
        // make a title label
        JLabel title = new JLabel("Peak List Conversion Tool");
        title.setOpaque(false);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        JPanel titlePanel = new WhiteBox(title, new Insets(0, 0, 0, 0));
        gbc.fill = gbc.HORIZONTAL;
        titlePanel.setBorder(BorderFactory.createEmptyBorder());
        add(titlePanel, gbc);
        
        // the URL
        JLabel link = new JLabel("http://www.proteomecommons.org/current/531 - Right-click for options (exit, etc.)");
        link.setForeground(Color.WHITE);
        link.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.insets = new Insets(0,120,0,0);
        JPanel linkPanel = new WhiteBox(link, new Insets(0, 0, 0, 0));
        linkPanel.setBorder(BorderFactory.createEmptyBorder());
        linkPanel.setBackground(new Color(50, 50, 150, 125));
        gbc.anchor = gbc.CENTER;
        add(linkPanel, gbc);
        
        // set everything in
        gbc.insets = new Insets(10, 120, 0, 0);
        gbc.fill = gbc.BOTH;
        gbc.anchor = gbc.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        // text area for data
        JTextArea jta = new CustomTextArea();
        jta.setText("Click here to select the peak list files to convert.");
        jta.setOpaque(true);
        jta.setBackground(new Color(255, 255, 255, 200));
        
        // add a scrolling pane
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        add(jsp, gbc);
        
        steps[0] = new TranslucentPane("Step 1: Select Files", jsp);
        add(steps[0], gbc);
        
        // dont' take up space
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridwidth = gbc.RELATIVE;
        gbc.insets = new Insets(5, 120, 5, 5);
        
        // make a combo box with all the names of formats
        writerFormats = new PickFileFormatBox();
        
        steps[1] = new TranslucentPane("Step 2: Select Output Format", writerFormats, false);
        add(steps[1], gbc);
        
        // add an options panel
        optionsPanel = new OptionsPanel();
        gbc.weightx = 0;
        gbc.gridwidth = gbc.REMAINDER;
        steps[3] = new TranslucentPane("Misc. Options", optionsPanel, false, 5);
        add(steps[3], gbc);
        
        // make a button for output file
        outputDirectoryField = new OutputDirectoryField();
        gbc.insets = new Insets(0, 120, 0, 0);
        gbc.weightx = 1;
        steps[2] = new TranslucentPane("Step 3: Select Output Directory", outputDirectoryField, false, 5);
        add(steps[2], gbc);
        
        JButton button = new ConvertPeakListsButton();
        gbc.insets = new Insets(0, 120, 0, 0);
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.HORIZONTAL;
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        add(button, gbc);
        
        // start the thread
        t.setDaemon(true);
        t.start();
        
        // start the steps thread
        stepsThread.setDaemon(true);
        stepsThread.start();
        
        // load any existing preferences
        File propsFile = new File(DEFAULT_PROPERTIES_FILE);
        if (propsFile.exists()) {
            Properties props = new Properties();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(propsFile);
                props.load(fis);
                // get the default input
                inputDirectory = new File(props.getProperty("input"));
                outputDirectory = new File(props.getProperty("output"));
                // set the output text
                outputDirectoryField.setText(outputDirectory.getAbsolutePath());
            } catch (Exception e){
                // noop
            } finally {
                try {fis.close();} catch (Exception e){}
            }
            
        }
    }
    
    // helper method to save the props
    private void saveProperties() {
        FileOutputStream fos = null;
        try {
            File propsFile = new File(DEFAULT_PROPERTIES_FILE);
            if (!propsFile.exists()) {
                propsFile.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(propsFile);
            // make the properties
            Properties props = new Properties();
            props.setProperty("input", inputDirectory.getAbsolutePath());
            props.setProperty("output", outputDirectory.getAbsolutePath());
            props.store(fos, "");
        }
        catch (Exception e){
            // noop
        }
        finally {
            try {fos.flush();} catch (Exception e){}
            try {fos.close();} catch (Exception e){}
        }
    }
    
    void updateBackground() throws HeadlessException, AWTException {
        // take a screen shot
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        Robot r = new Robot();
        screenShot = r.createScreenCapture(new Rectangle(d));
        
        // maually double-buffer
        buffer = new BufferedImage(700, 500, BufferedImage.TYPE_INT_ARGB);
    }
    
    public void paint(Graphics g) {
        // get the buffers graphics
        Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // paint the screen shot
        Container c = this.getParent();
        while (!(c instanceof JFrame)) {
            c = c.getParent();
        }
        JFrame frame = (JFrame)c;
        int x = frame.getX();
        int y = frame.getY();
        int insetLeft = frame.getInsets().left;
        int insetTop = frame.getInsets().top;
        g2d.drawImage(screenShot.getSubimage(x, y, getWidth()+insetLeft, getHeight()+insetTop), 0- insetLeft, 0 - insetTop, getWidth()+insetLeft, getHeight()+insetTop, this);
        
        
        // paint the background
        if (backgroundImage != null) {
            // copy the old image
            BufferedImage copy = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D copyGraphics = (Graphics2D)copy.getGraphics();
            AffineTransform newTrans = copyGraphics.getTransform().getRotateInstance(angle*Math.PI/180, backgroundImage.getWidth()/2, backgroundImage.getHeight()/2);
            copyGraphics.setTransform(newTrans);
            copyGraphics.drawImage(backgroundImage, 0, 0, copy.getWidth(), copy.getHeight(), this);
            
            // get the old transform
            g2d.drawImage(copy, -25, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
        }
        
        // paint the background
        g2d.setColor(getBackground());
        g2d.fillRect(120,80, getWidth(), getHeight());
        
        // paint the pic
        this.paintChildren(g2d);
        
        // toggle highlihgt
        long currentTime = System.currentTimeMillis();
        if (currentTime-lastHighlighted > 500) {
            highlightPanel = !highlightPanel;
            lastHighlighted = currentTime;
        }
        // highlight the panel that is selected
        if (highlightPanel && isRunHelperThread()) {
            // get the offsets
            int stepX = steps[stepsIndex].getX();
            int stepY = steps[stepsIndex].getY();
            int stepWidth = steps[stepsIndex].getWidth();
            int stepHeight = steps[stepsIndex].getHeight();
            
            // shade the background
            g2d.setColor(new Color(255, 200, 200, 125));
            g2d.fillRect(stepX+5, stepY, stepWidth-10, stepHeight-5);
            g2d.setColor(Color.RED);
            g2d.drawRect(stepX+5, stepY, stepWidth-10, stepHeight-5);
            
            // draw the click here text
            String text = "Next Step";
            if (stepsIndex == 3) {
                text = "Optional";
            }
            Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2d.drawString(text, (int)(stepX+stepWidth/2-textBounds.getWidth()), (int)(stepY+stepHeight/2+textBounds.getHeight()));
            
        }
        
        // finally paint the buffer
        g.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);
    }
    
    // the button that does the conversions
    class ConvertPeakListsButton extends JButton implements ActionListener {
        public ConvertPeakListsButton() {
            super("Convert Peak List(s)");
            addActionListener(this);
        }
        
        // do the conversion on mouse clicks
        public void actionPerformed(final ActionEvent e) {
            // spawn a thread to do the convertion
            Thread conversionThread = new Thread() {
                public void run() {
                    // start spinning that gear
                    ConvertPeakListGUIPanel.this.setSpinBackground(true);
                    try {
                        // check for output directory
                        String outputDirectory = null;
                        if (outputFile != null && outputFile.isDirectory()) {
                            outputDirectory = outputFile.getAbsolutePath();
                        }
                        
                        // get the extension
                        String extension = (String) writerFormats.getSelectedItem();
                        PeakListWriterFactory[] writers = GenericPeakListWriter.getRegisteredWriters();
                        for (int i = 0; i < writers.length; i++) {
                            if (writers[i].getName().equals(extension)) {
                                extension = writers[i].getFileExtension();
                                break;
                            }
                        }
                        
                        // if this is a no convert, then don't keep the intermediate file
                        if (keepIntermediateFile.isSelected()) {
                            // handle each selected file
                            for (int i=0;i<inputFiles.length;i++) {
                                PeakListReader reader = null;
                                try {
                                    // get the reader
                                    reader = GenericPeakListReader.getPeakListReader(inputFiles[i].getAbsolutePath());
                                    // check that it is the right type of peak list reader
                                    if (!(reader instanceof IntermediateFilePeakListReader)) {
                                        JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "You can only save intermediate files from embedded tools such as ReAdW, XCompass, etc.");
                                        return;
                                        
                                    }
                                    // do the conversion
                                    IntermediateFilePeakListReader ifplr = (IntermediateFilePeakListReader)reader;
                                    // save the file
                                    File intermediateFile = ifplr.getIntermediateFile();
                                    File saveAs = new File(ConvertPeakListGUIPanel.this.outputDirectory, inputFiles[i].getName()+ifplr.getIntermediateExtension());
                                    // copy over the file
                                    FileOutputStream fos = null;
                                    FileInputStream fis = null;
                                    try {
                                        fos = new FileOutputStream(saveAs);
                                        fis = new FileInputStream(intermediateFile);
                                        byte[] buf = new byte[10000];
                                        for (int bytesRead = fis.read(buf);bytesRead!=-1;bytesRead=fis.read(buf)) {
                                            fos.write(buf, 0, bytesRead);
                                        }
                                    } finally {
                                        try { fos.flush(); } catch (Exception e){}
                                        try { fos.close(); } catch (Exception e){}
                                        try { fis.close(); } catch (Exception e){}
                                    }
                                    JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "Saved intermediate file as "+saveAs);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "Can't handle "+inputFiles[i].getAbsolutePath()+". "+ex.getMessage());
                                    return;
                                } finally {
                                    try { reader.close(); } catch (Exception e){}
                                }
                            }
                            return;
                        }
                        
                        // make the converter
                        ConvertPeakList cpl = new ConvertPeakList();
                        // catch RAW exceptions/Bruker
                        try {
                            
                            // if no merge, write each file
                            if (!ConvertPeakListGUIPanel.this.merge.isSelected()) {
                                for (int i = 0; i < ConvertPeakListGUIPanel.this.inputFiles.length; i++) {
                                    // if no merge, make something for each file
                                    if (!ConvertPeakListGUIPanel.this.merge.isSelected()) {
                                        cpl.convertPeakList(inputFiles[i].getAbsolutePath(), ConvertPeakListGUIPanel.this.outputDirectory.getAbsolutePath()+File.separatorChar+ ConvertPeakListGUIPanel.this.inputFiles[i].getName() + extension);
                                    }
                                }
                            } else {
                                // get all the input files
                                String[] names = new String[ConvertPeakListGUIPanel.this.inputFiles.length];
                                for (int i = 0; i < inputFiles.length; i++) {
                                    names[i] = ConvertPeakListGUIPanel.this.inputFiles[i].getAbsolutePath();
                                }
                                
                                // convert and concat
                                cpl.mergePeakLists(names, new File(ConvertPeakListGUIPanel.this.outputDirectory, "merge"+extension).getAbsolutePath());
                            }
                            
                            // say you are done
                            JOptionPane.showMessageDialog(null, "Conversion Complete");
                        }
                        // handle if the ReAdW.exe binary can't be found
                        catch (CantLocateReAdWExecutableException ex){
                            int choice = JOptionPane.showConfirmDialog(ConvertPeakListGUIPanel.this, "Can't located ReAdW.exe. You can not convert RAW files without this program and the XCalibur binary from Thermo Finnigan. If you have ReAdW.exe installed, please click 'OK' to select the location of the program. Otherwise click 'Cancel'.");
                            if (choice == JOptionPane.CANCEL_OPTION) {
                                JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "File conversion failed! If you need a copy of the ReAdW.exe program, you may download it from You can download ReAdW.exe from http://tools.proteomecenter.org/ReAdW.php.");
                                return;
                            }
                            // show  file chooser
                            JFileChooser jfc = new JFileChooser();
                            jfc.setFileSelectionMode(jfc.FILES_ONLY);
                            jfc.setMultiSelectionEnabled(false);
                            jfc.setApproveButtonText("Use selected ReAdW.exe binary.");
                            jfc.showOpenDialog(ConvertPeakListGUIPanel.this);
                            // get/check the file
                            File selectedFile = jfc.getSelectedFile();
                            if (selectedFile == null || !selectedFile.exists()) {
                                JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "You chose an invalid ReAdW.exe binary. Conversion failed.");
                                return;
                            }
                            // set the file
                            RawViaReadwFactory.setReadwLocation(selectedFile);
                            // do the conversion
                            actionPerformed(e);
                        }
                        // handle if the CompassXport binary can't be found
                        catch (CantLocateCompassXportExecutableException ex){
                            int choice = JOptionPane.showConfirmDialog(ConvertPeakListGUIPanel.this, "Can't located CompassXport.exe. You can not convert Bruker file formats without this program. If you have CompassXport.exe installed, please click 'OK' to select the location of the program. Otherwise click 'Cancel'.");
                            if (choice == JOptionPane.CANCEL_OPTION) {
                                JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "File conversion failed! If you need a copy of the CompassXport.exe program, please see the on-line instructions http://www.proteomecommons.org/current/531/.");
                                return;
                            }
                            // show  file chooser
                            JFileChooser jfc = new JFileChooser();
                            jfc.setFileSelectionMode(jfc.FILES_ONLY);
                            jfc.setMultiSelectionEnabled(false);
                            jfc.setApproveButtonText("Use selected CompassXport.exe binary.");
                            jfc.showOpenDialog(ConvertPeakListGUIPanel.this);
                            // get/check the file
                            File selectedFile = jfc.getSelectedFile();
                            if (selectedFile == null || !selectedFile.exists()) {
                                JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, "You chose an invalid CompassXport.exe binary. Conversion failed.");
                                return;
                            }
                            // set the file
                            CompassXportFactory.setCompassXportLocation(selectedFile);
                            // do the conversion
                            actionPerformed(e);
                            
                        } catch (Exception ex){
                            JOptionPane.showMessageDialog(ConvertPeakListGUIPanel.this, ex.getMessage()+"\nCould not perform the conversion.");
                            ex.printStackTrace();
                        }
                    } finally {
                        // start spinning that gear
                        ConvertPeakListGUIPanel.this.setSpinBackground(false);
                        // reset the angle
                        ConvertPeakListGUIPanel.this.angle = 0;
                        ConvertPeakListGUIPanel.this.repaint();
                    }
                }
            };
            // start up the conversion
            conversionThread.start();
        }
    }
    
    // the text-area that the user clicks on
    class CustomTextArea extends JTextArea implements MouseListener {
        public CustomTextArea() {
            // don't let users manually add text
            setEditable(false);
            // ensure that the border looks as expected
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            addMouseListener(this);
        }
        
        public void mouseReleased(MouseEvent e) {
        }
        
        public void mousePressed(MouseEvent e) {
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
        public void mouseEntered(MouseEvent e) {
        }
        
        public void mouseClicked(MouseEvent e) {
            // open a JFileChooser
            JFileChooser jfc = new JFileChooser();
            jfc.setDialogTitle("Select File(s) to Convert");
            jfc.setMultiSelectionEnabled(true);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // set the saved directory
            jfc.setCurrentDirectory(inputDirectory);
            jfc.showOpenDialog(ConvertPeakListGUIPanel.this);
            
            // get the files
            File[] files = jfc.getSelectedFiles();
            if (files == null) {
                return;
            }
            // set the input files
            inputFiles = files;
            
            // set the default input directory
            if (inputFiles.length > 0 && inputFiles[0].getParentFile() != null) {
                inputDirectory = inputFiles[0].getParentFile();
                saveProperties();
            }
            
            // clear the old files
            setText("");
            
            // add the files
            for (int i = 0; i < files.length; i++) {
                append(files[i].getAbsolutePath() + "\n");
            }
            
            // update the steps
            stepsIndex = 1;
        }
        
    }
    
    public boolean isSpinBackground() {
        return spinBackground;
    }
    
    public void setSpinBackground(boolean spinBackground) {
        this.spinBackground = spinBackground;
    }
    
    class OptionsPanel extends JPanel {
        public OptionsPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            // add a new checkbox
            merge = new JCheckBox("Merge Files");
            gbc.insets = new Insets(1, 1, 1, 1);
            gbc.gridwidth = gbc.RELATIVE;
            gbc.fill = gbc.HORIZONTAL;
            merge.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            merge.setOpaque(false);
            
            // add the merge option
            add(merge, gbc);
            
            // add another checkbox for the keep literal
            keepIntermediateFile = new JCheckBox("Don't Convert");
            keepIntermediateFile.setOpaque(false);
            add(keepIntermediateFile, gbc);
            
            // show behind
            setOpaque(false);
        }
    }
    
    class PickFileFormatBox extends JComboBox implements MouseListener {
        public PickFileFormatBox() {
            // add a drop down box
            PeakListWriterFactory[] writers = GenericPeakListWriter.getRegisteredWriters();
            // describe all the writers
            String[] writerNames = new String[writers.length];
            for (int i = 0; i < writers.length; i++) {
                addItem(writers[i].getName());
            }
            
            // register it as its own listener
            addMouseListener(this);
        }
        
        public void mouseReleased(MouseEvent e) {
        }
        
        public void mousePressed(MouseEvent e) {
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
        public void mouseEntered(MouseEvent e) {
        }
        
        public void mouseClicked(MouseEvent e) {
            // update the steps index
            stepsIndex = 2;
            // repaint for luck
            ConvertPeakListGUIPanel.this.repaint();
        }
        
        
    }
    
    class OutputDirectoryField extends JTextField implements MouseListener {
        public OutputDirectoryField() {
            super(ConvertPeakListGUIPanel.this.outputDirectory.getAbsolutePath());
            addMouseListener(this);
            // don't let users edit
            setEditable(false);
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            // set some helper text
            setToolTipText("Click to select the output directory.");
        }
        
        public void mouseReleased(MouseEvent e) {
        }
        
        public void mousePressed(MouseEvent e) {
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
        public void mouseEntered(MouseEvent e) {
        }
        
        public void mouseClicked(MouseEvent e) {
            // open a JFileChooser
            JFileChooser jfc = new JFileChooser();
            jfc.setDialogTitle("Select Output Directory");
            jfc.setMultiSelectionEnabled(false);
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showOpenDialog(ConvertPeakListGUIPanel.this);
            
            // get the files
            File file = jfc.getSelectedFile();
            if (file == null) {
                return;
            }
            // update the directory
            outputDirectory = file;
            
            // update the text
            this.setText(outputDirectory.getAbsolutePath());
            
            // save the properties
            saveProperties();
            
            // update the steps
            stepsIndex = 3;
            ConvertPeakListGUIPanel.this.repaint();
        }
    }
    
    public boolean isRunHelperThread() {
        return runHelperThread;
    }
    
    public void setRunHelperThread(boolean runHelperThread) {
        this.runHelperThread = runHelperThread;
    }
    
}


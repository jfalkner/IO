/*
 *    Copyright 2004-2005 University of Michigan
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

import org.proteomecommons.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * This is a simple program that converts peak lists from one format to another using a GUI.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ConvertPeakListGUI {
	public static void main(String[] args) {
		// flags
		boolean normalize = false;
		boolean sort = false;

		// open a JFileChooser
		JFileChooser jfc = new JFileChooser();
		jfc.setName("Select File(s) to Convert");
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.showOpenDialog(null);

		// get the files
		File[] files = jfc.getSelectedFiles();
		if (files == null) {
			return;
		}

		// open up the convert frame
		ChooseFormat cf = new ChooseFormat();
		cf.setVisible(true);
		// loop while it is visible
		while (cf.isVisible()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// noop
			}
		}

		// get format
		String extension = (String) PeakListWriter.getFormats().get(cf.format);

		// handle concats
		if (cf.concat) {
			try {
				// make the writer
				String filename = files[0].getParentFile().getAbsolutePath()
						+ File.separatorChar + "concat" + extension;
				PeakListWriter writer = PeakListWriter.newWriter(filename);
				// convert each file
				for (int i = 0; i < files.length; i++) {
					PeakList p = PeakListReader
							.read(files[i].getAbsolutePath());
					writer.write(p);
				}
				writer.finish();
			} catch (Exception e) {
				// pop up a dialog
				Finish finish = new Finish("Conversion Failed.");
				finish.setVisible(true);
				return;
			}
		} else {
			// convert each file
			for (int i = 0; i < files.length; i++) {
				PeakList p = PeakListReader.read(files[i].getAbsolutePath());
				PeakListWriter.write(p, files[i].getAbsolutePath() + extension);
			}
		}
		
		// pop up a dialog
		Finish finish = new Finish("Conversion Complete.");
		finish.setVisible(true);
	}
}

class Finish extends JFrame {
	public Finish(String text) {
		// use grid layout
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets= new Insets(10,10,10,10);

		// text
		JTextArea jta = new JTextArea();
		jta.setEditable(false);
		jta.setOpaque(false);
		jta.setText(text);
		getContentPane().add(jta, gbc);
		pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// center it
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension d = t.getScreenSize();
		setLocation((int)(d.width/2-getWidth()/2), (int)(d.height/2-getHeight()/2));
	}
}

// helper class for choosing formats
class ChooseFormat extends JFrame implements ActionListener {
	boolean concat = false;

	String format = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// catch button events
		Object o = e.getSource();
		// look for toggle button clicks
		if (o instanceof JToggleButton) {
			concat = !concat;
		}
		// look for normal button clicks
		if (o instanceof JButton) {
			JButton jb = (JButton) o;
			format = jb.getText();
			setVisible(false);
			dispose();
		}

	}

	public ChooseFormat() {
		// use grid layout
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		// add some text
		JTextArea text = new JTextArea(
				"Please select the format you'd like to convert to and optionally select if you'd like all files to be concatenated in to a single peak list.");
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setEditable(false);
		text.setBackground(getBackground());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weighty = 1;
		gbc.weightx = 0;
		getContentPane().add(text, gbc);

		// add a button for each format
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		Map formats = PeakListWriter.getFormats();
		Set keys = formats.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String name = (String) i.next();
			String extension = (String) formats.get(name);
			// make a button
			JButton button = new JButton(name);
			button.addActionListener(this);
			gbc.ipady = 4;
			getContentPane().add(button, gbc);
		}

		gbc.insets = new Insets(5, 5, 5, 5);
		// add an option for concat to single peak list
		JToggleButton concat = new JToggleButton("Convert to single peak list");
		concat.addActionListener(this);
		getContentPane().add(concat, gbc);

		// kill on exit
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// pack
		pack();
		// can't resize
		setResizable(false);

		// center it
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension d = t.getScreenSize();
		setLocation((int)(d.width/2-getWidth()/2), (int)(d.height/2-getHeight()/2));
	}
}
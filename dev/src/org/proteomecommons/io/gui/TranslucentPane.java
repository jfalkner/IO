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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class TranslucentPane extends JPanel {
    public TranslucentPane(String label, JComponent component) {
        this(label, component, true, 5);
    }
    
    public TranslucentPane(String label, JComponent component, boolean fill) {
        this(label, component, fill, 5);
    }
    
    public TranslucentPane(String label, JComponent component, boolean fill, int leftInset) {
        // set the background color to be translucent
//        setBackground(new Color(225, 225, 225, 125));
        setOpaque(false);
        
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        // constraint
        GridBagConstraints gbc = new GridBagConstraints();
        
        // style the layout for the label
        gbc.gridwidth = gbc.REMAINDER;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = gbc.NORTHWEST;
        gbc.insets = new Insets(0, 5, 0, 5);
        
        // make a label
        JLabel selectedFilesLabel = new JLabel(label);
        selectedFilesLabel.setOpaque(false);
        selectedFilesLabel.setForeground(new Color(0, 0, 75));
        selectedFilesLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectedFilesLabel.setAlignmentY(selectedFilesLabel.LEFT_ALIGNMENT);
        // add to a panel
        JPanel box = new WhiteBox(selectedFilesLabel);
        add(box, gbc);
        
        if (fill) {
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.fill = gbc.BOTH;
        }
        gbc.insets = new Insets(5, leftInset, 5, 5);
        
        // text area for data
        add(component, gbc);
    }
}

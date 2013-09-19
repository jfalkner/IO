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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class WhiteBox extends JPanel {
    public WhiteBox(Component c) {
        this(c, new Insets(3, 3, 3, 3));
    }
    
    public WhiteBox(Component c, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        gbc.insets = insets;
        setBackground(new Color(255,255,255, 225));
        add(c, gbc);
//        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.GRAY));
    }
    
//    public void paint(Graphics g) {
//        g.setColor(Color.GRAY);
//        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
//        g.setColor(new Color(255, 255, 255, 225));
//        g.fillRoundRect(2, 2, getWidth()-5, getHeight()-5, 10, 10);
//
//        this.paintChildren(g);
//    }
}

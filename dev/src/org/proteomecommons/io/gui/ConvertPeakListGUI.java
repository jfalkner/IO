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
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ConvertPeakListGUI extends JFrame implements ComponentListener, MouseMotionListener, MouseListener, WindowListener {
    // track of last move
    int x = -1;
    int y = -1;
    double scrWidth = 0, scrHeight = 0;
    long lastChange = System.currentTimeMillis();
    boolean pressed = false;
    JPopupMenu pop = new JPopupMenu();
    Border on = BorderFactory.createLineBorder(Color.BLACK, 1);
    Border off = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    ConvertPeakListGUIPanel panel;

    public static void main(String[] args) {
        ConvertPeakListGUI gui = new ConvertPeakListGUI();
        // center it
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        // check that we're not drawing larger than the screen
        int widthCheck = gui.getWidth();
        int heightCheck = gui.getHeight();
        gui.scrWidth = d.getWidth();
        gui.scrHeight = d.getHeight();
        if (d.getWidth() < widthCheck) {
            widthCheck = (int) d.getWidth();
        }
        if (d.getHeight() < gui.getHeight()) {
            heightCheck = (int) d.getHeight();
        }
        gui.setSize(widthCheck, heightCheck);
        gui.setLocation((int) d.getWidth() / 2 - gui.getWidth() / 2, (int) d.getHeight() / 2 - gui.getHeight() / 2);
        gui.setVisible(true);

    }

    public ConvertPeakListGUI() {
        super("ProteomeCommons.org IO Framework");

        // dispose on close
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // add the panel
        panel = new ConvertPeakListGUIPanel();
        panel.setBorder(off);
        this.add(panel);

        // set the size
        setSize(700, 500);

        this.addComponentListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.addWindowListener(this);

        this.setUndecorated(true);

        pop.add(new ExitMenuItem());
        pop.add(new SpinMenuItem());
        pop.add(new UpdateBackgroundMenuItem());
        pop.add(new ToggleHelperThreadMenuItem());

    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        if (!pressed) {
            x = (int) (e.getComponent().getLocationOnScreen().getX() + e.getX());
            y = (int) (e.getComponent().getLocationOnScreen().getY() + e.getY());
        }
    }

    public void mouseDragged(MouseEvent e) {
        int x = this.getLocation().x + e.getPoint().x;
        int y = this.getLocation().y + e.getPoint().y;

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + this.getWidth() > (int) scrWidth) {
            x = (int) (scrWidth - this.getWidth());
        }
        if (y + this.getHeight() > (int) scrHeight) {
            y = (int) (scrHeight - this.getHeight());
        }

        long currentTime = System.currentTimeMillis();
        if (this.x != -1 && this.y != -1) {
            //Point p = this.getLocation();
            //this.setLocation((int)(p.getX()+x-this.x), (int)(p.getY()+y-this.y));
            this.setLocation(x, y);
        }

        if (currentTime - lastChange > 100) {
            // update x/y values
            this.x = x;
            this.y = y;
            lastChange = currentTime;
        }
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        pressed = true;
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != e.BUTTON1) {
            pop.show(this, e.getX(), e.getY());
        }
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    // menu item for exiting
    class ToggleHelperThreadMenuItem extends JMenuItem implements ActionListener {

        ToggleHelperThreadMenuItem() {
            super("Toggle Helper");
            addActionListener(this);
            setBackground(Color.WHITE);
        }

        public void actionPerformed(ActionEvent e) {
            ConvertPeakListGUI.this.panel.setRunHelperThread(!ConvertPeakListGUI.this.panel.isRunHelperThread());
        }
    }

    // menu item for exiting
    class SpinMenuItem extends JMenuItem implements ActionListener {

        SpinMenuItem() {
            super("Spin Gear");
            addActionListener(this);
            setBackground(Color.WHITE);
        }

        public void actionPerformed(ActionEvent e) {
            ConvertPeakListGUI.this.panel.setSpinBackground(!ConvertPeakListGUI.this.panel.isSpinBackground());
        }
    }

    // menu item for updating the background
    class UpdateBackgroundMenuItem extends JMenuItem implements ActionListener {

        UpdateBackgroundMenuItem() {
            super("Update Background");
            addActionListener(this);
            setBackground(Color.WHITE);
        }

        public void actionPerformed(ActionEvent e) {
            // hide
            ConvertPeakListGUI.this.setVisible(false);
            // update screenshot
            try {
                ConvertPeakListGUI.this.panel.updateBackground();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ConvertPeakListGUI.this, "Can't update background.");
            }
            // hide
            ConvertPeakListGUI.this.setVisible(true);
        }
    }
    // a menu item that stops the help thread
}

// menu item for exiting
class ExitMenuItem extends JMenuItem implements ActionListener {

    ExitMenuItem() {
        super("Exit IO Framework");
        addActionListener(this);
        setBackground(Color.WHITE);
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(1);
    }
}

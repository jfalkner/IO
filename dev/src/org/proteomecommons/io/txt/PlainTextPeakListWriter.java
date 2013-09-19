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
package org.proteomecommons.io.txt;

import org.proteomecommons.io.*;

import java.io.*;

/**
 * A writer that dumps peak list data in plain text. This writer is intended for the simple I-know-what-it-is-and-I-want-to-read-the-data cases. In many ways the format is good for nothing, but it is a simple way to dump hard-to-read data in to a simple text format. You will lose everything except the m/z and intensity pairs of the data.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class PlainTextPeakListWriter extends GenericPeakListWriter {
    private boolean firstPeakList = true;
    
    private String newline = "\n";
    
    private String spacer = "\t";
    
    FileWriter fw;
    BufferedWriter bw;
    
    public void setNewline(String newline) {
        this.newline = newline;
    }
    
    public void setSpacer(String spacer) {
        this.spacer = spacer;
    }
    
    public PlainTextPeakListWriter(String filename) {
        super(filename);
        try {
            File file = new File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't create file "+filename, ex);
        }
    }
    
    public void write(PeakList peaklist) {
        try {
            // space between peak lists
            if (!firstPeakList) {
                bw.write(newline);
            } else {
                firstPeakList = false;
            }
            
            Peak parent = peaklist.getParentPeak();
            if (parent != null) {
                // write m/z
                if (parent.getMassOverCharge() != Peak.UNKNOWN_MZ) {
                    bw.write(Double.toString(parent.getMassOverCharge()));
                } else {
                    bw.write("?");
                }
                // write intensity
                if (parent.getIntensity() != Peak.UNKNOWN_INTENSITY) {
                    bw.write(spacer+Double.toString(parent.getIntensity()));
                } else {
                    bw.write(spacer+"?");
                }
                // write charge
                if (parent.getCharge() != Peak.UNKNOWN_CHARGE) {
                    bw.write(spacer+parent.getCharge());
                } else {
                    bw.write(spacer+"?");
                }
                // write the header line
                bw.write(newline);
            }
            
            // write out all the peaks
            Peak[] peaks = peaklist.getPeaks();
            for (Peak p : peaks) {
                bw.write(p.getMassOverCharge()+spacer+p.getIntensity()+newline);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close() {
        try { bw.flush(); } catch (Exception e){}
        try { bw.close(); } catch (Exception e){}
        try { fw.flush(); } catch (Exception e){}
        try { fw.close(); } catch (Exception e){}
        
        super.close();
    }
    
    
}
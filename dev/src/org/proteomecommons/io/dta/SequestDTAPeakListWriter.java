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
package org.proteomecommons.io.dta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.proteomecommons.jaf.*;
import org.proteomecommons.io.*;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * A simple writer class that can serialize peak list files DTA format. format.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDTAPeakListWriter extends GenericPeakListWriter implements OnlyTandemPeakListWriter {
    // keep track of the first peak list
    private boolean isFirstPeakList = true;
    
    // make the writers
    FileWriter fw;
    BufferedWriter bw;
    
    // keep track of the number of files made
    int peakListsMade = 0;
    
    private boolean explode = true;
    private boolean ignoreMS = true;
    
    // keep track of the current file's name
    File file = null;
    
    private int minCharge = 1;
    private int maxCharge = 3;
    
    public SequestDTAPeakListWriter(String filename) {
        super(filename);
        try {
            // make any required directories
            file = new File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            // make the writers
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't write to file "+filename);
        }
    }
    
    /**
     * Simple method to write a MGF file from a peak list.
     *
     * @param peaklist
     */
    public void write(PeakList peaklist) {
        // inc the peak list authored count
        peakListsMade++;
        
        // ignore if this is MS data
        if (ignoreMS && peaklist.getParentPeak() == null) {
            return;
        }
        
        // if this isn't the first, make a new file
        if (peakListsMade > 1 && isExplode()) {
            try {
                String newFilename = getFilename().split("\\.dta")[0]+"."+peakListsMade+".dta";
                changeFiles(newFilename);
                // reset first peak list
                isFirstPeakList = true;
            } catch (IOException ex) {
                throw new RuntimeException("Can't make peak list "+file+"!");
            }
        }
        
        // write the spacer
        if (!isFirstPeakList) {
            try {
                bw.write("\n");
            } catch (IOException ex) {
                throw new RuntimeException("Can't write new-line for next peak list!?");
            }
        } else {
            isFirstPeakList = false;
        }
        
        try {
            // optional write tandem MS headers
            if (peaklist.getParentPeak() == null) {
                throw new IncompatibleFormatException("DTA format requires a tandem peak list.");
            }
            
            // get the parent
            Peak parent = peaklist.getParentPeak();
            
            // check m/z
            if (parent.getMassOverCharge() == Peak.UNKNOWN_MZ) {
                throw new IncompatibleFormatException("DTA format requires a known m/z. Can't fix the problem.");
            }
            
            // check if the charge is known
            if (parent.getCharge() != Peak.UNKNOWN_CHARGE) {
                // write out the known charge
                bw.write(calcDTAParentIonMass(parent) + " " + parent.getCharge() + "\n");
                // write out the peaks
                Peak[] peaks = peaklist.getPeaks();
                for (int i = 0; i < peaks.length; i++) {
                    // skipp nulls
                    if (peaks[i] == null){
                        continue;
                    }
                    // write the peak
                    bw.write(peaks[i].getMassOverCharge() + " " + peaks[i].getIntensity() + "\n");
                }
                bw.flush();
                
            } else {
                // make a peak list of up to +3 charge
                for (int guessedCharge=minCharge;guessedCharge<=maxCharge;guessedCharge++) {
                    // make a new file
                    String newFile = getFilename().split("\\.dta")[0]+"."+peakListsMade+"."+guessedCharge+".dta";
                    changeFiles(newFile);
                    // make a fake parent peka
                    GenericPeak fakeParent = new GenericPeak();
                    fakeParent.setCharge(guessedCharge);
                    fakeParent.setMassOverCharge(parent.getMassOverCharge());
                    // write out the known charge
                    bw.write(calcDTAParentIonMass(fakeParent) + " " + guessedCharge + "\n");
                    // write out the peaks
                    Peak[] peaks = peaklist.getPeaks();
                    for (int i = 0; i < peaks.length; i++) {
                        // skipp nulls
                        if (peaks[i] == null){
                            continue;
                        }
                        // write the peak
                        bw.write(peaks[i].getMassOverCharge() + " " + peaks[i].getIntensity() + "\n");
                    }
                    bw.flush();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void changeFiles(final String newFilename) throws IOException {
        // flush/close
        bw.flush();
        fw.flush();
        bw.close();
        fw.close();
        
        // delete 0 length files
        if (file.length() == 0) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
        
        // make a new file
        file = new File(newFilename);
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
    }
    
    /**
     * Helper method that calculates what the DTA format is supposed to show for parent ion mass.
     * @param p
     * @return
     */
    private double calcDTAParentIonMass(Peak p) {
        return (p.getMassOverCharge() * p.getCharge() - (p.getCharge() - 1) * Atom.H.getMassInDaltons());
    }
    
    public void close() {
        try { bw.flush(); bw.close(); } catch (Exception e){}
        try { fw.flush(); fw.close(); } catch (Exception e){}
    }
    
    public boolean isExplode() {
        return explode;
    }
    
    public void setExplode(boolean explode) {
        this.explode = explode;
    }
    
    public boolean isIgnoreMS() {
        return ignoreMS;
    }
    
    public void setIgnoreMS(boolean ignoreMS) {
        this.ignoreMS = ignoreMS;
    }

    public int getMinCharge() {
        return minCharge;
    }

    public void setMinCharge(int minCharge) {
        this.minCharge = minCharge;
    }

    public int getMaxCharge() {
        return maxCharge;
    }

    public void setMaxCharge(int maxCharge) {
        this.maxCharge = maxCharge;
    }
}
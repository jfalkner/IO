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
package org.proteomecommons.io.pkl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.proteomecommons.io.*;
import org.proteomecommons.io.PeakList;

/**
 * A simple writer class that can serialize peak list files in micromass PKL
 * format.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class MicromassPKLPeakListWriter extends GenericPeakListWriter implements OnlyTandemPeakListWriter {
    
    // Flag to indicate if the current line is between newlines
    private boolean used = false;
    
    // make the streams
    FileWriter fw;
    BufferedWriter bw;
    
    private boolean ignoreMS = true;
    
    public MicromassPKLPeakListWriter(String filename) {
        super(filename);
        
        try {
            File file = new File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            throw new RuntimeException("Can't make file "+filename, e);
        }
    }
    
    /**
     * This method does nothing. PKL files are always MSMS.
     */
    public int getTandemCount() {
        return 2;
    }
    
    /**
     * Simple method to write a MGF file from a peak list.
     *
     * @param peaklist
     */
    public void write(PeakList peaklist) {
        // ignore MS if instructed
        if (ignoreMS && peaklist.getParentPeak() == null) {
            return;
        }
        
        // check that it is a tandem peak list
        if (peaklist.getParentPeak() == null) {
            throw new IncompatibleFormatException("The PKL format only supports MSMS data. You did not provide a parent m/z and intensity.");
        }
        // check that the parent has charge
        Peak peak = peaklist.getParentPeak();
        if (peak.getCharge() == Peak.UNKNOWN_CHARGE || peak.getMassOverCharge() == Peak.UNKNOWN_MZ) {
            throw new IncompatibleFormatException("You must provide parent ion charge, mz, and intensity information for the PKL format.");
        }
        // check on intensity, default to 1
        if (peak.getIntensity() == Peak.UNKNOWN_INTENSITY) {
            GenericPeak gp = new GenericPeak();
            gp.setIntensity(1);
            gp.setCharge(peak.getCharge());
            gp.setMassOverCharge(peak.getMassOverCharge());
            gp.setCentroided(peak.getCentroided());
            gp.setMonoisotopic(peak.getMonoisotopic());
            gp.setAveraged(peak.getAveraged());
            // set peak to use the new intensity info
            peak = gp;
        }
        
        try {
            // put in the optional spacer
            if (used) {
                bw.write("\n");
            }
            used = true;
            
            // write the headers
//            bw.write(calcParentIonMZ(peak) + " " + peak.getIntensity() + " " + peak.getCharge() + "\n");
            bw.write(peak.getMassOverCharge() + " " + peak.getIntensity() + " " + peak.getCharge() + "\n");
            
            // write out the peaks
            Peak[] peaks = peaklist.getPeaks();
            for (int i = 0; i < peaks.length; i++) {
                bw.write(peaks[i].getMassOverCharge() + " "
                        + peaks[i].getIntensity() + "\n");
            }
            bw.write("\n"); // End of Peaklist marker
            
            // flush
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
//    /**
//     * Helper method to calc the appropriate parent ion m/z.
//     *
//     * @return
//     */
//    private double calcParentIonMZ(Peak peak) {
//        return (peak.getMassOverCharge() * peak.getCharge() - (peak.getCharge() - 1) * Atom.H.getMassInDaltons());
//    }
    
    public void close() {
        // close the streams
        try { bw.flush(); } catch (Exception e){}
        try { bw.close(); } catch (Exception e){}
        try { fw.flush(); } catch (Exception e){}
        try { fw.close(); } catch (Exception e){}
        
        super.close();
    }

    public boolean isIgnoreMS() {
        return ignoreMS;
    }

    public void setIgnoreMS(boolean ignoreMS) {
        this.ignoreMS = ignoreMS;
    }
    
}
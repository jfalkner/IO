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
package org.proteomecommons.io.wiff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import org.proteomecommons.io.*;

/**
 * A reader class that is capable of handling AnalystQS WIFF to CSV exports.
 * Note that this class does not natively read WIFF files. You must first use
 * AnalystQS to export the WIFF in to CSV format.
 *
 * TODO: Return monoisotopic, centroided peak lists.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class AnalystQSWIFFToCSVPeakListReader extends GenericPeakListReader {
    FileReader fr;
    BufferedReader br;
    
    
    public AnalystQSWIFFToCSVPeakListReader(String filename) {
        super(filename);
        
        // parse the header information
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            
            // purge up to the spectra
            for (String s = br.readLine(); s != null && !s.equals("[spectra]"); s = br.readLine()) {
                // kill the line.
                // TODO: save the meta information!
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#close()
     */
    public void close() {
        try { br.close(); } catch (Exception e) {}
        try { fr.close(); } catch (Exception e) {}
    }
    
    public PeakList getPeakList() {
        try {
            // read the next link
            String line = br.readLine();
            // if null, there are no more
            if (line == null || line.trim().equals("")) {
                return null;
            }
            
            // buffer all peaks
            LinkedList<Peak> peaks = new LinkedList<Peak>();
            
            // split the lines
            String[] split = line.split(",");
            // handle the peaks
            for (int i = 7; i < split.length; i += 2) {
                // parse the next peak
                GenericPeak p = new GenericPeak();
                p.setMassOverCharge(Double.parseDouble(split[i]));
                p.setIntensity(Double.parseDouble(split[i + 1]));
                p.setCentroided(Peak.CENTROIDED);
                peaks.add(p);
            }
            
            // return a peak list
            GenericPeakList gpl = new GenericPeakList();
            gpl.setPeaks(peaks.toArray(new Peak[0]));
            
            return gpl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
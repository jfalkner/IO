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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class PlainTextPeakListReader extends GenericPeakListReader {
    FileReader fr;
    BufferedReader br;
    public PlainTextPeakListReader(String filename) {
        super(filename);
        
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void close() {
        try { br.close(); } catch (Exception e){}
        try { fr.close(); } catch (Exception e){}
    }
    
    public PeakList getPeakList() {
        // make a new peak list
        GenericPeakList peaklist = new GenericPeakList();
        // keep the peaks around
        List<Peak> peaks = new LinkedList();
        
        try {
            String line = br.readLine();
            while(line != null && line.trim().equals("")) {
                line = br.readLine();
            }
            
            // return null if null
            if (line == null) {
                return null;
            }
            
            // parse the first line as parent ion information
            String[] parts = line.split("\\s");
            if (parts.length > 2) {
                GenericPeak precursor = new GenericPeak();
                peaklist.setParentPeak(precursor);
                // try to keep the first as parent m/z
                try {
                    precursor.setMassOverCharge(Double.parseDouble(parts[0]));
                } catch (Exception e){
                    // noop
                }
                try {
                    precursor.setIntensity(Double.parseDouble(parts[1]));
                } catch (Exception e){
                    // noop
                }
                try {
                    precursor.setCharge(Integer.parseInt(parts[2]));
                } catch (Exception e){
                    // noop
                }
                // buffer a new line
                line = br.readLine();
            }
            
            // read the next peak list
            for (;line != null; line = br.readLine()) {
                // parse the line
                if (line == null || line.trim().equals("")) {
                    break;
                }
                
                // else, parse the peak
                GenericPeak peak = new GenericPeak();
                peaks.add(peak);
                String[] split = line.split("\\s");
                // conditionally take the first as m/z
                if (split.length > 0) {
                    try {
                        peak.setMassOverCharge(Double.parseDouble(split[0]));
                    } catch (Exception e){
                        // noop
                    }
                }
                // conditionally take the first as intensity
                if (split.length > 1) {
                    try {
                        peak.setIntensity(Double.parseDouble(split[1]));
                    } catch (Exception e){
                        // noop
                    }
                }
                // conditionally take the first as charge
                if (split.length > 2) {
                    try {
                        peak.setCharge(Integer.parseInt(split[2]));
                    } catch (Exception e){
                        // noop
                    }
                }
            }
            
            // return the peak lsit
            peaklist.setPeaks(peaks.toArray(new Peak[0]));
            return peaklist;
        } catch (IOException ex) {
            throw new RuntimeException("File prematurely closed. Can't read peak list.");
        }
    }
}

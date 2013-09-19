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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.proteomecommons.io.*;
import org.proteomecommons.io.Peak;

/**
 * A PeakListReader implementation that reads Micromass PKL files.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MicromassPKLPeakListReader extends GenericPeakListReader {
    FileReader fr;
    BufferedReader br;
    
    /**
     * Public constructor.
     *
     * @param fileName
     *            Name of the PKL file to parse.
     * @throws IOException
     */
    public MicromassPKLPeakListReader(String filename) {
        super(filename);
        
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#close()
     */
    public void close() {
        try {br.close(); } catch (Exception e){}
        try {fr.close(); } catch (Exception e){}
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#getPeakList()
     */
    public PeakList getPeakList(){
        try {
            // parse the parent
            GenericPeak parent = new GenericPeak();
            
            // handle meta-information
            String string = br.readLine();
            if (string == null) {
                return null;
            }
            // skip blank lines
            if (string.trim().equals("")) {
                return getPeakList();
            }
            
            // split the string
            String parts[] = string.split("[\\s,]");
            
            // set the parent ion mass
            double mz = Double.parseDouble(parts[0]);
            parent.setMassOverCharge(mz);
            // try to handle charge
            parent.setIntensity(Double.parseDouble(parts[1]));
            // try to handle charge
            parent.setCharge(Integer.parseInt(parts[2]));
            
            LinkedList<Peak> peaks = new LinkedList<Peak>();
            for (String line = br.readLine();line != null; line = br.readLine()){
                // stop on blank lines
                if (line.trim().equals("")) {
                    break;
                }
                
                // process ions
                String[] split = line.split("[\\s,]");
                
                // parse the m/z
                double massOverChargeInDaltons;
                try {
                    massOverChargeInDaltons = Double.parseDouble(split[0]);
                } catch (NumberFormatException e) {
                    throw new InvalidFileFormatException(split[0]+" is not a valid number.");
                }
                
                // parse the intensity
                double intens;
                try {
                    intens = Double.parseDouble(split[1]);
                } catch (NumberFormatException e) {
                    throw new InvalidFileFormatException(split[1]+" is not a valid number.");
                }
                
                // return the appropriate fragment
                GenericPeak gp = new GenericPeak();
                gp.setMassOverCharge(massOverChargeInDaltons);
                gp.setIntensity(intens);
                
                // add to the list of peaks
                peaks.add(gp);
            }
            
            // make the peak list
            GenericPeakList gpl = new GenericPeakList();
            gpl.setParentPeak(parent);
            gpl.setPeaks(peaks.toArray(new Peak[0]));
            return gpl;
        } catch (Exception e) {
            throw new InvalidFileFormatException(e);
        }
    }
}
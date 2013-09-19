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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.*;
import org.proteomecommons.jaf.Atom;

/**
 * A PeakListReader implementation that reads Sequence DTA files. m/z values are
 * adjusted to the mass that a mass spec would observe.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class SequestDTAPeakListReader extends GenericPeakListReader {
    FileReader fr;
    BufferedReader br;
    
    // count for peak lists read
    int peakListsRead = 0;
    
    
    
    /**
     * @see org.proteomecommons.io.PeakListReader#close()
     */
    public void close() {
        try { br.close(); } catch (Exception e) {}
        try { fr.close(); } catch (Exception e) {}
    }
    
    /**
     * Public constructor.
     *
     * @param fileName
     *            Name of the DTA file to parse.
     * @throws IOException
     */
    public SequestDTAPeakListReader(String filename) {
        super(filename);
        try {
            // make a file reader
            fr = new FileReader(filename);
            // load the file reference
            br = new BufferedReader(fr);
        } catch (Exception e) {
            throw new InvalidFileFormatException("Can't parse headers from DTA file!");
        }
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#next()
     */
    public PeakList getPeakList() {
        // inc number of peak lists read
        peakListsRead++;
        
        try {
            // get the first line
            String line = br.readLine();
            
            // flag for use unknown charge
            boolean useUnknownCharge = false;
            
            // check for null
            if (line == null) {
                // conditionally open the next file
                if (peakListsRead > 1) {
                    File file = new File(this.getName().split("\\.dta")[0]+"."+peakListsRead+".dta");
                    if (file.exists()) {
                        // close the old readers
                        br.close();
                        fr.close();
                        
                        // make a new reader
                        fr = new FileReader(file);
                        br = new BufferedReader(fr);
                        
                        // read the line
                        line = br.readLine();
                        
                        // flag the use of unknown charge
                        useUnknownCharge = true;
                    } else {
                        return null;
                    }
                } else {
                    // check for a new peak list
                    return null;
                }
            }
            
            // if the line is blank, return a new one
            if (line.trim().equals("")) {
                return getPeakList();
            }
            
            // split by whitespace
            String[] parts = line.split("\\s");
            if(parts.length < 2) {
                throw new InvalidFileFormatException("The first line of a DTA file must include a m/z and charge pair.");
            }
            
            // make a new TandemPeakList
            GenericPeakList gtpl = new GenericPeakList();
            
            // set the parent peak
            DTAPeak parent = new DTAPeak();
            //  mass is always singley protenated ion
            double mass = Double.parseDouble(parts[0]);
            int charge = Integer.parseInt(parts[1]);
            if (!useUnknownCharge) {
                parent.setCharge(charge);
            }
            // subtract the proton
            mass -= Atom.H.getMassInDaltons();
            // add the proton weights for the charge
            mass = mass + Atom.H.getMassInDaltons()*charge;
            // divide by charge for the final mass
            mass /= charge;
            
            // adjust parent ion -- convert to what a mass spec would observe
            parent.setMassOverCharge(mass);
            
            // set the parent ion
            gtpl.setParentPeak(parent);
            
            
            // set all the other peaks
            LinkedList<Peak> peaks = new LinkedList();
            for (line = br.readLine(); line != null; line = br.readLine()) {
                // read a line
                if (line == null || line.trim().equals("")) {
                    break;
                }
                
                // split
                parts = line.split("\\s");
                if(parts.length < 2) {
                    throw new InvalidFileFormatException("A line of a DTA file must include a m/z and intensity pair.");
                }
                
                //  set the first entry as parent ion mass and the associated charge
                double massOverChargeInDaltons;
                double intensity;
                try {
                    massOverChargeInDaltons = Double.parseDouble(parts[0]);
                } catch (NumberFormatException nfe) {
                    throw new InvalidFileFormatException("Expected a number but found the value '"+parts[0]+"'");
                }
                
                try {
                    intensity = Double.parseDouble(parts[1]);
                } catch (NumberFormatException nfe) {
                    throw new InvalidFileFormatException("Expected a number but found the value '"+parts[1]+"'");
                }
                
                // return the appropriate fragment
                GenericPeak p = new GenericPeak();
                p.setMassOverCharge(massOverChargeInDaltons);
                p.setIntensity(intensity);
                peaks.add(p);
            }
            
            // set the peaks
            gtpl.setPeaks(peaks.toArray(new Peak[0]));
            
            // return the peak list
            return gtpl;
        } catch (IOException e) {
            throw new InvalidFileFormatException("Invalid DTA file format!", e);
        }
    }
}
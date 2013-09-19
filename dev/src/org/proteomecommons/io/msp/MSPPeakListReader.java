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
package org.proteomecommons.io.msp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;

/**
 * Reader functionality for MSP files from stein's NIST work
 * @author Jarret - jar@cs.washington.edu
 */
public class MSPPeakListReader extends GenericPeakListReader{
    FileReader fr;
    BufferedReader br;
    
    /** Creates a new instance of MSPPeakListReader */
    public MSPPeakListReader(String filename) {
        super(filename);
        
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close() {
        try{ fr.close(); } catch (Exception e) {}
        try{ br.close(); } catch (Exception e) {}
    }
    
    public PeakList getPeakList()  {
        
        HashSet<String> names = new HashSet<String>();
        ArrayList<GenericPeak> peaks = new ArrayList<GenericPeak>();
        // handle the parent peak
        GenericPeak parent = new GenericPeak();
        
        try{
            for(String data = br.readLine(); data != null; data = br.readLine()){
                if(data.trim().equals("")){
                    //done
                    break;
                } else if(data.startsWith("Comment")){
                    // parse the parent m/z
                    String[] split = data.split("\t");
                    // find the parent
                    for (String s : split) {
                        if (s.startsWith("Parent")) {
                            // split
                            String[] parts = s.split("=");
                            // parse the parent value
                            parent.setMassOverCharge(Double.parseDouble(parts[1]));
                        }
                    }
                } else if(data.startsWith("MW") || data.startsWith("Nu")){
                    //ignore mass wegiht, and num peaks
                }
                
                else if(data.startsWith("Name:")){
                    //parse out the pep
                    names.add(data.substring(5).trim().split("/")[0]);
                    //System.out.println("names: " + names.toString());
                    // set the charge
                    parent.setCharge(Integer.parseInt(data.split("/")[1]));
                } else {
                    //try parsing out a peak
                    try{
                        String[] mzInten = data.split("\\t");
                        double mz = Double.parseDouble(mzInten[0]);
                        double inten = Double.parseDouble(mzInten[1]);
                        GenericPeak gp = new GenericPeak();
                        gp.setIntensity(inten);
                        gp.setMassOverCharge(mz);
                        gp.setCharge(1); //? make jayson chekc
                        peaks.add(gp);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch(IOException ioe){
            throw new RuntimeException(ioe);
        }
        
        //makeup a peaklist
        if(names.size()>0 || peaks.size() > 0){
            GenericPeakList pl = new MSPPeakList(peaks.toArray(new GenericPeak[0]), new LinkedList<String>(names));
            pl.setParentPeak(parent);
            return pl;
        }
        
        return null;
    }
    
}
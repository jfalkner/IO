/*
 *    Copyright 2004 Jayson Falkner
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
package org.proteomecommons.io.mgf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * A simple writer class that can serialize peak list files in MGF format,
 * optionally saving the meta-information.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class MascotGenericFormatPeakListWriter extends GenericPeakListWriter {
    OutputStream out;
    
    // don't add whitespace for the first peak list
    private boolean firstPeakList = true;
    
    // the writers
    FileWriter fw;
    BufferedWriter osw;
    
    public MascotGenericFormatPeakListWriter(String filename) {
        super(filename);
        try {
            // make the file/writers
            File file = new File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            fw = new FileWriter(file);
            osw = new BufferedWriter(fw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't write to file "+filename, ex);
        }
    }
    
    /**
     * Simple method to write a MGF file from a peak list. Added meta-data at
     * peaklist level (title, Tol, TOlU, SEQ and COMP)
     *
     * @param peaklist
     */
    public void write(PeakList peaklist) {
        // if null, return
        if (peaklist == null){
            return;
        }
        // if this is an MGF peak list, write the meta-info
        try {
            // write the headers
            if (!firstPeakList) {
                osw.write("\n");
            } else {
                firstPeakList = false;
            }
            osw.write("BEGIN IONS\n");
            // if this is a mascot peaklist, override generic writing code
            if (peaklist instanceof MascotGenericFormatPeakList) {
                // write the meta info
                MascotGenericFormatPeakList mgf = (MascotGenericFormatPeakList) peaklist;
                
                // write the preserved charge string
                if (mgf.getCharge() != null) {
                    osw.write(mgf.getCharge()+"\n");
                } else if (mgf.getParentPeak() != null && mgf.getParentPeak().getCharge() != Peak.UNKNOWN_CHARGE){
                    //fake it
                    osw.write("CHARGE=");
                    int charge = mgf.getParentPeak().getCharge();
                    osw.write(Integer.toString(Math.abs(charge)));
                    if(charge > 0){
                        osw.write("+");
                    }
                    if(charge < 0)
                        osw.write("-");
                    //osw.write(Integer.toString(charge));
                    osw.newLine();
                }
                // write the preserved mass string
                if (mgf.getPepmass() != null) {
                    osw.write(mgf.getPepmass() + "\n");
                } else if (mgf.getParentPeak() != null && mgf.getParentPeak().getMassOverCharge() != Peak.UNKNOWN_MZ){
                    osw.write("PEPMASS=");
                    osw.write(Double.toString(mgf.getParentPeak().getMassOverCharge()));
                    osw.newLine();
                }
                if (mgf.getTitle() != null) {
                    osw.write("TITLE=" + mgf.getTitle() + "\n");
                }
            }
            // else consider it generic
            else {
                Peak p = peaklist.getParentPeak();
                // write the parent mass
                if (p != null && p.getMassOverCharge() != Peak.UNKNOWN_MZ) {
                    // optionally account for intensity information
                    String value = Double.toString(p.getMassOverCharge());
                    if (p.getIntensity() != Peak.UNKNOWN_INTENSITY) {
                        value += "\t"+p.getIntensity();
                    }
                    osw.write("PEPMASS=" + value + "\n");
                }
                
                // write out the charge in the expected format
                if(p != null && p.getCharge() != Peak.UNKNOWN_CHARGE) {
                    if (p.getCharge() >= 0) {
                        osw.write("CHARGE=" + Math.abs(p.getCharge())+ "+\n");
                    } else {
                        osw.write("CHARGE=" + Math.abs(p.getCharge())+ "-\n");
                    }
                }
            }
            
            // write out the peaks
            Peak[] peaks = peaklist.getPeaks();
            for (int i = 0; i < peaks.length; i++) {
                osw.write(peaks[i].getMassOverCharge() + "\t"
                        + peaks[i].getIntensity() + "\n");
            }
            
            // end the ions
            osw.write("END IONS\n");
            osw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close() {
        try { osw.flush(); } catch (Exception e){}
        try { osw.close(); } catch (Exception e){}
        try { fw.flush(); } catch (Exception e){}
        try { fw.close(); } catch (Exception e){}
    }
}
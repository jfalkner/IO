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
package org.proteomecommons.io.mgf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.proteomecommons.io.*;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakListReader;

/**
 * A PeakListReader implementation designed to handle files in Mascot Generic
 * Format (.mgf), a popular format for ABI instruments.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author Dominic Battre
 *
 */
public class MascotGenericFormatPeakListReader extends GenericPeakListReader {
    // buffered input for the peak list
    BufferedReader in;
    FileReader fr;
    
    //Embedded PeakListReader, instantiated if FORMAT other than mgf exists
    private PeakListReader embeddedReader = null;
    
    /**
     * @see org.proteomecommons.io.PeakListReader#close()
     */
    public void close() {
        try {
            in.close();
        } catch (Exception e) {
            //  noop
        } finally {
            in = null;
        }
    }
    
    public PeakList getPeakList() {
        try {
            // make a new peak list
            MascotGenericFormatPeakList peaklist = new MascotGenericFormatPeakList();
            
            // ll of all the peaks
            LinkedList<Peak> peaks = new LinkedList();
            
            boolean foundPeaks = false;
            
            // get the meta-info
            // handle meta-information
            String string = null;
            while ((string = in.readLine()) != null) {
                // make sure lines with a single white space are recognized as an
                string = string.trim();
                
                // if blank, get next
                if (string.equals("")){
                    continue;
                }
                
                // try to find parent mass info
                if (string.startsWith("PEPMASS")) {
                    // flag as tandem
                    peaklist.setTandemCount(2);
                    // preserve the string
                    peaklist.setPepmass(string);
                    continue;
                }
                
                // try to handle charge
                if (string.startsWith("CHARGE")) {
                    // flag as tandem
                    peaklist.setTandemCount(2);
                    // preserve the string
                    peaklist.setCharge(string);
                }
                
//            } else if (string.startsWith("SEQ")) {
//               string.substring(string.indexOf("=") + 1,
//                        string.length());
//            } else if (string.startsWith("COMP")) {
//                compositionQualifier = string.substring(
//                        string.indexOf("=") + 1, string.length());
//            }
                
                // handle the title information
                if (string.startsWith("TITLE")) {
                    // get the tentative title
                    String tentativeTitle = string.substring(string.indexOf("=") + 1, string.length());
                    if (tentativeTitle != null) {
                        tentativeTitle = tentativeTitle.trim();
                    }
                    // set the title
                    peaklist.setTitle(tentativeTitle);
                    continue;
                }
//            }
//            else if (string.startsWith("TOLU")) {
//                unitOfTolerance = string.substring(string.indexOf("=") + 1,
//                        string.length());
//            } else if (string.startsWith("TOL")) {
//                String tol = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                this.tolerance = Integer.parseInt(tol);
//
//            } else if (string.startsWith("ACCESSION")) {
//                accession = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("CLE")) {
//                enzyme = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("COM")) {
//                searchTitle = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("DB")) {
//                database = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("ERRORTOLERANT")) {
//                String st = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                errorTolerance = Integer.parseInt(st);
//                metaDataFound = true;
//            } else if (string.startsWith("FORMAT")) {
//                format = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("ICAT")) {
//                icat = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("INSTRUMENT")) {
//                ionSeries = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("ITOLU")) {
//                unitOfIonTolerance = string.substring(string.indexOf("=") + 1,
//                        string.length());
//                metaDataFound = true;
//            } else if (string.startsWith("ITOL")) {
//                ionTolerance = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("MASS")) {
//                mass = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("MODS")) {
//                fixedMods = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("IT_MODS")) {
//                variableMods = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("OVERVIEW")) {
//                reportOverview = string.substring(string.indexOf("=") + 1,
//                        string.length());
//                metaDataFound = true;
//            } else if (string.startsWith("PFA")) {
//                String st = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                partials = Integer.parseInt(st);
//                metaDataFound = true;
//
//            } else if (string.startsWith("PRECURSOR")) {
//                precursor = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("REPORT")) {
//                maxHits = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("REPTYPE")) {
//                reportType = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("SEARCH")) {
//                search = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("SEG")) {
//                proteinMass = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("TAXONOMY")) {
//                taxonomy = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("USEREMAIL")) {
//                userMail = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            } else if (string.startsWith("USERNAME")) {
//                userName = string.substring(string.indexOf("=") + 1, string
//                        .length());
//                metaDataFound = true;
//            }
                
                // handle peaks
                if (string.startsWith("BEGIN IONS")) {
                    // nadda, we're in a peak list
                    foundPeaks = true;
                }
                
                // check for blank peak lists
                if (string.contains("END IONS")) {
                    break;
                }
                
                // if the line starts with 1-9
                if (string.charAt(0) >= '1' && string.charAt(0) <= '9') {
                    // parse all peaks
                    for (;string.indexOf("END IONS") == -1 && string != null; string = in.readLine()) {
                        // try, skip bad lines
                        try {
                            // split on all whitespace
                            String[] split = string.split("\\s+");
                            //  set the first entry as parent ion mass and the associated charge
                            double massOverChargeInDaltons;
                            double intens;
                            try {
                                massOverChargeInDaltons = Double.parseDouble(split[0]);
                                intens = Double.parseDouble(split[1]);
                            } catch (Exception e) { // Any non-Peak data will return null (e.g. END
                                // IONS)
                                return null;
                            }
                            
                            // return the appropriate fragment
                            GenericPeak gp = new GenericPeak();
                            gp.setMassOverCharge(massOverChargeInDaltons);
                            gp.setIntensity(intens);
                            peaks.add(gp);
                        } catch (Exception e) {
                            // noop
                        }
                    }
                    // quit so that multiple peak lists aren't read at once
                    break;
                }
            }
            
            // if no peaks are found, exit
            if (!foundPeaks) {
                return null;
            }
            
            // finally, add all the peaks
            peaklist.setPeaks(peaks.toArray(new Peak[0]));
            return peaklist;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Make a new PeakListReader designed to read Mascot Generic Format peak
     * list files and point the reader to the file with the specified name.
     *
     * @param fileName
     * @throws IOException
     */
    public MascotGenericFormatPeakListReader(String filename) {
        super(filename);
        
        try {
            // load the file reference
            fr = new FileReader(filename);
            in = new BufferedReader(fr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
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
package org.proteomecommons.io.decoy.mascot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.UnknownFileFormatException;
import org.proteomecommons.io.decoy.GenericDecoyDatabaseAnalysis;
import org.proteomecommons.io.decoy.GenericScoredPeptide;
import org.proteomecommons.io.mgf.MascotGenericFormatPeakList;
import org.proteomecommons.jaf.Atom;
import org.proteomecommons.jaf.Peptide;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class TitleIdentifiedMascotDecoyDatabaseAnalysis extends GenericDecoyDatabaseAnalysis {
    File dir;
    // Mascot sorts all peak lists....need original MGF to map back to # of peak list
    File peakListFile;
    
    // mappings for the mods
    Map<String, String> mods = new HashMap();
    Map<String, String> fixedMods = new HashMap();
    
    public TitleIdentifiedMascotDecoyDatabaseAnalysis(File mascotOutputFile, File peakListFile) {
        this.dir = dir;
        this.peakListFile = peakListFile;
        
        // keep a map of the all the titles to peak list #
        HashMap<String, Integer> titleToIndex = new HashMap();
        
        // map titles to peak list index
        try {
            // read in all the peak lsits
            PeakListReader plr = GenericPeakListReader.getPeakListReader(peakListFile.getAbsolutePath());
            int index = 0;
            for (PeakList pl = plr.getPeakList();pl!=null;pl = plr.getPeakList()) {
                index++;
                MascotGenericFormatPeakList mgfpl = (MascotGenericFormatPeakList)pl;
                titleToIndex.put(mgfpl.getTitle(), index);
            }
        } catch (UnknownFileFormatException ex) {
            throw new RuntimeException("Peak list reading issues.", ex);
        }
        
        // do a parse through the file to get the query/title matches
        HashMap<Integer, String> queryNumberToTitle = new HashMap();
        try {
            FileReader fr = new FileReader(mascotOutputFile);
            BufferedReader br = new BufferedReader(fr);
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // check if it is a query line
                if (line.contains("Content-Type: application/x-Mascot; name=\"query")) {
                    String[] parts = line.split("Content-Type: application/x-Mascot; name=\"query");
                    String queryCount = parts[1].split("\"")[0];
                    // burn the next line
                    br.readLine();
                    // get the title
                    String titleLine = br.readLine();
                    if (!titleLine.contains("title=")) {
                        throw new RuntimeException("Expected a query's title line!");
                    }
                    String title = titleLine.split("title=")[1].replace("%7e", "~");
                    // make the mapping
                    queryNumberToTitle.put(Integer.parseInt(queryCount), title);
                }
            }
            br.close();
            fr.close();
        } catch (Exception e){
            throw new RuntimeException("Can't handle parsing Mascot output file.", e);
        }
        
          
            // parse out the results from each file
            FileReader fr = null;
            BufferedReader br = null;
            try {
                // init the readers
                fr = new FileReader(mascotOutputFile);
                br = new BufferedReader(fr);
                
                // look for the peptide match
                for (String line = br.readLine();line!=null;line=br.readLine()) {
                    // if it is a mod, track the mod
                    if (line.startsWith("delta") && line.split("=").length == 2) {
                        String[] parts = line.split("delta|=|,");
                        mods.put(parts[1], parts[2]);
                    }
                    // handle the fixed mods
                    if (line.startsWith("FixedMod") && line.split("=").length == 2) {
                        String[] parts = line.split("=|,");
                        String[] partsOne = br.readLine().split("=|,");
                        // add all the fixed mods
                        fixedMods.put(partsOne[1], parts[1]);
                    }
                    // handle the peptides
                    if (line.contains("name=\"peptides\"")) {
                        break;
                    }
                }
                
                // handle each peptide
                int count = 1;
                int peptideCount = 1;
                String line = br.readLine();
                // clear out whitespace
                while (line !=null && !line.startsWith("q"+count+"_p"+peptideCount+"=")) {
                    line = br.readLine();
                }
                // get the matches
                while (line !=null) {
                    // check if it is the next peptide
                    if (line.startsWith("q"+count+"_p"+(peptideCount+1)+"=")) {
                        peptideCount++;
                    }
                    // check if the next peak list
                    if (line.startsWith("q"+(count+1)+"_p")) {
                        count++;
                        // reset the peptide count
                        peptideCount = 1;
                    }
                    
                    // check if this is the line with peptide information
                    if (line.startsWith("q"+count+"_p"+peptideCount+"=")) {
                        if (line.trim().endsWith("-1")) {
                            line = br.readLine();
                            continue;
                        }
                        // get the parts of the line
                        String[] parts = line.split("q"+count+"_p"+peptideCount+"=|,");
                        String peptideString = parts[5];
                        String modificationString = parts[7];
                        String score = parts[8];
                        // get the accession string
                        String accession = parts[11].split("\"")[1];
                        
                        // construct the modified peptide string -- assume '1' is oxidation, need to update this
                        StringWriter modPeptideString = new StringWriter();
                        char[] pepChars = peptideString.toCharArray();
                        for (int i=1;i<modificationString.length()-1;i++) {
                            modPeptideString.write(pepChars[i-1]);
                            
                            double delta = 0.0;
                            
                            // check for static mods
                            if (i == 1 && fixedMods.containsKey("N_term")) {
                                String value = fixedMods.get("N_term");
                                delta += Double.parseDouble(value) - Atom.H.getMassInDaltons();
                            }
                            
                            // check for any static mod
                            String aaString = Character.toString(pepChars[i-1]);
                            if (fixedMods.containsKey(aaString)) {
                                String value = fixedMods.get(aaString);
                                delta += Double.parseDouble(value);
                            }
                            
                            // check for any potential mod
                            String modString = Character.toString(modificationString.charAt(i));
                            if (mods.containsKey(modString)) {
                                String value = mods.get(modString);
                                delta += Double.parseDouble(value);
                            }
                            
                            // set the value
                            if (delta > 0 || delta < 0) {
                                modPeptideString.write("("+delta+")");
                            }
                        }
                        
                        // add the scored peptide
                        GenericScoredPeptide sp = new GenericScoredPeptide();
                        sp.setPeptide(new Peptide(modPeptideString.toString()));
                        sp.setScore(Double.parseDouble(score));
                        // use the count to find the title
                        String title = queryNumberToTitle.get(count);
                        // use the title to find the peak list number
                        if (!titleToIndex.containsKey(title)){
                            line = br.readLine();
                            continue;
                        }
                        int peakListNumber = titleToIndex.get(title);
                        sp.setPeakListIdentifier(Integer.toString(peakListNumber));
                        // add the peptide
                        if (accession.contains("R")) {
                            getDecoyPeptides().add(sp);
                        } else {
                            getPeptides().add(sp);
                        }
                    }
                    
                    // get the next line
                    line = br.readLine();
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't handle file "+mascotOutputFile, e);
            } finally {
                try { br.close(); } catch (Exception e){}
                try { fr.close(); } catch (Exception e){}
            }
    }
    
    public static void main(String[] args) throws Exception {
        File resultFile = new File("C:/Bonanza-New/F008439.dat");        
        File mgfFile = new File("C:/Bonanza-New/all-peaklists.mgf");
        
        // make the search
        TitleIdentifiedMascotDecoyDatabaseAnalysis analysis = new TitleIdentifiedMascotDecoyDatabaseAnalysis(resultFile, mgfFile);
        
        // print out basic stats
        System.out.println("*** Mascot Decoy Database Analysis Results ***");
        System.out.println("Peptides: "+analysis.getPeptides().size());
        System.out.println("Decoy Peptides: "+analysis.getDecoyPeptides().size());
        System.out.println("Peptide count at 99.9% confidence: "+analysis.getPeptides(0.999).size());
        System.out.println("Peptides count at 99% confidence: "+analysis.getPeptides(0.99).size());
        System.out.println("Peptides count at 95% confidence: "+analysis.getPeptides(0.95).size());

        System.out.println("\n");
        // print them out
        analysis.print(0.99);
    }
    
}
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
package org.proteomecommons.io.decoy.sequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.UnknownFileFormatException;
import org.proteomecommons.io.decoy.GenericDecoyDatabaseAnalysis;
import org.proteomecommons.io.decoy.GenericScoredPeptide;
import org.proteomecommons.io.mgf.MascotGenericFormatPeakList;
import org.proteomecommons.jaf.Peptide;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDecoyDatabaseAnalysis extends GenericDecoyDatabaseAnalysis {
    File dir;
    File peakListFile;
    
    public SequestDecoyDatabaseAnalysis(File sequestResultsZIP, File mgfFile, Map<String, String> modifications) {
        this.dir = dir;
        this.peakListFile = mgfFile;
        
        // parse the MGF file
        HashMap<String, Integer> mappings = new HashMap();
        try {
            // read in all the peak lsits
            PeakListReader plr = GenericPeakListReader.getPeakListReader(peakListFile.getAbsolutePath());
            int index = 0;
            for (PeakList pl = plr.getPeakList();pl!=null;pl = plr.getPeakList()) {
                index++;
                MascotGenericFormatPeakList mgfpl = (MascotGenericFormatPeakList)pl;
                String id = mgfpl.getTitle();
                mappings.put(id, index);
            }
        } catch (UnknownFileFormatException ex) {
            throw new RuntimeException("Peak list reading issues.", ex);
        }
        
        
        try {
            // make a ZIP reader
            FileInputStream fis = new FileInputStream(sequestResultsZIP);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ZipInputStream zis = new ZipInputStream(bis);
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()){
                // only parse .out files
                if (!ze.getName().endsWith(".out")) {
                    continue;
                }
                
                // get the name
                String id = ze.getName().split("\\.")[0];
                if (id.contains("/")) {
                    id = id.split("/")[1];
                }
                
                // parse out the results from each file
                InputStreamReader isr = new InputStreamReader(zis);
                BufferedReader br = null;
                try {
                    // make a new reader for the zipped content
                    br = new BufferedReader(isr);
                    
                    // skip up to the matches
                    for (String line=br.readLine();line!=null;line=br.readLine()) {
                        if (line.contains("---")) {
                            break;
                        }
                    }
                    
                    // parse each protein
                    for (String line=br.readLine();line!=null;line=br.readLine()) {
                        // exit at the end of the peptides
                        if (line.trim().equals("")) {
                            break;
                        }
                        
                        // handle the peptide
                        String[] parts = line.replace("/", " ").trim().split("\\s+");
                        
                        // get the parts of interest
                        String xcorr = parts[parts.length-5];
                        String reference = parts[parts.length-2];
                        String peptideString = parts[parts.length-1];
                        String[] peptideParts = peptideString.split("\\.");
                        // get just the sequence
                        String peptide = peptideParts[1];
//                    peptide = peptide.replace("*", "(15.9949)");
//                    // change all # to 0.98
//                    peptide = peptide.replace("#", "(0.98402)");
//                    // change all C  to C(57.022)
//                    peptide = peptide.replace("C", "C(57.022)");
                        
                        // replace using all mods
                        for (String key : modifications.keySet()) {
                            String value = modifications.get(key);
                            // replace all uses of the mod
                            peptide = peptide.replace(key, value);
                        }
                        
                        // make the scored peptide
                        GenericScoredPeptide sp = new GenericScoredPeptide();
                        try {
                            sp.setPeptide(new Peptide(peptide));
                            sp.setScore(Double.parseDouble(xcorr));
//                            System.out.println(xcorr+": "+line);
                        } catch (Exception e) {
//                            System.out.println("Can't parse "+xcorr+" from:"+line);
                            break;
                        }
                        Integer mapping = mappings.get(id);
                        if (mapping != null) {
                            sp.setPeakListIdentifier(Integer.toString(mapping));
                        } else {
                            System.out.println("Warning: Can't find mapping for "+id);
                        }
//                    sp.setPeakListIdentifier(id);
                        // put it in the right bin
                        if (reference.contains("R.")) {
                            this.getDecoyPeptides().add(sp);
                        } else {
                            this.getPeptides().add(sp);
                        }
                        // break out
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Can't handle file "+sequestResultsZIP, e);
                } finally {
                    // noop
                }
                
            }
            // close the streams
            try { zis.close(); } catch (Exception e){}
            try { bis.close(); } catch (Exception e){}
            try { fis.close(); } catch (Exception e){}
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
//        File resultFile = new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-initial-search/aurum-sequest-HIS-IPI");
        File resultFile = new File("C:/Bonanza-New/sequest.zip");
        
        
//        File mgfFile = new File("C:/todelete/xtandemoutput/all.mgf");
        File mgfFile = new File("C:/Bonanza-New/all-peaklists.mgf");
        
        // map the modifications -- these should eventually be parsed straight from the output
        Map<String, String> modifications = new HashMap();
        modifications.put("*", "(144.1)");
        modifications.put("#", "(15.99)");
        modifications.put("]", "(144.1)");
        modifications.put("C", "C(46)");
        modifications.put("K", "K(144.1)");
        modifications.put("(144.1)(144.1)", "(144.1)");
        modifications.put("(46)(144.1)", "(190.1)");
        
        // make the search
        SequestDecoyDatabaseAnalysis analysis = new SequestDecoyDatabaseAnalysis(resultFile, mgfFile, modifications);
        
        // print out basic stats
        System.out.println("*** Sequest Decoy Database Analysis Results ***");
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

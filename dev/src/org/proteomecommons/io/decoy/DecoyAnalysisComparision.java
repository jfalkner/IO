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
package org.proteomecommons.io.decoy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DecoyAnalysisComparision {
    public static void main(String[] args) throws Exception {
        // make a map with all the combinations
        String[] names = new String[]{
        "Mascot", "Tandem", "Sequest T7", "Sequest HIS",
        "Mascot*", "Tandem*", "Sequest* T7", "Sequest* HIS"};
        // files to handle
        File[] files = new File[] {
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/mascot-initial.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/xtandem-initial.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/sequest-initial-T7.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/sequest-initial-HIS.txt"),
            // revised searches
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/mascot-revised.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/xtandem-revised.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/sequest-revised-T7.txt"),
            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-decoy-analysis/sequest-revised-HIS.txt")
        };

//        // make a map with all the combinations
//        String[] names = new String[]{
//            "Mascot", "Tandem", "Sequest"
//        };
//        // files to handle
//        File[] files = new File[] {
//            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/oshea-decoy-analysis/oshea-mascot-decoy-99.txt"),
//            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/oshea-decoy-analysis/oshea-tandem-decoy-99.txt"),
//            new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/oshea-decoy-analysis/oshea-sequest-decoy-99.txt"),
//        };

        
        {
            // make the file readers
            FileReader[] readers = new FileReader[files.length];
            for (int i=0;i<files.length;i++) {
                readers[i] = new FileReader(files[i]);
            }
            // make the buffers
            BufferedReader[] buffers = new BufferedReader[files.length];
            for (int i=0;i<files.length;i++) {
                buffers[i] = new BufferedReader(readers[i]);
            }
            // skip to the peptides
            for (int i=0;i<files.length;i++) {
                skipHeader(buffers[i]);
            }
            
            // load the matches
            {
                // set of everything
                Set<Integer> all = new TreeSet();
                
                // sets of values
                Set<Integer>[] sets = new Set[files.length];
                for (int i=0;i<files.length;i++) {
                    // load the file's matches
                    sets[i] = loadMatches(buffers[i]);
                    // add to the all bin
                    all.addAll(sets[i]);
                }
                
                // make a combo iterating array
                boolean[] currentCombo = new boolean[files.length];
                
                // a map for combo -> matches
                List<NamedCount> allComboCounts = new ArrayList();
                
                // start with 1, go until it is maxed -- this works using binary arithmetic
                while (increment(currentCombo, 0)) {
                    // track uses of the value
                    int matches = 0;
                    
                    // check each value
                    for (Integer val : all){
                        if (comboContains(val, currentCombo, sets)) {
                            matches++;
                        }
                    }
                    
                    // figure out what names are participating
                    List<String> participatingNames = new ArrayList();
                    for (int i=0;i<currentCombo.length;i++) {
                        if (currentCombo[i]) {
                            participatingNames.add(names[i]);
                        }
                    }
                    
                    // sort names
                    Collections.sort(participatingNames);
                    
                    // add the count
                    String name = participatingNames.get(0);
                    if (participatingNames.size() > 1){
                        for (int i=1;i<participatingNames.size()-1;i++) {
                            name += ", "+participatingNames.get(i);
                        }
                        name += " and "+participatingNames.get(participatingNames.size()-1);
                    }
                    allComboCounts.add(new NamedCount(name, matches));
                }
                
                // display the overlap
                System.out.println("*** Overlap of peak list files ***");
                Collections.sort(allComboCounts);
                Collections.reverse(allComboCounts);
                for (NamedCount nc : allComboCounts) {
                    System.out.println(nc.name+"\t"+nc.count);
                }
                
            }
            // close all buffers and readers
            for (int i=0;i<files.length;i++) {
                buffers[i].close();
                readers[i].close();
            }
        }

        {
            // make the file readers
            FileReader[] readers = new FileReader[files.length];
            for (int i=0;i<files.length;i++) {
                readers[i] = new FileReader(files[i]);
            }
            // make the buffers
            BufferedReader[] buffers = new BufferedReader[files.length];
            for (int i=0;i<files.length;i++) {
                buffers[i] = new BufferedReader(readers[i]);
            }
            // skip to the peptides
            for (int i=0;i<files.length;i++) {
                skipHeader(buffers[i]);
            }
            
            // load the matches
            {
                // set of everything
                Set<String> all = new TreeSet();
                
                // sets of values
                Set<String>[] sets = new Set[files.length];
                for (int i=0;i<files.length;i++) {
                    // load the file's matches
                    sets[i] = loadPeptideMatches(buffers[i]);
                    // add to the all bin
                    all.addAll(sets[i]);
                }
                
                // make a combo iterating array
                boolean[] currentCombo = new boolean[files.length];
                
                // a map for combo -> matches
                List<NamedCount> allComboCounts = new ArrayList();
                
                // start with 1, go until it is maxed -- this works using binary arithmetic
                while (increment(currentCombo, 0)) {
                    // track uses of the value
                    int matches = 0;
                    
                    // check each value
                    for (String val : all){
                        if (comboContains(val, currentCombo, sets)) {
                            matches++;
                        }
                    }
                    
                    // figure out what names are participating
                    List<String> participatingNames = new ArrayList();
                    for (int i=0;i<currentCombo.length;i++) {
                        if (currentCombo[i]) {
                            participatingNames.add(names[i]);
                        }
                    }
                    
                    // sort
                    Collections.sort(participatingNames);
                    
                    // add the count
                    String name = participatingNames.get(0);
                    if (participatingNames.size() > 1){
                        for (int i=1;i<participatingNames.size()-1;i++) {
                            name += ", "+participatingNames.get(i);
                        }
                        name += " and "+participatingNames.get(participatingNames.size()-1);
                    }
                    allComboCounts.add(new NamedCount(name, matches));
                }
                
                // display the overlap
                System.out.println("\n*** Overlap of identified peptide sequences (normalized mods) ***");
                Collections.sort(allComboCounts);
                Collections.reverse(allComboCounts);
                for (NamedCount nc : allComboCounts) {
                    System.out.println(nc.name+"\t"+nc.count);
                }
                
                
                // dump out all peptides matched by # of search engines that found them
                ArrayList<NamedCount> allPeptidesSortedBySearchEngineCount = new ArrayList();
                for(String pep : all) {
                    // make a new count
                    NamedCount nc = new NamedCount(pep, 0);
                    for (Set set : sets) {
                        if (set.contains(pep)) {
                            nc.count++;
                        }
                    }
                    // add to the big list
                    allPeptidesSortedBySearchEngineCount.add(nc);
                }
                // reverse to get descending
                Collections.sort(allPeptidesSortedBySearchEngineCount);
                Collections.reverse(allPeptidesSortedBySearchEngineCount);
                
                // dump the list
                System.out.println("\n*** All peptides identified sorted by number of search engines ***");
                System.out.println("Peptide Sequence\t# Search Engines");
                for (NamedCount nc : allPeptidesSortedBySearchEngineCount) {
                    System.out.println(nc.name+"\t"+nc.count);
                }
                
            }
            // close all buffers and readers
            for (int i=0;i<files.length;i++) {
                buffers[i].close();
                readers[i].close();
            }
        }
    }
    
    private static boolean comboContains(final Integer val, final boolean[] currentCombo, final Set<Integer>[] sets) {
        // check this combination
        for (int i=0;i<currentCombo.length;i++) {
            // if this set is in the combo, check it
            if(currentCombo[i]) {
                // check the set for the value
                if (!sets[i].contains(val)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean comboContains(final String val, final boolean[] currentCombo, final Set<String>[] sets) {
        // check this combination
        for (int i=0;i<currentCombo.length;i++) {
            // if this set is in the combo, check it
            if(currentCombo[i]) {
                // check the set for the value
                if (!sets[i].contains(val)) {
                    return false;
                }
            }
        }
        return true;
    }

    
    private static boolean increment(boolean[] combo, int index) {
        // check for the end
        if (index >= combo.length) {
            return false;
        }
        
        // try to increment at this index
        if (!combo[index]) {
            combo[index] = true;
            return true;
        } else {
            // flag this off and carry the one
            combo[index] = false;
            boolean incremented = increment(combo, index+1);
            // if it incremented, return true
            if (incremented) return true;
            // if the number couldn't increment, replace it
            else {
                combo[index] = true;
                return false;
            }
        }
    }
    
    private static boolean notAllTrue(boolean[] combo) {
        for (int i=0;i<combo.length;i++) {
            if (!combo[i]) return true;
        }
        return false;
    }
    
    private static Set<Integer> loadMatches(final BufferedReader brA) throws IOException, NumberFormatException {
        // read all the numbers in to memory
        Set<Integer> iA = new TreeSet();
        for (String s = brA.readLine();s!=null; s=brA.readLine()) {
            // break at decoy section
            if (s.trim().equals("")) {
                break;
            }
            // split the string
            String[] parts = s.split("\t");
            Integer i = new Integer(parts[2]);
            iA.add(i);
        }
        return iA;
    }
    
    private static Set<String> loadPeptideMatches(final BufferedReader brA) throws IOException, NumberFormatException {
        // read all the numbers in to memory
        Set<String> iA = new TreeSet();
        for (String s = brA.readLine();s!=null; s=brA.readLine()) {
            // break at decoy section
            if (s.trim().equals("")) {
                break;
            }
            // split the string
            String[] parts = s.split("\t");
            iA.add(parts[1]);
        }
        return iA;
    }
    
    
    private static void skipHeader(final BufferedReader brA) throws IOException {
        // skip up to the start of the peptides
        for (String s = brA.readLine();s!=null;s=brA.readLine()) {
            if (s.contains("*** Peptides at")) {
                brA.readLine();
                break;
            }
        }
    }
}

class NamedCount implements Comparable {
    int count;
    String name;
    public NamedCount(String name, int count) {
        this.name = name;
        this.count = count;
    }
    public int compareTo(Object o) {
        NamedCount a = (NamedCount)o;
        return new Integer(count).compareTo(a.count);
    }
}

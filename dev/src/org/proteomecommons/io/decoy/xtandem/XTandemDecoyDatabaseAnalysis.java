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
package org.proteomecommons.io.decoy.xtandem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.proteomecommons.io.decoy.GenericDecoyDatabaseAnalysis;
import org.proteomecommons.io.decoy.GenericScoredPeptide;
import org.proteomecommons.io.decoy.ScoredPeptide;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class XTandemDecoyDatabaseAnalysis extends GenericDecoyDatabaseAnalysis {
    File file;
    
    public XTandemDecoyDatabaseAnalysis(File file) {
        this.file = file;
        
        // do the search
        XTandemDecoyDatabaseAnalysisContentHandler results = new XTandemDecoyDatabaseAnalysisContentHandler();
        // set the ArrayList objects so that they are appropriately loaded
//        results.peptides = getPeptides();
//        results.decoyPeptides = getDecoyPeptides();
        try {
            XMLReader read = XMLReaderFactory.createXMLReader();
            read.setContentHandler(results);
            read.parse(file.getCanonicalPath());
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        
        // set the peptides
        getPeptides().addAll(results.peptides);
        getDecoyPeptides().addAll(results.decoyPeptides);
    }
    
    public static void main(String[] args) throws Exception {
//        File resultFile = new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-tandem-decoy-IPI/aurum-tandem-decoy-IPI.xml");

//        File resultFile = new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-revised-search/aurum-xtandem-IPI.xml");
//        File resultFile = new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-initial-search/aurum-xtandem-IPI.xml");

        File resultFile = new File("C:/Documents and Settings/Jayson/Desktop/spectral-searcher/aurum-crap-search/aurum-xtandem-crap.xml");
        
        // make the search
        XTandemDecoyDatabaseAnalysis analysis = new XTandemDecoyDatabaseAnalysis(resultFile);
        
        // print out basic stats
        System.out.println("*** XTandem Decoy Database Analysis Results ***");
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

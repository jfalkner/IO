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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.TreeSet;
import org.proteomecommons.io.decoy.GenericScoredPeptide;
import org.proteomecommons.io.decoy.ScoredPeptide;
import org.proteomecommons.jaf.Peptide;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Jarret
 */
public class XTandemDecoyDatabaseAnalysisContentHandler extends DefaultHandler{
    TreeSet<ScoredPeptide> peptides = new TreeSet();
    TreeSet<ScoredPeptide> decoyPeptides = new TreeSet();
    private int currentGroup = -1;
    private int start = -1;
    private String currentPep = "";
    private String[] modifications = null;
    private int modcount = 0;
    private double score = GenericScoredPeptide.UNKNOWN_SCORE;
    boolean isReversed = false;
    
    
    public void startElement(String string, String localName, String string1, Attributes attributes) throws SAXException {
        //handle opening group tags
        if(localName.equals("protein")){
            isReversed = attributes.getValue("label").contains("reversed sequence");
        }
        //handle opening group tags
        else if(localName.equals("group") && attributes.getValue("id") != null){
            currentGroup = Integer.parseInt(attributes.getValue("id"));
        }
        //handle opening domain tags
        else if(localName.equals("domain") && attributes.getValue("seq") != null){
            modcount = 0;
            // get the starting sequence
            start = Integer.parseInt(attributes.getValue("start"));
            // use the hyperscore as the score
            score = Double.parseDouble(attributes.getValue("hyperscore"));
//            score = Double.parseDouble(attributes.getValue("b_score"))+Double.parseDouble(attributes.getValue("y_score"));
            // set the base sequence
            currentPep = attributes.getValue("seq");
            // note the modification
            modifications = new String[currentPep.length()];
            start = Integer.parseInt(attributes.getValue("start"));
        }
        //handle opening aa tags for mods
        else if(localName.equals("aa") && attributes.getValue("type")!= null && attributes.getValue("modified")!= null){
            int at = Integer.parseInt(attributes.getValue("at"));
            char type = attributes.getValue("type").charAt(0);
            if(currentPep.charAt(at-start) != type && currentPep.toLowerCase().charAt(at-start) != type){
                throw new RuntimeException("modifying " + currentPep.charAt(at-start) + " but it should be " + type);
            } else {
                // modify the current peptide
                if (modifications[at-start] == null) {
                    modifications[at-start] = attributes.getValue("modified");
                }
                // handle doubly modified amino-acids
                else {
                    // sort the mods
                    String[] sortedMods = new String[]{
                        modifications[at-start],
                        attributes.getValue("modified")
                    };
                    Arrays.sort(sortedMods);
                    
                    // add the modification values
                    double modificationMass = Double.parseDouble(sortedMods[0]);
                    modificationMass += Double.parseDouble(sortedMods[1]);
                    // set the modification mass
                    modifications[at-start] = Double.toString(modificationMass);
                    
//                    // check n-term deamidation and iTRAQ
//                    if (sortedMods[0].equals("0.98402") && sortedMods[1].equals("144.155")) {
//                        modifications[at-start] = "145.13902";
//                    }
//                    // check n-term oxidation and iTRAQ
//                    else if (sortedMods[0].equals("144.155") && sortedMods[1].equals("15.9949")) {
//                        modifications[at-start] = "160.1499";
//                    }
//                    // check n-term K iTRAQ and iTRAQ
//                    else if (sortedMods[0].equals("144.155") && sortedMods[1].equals("144.155")) {
//                        modifications[at-start] = "288.31";
//                    }
//                    // check n-term Y iTRAQ and iTRAQ
//                    else if (sortedMods[0].equals("144.102") && sortedMods[1].equals("144.155")) {
//                        modifications[at-start] = "288.257";
//                    }
//                    // check n-term C MMTS and iTRAQ
//                    else if (sortedMods[0].equals("144.155") && sortedMods[1].equals("45.9878")) {
//                        modifications[at-start] = "190.1428";
//                    }
//                    // check n-term pyro-glu E and iTRAQ -- is this possible!?
//                    else if (sortedMods[0].equals("-18.0106") && sortedMods[1].equals("144.155")) {
//                        modifications[at-start] = "126.1444";
//                    }
//                    // check n-term pyro-glu/deamidated Q and iTRAQ -- is this possible!?
//                    else if (sortedMods[0].equals("-17.0265") && sortedMods[1].equals("145.13902")) {
//                        modifications[at-start] = "128.11252";
//                    }
//                    // check n-term pyro-glu Q and iTRAQ -- is this possible!?
//                    else if (sortedMods[0].equals("-17.0265") && sortedMods[1].equals("144.155")) {
//                        modifications[at-start] = "127.1285";
//                    }
//                    // check n-term pyro-glu Q and deamidation -- is this possible!?
//                    else if (sortedMods[0].equals("-17.0265") && sortedMods[1].equals("0.98402")) {
//                        modifications[at-start] = "-16.04248";
//                    }
//                    // check n-term pyro-glu Q and propionamide -- is this possible!?
//                    else if (sortedMods[0].equals("-17.0265") && sortedMods[1].equals("57.022")) {
//                        modifications[at-start] = "39.9955";
//                    }
//                    else {
//                        // debug leftover
//                        System.out.println("Couldn't handle: "+currentPep+", mod1: "+sortedMods[0]+", mod2: "+sortedMods[1]);
//                    }
                }
            }
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // at ends of domain, add the peptide
        if(localName.equals("domain")){
            // make the peptide
            StringWriter sw = new StringWriter();
            for (int i=0;i<currentPep.length();i++) {
                sw.write(currentPep.charAt(i));
                if (modifications[i] != null) {
                    sw.write("("+modifications[i]+")");
                }
            }
            String peptideString = sw.toString();
//            // check if it exists
//            if (peptideString.contains("X")) {
//                return;
//            }
            try {
                // convert to a peptide
                Peptide p = new Peptide(peptideString);
                // add the scored peptide
                GenericScoredPeptide gsp = new GenericScoredPeptide();
                gsp.setPeptide(p);
                gsp.setScore(score);
                gsp.setPeakListIdentifier(Integer.toString(currentGroup));
                if (isReversed) {
                    decoyPeptides.add(gsp);
                } else {
                    peptides.add(gsp);
                }
                
            } catch (Exception ex) {
                System.out.println("Can't create a peptide using "+peptideString);
//                throw new RuntimeException("Can't create a peptide using "+peptideString, ex);
            }
        }
    }
}

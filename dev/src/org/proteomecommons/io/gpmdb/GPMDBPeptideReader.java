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
package org.proteomecommons.io.gpmdb;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.proteomecommons.io.GenericPeptideReader;
import org.proteomecommons.jaf.Atom;
import org.proteomecommons.jaf.GenericAtom;
import org.proteomecommons.jaf.GenericModification;
import org.proteomecommons.jaf.GenericModifiedResidue;
import org.proteomecommons.jaf.GenericResidue;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
/**
 * Peptide reading support for the gpmdb
 * @author Jarret jar@cs.washington.edu
 *
 */
public class GPMDBPeptideReader extends GenericPeptideReader {
    private static final boolean DEBUG = false;
    
    //these are just peptides parsed from GPMDB pages seen thus far
    private LinkedList peptides = new LinkedList();
    
    //if there are more links to exlore before this reader is empty, they're in this list
    private LinkedList possibleLinks = new LinkedList();
    
    //possible protein sequences
    private HashSet possibleProteins = new HashSet();
        
    /**
     * Create a GPM reader that starts looking for peptides at the specified URL.
     * @param url a GPMDB page on which to start looking for peptides.  Search results from keywords, Accession numbers, GPM numbers, or a protein model page are all valid starting points.
     */
    public GPMDBPeptideReader(String url){
        //System.out.println("Setup with " + url);
        possibleLinks.add(url);
    }
    
        /* (non-Javadoc)
         * @see org.proteomecommons.io.GenericPeptideReader#next()
         */
    public Peptide next() {
        if(DEBUG) System.out.println("Finding a peptide with " + possibleLinks.size() + " possible links and " + peptides.size() + " queued peptides.");
        
        //base case: no peptides, no links to parse
        if(peptides.size() == 0 && possibleLinks.size() == 0){
            return null;
        }
        
        //any queued peptides?
        if(peptides.size() > 0){
            return (Peptide)peptides.removeFirst();
        }
        
        //parse a link
        parse((String)possibleLinks.removeFirst());
        return next();
    }
    
    /**
     * A helper to parse one page of the gpmdb and update the internal peptides and possible links lists.
     * @param url the location to parse
     */
    private void parse(String url) {
        //create a singular HttpClient object
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost("gpmdb-us.thegpm.org");
        
        //establish a connection within 25 seconds
        client.getHttpConnectionManager().
                getParams().setConnectionTimeout(25000);
        
        //create a method object
        HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);
        
        //System.out.println("Getting " + url);
        
        //execute the method
        try{
            client.executeMethod(method);
            InputStream responseBody = method.getResponseBodyAsStream();
            if(responseBody != null){
                //if(!url.endsWith("xml")){
                //kludge through the html
                
                //go through the html and look for various elements like peptide sequences, or links to more about this hit
                BufferedReader html = new BufferedReader(new InputStreamReader(responseBody));
                
                String line = html.readLine();
                
                //some parsing state
                String proteinModel = "";
                boolean inModel = false;
                String proteinID = null;
                int tableStringCount = 0;
                //generic loop to process any html that we might see on the gpmdb.. from any of the page generating scripts.
                while(line != null){
                    //check the line for links to "GPM - protein model:" pages... but don't look for more links if we're already at a protein.pl
                    //for example, if they clicked on an accession link
                    if(line.indexOf("protein.pl") != -1 && url.indexOf("protein.pl") == -1){
                        //System.out.println("Contains a link to a protein xml doc: " + line);
                        //split on links
                        String[] splitLinks = line.split("href=\"*\"");
                        //look over all of the split results to find good links
                        for(int index = 0; index < splitLinks.length; index++){
                            //System.out.println("possible " + index + " " + splitLinks[index]);
                            if(splitLinks[index].indexOf("archive") != -1 && splitLinks[index].indexOf("protein.pl") != -1){
                                splitLinks[index] = splitLinks[index].substring(0, splitLinks[index].indexOf("\">"));
                                if(DEBUG) System.out.println("Found more peptide model pages.. Adding " + index + " " + splitLinks[index]);
                                possibleLinks.add(splitLinks[index]);
                            }
                        }
                    }
                    
                    //check for links to "Search Results for:" if we've done a keyword search
                    if(url.indexOf("dblist_keyword.pl") != -1 && line.indexOf("dblist_label.pl") != -1){
//                        System.out.println("Expandling links by accessions");
                        
                        String[] splitLinks = line.split("href=\"*\"");
                        //look over all of the split results to find good links
                        for(int index = 0; index < splitLinks.length; index++){
                            //System.out.println("possible " + index + " " + splitLinks[index]);
                            if(splitLinks[index].indexOf("dblist_label.pl") != -1){
                                splitLinks[index] = splitLinks[index].substring(0, splitLinks[index].indexOf("\">"));
//                                System.out.println("Found more protein search result pages.. Adding " + index + " " + splitLinks[index]);
                                possibleLinks.add(splitLinks[index]);
                            }
                        }
                    }
                    
                    //look for the actual pepetides and model sequence if we're on a protein model page
                    if(url.indexOf("protein.pl") != -1){
//                        System.out.println("Handling Ling: "+line);
                        
                        // pull out protein id if appropriate
                        if (line.indexOf("<BR>protein model: ")!=-1) {
                            String[] parts = line.split("<BR>protein model: |<BR><BR>");
                            System.out.println("Setting ID: "+parts[1]);
                            proteinID = parts[1];
                        }
                        
                        // fix me!
                        if (line.indexOf("<table")!=-1) {
                            tableStringCount++;
                        }
                        
                        //look for the protein model... and parse it
                        if(tableStringCount == 5 && line.indexOf("</table>")==-1){
                            inModel = true;
                            
                            //grab all of the AminoAcid chars on these lines
                            String chars = removeAllTags(line);
//                            System.out.println("Adding: "+filterAminoAcidChars(chars));
                            proteinModel = proteinModel + filterAminoAcidChars(chars);
                            
                        }
                        
                        if((tableStringCount > 4 && line.indexOf("</table>")!=-1)){
//                            System.out.println("Flagging out of model.");
                            inModel = false;
                            tableStringCount++;
                            possibleProteins.add(proteinModel);
//                            System.out.println("Added protein model " + proteinModel);
                            proteinModel = "";
                        }
                        
//                        //look for the protein model... and parse it
//                        if(line.indexOf("<pre>") != -1 || tableStringCount == 5){
//                            inModel = true;
//                        } else if(line.indexOf("</pre>") != -1 || (tableStringCount==5 && line.indexOf("</table>")!=-1)){
//                            inModel = false;
//                            tableStringCount++;
//                            possibleProteins.add(proteinModel);
//                            if(DEBUG) System.out.println("Added protein model " + proteinModel);
//                            proteinModel = "";
//                        } else if (inModel){
//                            //grab all of the AminoAcid chars on these lines
//                            String chars = removeAllTags(line);
//
//                            proteinModel = proteinModel + filterAminoAcidChars(chars);
//
//                        } else
                        if (line.indexOf("spectrum") != -1 && line.indexOf("sequence") != -1){
                            //found the table with peptide sequences... parse the peptides out of it
                            String[] rows = line.split("<tr>");
                            
                            //process rows and columns in the table.  There are peptide sequences in there.
                            for(int row = 0; row < rows.length; row++){
                                String[] columns = rows[row].split("</td>");
                                for(int column = 0; column < columns.length; column++){
                                    String possiblePep = removeAllTags(columns[column]);
                                    possiblePep = filterAminoAcidChars(possiblePep);
                                    //System.out.println(possiblePep);
                                    
                                    //give all detagged and filtered strings that are above a specified size a chance at becoming a peptide.
                                    if(possiblePep.length() > 4 && columns[column].indexOf("nbsp") == -1){
                                        try {
                                            //convert the possible pep into a real live peptide sequence
                                            String pepSeq = validatePeptideSequence(possiblePep);
                                            peptides.add(new GPMDBPeptide(pepSeq, proteinID));
                                        } catch (Exception e) {
                                            System.err.println("Tried to make a peptide from " + possiblePep + " but failed");
                                            System.err.println("The possible string was " + columns[column]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    
                    //and look for more
                    line = html.readLine();
                }
                //	} else {
                        /*
                        //setup an xml reader to parse the document
                         
                        try {
                                XMLInputFactory factory = XMLInputFactory.newInstance();
                                parser = factory.createXMLStreamReader(responseBody);
                         
                                //state for parsing the document
                                boolean inPeptide = false; //in a 'peptide' tag?
                         
                                //walk through the document, and buffer some peptides
                                for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()){
                                        if(event == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("domain")){
                                                String pepSeq = parser.getAttributeValue(null, "seq");
                                                //TODO do mods too
                                                try{
                                                        peptides.add(new Peptide(pepSeq));
                                                } catch (Exception e){
                                                        System.out.println("couldn't get a pepseq from the xml...");
                                                }
                                        } else if (event == XMLStreamConstants.CHARACTERS && inPeptide){
                                                //System.out.println("Found characters in the doc in  peptide tag");
                                                char[] chars = parser.getTextCharacters();
                                                String fixed = "";
                                                for(int character = 0; character < chars.length; character++){
                                                        if(chars[character] == 'A' ||
                                                                        chars[character] == 'R' ||
                                                                        chars[character] == 'N' ||
                                                                        chars[character] == 'D' ||
                                                                        chars[character] == 'C' ||
                                                                        chars[character] == 'E' ||
                                                                        chars[character] == 'Q' ||
                                                                        chars[character] == 'G' ||
                                                                        chars[character] == 'H' ||
                                                                        chars[character] == 'I' ||
                                                                        chars[character] == 'L' ||
                                                                        chars[character] == 'K' ||
                                                                        chars[character] == 'M' ||
                                                                        chars[character] == 'F' ||
                                                                        chars[character] == 'P' ||
                                                                        chars[character] == 'S' ||
                                                                        chars[character] == 'T' ||
                                                                        chars[character] == 'W' ||
                                                                        chars[character] == 'Y' ||
                                                                        chars[character] == 'V'){
                                                                fixed = fixed + chars[character];
                                                        }
                                                }
                                                if(!possibleProteins.contains(fixed)){
                                                        possibleProteins.add(fixed);
                                                }
                                                //System.out.println(fixed);
                                        } else if (event == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("peptide")){
                                                inPeptide = true;
                                        } else if(event == XMLStreamConstants.END_ELEMENT && parser.getLocalName().equals("peptide")){
                                                inPeptide = false;
                                        }
                                }
                        } catch (XMLStreamException ex) {
                                System.out.println(ex);
                        }
                         */
                //	}
            }
        } catch (HttpException he) {
            System.err.println("Could not connect to '" + url + "'");
        } catch (IOException ioe){
            System.err.println("Unable to connect to the GPMDB.");
        }
        
        //clean up the connection resources
        method.releaseConnection();
    }
    
    /**
     * Make sure that everything in the sequence is going to make a valid peptide.
     * This means any mods need to be registered after being translated from ;mod: form
     * @param possiblePep a string that needs a chance to become a peptide
     * @return a hopefully valid peptide sequence.  Modifications are changed from ;+15.2: to (+15.2) and registered as modifications in the JAF.
     */
    public String validatePeptideSequence(String possiblePep) {
        //look for (+15.9328) and make it into a mod
        int left = possiblePep.indexOf(";");
        int right = possiblePep.indexOf(":");
        
        while(left != -1 && right != -1){
            //System.out.println(possiblePep);
            
            //figure out how to setup the mod
            char moddedAmino = possiblePep.charAt(left-1);
            String mod = possiblePep.substring(left+1, right);
            double modMass = Double.parseDouble(mod);
            //System.out.println(mod + " found after " + moddedAmino);
            
            //fix the sequence so a Peptide can be made
            possiblePep = possiblePep.replaceFirst(";", "(");
            possiblePep = possiblePep.replaceFirst(":", ")");
            
            //build the atom, modification, and modified residue and register them appropriately.
            Residue res = GenericResidue.getResidueByFASTAChar(moddedAmino);
            GenericAtom atom = new GenericAtom(mod, modMass, 100.0);
            GenericModification gm = new GenericModification(mod, new Atom[]{atom}, new Atom[0]);
            GenericModifiedResidue gmr = new GenericModifiedResidue(res, gm);
            GenericResidue.addResidue(gmr);
            
            //System.out.println(possiblePep);
            
            left = possiblePep.indexOf(";");
            right = possiblePep.indexOf(":");
        }
        
        
        return possiblePep;
    }
    
    /**
     *
     * @param possiblePep a combination of characters, some of which might be amino acids.  Modifications are noted by ACY;mod:AAK
     * @return a filtered string containing only amino acids and mods like ACY;mod:AAK
     */
    public String filterAminoAcidChars(String possiblePep) {
        //build the output string
        String out = "";
        char[] chars = possiblePep.toCharArray();
        
        //state to remember we're in a mod.. .don't filter characters that are in a mod
        for(int character = 0; character < chars.length; character++){
            //go along the string and only keep amino acid chars and modifications
            if(chars[character] == 'A' ||
                    chars[character] == 'R' ||
                    chars[character] == 'N' ||
                    chars[character] == 'D' ||
                    chars[character] == 'C' ||
                    chars[character] == 'E' ||
                    chars[character] == 'Q' ||
                    chars[character] == 'G' ||
                    chars[character] == 'H' ||
                    chars[character] == 'I' ||
                    chars[character] == 'L' ||
                    chars[character] == 'K' ||
                    chars[character] == 'M' ||
                    chars[character] == 'F' ||
                    chars[character] == 'P' ||
                    chars[character] == 'S' ||
                    chars[character] == 'T' ||
                    chars[character] == 'W' ||
                    chars[character] == 'Y' ||
                    chars[character] == 'V'){
                out = out + chars[character];
            }
        }
        return out;
    }
//    public String filterAminoAcidChars(String possiblePep) {
//        //build the output string
//        String out = "";
//        char[] chars = possiblePep.toCharArray();
//
//        //state to remember we're in a mod.. .don't filter characters that are in a mod
//        boolean inMod = false;
//        for(int character = 0; character < chars.length; character++){
//            if(chars[character] == ';'){
//                inMod = true;
//            }
//            //go along the string and only keep amino acid chars and modifications
//            if(inMod || chars[character] == 'A' ||
//                    chars[character] == 'R' ||
//                    chars[character] == 'N' ||
//                    chars[character] == 'D' ||
//                    chars[character] == 'C' ||
//                    chars[character] == 'E' ||
//                    chars[character] == 'Q' ||
//                    chars[character] == 'G' ||
//                    chars[character] == 'H' ||
//                    chars[character] == 'I' ||
//                    chars[character] == 'L' ||
//                    chars[character] == 'K' ||
//                    chars[character] == 'M' ||
//                    chars[character] == 'F' ||
//                    chars[character] == 'P' ||
//                    chars[character] == 'S' ||
//                    chars[character] == 'T' ||
//                    chars[character] == 'W' ||
//                    chars[character] == 'Y' ||
//                    chars[character] == 'V'){
//                out = out + chars[character];
//            }
//            if(chars[character] == ':'){
//                inMod = false;
//            }
//        }
//        return out;
//    }
    
    /**
     *
     * @param line a line containing html tags and other text
     * @return the given string with all html tags removed.  Translates tags that specified modifications into the form ;modification:
     */
    public String removeAllTags(String line) {
        int left = line.indexOf("<");
        int right = line.indexOf(">");
        
        //look for all of the tags
        while(left != -1 && right != -1){
            String removal = line.substring(left, right+1);
            //System.out.println("Removing " + removal);
            
            //one last chance if that tag is a mod
            String[] lastChance = modMassChanges(removal);
            String mod = "";
            if(lastChance.length > 0){
                mod = ";" + lastChance[0].trim() + ":";
            }
            
            //reinsert the mod
            line = line.substring(0,left) + line.substring(right+1, line.length());
            if(line.length() <= left){
                line = line + mod;
            } else {
                line = line.substring(0, left+1) + mod + line.substring(left +1, line.length());
            }
            
            left = line.indexOf("<");
            right = line.indexOf(">");
        }
        return line;
    }
    
    /**
     *
     * @return a set of the protein models that this reader has seen so far
     */
    public HashSet getProteins(){
        return possibleProteins;
    }
    
    /**
     * Static method to test parsing of a few of the gpmdb sites.
     * @param args
     */
    public static void main(String[] args){
        GPMDBPeptideReader pr = new GPMDBPeptideReader("http://gpmdb.thegpm.org/thegpm-cgi/dblist_keyword.pl?keyword=clusterin&db=Mus+musculus&db_index=0");
        Peptide p = pr.next();
        int count = 0;
        while(p != null){
            System.out.println(p);
            p = pr.next();
            count++;
        }
        System.out.println( count + " peptides.");
        System.out.println( pr.possibleProteins.size() + " possible proteins.");
    }
    
    /**
     *
     * @return an arraylist of Strings that are the value of title elements in mod span tags
     */
    public String[] modMassChanges(String html){
        ArrayList modMasses = new ArrayList();
        int left = html.indexOf("<span");
        int right = html.indexOf("mod\">");
        while(left != -1 && right != -1){
            modMasses.add(html.substring(left, right + 5));
            html = html.substring(0,left) + html.substring(right+5, html.length());
            //System.out.println(html);
            left = html.indexOf("<span");
            right = html.indexOf("mod\">");
        }
        String[] mods = (String[])modMasses.toArray(new String[0]);
        
        for(int mod = 0; mod < mods.length; mod++){
            mods[mod] = mods[mod].substring(mods[mod].indexOf("\"")+1);
            mods[mod] = mods[mod].substring(0, mods[mod].indexOf("\""));
            if(DEBUG) System.out.println(mods[mod]);
        }
        
        return mods;
    }
    
    /**
     *
     * @return a list of any peptides that this reader is ready to return
     */
    public LinkedList getPeptidesQueue(){
        return peptides;
    }
    
    /**
     *
     * @return the links that this reader might examine for more peptides
     */
    public LinkedList getPossibleLinksQueue(){
        return possibleLinks;
    }
}

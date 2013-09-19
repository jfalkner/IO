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
package org.proteomecommons.io.fasta.generic;

import java.io.FileInputStream;
import java.io.InputStream;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;
import org.proteomecommons.jaf.Protein;

/**
 * An abstraction for reading proteins from a protein database.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author James "Augie" Hill <augie@828productions.com>
 *
 */
public class GenericFASTAReader extends FASTAReader {

    /**
     * Public constructor where the InputStream instance is assumed to be the
     * FASTA sequence.
     *
     * @param is
     */
    public GenericFASTAReader(InputStream is) {
        setInputStream(is);
    }
    
    /**
     * Get the next protein, or null if there is no more proteins.
     *
     * @return The next protein from the FASTA file or null if none or left.
     */
    public FASTAProtein next() {
        try {
            String headerLine = "", sequence = "";
            
            // get the header line
            for (int i = getInputStream().read(); i != -1 && i != '\n'; i = getInputStream().read()) {
                headerLine = headerLine + (char) i;
            }
            
            // get the sequence
            for (int i = getInputStream().read(); i != -1 && i != '>'; i = getInputStream().read()) {
                // skip whitespace
                if (i < 'A' || i > 'Z') {
                    continue;
                }
                // check for non-valid fasta characters
                if (i=='Z'||i == 'X' || i== 'J'|| i=='O' || i == 'B'){
                    continue;
                }
                // append to the sequence
                sequence = sequence + (char) i;
            }
            
            // this is the end of the file
            if (headerLine.length() == 0 && sequence.length() == 0) {
                return null;
            }
            
            // make sure the name doesn't start with the '>' character
            if (headerLine.charAt(0) == '>') {
                headerLine = headerLine.substring(1);
            }
            
            // parse the header
            FASTAProtein protein = null;
            if (headerLine.indexOf(' ') != -1) {
                protein = new FASTAProtein(headerLine.substring(0, headerLine.indexOf(' ')), sequence, headerLine.substring(headerLine.indexOf(' ')));
                protein.setDescription(headerLine.substring(headerLine.indexOf(' ') + 1));
            } else {
                protein = new FASTAProtein(headerLine, sequence, "");
            }
            return protein;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Test the parser.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(args[0]);
        GenericFASTAReader fpp = new GenericFASTAReader(fis);
        for (Protein p = fpp.next(); p != null; p = fpp.next()) {
            System.out.println(p.getName().trim());
            System.out.println(p.getSequence().trim());
        }
    }
}
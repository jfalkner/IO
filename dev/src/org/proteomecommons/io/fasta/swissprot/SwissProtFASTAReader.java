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

package org.proteomecommons.io.fasta.swissprot;

import java.io.InputStream;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class SwissProtFASTAReader extends FASTAReader {
    
    public SwissProtFASTAReader(InputStream is) {
        setInputStream(is);
    }
    
    /**
     * Get the next protein, or null if there is no more proteins.
     *
     * @return The next protein from the FASTA file or null if none or left.
     */
    public FASTAProtein next() {
        try {
            
            // get the name
            String headerLine = "";
            for (int i = getInputStream().read(); i != -1 && i != '\n'; i = getInputStream().read()) {
                headerLine = headerLine + (char) i;
            }
            
            // get the sequence
            String sequence = "";
            for (int i = getInputStream().read(); i != -1 && i != '>'; i = getInputStream().read()) {
                // skip whitespace
                if (i < 'A' || i > 'Z') {
                    continue;
                }
                // check for non-valid fasta characters
                if (i=='Z'||i == 'X' || i== 'J'|| i=='O' || i == 'B'){
                    continue;
                }
                sequence = sequence + (char) i;
            }
            
            // if at the end, skip
            if (sequence.length() == 0 && headerLine.length() == 0) {
                return null;
            }
            
            // make sure we did not get the header line character
            if (headerLine.charAt(0) == '>') {
                headerLine = headerLine.substring(1);
            }
            
            // make the protein
            // example header line: >Q4U9M9|104K_THEAN 104 kDa microneme-rhoptry antigen precursor
            FASTAProtein protein = null;
            if (headerLine.indexOf(' ') == -1) {
                protein = new FASTAProtein(headerLine.substring(0, headerLine.indexOf('|')), sequence, "");
            } else {
                protein = new FASTAProtein(headerLine.substring(0, headerLine.indexOf('|')), sequence, headerLine.substring(headerLine.indexOf('|')));
                protein.setDescription(headerLine.substring(headerLine.indexOf(' ') + 1));
            }
            protein.setHeaderItem("ID", headerLine.substring(headerLine.indexOf('|') + 1, headerLine.indexOf(' ')));
            return protein;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}

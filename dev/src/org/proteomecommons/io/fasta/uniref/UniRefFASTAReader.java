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

package org.proteomecommons.io.fasta.uniref;

import java.io.InputStream;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class UniRefFASTAReader extends FASTAReader {
    
    public UniRefFASTAReader(InputStream is) {
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
            // example header line: >UniRef100_Q4U9M9 Cluster: 104 kDa microneme-rhoptry antigen precursor; n=1; Theileria annulata|Rep: 104 kDa microneme-rhoptry antigen precursor - Theileria annulata
            FASTAProtein protein = null;
            if (headerLine.indexOf(' ') == -1) {
                protein = new FASTAProtein(headerLine.substring(10), sequence, "");
            } else {
                protein = new FASTAProtein(headerLine.substring(10, headerLine.indexOf(' ')), sequence, headerLine.substring(headerLine.indexOf(' ')));
                protein.setDescription(headerLine.substring(headerLine.indexOf(' ') + 1));
            }
            return protein;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}

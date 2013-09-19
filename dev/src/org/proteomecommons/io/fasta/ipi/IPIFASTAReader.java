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

package org.proteomecommons.io.fasta.ipi;

import java.io.InputStream;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class IPIFASTAReader extends FASTAReader {
    
    public IPIFASTAReader(InputStream is) {
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
            
            // get the name
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
            // example header line: >IPI:IPI00177321.1|REFSEQ_XP:XP_168060 Tax_Id=9606 similar to NOD3 protein
            FASTAProtein protein = null;
            if (headerLine.indexOf(' ') == -1) {
                protein = new FASTAProtein(headerLine, sequence, "");
            } else {
                protein = new FASTAProtein(headerLine.substring(4, headerLine.indexOf('.')), sequence, headerLine.substring(headerLine.indexOf('.')));
                headerLine = headerLine.substring(headerLine.indexOf(' ') + 1);
                
                // parse out the Tax_Id
                if (headerLine.startsWith("Tax_Id=")) {
                    protein.setHeaderItem("NCBITAXID", headerLine.substring(7, headerLine.indexOf(' ')));
                    headerLine = headerLine.substring(headerLine.indexOf(' ') + 1);
                }
                
                protein.setDescription(headerLine);
            }
            return protein;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}

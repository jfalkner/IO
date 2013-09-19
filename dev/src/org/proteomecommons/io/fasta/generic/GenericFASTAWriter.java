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

import org.proteomecommons.io.fasta.AbstractFASTAWriter;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class GenericFASTAWriter extends AbstractFASTAWriter {
    
    public GenericFASTAWriter(String filename) {
        super(filename);
    }
    
    /**
     * @param FASTAProtein
     * @throws IOException
     */
    public void write(FASTAProtein protein) {
        try {
            try {
                // write the header line
                getFileWriter().append('>' + protein.getAccession() + protein.getHeaderLineAfterAC() + '\n');
                
                // write the sequence
                int count = 0;
                for (char c : protein.getSequence().toCharArray()) {
                    count++;
                    getFileWriter().append(c);
                    // limit each line of the sequence to 75 characters
                    if (count % 75 == 0) {
                        getFileWriter().append('\n');
                    }
                }
                
                if (count % 75 != 0) {
                    getFileWriter().append('\n');
                }
            } finally {
                getFileWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @param protein   the protein to be output.
     * @param signifyReverse    the string to be added to the accession that sifnifies that the sequence is reversed.
     * @throws IOException
     */
    public void writeReverse(FASTAProtein protein, String signifyReverse) {
        try {
            try {
                // write the header line
                getFileWriter().append('>' + protein.getAccession() + signifyReverse + protein.getHeaderLineAfterAC() + '\n');
                
                // write the sequence in reverse
                char[] sequence = protein.getSequence().toCharArray();
                int count = 0;
                for (int i = sequence.length - 1; i >= 0; i--) {
                    count++;
                    getFileWriter().append(sequence[i]);
                    // limit each line of the sequence to 75 characters
                    if (count % 75 == 0 && i != 0) {
                        getFileWriter().append('\n');
                    }
                }
                
                getFileWriter().append('\n');
            } finally {
                getFileWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}

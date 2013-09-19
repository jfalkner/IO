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

import org.proteomecommons.io.fasta.AbstractFASTAWriter;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class IPIFASTAWriter extends AbstractFASTAWriter {
    
    public IPIFASTAWriter(String filename) {
        super(filename);
    }
    
    /**
     * @param FASTAProtein
     * @throws IOException
     */
    public void write(FASTAProtein protein) {
        
    }
    
    /**
     * @param FASTAProtein
     * @param String
     * @throws IOException
     */
    public void writeReverse(FASTAProtein protein, String signifyReverse) {
        
    }
    
}

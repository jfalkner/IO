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

package org.proteomecommons.io;

import junit.framework.TestCase;
import org.proteomecommons.io.ShuffledProteinReader;
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 *
 * @author Jayson
 */
public class ShuffledProteinReaderTest extends TestCase {
    public void testShuffledProteinReader() throws Exception {
        // make up a protein
        Protein protein = new Protein("Foo Protein", "ACDEHPLIK");
        // make a foo reader
        FooProteinReader fpr = new FooProteinReader(protein);
        
        // make a protein shuffler
        ShuffledProteinReader spr = new ShuffledProteinReader(fpr);
        
        // get the shuffled protein
        Protein shuffled = spr.next();
        
        // check that the length is the same
        if (shuffled.getSequence().length() != protein.getSequence().length()) {
            throw new Exception("ShuffledProteinReader is not producing proteins of the appropriate size.F");
        }
        
        // flag for similarity
        boolean sameResidues = true;
        // if the sequence is the same, throw an error
        Residue[] proteinResidues = protein.getSequenceAsResidues();
        Residue[] shuffledResidues = shuffled.getSequenceAsResidues();
        for (int i=0;i<proteinResidues.length;i++){
            if (!proteinResidues[i].equals(shuffledResidues[i])) {
                sameResidues = false;
            }
        }

        // if same, throw error
        if (sameResidues) {
            throw new Exception("Residues did not shuffle!");
        }
        
    }
    
}

class FooProteinReader extends GenericProteinReader {
    private boolean used = false;
    private Protein protein;
    public FooProteinReader(Protein protein){
        this.protein = protein;
    }
    public Protein next() {
        if (used){
            return null;
        }
        used = true;
        //return  a protein
        return protein;
    }
}

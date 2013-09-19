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
import java.util.LinkedList;
import org.proteomecommons.io.GenericProteinReader;
import org.proteomecommons.io.ProteinReader;
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 * This is a helper class that will take protein sequence data from another ProteinReader class and shuffle it pseudo-randomly. The change is noted in the protein's name by the addition of "(shuffled sequence)".
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ShuffledProteinReader extends GenericProteinReader {
    private ProteinReader pr;
    public ShuffledProteinReader(ProteinReader pr) {
        this.pr = pr;
    }
    public Protein next() {
        // get a protein from the passed reader
        Protein p = pr.next();
        
        // buffer the residues
        LinkedList buf = new LinkedList();
        // get the residues
        Residue[] residues = p.getSequenceAsResidues();
        for (int i=0;i<residues.length;i++){
            buf.add(residues[i]);
        }
        
        // remove randomly
        Residue[] shuffledResidues = new Residue[residues.length];
        for (int i=0;i<residues.length;i++){
            int rand = (int)(Math.random()*(buf.size()-1));
            // never put the same residue as the first residue
            // this prevents non-shuffling
            if (i==0 && rand ==0){
                i--;
                continue;
            }
            // insert a pseudo-random residue
            shuffledResidues[i] = (Residue)buf.remove(rand);
        }
        
        // return the modified protein
        return new Protein(p.getName()+"(shuffled sequence)", shuffledResidues);
    }
}
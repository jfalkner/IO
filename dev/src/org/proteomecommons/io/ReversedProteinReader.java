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
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 * This is a helper class that will take protein sequence data from another ProteinReader class and reverse it. The change is noted in the protein's name by the addition of "(reversed sequence)".
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class ReversedProteinReader extends GenericProteinReader {
    private ProteinReader pr;
    public ReversedProteinReader(ProteinReader pr) {
        this.pr = pr;
    }
    public Protein next() {
        // get a protein from the passed reader
        Protein p = pr.next();
        // null check
        if (p == null) {
            return null;
        }
        
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
            // insert a pseudo-random residue
            shuffledResidues[i] = residues[residues.length-i-1];
        }
        
        // return the modified protein
        return new Protein(p.getName()+"(reversed sequence)", shuffledResidues);
    }
}
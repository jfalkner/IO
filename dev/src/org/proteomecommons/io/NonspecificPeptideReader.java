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

import java.io.FileInputStream;

import org.proteomecommons.io.PeptideReader;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 * A peptide reader that reads non-specific cleaves from a fasta entry.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class NonspecificPeptideReader extends GenericPeptideReader {
    private Residue[] residues;
    private ProteinReader pr = null;
    
    private int startIndex = 0;
    private int endIndex = 0;
    
    public NonspecificPeptideReader(Residue[] residues) {
        this.residues = residues;
    }
    
    public NonspecificPeptideReader(ProteinReader pr){
        this.pr = pr;
    }
    
    public Peptide next() {
        // inc the index
        endIndex++;
        // return null if complete
        if (residues == null || startIndex >= residues.length){
            // use remaining proteins
            if(pr!=null){
                Protein protein = pr.next();
                if (protein!=null){
                    // set the new residues
                    residues = protein.getSequenceAsResidues();
                    // reset the residue index
                    this.startIndex = 0;
                    this.endIndex = 0;
                    // return the peptide
                    return next();
                }
            }
            return null;
        }
        
        // check the size and location of the end index
        if (endIndex-startIndex> this.getMaxPeptideLength() || endIndex > residues.length){
            // if to big, reset to the next sequence
            startIndex++;
            endIndex = startIndex;
            return next();
        }
        
        // make a peptide
        Residue[] res = new Residue[endIndex-startIndex];
        // return the peptide
        System.arraycopy(residues, startIndex, res, 0, res.length);
        Peptide peptide = new Peptide(res);
        
        // check the size and location of the end index
        if (peptide.getMass()> getMaxPeptideMass()){
            // if to big, reset to the next sequence
            startIndex++;
            endIndex = startIndex;
            return next();
        }
        
        // if not valid, skip
        if(!isValid(peptide)){
            return next();
        }
        
        // return the new peptide
        return peptide;
    }
}
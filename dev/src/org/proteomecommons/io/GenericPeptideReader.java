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

import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;

/**
 * An abstract base class with helper methods for reading and filtering peptides.
 * @author Jayson
 */
public abstract class GenericPeptideReader implements PeptideReader {
    // min length in residues that a peptide must have
    private int minPeptideLength = 0;
    
    // max length in residues that a peptide must have
    private int maxPeptideLength = Integer.MAX_VALUE;
    
    // min mass that a peptide must have
    private double minPeptideMass = 0;
    
    // max mass that a peptide must have
    private double maxPeptideMass = Double.MAX_VALUE;
    
    public abstract Peptide next();
    
    public boolean isValid(Peptide p){
        // check for mass violations
        if (p.getMass() < minPeptideMass || p.getMass() > maxPeptideMass){
            return false;
        }
        // check for size violations
        if (p.getResidues().length < minPeptideLength || p.getResidues().length > maxPeptideLength){
            return false;
        }
        return true;
    }
    
    public int getMinPeptideLength() {
        return minPeptideLength;
    }
    
    public void setMinPeptideLength(int minPeptideLength) {
        this.minPeptideLength = minPeptideLength;
    }
    
    public int getMaxPeptideLength() {
        return maxPeptideLength;
    }
    
    public void setMaxPeptideLength(int maxPeptideLength) {
        this.maxPeptideLength = maxPeptideLength;
    }
    
    public double getMinPeptideMass() {
        return minPeptideMass;
    }
    
    public void setMinPeptideMass(double minPeptideMass) {
        this.minPeptideMass = minPeptideMass;
        // auto correct for min length
        if (minPeptideLength == 0){
            minPeptideLength = (int)( minPeptideMass / Residue.W.getMassInDaltons());
        }
    }
    
    public double getMaxPeptideMass() {
        return maxPeptideMass;
    }
    
    public void setMaxPeptideMass(double maxPeptideMass) {
        this.maxPeptideMass = maxPeptideMass;
        // auto correct for max length
        if (maxPeptideLength == Integer.MAX_VALUE){
            maxPeptideLength = (int)( maxPeptideMass / Residue.G.getMassInDaltons());
        }
    }
    
    
}

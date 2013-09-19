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

import java.util.Iterator;
import java.util.LinkedList;
import org.proteomecommons.jaf.ModifiedResidue;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;


/**
 * A PeptideReader that will account for potential modification possibilities.
 * @author Jayson
 */
public class PotentialModificationPeptideReader extends GenericPeptideReader {
    // the source reader to modify
    private PeptideReader pr;
    // list of possible mods
    private LinkedList potentialModifications = new LinkedList();
    // keep a stack of possible peptides
    LinkedList stack = new LinkedList();
    // flag for repeating unmodified peptides
    boolean showUnmodifiedPeptides = false;
    private int maxModifications = 3;
    
    /** Creates a new instance of PotentialModificationPeptideReader */
    public PotentialModificationPeptideReader(PeptideReader pr) {
        this.pr = pr;
    }
    
    public void addPotentialModification(Residue r) {
        potentialModifications.add(r);
    }
    public Residue[] getPotentialModifications() {
        return (Residue[])potentialModifications.toArray(new Residue[potentialModifications.size()]);
    }
    public void setShowUnmodifiedPeptides(boolean flag){
        this.showUnmodifiedPeptides = flag;
    }
    public boolean getShowUnmodifiedPeptides() {
        return showUnmodifiedPeptides;
    }
    public int getMaxModification(){
        return maxModifications;
    }
    public void setMaxModifications(int maxMods){
        this.maxModifications = maxMods;
    }
    
    public Peptide next() {
        // use the stack if needed
        if (stack.size()>0){
            Peptide p = (Peptide)stack.removeFirst();
            return p;
        }
        
        // buffer and stack a new peptide
        Peptide p = pr.next();
        // if null, return null
        if (p == null){
            return null;
        }
        
        // buffer for multi-mods
        LinkedList multiMods = new LinkedList();
        // start with the original residue
        multiMods.add(p);
        
        // try modifying at each residue
        for (int i=0;i<p.getResidues().length&&multiMods.size()<Math.pow(2, maxModifications);i++){
            LinkedList tempMods = new LinkedList();
            // mod each possibility
            for (Iterator z = multiMods.iterator();z.hasNext();){
                Peptide zp = (Peptide)z.next();
                // try altering the residues
                Residue[] residues = zp.getResidues();
                // look for places to mod based on FASTA char
                for (Iterator it = potentialModifications.iterator();it.hasNext();){
                    ModifiedResidue r = (ModifiedResidue)it.next();
                    // enforce n-term specific
                    if (r.getModification().isNTerminusOnly() && i!=0){
                        continue;
                    }
                    // enforce c-term specific
                    if (r.getModification().isCTerminusOnly() && i!=residues.length-1){
                        continue;
                    }
                    if (r.getBaseResidue().getName().equals(residues[i].getName())){
                        // make a new possible match
                        Residue[] modifiedResidues = new Residue[residues.length];
                        System.arraycopy(residues, 0, modifiedResidues, 0, residues.length);
                        modifiedResidues[i] = r;
                        // make the peptide
                        Peptide modifiedPeptide = new Peptide(modifiedResidues);
                        // push to the stack
                        tempMods.add(modifiedPeptide);
                    }
                }
            }
            // add all the mods for that location in the residue
            multiMods.addAll(tempMods);
        }
        // if unmodified peptides are not to be shown, remove it
        if (!showUnmodifiedPeptides){
            multiMods.removeFirst();
        }
        
        // add all the mods to the stack
        stack.addAll(multiMods);
        
        // return the top of the stack
        return next();
    }
    
    public void setMinPeptideMass(double minPeptideMass) {
        super.setMinPeptideMass(minPeptideMass);
        if (pr != null && pr instanceof GenericPeptideReader){
            GenericPeptideReader gpr = (GenericPeptideReader)pr;
            gpr.setMinPeptideMass(minPeptideMass);
        }
    }
    
    public void setMaxPeptideMass(double maxPeptideMass) {
        super.setMaxPeptideMass(maxPeptideMass);
        if (pr != null && pr instanceof GenericPeptideReader){
            GenericPeptideReader gpr = (GenericPeptideReader)pr;
            gpr.setMaxPeptideMass(maxPeptideMass);
        }
    }
    
    public void setMinPeptideLength(int minPeptideLength) {
        super.setMinPeptideLength(minPeptideLength);
        if (pr != null && pr instanceof GenericPeptideReader){
            GenericPeptideReader gpr = (GenericPeptideReader)pr;
            gpr.setMinPeptideLength(minPeptideLength);
        }
    }
    
    public void setMaxPeptideLength(int maxPeptideLength) {
        super.setMaxPeptideLength(maxPeptideLength);
        if (pr != null && pr instanceof GenericPeptideReader){
            GenericPeptideReader gpr = (GenericPeptideReader)pr;
            gpr.setMaxPeptideLength(maxPeptideLength);
        }
    } 
}

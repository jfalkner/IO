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
import org.proteomecommons.jaf.Atom;
import org.proteomecommons.jaf.GenericModifiedResidue;
import org.proteomecommons.jaf.GenericResidue;
import org.proteomecommons.jaf.ModifiedResidue;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;


/**
 *
 * @author Jayson
 */
public class PotentialModificationPeptideReaderTest extends TestCase{
    
    public void testPotentialModificationPeptideReader() throws Exception {
        // make a known peptide
        TrypticPeptideReader tpr = new TrypticPeptideReader(GenericResidue.getResidues("ACMK"));
        // pipe in the reader
        PotentialModificationPeptideReader pmpr = new PotentialModificationPeptideReader(tpr);
        pmpr.setShowUnmodifiedPeptides(true);
        // set a new modification
        GenericModifiedResidue mr = new GenericModifiedResidue("Foo Bar", Residue.A, new Atom[0], new Atom[0]);
        pmpr.addPotentialModification(mr);
        
        // test what is made
        if (!pmpr.next().toString().equals("ACMK")) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        Peptide mod = pmpr.next();
        if (!mod.getResidues()[0].equals(mr)) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        if (pmpr.next()!= null) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
    }
    
    public void testDualPotentialModificationPeptideReader() throws Exception {
        // make a known peptide
        TrypticPeptideReader tpr = new TrypticPeptideReader(GenericResidue.getResidues("AACMK"));
        // pipe in the reader
        PotentialModificationPeptideReader pmpr = new PotentialModificationPeptideReader(tpr);
        pmpr.setShowUnmodifiedPeptides(true);
        // set a new modification
        GenericModifiedResidue mr = new GenericModifiedResidue("Foo Bar", Residue.A, new Atom[0], new Atom[0]);
        pmpr.addPotentialModification(mr);
        
//        for(Peptide p=pmpr.next();p!=null;p=pmpr.next()) {
//            Residue[] res = p.getResidues();
//            for (int i=0;i<res.length;i++){
//                if (res[i] instanceof ModifiedResidue) {
//                    System.out.print(res[i].getName());
//                } else {
//                    System.out.print(res[i].getFASTAChar());
//                }
//                
//            }
//            System.out.println();
//        }
        
        // test what is made
        if (!pmpr.next().toString().equals("AACMK")) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        Peptide mod = pmpr.next();
        if (!mod.getResidues()[0].equals(mr)||mod.getResidues()[1].equals(mr)) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        Peptide mod2 = pmpr.next();
        if (!mod2.getResidues()[1].equals(mr)||mod2.getResidues()[0].equals(mr)) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        Peptide mod3 = pmpr.next();
        if (!mod3.getResidues()[1].equals(mr)||!mod3.getResidues()[0].equals(mr)) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
        // test what is made
        if (pmpr.next()!= null) {
            throw new Exception("PotentialModificationPeptideReader isn't correctly modifying peptides.");
        }
    }
    
}

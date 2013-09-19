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

import java.io.ByteArrayInputStream;
import junit.framework.TestCase;
import org.proteomecommons.io.fasta.generic.GenericFASTAReader;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;

/**
 *
 * @author Jayson
 */
public class TrypticPeptideReaderTest extends TestCase {
    
    public void testTrypticPeptideReader() throws Exception {
        // make a residue array
        Peptide p = new Peptide("ACKACRDDEEK");
        // get the residues
        Residue[] res = p.getResidues();
        // make the reader
        TrypticPeptideReader tpr = new TrypticPeptideReader(res);
        // set some constraints
        tpr.setMinPeptideLength(0);
        tpr.setMinPeptideMass(0);
        tpr.setMaxPeptideLength(Integer.MAX_VALUE);
        tpr.setMaxPeptideMass(Double.MAX_VALUE);
        // flag for testing validity
        boolean test = false;
        // test known results
        test |= !tpr.next().toString().equals("ACK");
        test |= !tpr.next().toString().equals("ACR");
        test |= !tpr.next().toString().equals("DDEEK");
        
        // if any test failed, raise an exception
        if (test){
            throw new Exception("NonspecificPeptideReader class isn't correctly creating peptides.");
        }
    }
    
    public void testMultiProteinTrypticPeptideReader() throws Exception {
        // build a fasta reader
        String p1 = "ACK";
        String p2 = "ADK";
        String p3 = "AEFK";
        String fasta = ">foo\n"+p1+p2+">bar\r\nAE\nFK>foo bar\n"+p2+p1+p3;
        // write to a temp file
        ByteArrayInputStream bais = new ByteArrayInputStream(fasta.getBytes());
        GenericFASTAReader fpr = new GenericFASTAReader(bais);
        // make the reader
        TrypticPeptideReader tpr = new TrypticPeptideReader(fpr);
        // set some constraints
        tpr.setMinPeptideLength(0);
        tpr.setMinPeptideMass(0);
        tpr.setMaxPeptideLength(Integer.MAX_VALUE);
        tpr.setMaxPeptideMass(Double.MAX_VALUE);
        // flag for testing validity
        boolean test = false;
        // test foo
        test |= !tpr.next().toString().equals(p1);
        test |= !tpr.next().toString().equals(p2);
        // test bar
        test |= !tpr.next().toString().equals(p3);
        // test foo bar
        test |= !tpr.next().toString().equals(p2);
        test |= !tpr.next().toString().equals(p1);
        test |= !tpr.next().toString().equals(p3);

        // if any test failed, raise an exception
        if (test){
            throw new Exception("NonspecificPeptideReader class isn't correctly creating peptides.");
        }
    }
    
}

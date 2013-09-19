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
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;

/**
 *
 * @author Jayson
 */
public class MissedCleavePeptideReaderTest extends TestCase{
    
   public void testMissedCleavePeptideReader() throws Exception {
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
       
       // pipe through a missed cleave test
       MissedCleavePeptideReader mcpr = new MissedCleavePeptideReader(tpr);
       // set some constraints
       mcpr.setMinPeptideLength(0);
       mcpr.setMinPeptideMass(0);
       mcpr.setMaxPeptideLength(Integer.MAX_VALUE);
       mcpr.setMaxPeptideMass(Double.MAX_VALUE);
       mcpr.setMaxMissedCleaves(1);

       // flag for testing validity
       boolean test = false;
       // test known results
       test |= !mcpr.next().toString().equals("ACK");
       test |= !mcpr.next().toString().equals("ACKACR");
       test |= !mcpr.next().toString().equals("ACR");
       test |= !mcpr.next().toString().equals("ACRDDEEK");
       test |= !mcpr.next().toString().equals("DDEEK");
       
       // if any test failed, raise an exception
       if (test){
         throw new Exception("MissedCleavePeptideReader class isn't correctly creating peptides.");
       }
   }
}

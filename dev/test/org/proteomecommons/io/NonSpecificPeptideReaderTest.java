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
public class NonSpecificPeptideReaderTest extends TestCase{
    
   public void testNonspecificPeptideReader() throws Exception {
       // make a residue array
       Peptide p = new Peptide("ACK");
       // get the residues
       Residue[] res = p.getResidues();
       // make the reader
       NonspecificPeptideReader nspr = new NonspecificPeptideReader(res);
       // set some constraints
       nspr.setMinPeptideLength(0);
       nspr.setMinPeptideMass(0);
       nspr.setMaxPeptideLength(Integer.MAX_VALUE);
       nspr.setMaxPeptideMass(Double.MAX_VALUE);
       // flag for testing validity
       boolean test = false;
       // test known results
       test |= !nspr.next().toString().equals("A");
       test |= !nspr.next().toString().equals("AC");
       test |= !nspr.next().toString().equals("ACK");
       test |= !nspr.next().toString().equals("C");
       test |= !nspr.next().toString().equals("CK");
       test |= !nspr.next().toString().equals("K");
       
       // if any test failed, raise an exception
       if (test){
         throw new Exception("NonspecificPeptideReader class isn't correctly creating peptides.");
       }
   }
}

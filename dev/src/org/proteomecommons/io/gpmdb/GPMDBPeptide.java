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

package org.proteomecommons.io.gpmdb;

import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GPMDBPeptide extends Peptide {
    private String proteinID;

    public GPMDBPeptide(String string, String proteinID) throws Exception {
        super(string);
        this.proteinID = proteinID;
    }
    public GPMDBPeptide(Residue[] residues, String proteinID) {
        super(residues);
        this.proteinID = proteinID;
    }
    
    public String getProteinID() {
        return proteinID;
    }

    public void setProteinID(String proteinID) {
        this.proteinID = proteinID;
    }
    
}

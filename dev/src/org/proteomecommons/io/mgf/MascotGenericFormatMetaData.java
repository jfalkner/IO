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
package org.proteomecommons.io.mgf;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * Contains Meta Data of Mascot Generic Format file (.mgf). These are file-level
 * meta data. Note that meta data within PeakLists in .mgf files are stored in
 * PeakList instances.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */

public class MascotGenericFormatMetaData {
    private String queryIdentifier = null;
    private String peptideMass = null;
    private String peptideCharge = null;
    /**
     *Gets the peptide's charge, i.e. CHARGE.
     */
    public String getPeptideCharge() {
        return peptideCharge;
    }
    public void setPeptideCharge(String peptideCharge){
        this.peptideCharge = peptideCharge;
    }
    /**
     * Gets the query identifier, i.e. TITLE.
     */
    public String getQueryIdentifier() {
        return queryIdentifier;
    }
    public void setQueryIdentifier(String qi){
        this.queryIdentifier = qi;
    }
    /**
     * Gets the peptide's mass, i.e. PEPMASS.
     */
    public String getPeptideMass() {
        return peptideMass;
    }
    public void setPeptideMass(String peptideMass){
        this.peptideMass = peptideMass;
    }
}
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
package org.proteomecommons.io.mzxml.v1_1_1;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MsInstrument {
    private MsManufacturer msManufacturer = null;
    private MsModel msModel = null;
    private MsIonisation msIonisation = null;
    private MsMassAnalyzer msMassAnalyzer = null;
    private MsDetector msDetector = null;
    private MsResolution msResolution = null;
    private Software software = null;
    private Operator operator = null;

    public MsManufacturer getMsManufacturer() {
        return msManufacturer;
    }

    public void setMsManufacturer(MsManufacturer msManufacturer) {
        this.msManufacturer = msManufacturer;
    }

    public MsModel getMsModel() {
        return msModel;
    }

    public void setMsModel(MsModel msModel) {
        this.msModel = msModel;
    }

    public MsIonisation getMsIonisation() {
        return msIonisation;
    }

    public void setMsIonisation(MsIonisation msIonisation) {
        this.msIonisation = msIonisation;
    }

    public MsMassAnalyzer getMsMassAnalyzer() {
        return msMassAnalyzer;
    }

    public void setMsMassAnalyzer(MsMassAnalyzer msMassAnalyzer) {
        this.msMassAnalyzer = msMassAnalyzer;
    }

    public MsDetector getMsDetector() {
        return msDetector;
    }

    public void setMsDetector(MsDetector msDetector) {
        this.msDetector = msDetector;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public MsResolution getMsResolution() {
        return msResolution;
    }

    public void setMsResolution(MsResolution msResolution) {
        this.msResolution = msResolution;
    }
}

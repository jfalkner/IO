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

import org.proteomecommons.io.GenericPeakList;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MzXMLPeakList extends GenericPeakList {
    // keep track of msRun information
    private MsRun msRun = null;
    
    // meta-info
    private String num = null;
    private String msLevel = null;
    private String peaksCount = null;
    private String polarity = null;
    private String scanType = null;
    private String centroided = null;
    private String deisotoped = null;
    private String chargeDeconvoluted = null;
    private String retentionTime = null;
    private String ionisationEnergy = null;
    private String collisionEnergy = null;
    private String startMz = null;
    private String endMz = null;
    private String lowMz = null;
    private String highMz = null;
    private String basePeakMz = null;
    private String basePeakIntensity = null;
    private String totIonCurrent = null;
    private String precursorScanNum = null;
    
    public MsRun getMsRun() {
        return msRun;
    }
    
    public void setMsRun(MsRun msRun) {
        this.msRun = msRun;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(String msLevel) {
        this.msLevel = msLevel;
    }

    public String getPeaksCount() {
        return peaksCount;
    }

    public void setPeaksCount(String peaksCount) {
        this.peaksCount = peaksCount;
    }

    public String getPolarity() {
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public String getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(String retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getLowMz() {
        return lowMz;
    }

    public void setLowMz(String lowMz) {
        this.lowMz = lowMz;
    }

    public String getHighMz() {
        return highMz;
    }

    public void setHighMz(String highMz) {
        this.highMz = highMz;
    }

    public String getBasePeakMz() {
        return basePeakMz;
    }

    public void setBasePeakMz(String basePeakMz) {
        this.basePeakMz = basePeakMz;
    }

    public String getBasePeakIntensity() {
        return basePeakIntensity;
    }

    public void setBasePeakIntensity(String basePeakIntensity) {
        this.basePeakIntensity = basePeakIntensity;
    }

    public String getTotIonCurrent() {
        return totIonCurrent;
    }

    public void setTotIonCurrent(String totIonCurrent) {
        this.totIonCurrent = totIonCurrent;
    }

    public String getPrecursorScanNum() {
        return precursorScanNum;
    }

    public void setPrecursorScanNum(String precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getCentroided() {
        return centroided;
    }

    public void setCentroided(String centroided) {
        this.centroided = centroided;
    }

    public String getDeisotoped() {
        return deisotoped;
    }

    public void setDeisotoped(String deisotoped) {
        this.deisotoped = deisotoped;
    }

    public String getChargeDeconvoluted() {
        return chargeDeconvoluted;
    }

    public void setChargeDeconvoluted(String chargeDeconvoluted) {
        this.chargeDeconvoluted = chargeDeconvoluted;
    }

    public String getIonisationEnergy() {
        return ionisationEnergy;
    }

    public void setIonisationEnergy(String ionisationEnergy) {
        this.ionisationEnergy = ionisationEnergy;
    }

    public String getCollisionEnergy() {
        return collisionEnergy;
    }

    public void setCollisionEnergy(String collisionEnergy) {
        this.collisionEnergy = collisionEnergy;
    }

    public String getStartMz() {
        return startMz;
    }

    public void setStartMz(String startMz) {
        this.startMz = startMz;
    }

    public String getEndMz() {
        return endMz;
    }

    public void setEndMz(String endMz) {
        this.endMz = endMz;
    }
    
    public boolean isCentroided() {
        try {
            boolean value = Boolean.parseBoolean(centroided);
            return value;
        } catch (Exception e) {
            // noop
        }
        return false;
    }
    
    public boolean isDeisotoped() {
        try {
            boolean value = Boolean.parseBoolean(deisotoped);
            return value;
        } catch (Exception e) {
            // noop
        }
        return false;
    }
    
    public boolean isChargeDeconvolution() {
        try {
            boolean value = Boolean.parseBoolean(chargeDeconvoluted);
            return value;
        } catch (Exception e) {
            // noop
        }
        return false;
    }
}

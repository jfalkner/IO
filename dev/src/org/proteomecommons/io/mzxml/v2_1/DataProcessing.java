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
package org.proteomecommons.io.mzxml.v2_1;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DataProcessing {
    private Software software = null;
    private String intensityCutoff = null;
    private String centroided = null;
    private String deisotoped = null;
    private String chargeDeconvoluted = null;
    private String spotIntegration = null;
    
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
    
    public boolean isSpotIntegration() {
        try {
            boolean value = Boolean.parseBoolean(spotIntegration);
            return value;
        } catch (Exception e) {
            // noop
        }
        return false;
    }
    
    public Software getSoftware() {
        return software;
    }
    
    public void setSoftware(Software software) {
        this.software = software;
    }
    
    public String getIntensityCutoff() {
        return intensityCutoff;
    }
    
    public void setIntensityCutoff(String intensityCutoff) {
        this.intensityCutoff = intensityCutoff;
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
    
    public String getSpotIntegration() {
        return spotIntegration;
    }
    
    public void setSpotIntegration(String spotIntegration) {
        this.spotIntegration = spotIntegration;
    }
}

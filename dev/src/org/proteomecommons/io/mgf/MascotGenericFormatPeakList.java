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

/**
 * A MGF-specific peak list class. This class carries meta-info that is specific
 * to the Mascot search engine.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MascotGenericFormatPeakList extends GenericPeakList {
    private String pepmass = null;
    private String charge = null;
    private String title = null;
    
    public String getPepmass() {
        return pepmass;
    }
    
    public void setPepmass(String pepmass) {
        this.pepmass = pepmass;
    }
    
    public String getCharge() {
        return charge;
    }
    
    public void setCharge(String charge) {
        this.charge = charge;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Peak getParentPeak() {
        if (pepmass != null) {
            // fall back on making a generic peak
            GenericPeak gp = new GenericPeak();
            // check for known pepmass and intensity
            if (getPepmass() != null) {
                String[] split = getPepmass().split("=|\\s");
                // first in the split would be the m/z
                if (split.length > 1) {
                    try {
                        gp.setMassOverCharge(Double.parseDouble(split[1]));
                    } catch (NumberFormatException ex) {
                        // noop
                    }
                }
                // second in the split would be the intensity
                if (split.length > 2) {
                    try {
                        gp.setIntensity(Double.parseDouble(split[2]));
                    } catch (NumberFormatException ex) {
                        // noop
                    }
                }
            }
            // check for charge
            if (getCharge() != null && !getCharge().trim().equals("")) {
                boolean negative = getCharge().indexOf("-") != -1;
                if (negative) {
                    try {
                        String chargeString = getCharge().split("=")[1].trim();
                        chargeString = chargeString.replace("-", "");
                        gp.setCharge(-1 * Integer.parseInt(chargeString));
                    } catch (NumberFormatException ex) {
                        // noop
                    }
                } else {
                    try {
                        String chargeString = getCharge().split("=")[1].trim();
                        chargeString = chargeString.replace("+", "");
                        gp.setCharge(Integer.parseInt(chargeString));
                    } catch (NumberFormatException ex) {
                        // noop
                    }
                }
                
            }
            return gp;
        }
        return super.getParentPeak();
    }
}
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
package org.proteomecommons.io.filter;

import java.util.ArrayList;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class RecalibratePeakListFilter implements PeakListFilter {
    // the PPM to recalibrate by
    double ppm;
    public RecalibratePeakListFilter(double ppm) {
        this.ppm = ppm;
    }
    public PeakList filter(PeakList peaklist) {
        // get the peaks
        Peak[] peaks = peaklist.getPeaks();
        
        // make a new set of peaks
        ArrayList<Peak> recalibratedPeaks = new ArrayList();
        
        // make a new peak list
        for (Peak peak : peaks) {
            // get the m/z recalibration shift
            double shift = ppm*peak.getMassOverCharge()/1000000;
            // make the new peak
            GenericPeak gp = new GenericPeak();
            gp.setMassOverCharge(peak.getMassOverCharge()+shift);
            gp.setIntensity(peak.getIntensity());
            gp.setCentroided(peak.getCentroided());
            gp.setCharge(peak.getCharge());
            gp.setMonoisotopic(peak.getMonoisotopic());
            // add to the list
            recalibratedPeaks.add(gp);
        }
        
        // make a new peaklist
        GenericPeakList gpl = new GenericPeakList();
        gpl.setParentPeak(peaklist.getParentPeak());
        gpl.setPeaks(recalibratedPeaks.toArray(new Peak[0]));
        
        // return the recalibrated peak list
        return gpl;
    }
}

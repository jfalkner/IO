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

import java.util.Arrays;

import org.proteomecommons.io.*;
import org.proteomecommons.io.util.*;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.jaf.Atom;
import org.proteomecommons.jaf.util.CalculateIsotopeDistribution;

/**
 * A filter that takes centroided data and assgins charge state. This class will
 * not work on monoisotopic peak lists.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class AssignChargePeakListFilter implements PeakListFilter {
    private PeakMatcher peakMatcher = new PeakMatcher();
    
    private double minOfBasePeakPercent = 1;
    
    private int maxChargeState = 4;
    
    private double allowedIsotopeDeviation = 0.2;
    
    public void setMaxChargeState(int maxChargeState) {
        this.maxChargeState = maxChargeState;
    }
    
    public int getMaxChargeState() {
        return maxChargeState;
    }
    
    public double getAllowedIsotopeDeviation() {
        return allowedIsotopeDeviation;
    }
    public void setAllowedIsotopeDeviation(double allowedDeviation){
        this.allowedIsotopeDeviation = allowedDeviation;
    }
    
    /**
     * Sets the minimum counts that a peak must have to be considered a
     * surviving peak.
     *
     * @param percent
     */
    public void setMinOfBasePeakPercent(double percent) {
        this.minOfBasePeakPercent = percent;
    }
    
    /**
     * Gets the minimum number of counts a peak must have to be considered a
     * surviving peak.
     *
     * @return
     */
    public double getMinOfBasePeakPercent() {
        return minOfBasePeakPercent;
    }
    
    /**
     * Set the PeakMatcher class that is used for matching peaks.
     *
     * @param ppm
     */
    public void setPeakMatcher(PeakMatcher pm) {
        this.peakMatcher = pm;
    }
    
    /**
     * A getter method for the utility code that matches peaks. You can configure the PeakMatcher class for your particular needs.
     *
     * @return
     */
    public PeakMatcher getPeakMatcher() {
        return peakMatcher;
    }
    
    /**
     * @see org.proteomecommons.io.filter.PeakListFilter#filter(org.proteomecommons.io.PeakList)
     */
    public PeakList filter(PeakList peaklist) {
        // sort the peaks for consistency
        Peak[] peaks = peaklist.getPeaks();
        Arrays.sort(peaks);
        
        // make new peaks
        GenericPeak[] acs = new GenericPeak[peaks.length];
        for (int i=0;i<peaks.length;i++){
            // skip if it is already charge assigned
            if (peaks[i] instanceof GenericPeak){
                acs[i] = (GenericPeak)peaks[i];
                continue;
            }
            acs[i] = new GenericPeak();
            acs[i].setIntensity(peaks[i].getIntensity());
            acs[i].setMassOverCharge(peaks[i].getMassOverCharge());
        }
        
        
        // assign charges to the peaks
        assignChargeStates((GenericPeak[])acs);
        
//		//TODO: fix this....
//		if (true){
//			throw new RuntimeException("Can't assign charge states.");
//		}
        ((GenericPeakList)peaklist).setPeaks(acs);
        return peaklist;
    }
    // helper method to assign charge states
    private void assignChargeStates(GenericPeak[] peaks) {
        // make isotope calculators
        CalculateIsotopeDistribution cid = new CalculateIsotopeDistribution();
        cid.setIncrement(Atom.H.getMassInDaltons());
        
        // look for matching isotopes
        for (int i = 0; i < peaks.length; i++) {
            // track best distance
            double best = Double.MAX_VALUE;
            //			System.out.println(peaks[i].massOverChargeInDaltons);
            boolean foundCharge = false;
            // try all valid charge states
            for (int charge = 1; charge <= maxChargeState; charge++) {
                // find the next best peak for this charge state
                double mz = peaks[i].getMassOverCharge()
                + Atom.H.getMassInDaltons() / charge;
                Peak next = peakMatcher.match(peaks, mz);
                if (next == null) {
                    continue;
                }
                
                // calc how far off the mass shift is
                double distance = Math.abs(Math.abs(next.getMassOverCharge()
                - peaks[i].getMassOverCharge())
                - Atom.H.getMassInDaltons() / charge);
                // calculate how far off the expected isotope intensity is
                double isotopeMz = peaks[i].getMassOverCharge() * charge - ((charge - 1) * Atom.H.getMassInDaltons());
                double[] dist = cid.approximateIsotopeDistribution(isotopeMz);
                double isotopeError = Math
                        .abs((peaks[i].getIntensity() / dist[0]) * dist[1]
                        - next.getIntensity());
                
                // skip if the expected isotope error is too far off
                double isotopeDeviation = isotopeError
                        / (peaks[i].getIntensity() / dist[0]);
                
                // if the deviation is too far off, skip assigning charge
                if (isotopeDeviation > allowedIsotopeDeviation){
                    continue;
                }
                
                // note that the charge was found
                foundCharge = true;
                // assign the charge
                peaks[i].setCharge(charge);
                peaks[i + 1].setCharge(charge);
            }
            // assign unkonwn charge
            if (!foundCharge) {
                peaks[i].setCharge(Peak.UNKNOWN_CHARGE);
            }
        }
    }
}
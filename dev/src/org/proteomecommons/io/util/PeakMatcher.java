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
package org.proteomecommons.io.util;

import java.util.Arrays;

import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.Peak;

/**
 * A consolidated location for all peak matching code. Use this class to
 * effectively abstract any peak matching that you need.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class PeakMatcher {
    private double ppmTol = 10;
    
    private double min = 0.03;
    
    public PeakMatcher() {
        // noop
    }
    
    public PeakMatcher(double ppmTolerance, double minTolerance){
        this.ppmTol = ppmTolerance;
        this.min = minTolerance;
    }
    
    public double getPPMTolerance() {
        return ppmTol;
    }
    
    public void setPPMTolerance(double tolerance) {
        this.ppmTol = tolerance;
    }
    
    /**
     * Retruns the smallest value by which peaks are matched. For example, a
     * 10ppm mass accuracy on small peaks gives a ridicuously small number,
     * often unrealistically so. If the min tolerance is set, it is assumed that
     * any peak within that value must be a match regardless of the PPM cutoff.
     *
     * @return
     */
    public double getMinTolerance() {
        return min;
    }
    
    /**
     * Sets the smallest value by which peaks are matched. For example, a 10ppm
     * mass accuracy on small peaks gives a ridicuously small number, often
     * unrealistically so. If the min tolerance is set, it is assumed that any
     * peak within that value must be a match regardless of the PPM cutoff.
     *
     * @return
     */
    public void setMinTolerance(double tolerance) {
        this.min = tolerance;
    }
    
    /**
     * Efficiently determine if there is a matching peak in the given array of
     * peaks. This method using a binary search for efficiency.
     *
     * @param peaks
     * @param gp
     * @return
     */
    public Peak match(Peak[] peaks, double mz) {
        // find the index
        GenericPeak gp = new GenericPeak();
        gp.setMassOverCharge(mz);
        return match(peaks, gp);
        
    }
    public Peak matchReturnMostIntense(Peak[] peaks, double mz) {
        // find the index
        GenericPeak gp = new GenericPeak();
        gp.setMassOverCharge(mz);
        return matchReturnMostIntense(peaks, gp);
        
    }
    
    /**
     * Efficiently determine if there is a matching peak in the given array of
     * peaks. This method using a binary search for efficiency.
     *
     * @param peaks
     * @param gp
     * @return
     */
    public Peak match(Peak[] peaks, Peak gp) {
        // calc the max error once
        double error = ppmTol * gp.getMassOverCharge() / 1000000;
        if (error < min) {
            error = min;
        }
        
        // skip null
        if (peaks==null|| peaks.length <2 || gp == null){
            return null;
        }
        
        // find the index
        int index = Arrays.binarySearch(peaks, gp);
        if (index < 0) {
            index = -1 * (index + 1);
        }
        // find the best match
        Peak bestMatch = null;
        // check below
        double diffBelow = Double.MAX_VALUE;
        if (index >= 0 && index < peaks.length) {
            diffBelow = Math.abs(peaks[index].getMassOverCharge()
            - gp.getMassOverCharge());
            if (diffBelow <= error) {
                bestMatch = peaks[index];
            }
        }
        // check above
        double diffAbove = Double.MAX_VALUE;
        if (index - 1 >= 0 && index - 1 < peaks.length) {
            diffAbove = Math.abs(peaks[index - 1].getMassOverCharge()
            - gp.getMassOverCharge());
            if (diffAbove <= error && diffAbove < diffBelow) {
                bestMatch = peaks[index - 1];
            }
        }
        // fall back on false
        return bestMatch;
    }
    
    /**
     * Efficiently determine if there is a matching peak in the given array of
     * peaks. This method using a binary search for efficiency.
     *
     * @param peaks
     * @param gp
     * @return
     */
    public Peak matchReturnMostIntense(Peak[] peaks, Peak gp) {
        // calc the max error once
        double error = ppmTol * gp.getMassOverCharge() / 1000000;
        if (error < min) {
            error = min;
        }
        
        // skip null
        if (peaks==null|| peaks.length <2 || gp == null){
            return null;
        }
        
        // find the index
        int index = Arrays.binarySearch(peaks, gp);
        if (index < 0) {
            index = -1 * (index + 1);
        }
        // find the best match
        Peak bestMatch = null;
        // check below
        while (index >= 0 && index < peaks.length) {
            double diff = Math.abs(peaks[index].getMassOverCharge()
            - gp.getMassOverCharge());
            if (diff <= error && (bestMatch == null || bestMatch.getIntensity()<peaks[index].getIntensity())) {
                bestMatch = peaks[index];
            }
            // skip if error is too large
            else {
                break;
            }
            index++;
        }
        
        // check above
        while (index - 1 >= 0 && index - 1 > 0) {
            double diff = Math.abs(peaks[index - 1].getMassOverCharge()
            - gp.getMassOverCharge());
            if (diff <= error && (bestMatch == null || bestMatch.getIntensity()<peaks[index-1].getIntensity())) {
                bestMatch = peaks[index-1];
            }
            // skip if error is too large
            else {
                break;
            }
            index--;
        }
        // fall back on false
        return bestMatch;
    }
    
    /**
     * Helper method to find the most intense peak amongst a collection of
     * peaks.
     *
     * @param peaks
     * @return The most intense peak or null if no peaks are found.
     */
    public static Peak findBasePeak(Peak[] peaks) {
        // null check
        if (peaks == null || peaks.length==0){
            return null;
        }
        // start at the first peak.
        Peak base = peaks[0];
        for (int i = 0; i < peaks.length; i++) {
            if (peaks[i].getIntensity() > base.getIntensity()) {
                base = peaks[i];
            }
        }
        return base;
    }
}
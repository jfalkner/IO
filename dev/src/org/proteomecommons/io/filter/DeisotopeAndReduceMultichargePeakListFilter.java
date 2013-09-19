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

import org.proteomecommons.io.*;
import org.proteomecommons.jaf.*;
import org.proteomecommons.jaf.util.*;
import java.util.*;

/**
 * This class both deisotopes and converts multiply charged peptides to their
 * singly charge form. It is intended for use with electrospray instruments that
 * can resolve charge states.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class DeisotopeAndReduceMultichargePeakListFilter implements
        PeakListFilter {
    private double ppmTolerance = 10;
    
    // min intensity to consider
    private double threshold = 1000;
    
    private double minTolerance = 0.02;
    
    private double minOfBasePeakPercent = .1;
    
    private int maxChargeState = 5;
    
    public void setMaxChargeState(int maxChargeState) {
        this.maxChargeState = maxChargeState;
    }
    
    public int getMaxChargeState() {
        return maxChargeState;
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
     * Set the tolerance for consideration of isotopic peaks. Default value is
     * 10ppm.
     *
     * @param ppm
     */
    public void setTolerance(double ppm) {
        this.ppmTolerance = ppm;
    }
    
    /**
     * Gets the current tolerance of isotopic peaks.
     *
     * @return
     */
    public double getTolerance() {
        return ppmTolerance;
    }
    
    /**
     * @see org.proteomecommons.io.filter.PeakListFilter#filter(org.proteomecommons.io.PeakList)
     */
    public PeakList filter(PeakList peaklist) {
        // assign charges
        AssignChargePeakListFilter acplf = new AssignChargePeakListFilter();
//		acplf.setMinTolerance(minTolerance);
//		acplf.setMaxChargeState(maxChargeState);
//		acplf.setTolerance(ppmTolerance);
        peaklist = acplf.filter(peaklist);
        
        // deisotop each of the peaks
        CalculateIsotopeDistribution cid = new CalculateIsotopeDistribution();
        cid.setIncrement(1);
        
        Peak[] peaks = (Peak[])peaklist.getPeaks();
        Arrays.sort(peaks);
        
        // extra peaks to add
        LinkedList extraPeaks = new LinkedList();
        
        // deisotope each peak
        for (int i = 0; i < peaks.length; i++) {
            // skip peaks with unknown charges
            if (peaks[i].getCharge() == Peak.UNKNOWN_CHARGE) {
                continue;
            }
            // approximate isotope distribution for singly charged
            double[] approximation = cid
                    .approximateIsotopeDistribution(peaks[i].getMassOverCharge()
                    * peaks[i].getCharge()
                    - Atom.H.getMassInDaltons()
                    * (peaks[i].getCharge() - 1));
            // try each of the approximations
            for (int j = 1; j < approximation.length; j++) {
                // skip empty ones
                if (approximation[j] == 0) {
                    continue;
                }
                
                // get the peaks to deplete
                Peak toDeplete = findPeak((peaks[i].getMassOverCharge() + j
                        * Atom.H.getMassInDaltons() / peaks[i].getCharge()), peaks);
                
                // deplete the peak
                if (toDeplete != null) {
                    // deplete by the normlize approximation amount, multiplied
                    // by the intensity of the monoisotopic peak
                    double iso = approximation[j];
                    double peakIntensity = peaks[i].getIntensity();
                    double monoIso = approximation[0];
                    
                    double amount = peaks[i].getIntensity() * approximation[j]
                            / approximation[0];
                    // reduce the intensity
                    if (toDeplete instanceof GenericPeak) {
                        GenericPeak gp = (GenericPeak)toDeplete;
                        gp.setIntensity(gp.getIntensity()-amount);
                    }
                    //					System.out.println("Depleting
                    // "+toDeplete.massOverChargeInDaltons+" by "+amount+",
                    // left: "+toDeplete.intensity);
                }
            }
        }
        
        //		showPeakList(peaklist.duplicate());
        
        // remove the multiply charged peaks, add to singly
        for (int i = 0; i < peaks.length; i++) {
            // skip stuff that doesn't need to changed
            if (peaks[i].getCharge() == 1 || peaks[i].getIntensity() <= 0
                    || peaks[i].getCharge() == Peak.UNKNOWN_CHARGE) {
                continue;
            }
            
            // figure out the singly charged peak
            double singleMZ = peaks[i].getMassOverCharge() * peaks[i].getCharge() - (peaks[i].getCharge() - 1) * Atom.H.getMassInDaltons();
            
            // find the singely charge peak
            Peak ps = findPeak(singleMZ, peaks);
            
            boolean spent = false;
            //if there are matches, add to them
            if (ps != null) {
                if (ps.getCharge() == 1) {
                    //						System.out.println("Adding " + peaks[i].intensity+ " to "
                    // + ps.massOverChargeInDaltons);
                    if (ps instanceof GenericPeak){
                        // cast to generic peaks
                        GenericPeak gp = (GenericPeak)ps;
                        GenericPeak a = (GenericPeak)peaks[i];
                        // set the intensities
                        gp.setIntensity(gp.getIntensity() + a.getIntensity());
                        a.setIntensity(0);
                    }
                    spent = true;
                }
            }
            
            // if the singly charged parent wasn't found, add it
            if (!spent) {
                // try finding the peak in the extras
                Peak[] extras = (Peak[]) extraPeaks.toArray(new Peak[0]);
                // check if any matched
                if (extras.length > 0) {
                    // sort so that search works
                    Arrays.sort(extras);
                    // find the singely charge peak
                    ps = findPeak(singleMZ, extras);
                    //if there are matches, add to them
                    if (ps != null) {
                        if (ps.getCharge() == 1) {
                            //								System.out.println("Adding "+ peaks[i].intensity
                            // + " to "+ ps.massOverChargeInDaltons);
                            ((GenericPeak)ps).setIntensity(ps.getIntensity() + peaks[i].getIntensity());
                            // nuke old intensity
                            ((GenericPeak)peaks[i]).setIntensity(0);
                            spent = true;
                        }
                    }
                }
            }
            
            // make a new peak
            if (!spent) {
                //					System.out.println("Making peak " + singleMZ+" removing
                // "+peaks[i].massOverChargeInDaltons);
                GenericPeak gp = new GenericPeak();
                gp.setIntensity(peaks[i].getIntensity());
                gp.setMassOverCharge(singleMZ);
                gp.setCharge(1);
                extraPeaks.add(gp);
                ((GenericPeak)peaks[i]).setIntensity(0);
            }
        }
        
        // return only the peaks that are still above the threshhold
        LinkedList toReturn = new LinkedList();
        for (int i = 0; i < peaks.length; i++) {
            if (peaks[i].getIntensity() > threshold) {
                toReturn.add(peaks[i]);
            }
        }
        // add all the extras
        toReturn.addAll(extraPeaks);
        
        // convert original to tandem
        GenericPeakList monoisotopicPeakList = new GenericPeakList();
        // get the peaks
        Peak[] tempPeaks = (Peak[]) toReturn.toArray(new Peak[toReturn.size()]);
        Arrays.sort(tempPeaks);
        monoisotopicPeakList.setPeaks(tempPeaks);
        // return the new peaklist
        return monoisotopicPeakList;
    }
    
    private Peak findPeak(double mz, Peak[] peaks) {
        // make a peak to search with
        GenericPeak p = new GenericPeak();
        p.setMassOverCharge(mz);
        // find the index
        int index = Arrays.binarySearch(peaks, p);
        if (index < 0) {
            index = (index + 1) * -1;
        }
        
        // return the best
        try {
            double errUp = Double.MAX_VALUE;
            try {
                errUp = Math.abs(peaks[index].getMassOverCharge() - mz);
            } catch (Exception e) {
                // noop
            }
            double errDown = Double.MAX_VALUE;
            try {
                errDown = Math.abs(peaks[index - 1].getMassOverCharge()
                - mz);
            } catch (Exception e) {
                // noop
            }
            
            // pick closest peak
            Peak closest = peaks[index - 1];
            // check if the other peak is better
            if (errUp < errDown) {
                closest = peaks[index];
            }
            
            // if it is close enough, use it
            double error = Math.abs(mz - closest.getMassOverCharge());
            double tolerance = ppmTolerance * mz / 1000000;
            // adjust tolerance if neccisary
            if (tolerance < minTolerance) {
                tolerance = minTolerance;
            }
            // check if the peak is close enough
            if (error < tolerance) {
                return closest;
            }
            
        } catch (Exception e) {
            // noop
        }
        
        return null;
    }
}
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
package org.proteomecommons.io;

/**
 * Abstraction of a peptide fragment, i.e. a "peak" in either MS/MS or MS
* spectra. This class works well but is memory wasting. High performance code implement Peak using the exact information that they require.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class GenericPeak implements Comparable, Peak {
  private double massOverCharge;
  private double intensity;
  private int charge = Peak.UNKNOWN_CHARGE;
  private int averaged = Peak.UNKNOWN_AVERAGED;
  private int chargeDeconvoluted = Peak.UNKNOWN_MONOISOTOPIC;
  private int centroided = Peak.UNKNOWN_CENTROIDED;
  private int deisotoped = Peak.UNKNOWN_DEISOTOPED;
    
    /**
     * Peaks may be sorted or searched efficiently using Java's merge sort and
     * binary search algorithms (see java.util.Arrays)
     */
    public int compareTo(Object a) {
        Peak aa = (Peak) a;
        if (getMassOverCharge() == aa.getMassOverCharge()) {
            return 0;
        }
        if (getMassOverCharge() > aa.getMassOverCharge()) {
            return 1;
        }
        return -1;
    }
    
    public String toString() {
        return "("+this.massOverCharge+", "+this.getIntensity()+")";
    }

    public double getMassOverCharge() {
        return massOverCharge;
    }

    public void setMassOverCharge(double massOverCharge) {
        this.massOverCharge = massOverCharge;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        // set the base value
        this.charge = charge;
        // this also means that the peak is centroided
        this.setCentroided(Peak.CENTROIDED);
        // not averaged
        this.setAveraged(Peak.NOT_AVERAGED);
    }

    public int getAveraged() {
        return averaged;
    }

    public void setAveraged(int averaged) {
        this.averaged = averaged;
    }

    public int getMonoisotopic() {
        return chargeDeconvoluted;
    }

    public void setMonoisotopic(int monoisotopic) {
        this.chargeDeconvoluted = monoisotopic;
    }

    public int getCentroided() {
        return centroided;
    }

    public void setCentroided(int centroided) {
        this.centroided = centroided;
    }

    public int getDeisotoped() {
        return deisotoped;
    }

    public void setDeisotoped(int deisotoped) {
        this.deisotoped = deisotoped;
    }
}
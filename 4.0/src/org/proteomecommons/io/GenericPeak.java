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
 * spectra.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class GenericPeak implements Comparable, Peak {
	/**
	 * Mass in Daltons divided by the peak's charge.
	 */
	private double massOverChargeInDaltons = Peak.UNKNOWN_MZ;

	/**
	 * Relative intensity of the peak.
	 */
	private double intensity = Peak.UNKNOWN_INTENSITY;

	/**
	 * Charge state of the peak. -1 if unknown.
	 */
	private int charge = MonoisotopicPeak.UNKNOWN_CHARGE;

	private int averaged = MonoisotopicPeak.UNKNOWN_AVERAGED;

	public double getMassOverCharge() {
		return massOverChargeInDaltons;
	}

	public void setMassOverCharge(double mz) {
		this.massOverChargeInDaltons = mz;
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
		this.charge = charge;
	}

	public int getAveraged() {
		return averaged;
	}

	public void setAveraged(int averaged) {
		this.averaged = averaged;
	}

	/**
	 * Peaks may be sorted or searched efficiently using Java's merge sort and
	 * binary search algorithms (see java.util.Arrays)
	 */
	public int compareTo(Object a) {
		Peak aa = (Peak) a;
		if (massOverChargeInDaltons - aa.getMassOverCharge() == 0) {
			return 0;
		}
		if (massOverChargeInDaltons - aa.getMassOverCharge() > 0) {
			return 1;
		}
		return -1;
	}
}
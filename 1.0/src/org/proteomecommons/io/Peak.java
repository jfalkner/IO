/*
 *    Copyright 2004 Jayson Falkner
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
public class Peak implements Comparable {
	/**
	 * Mass in Daltons divided by the peak's charge.
	 */
	public double massOverChargeInDaltons;
/**
 * Relative intensity of the peak.
 */
	public double intensity = 1;
/**
 * Charge state of the peak. -1 if unknown.
 */
	public int charge = 1;

	/**
	 * Public constructor of the class. All peaks must have a m/z value, but intensities are optional.
	 * @param massOverChargeInDaltons
	 *            The fragment's mass over charge in daltons (x-axis)
	 */
	public Peak(double massOverChargeInDaltons) {
		this.massOverChargeInDaltons = massOverChargeInDaltons;
	}

	/**
	 * Public constructor that initializes the peak with both an intensity and a m/z value. 
	 * @param massOverChargeInDaltons
	 *            The fragment's mass over charge in daltons (x-axis)
	 * @param intensity
	 *            The intensity associated with the fragment.
	 */
	public Peak(double massOverChargeInDaltons, double intensity) {
		this.massOverChargeInDaltons = massOverChargeInDaltons;
		this.intensity = intensity;
	}

	/**
	 * Public constructor that initializes the peak with an intensity, a m/z value, and a charge state. The m/z value is assumed to be the m/z accounting for the charge -- it is not modified..
	 * @param massOverChargeInDaltons
	 *            The fragment's mass over charge in daltons (x-axis)
	 * @param intensity
	 *            The intensity associated with the fragment.
	 * @param charge
	 *            The charge of the peptide fragment.
	 */
	public Peak(double massOverChargeInDaltons, double intensity, int charge) {
		this.massOverChargeInDaltons = massOverChargeInDaltons;
		this.intensity = intensity;
		this.charge = charge;
	}

	/**
	 * Peaks may be sorted or searched efficiently using Java's merge sort and
	 * binary search algorithms (see java.util.Arrays)
	 */
	public int compareTo(Object a) {
		Peak aa = (Peak) a;
		if (massOverChargeInDaltons - aa.massOverChargeInDaltons == 0) {
			return 0;
		}
		if (massOverChargeInDaltons - aa.massOverChargeInDaltons > 0) {
			return 1;
		}
		return -1;
	}
}
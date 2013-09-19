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

import java.util.Arrays;

/**
 * Abstraction for a mono-isotopic, centroided spectrum (i.e. peak list). There
 * isn't much to this class. If the parent ion charge is known, its value is in
 * parentIonCharge. If the parent ion's m/z is known, it is in
 * parentIonMassOverChargeInDaltons. All monisotopic peaks may be found in the
 * Peak[] peaks. Order and normalization of peaks is not guaranteed unless
 * explicitly asked for.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class PeakList {
	/**
	 * The parent ion's m/z. -1 if not known.
	 */
	public double parentIonMassOverChargeInDaltons;
	/**
	 * The parent ion's charge. -1 if not known.
	 */
	public int parentIonCharge;
	/**
	 * The peaks in the peak list.
	 * 
	 * @see org.proteomecommons.io.PeakList
	 */
	public Peak[] peaks = null;
	/**
	 * An Arbitrary name for the peak list
	 */
	public String name = "Unnamed Peak List";

	/**
	 * A reference to the PeakListReader instance that loaded this peak. Useful
	 * if you'd like to know what type of file the peak came from.
	 */
	public PeakListReader reader;

	/**
	 * Public constructor.
	 * 
	 * @param reader
	 *            The PeakListReader instance used to create the peak list.
	 */
	public PeakList(PeakListReader reader) {
		this.reader = reader;
	}

	/**
	 * Getter method for the parent ion's mass in daltons.
	 * 
	 * @return Parent ion's mass in daltons.
	 */
	public double getParentIonMass() {
		return this.parentIonMassOverChargeInDaltons;
	}

	/**
	 * Getter method for the parent ion's charge.
	 * 
	 * @return Parent ion's charge states.
	 */
	public int getParentIonCharge() {
		return this.parentIonCharge;
	}

	/**
	 * Utility method that sorts peaks in ascending order.
	 * 
	 * @return An array of PeptideFragment objects sorted in ascending order.
	 */
	public void sortPeaks() {
		// merge sort the peaks
		Arrays.sort(this.peaks);
	}

	/**
	 * Utility method that normalizes peak values so the summation of
	 * intensities equals 1.
	 * 
	 * @return An array of PeptideFragment objects sorted in ascending order.
	 */
	public void normalizePeaks() {
		// caclulate the total intensity
		double totalIntensity = 0;
		for (int i = 0; i < peaks.length; i++) {
			totalIntensity += peaks[i].intensity;
		}

		// normalized to max peak
		for (int i = 0; i < this.peaks.length; i++) {
			this.peaks[i].intensity /= totalIntensity;
		}
	}
}
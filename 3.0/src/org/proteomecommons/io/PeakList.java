/*
 *    Copyright 2004-2005 University of Michigan
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
import org.proteomecommons.io.filter.*;

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
	 * Abstraction for unknown values.
	 */
	public static int UNKNOWN = Integer.MIN_VALUE;

	private double intensity;

	/**
	 * The parent ion's m/z. PeakList.UNKNOWN if not known.
	 */
	public double parentIonMassOverChargeInDaltons;

	/**
	 * The parent ion's charge. PeakList.UNKNOWN if not known..
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
	private String name = "Unnamed Peak List";

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

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

	/**
	 * Helper method that uses the appropriate PeakListFilter to reduce multiply
	 * charged peak lists to singly charged.
	 */
	public static PeakList filterReduceToSingleCharge(PeakList peaklist) {
		// make a new instance of
		ReduceMultichargePeakListFilter filter = new ReduceMultichargePeakListFilter();
		return filter.filter(peaklist);
	}

	/**
	 * Helper method to filter a peak list down to only the peaks that fall
	 * within a dynamic range.
	 */
	public static PeakList filterDynamicRange(PeakList peaklist, double range) {
		// make a new instance of
		DynamicRangePeakListFilter filter = new DynamicRangePeakListFilter(
				range);
		return filter.filter(peaklist);
	}

	/**
	 * Helper method to filter a peak list down to the top most intense peaks.
	 */
	public static PeakList filterIntensity(PeakList peaklist, int numberOfPeaks) {
		// make a new instance of
		IntensityPeakListFilter filter = new IntensityPeakListFilter(
				numberOfPeaks);
		return filter.filter(peaklist);
	}
}
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
import java.util.LinkedList;

import org.proteomecommons.io.*;
import org.proteomecommons.jaf.*;
import org.proteomecommons.io.util.*;
import org.proteomecommons.jaf.util.CalculateIsotopeDistribution;

/**
 * This class deisotopes centroided peaklists. It is intended for use with
 * electrospray instruments that produce centroided but not monoisotopic data.
 * You can configure all of the key elements involved in picking peaks, labeling
 * charge states, and removing isotopes.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class DeisotopeCentroidedPeakListFilter implements PeakListFilter {
	private PeakMatcher peakMatcher = new PeakMatcher();

	private AssignChargePeakListFilter acplf = new AssignChargePeakListFilter();

	private int maxChargeState = 4;

	private int isotopesToRemove = 10;

	public void setMaxChargeState(int maxChargeState) {
		this.maxChargeState = maxChargeState;
	}

	public int getMaxChargeState() {
		return maxChargeState;
	}

	public void setPeakMatcher(PeakMatcher peakMatcher) {
		this.peakMatcher = peakMatcher;
		acplf.setPeakMatcher(peakMatcher);
	}

	public PeakMatcher getPeakMatcher() {
		return peakMatcher;
	}

	/**
	 * @see org.proteomecommons.io.filter.PeakListFilter#filter(org.proteomecommons.io.PeakList)
	 */
	public PeakList filter(PeakList peaklist) {
		// assign the charges
		PeakList chargedPeakList = acplf.filter(peaklist);

		// trim off isotope peaks
		Peak[] peaks = chargedPeakList.getPeaks();

		// make isotope calculators
		CalculateIsotopeDistribution cid = new CalculateIsotopeDistribution();
		cid.setIncrement(Atom.H.getMassInDaltons());

		// add all the peaks to a list
		LinkedList monoisotopicPeaks = new LinkedList();
		for (int i = 0; i < peaks.length; i++) {
			monoisotopicPeaks.add(peaks[i]);
		}

		// look for matching isotopes
		for (int i = 0; i < peaks.length; i++) {
			// convert to charge assigned peak
			GenericPeak cap = (GenericPeak) peaks[i];

			// get the charge
			double charge = cap.getCharge();
			// skip unknown charge states
			if (charge == ChargeAssignedPeak.UNKNOWN_CHARGE) {
				continue;
			}
			// skip peaks that have been removed
			if (!monoisotopicPeaks.contains(peaks[i])) {
				continue;
			}

			// approximate the isotope distribution
			double singleyChargedMz = peaks[i].getMassOverCharge() * charge
					- ((charge - 1) * Atom.H.getMassInDaltons());
			double[] dist = cid
					.approximateIsotopeDistribution(singleyChargedMz);

			// try to remove the first 10 isotopes
			for (int isotopeCount = 1; isotopeCount < isotopesToRemove; isotopeCount++) {
				// calculate where the next isotope peak should be
				double isotopeMz = peaks[i].getMassOverCharge()
						+ Atom.H.getMassInDaltons() / charge * isotopeCount;

				// get the isotope peak
				Peak isotopePeak = peakMatcher.match(peaks, isotopeMz);

				// if the peak doesn't exist, skip it
				if (isotopePeak == null) {
					continue;
				}
				// calculate how far off the isotope intensity is
				double isotopeError = Math
						.abs((peaks[i].getIntensity() / dist[0]) * dist[isotopeCount]
								- isotopePeak.getIntensity());

				// calculate the relative deviation
				double isotopeDeviation = isotopeError
						/ (peaks[i].getIntensity() / dist[0]);

				// if the deviation is too far off, skip removal
				if (isotopeDeviation > acplf.getAllowedIsotopeDeviation()) {
					continue;
				}
				
				// remove the peak
				monoisotopicPeaks.remove(isotopePeak);
			}
		}
		
		// return the monoisotopic peaks
		GenericPeakList gpl = (GenericPeakList)GenericPeakList.duplicate(peaklist);
		gpl.setPeaks((Peak[])monoisotopicPeaks.toArray(new Peak[0]));

		return gpl;
	}
}
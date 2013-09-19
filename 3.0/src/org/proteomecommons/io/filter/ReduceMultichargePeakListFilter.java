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
package org.proteomecommons.io.filter;

import java.util.*;

import org.proteomecommons.io.*;

/**
 * A filter that converts a multiply charged peak list to a singly charged peak
 * list. All possiblities of peaks are kept.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ReduceMultichargePeakListFilter implements PeakListFilter {

	/**
	 * Convert each multiply charged peak to a singly charged peak.
	 */
	public PeakList filter(PeakList peaklist) {
		// if singly charged, skip
		if (peaklist == null || peaklist.parentIonCharge < 2) {
			return peaklist;
		}

		// make peaks for the multi charge
		LinkedList peaks = new LinkedList();

		// calc singly charged species
		double proton = 1.00728;
		double singleChargeParent = peaklist.parentIonMassOverChargeInDaltons
				* peaklist.parentIonCharge - proton
				* (peaklist.parentIonCharge - 1);

		// filter out the original list
		for (int i = 0; i < peaklist.peaks.length; i++) {
			// add convert each possible charge state
			for (int j = 1; j <= peaklist.parentIonCharge; j++) {
				// add singly charged peak
				if (peaklist.peaks[i].massOverChargeInDaltons < (singleChargeParent + proton
						* (j - 1))
						/ j) {
					// mass is multiplied out, with extra protons subtracted
					double mz = peaklist.peaks[i].massOverChargeInDaltons * j
							- proton * (j - 1);
					// make the new singly charged peak
					peaks.add(new Peak(mz, peaklist.peaks[i].intensity, 1));
				}
			}
		}

		// make a new peak list
//		FilteredPeakList fpl = new FilteredPeakList(peaklist, this);
PeakList fpl = new PeakList();
		// set the peaks
		fpl.peaks = (Peak[]) peaks.toArray(new Peak[0]);
		Arrays.sort(fpl.peaks);
		// set the parent charge
		fpl.parentIonCharge = 1;
		// set the parent mz
		fpl.parentIonMassOverChargeInDaltons = singleChargeParent;

		// return the filtered peak list
		return fpl;
	}
}
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

import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.jaf.Atom;

/**
 * A filter that converts a multiply charged peak list to a singly charged peak
 * list. All possiblities of peaks are kept. It is a brute force method to
 * ensure that you get the correct singely charge peak, at the expense of some
 * noise peaks.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ReduceMultichargePeakListFilter implements PeakListFilter {

	/**
	 * Convert each multiply charged peak to a singly charged peak.
	 */
	public PeakList filter(PeakList peaklist) {
		if (true){
			throw new RuntimeException("Code doesn't work, dont' use it.");
		}
//		// if singly charged, skip
//		if (peaklist == null || peaklist.getParentIonCharge() < 2) {
//			return peaklist;
//		}
//
//		// make peaks for the multi charge
//		LinkedList peaks = new LinkedList();
//
//		// calc singly charged species
//		double proton = Atom.H.getMassInDaltons();
//		double singleChargeParent = peaklist.getParentIonMassOverCharge()
//				* peaklist.getParentIonCharge() - proton
//				* (peaklist.getParentIonCharge() - 1);
//
//		// filter out the original list
//		for (int i = 0; i < peaklist.peaks.length; i++) {
//			// add convert each possible charge state
//			for (int j = 1; j <= peaklist.getParentIonCharge(); j++) {
//				// add singly charged peak
//				if (peaklist.peaks[i].massOverChargeInDaltons < (singleChargeParent + proton
//						* (j - 1))
//						/ j) {
//					// mass is multiplied out, with extra protons subtracted
//					double mz = peaklist.peaks[i].massOverChargeInDaltons * j
//							- proton * (j - 1);
//					// make the new singly charged peak
//					peaks.add(new Peak(mz, peaklist.peaks[i].intensity, 1));
//				}
//			}
//		}
//
//		// make a new peak list
//		//		FilteredPeakList fpl = new FilteredPeakList(peaklist, this);
//		PeakList fpl = new PeakList();
//		// set the peaks
//		fpl.peaks = (Peak[]) peaks.toArray(new Peak[0]);
//		Arrays.sort(fpl.peaks);
//		// set the parent charge
//		fpl.setParentIonCharge(1);
//		// set the parent mz
//		fpl.setParentIonMassOverCharge(singleChargeParent);
//
//		// return the filtered peak list
//		return fpl;
		return null;
	}
}
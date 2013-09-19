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

import java.util.LinkedList;

import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * A filter that will keep only peaks below the singlely charged paren't ion
 * peak. This filter is handy for times when you want to clean up a peak list.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class KeepOnlyBelowParentIon implements PeakListFilter {

	/**
	 * Keeps on the peaks that are less than the parent.
	 */
	public PeakList filter(PeakList peaklist) {

//		// reduce the original set of peaks by a dynamic range
//		Peak[] peaks = peaklist.getPeaks();
//
//		LinkedList below = new LinkedList();
//		// keep only the top peaks
//		for (int i = 0; i < peaks.length; i++) {
//			if (peaks[i].massOverChargeInDaltons < peaklist
//					.getParentIonMassOverCharge() - .5) {
//				below.add(peaks[i]);
//			}
//		}
//
//		// make a new peak list
//		peaklist.setPeaks((Peak[]) below.toArray(new Peak[below.size()]));
//
//		return peaklist;
return null;
	}
}
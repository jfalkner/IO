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

import java.util.*;

import org.proteomecommons.io.Peak;
import org.proteomecommons.io.*;

/**
 * A filter for removing peaks that are smaller than a certain percent of the
 * base peak, e.g. preserve only a few orders of dynamic range.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class MergeSamePeakListFilter implements PeakListFilter {
	// number of most intense peaks to keep
	double proximityInDaltons = 0.02;

	/**
	 * Public constructor.
	 * 
	 * @param count
	 *            How many peaks to keep from the list.
	 */
	public MergeSamePeakListFilter(double proximityInDaltons) {
		this.proximityInDaltons = proximityInDaltons;
	}

	/**
	 * Filter's the peaks in a peak list and keeps on the most intense.
	 */
	public PeakList filter(PeakList peaklist) {

		// reduce the original set of peaks by a dynamic range
		Peak[] peaks = peaklist.getPeaks();
		
		// sort
		Arrays.sort(peaks);

		// merge similar peaks
		LinkedList peaksToKeep = new LinkedList();
		for (int i = 0; i < peaks.length-1; i++) {
			// add the peak
			peaksToKeep.add(peaks[i]);
			double a = peaks[i].getMassOverCharge();

			// skip over similar peaks
			while (i<peaks.length-1&&Math.abs(peaks[i].getMassOverCharge()-a)<=proximityInDaltons){
			  i++;	
			}
		}

		// dupe the peak list
		((GenericPeakList)peaklist).setPeaks((Peak[]) peaksToKeep.toArray(new Peak[0]));

		return peaklist;
	}
}
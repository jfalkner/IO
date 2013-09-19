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
public class RelativeIntensityPeakListFilter implements PeakListFilter {
	// number of most intense peaks to keep
	double amountOfBasePeak;

	/**
	 * Public constructor.
	 * 
	 * @param count
	 *            How many peaks to keep from the list.
	 */
	public RelativeIntensityPeakListFilter(double amountOfBasePeak) {
		this.amountOfBasePeak = amountOfBasePeak;
	}

	/**
	 * Filter's the peaks in a peak list and keeps on the most intense.
	 */
	public PeakList filter(PeakList peaklist) {

		// reduce the original set of peaks by a dynamic range
		Peak[] peaks = peaklist.getPeaks();

		// find the base peak
		double basePeakIntensity = Double.MIN_VALUE;
		for (int i = 0; i < peaks.length; i++) {
			if (peaks[i].getIntensity() > basePeakIntensity) {
				basePeakIntensity = peaks[i].getIntensity();
			}
		}

		// cull the weak
		LinkedList peaksToKeep = new LinkedList();
		for (int i = 0; i < peaks.length; i++) {
			if (peaks[i].getIntensity() >= basePeakIntensity * amountOfBasePeak) {
				peaksToKeep.add(peaks[i]);
			}
		}

		// dupe the peak list
//		GenericPeakList gpl = (GenericPeakList) GenericPeakList.duplicate(peaklist);
		// TODO this loses all the parent peak lists's meta-info. use a filtered peak list
                GenericPeakList gpl = new GenericPeakList();
		gpl.setPeaks((Peak[]) peaksToKeep.toArray(new Peak[0]));

		return gpl;
	}
}
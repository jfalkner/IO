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

import org.proteomecommons.io.PeakList;

/**
 * An abstraction to represent any peak list that has passed through the filter
 * chain.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class FilteredPeakList extends PeakList {
	// reference to the peak list this was derived from
	PeakList originalPeakList;

	// reference to the filter that did the modification
	PeakListFilter filter;

	public FilteredPeakList(PeakList peaklist, PeakListFilter filter) {

		// ref original peaklist
		originalPeakList = peaklist;

		// ref filter that made this list
		this.filter = filter;

		// set defaults to be orignal peaklist's
		this.parentIonCharge = peaklist.parentIonCharge;
		this.parentIonMassOverChargeInDaltons = peaklist.parentIonMassOverChargeInDaltons;
		this.peaks = peaklist.peaks;
	}
}
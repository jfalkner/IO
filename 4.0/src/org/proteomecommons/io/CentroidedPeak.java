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
package org.proteomecommons.io;

/**
 * Abstraction of a peptide fragment, i.e. a "peak" in either MS/MS or MS
 * spectra.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public interface CentroidedPeak extends Peak {
	/**
	 * Constant for flagging if this peak's mass was determined from averaging.
	 */
	public static int UNKNOWN_AVERAGED = Integer.MIN_VALUE;

	/**
	 * Flags that symbolizes this peak was averaged and reduced to a
	 * monoisotopic peak, not resolved to a visible monoisotopic peak. Older
	 * instruments and current instruments dealing with high charge states often
	 * can't accurately resolve a monoisotopic peak.
	 */
	public static int AVERAGED = 0;

	/**
	 * Flags that the monoisotopic peak was clearly resolved, not averaged.
	 */
	public static int RESOLVED = 1;

	/**
	 * A read-only method of obtaining if the monoisotopic mass was clearly
	 * resolved or if it was averaged.
	 * 
	 * @return
	 */
	public int getAveraged();
}
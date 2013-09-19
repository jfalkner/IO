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
package org.proteomecommons.io.dta;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * An implementation of the PeakListWriter class that can serialize peak lists
 * in to Sequest DTA format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class SequestDTAPeakListWriter extends PeakListWriter {
	OutputStream out;

	public SequestDTAPeakListWriter(OutputStream out) {
		this.out = out;
	}

	/**
	 * Simple method to write a DTA file from a peak list.
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(out);
		// write the parent ion plus charge
		osw.write(peaklist.parentIonMassOverChargeInDaltons + " "
				+ (int) peaklist.parentIonCharge + "\n");

		// write out the peaks
		for (int i = 0; i < peaklist.peaks.length; i++) {
			osw.write(peaklist.peaks[i].massOverChargeInDaltons + " "
					+ peaklist.peaks[i].intensity + "\n");
		}
		osw.flush();
	}
}
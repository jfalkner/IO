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
 * A simple writer class that can serialize peak list files in micromass PKL
 * format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDTAPeakListWriter extends PeakListWriter {
	OutputStream out;

	// Flag to indicate if the current line is between newlines
	private boolean used = false;

	// OutputStreamWriter object at object-level. Avoids having to create
	// this object in every function call. Improves performance, particularly
	// in in-memory processing
	private OutputStreamWriter osw = null;

	public SequestDTAPeakListWriter(OutputStream out) {
		this.out = out;
	}

	/**
	 * Simple method to write a MGF file from a peak list.
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws Exception {
		// if used, skip
		if (used){
			return;
		}
		used = true;
		
		// make a writer
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		// write the headers
		osw.write((peaklist.parentIonMassOverChargeInDaltons*peaklist.parentIonCharge-peaklist.parentIonCharge+1) + " " + peaklist.parentIonCharge
				+ "\n");

		// write out the peaks
		for (int i = 0; i < peaklist.peaks.length; i++) {
			osw.write(peaklist.peaks[i].massOverChargeInDaltons + " "
					+ peaklist.peaks[i].intensity + "\n");
		}
		osw.write("\n"); // End of Peaklist marker
		
		// flush
		osw.flush();
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#finish() Marks end of
	 *      peak-list in in-memory processing. Resets the flag that indicates
	 *      boundaries of peaklist
	 */
	public void finish() throws IOException {
		osw.flush();
		osw.close();

	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double)
	 *      Writes newline if a peaklist was processed earlier. This function is
	 *      retained for backward compatibility. The next function with other
	 *      meta info needs to be used.
	 */
	public void startPeakList(double parentIonMassOverChargeInDaltons,
			double charge) throws IOException {
		// skip if used
		if (used) {
			throw new IOException("DTA files may only have one peak list!");
		}
		used = true;
		
		// make a writer if you need it
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		// print the first line -- DTA shows parent ion mass
		osw.write((parentIonMassOverChargeInDaltons*charge-charge+1) + " " + charge
				+ "\n");
	}

	/**
	 * Writes newline if a peaklist was processed earlier. Writes peaklist-level
	 * meta data if present
	 */
	public void startPeakList(double parentIonMassOverChargeInDaltons,
			double intensity,double parentIonCharge) throws IOException {
		startPeakList(parentIonMassOverChargeInDaltons, parentIonCharge);
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 *      Writes values of the Peak passed
	 */
	public void write(Peak peak) throws IOException {
		// TODO Auto-generated method stub
		if (osw == null) {
			throw new IOException(
					"You must start the peak list before writing peaks.");
		}
		osw.write(peak.massOverChargeInDaltons + "\t" + peak.intensity + "\n");
	}
}
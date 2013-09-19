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
package org.proteomecommons.io.pkl;

import java.io.BufferedWriter;
import java.io.OutputStream;

import org.proteomecommons.io.CheckedIOException;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.*;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.jaf.Atom;

/**
 * A simple writer class that can serialize peak list files in micromass PKL
 * format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class MicromassPKLPeakListWriter extends GenericTandemPeakListWriter {

	// Flag to indicate if the current line is between newlines
	private boolean used = false;

	private boolean isFirstPeak = true;

	public MicromassPKLPeakListWriter(OutputStream out) {
		super(out);
	}

	/**
	 * This method does nothing. PKL files are always MSMS.
	 */
	public int getTandemCount() {
		return 2;
	}

	/**
	 * Simple method to write a MGF file from a peak list.
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws CheckedIOException {
		// check that it is a tandem peak list
		if (!(peaklist instanceof TandemPeakList)) {
			throw new IncompatibleFormatException(
					"The PKL format only supports MSMS data.");
		}
		// check that the parent has charge
		TandemPeakList tandem = (TandemPeakList) peaklist;
		Peak peak = tandem.getParent();
		if (!(peak instanceof ChargeAssignedPeak)) {
			throw new IncompatibleFormatException(
					"You must provide parent ion charge, mz, and intensity information for the PKL format.");
		}
		// cast to the appropriate peak
		ChargeAssignedPeak parent = (ChargeAssignedPeak) peak;

		try {
			BufferedWriter bw = this.getBufferedWriter();
			// write the headers
			bw.write(calcParentIonMZ(parent) + " " + parent.getIntensity()
					+ " " + parent.getCharge() + "\n");

			// write out the peaks
			Peak[] peaks = peaklist.getPeaks();
			for (int i = 0; i < peaks.length; i++) {
				bw.write(peaks[i].getMassOverCharge() + " "
						+ peaks[i].getIntensity() + "\n");
			}
			bw.write("\n"); // End of Peaklist marker

			// flush
			bw.flush();
		} catch (Exception e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}

	/**
	 * Helper method to calc the appropriate parent ion m/z.
	 * 
	 * @return
	 */
	private double calcParentIonMZ(ChargeAssignedPeak peak) {
		return (peak.getMassOverCharge() * peak.getCharge() - (peak.getCharge() - 1)
				* Atom.H.getMassInDaltons());
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double)
	 *      Writes newline if a peaklist was processed earlier. This function is
	 *      retained for backward compatibility. The next function with other
	 *      meta info needs to be used.
	 */
	public void startPeakList() throws CheckedIOException {
		try {
			// skip if used
			if (used) {
				BufferedWriter bw = getBufferedWriter();
				bw.write("\n");
			}
			used = true;
		} catch (Exception e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 *      Writes values of the Peak passed
	 */
	public void write(Peak peak) throws CheckedIOException {
		try {
			BufferedWriter bw = getBufferedWriter();
			// if no header, write header
			if (isFirstPeak) {
				ChargeAssignedPeak cap = (ChargeAssignedPeak) getParent();
				// print the first line -- DTA shows parent ion mass
				bw.write(calcParentIonMZ(cap) + " " + cap.getCharge() + "\n");
			}
			// flag off the first peak
			isFirstPeak = false;

			bw.write(peak.getMassOverCharge() + "\t" + peak.getIntensity()
					+ "\n");
		} catch (Exception e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}
}
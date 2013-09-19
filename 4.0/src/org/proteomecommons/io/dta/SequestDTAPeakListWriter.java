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
package org.proteomecommons.io.dta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.proteomecommons.jaf.*;
import org.proteomecommons.io.*;
import org.proteomecommons.io.CheckedIOException;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * A simple writer class that can serialize peak list files DTA format. format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDTAPeakListWriter extends GenericPeakListWriter implements
		TandemPeakListWriter {

	// Flag to indicate if the current line is between newlines
	private boolean used = false;

	private boolean isFirstPeak = true;

	private MonoisotopicPeak parent = null;

	public SequestDTAPeakListWriter(OutputStream out) {
		super(out);
	}

	/**
	 * Simple method to write a MGF file from a peak list.
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws CheckedIOException {
		// if used, skip
		if (used) {
			return;
		}

		// flag the writer as having been used
		used = true;

		BufferedWriter bw = this.getBufferedWriter();
		try {
			// optional write tandem MS headers
			if (peaklist instanceof TandemPeakList) {
				TandemPeakList tandem = (TandemPeakList) peaklist;
				if (tandem.getParent() != null) {
					// check if the charge is known
					Peak parent = tandem.getParent();
					if (parent instanceof MonoisotopicPeak) {
						MonoisotopicPeak mp = (MonoisotopicPeak) parent;
						bw.write(calcDTAParentIonMass(mp) + " "
								+ mp.getCharge() + "\n");
					}
					// if not monoisotopic, fib
					else {
						throw new IncompatibleFormatException(
								"DTA format requires a MonoisotopicPeak for the parent ion.");
					}
				}
				// if not tandem, fib as best as you can
				else {
					throw new IncompatibleFormatException(
							"DTA format requires a TandemPeakList.");
				}
			}

			// write out the peaks
			Peak[] peaks = peaklist.getPeaks();
			for (int i = 0; i < peaks.length; i++) {
				// skipp nulls
				if (peaks[i] == null){
					continue;
				}
				// write the peak
				bw.write(peaks[i].getMassOverCharge() + " "
						+ peaks[i].getIntensity() + "\n");
			}

			bw.write("\n"); // End of Peaklist marker

			// flush
			bw.flush();
		} catch (IOException e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double)
	 *      Writes newline if a peaklist was processed earlier. This function is
	 *      retained for backward compatibility. The next function with other
	 *      meta info needs to be used.
	 */
	public void startPeakList() throws CheckedIOException {
		// skip if used
		if (used) {
			throw new CanOnlyContainOneSpectrumException();
		}
		used = true;

		BufferedWriter bw = getBufferedWriter();
	}

	/**
	 * Helper method that calculates what the DTA format is supposed to show for parent ion mass.
	 * @param p
	 * @return
	 */
	private double calcDTAParentIonMass(MonoisotopicPeak p) {
		return (p.getMassOverCharge() * p.getCharge() - (p.getCharge() - 1)
				* Atom.H.getMassInDaltons());
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 *      Writes values of the Peak passed
	 */
	public void write(Peak peak) throws CheckedIOException {
		BufferedWriter bw = getBufferedWriter();
		try {
			// if no header, write header
			if (isFirstPeak) {
				// print the first line -- DTA shows parent ion mass
				bw.write(calcDTAParentIonMass(parent)
						+ " " + parent.getCharge() + "\n");
			}
			// flag off the first peak
			isFirstPeak = false;

			bw.write(peak.getMassOverCharge() + "\t" + peak.getIntensity()
					+ "\n");
		} catch (IOException e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}

	/**
	 * This method sets the parent ion that wil be used for determining the
	 * parent ion charge and intensity.
	 * 
	 * @see org.proteomecommons.io.TandemPeakListWriter#setParent()
	 */
	public void setParent(Peak parent) throws CheckedIOException {
		if (!(parent instanceof MonoisotopicPeak)) {
			throw new IncompatibleFormatException(
					"The DTA format requires that you set a parent ion with a known charge and mz.");
		}
		this.parent = (MonoisotopicPeak) parent;
	}

	/**
	 * The DTA format doesn't track parent peak lists. This method does nothing.
	 * 
	 * @see org.proteomecommons.io.TandemPeakListWriter#setParentPeakList()
	 */
	public void setParentPeakList(PeakList peaklist) {
		// TODO Auto-generated method stub

	}

	/**
	 * The DTA format doesn't track MS level, this method does nothing.
	 * 
	 * @see org.proteomecommons.io.TandemPeakListWriter#setTandemCount()
	 */
	public void setTandemCount(int count) {
		// doesn't matter for this format
	}
}
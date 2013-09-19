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
 * @author Jayson Falkner - jfalkner@umich.edu
 * 
 * A PeakListReader implementation that reads Sequence DTA files.
 */
public class SequestDTAPeakListReader extends PeakListReader {
	InputStream in;

	// the parent ion m/z value
	private double parentIonMassOverChargeInDaltons = 0;

	// the parent ion charge
	private int parentIonCharge = 0;

	// flag for start of peak list
	boolean isStartOfPeakList = true;

	// buffer for the next peak
	Peak next = null;

	/**
	 * @see org.proteomecommons.io.PeakListReader#close()
	 */
	public void close() {
		try {
			in.close();
		} catch (Exception e) {
			//  noop
		} finally {
			in = null;
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() {
		// check buffer
		if (next != null) {
			return true;
		}

		// try to load buffer
		try {
			next = next();
		} catch (Exception e) {
			// noop
		}
		return next != null;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() {
		return isStartOfPeakList;
	}

	/**
	 * Public constructor.
	 * 
	 * @param fileName
	 *            Name of the DTA file to parse.
	 * @throws IOException
	 */
	public SequestDTAPeakListReader(String filename) throws IOException {
		this(new FileInputStream(filename), filename);
	}

	public SequestDTAPeakListReader(InputStream in, String filename)
			throws IOException {
		// load the file reference
		this.in = in;
		// set the name
		name = filename;

		// eat up whitespace
		int i = in.read();
		while ((i == ' ' || i == '\r' || i == '\n') && i != -1) {
			i = in.read();
		}
		// read in m/z
		StringWriter mz = new StringWriter();
		while (i != ' ' && i != '\t' && i != -1) {
			mz.write((char) i);
			i = in.read();
		}
		i = in.read();

		// read in intensity
		StringWriter intensity = new StringWriter();
		while (i != '\n' && i != '\r' && i != -1) {
			intensity.write((char) i);
			i = in.read();
		}

		//  set the first entry as parent ion mass and the associated charge
		this.parentIonMassOverChargeInDaltons = Double.parseDouble(mz
				.toString());
		this.parentIonCharge = Integer.parseInt(intensity.toString());

		// flag not first
		isStartOfPeakList = false;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getParentIonCharge()
	 */
	public int getParentIonCharge() {
		return parentIonCharge;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getParentIonMassOverChargeInDaltons()
	 */
	public double getParentIonMassOverChargeInDaltons() {
		return parentIonMassOverChargeInDaltons;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() throws IOException {
		// use the cache if it is there
		if (next != null) {
			Peak temp = next;
			next = null;
			return temp;
		}

		// eat up whitespace
		int i = in.read();
		while ((i == ' ' || i == '\t' || i == '\r' || i == '\n') && i != -1) {
			i = in.read();
		}
		// read in m/z
		StringWriter mz = new StringWriter();
		while (i != ' ' && i != '\t' && i != -1) {
			mz.write((char) i);
			i = in.read();
		}
		i = in.read();

		// read in intensity
		StringWriter intensity = new StringWriter();
		while (i != '\n' && i != '\r' && i != -1) {
			intensity.write((char) i);
			i = in.read();
		}

		//  set the first entry as parent ion mass and the associated charge
		double massOverChargeInDaltons;
		double intens;
		try {
			massOverChargeInDaltons = Double.parseDouble(mz.toString());
			intens = Double.parseDouble(intensity.toString());
		} catch (Exception e) {
			return null;
		}
		// return the appropriate fragment
		return new Peak(massOverChargeInDaltons, intens);
	}
}
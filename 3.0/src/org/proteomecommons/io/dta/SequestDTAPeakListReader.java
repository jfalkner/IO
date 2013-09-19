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
 * A PeakListReader implementation that reads Sequence DTA files. m/z values are
 * adjusted to the mass that a mass spec would observe.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class SequestDTAPeakListReader extends PeakListReader {
	BufferedReader in;

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
		this.in = new BufferedReader(new InputStreamReader(in));
		// set the name
		name = filename;

		String line = this.in.readLine();
		String[] parts = line.split("\\s");

		//  set the first entry as parent ion mass and the associated charge
		this.parentIonMassOverChargeInDaltons = Double.parseDouble(parts[0]);
		this.parentIonCharge = Integer.parseInt(parts[1]);
		// adjust parent ion -- convert to what a mass spec would observe
		this.parentIonMassOverChargeInDaltons += (this.parentIonCharge - 1)*1.00728;
		this.parentIonMassOverChargeInDaltons /= this.parentIonCharge;
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
		// flag off start
		isStartOfPeakList = false;

		// use the cache if it is there
		if (next != null) {
			Peak temp = next;
			next = null;
			return temp;
		}

		// read a line
		String line = in.readLine();
		if (line == null) {
			return null;
		}
		// split
		String[] parts = line.split("\\s");

		//  set the first entry as parent ion mass and the associated charge
		double massOverChargeInDaltons;
		double ionCharge;
		try {
			massOverChargeInDaltons = Double.parseDouble(parts[0]);
			ionCharge = Double.parseDouble(parts[1]);
		} catch (Exception e) {
			return null;
		}
		// return the appropriate fragment
		return new Peak(massOverChargeInDaltons, ionCharge);
	}
}
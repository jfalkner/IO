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
package org.proteomecommons.io.mgf;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 * 
 * A PeakListReader implementation designed to handle files in Mascot Generic
 * Format (.mgf), a popular format for ABI instruments.
 */
public class MascotGenericFormatPeakListReader extends PeakListReader {
	// the parent ion's m/z value
	private double parentIonMassOverChargeInDaltons = 0;

	// the parent ion's charge
	private int parentIonCharge = 0;

	// buffered input for the peak list
	BufferedReader in;

	// the next peak in the current peak list
	Peak next;

	// flag for if we're at the first peak of a new peak list
	boolean startOfPeakList = true;

	/**
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() {
		// check if the buffer has been filled
		if (next != null) {
			return true;
		}

		// try to fill the buffer
		try {
			next = next();
		} catch (Exception e) {
			// noop
		}

		// if valid, indicate so
		return next != null;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() {
		return startOfPeakList;
	}

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
	 * Make a new PeakListReader designed to read Mascot Generic Format peak
	 * list files and point the reader to the file with the specified name.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public MascotGenericFormatPeakListReader(String fileName)
			throws IOException {
		this(new FileInputStream(fileName), fileName);
	}

	/**
	 * Make a new PeakListReader designed to read Mascot Generic Format peak
	 * list files and point the reader to the java.io.InputStream with the
	 * specified name.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public MascotGenericFormatPeakListReader(InputStream is, String name)
			throws IOException {
		// reference the parent file
		this.name = name;

		// load the file reference
		this.in = new BufferedReader(new InputStreamReader(is));

		// parse meta-info and the first peak
		parseMetaInfo();
	}

	/**
	 * Helper method to parse meta-info. TODO: handle all the other forms of MGF
	 * meta-info.
	 * 
	 * @return true if there is a peak list, false if not.
	 * @throws IOException
	 */
	private boolean parseMetaInfo() throws IOException {
		// handle meta-information
String string = null;		
for (string = in.readLine(); in != null; string = in.readLine()) {
			// try to find parent mass info
			if (string.startsWith("PEPMASS")) {
				String parentMass = string.substring(string.indexOf("=") + 1);
				// check there is nothing else
				if (parentMass.indexOf(" ") != -1) {
					parentMass = parentMass.substring(0, parentMass
							.indexOf(" "));
				}
				// set the parent ion mass
				this.parentIonMassOverChargeInDaltons = Double
						.parseDouble(parentMass);
			}
			// try to handle charge
			else if (string.startsWith("CHARGE")) {
				String charge = string.substring(string.indexOf("=") + 1,
						string.indexOf("=") + 2);
				this.parentIonCharge = Integer.parseInt(charge);
			}
			// break at begin ions
			else if (string.indexOf("BEGIN IONS") != -1) {
				startOfPeakList = true;
			}
			// break at begin ions
			else {
				// skip blanks
				if (string.equals("")) {
					continue;
				}
				char c = string.charAt(0);
				if (c >= '0' && c <= '9') {
					// flag start of ions
					startOfPeakList = true;
					// parse the peak
					try {
						next = next(string);
					} catch (Exception e) {
						// noop
					}
					return true;
				}
			}
		}

		// if the meta-info isn't found, return false
		return false;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getParentIonCharge()
	 */
	public int getParentIonCharge() {
		return parentIonCharge;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getParentIonMassOverChargeDaltons()
	 */
	public double getParentIonMassOverChargeInDaltons() {
		return parentIonMassOverChargeInDaltons;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() throws Exception {
		return next(null);
	}

	// helper to parse peaks
	private Peak next(String line) throws Exception {
		// flag off the start of a new peak list
		startOfPeakList = false;

		// if buffered, use buffer
		if (next != null) {
			// temporarily reference the next
			Peak temp = next;
			// null the buffer
			next = null;
			// return the good reference
			return temp;
		}

		// get the next ion
		if (line == null) {
			line = in.readLine();
		}
		// check for end of ions
		if (line == null || line.length() == 0) {
			return null;
		}

		// check for the end of the ions
		if (line.startsWith("END IONS")) {
			// load the next meta info
			if (!parseMetaInfo()) {
				return null;
			}
		}

		// process ions
		String[] split = line.split("\t");

		//  set the first entry as parent ion mass and the associated charge
		double massOverChargeInDaltons;
		double intens;
		try {
			massOverChargeInDaltons = Double.parseDouble(split[0]);
			intens = Double.parseDouble(split[1]);
		} catch (Exception e) {
			return null;
		}
		// return the appropriate fragment
		return new Peak(massOverChargeInDaltons, intens);
	}
}
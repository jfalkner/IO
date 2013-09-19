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
package org.proteomecommons.io.pkl;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * A PeakListReader implementation that reads Micromass PKL files.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MicromassPKLPeakListReader extends PeakListReader {

	// buffered input for the peak list
	BufferedReader in;

	// the parent ion m/z value
	private double parentIonMassOverChargeInDaltons = 0;

	// the parent ion charge
	private int parentIonCharge = 0;

	// intensity
	private double intensity = -1;

	// the next peak in the current peak list
	Peak next;

	// flag for if we're at the first peak of a new peak list
	boolean startOfPeakList = true;

	/**
	 * Public constructor.
	 * 
	 * @param fileName
	 *            Name of the PKL file to parse.
	 * @throws IOException
	 */
	public MicromassPKLPeakListReader(String filename) throws IOException {
		this(new FileInputStream(filename), filename);
	}

	public MicromassPKLPeakListReader(InputStream is, String filename)
			throws IOException {
		//		super(in, filename);
		// reference the parent file
		this.name = filename;

		// load the file reference
		this.in = new BufferedReader(new InputStreamReader(is));
		
		// parse init meta info
		parseMetaInfo();
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() {
		if (next != null) {
			return true;
		}
		// try to parse a new peak
		try {
			next = next();
		}
		catch (Exception e){
			// noop
		}
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
	 * Returns a PeakList object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large peaklist files. Additional
	 * peaklist-level meta data are set with the processed meta data
	 * 
	 * @param sort
	 *            true if the peak list should be sorted, false if not
	 * @param normailze
	 *            true if the peak list intensity values should be normalized
	 *            between 0 and 1.
	 */
	public PeakList getPeakList(boolean sort, boolean normalize)
			throws Exception {
		PeakList pl = super.getPeakList(sort, normalize);
		if (pl != null) {
			pl.setIntensity(intensity);
		}
		return pl;
	}

	/**
	 * Helper method to parse meta-info.
	 * 
	 * Peaklist-level meta data are parsed
	 * 
	 * @return true if there is a peak list, false if not.
	 * @throws IOException
	 */
	private boolean parseMetaInfo() throws IOException {
		// handle meta-information
		String string = in.readLine();
		if (string == null) {
			return false;
		}
		String parts[] = string.split("[\\s,]");

		// set the parent ion mass
		this.parentIonMassOverChargeInDaltons = Double.parseDouble(parts[0]);
		// try to handle charge
		this.intensity = Double.parseDouble(parts[1]);
		// try to handle charge
		this.parentIonCharge = Integer.parseInt(parts[2]);
		return true;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getIntensity()
	 */
	public double getIntensity() {
		return intensity;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() throws Exception {
		// flag off the start of a new peak list
		startOfPeakList = false;

		// use the cache if it exists
		if (next != null) {
			Peak temp = next;
			next = null;
			return temp;
		}
		return next(null);
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
	 * helper to parse peaks If not inside PeakList parsing, calls parseMetaInfo
	 * to parse peaklist-level meta info. On EOF, returns null and resets start
	 * of next PeakList flag. parses line for Peak values and return Peak
	 * objects. On reaching newline, marks flags to indicate potential start of
	 * next PeakList and end of processing of this PeakList. Note that meta data
	 * parsing is not done anywhere else. It's all run from this function.
	 */
	private Peak next(String line) throws Exception {
		// read the next line
		if (line == null) {
			line = in.readLine();
			// check for EOF
			if (line == null) {
				return null;
			}
		}

		// check for newline (end of peaklist)
		if (line.equals("")) {
			if (parseMetaInfo()){
			   startOfPeakList = true;
			}
			return null;
		}
		// process ions
		String[] split = line.split("[\\s,]");

		//  set the first entry as parent ion mass and the associated charge
		double massOverChargeInDaltons;
		double intens;
		try {
			massOverChargeInDaltons = Double.parseDouble(split[0]);
			intens = Double.parseDouble(split[1]);
		} catch (Exception e) { // Any non-Peak data will return null (e.g.
								// empty line)
			return null;
		}
		// return the appropriate fragment
		return new Peak(massOverChargeInDaltons, intens);
	}

}
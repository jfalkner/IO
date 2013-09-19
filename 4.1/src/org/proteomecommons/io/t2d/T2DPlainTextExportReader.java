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
package org.proteomecommons.io.t2d;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.*;
import org.proteomecommons.jaf.Atom;

/**
 * A PeakListReader implementation that reads Sequence DTA files. m/z values are
 * adjusted to the mass that a mass spec would observe.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class T2DPlainTextExportReader extends GenericPeakListReader implements
		PeakListReader {
	private BufferedReader br;

	// flag for start of peak list
	boolean isStartOfPeakList = true;

	// buffer for the next peak
	Peak next = null;

	/**
	 * @see org.proteomecommons.io.PeakListReader#close()
	 */
	public void close() {
		try {
			br.close();
		} catch (Exception e) {
			// noop
		}
		// set it to null
		br = null;
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
	public T2DPlainTextExportReader(String filename) throws IOException {
		this(new FileInputStream(filename));
		// set the name
		setName(filename);
	}

	public T2DPlainTextExportReader(InputStream in) {
		super(in);
		// load the file reference
		br = new BufferedReader(new InputStreamReader(getInputStream()));

		try {
			String line = br.readLine();
			String[] parts = line.split("\\s");

			// set the parent peak
			GenericPeak p = new GenericPeak();
		} catch (Exception e) {
			throw new RuntimeException("Couldn't initialize the T2D file.");
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() {
		try {
			// flag off start
			isStartOfPeakList = false;

			// use the cache if it is there
			if (next != null) {
				Peak temp = next;
				next = null;
				return temp;
			}

			// read a line
			String line = br.readLine();
			if (line == null) {
				return null;
			}
			// split
			String[] parts = line.split("\\s");

			//  set the first entry as parent ion mass and the associated charge
			double massOverChargeInDaltons;
			double intensity;
			try {
				massOverChargeInDaltons = Double.parseDouble(parts[0]);
				intensity = Double.parseDouble(parts[1]);
			} catch (Exception e) {
				return null;
			}
			// return the appropriate fragment
			GenericPeak p = new GenericPeak();
			p.setMassOverCharge(massOverChargeInDaltons);
			p.setIntensity(intensity);
			return p;
		} catch (Exception e) {
			return null;
		}
	}
}
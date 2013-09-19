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
package org.proteomecommons.io.txt;

import org.proteomecommons.io.*;

import java.util.*;
import java.io.*;

/**
 * Support for reading plain-text files that have points delimited by
 * whitespace.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PlainTextSpectrumReader extends SpectrumReader {
   
   /**
    * Handle the IO stream.
    * @param in
    */
   public PlainTextSpectrumReader(InputStream in){
   	// save the inputstream
   	super(in);
   }
	
	
	/**
	 * @see org.proteomecommons.io.SpectrumReader#close()
	 */
	public void close() {
		throw new RuntimeException("Stream support not implemented.");
	}

	/**
	 * @see org.proteomecommons.io.SpectrumReader#hasNext()
	 */
	public boolean hasNext() {
		throw new RuntimeException("Stream support not implemented.");
	}

	/**
	 * @see org.proteomecommons.io.SpectrumReader#isStartOfSpectrum()
	 */
	public boolean isStartOfSpectrum() {
		throw new RuntimeException("Stream support not implemented.");
	}

	/**
	 * @see org.proteomecommons.io.SpectrumReader#next()
	 */
	public double[] next() throws Exception {
		throw new RuntimeException("Stream support not implemented.");
	}

	/**
	 * Override the default getSpectrum() method. TODO: don't be lazy add proper
	 * stream support.
	 * 
	 * @param stream
	 * @return
	 */
	public Spectrum getSpectrum(boolean normalize, boolean sort) {
		// parse points
		try {
			Spectrum spectrum = new Spectrum();

			ArrayList points = new ArrayList();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(getInputStream()));
			// kill first line
			in.readLine();
			for (String line = in.readLine(); line != null && line.length() > 1; line = in
					.readLine()) {
				String[] split = line.split("\t");
				double[] point = new double[2];
				point[0] = Double.parseDouble(split[0]);
				point[1] = Double.parseDouble(split[1]);
				points.add(point);

			}

			// convert points to array
			spectrum.setPoints((double[][]) points.toArray(new double[0][0]));

			// return the spectrum
			return spectrum;

		} catch (Exception e) {
			e.printStackTrace();
		}

		// if the data can't be read, return null
		return null;
	}
}
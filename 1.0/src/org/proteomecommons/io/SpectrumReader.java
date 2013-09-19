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
package org.proteomecommons.io;

import java.util.*;
import java.io.*;

/**
 * @author Jayson Falkner - jfalkner@umich.edu
 * 
 * Abstract class for reading a spectrum. TODO: add in stream support, similar
 * to the peak list reader.
 */
public abstract class SpectrumReader {

	/**
	 * Helper method to read the spectrum.
	 * 
	 * @param spectrumFile
	 * @return
	 */
	public static Spectrum read(String spectrumFile) {

		// parse points
		try {
			Spectrum spectrum = new Spectrum();
			ArrayList points = new ArrayList();
			BufferedReader in = new BufferedReader(new FileReader(spectrumFile));
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
			spectrum.points = (double[][]) points.toArray(new double[0][0]);

			// return the spectrum
			return spectrum;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// if the data can't be read, return null
		return null;
	}
}
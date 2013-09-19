/*
 *    Copyright 2004-2005 University of Michigan
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
package org.proteomecommons.io.util;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * Print the spectrum. A simple tool to detect if a spectrum reader is working.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PrintSpectrum {
	public static void main(String[] args) {
		// flags
		boolean debug = false;

		// check for at least one arg
		if (args.length < 1) {
			System.out.println("usage: java PrintSpectrum <spectrum file>");
			System.out.println(" e.g.: java PrintSpectrum spectrum.txt");
			return;
		}

		// read/print the peak list
		if (debug) {
			System.out.println("Reading " + args[0] + ", file size: "
					+ new File(args[0]).length());
		}

		// try reading/parsing the peak list
		try {
			int totalPeakLists = 0;
			SpectrumReader reader = SpectrumReader.newReader(args[0]);
			for (Spectrum pl = reader.getSpectrum(false, false); pl != null; pl = reader
					.getSpectrum(false, false)) {
				// increment peak lists
				totalPeakLists++;

				// print the name
				System.out.println(pl.getName());

				// print the peaks
				double[][] points = pl.getPoints();
				for (int i = 0; i < points.length; i++) {
					System.out.println(points[i][0]	+ ", " + points[i][1]);
				}

				// debug total
				if (debug) {
					System.out.println("Total Peaks: " + points.length);
				}
			}
			if (debug) {
				System.out.println("Total peak lists found: " + totalPeakLists);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error reading peak list!");
		}
	}
}
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
 * Print the spectum(a) using the stream IO interfaces. A simple method to test
 * if a spectrum reader is correctly reading data through use of the
 * memory-efficient interfaces.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PrintSpectrumStream {
	public static void main(String[] args) {
		// flags
		boolean debug = false;

		// check for at least one arg
		if (args.length < 1) {
			System.out
					.println("usage: java PrintSpectrumStream <peak list file>");
			System.out.println(" e.g.: java PrintSpectrumStream spectrum.txt");
			return;
		}

		// read/print the peak list
		if (debug) {
			System.out.println("Reading " + args[0] + ", file size: "
					+ new File(args[0]).length());
		}

		// try reading/parsing the peak list
		try {
			int totalSpectra = 0;
			SpectrumReader reader = SpectrumReader.newReader(args[0]);
			while (reader.isStartOfSpectrum()) {
				// increment peak lists
				totalSpectra++;

				// print the name
				System.out.println(reader.getName());

				// write the peaks
				while (reader.hasNext()) {
					// get the next peak
					double[] d = reader.next();
					// print each value
					for (int i = 0; i < d.length; i++) {
						if (i != 0) {
							System.out.print("\t");
						}
						System.out.print(d[i]);
					}
					System.out.println("");
				}
			}
			if (debug) {
				System.out.println("Total spectra found: " + totalSpectra);
			}
		} catch (Exception e) {
			System.out.println("Error reading spectrum!");
		}
	}
}
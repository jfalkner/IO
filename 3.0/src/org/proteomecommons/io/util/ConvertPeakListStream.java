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
 * This is a simple program that converts peak lists from one format to another.
 * This program is identical to the ConvertPeakList class, but it uses the
 * stream interfaces instead of in-memory.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ConvertPeakListStream {
	public static void main(String[] args) {
		// flags
		boolean normalize = false;
		boolean sort = false;

		// check for at least one arg
		if (args.length < 2) {
			System.out
					.println("usage: java ConvertPeakList <input file> <output file>");
			System.out
					.println(" e.g.: java ConvertPeakList peaklist.mgf peaklist.dta");
			System.out
					.println("\nThis program takes pairs of arguments and uses them to convert peak list files from one format to another. Odd arguments are treated as input. Even arguments are treated as output.");
			System.out.println("\nOptions:");
			System.out.println("--normalize\tNormalize intensities [0-1]");
			System.out.println("--sort\tSort m/z in ascending order");
			return;
		}

		for (int i = 0; i < args.length - 1; i += 2) {
			// try reading/parsing the peak list
			try {
				PeakListReader reader = PeakListReader.newReader(args[i]);
				PeakListWriter writer = PeakListWriter.newWriter(args[i + 1]);
				
				// read via stream
				while (reader.isStartOfPeakList()) {
					// print the parent mass/charge
					writer.startPeakList(reader.getParentIonMassOverChargeInDaltons(),
							reader.getIntensity(), reader.getParentIonCharge());
					
					// write the peaks
					while (reader.hasNext()) {
						// get the next peak
						Peak p = reader.next();
						writer.write(p);
					}
				}
				// finish the writer
				writer.finish();

			} catch (Exception e) {
				System.out.println("Error converting " + args[i] + " or writing "
						+ args[i + 1] + ".");
			}
		}

	}
}
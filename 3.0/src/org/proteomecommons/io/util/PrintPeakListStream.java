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
 * Print the peak list(s) using the stream IO interfaces. A simple method to
 * test if a peak list reader is correctly reading peaks through use of the
 * memory-efficient interfaces.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PrintPeakListStream {
	public static void main(String[] args) {
		// flags
		boolean debug = false;

		// check for at least one arg
		if (args.length < 1) {
			System.out.println("usage: java PrintPeakList <peak list file>");
			System.out.println(" e.g.: java PrintPeakList peaklist.mgf");
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
			PeakListReader reader = PeakListReader.newReader(args[0]);
			while (reader.isStartOfPeakList()) {
				// increment peak lists
				totalPeakLists++;

				// print the name
				System.out.println(reader.getName());

				// print the parent mass/charge
				System.out.println(reader.getParentIonMassOverChargeInDaltons()
						+ "\t" + reader.getParentIonCharge());
				// write the peaks
				while (reader.hasNext()) {
					// get the next peak
					Peak p = reader.next();
					System.out.println(p.massOverChargeInDaltons + ", "
							+ p.intensity);
				}
			}
			if (debug) {
				System.out.println("Total peak lists found: " + totalPeakLists);
			}
		} catch (Exception e) {
			System.out.println("Error reading peak list!");
		}
	}
}
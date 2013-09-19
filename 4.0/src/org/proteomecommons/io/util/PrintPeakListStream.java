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
		// filename to read
		String filename = null;

		// check for at least one arg
		if (args.length < 1 || args[0].equals("-help")
				|| args[0].equals("--help")) {
			System.out.println("usage: java PrintPeakListStream <peak list file>");
			System.out.println(" e.g.: java PrintPeakListStream peaklist.mgf");
			System.out.println("");
			System.out.println("More Options:");
			System.out.println("-reader <class name> <regular expression>");
			Help.printMessage("This option loads a custom PeakListReaderFactory (i.e. your code) and registers it using the given regular expression.");
			return;
		}
		
		// check args
		for (int i = 0; i < args.length; i++) {
			// check for the -reader flag
			if (args[i].equals("-reader")) {
				try {
					// get the class
					PeakListReaderFactory srf = (PeakListReaderFactory) Class
							.forName(args[i + 1]).newInstance();
					// get the regexp
					String regexp = args[i + 2];
					// register it
					GenericPeakListReader.setPeakListReader(srf, regexp);
				} catch (Exception e) {
					System.out.println("Couldn't load " + args[i + 1]
							+ ", skipping.");
					return;
				}
				// increment index
				i+=2;
				continue;
			}
			// assume it is the file
			filename = args[i];
		}

		// check filename
		if (filename == null){
			System.out.println("You didn't specify a file to read/print.");
			return;
		}
		
		// read/print the peak list
		if (debug) {
			System.out.println("Reading " + filename + ", file size: "
					+ new File(filename).length());
		}

		// try reading/parsing the peak list
		try {
			int totalPeakLists = 0;
			PeakListReader reader = GenericPeakListReader
					.getPeakListReader(filename);
			
			if (reader == null){
				System.out.println("Can't find ");
				return;
			}
			
			while (reader.isStartOfPeakList()) {
				// increment peak lists
				totalPeakLists++;

				// if tandem, include the tandem data
				if (reader instanceof TandemPeakListReader) {
					TandemPeakListReader tandem = (TandemPeakListReader) reader;
					// print ms level
					System.out.println(tandem.getTandemCount());

					// check the parent ion peak
					Peak parent = tandem.getParent();
					// print the parent mass/charge
					if (parent.getMassOverCharge() == Peak.UNKNOWN_MZ) {
						System.out.print("UNKNOWN_MZ");
					} else {
						System.out.print(parent.getMassOverCharge());
					}
					// print the intensity
					if (parent.getIntensity() == Peak.UNKNOWN_INTENSITY) {
						System.out.print("\tUNKNOWN_INTENSITY");
					} else {
						System.out.print("\t" + parent.getIntensity());
					}
					// check if it is charged
					if (parent instanceof ChargeAssignedPeak) {
						ChargeAssignedPeak cap = (ChargeAssignedPeak) parent;
						// print out the charge
						if (cap.getCharge() == ChargeAssignedPeak.UNKNOWN_CHARGE) {
							System.out.println("\tUNKNOWN_CHARGE");
						} else {
							System.out.println("\t" + cap.getCharge());
						}
					}
				}
				// fall back on MS when it is done
				else {
					System.out.println(1);
				}
				
				
				// write the peaks
				while (reader.hasNext() && !reader.isStartOfPeakList()) {
					// get the next peak
					Peak p = reader.next();
					System.out.println(p.getMassOverCharge() + ", "
							+ p.getIntensity());
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
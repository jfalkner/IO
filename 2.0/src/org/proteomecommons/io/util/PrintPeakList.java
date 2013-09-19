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
package org.proteomecommons.io.util;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * Print the peak list. A simple method to test if a peak list reader is
 * correctly reading peaks.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PrintPeakList {
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
			PeakListReader reader = PeakListReader.newReader(args[0]);
			PeakList pl = reader.getPeakList(false, false);
			for (int i = 0; i < pl.peaks.length; i++) {
				System.out.println(pl.peaks[i].massOverChargeInDaltons + ", "
						+ pl.peaks[i].intensity);
			}

			// debug total
			if (debug) {
				System.out.println("Total Peaks: " + pl.peaks.length);
			}
			//		
			//		// save the peak list as the different formats
			//		PeakListWriter.write(pl, args[0]+".mgf");
			//		PeakListWriter.write(pl, args[0]+".dta");
		} catch (Exception e) {
			System.out.println("Error reading peak list!");
		}
	}
}
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
import java.util.*;

/**
 * @author Jayson Falkner - jfalkner@umich.edu
 * 
 * A simple utility to check if a set of peak lists contains a modification or not. You can specify as many mods as you please and you may also customize the mass accuracy tolerance.
 */
public class PotentialModificationFinder {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("usage: java -jar PotentialModificationFinder.jar <directory> [<--mz mass>] [<--tolerance daltons>]");
			System.out
					.println(" e.g.: java -jar PotentialModificationFinder.jar C:\\Peaklists --mz 80");
			System.out.println("\nIf you have any questions on how to use this, please send an e-mail to jfalkner@umich.edu");
			return;
		}

		double tolerance = .5;
		// get all masses to look for
		LinkedList masses = new LinkedList();
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("--mz")) {
				try {
					masses.add(new Double(Double.parseDouble(args[i + 1])));
				} catch (Exception e) {
					System.out.println("Can't figure out --mz " + args[i + 1]
							+ ", skipping.");
				}
			}
			if (args[i].equals("--tolerance")) {
				try {
					tolerance = Double.parseDouble(args[i+1]);
				} catch (Exception e) {
					System.out.println("Can't figure out --tolerance "
							+ args[i + 1] + ", skipping.");
					e.printStackTrace();
				}
			}
		}
		
		// check if there are any masses specified
		if (masses.size() ==0){
			System.out.println("You must specify at least one mass to look for, e.g. --mz 80");
			return;
		}

		// get the file
		File file = new File(args[0]);
		if (!file.exists()) {
			System.out.println("Can't find directory, exiting.");
			return;
		}

		// get all files in the directory
		String[] fileNames = file.list();
		LinkedList peaklists = new LinkedList();
		for (int i = 0; i < fileNames.length; i++) {
			try {
				// try to parse the peak list
				PeakList p = PeakListReader.read(file.getAbsolutePath()+File.separatorChar+fileNames[i]);
				if (p==null){
					throw new Exception("Can't read the file.");
				}
				peaklists.add(p);
			} catch (Exception e) {
				System.out.println("Skipping "+fileNames[i]+", can't parse it.");
			}
		}

		// check each for the peak
		for (Iterator it = peaklists.iterator(); it.hasNext();) {
			PeakList p = (PeakList) it.next();
			// check for the right peak
			for (Iterator mass = masses.iterator(); mass.hasNext();) {
				Double m = (Double)mass.next();
				double toMatch = p.parentIonMassOverChargeInDaltons - m.doubleValue();
				for (int i = 0; i < p.peaks.length; i++) {
           if (p.peaks[i].massOverChargeInDaltons < toMatch+tolerance && 
           		p.peaks[i].massOverChargeInDaltons > toMatch-tolerance) {
           	// print the match
           	System.out.println("Found a match at "+m+": "+p.reader.getName());
           }
				}
			}

		}

		System.out.println("Done.");
	}
}
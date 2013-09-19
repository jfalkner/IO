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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.*;

import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.filter.PeakListFilter;

/**
 * This is a simple program that converts peak lists from one format to another.
 * It can also do a number of filtering operations on the given peak lists such
 * as splitting peak lists out of a concatinated file, deisotoping, reducing
 * multiply charged ions, normalizing intensity values, and sorting peaks to
 * ensure order. Additionally, any custom PeakListFilter instance may be used
 * with this class, simply register it using the addPeakListFilter() method.
 * PeakListFilter objects are applied in the order which they are registered.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ConvertPeakList {
	private boolean split = false;

	// keep track of filters to use
	private LinkedList filters = new LinkedList();

	public void addFilter(PeakListFilter plf) {
		filters.add(plf);
	}

	public void removeFilter(PeakListFilter plf) {
		filters.remove(plf);
	}

	public PeakListFilter[] getFilters() {
		return (PeakListFilter[]) filters.toArray();
	}

	public void removeAllFilters() {
		filters.clear();
	}

	public void removeAll(Collection c) {
		filters.removeAll(c);
	}

	public void removeAll(PeakListFilter[] filters) {
		for (int i = 0; i < filters.length; i++) {
			this.filters.remove(filters[i]);
		}
	}

	public void addAll(Collection c) {
		filters.addAll(c);
	}

	public void addAll(PeakListFilter[] filters) {
		for (int i = 0; i < filters.length; i++) {
			this.filters.add(filters[i]);
		}
	}

	/**
	 * Converts the input peak list file to the output file.
	 * @param in
	 * @param out
	 */
	public void convertPeakList(String in, String out) {
		mergePeakLists(new String[]{in},  out);
	}
	
	/**
	 * Merges the given peak lists.
	 * 
	 * @param in
	 *            The files to merge.
	 * @param output
	 *            The file to write.
	 */
	public void mergePeakLists(String[] in, String output) {
		try {
			PeakListWriter writer = GenericPeakListWriter
					.getPeakListWriter(output);
			System.out.println("Making writer: "+output);
			for (int i = 0; i < in.length; i++) {
				PeakListReader reader = GenericPeakListReader
						.getPeakListReader(in[i]);
				// don't let an exception stop other peak lists
				try {
					for (PeakList pl = reader.getPeakList(); pl != null; pl = reader
							.getPeakList()) {
						// filter
						pl = filter(pl);
						// skip bad peak lists
						if (pl == null) {
							System.err.println("Bad peak list, skipping.");
						}
						// write the results
						writer.write(pl);
					}
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the utility and pipe peak lists.
	 */
	public PeakList filter(PeakList pl) {
		// track what input file is being used
		try {
			// apply each filter
			for (Iterator it = filters.iterator(); it.hasNext();) {
				PeakListFilter plf = (PeakListFilter) it.next();
				// try/catch each filter as not to break the whole chain
				try {
					pl = plf.filter(pl);
				} catch (Exception e) {
					System.err.println("Can't apply filter " + plf
							+ ", ignoring it.");
				}
			}
			// write the next peak list
			return pl;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("Can't do the conversion, skipping.");
			} catch (Exception ee) {
				// noop
			}
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		// check for at least one arg
		if (args.length < 2) {
			System.out
					.println("usage: java ConvertPeakList -merge <input file> <input file 2> ... <output file>");
			System.out
					.println(" e.g.: java ConvertPeakList -merge peaklist.mgf peaklist2.dta merge.mgf");
			System.out
					.println("\nThis program takes pairs of arguments and uses them to convert peak list files from one format to another. Odd arguments are treated as input. Even arguments are treated as output.");
			System.out.println("\nOptions:");
			//				System.out.println("--normalize\tNormalize intensities [0-1]");
			//				System.out.println("--sort\tSort m/z in ascending order");
			return;
		}

		for (int argIndex = 0; argIndex < args.length; argIndex++) {
			// skip non-directives
			if (!args[argIndex].startsWith("-")) {
				System.out.println("Skipping argument "+args[argIndex]);
				continue;
			}
			// try all the known options
			if (args[argIndex].equals("-merge")) {
				LinkedList inputs = new LinkedList();
				// assume the output is the last file
				String output = args[args.length - 1];
				for (int i = argIndex+1; i < args.length - 1; i++) {
					if (args[i].startsWith("-")){
						break;
					}
					inputs.add(args[i]);
					argIndex++;
				}
				
				// do the merge
				ConvertPeakList cpl = new ConvertPeakList();
				String[] ins = (String[]) inputs.toArray(new String[0]);
				cpl.mergePeakLists(ins, output);
			}

		}
	}
}
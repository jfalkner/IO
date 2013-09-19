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
package org.proteomecommons.io;

import org.proteomecommons.io.*;
import org.proteomecommons.jaf.*;
import java.util.*;

/**
 * A peptide reader that automatically manages missed cleaves. You can set the
 * number of missed cleaves considered using the maxMissedCleaves getter and
 * setter methods. Any other PeptideReader that wants to have generic missed
 * cleave support can be used with this.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MissedCleavePeptideReader implements PeptideReader {
	// the embedded peptide reader
	PeptideReader pr;

	// the defaul max values
	private int maxMissedCleaves = 2;

	public void setMaxMissedCleaves(int max) {
		this.maxMissedCleaves = max;
	}

	/**
	 * Get the number of missed cleaves allowed.
	 * @return
	 */
	public int getMaxMissedCleaves() {
		return maxMissedCleaves;
	}

	/**
	 * Sets the number of missed cleaves allowed.
	 * @param pr
	 */
	public MissedCleavePeptideReader(PeptideReader pr) {
		this.pr = pr;
	}

	private LinkedList oldPeptides = new LinkedList();

	private LinkedList cache = new LinkedList();

	private boolean morePeptides = true;

	/**
	 * Returns the next peptide. If missed cleaves are being considered, the
	 * missed cleaves are returned.
	 */
	public Peptide next() {
		// use the cache if it is available
		if (cache.size() > 0) {
			return (Peptide) cache.removeFirst();

		}

		// if no missed cleaves, use it
		if (maxMissedCleaves == 0) {
			return pr.next();
		}

		// add the next peptide, if it exists
		while (oldPeptides.size() <= maxMissedCleaves && morePeptides) {
			Peptide pep = pr.next();
			if (pep != null) {
				oldPeptides.add(pep);
			} else {
				morePeptides = false;
			}
		}

		// handle missed cleaves
		if (oldPeptides.size() > maxMissedCleaves || !morePeptides) {
			// make a peptide for each missed cleave
			for (int i = 0; i < oldPeptides.size(); i++) {
				// make the peptide
				int count = 0;
				for (int j = 0; j <= i && j < oldPeptides.size(); j++) {
					Peptide a = (Peptide) oldPeptides.get(j);
					count += a.getResidueCount();
				}
				// make an array of the appropriate size
				Residue[] aRes = new Residue[count];
				// make the missed cleave residue
				count = 0;
				for (int j = 0; j <= i && j < oldPeptides.size(); j++) {
					Peptide a = (Peptide) oldPeptides.get(j);
					// copy the residues
					System.arraycopy(a.getResidues(), 0, aRes, count, a
							.getResidueCount());
					count += a.getResidueCount();
				}

				// make the peptide
				Peptide temp = new Peptide(aRes);

				cache.add(temp);
			}
		}

		// pop the top residues
		if (oldPeptides.size() > 0) {
			oldPeptides.removeFirst();
		}

		// use the cache if it is available
		if (cache.size() > 0) {
			return (Peptide) cache.removeFirst();

		}
		// fall back on null
		return null;
	}
}
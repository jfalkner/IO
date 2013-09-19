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
package org.proteomecommons.io.fasta;

import java.io.FileInputStream;

import org.proteomecommons.io.PeptideReader;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 * A peptide reader that reads non-specific cleaves from a fasta entry.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class FASTANonSpecificPeptideReader implements PeptideReader {
	Residue[] residues;

	int residueIndex = 0;

	// min length in residues that a peptide must have
	private int minPeptideLength = 5;

	int length = minPeptideLength;

	// max length in residues that a peptide must have
	private int maxPeptideLength = 25;

	// min mass that a peptide must have
	private double minPeptideMass = 600;

	// max mass that a peptide must have
	private double maxPeptideMass = 3000;

	private int maxMissedCleaves = 2;

	public void setMaxMissedCleaves(int max) {
		this.maxMissedCleaves = max;
	}

	public int getMaxMissedCleaves() {
		return maxMissedCleaves;
	}

	public void setMinPeptideMass(double mass) {
		this.minPeptideMass = mass;
	}

	public double getMinPeptideMass() {
		return minPeptideMass;
	}

	public void setMaxPeptideMass(double mass) {
		this.maxPeptideMass = mass;
	}

	public double getMaxPeptideMass() {
		return maxPeptideMass;
	}

	public void setMinPeptideLength(int length) {
		this.minPeptideLength = length;
	}

	public int getMinPeptideLength() {
		return minPeptideLength;
	}

	public void setMaxPeptideLength(int length) {
		this.maxPeptideLength = length;
	}

	public int getMaxPeptideLength() {
		return maxPeptideLength;
	}

	public FASTANonSpecificPeptideReader(Residue[] residues) {
		this.residues = residues;
	}

	public Peptide next() {
		// if at the end, return null
		if (residueIndex + minPeptideLength >= residues.length) {
			return null;
		}

		// check that length isn't too large
		if (length > maxPeptideLength
				|| residueIndex + length >= residues.length) {
			length = minPeptideLength;
			residueIndex++;
			return next();
		}

		// return the peptide
		Residue[] res = new Residue[length];
		System.arraycopy(residues, residueIndex, res, 0, res.length);
		Peptide peptide = new Peptide(res);

		// increment length
		length++;

		// check restrictions
		if (peptide.getResidueCount() > maxPeptideLength) {
			return next();
		}
		if (peptide.getMass() < minPeptideMass
				|| peptide.getMass() > maxPeptideMass) {
			return next();
		}

		// return the new peptide
		return peptide;
	}

	/**
	 * Tests the code against a FASTA file.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(args[0]);
		FASTAProteinReader fpp = new FASTAProteinReader(fis);
		for (Protein p = fpp.next(); p != null; p = fpp.next()) {
			System.out.println(p.getName());
			// parse the peptides
			FASTATrypticPeptideReader tpp = new FASTATrypticPeptideReader(p
					.getSequenceAsResidues());
			for (Peptide peptide = tpp.next(); peptide != null; peptide = tpp
					.next()) {
				System.out.println(peptide + ", mz(+1): " + peptide.getMass()
						+ ", residues: " + peptide.getResidueCount());
			}
			System.out.println(p.getSequence());
		}
	}

}
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
import org.proteomecommons.jaf.residues.Arginine;
import org.proteomecommons.jaf.residues.Lysine;
import org.proteomecommons.jaf.residues.Proline;

/**
 * A trypsin parser for FASTA files.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class FASTATrypticPeptideReader implements PeptideReader {
	Residue[] residues;

	int residueIndex = -1;

	public FASTATrypticPeptideReader(Residue[] residues) {
		this.residues = residues;
	}

	public Peptide next() {
		// if at the end, return null
		if (residueIndex >= residues.length-1) {
			return null;
		}

		// track the indexes
		residueIndex++;
		int oldIndex = residueIndex;
		while (residueIndex < residues.length) {
			// cleave at Lys or Arg
			if (residues[residueIndex] instanceof Lysine
					|| residues[residueIndex] instanceof Arginine) {
				// skip if proline is next
				if (residueIndex!=residues.length-1&&!(residues[residueIndex+1] instanceof Proline)){
					break;
				}
			}
			residueIndex++;
		}

		// if at the end, set to the end
		if (residueIndex >= residues.length){
			residueIndex = residues.length-1;
		}
		// return the peptide
		Residue[] res = new Residue[residueIndex - oldIndex+1];
		System.arraycopy(residues, oldIndex, res, 0, res.length);

		// return the new peptide
		return new Peptide(res);
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
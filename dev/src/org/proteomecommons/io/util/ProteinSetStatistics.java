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

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;
import org.proteomecommons.io.TrypticPeptideReader;

import org.proteomecommons.io.fasta.generic.GenericFASTAReader;
import org.proteomecommons.jaf.*;
import org.proteomecommons.jaf.Protein;
import org.proteomecommons.jaf.Residue;

/**
 * Generate some simple statistics about a FASTA file.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class ProteinSetStatistics {
	// count single residues
	private long[] singleResidueCounts = new long[256];

	// track protein counts by residues they contain
	private long[] proteinsThatHaveResidueCounts = new long[256];

	// track peptide counts by residues they contain
	private long[] peptidesThatHaveResidueCounts = new long[256];

	// total number of proteins
	private long proteinCount = 0;

	// total number of peptides
	private long peptideCount = 0;

	// total number of residues
	private long residueCount = 0;

	// min length in residues that a peptide must have
	private int minPeptideLength = 5;

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

	/**
	 * Get the count for residue use.
	 * 
	 * @param r
	 * @return
	 */
	public long getCount(Residue r) {
		return singleResidueCounts[r.getFASTAChar()];
	}

	public long getProteinsThatHaveCount(Residue r) {
		return proteinsThatHaveResidueCounts[r.getFASTAChar()];
	}

	public long getPeptidesThatHaveCount(Residue r) {
		return peptidesThatHaveResidueCounts[r.getFASTAChar()];
	}

	public long getResidueCount() {
		return residueCount;
	}

	public long getPeptideCount() {
		return peptideCount;
	}

	public void calcStatistics(String fastaFile) {
		try {
			FileInputStream fis = new FileInputStream(fastaFile);
			GenericFASTAReader fpp = new GenericFASTAReader(fis);
			// track each protein
			for (Protein p = fpp.next(); p != null; p = fpp.next()) {
				// increment protein count
				proteinCount++;

				// track per protein residues
				int[] residueCounts = new int[256];
				// update residue counts
				Residue[] residues = p.getSequenceAsResidues();
				for (int i = 0; i < residues.length; i++) {
					// increment the total residue count
					singleResidueCounts[residues[i].getFASTAChar()]++;
					// increment per protein count
					residueCounts[(char) residues[i].getFASTAChar()]++;

					// count the residues
					residueCount++;
				}

				// update proteins with residue count
				for (int i = 0; i < residueCounts.length; i++) {
					if (residueCounts[i] > 0) {
						proteinsThatHaveResidueCounts[i]++;
					}
				}

				// do peptide stats
				LinkedList oldPeptides = new LinkedList();
				TrypticPeptideReader tpp = new TrypticPeptideReader(p
						.getSequenceAsResidues());
				for (Peptide pep = tpp.next(); pep != null; pep = tpp.next()) {
					// add the old peptide
					oldPeptides.add(pep);

					while (oldPeptides.size() > maxMissedCleaves) {
						// make a peptide for each missed cleave
						for (int i = 1; i <= maxMissedCleaves; i++) {
							// make the peptide
							int count = 0;
							for (int j = 0; j < i; j++) {
								Peptide a = (Peptide) oldPeptides.get(j);
								count += a.getResidueCount();
							}
							// make an array of the appropriate size
							Residue[] aRes = new Residue[count];
							// make the missed cleave residue
							count = 0;
							for (int j = 0; j < i; j++) {
								Peptide a = (Peptide) oldPeptides.get(j);
								// copy the residues
								System.arraycopy(a.getResidues(), 0, aRes,
										count, a.getResidueCount());
								count += a.getResidueCount();
							}

							// make the peptide
							Peptide temp = new Peptide(aRes);

							// check min/max
							if (temp.getResidueCount() < minPeptideLength
									|| temp.getResidueCount() > maxPeptideLength) {
								continue;
							}
							// check min/max
							if (temp.getMass() < minPeptideMass
									|| temp.getMass() > maxPeptideMass) {
								continue;
							}

							// calc the stats
							peptideCount++;
							//  		  				System.out.println(temp);
							// track per protein residues
							int[] peptideResidueCounts = new int[256];
							// update residue counts
							char[] peptideResidues = temp.getResiduesAsChars();
							for (int j = 0; j < peptideResidues.length; j++) {
								// increment the total residue count
								peptideResidueCounts[(char) peptideResidues[j]]++;
							}

							// update proteins with residue count
							for (int j = 0; j < peptideResidueCounts.length; j++) {
								if (peptideResidueCounts[j] > 0) {
									peptidesThatHaveResidueCounts[j]++;
								}
							}
						}
						// pop the top residues
						oldPeptides.removeFirst();
					}

				}

			}
		} catch (Exception e) {
			// noop
		}
	}

	public long getProteinCount() {
		return proteinCount;
	}

	public static void main(String[] args) {
		// make new stats
		ProteinSetStatistics fs = new ProteinSetStatistics();
		// calc stats for all FASTA files
		for (int i = 0; i < args.length; i++) {
			fs.calcStatistics(args[i]);
		}

		// display the statistics
		System.out.println("FASTA files analyzed:");
		for (int i = 0; i < args.length; i++) {
			System.out.println("  " + args[i]);
		}
		System.out.println("");

		// pretty number formats
		DecimalFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(true);
		//  	nf.setMaximumIntegerDigits(3);

		System.out.println("Total Proteins: " + fs.getProteinCount());
		System.out.println("Total Residues: " + fs.getResidueCount());
		System.out.println("Total Tryptic Peptides: " + fs.getPeptideCount());
		System.out.println("  Max missed cleaves: " + fs.getMaxMissedCleaves());
		System.out.println("  Max peptie length: " + fs.getMaxPeptideLength());
		System.out.println("  Min peptide length: " + fs.getMinPeptideLength());
		System.out.println("  Max peptide mass: " + fs.getMaxPeptideMass());
		System.out.println("  Min peptide mass: " + fs.getMinPeptideMass());

		System.out.println("\nResidue counts:");
		CommonResidue[] commonResidues = GenericResidue.getCommonResidues();
		for (int i = 0; i < commonResidues.length; i++) {
			long count = fs.getCount(commonResidues[i]);
			double percent = (double) count / (double) fs.getResidueCount()
					* 100;
			System.out.println("  " + commonResidues[i].getName() + "("
					+ commonResidues[i].getFASTAChar() + "): "
					+ nf.format(count) + " or " + nf.format(percent) + "%");
		}

		System.out.println("\nProteins with a particular residue:");
		for (int i = 0; i < commonResidues.length; i++) {
			long count = fs.getProteinsThatHaveCount(commonResidues[i]);
			double percent = (double) count / (double) fs.getProteinCount()
					* 100;
			System.out.println("  " + commonResidues[i].getName() + "("
					+ commonResidues[i].getFASTAChar() + "): "
					+ nf.format(count) + " or " + nf.format(percent) + "%");
		}

		System.out.println("\nPeptides with a particular residue:");
		for (int i = 0; i < commonResidues.length; i++) {
			long count = fs.getPeptidesThatHaveCount(commonResidues[i]);
			double percent = (double) count / (double) fs.getPeptideCount()
					* 100;
			System.out.println("  " + commonResidues[i].getName() + "("
					+ commonResidues[i].getFASTAChar() + "): "
					+ nf.format(count) + " or " + nf.format(percent) + "%");
		}
	}
}
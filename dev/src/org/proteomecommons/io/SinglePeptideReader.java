package org.proteomecommons.io;

import org.proteomecommons.jaf.Peptide;

/**
 * Utility class to make a peptide reader that only returns the peptide that it was constructed ith.
 * @author Jarret
 *
 */
public class SinglePeptideReader implements PeptideReader {
	private Peptide p;
	public SinglePeptideReader(Peptide p){
		this.p = p;
	}
	public Peptide next() {
		Peptide temp = p;
		p = null;
		return temp;
	}

}

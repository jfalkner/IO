package org.proteomecommons.io;

import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Protein;

/**
 * This is a simple utility class to make a Peptide appear as the only Protein to come out of the constructed ProteinReader.
 * @author Jarret
 *
 */
public class PeptideAsProteinReader implements ProteinReader {

	private Protein pro;
	private boolean hasReturnedOnce = false;

	public PeptideAsProteinReader(String name, Peptide p){
		this.pro = new Protein(name, p.toString());
	}
	
	public Protein next() {
		if(hasReturnedOnce){
			return null;
		}
		hasReturnedOnce = true;
		return pro;
	}

}

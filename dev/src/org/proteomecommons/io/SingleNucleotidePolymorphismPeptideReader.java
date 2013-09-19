package org.proteomecommons.io;

import org.proteomecommons.jaf.CommonResidue;
import org.proteomecommons.jaf.Peptide;
import org.proteomecommons.jaf.Residue;
import java.util.*;

/**
 * A peptide reader that generates the possible peptides from the source residues assuming 1 SNP occurs.
 * @author Jarret
 *
 */
public class SingleNucleotidePolymorphismPeptideReader extends
		GenericPeptideReader {
	private PeptideReader pr = null;
	private Stack modifiedPeps = new Stack();
	
	public SingleNucleotidePolymorphismPeptideReader(PeptideReader pr){
		this.pr  = pr;
		loadPeptideResidues();
	}
	
	/**
	 * Get the next SNP variation of the source residues.
	 */
	public Peptide next() {
		if(modifiedPeps.size() > 0){
			return (Peptide)modifiedPeps.pop();
		} else if (loadPeptideResidues()){
			return (Peptide)modifiedPeps.pop();
		} else {
			return null;
		}
	}
	
	private boolean loadPeptideResidues(){
		Peptide p = pr.next();
		if(p == null){return false;}
		
		//populate the modifiedpeps stack
		Residue[] residues = p.getResidues();
		for(int i = 0; i < residues.length; i++){
			if(residues[i] instanceof CommonResidue){
				CommonResidue cr = (CommonResidue)residues[i];
				CommonResidue[] snps = CommonResidue.getSNPs(cr);
				
				//substitute each SNP in
				for(int j = 0; j < snps.length; j++){
					Residue[] temp = new Residue[residues.length];
					System.arraycopy(residues, 0, temp, 0, residues.length);
					temp[i] = snps[j];
					modifiedPeps.push(new Peptide(temp));
				}
			}
		}
		
		return modifiedPeps.size() > 0;
	}

}

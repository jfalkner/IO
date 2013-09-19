package org.proteomecommons.io;

import org.proteomecommons.jaf.CommonResidue;
import org.proteomecommons.jaf.GenericResidue;
import org.proteomecommons.jaf.Peptide;
/**
 * A PeptideReader class that generates random Peptides.  CommonResidues from 
 * the JAF project make up the contents of the randomly generated Peptides.
 * @author Jarret
 *
 */
public class RandomPeptideGenerator extends GenericPeptideReader {
	private int howManyPeptides= 0;
	private int numReturnedPeptides = 0;
	private int badPeptideCount = 0;
	
	/**
	 * An object that creates Peptides of the specified lengths and inferred values
	 * for minimum and maximum mass based on the masses of Glycine*smallestLength
	 * and Tryptophan*largestLength.  Use the setMass methods to further filter the
	 * random peptides if the assumed minimum and maximum masses are not appropriate.
	 * @param smallestLength the length of the smallest peptide to generate
	 * @param largestLength the length of the largest peptide to generate
	 * @param howManyPeptides number of peptides to generate before next() returns null.
	 */
	public RandomPeptideGenerator(int smallestLength, int largestLength, int howManyPeptides){
		setMinPeptideLength(smallestLength);
		setMinPeptideMass(smallestLength*CommonResidue.Glycine.getMassInDaltons());
		setMaxPeptideLength(largestLength);
		setMaxPeptideMass(largestLength*CommonResidue.Tryptophan.getMassInDaltons());
		this.howManyPeptides = howManyPeptides;
	}
	
	/**
	 * An object that creates Peptides of the specified masses and inferred values for 
	 * the lengths based on floor(smallestMass/Tryptophan) and ceiling(largestMass/Glycine).
	 * Use setLength methods to further filter peptides if the assumed lenghts do not 
	 * represent desired random results.
	 * @param smallestMass the smallest mass of the generated peptides
	 * @param largestMass the largest mass of the genrated peptides
	 * @param howManyPeptides number of peptides to generate before next() returns null.
	 */
	public RandomPeptideGenerator(double smallestMass, double largestMass, int howManyPeptides){
		setMinPeptideMass(smallestMass);
		//setMinPeptideLength((int)(smallestMass/CommonResidue.Tryptophan.getMassInDaltons()));
		setMaxPeptideMass(largestMass);
		//setMaxPeptideLength((int)Math.ceil(largestMass/CommonResidue.Glycine.getMassInDaltons()));
		this.howManyPeptides = howManyPeptides;
	}

	/**
	 * Returns a randomly generated Peptide that isValid() if this object hasn't already
	 * returned its maximum number of Peptides specified upon construction.
	 */
	public Peptide next() {
		//validity checks
		if(getMinPeptideLength() > 0 && 
				getMaxPeptideLength() >= getMinPeptideLength() &&
				getMinPeptideMass() <= getMaxPeptideMass() &&
				numReturnedPeptides < howManyPeptides){
			
			//make space for the randomly generated residues
			int pepLength = (int)(Math.random()*(getMaxPeptideLength()-getMinPeptideLength()) + getMinPeptideLength());
			CommonResidue[] pepResidues = new CommonResidue[pepLength];
			
			//use the common residues as possible sources
			CommonResidue[] possibleResidues = GenericResidue.getCommonResidues();
			for(int i = 0; i < pepLength; i++){
				pepResidues[i] = possibleResidues[(int)(Math.random()*possibleResidues.length)];
			}
			
			Peptide p = new Peptide(pepResidues);
			if(isValid(p)){
				numReturnedPeptides++;
				return p;
			} else {
				badPeptideCount++;
				/*//uncomment for debug
				if(badPeptideCount % howManyPeptides == 0){
					System.out.println("Using the following paramaters, the random peptide generator has made more invalid than valid peptides.  Consider trying more reasonable values.");
					System.out.println(toString());
				}
				*/
				return next();
			}
		} else {
			return null;
		}
	}
	
	public String toString(){
		return "Generating up to: " + howManyPeptides + " in the mass window " + getMinPeptideMass() + " to " + getMaxPeptideMass() + ", the length is " +getMinPeptideLength() + " to " + getMaxPeptideLength() + " residues.\n" + 
		((double)numReturnedPeptides*100)/totalGenerated() + "% of randomly generated peptides have met the criteria for being valid (" +numReturnedPeptides+" of "+totalGenerated() + ").";
	}
	
	private int totalGenerated(){
		return numReturnedPeptides+badPeptideCount;
	}
	
	/**
	 * Set the minimum mass for generated peptides.
	 * @param newMass must be greater than or equal to Glycine's mass multiplied by the smallest allowed peptide length
	 */
	public void setMinPeptideMass(double newMass){
		if(newMass >= getMinPeptideLength()*CommonResidue.Glycine.getMassInDaltons() &&
				newMass < getMaxPeptideLength()*CommonResidue.Tryptophan.getMassInDaltons()){
			super.setMinPeptideMass(newMass);
		}
	}
	
	/**
	 * Sets the maximum mass for generated peptides.
	 * @param newMass must be less than or equal to Tryptophan's mass multipled by the largest allowed peptide length
	 */
	public void setMaxPeptideMass(double newMass){
		if(newMass <= getMaxPeptideLength()*CommonResidue.Tryptophan.getMassInDaltons() &&
				newMass > getMinPeptideLength()*CommonResidue.Glycine.getMassInDaltons()){
			super.setMaxPeptideMass(newMass);
		}
	}
}

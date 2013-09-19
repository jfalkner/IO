package org.proteomecommons.io;

import java.util.ArrayList;

import org.proteomecommons.jaf.CommonResidue;
import org.proteomecommons.jaf.Peptide;

import junit.framework.TestCase;

public class RandomPeptideGeneratorTest extends TestCase {
	RandomPeptideGenerator rpg;
	
	/*
	 * Test method for 'org.proteomecommons.io.RandomPeptideGenerator.next()'
	 */
	public void testNext() {
		rpg = new RandomPeptideGenerator(1, 1, 1);
		assertNotNull(rpg.next());
	}

	/*
	 * Test method for 'org.proteomecommons.io.RandomPeptideGenerator.RandomPeptideGenerator(int, int, int)'
	 */
	public void testRandomPeptideGeneratorIntIntIntLen1() {
		rpg = new RandomPeptideGenerator(1, 1, 1);
		assertEquals(rpg.next().getResidues().length, 1);
	}

	/*
	 * Test method for 'org.proteomecommons.io.RandomPeptideGenerator.RandomPeptideGenerator(int, int, int)'
	 */
	public void testRandomPeptideGeneratorIntIntIntLen2() {
		rpg = new RandomPeptideGenerator(2, 2, 1);
		assertEquals(rpg.next().getResidues().length, 2);
	}

	/*
	 * Test method for 'org.proteomecommons.io.RandomPeptideGenerator.RandomPeptideGenerator(int, int, int)'
	 */
	public void testRandomPeptideGeneratorIntIntIntGenerate500() {
		rpg = new RandomPeptideGenerator(2, 15, 500);
		ArrayList al = new ArrayList();
		Peptide p = rpg.next();
		while(p != null){
			al.add(p);
			p = rpg.next();
		}
		assertEquals(al.size(), 500);
	}
	
	/*
	 * Print out 5 of the randomly generated peptides.
	 */
	public void testPrint5(){
		rpg = new RandomPeptideGenerator(3, 17, 5);
		Peptide p = rpg.next();
		while(p != null){
			System.out.println(p);
			p = rpg.next();
		}
	}
	
	/*
	 * Make sure all of the peptides generated are within the mass window.
	 */
	public void testMassWindowMassUnspecified(){
		rpg = new RandomPeptideGenerator(2, 20, 20000);
		for(Peptide p = rpg.next(); p != null; p = rpg.next()){
			assertTrue(p.getMass() >= 2*CommonResidue.Glycine.getMassInDaltons() && p.getMass() <= 20*CommonResidue.Tryptophan.getMassInDaltons());
		}
		System.out.println(rpg.toString());
	}
	
	public void testMassWindowSpecified(){
		rpg = new RandomPeptideGenerator(350.0, 4000.0, 20000);
		for(Peptide p = rpg.next(); p != null; p = rpg.next()){}
		System.out.println(rpg.toString());
	}
	
	public void testSmallestMassTooLow(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		rpg.setMinPeptideMass(1.0);
		assertTrue(rpg.getMinPeptideMass() > 0);
	}
	
	public void testSmallestMassGreaterThanMaxAllowed(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		rpg.setMinPeptideMass(1000.0);
		assertTrue(rpg.getMinPeptideMass() < 240);
	}
	
	public void testLargestMassTooHigh(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		rpg.setMaxPeptideMass(20000.0);
		assertTrue(rpg.getMaxPeptideMass() < 20000.0);
	}
	
	public void testLargestMassSmallerThanMinAllowed(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		rpg.setMaxPeptideMass(200.0);
		assertTrue(rpg.getMaxPeptideMass() > 200.0);
	}
	
	public void testValidMinMassSetting(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		double oldMinMass = rpg.getMinPeptideMass();
		rpg.setMinPeptideMass(oldMinMass + 10);
		assertTrue(rpg.getMinPeptideMass() > oldMinMass);
	}
	
	public void testValidMaxMassSetting(){
		rpg = new RandomPeptideGenerator(4, 5, 5);
		double oldMaxMass = rpg.getMaxPeptideMass();
		rpg.setMaxPeptideMass(oldMaxMass - 10);
		assertTrue(rpg.getMaxPeptideMass() < oldMaxMass);
	}
}

package org.proteomecommons.io;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.proteomecommons.io.fasta.generic.GenericFASTAReader;
import org.proteomecommons.jaf.*;

import junit.framework.TestCase;

public class SingleNucleotidePolymorphismPeptideReaderTest extends TestCase {

	/*
	 * Test method for 'org.proteomecommons.io.SingleNucleotidePolymorphismPeptideReader.next()'
	 */
	public void testNext() {
		//make a mock FASTA source
		String pep = ">gi|10863937|ref|NP_066960.1| tumor necrosis factor, alpha-induced protein 1 - T7 tag on N-term [Homo sapiens]\n";
		pep += CommonResidue.A.getFASTAChar() + "" + CommonResidue.C.getFASTAChar();
		StringBufferInputStream sr = new StringBufferInputStream(pep);
		
		//read the mock source into a FASTA reader
		GenericFASTAReader frp = new GenericFASTAReader(sr);
		TrypticPeptideReader tpr = new TrypticPeptideReader(frp);
		SingleNucleotidePolymorphismPeptideReader snppr = new
			SingleNucleotidePolymorphismPeptideReader(tpr);
		ArrayList snpsGenerated = new ArrayList();
		for(Peptide p = snppr.next(); p != null; p = snppr.next()){
			snpsGenerated.add(p.toString());
		}
		
		System.out.println("SNPs generated were: "+snpsGenerated);
		
		//peptide started as A and C
		CommonResidue[] aSNPs = CommonResidue.A.getSNPs();
		CommonResidue[] cSNPs = CommonResidue.C.getSNPs();
		
		//make sure each combination of one common residue SNP is in the snpsGenerated
		for(int i = 0; i < aSNPs.length; i++){
			//System.out.println("Looking for: " + aSNPs[i].getOneLetter() + CommonResidue.C.getOneLetter());
			assertTrue(snpsGenerated.contains(new Peptide(new Residue[]{aSNPs[i], CommonResidue.C}).toString()));
		}
		for(int i = 0; i < cSNPs.length; i++){
			assertTrue(snpsGenerated.contains((new Peptide(new Residue[]{CommonResidue.A, cSNPs[i]})).toString()));
		}
	}

}

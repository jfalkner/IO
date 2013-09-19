package org.proteomecommons.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.proteomecommons.jaf.CommonResidue;
import org.proteomecommons.jaf.Peptide;

import junit.framework.TestCase;

public class GenomicPeptideReaderTest extends TestCase {

	/*
	 * Test method for 'org.proteomecommons.io.HumanGenomicPeptideReader.transcribe(String)'
	 */
	public void testTranscribe() {
		String before = "ATGC";
		assertEquals("UACG", GenomicPeptideReader.transcribe(before));
	}
	
	public void testTranscribeATCTTCATACT(){
		System.out.println("Transcribing CTCTTCATACA");
		System.out.println(GenomicPeptideReader.transcribe("CTCTTCATACA"));
		System.out.println("Complementing CTCTTCATACA");
		String complement = new String(GenomicPeptideReader.complement("CTCTTCATACA".toCharArray()));
		System.out.println(complement);
		System.out.println("flipping " + complement);
		String flip = new String(GenomicPeptideReader.flip(complement.toCharArray()));
		System.out.println(flip);
		System.out.println("Transcribing the flipped complement");
		System.out.println(GenomicPeptideReader.transcribe(flip));
	}

	/*
	 * Test method for 'org.proteomecommons.io.HumanGenomicPeptideReader.flipAndComplement(char[])'
	 */
	public void testFlipAndComplement() {
		String before = "ATGC";
		String flip = "CGTA";
		String flippedComplement = "GCAT";
		assertEquals(flippedComplement, new String(GenomicPeptideReader.flipAndComplement(before.toCharArray())));
	}
	
	public void testReading(){
		File temp;
		try {
			temp = File.createTempFile("genomic_test", ".fasta");
			temp.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write(">FASTA comment\n");
			bw.write("TAGAAGCGA"); //that's AUCUUCGCU transcribed
			bw.flush();
			bw.close();
			
			GenomicPeptideReader gpr = new GenomicPeptideReader();
			gpr.setMinPeptideLength(1);
			gpr.setMaxPeptideLength(2);
			gpr.setFastaFile(temp.getAbsolutePath());
			ArrayList peps = new ArrayList();
			Peptide p = gpr.next();
			while(p != null){
				peps.add(p);
				p = gpr.next();
			}
			System.out.println(peps);
			
			//one length peptides (forward reading)
			//AUC -> Isoleucine
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Isoleucine})));
			// UUC -> Phe
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Phenylalanine})));
			// GCU -> Alanine
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Alanine})));
			//one length peptides (forward reading, offset 1)
			//UCU
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine})));
			//UCG
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine})));
			
			
			//two length peptides: (index offset 0)
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Isoleucine, CommonResidue.Phenylalanine})));
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Phenylalanine, CommonResidue.Alanine})));
			//index offset 1
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine, CommonResidue.Serine})));
			//index offset 2 CUUCGC
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Leucine, CommonResidue.Arginine})));
			
			//three length peptides:
			//assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Isoleucine, CommonResidue.Phenylalanine, CommonResidue.Alanine})));
			
			//(reverse reading) TAGAAGCGA is AGC,GAA,GAT reversed... complement is TCG,CTT,CTA
			//So... it's AGCGAAGAU
			//one length peptides 
			//AGC -> Serine
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine})));
			//GAA -> Glutamic
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.GlutamicAcid})));
			//GAU -> Aspartic
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.AsparticAcid})));
			
			//two length peptides
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine, CommonResidue.GlutamicAcid})));
			assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.GlutamicAcid, CommonResidue.AsparticAcid})));
			
			//three length peptide
			//assertTrue(peps.contains(new Peptide(new CommonResidue[]{CommonResidue.Serine, CommonResidue.Leucine, CommonResidue.Leucine})));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

package org.proteomecommons.io.gpmdb;

import org.proteomecommons.jaf.Peptide;

import junit.framework.TestCase;

public class GPMDBPeptideReaderTest extends TestCase {

	/*
	 * Test method for 'org.proteomecommons.io.gpmdb.GPMDBPeptideReader.removeAllTags(String)'
	 */
	public void testRemoveAllTags() {
		GPMDBPeptideReader reader = new GPMDBPeptideReader(null);
		String withTags = "blah<tag>blah";
		assertEquals("blahblah", reader.removeAllTags(withTags));
	}


//	/*
//	 * Test method for 'org.proteomecommons.io.gpmdb.GPMDBPeptideReader.removeAllTags(String)'
//	 */
//	public void testRemoveAllTags2() {
//		GPMDBPeptideReader reader = new GPMDBPeptideReader(null);
//		String withTags = "blah<tag>blah<tag>";
//		assertEquals("blahblah", reader.removeAllTags(withTags));
//		
//
//		withTags = "HPGDFGADAQGA<span title=\"+15.9949 \" class=\"mod\">M</span>";
//		assertEquals("HPGDFGADAQGAM;+15.9949:", reader.removeAllTags(withTags));
//
//		withTags = "HPGDFGADAQGA<span title=\"+15.9949 \" class=\"mod\">M</span>D";
//		assertEquals("HPGDFGADAQGAM;+15.9949:D", reader.removeAllTags(withTags));
//
//		withTags = "HPGD<span title=\"+15.9949 \" class=\"mod\">D</span>FGADAQGA<span title=\"+15.9949 \" class=\"mod\">M</span>D";
//		assertEquals("HPGDD;+15.9949:FGADAQGAM;+15.9949:D", reader.removeAllTags(withTags));
//		
//		String pep = "HPGDD;+15.9949:FGADAQGAM;+15.9949:D";
//		assertEquals(pep, reader.filterAminoAcidChars(pep));
//	}

	/*
	 * Test method for 'org.proteomecommons.io.gpmdb.GPMDBPeptideReader.removeAllTags(String)'
	 */
	public void testModMassChanges() {
		GPMDBPeptideReader reader = new GPMDBPeptideReader(null);
		String withTags = "HPGDFGADAQGA<span title=\"+15.9949 \" class=\"mod\">M</span>";
		System.out.println(reader.modMassChanges(withTags));
		assertTrue(reader.modMassChanges(withTags)[0].equals("+15.9949 "));
		System.out.println(Double.parseDouble(reader.modMassChanges(withTags)[0]));
	}

//	/*
//	 * Test method for 'org.proteomecommons.io.gpmdb.GPMDBPeptideReader.removeAllTags(String)'
//	 */
//	public void testValidatePeptideSequence() {
//		GPMDBPeptideReader reader = new GPMDBPeptideReader(null);
//		String pep = "HPGDC;+15.9949:FGADAQGAM;+15.9949:D";
//		assertEquals(pep, reader.filterAminoAcidChars(pep));
//		
//		pep = reader.validatePeptideSequence(pep);
//		
//		try {
//			Peptide p = new Peptide(pep);
//		} catch (Exception e) {
//			fail();
//		}
//	}

}

package org.proteomecommons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import org.proteomecommons.jaf.CommonResidue;
import org.proteomecommons.jaf.Peptide;

/**
 * A six-frame genetic reader of FASTA nucleotide BLAST database dumps.
 * @author Jarret
 *
 */
//command to make a suitable FASTA file:
//C:\blast\bin>fastacmd -d C:\human_genomic\human_genomic -p F -D 1 -o c:\human_genomic\human_genomic_nucleotide.fasta
public class GenomicPeptideReader extends GenericPeptideReader {
	class HGPRInner {
		private BufferedReader genome;
		private String buff = "";
		private Stack nucleotides = null;
		private int currentPeptideLength;
		
		/**
		 * This inner class handles simple reading of the genome data in two directions, starting at one index.
		 * @param humanGenome the genome to parse for peptides
		 * @throws Exception
		 */
		public HGPRInner(File humanGenome, int pepLength) throws Exception{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(humanGenome));
			BufferedReader br = new BufferedReader(isr);
			
			//ditch the first line of fasta comments
			br.readLine();
			
			this.genome = br;
			currentPeptideLength = pepLength;
		}
		
		public Peptide next() {
			try{
				if(nucleotides == null){ //initialize the stack
					nucleotides = new Stack();
					buff = readANucleotide();
					if(buff == null){
						System.err.println("Can't understand the FASTA file.");
						return null;
					}
					
					//make the first sequence of currentPeptideLength peptides
					while(buff.length() < currentPeptideLength*3){
						buff = buff + readANucleotide();
					}
					addBuffer();
					//System.out.println("Initial nucleotides: " + nucleotides);
				} 
				
				if(!nucleotides.isEmpty()){ //the stack has something
					String makeMeIntoPeptide = (String)nucleotides.pop();
					return CommonResidue.peptideFromCodons(transcribe(makeMeIntoPeptide));
				}
				
				//add something to the stack, and return a new peptide
				String moreNucleotide = readANucleotide();
				if(moreNucleotide == null){ // finished
					return null;
				} else {
					buff = buff.substring(1, buff.length()) + moreNucleotide;
					addBuffer();
				}
				
				String makeMeIntoPeptide = (String)nucleotides.pop();
				return CommonResidue.peptideFromCodons(transcribe(makeMeIntoPeptide));
				
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * Central method to put the current buffer in the stack, as well as the reverse complement.
		 *
		 */
		private void addBuffer() {
			nucleotides.add(buff);
			char[] flipMe = buff.toCharArray();
			char[] flipped = flipAndComplement(flipMe);
			nucleotides.add(new String(flipped));
		}
		
		private String readANucleotide() throws Exception{
			int i = genome.read();
			while(i != -1){
				if(i == 'T' || i == 'A' || i == 'C' || i == 'G'){
					return new String(new char[]{(char)i});
				} 
				i = genome.read();
			}
			return null;
		}
	}

	private String fasta = null;
	private Stack readers = null;
	
	public GenomicPeptideReader(){
	}
	
	public void setFastaFile(String fasta){
		this.fasta = fasta;
	}
	
	public String getFastaFile(){
		return fasta;
	}
	
	public Peptide next(){
		try{
			if(readers == null){ //initialize all of the possible 6 frame reading
				readers = new Stack();
				for(int pepLength = getMinPeptideLength(); pepLength < getMaxPeptideLength()+1; pepLength++){
					readers.add(new HGPRInner(new File(getFastaFile()), pepLength)); //skip no nucleotides
				}
				System.out.println(readers.size() + " subreaders made for this genome.");
			}
			
			//check a reader for a pep
			if(readers.isEmpty()){
				return null;
			}
			
			HGPRInner pepGen = (HGPRInner)readers.peek();
			Peptide p = pepGen.next();
			if(p == null){
				readers.pop();
				return next();
			} 
			
			return p;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Perform an RNA transcription of the specified DNA. 
	 * @param dna a sequence to translate
	 * @return the specified String with T->A, A->U, C->G, and G->C
	 */
	//TODO write test case
	public static String transcribe(String dna){
		char[] translate = dna.toCharArray();
		for(int i = 0; i < translate.length; i++){
			if(translate[i] == 'T'){
				translate[i] = 'A';
			} else if (translate[i] == 'A'){
				translate[i] = 'U';
			} else if (translate[i] == 'C'){
				translate[i] = 'G';
			} else if (translate[i] == 'G'){
				translate[i] = 'C';
			}
		}
		return new String(translate);
	}
	
	/**
	 * Reverses and compliements the specified dna sequence.
	 * For example, TAG -> GAT -> CTA
	 * @param 
	 * @return the complemented reverse of dna
	 */
	//TODO could make memory usage be more local if this is done in the same loop
	//TODO test case
	public static char[] flipAndComplement(char[] dna){
		return complement(flip(dna));
	}
	
	public static char[] flip(char[] dna){
		int left = 0;
		int right = dna.length -1;
		while(left < right){
			char temp = dna[left];
			dna[left] = dna[right];
			dna[right] = temp;
			left++;
			right--;
		}
		return dna;
	}
	
	public static char[] complement(char[] dna){
		for(int i = 0; i < dna.length; i++){
			if(dna[i] == 'T'){
				dna[i] = 'A';
			} else if (dna[i] == 'A'){
				dna[i] = 'T';
			} else if (dna[i] == 'C'){
				dna[i] = 'G';
			} else if (dna[i] == 'G'){
				dna[i] = 'C';
			} else {
				System.err.println("The only valid characters to complement in DNA are T,A,C, and G.");
			}
		}
		return dna;
	}
	
	public static void main(String[] args) throws Exception{
		GenomicPeptideReader hgpr = new GenomicPeptideReader();
		hgpr.setFastaFile((args[0]));
		hgpr.setMinPeptideLength(5);
		hgpr.setMaxPeptideLength(20);
		Peptide p = hgpr.next();
		//System.out.println(p);
		int count = 0;
		long start = System.currentTimeMillis();
		while(p != null){
			p = hgpr.next();
			count++;
			if(count %1000000 == 0){
				System.out.println(count + " peptides in " + (System.currentTimeMillis() - start)/1000 + " seconds.");
			}
		}
		System.out.println("Finished. Generated " + count + " peptides in " + (System.currentTimeMillis() - start)/1000 + " seconds.");
	}
	
}

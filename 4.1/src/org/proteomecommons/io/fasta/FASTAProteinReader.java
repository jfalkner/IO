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
package org.proteomecommons.io.fasta;

import java.io.FileInputStream;
import java.io.InputStream;

import org.proteomecommons.io.ProteinReader;
import org.proteomecommons.jaf.Protein;

/**
 * An abstraction for reading proteins from a protein database.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class FASTAProteinReader implements ProteinReader {
	private InputStream fastaData;

	byte[] name = new byte[100000];

	int nameIndex = 0;

	byte[] sequence = new byte[1000000];

	int sequenceIndex = 0;

	/**
	 * Public constructor where the InputStream instance is assumed to be the
	 * FASTA sequence.
	 * 
	 * @param is
	 */
	public FASTAProteinReader(InputStream is) {
		this.fastaData = is;
	}

	/**
	 * Get the next protein, or null if there is no more proteins.
	 * 
	 * @return The next protein from the FASTA file or null if none or left.
	 */
	public Protein next() {
		try {
			// get the name
			for (int i = fastaData.read(); i != -1 && i != '\n'; i = fastaData
					.read()) {
				name[nameIndex] = (byte) i;
				nameIndex++;
			}

			// get the sequence
			for (int i = fastaData.read(); i != -1 && i != '>'; i = fastaData
					.read()) {
				// skip whitespace
				if (i < 'A' || i > 'Z') {
					continue;
				}
				sequence[sequenceIndex] = (byte) i;
				sequenceIndex++;
			}

			// if at the end, skip
			if (sequenceIndex == 0 && nameIndex == 1) {
				return null;
			}

			// bytes
			byte[] nameTrim = new byte[nameIndex];
			System.arraycopy(name, 0, nameTrim, 0, nameIndex);
			byte[] sequenceTrim = new byte[sequenceIndex];
			System.arraycopy(sequence, 0, sequenceTrim, 0, sequenceIndex);

			// reset the values
			nameIndex = 1;
			sequenceIndex = 0;

			// make the protein
			return new Protein(nameTrim, sequenceTrim);

		} catch (Exception e) {
			// noop
		}
		return null;
	}

	/**
	 * Test the parser.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(args[0]);
		FASTAProteinReader fpp = new FASTAProteinReader(fis);
		for (Protein p = fpp.next(); p != null; p = fpp.next()) {
			System.out.println(p.getName());
			System.out.println(p.getSequence());
		}
	}
}
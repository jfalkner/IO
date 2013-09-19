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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * This is a helper class that will shuffle the protein sequence in a FASTA
 * file. This is helpful because it gives one a reasonable set of protein
 * sequences from which they may use as "noise", i.e. searching against the
 * shuffled sequence set should give no hits, where as searching against the
 * normal sequences should give hits.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class FASTASequenceShufflerProteinWriter {
	public static void main(String[] args) throws Exception {
		// read in from the FASTA file
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				args[0]));
		// write out to a file of a similar name
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(args[0] + ".shuffled"));

		boolean isId = true;
		ByteArrayOutputStream temp = new ByteArrayOutputStream();
		// read each protein, shuffle each sequence
		for (int i = in.read(); i != -1; i = in.read()) {
			// handle ids and sequence seperately
			if (isId) {
				if (i == '\n') {
					temp.write(i);
					out.write(temp.toByteArray());
					temp.reset();
					isId = false;
				} else {
					temp.write(i);
				}
			} else {
				if (i == '>') {
					// shuffle and write out bytes
					byte[] bytes = temp.toByteArray();
					ArrayList list = new ArrayList();
					for (int count = 0; count < bytes.length; count++) {
						list.add(new Byte(bytes[count]));
					}
					// write them out randomly
					while (list.size() > 0) {
						Byte b = (Byte) list.remove((int) (list.size() * Math
								.random()));
						out.write(b.byteValue());
					}
					out.write('\n');
					isId = true;
					// reset the id
					temp.reset();
					temp.write(i);
				} else {
					// keep valid sequence characters
					if (i <= 'Z' && i >= 'A') {
						temp.write(i);
					}
				}
			}
		}

		// shuffle and write out bytes
		byte[] bytes = temp.toByteArray();
		ArrayList list = new ArrayList();
		for (int count = 0; count < bytes.length; count++) {
			list.add(new Byte(bytes[count]));
		}
		// write them out randomly
		while (list.size() > 0) {
			Byte b = (Byte) list.remove((int) (list.size() * Math.random()));
			out.write(b.byteValue());
		}

		out.flush();
		out.close();
	}

}
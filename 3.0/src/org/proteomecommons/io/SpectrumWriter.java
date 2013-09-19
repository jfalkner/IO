/*
 *    Copyright 2004-2005 University of Michigan
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
package org.proteomecommons.io;

import java.io.*;
import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.pkl.*;
import org.proteomecommons.io.txt.*;

/**
 * An abstract base class for serializing peak lists. Proper documentation is
 * consolidated in the HTML developer docs.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public abstract class SpectrumWriter {
	// abstract OutputStream for efficient buffering.
	OutputStream out;

	// manage the size of the buffer
	private int bufferSize = 100000;
	
	// name of the file
	String name = null;
	
	/**
	 * Getter method for the OutputStream.
	 * @return
	 */
	public OutputStream getOutputStream() {
		return out;
	}
	/**
	 * Setter method for this writer's arbitrary name.
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Getter method for this writer's arbitrary name.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Public constructor that abstracts the OutputStream instance.
	 * 
	 * @param out
	 */
	public SpectrumWriter(OutputStream out) {
		// buffer
		this.out = new BufferedOutputStream(out, bufferSize);
	}

	/**
	 * Helper method to write a peak list to an output stream.
	 * 
	 * @param spectrum
	 *            The peak list to serialize.
	 * @param fileName
	 *            Name of file to write.
	 * @throws Exception
	 *             Any possible IO exceptions.
	 */
	public static void write(Spectrum spectrum, OutputStream out, String type) {
		try {
			// check if it is a null peak list
			if (spectrum == null) {
				System.err.println("Warning: the spectrum is null!");
				return;
			}

		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * A helper method to serialize a collection of spectra in to a single file,
	 * e.g. an entire LC MSMS run.
	 * 
	 * @param spectrums
	 *            The peak lists to serialize.
	 * @param out
	 *            The OutputStream object to use.
	 * @param type
	 *            The format.
	 */
	public static void write(Spectrum[] spectrums, OutputStream out, String type) {
		try {
			// check if it is a null peak list
			if (spectrums == null) {
				System.err.println("Warning: the peak list is null!");
				return;
			}

			// write each spectrum
			SpectrumWriter writer = newWriter(out, type);
			for (int i = 0; i < spectrums.length; i++) {
				writer.write(spectrums[i]);
			}
		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * Helper method to get an appropriate peak list writer.
	 * 
	 * @param filename
	 *            The file to write to.
	 * @return A peak list writer that will write the appropriate format, or
	 *         null.
	 * @throws IOException
	 */
	public static SpectrumWriter newWriter(String filename) throws IOException {
		// make the file
		File file = new File(filename);
		FileOutputStream out = new FileOutputStream(file);

		// piggy-back the more robust peaklist
		return newWriter(new BufferedOutputStream(out), filename);
	}

	/**
	 * Helper method to get an appropriate peak list writer.
	 * 
	 * @param out
	 *            The output stream to serilize information to.
	 * @param type
	 *            The type of writer, e.g. file name to get.
	 * @return A SpectrumWriter implementation suitable for serializing your
	 *         data, or null if none exist.
	 */
	public static SpectrumWriter newWriter(OutputStream out, String type) {
		// null check
		if (type == null) {
			return null;
		}

		return null;
	}

	/**
	 * Method for writing a peak list.
	 * 
	 * @param spectrum
	 * @throws IOException
	 */
	public abstract void write(Spectrum spectrum) throws Exception;

	/**
	 * Start method for writing a MSMS peak list with known m/z and charge.
	 * Multiple
	 * 
	 * @param parentIonMassOverChargeInDaltons
	 * @param charge
	 *            The charge of the parent ion.
	 */
	public abstract void startSpectrum() throws IOException;

	/**
	 * Write a peak to the peak list.
	 * 
	 * @param peak
	 *            The peak to write.
	 */
	public abstract void write(double[] point) throws IOException;

	/**
	 * Finish writing the entired peak list file, including all sub-peak lists.
	 * Do not use this method to delimit the end of a single peak list when more
	 * peak lists are to occur after it (e.g. a LC MS run). Use multiple
	 * invokations of startSpectrum().
	 *  
	 */
	public abstract void finish() throws IOException;
}
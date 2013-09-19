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

import java.util.*;
import java.io.*;
import org.proteomecommons.io.txt.*;

/**
 * Abstract class for reading a spectrum. Proper documentation is consolidated
 * in this project's developer documentation.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public abstract class SpectrumReader {
	// abstract the InputStream (for buffering)
	private InputStream in;

	// buffer size
	private int bufferSize = 100000;

	// the arbitrary name or file name
	private String name = null;
	
	/**
	 * Getter for the InputStream of iterest.
	 * @return
	 */
	public InputStream getInputStream() {
		return in;
	}

	/**
	 * Getter for this reader's arbitrary name, defaults to the file read.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for this reader's arbitrary name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Public constructor.
	 * 
	 * @param in
	 *            InputStream to read data from.
	 */
	public SpectrumReader(InputStream in) {
		// buffer the input for speed
		this.in = new BufferedInputStream(in, bufferSize);
	}

	/**
	 * Read spectrum(a) data from the given file name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static SpectrumReader newReader(String fileName) {
		//handle errors
		try {
			// make a new file
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			// get an appropriate reader
			SpectrumReader reader = newReader(fis, fileName);
			return reader;
		} catch (Exception e) {
			//noop
		}

		// return no match found
		return null;
	}

	/**
	 * Return a SpectrumReader instance that is appropriate for the given
	 * format.
	 * 
	 * @param is
	 * @param fileName
	 * @return
	 */
	public static SpectrumReader newReader(InputStream is, String fileName) {
		// use plain-text
		if (fileName.endsWith(".txt")) {
			// make the reader
			PlainTextSpectrumReader ptsr = new PlainTextSpectrumReader(is);
			// set the filename
			ptsr.setName(fileName);
			return ptsr;
		}

		return null;
	}

	/**
	 * A method to notify the SpectrumReader instance that no more Peak objects
	 * or Spectrum objects are going to be read. This gives the reader a change
	 * to free up any unneeded resources that are allocated.
	 *  
	 */
	public abstract void close();

	/**
	 * Stream reading support for peaks. This method returns the next point in
	 * the spectrum list and if there are more than one peak list in the file
	 * and this was the last peak of one of the peak lists, the meta-information
	 * for the next peak list will be parsed and the isStartOfSpectrum() method
	 * will return true.
	 * 
	 * @return The next peak, may or may not be sequential.
	 */
	public abstract double[] next() throws Exception;

	/**
	 * A method to check if there are more peaks available in this peak list
	 * file. Note more than one peak list may be in a file, and you will need to
	 * use the isStartOfSpectrum() method to determine if you are at the start
	 * of a new peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public abstract boolean hasNext();

	/**
	 * A method to check if this stream is at the start of a new peak list. This
	 * method returns true only when you are at the start of a peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public abstract boolean isStartOfSpectrum();

	/**
	 * Returns a Spectrum object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large spectrum files.
	 * 
	 * @param sort
	 *            true if the peak list should be sorted, false if not
	 * @param normailze
	 *            true if the peak list intensity values should be normalized
	 *            between 0 and 1.
	 */
	public Spectrum getSpectrum(boolean sort, boolean normalize)
			throws Exception {
		// if there are no more peak lists, skip
		if (!isStartOfSpectrum()) {
			return null;
		}

		// make a new spectrum
		Spectrum spectrum = new Spectrum();
		
		// set the name
		spectrum.setName(getName());
		
		// load the peaks in to memory
		LinkedList temp = new LinkedList();
		// read all the peaks up to the next peak list
		while (hasNext()) {
			temp.add(next());
		}
		// convert to an array of peaks
		spectrum.setPoints((double[][]) temp.toArray(new double[0][0]));

		// by default normalize
		if (normalize) {
			//			spectrum.normalizePeaks();
		}

		// by default sort
		if (sort) {
			//			spectrum.sortPeaks();
		}

		// return the peak list
		return spectrum;
	}

	/**
	 * Returns a Spectrum object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large spectrum files.
	 *  
	 */
	public Spectrum getSpectrum() throws Exception {
		return getSpectrum(false, false);
	}

}
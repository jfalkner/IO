/*
 *    Copyright 2004 Jayson Falkner
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.proteomecommons.io.mgf.MascotGenericFormatPeakListReader;

/**
 * The abstact base class for reading peak list information from a file. This
 * class supports batch reading of entire peak lists or streaming reading of
 * individual peaks.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public abstract class PeakListReader {
	// the name of this reader, usually the name of a file
	protected String name;

	/**
	 * Sets the name of this PeakListReader. If you are parsing a file, and you
	 * are using one of the helper methods of this class, the name is
	 * automatically set to be the name of the file.
	 * 
	 * @param name
	 *            The name of this PeakListReader
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * A method to notify the PeakListReader instance that no more Peak objects
	 * or PeakList objects are going to be read. This gives the reader a change
	 * to free up any unneeded resources that are allocated.
	 *  
	 */
	public abstract void close();

	/**
	 * Utility method to make a new PeakListReader object that is suitable for
	 * reading the given file. The type of peak list format is guessed from the
	 * file name's extentsion.
	 * 
	 * @param fileName
	 *            The name of the file that is to be read.
	 * @return A new PeakListReader instance that is ready to provide Peak and
	 *         PeakList objects.
	 */
	public static PeakListReader newReader(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			return newReader(fis, fileName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Identical to the newReader(String) method, but information is parsed from
	 * an InputStream. This method allows you to parse peak lists from resources
	 * such as files, sockets, or any other java.io.InputStream.
	 * 
	 * @param is
	 *            The java.io.InputStream instance to read data from.
	 * @param fileName
	 *            The name from which the peak list format is to be derived,
	 *            e.g. MGF is ".mgf"
	 * @return A new PeakListReader instance that is ready to parse Peak and
	 *         PeakList objects.
	 * @throws IOException
	 */
	public static PeakListReader newReader(InputStream is, String fileName) {
		try {
			// assume it is mgf format (that is all we currently support)
			//		if (fileName.endsWith("dta")) {
			//			return new SequestDTAPeakListReader(fis, fileName);
			//		}
			if (fileName.endsWith("mgf")) {
				return new MascotGenericFormatPeakListReader(is, fileName);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the name of this PeakListReader object. If this PeakListReader
	 * was derived from a file, the name of the file is returned. For all other
	 * cases the name is undefined.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Helper method to read only the first peak list from a peak list file and
	 * return the results.
	 * 
	 * @param fileName
	 * @return The first peak list in the file, or null if there is no peak
	 *         list.
	 */
	public static PeakList read(String fileName) {
		try {
			// make a new reader
			PeakListReader plr = newReader(fileName);
			// check if it is null
			if (plr == null) {
				return null;
			}
			// ref peaklist and close off resources
			PeakList pl = plr.getPeakList();

			// finish the resource
			plr.close();

			return pl;
		} catch (Exception e) {
			// noop
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads the next peak list from the given java.io.InputStream.
	 * 
	 * @param is
	 *            The java.io.InputStream to use for peak list data.
	 * @param fileName
	 *            The name to use for guessing the peak list format.
	 * @return The next peak list in this stream, null if no valid peak lists
	 *         are found.
	 * @throws Exception
	 */
	public static PeakList read(InputStream is, String fileName) {
		try {
			// make a new reader
			PeakListReader plr = newReader(is, fileName);
			// get the peak list from it
			PeakList pl = plr.getPeakList();
			plr.close();
			return pl;
		} catch (Exception e) {
			// noop
		}
		return null;
	}

	/**
	 * Stream reading support for peaks. This method returns the next Peak in
	 * the peak list and if there are more than one peak list in the file and
	 * this was the last peak of one of the peak lists, the meta-information for
	 * the next peak list will be parsed and the isStartOfPeakList() method will
	 * return true.
	 * 
	 * @return The next peak, may or may not be sequential.
	 */
	public abstract Peak next() throws Exception;

	/**
	 * A method to check if there are more peaks available in this peak list
	 * file. Note more than one peak list may be in a file, and you will need to
	 * use the isStartOfPeakList() method to determine if you are at the start
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
	public abstract boolean isStartOfPeakList();

	/**
	 * Returns a PeakList object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large peaklist files.
	 * 
	 * @param sort
	 *            true if the peak list should be sorted, false if not
	 * @param normailze
	 *            true if the peak list intensity values should be normalized
	 *            between 0 and 1.
	 */
	public PeakList getPeakList(boolean sort, boolean normalize)
			throws Exception {
		// make a new peaklist
		PeakList peaklist = new PeakList(this);
		// set parent ion mass
		peaklist.parentIonCharge = getParentIonCharge();
		// set parent mass over charge in daltons
		peaklist.parentIonMassOverChargeInDaltons = getParentIonMassOverChargeInDaltons();
		// load the peaks in to memory
		Vector temp = new Vector();
		// read all the peaks up to the next peak list
		for (Peak p = next(); hasNext() && !isStartOfPeakList(); p = next()) {
			temp.add(p);
		}
		// convert to an array of peaks
		peaklist.peaks = (Peak[]) temp.toArray(new Peak[0]);

		// by default normalize
		if (normalize) {
			peaklist.normalizePeaks();
		}

		// by default sort
		if (sort) {
			peaklist.sortPeaks();
		}
		
		// return the peak list
		return peaklist;
	}

	/**
	 * Returns a PeakList object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large peaklist files.
	 *  
	 */
	public PeakList getPeakList() throws Exception {
		return getPeakList(true, true);
	}

	/**
	 * Returns the parent ion charge for the current peak list being parsed.
	 * 
	 * @return The parent ion charge.
	 */
	public int getParentIonCharge() {
		return 0;
	}

	/**
	 * Returns the parent ion's m/z value.
	 * 
	 * @return
	 */
	public double getParentIonMassOverChargeInDaltons() {
		return -1;
	}
}
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
package org.proteomecommons.io;

import java.io.FileInputStream;
import java.util.zip.*;
import java.util.regex.*;
import java.io.IOException;
import java.io.*;
import java.util.*;

import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.mzdata.MzDataPeakListReaderFactory;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.pkl.*;
import org.proteomecommons.io.wiff.*;
import org.proteomecommons.io.mzdata.*;
import org.proteomecommons.io.mzxml.*;
import org.proteomecommons.io.t2d.*;
import org.proteomecommons.io.zip.*;

/**
 * The abstact base class for reading peak list information from a file. This
 * class supports batch reading of entire peak lists or streaming reading of
 * individual peaks.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public abstract class GenericPeakListReader implements PeakListReader {
	private int tandemCount = TandemPeakList.UNKNOWN_TANDEM_COUNT;

	private Peak parent = null;

	private PeakList parentPeakList = null;

	// auto buffer
	private InputStream in;

	private BufferedReader textIn = null;

	// cache size
	private int cacheSize = 100000;

	// the name of this reader, usually the name of a file
	private String name;

	// map of all readers
	private static HashMap readers = new HashMap();

	static {
		// load the sequest reader factory
		SequestDTAPeakListReaderFactory sequest = new SequestDTAPeakListReaderFactory();
		setPeakListReader(sequest, ".*.dta|.*.DTA");
		// load the micromass reader
		MicromassPKLPeakListReaderFactory micromass = new MicromassPKLPeakListReaderFactory();
		setPeakListReader(micromass, ".*pkl|.*.PKL");
		//		setPeakListReader(micromass, ".*PKL");
		// load the mascot reader
		MascotGenericFormatPeakListReaderFactory mascot = new MascotGenericFormatPeakListReaderFactory();
		setPeakListReader(mascot, ".*.mgf|.*.MGF");
		// load the wiff csv reader
		AnalystQSWIFFToCSVPeakListReaderFactory wiffcsv = new AnalystQSWIFFToCSVPeakListReaderFactory();
		setPeakListReader(wiffcsv, wiffcsv.getRegularExpression());
		// load the mzXML reader
		MZXMLPeakListReaderFactory mzXML = new MZXMLPeakListReaderFactory();
		setPeakListReader(mzXML, ".*.mzXML|.*.mzxml|.*MZXML");
		// load the mzData reader
		MzDataPeakListReaderFactory mzData = new MzDataPeakListReaderFactory();
		setPeakListReader(mzData, ".*.mzData|.*.mzdata|.*MZDATA|.*xml");
		// handle plain text exports
		T2DPlainTextExportReaderFactory t2d = new T2DPlainTextExportReaderFactory();
		setPeakListReader(t2d, ".*txt");
		// add support for a known format that is ZIP compressed
		ZipPeakListReaderFactory zip = new ZipPeakListReaderFactory();
		setPeakListReader(zip, zip.getRegularExpression());
	}

	/**
	 * Convenience method for getting a BufferedReader from the InputStream.
	 * 
	 * @return
	 */
	public BufferedReader getBufferedReader() {
		// if null, make a new one
		if (textIn == null) {
			textIn = new BufferedReader(new InputStreamReader(in));
		}
		// return the buffered reader
		return textIn;
	}

	/**
	 * Helper method to get the InputStream.
	 */
	public InputStream getInputStream() {
		return in;
	}

	/**
	 * Helper method to set the InputStream.
	 */
	public void setInputStream(InputStream in) {
		this.in = new BufferedInputStream(in, cacheSize);
	}

	/**
	 * Public constructor, automatically
	 * 
	 * @param in
	 */
	public GenericPeakListReader(InputStream in) {
		this.in = new BufferedInputStream(in, cacheSize);
	}

	/**
	 * Helper method to register reader objects. Each factory is registered with
	 * a regular expression. The regular expression is used to screen filenames
	 * for matches that can be parsed by the reader.
	 * 
	 * @param factory
	 *            An instance of PeakListReaderFactory.
	 * @param regex
	 *            A regular expression that is used to match files to the
	 *            appropriate factory.
	 */
	public synchronized static void setPeakListReader(
			PeakListReaderFactory factory, String regex) {
		// treat the extension as a regular expression
		Pattern pattern = Pattern.compile(regex);
		readers.put(pattern, factory);
	}

	/**
	 * Helper method to get an appropriate reader based on a filename.
	 * 
	 * @param name
	 */
	public synchronized static PeakListReader getPeakListReader(InputStream in,
			String filename) {
		// get the appropriate factory
		PeakListReaderFactory factory = (PeakListReaderFactory) getPeakListReaderFactory(filename);

		// make the reader
		PeakListReader reader = factory.newInstance(in);
		try {
			reader.setName(filename);
		} catch (Exception e) {
			// noop
		}

		// return a new PeakListReader instance
		return reader;
	}

	public void setTandemCount(int count) {
		this.tandemCount = count;
	}

	public int getTandemCount() {
		return tandemCount;
	}

	public Peak getParent() {
		return parent;
	}

	public void setParent(Peak parent) {
		this.parent = parent;
	}

	public PeakList getParentPeakList() {
		return parentPeakList;
	}

	public void setParentPeakList(PeakList peaklist) {
		this.parentPeakList = peaklist;
	}

	/**
	 * Helper method to get an appropriate reader based on a filename.
	 * 
	 * @param name
	 *            The name of the file.
	 * @return The peak list reader appropriate for the file or null if none
	 *         exist.
	 */
	public synchronized static PeakListReader getPeakListReader(String filename) {
		try {
			// make an input stream
			FileInputStream fis = new FileInputStream(filename);
			return getPeakListReader(fis, filename);
		} catch (Exception e) {
			// fallback on returning null
			//			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Helper method to get an appropriate PeakListReaderFactory instance.
	 * 
	 * @param name
	 */
	public synchronized static PeakListReaderFactory getPeakListReaderFactory(
			String filename) {

		// check for the appropriate factory
		for (Iterator it = readers.keySet().iterator(); it.hasNext();) {
			Pattern p = (Pattern) it.next();
			Matcher m = p.matcher(filename);
			//			System.out.println(filename+": "+p.pattern());
			if (m.matches()) {
				//				System.out.println("Returning: "+p.pattern());
				return (PeakListReaderFactory) readers.get(p);
			}
		}

		// get the appropriate factory
		return null;
	}

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
	public abstract void close() throws CheckedIOException;

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
	public static PeakList read(String fileName) throws CheckedIOException {
		try {
			// make a new reader
			PeakListReader plr = getPeakListReader(fileName);
			// check if it is null
			if (plr == null) {
				return null;
			}
			plr.setName(fileName);

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
	public static PeakList read(InputStream is, String fileName)
			throws CheckedIOException {
		try {
			// make a new reader
			PeakListReader plr = getPeakListReader(is, fileName);
			// set the name
			plr.setName(fileName);
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
	public abstract Peak next() throws CheckedIOException;

	/**
	 * A method to check if there are more peaks available in this peak list
	 * file. Note more than one peak list may be in a file, and you will need to
	 * use the isStartOfPeakList() method to determine if you are at the start
	 * of a new peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public abstract boolean hasNext() throws CheckedIOException;

	/**
	 * A method to check if this stream is at the start of a new peak list. This
	 * method returns true only when you are at the start of a peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public abstract boolean isStartOfPeakList() throws CheckedIOException;

	/**
	 * Returns a PeakList object with an array of Peak objects . This method
	 * does load everything in to memory, and it is not well-suited for
	 * extraordinarily large peaklist files.
	 */
	public PeakList getPeakList() throws CheckedIOException {
		// if there are no more peak lists, skip
		if (!isStartOfPeakList()) {
			return null;
		}

		// load the peaks in to memory
		LinkedList temp = new LinkedList();
		// load first/null check
		Peak p = next();
		if (p != null) {
			// add the first peak
			temp.add(next());
		}
		// read all the peaks up to the next peak list
		while (hasNext() && !isStartOfPeakList()) {
			temp.add(next());
		}

		// make a new peaklist
		if (this instanceof TandemPeakListReader) {
			// cast to a tandem
			TandemPeakListReader tandem = (TandemPeakListReader) this;
			if (tandem.getTandemCount() != 1) {
				GenericTandemPeakList gtpl = new GenericTandemPeakList();
				gtpl.setPeaks((Peak[]) temp.toArray(new Peak[temp.size()]));
				gtpl.setParent(tandem.getParent());
				gtpl.setParentPeakList(tandem.getParentPeakList());
				return gtpl;
			}
		}

		// fall back on a peaklist
		GenericPeakList peaklist = new GenericPeakList();
		// convert to an array of peaks
		peaklist.setPeaks((Peak[]) temp.toArray(new Peak[temp.size()]));
		return peaklist;
	}
}
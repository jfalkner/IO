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

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.pkl.*;
import org.proteomecommons.io.wiff.*;
import org.proteomecommons.io.txt.*;
import org.proteomecommons.io.mzdata.*;

/**
 * An abstract base class for serializing peak lists. This class has two methods
 * of peak list serialization: in memory and stream. "in memory" is a
 * all-at-once approach where the entire peak list is parsed in to memory, i.e.
 * a PeakList object, and serialized to disk via invokation of the
 * write(PeakList) or write(PeakList[]) methods. The "stream" implementation is
 * a memory sensitive implementation where parts of the peak list are serialized
 * to disk as they are avaiable via invokation of the startPeakList(),
 * write(Peak), and finish() methods. In general the in-memory approach is
 * simple and sufficient; however, the stream approach is helpful for
 * serializing large peak list files, especially files too large to keep in
 * memory all at once.
 * 
 * See the documentation provided with this project for information on
 * individual peak list formats. Also note that individual implementations fot
 * he PeakListReader and PeakListWriter classes may provide file-format specific
 * functionality.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public abstract class GenericPeakListWriter implements PeakListWriter{
	// auto buffer
	private OutputStream out;

	private BufferedWriter textOut = null;

	// cache size
	public int cacheSize = 100000;

	// map of all readers
	private static HashMap writers = new HashMap();

	static {
		// load the sequest reader factory
		SequestDTAPeakListWriterFactory sequest = new SequestDTAPeakListWriterFactory();
		setPeakListWriter(sequest, ".*.dta|.*.DTA");
		// load the micromass reader
		MicromassPKLPeakListWriterFactory micromass = new MicromassPKLPeakListWriterFactory();
		setPeakListWriter(micromass, ".*pkl|.*.PKL");
		// load the mascot reader
		MascotGenericFormatPeakListWriterFactory mascot = new MascotGenericFormatPeakListWriterFactory();
		setPeakListWriter(mascot, ".*.mgf|.*.MGF");
		// load the plain text writers
		PlainTextPeakListWriterFactory text = new PlainTextPeakListWriterFactory();
		setPeakListWriter(text, ".*.txt|.*.TXT");
		// load the plain text writers
		PlainTextNotepadFriendlyPeakListWriterFactory notepad = new PlainTextNotepadFriendlyPeakListWriterFactory();
		setPeakListWriter(notepad, ".*.notepad.txt|.*.notepad.TXT");
		// load the mzdata writers
		MzDataPeakListWriterFactory mzData = new MzDataPeakListWriterFactory();
		setPeakListWriter(mzData, ".*.mzdata.xml|.*.mzdata.XML|.*.mzdata|.*.MZDATA");
	}
	
	/**
	 * Returns all the registered writers.
	 * @return
	 */
	public static PeakListWriterFactory[] getRegisteredWriters(){
		return (PeakListWriterFactory[])writers.values().toArray(new PeakListWriterFactory[0]);
	}

	/**
	 * Convenience method for getting a BufferedReader from the InputStream.
	 * 
	 * @return
	 */
	public BufferedWriter getBufferedWriter() {
		// if null, make a new one
		if (textOut == null) {
			textOut = new BufferedWriter(new OutputStreamWriter(out));
		}
		// return the buffered reader
		return textOut;
	}

	/**
	 * Helper method to get the raw OutputStream.
	 */
	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * Helper method to set the raw OutputStream.
	 */
	public void setOutputStream(InputStream in) {
		this.out = new BufferedOutputStream(out, cacheSize);
	}

	/**
	 * Public constructor, automatically sets up buffered IO.
	 * 
	 * @param out
	 */
	public GenericPeakListWriter(OutputStream out) {
		this.out = new BufferedOutputStream(out, cacheSize);
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
	public synchronized static void setPeakListWriter(
			PeakListWriterFactory factory, String regex) {
		// treat the extension as a regular expression
		Pattern pattern = Pattern.compile(regex);
		writers.put(pattern, factory);
	}

	/**
	 * Helper method to get an appropriate reader based on a filename.
	 * 
	 * @param name
	 */
	public synchronized static PeakListWriter getPeakListWriter(OutputStream out,
			String filename) {
		// get the appropriate factory
		PeakListWriterFactory factory = (PeakListWriterFactory) getPeakListWriterFactory(filename);

		// make the reader
		PeakListWriter writer = factory.newInstance(out);

		// return a new PeakListReader instance
		return writer;
	}

	/**
	 * Helper method to get an appropriate reader based on a filename.
	 * 
	 * @param name
	 *            The name of the file.
	 * @return The peak list reader appropriate for the file or null if none
	 *         exist.
	 */
	public synchronized static PeakListWriter getPeakListWriter(String filename) {
		try {
			// make an output stream
			FileOutputStream fos = new FileOutputStream(filename);
			return getPeakListWriter(fos, filename);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Helper method to get an appropriate PeakListReaderFactory instance.
	 * 
	 * @param name
	 */
	public synchronized static PeakListWriterFactory getPeakListWriterFactory(
			String filename) {

		// check for the appropriate factory
		for (Iterator it = writers.keySet().iterator(); it.hasNext();) {
			Pattern p = (Pattern) it.next();
			Matcher m = p.matcher(filename);
			//			System.out.println(filename+": "+p.pattern());
			if (m.matches()) {
				//				System.out.println("Returning: "+p.pattern());
				return (PeakListWriterFactory) writers.get(p);
			}
		}

		// get the appropriate factory
		return null;
	}
	
	

	/**
	 * A helper implementation for writing an entire peak lists. This class will invoke the startPeakList() method and invoke the bulk write(Peak[]) method for all the peaks.
	 */
	public void write(PeakList peaklist) throws CheckedIOException {
		// start the peak list
		startPeakList();
		// write the peaks
		write(peaklist.getPeaks());
	}
	
	/**
	 * Bulk stream-write method.
	 */
	public void write(Peak[] peaks) throws CheckedIOException {
		write(peaks, 0, peaks.length);
	}

	/**
	 * Bulk stream-write method.
	 */
	public void write(Peak[] peaks, int offset, int length)
			throws CheckedIOException {
		for (int i = 0; i < length; i++) {
			write(peaks[i + offset]);
		}
	}

	/**
	 * Finish writing the entired peak list file, including all sub-peak lists.
	 * Do not use this method to delimit the end of a single peak list when more
	 * peak lists are to occur after it (e.g. a LC MS run). Use multiple
	 * invokations of startPeakList().
	 *  
	 */
	public void close() throws CheckedIOException {
		// close the writer if it isn't
		if (out != null) {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				throw new CheckedIOException(
						"Can't close underlying IO stream.", e);
			}
		}
	}
}
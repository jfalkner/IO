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
import java.util.*;
import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.pkl.*;

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
public abstract class PeakListWriter {
	// list of writer's available
	private static HashMap writers = new HashMap();
	static {
		writers.put("Mascot Generic Format (.mgf)",".mgf");
		writers.put("Finnigan (.dta)",".dta");
		writers.put("Micromass (.pkl)",".pkl");
	}
	
	/**
	 * Gets a map of supported formats.
	 * @return
	 */
	public static final Map getFormats() {
		return writers;
	}

	/**
	 * Helper method to write a peak list to an output stream.
	 * 
	 * @param peaklist
	 *            The peak list to serialize.
	 * @param fileName
	 *            Name of file to write.
	 * @throws Exception
	 *             Any possible IO exceptions.
	 */
	public static void write(PeakList peaklist, OutputStream out, String type) {
		try {
			// check if it is a null peak list
			if (peaklist == null) {
				System.err.println("Warning: the peak list is null!");
				return;
			}

			// if .mgf, return a MGF writer
			if (type.endsWith(".mgf")) {
				MascotGenericFormatPeakListWriter writer = new MascotGenericFormatPeakListWriter(
						out);
				writer.write(peaklist);
				writer.finish();
				return;
			}
			// if .pkl or .dta use the DTA writer
			else if (type.endsWith(".dta")) {
				SequestDTAPeakListWriter writer = new SequestDTAPeakListWriter(
						out);
				writer.write(peaklist);
				writer.finish();
				return;
			}
			else if (type.endsWith(".pkl")){
				MicromassPKLPeakListWriter writer = new MicromassPKLPeakListWriter(
						out);
				writer.write(peaklist);
				writer.finish();
				return;
			}
		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * A helper method to serialize a collection of peak lists in to the sample
	 * peak list file, e.g. an entire LC MSMS run.
	 * 
	 * @param peaklists
	 *            The peak lists to serialize.
	 * @param out
	 *            The OutputStream object to use.
	 * @param type
	 *            The format to use ("pkl", "mgf", or "dta").
	 */
	public static void write(PeakList[] peaklists, OutputStream out, String type) {
		try {
			// check if it is a null peak list
			if (peaklists == null) {
				System.err.println("Warning: the peak list is null!");
				return;
			}

			
			PeakListWriter writer = newWriter(out, type);
				for (int i = 0; i < peaklists.length; i++) {
					writer.write(peaklists[i]);
				}
		} catch (Exception e) {
			// noop
		}
	}
	
	/**
	 * Method for writing a peak list.
	 * @param peaklist
	 * @throws IOException
	 */
	public abstract void write(PeakList peaklist) throws Exception;

	/**
	 * Helper method to write a peak list to a file.
	 * 
	 * @param peaklist
	 *            The peak list to serialize.
	 * @param fileName
	 *            Name of file to write.
	 * @throws Exception
	 *             Any possible IO exceptions.
	 */
	public static void write(PeakList peaklist, String fileName) {
		try {
			// make a file
			FileOutputStream fos = new FileOutputStream(fileName);

			// write the file
			write(peaklist, fos, fileName);

			// flush/close the file
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * Helper method to serialize multiple peaklists in to the same file, e.g.
	 * an entire LC MSMS run.
	 * 
	 * @param peaklists
	 * @param fileName
	 */
	public static void write(PeakList[] peaklists, String fileName) {
		try {
			// make a file
			FileOutputStream fos = new FileOutputStream(fileName);

			// write the file
			write(peaklists, fos, fileName);

			// flush/close the file
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// noop
		}
	}
	
	/**
	 * Helper method to get an appropriate peak list writer.
	 * @param filename The file to write to.
	 * @return A peak list writer that will write the appropriate format, or null.
	 * @throws IOException
	 */
	public static PeakListWriter newWriter(String filename) throws IOException {
		// make the file
		File file = new File(filename);
		FileOutputStream out = new FileOutputStream(file);
		return newWriter(new BufferedOutputStream(out), filename);
	}

	/**
	 * Helper method to get an appropriate peak list writer.
	 * 
	 * @param out
	 *            The output stream to serilize information to.
	 * @param type
	 *            The type of writer, e.g. file name to get.
	 * @return A PeakListWriter implementation suitable for serializing your
	 *         data, or null if none exist.
	 */
	public static PeakListWriter newWriter(OutputStream out, String type) {
		// null check
		if (type == null) {
			return null;
		}
		// check by extension
		if (type.endsWith("mgf") || type.endsWith("MGF")) {
			return new MascotGenericFormatPeakListWriter(out);
		}
		// check by extension
		if (type.endsWith("dta") || type.endsWith("DTA")) {
			return new SequestDTAPeakListWriter(out);
		}
		// check by extension
		if (type.endsWith("pkl") || type.endsWith("PKL")) {
			return new MicromassPKLPeakListWriter(out);
		}
		return null;
	}

	/**
	 * Start method for writing a MSMS peak list with known m/z and charge. Multiple
	 * 
	 * @param parentIonMassOverChargeInDaltons
	 * @param charge
	 *            The charge of the parent ion.
	 */
	public abstract void startPeakList(double parentIonMassOverChargeInDaltons,
			double charge) throws IOException;

	/**
	 * Start method for writing a MSMS peak list with known m/z, parent ion intensity, and charge. Multiple
	 * 
	 * @param parentIonMassOverChargeInDaltons The m/z of the parent ion.
	 * @param intensity The intensity of the parent ion, presumably this is MS^n
	 * @param charge
	 *            The charge of the parent ion.
	 */
	public abstract void startPeakList(double parentIonMassOverChargeInDaltons,
			double intensity, double charge) throws IOException;

	
	/**
	 * Write a peak to the peak list.
	 * 
	 * @param peak
	 *            The peak to write.
	 */
	public abstract void write(Peak peak) throws IOException;

	/**
	 * Finish writing the entired peak list file, including all sub-peak lists.
	 * Do not use this method to delimit the end of a single peak list when more
	 * peak lists are to occur after it (e.g. a LC MS run). Use multiple
	 * invokations of startPeakList().
	 *  
	 */
	public abstract void finish() throws IOException;
}
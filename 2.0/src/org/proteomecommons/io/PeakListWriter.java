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

import java.io.*;
import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.dta.*;

/**
 * An abstract base class for serializing peak lists.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public abstract class PeakListWriter {
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
	public static void write(PeakList peaklist, OutputStream out, String type)
			throws Exception {
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
			return;
		}
		// if .pkl or .dta use the DTA writer
		else if (type.endsWith(".dta") || type.endsWith(".pkl")) {
			SequestDTAPeakListWriter writer = new SequestDTAPeakListWriter(
					out);
			writer.write(peaklist);
			return;
		}
		// thorw an exception, no writer!
		throw new Exception("No writers for " + type + "!");
	}

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
	public static void write(PeakList peaklist, String fileName)
			throws Exception {

		// make a file
		FileOutputStream fos = new FileOutputStream(fileName);

		// write the file
		write(peaklist, fos, fileName);

		// flush/close the file
		fos.flush();
		fos.close();
	}
}
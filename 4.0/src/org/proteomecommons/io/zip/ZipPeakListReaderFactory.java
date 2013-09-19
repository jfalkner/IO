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
package org.proteomecommons.io.zip;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.*;

import org.proteomecommons.io.*;

/**
 * This is a factory for creating custom ZIP readers. The class is used to
 * provide seamless support for reading a known format that has been compressed
 * using ZIP format. Simply pass the compressed file and this class will take
 * care of uncompressing it and parsing the contents of the file using the
 * appropriate peak list reader object.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ZipPeakListReaderFactory implements PeakListReaderFactory {

	/**
	 * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
	 */
	public PeakListReader newInstance(InputStream is) {
		// read the input stream as ZIP
		ZipInputStream zis = new ZipInputStream(is);
		try {
			// get the appropriate file to handle it
			PeakListReaderFactory plrf = null;
			for (ZipEntry ze = zis.getNextEntry();ze != null; ze = zis
					.getNextEntry()) {
				plrf = GenericPeakListReader.getPeakListReaderFactory(ze
						.getName());

				// return the factory
				if (plrf != null) {
					return plrf.newInstance(zis);
				}
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Return the regular expression for what this reader can handle.
	 * 
	 * @return
	 */
	public String getRegularExpression() {
		return ".*zip|.*ZIP";
	}
}
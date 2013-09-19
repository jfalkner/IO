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

import java.io.InputStream;

/**
 * A class for picking peaks from a spectrum. This class extends the peak list
 * reader class allowing peaks to be picked from a spectrum either by in-memory
 * or stream-based methods. A default peak picking algorithm is used, but the
 * framework is modular and any other peak picking algorithm may be used. See
 * the developers documentation for more information about plugging in a custom
 * peak peaking module.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public abstract class SpectrumPeakListReader extends PeakListReader {
	
	// convert a spectrum in to a reader
	public static PeakListReader getInstance(Spectrum spectrum) {
		// return the default
		return null;
	}
	
	// convert a SpectrumReader in to a PeakListReader
	public static PeakListReader getInstance(SpectrumReader reader) {
		// return the default
		return null;
	}

	// create a PeakListReader by piping the spectrum file appropriately
	public static PeakListReader newReader(String fileName) {
		try {
			return getInstance(SpectrumReader.newReader(fileName));
		} catch (Exception e) {
			return null;
		}
	}

	// create a PeakListReader by piping the spectrum appropriately
	public static PeakListReader newReader(InputStream is, String fileName) {
		try {
			return getInstance(SpectrumReader.newReader(fileName));
		} catch (Exception e) {
			return null;
		}
	}

}
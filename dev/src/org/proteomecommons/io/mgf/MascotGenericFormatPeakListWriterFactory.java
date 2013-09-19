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
package org.proteomecommons.io.mgf;


import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 * A PeakListWriterFactory instance for creating PeakListWriter objects that can
 * serialized peak lists in MGF format.
 * 
 * @author Jayon Falkner - jfalkner@umich.edu
 */
public class MascotGenericFormatPeakListWriterFactory implements PeakListWriterFactory {

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#getFileExtension()
	 */
	public String getFileExtension() {
		return ".mgf";
	}

	public String getName() {
		return "Mascot Generic Format";
	}
	
	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#newInstance(java.io.OutputStream)
	 */
	public PeakListWriter newInstance(String filename) {
		return new MascotGenericFormatPeakListWriter(filename);
	}
}
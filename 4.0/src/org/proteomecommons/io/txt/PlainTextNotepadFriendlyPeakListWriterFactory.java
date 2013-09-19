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
package org.proteomecommons.io.txt;

import java.io.OutputStream;

import org.proteomecommons.io.*;

/**
 * A slight tweak to the normal plain text writer to make its output look good
 * in the notepad program for MS Windows.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class PlainTextNotepadFriendlyPeakListWriterFactory implements
		PeakListWriterFactory {

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#getFileExtension()
	 */
	public String getFileExtension() {
		return "txt";
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#getName()
	 */
	public String getName() {
		return "Plain Text (Notepad Friendly)";
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#newInstance(java.io.OutputStream)
	 */
	public PeakListWriter newInstance(OutputStream out) {
		PlainTextPeakListWriter writer = new PlainTextPeakListWriter(out);
		writer.setNewline("\r\n");
		return writer;
	}
}
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

import org.proteomecommons.io.*;

import java.io.*;

/**
 * A writer that dumps peak list data in plain text.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class PlainTextPeakListWriter extends GenericPeakListWriter {
	private boolean firstPeakList = true;

	private String newline = "\n";

	private String spacer = "\t";

	public void setNewline(String newline) {
		this.newline = newline;
	}

	public void setSpacer(String spacer) {
		this.spacer = spacer;
	}

	public PlainTextPeakListWriter(OutputStream out) {
		super(out);
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList()
	 */
	public void startPeakList() throws CheckedIOException {
		if (firstPeakList) {
			firstPeakList = false;
		} else {
			try {
				BufferedWriter out = getBufferedWriter();
				out.write("\n");
			} catch (Exception e) {
				throw new CheckedIOException(e);
			}
		}

	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 */
	public void write(Peak peak) throws CheckedIOException {
		try {
			BufferedWriter out = getBufferedWriter();
			out.write(peak.getMassOverCharge() + "\t" + peak.getIntensity()
					+ "\n");
		} catch (Exception e) {
			throw new CheckedIOException(e);
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#close()
	 */
	public void close() throws CheckedIOException {
		try {
			BufferedWriter out = getBufferedWriter();
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new CheckedIOException(e);
		}
	}
}
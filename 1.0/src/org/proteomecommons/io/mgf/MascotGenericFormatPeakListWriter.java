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
package org.proteomecommons.io.mgf;

import org.proteomecommons.io.*;
import java.io.*;

/**
 * @author Jayson Falkner - jfalkner@umich.edu
 * 
 * A simple writer class that can serialize peak list files.
 */
public class MascotGenericFormatPeakListWriter extends PeakListWriter {
	OutputStream out;

	public MascotGenericFormatPeakListWriter(OutputStream out) {
		this.out = out;
	}

	/**
	 * Simple method to write a MGF file from a peak list.
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(out);
		// write the headers
		osw.write("BEGIN IONS\n");
		osw
				.write("PEPMASS=" + peaklist.parentIonMassOverChargeInDaltons
						+ "\n");
		if (peaklist.parentIonCharge >= 0) {
			osw.write("CHARGE=" + peaklist.parentIonCharge + "+\n");
		} else {
			osw.write("CHARGE=" + peaklist.parentIonCharge + "-\n");
		}

		// write out the peaks
		for (int i = 0; i < peaklist.peaks.length; i++) {
			osw.write(peaklist.peaks[i].massOverChargeInDaltons + "\t"
					+ peaklist.peaks[i].intensity + "\n");
		}

		// end the ions
		osw.write("END IONS");
		osw.flush();
	}
}
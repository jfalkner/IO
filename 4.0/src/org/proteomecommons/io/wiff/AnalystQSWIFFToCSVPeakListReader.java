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
package org.proteomecommons.io.wiff;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.LinkedList;

import org.proteomecommons.io.*;

/**
 * A reader class that is capable of handling AnalystQS WIFF to CSV exports.
 * Note that this class does not natively read WIFF files. You must first use
 * AnalystQS to export the WIFF in to CSV format.
 * 
 * TODO: Return monoisotopic, centroided peak lists.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class AnalystQSWIFFToCSVPeakListReader extends GenericPeakListReader {
	LinkedList peakBuffer = new LinkedList();

	boolean isStartOfPeakList = true;

	boolean hasNext = true;

	public AnalystQSWIFFToCSVPeakListReader(InputStream is) {
		super(is);
		
		// parse the header information
		BufferedReader in = getBufferedReader();
		try {
			String s;
			for (s = in.readLine(); s != null && !s.equals("[spectra]"); s = in
					.readLine()) {
				// kill the line.
				// TODO: save the meta information!
			}

			// read the first peak list
			loadNextPeakList();
		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#close()
	 */
	public void close() {
		try {
			getBufferedReader().close();
		} catch (Exception e) {
			// noop
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() {
		return peakBuffer.size() > 0;
	}

	private void loadNextPeakList() {
		try {
			// get the buffer
			BufferedReader in = getBufferedReader();
			String line = in.readLine();
//			System.out.println("Handling ling: "+line);
			// if null, there are no more
			if (line == null) {
				return;
			}

			// flag the start of the next peak list
			isStartOfPeakList = true;

			// split the lines
			String[] split = line.split(",");
			// handle the peaks
			for (int i = 7; i < split.length; i += 2) {
				// parse the next peak
				GenericCentroidedPeak p = new GenericCentroidedPeak();
				p.setMassOverCharge(Double.parseDouble(split[i]));
				p.setIntensity(Double.parseDouble(split[i + 1]));
				peakBuffer.add(p);
			}
		} catch (Exception e) {
			// noop
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() {
		return isStartOfPeakList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() {
		isStartOfPeakList = false;
		// use the buffer
		if (peakBuffer.size() > 0) {
			// grab the next peak
			Peak p = (Peak) peakBuffer.removeFirst();

			// load more if there are more
			if (peakBuffer.size() == 0) {
				loadNextPeakList();
				hasNext = false;
			} else {
				hasNext = true;
			}

			return p;
		}

		// try to load the next peak list
		loadNextPeakList();
		while (isStartOfPeakList && peakBuffer.size()==0){
			isStartOfPeakList = false;
			loadNextPeakList();
		}

		// if the buffer is zero, return null
		if (peakBuffer.size() == 0) {
			return null;
		}

		//return the next peaklist
		return next();
	}
}
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
package org.proteomecommons.io.mzxml;

import org.proteomecommons.io.*;

import javax.xml.stream.*;

import java.io.*;

/**
 * This class reads peak list data from mzXML files. It currently skips raw
 * spectral data, and it does not preserve any of the misc meta-data in the
 * mzXML format.
 * 
 * @author Jarret Falkner - jar@cs.washington.edu
 *  
 */
public class MZXMLPeakListReader extends GenericPeakListReader implements
		TandemPeakListReader {
	//parsing sources
	private XMLStreamReader parser;

	//data about the peaks
	private int bitPrecision;

	private DataInputStream peakStream;

	private int peaksRead = 0;

	private int peaksInThisList;

	//info from the precursorMz element in mzXML
	private double parentIonOverMassCharge = Peak.UNKNOWN_MZ;

	private int parentIonCharge = ChargeAssignedPeak.UNKNOWN_CHARGE;

	private double precursorIntensity = Peak.UNKNOWN_INTENSITY;
	private boolean centroidedScan = false;

	//state variables for parsing
	private boolean inScan = false;

	private boolean inPeak = false;

	private boolean inPrecursorMz = false;

	private boolean hasMorePeaks = true;

	//the current parser event
	private int event;

	/**
	 * TODO: this method currently returns 1 (MS) when the precursor mz isn't
	 * known, and 2 (MSMS) when the precursor mass is known. This eventually
	 * should be replaced with code that properly uses the mzXML file's tandem
	 * MS info, i.e. supports MS^n.
	 */
	public int getTandemCount() {
		// return 1 if parent pass isn't known
		if (parentIonOverMassCharge == Peak.UNKNOWN_MZ) {
			return 1;
		}
		// return 2 otherwise
		return 2;
	}

	/**
	 * Create an reader for peak lists in mzXML format. This reader uses the
	 * StAX parser.
	 * 
	 * @param in
	 *            the input stream to read from
	 */
	public MZXMLPeakListReader(InputStream in) {
		super(in);
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			parser = factory.createXMLStreamReader(in);

			hasMorePeaks = scanForNextPeak();
		} catch (XMLStreamException ex) {
			System.out.println(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.GenericPeakListReader#close()
	 */
	public void close() throws CheckedIOException {
		try {
			parser.close();
			getInputStream().close();
		} catch (IOException ioe) {
			throw new CheckedIOException("Error closing input", ioe);
		} catch (XMLStreamException xse) {
			throw new CheckedIOException("Couldn't close the XML parser", xse);
		}
	}

	/**
	 * Overwrites any current peak information with the next peak list in the
	 * file
	 * 
	 * @return true if the peaks were updated with a new list. False if there
	 *         aren't any more peaks in the file
	 * @throws XMLStreamException
	 */
	private boolean scanForNextPeak() throws XMLStreamException {
		for (event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser
				.next()) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (parser.getLocalName().equals("scan")) {
					inScan = true;
					//find number of peaks
					String numPeaks = parser.getAttributeValue(null,
							"peaksCount");
					if (numPeaks != null) {
						peaksInThisList = Integer.parseInt(numPeaks);
					} else {
						throw new XMLStreamException(
								"Invalid mzXML: no peaksCount attribute in peak tag");
					}
					//find if the scan is centroided
					String centroidedText = parser.getAttributeValue(null,
							"centroided");
					//System.out.println("Centroided text is " +
					// centroidedText);
					if (centroidedText != null) {
						if (centroidedText.equals("1")) {
							centroidedScan = true;
						}
					}

				} else if (parser.getLocalName().equals("peaks")) {
					inPeak = true;
					//find precision
					String precisionText = parser.getAttributeValue(null,
							"precision");
					if (precisionText != null) {
						bitPrecision = Integer.parseInt(precisionText);
					} else {
						throw new XMLStreamException(
								"Invalid mzXML: no precision attribute in peak tag");
					}
				} else if (parser.getLocalName().equals("precursorMz")) {
					inPrecursorMz = true;
					//check if the parent intensity is specified
					String intensityText = parser.getAttributeValue(null,
							"precursorIntensity");
					if (intensityText != null) {
						precursorIntensity = Double.parseDouble(intensityText);
					} else {
						precursorIntensity = Peak.UNKNOWN_INTENSITY;
					}
					//check if the parent charge is specified
					String precursorCharge = parser.getAttributeValue(null,
							"precursorCharge");
					if (precursorCharge != null) {
						parentIonCharge = Integer.parseInt(precursorCharge);
					} else {
						parentIonCharge = ChargeAssignedPeak.UNKNOWN_CHARGE;
					}
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				if (parser.getLocalName().equals("scan")) {
					inScan = false;
				} else if (parser.getLocalName().equals("peaks")) {
					inPeak = false;
				} else if (parser.getLocalName().equals("precursorMz")) {
					inPrecursorMz = false;
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				if (inPeak) {
					setPeakInfo(parser.getText());
					return true; //peaks updated
				} else if (inPrecursorMz) {
					parentIonOverMassCharge = Double.parseDouble(parser
							.getText());
				}
				break;
			} // end switch
		}
		return false; //peaks were not updated
	}

	/**
	 * If the current peak list is exhausted, this method will move on to the
	 * next peak list in the file
	 * 
	 * @throws XMLStreamException
	 */
	private void checkForEndOfScan() throws XMLStreamException {
		if (peaksRead >= peaksInThisList) {
			hasMorePeaks = scanForNextPeak();
		}
	}

	/**
	 * Extracts the Base64 encoded data from a <peaks>tag
	 * 
	 * @param peaks
	 *            the base64 text to decode
	 */
	private void setPeakInfo(String encodedPeaks) {
		//process the base64 text
		byte[] peakBytes = Base64.decode(encodedPeaks);
		peakStream = new DataInputStream(new ByteArrayInputStream(peakBytes));
		peaksRead = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.GenericPeakListReader#next()
	 */
public Peak next() throws CheckedIOException {
		try {
			double massOverChargeInDaltons;
			double intensity;
			if (bitPrecision == 32) {
				massOverChargeInDaltons = (double) peakStream.readFloat();
				intensity = (double) peakStream.readFloat();
			} else {
				massOverChargeInDaltons = peakStream.readDouble();
				intensity = peakStream.readDouble();
			}

			peaksRead++;

			checkForEndOfScan();

			// check for centroided. TODO: distinguish between centroided and monoisotopic.
			if (centroidedScan){
				GenericMonoisotopicPeak gmp = new GenericMonoisotopicPeak();
				gmp.setMassOverCharge(massOverChargeInDaltons);
				gmp.setIntensity(intensity);
				return gmp;
			}
			// fall back on raw data
			GenericPeak gmp = new GenericPeak();
			gmp.setMassOverCharge(massOverChargeInDaltons);
			gmp.setIntensity(intensity);
			return gmp;
		} catch (XMLStreamException e) {
			throw new CheckedIOException("Error when parsing the XML file", e);
		} catch (IOException ioe) {
			throw new CheckedIOException(
					"Error reading the internal peak byte stream", ioe);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.GenericPeakListReader#hasNext()
	 */
	public boolean hasNext() throws CheckedIOException {
		if (hasMorePeaks && peaksRead < peaksInThisList) {
			//the current peak list has more
			return true;
		} else if (event == XMLStreamConstants.END_DOCUMENT) {
			//the file is exhausted
			return false;
		} else {
			try { //to get the next peak list
				scanForNextPeak();
			} catch (XMLStreamException e) {
				throw new CheckedIOException("Error parsing the XML document",
						e);
			}
			return hasMorePeaks;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.GenericPeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() throws CheckedIOException {
		return peaksRead == 0;
	}
}
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
package org.proteomecommons.io.mzdata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.proteomecommons.io.*;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.mzxml.Base64;

/**
 * Reads peak information from mzData files.
 * 
 * @author Jarret Falkner - jar@cs.washington.edu
 * @version for mzData 1.05
 */
public class MzDataPeakListReader extends GenericPeakListReader implements
		TandemPeakListReader {
	//parsing sources
	private XMLStreamReader parser;

	//the current parser event
	private int event = XMLStreamConstants.SPACE;

	//the current state of the parser
	private boolean inProcessingMethod = false;

	private boolean inSpectrum = false;

	private boolean inMzData = false;

	private boolean inIntensityData = false;

	private boolean readyToDecodePeakData = false;

	private boolean nextMzUpdated = false;

	private boolean nextIntensityUpdated = false;

	//processingMethod variables
	private boolean deisotoped = false;

	private boolean chargeDeconvolved = false;

	private boolean centroided = false;

	//info about the current peaks
	private ByteBuffer currentMzBytes;

	private ByteBuffer currentIntensityBytes;

	private int currentPrecision = 0;

	private boolean currentByteOrderIsBig = false;

	private int parentIonCharge = ChargeAssignedPeak.UNKNOWN_CHARGE;

	private double parentIonMassOverCharge = Peak.UNKNOWN_MZ;

	private double parentIonIntensity = Peak.UNKNOWN_INTENSITY;

	/**
	 * TODO: this only works for MS and MSMS at the moment, someone needs to
	 * tweak the reader to follow what MS lever the mzData file is showing.
	 */
	public int getTandemCount() {
		if (parentIonMassOverCharge != Peak.UNKNOWN_MZ) {
			return 1;
		}
		// fall back on proper MSMS
		return 2;
	}

	/**
	 * Create a reader for mzdata files
	 * 
	 * @param in
	 *            the input stream to read
	 */
	public MzDataPeakListReader(InputStream in) {
		super(in);
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			parser = factory.createXMLStreamReader(in);

			/* Find the first set of peaks to initialize the reader. */
			findMorePeaks();

		} catch (XMLStreamException ex) {
			System.out.println(ex);
		}
	}

	/**
	 * Parses the xml to update both the next mz and next intensity values.
	 * 
	 * @return true if there were more values found, false if the file is
	 *         exhausted
	 */
	private boolean findMorePeaks() throws XMLStreamException {
		while (event != XMLStreamConstants.END_DOCUMENT) {
			event = parser.next();

			//process opening tags
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (parser.getLocalName().equals("spectrum")) {
					inSpectrum = true;
					//and reset precursor info in case a previous spectrum
					// specified it
					parentIonMassOverCharge = Peak.UNKNOWN_MZ;
					parentIonIntensity = Peak.UNKNOWN_INTENSITY;
					parentIonCharge = ChargeAssignedPeak.UNKNOWN_CHARGE;
				} else if (parser.getLocalName().equals("cvParam")) {
					//check for precursor info
					String cvName = parser.getAttributeValue(null, "name");
					String cvValue = parser.getAttributeValue(null, "value");
					if (cvName != null && cvValue != null) {
						cvHelper(cvName, cvValue);
					}
				} else if (parser.getLocalName().equals("mzArrayBinary")) {
					inMzData = true;
				} else if (parser.getLocalName().equals("intenArrayBinary")) {
					inIntensityData = true;
				} else if (parser.getLocalName().equals("data")) {
					if (inMzData || inIntensityData) {
						readyToDecodePeakData = true;
						currentPrecision = Integer.parseInt(parser
								.getAttributeValue(null, "precision"));
						String nextByteOrderText = parser.getAttributeValue(
								null, "endian");
						currentByteOrderIsBig = nextByteOrderText.equals("big");
					}
				} else if (parser.getLocalName().equals("processingMethod")) {
					inProcessingMethod = true;
				}
			}

			//process text between tags
			if (event == XMLStreamConstants.CHARACTERS) {
				if (readyToDecodePeakData) {
					if (inMzData) {
						nextMzUpdated = updateMz(parser.getText());
					}
					if (inIntensityData) {
						nextIntensityUpdated = updateIntensity(parser.getText());
					}
					if (nextMzUpdated && nextIntensityUpdated) {
						//we're done. Reset state and return
						nextMzUpdated = false;
						nextIntensityUpdated = false;
						return true;
					}
				}
			}

			//process closing tags
			if (event == XMLStreamConstants.END_ELEMENT) {
				if (parser.getLocalName().equals("spectrum")) {
					inSpectrum = false;
				} else if (parser.getLocalName().equals("mzArrayBinary")) {
					inMzData = false;
				} else if (parser.getLocalName().equals("intenArrayBinary")) {
					inIntensityData = false;
				} else if (parser.getLocalName().equals("data")) {
					if (inMzData || inIntensityData) {
						readyToDecodePeakData = false;
					}
				} else if (parser.getLocalName().equals("processingMethod")) {
					inProcessingMethod = false;
				}
			}

		} //end loop

		//Couldn't find any more peaks from the xml
		return false;
	}

	/**
	 * Process encoded mz values
	 * 
	 * @return true if mz is updated
	 */
	private boolean updateMz(String encodedPeakData) {
		byte[] nextMzBytes = Base64.decode(encodedPeakData);
		currentMzBytes = ByteBuffer.wrap(nextMzBytes);

		if (currentByteOrderIsBig) {
			currentMzBytes = currentMzBytes.order(ByteOrder.BIG_ENDIAN);
		} else {
			currentMzBytes = currentMzBytes.order(ByteOrder.LITTLE_ENDIAN);
		}
		return true;
	}

	/**
	 * Process encoded intensity values
	 * 
	 * @return true if intensity is updated
	 */
	private boolean updateIntensity(String encodedPeakData) {
		byte[] nextIntensityBytes = Base64.decode(encodedPeakData);
		currentIntensityBytes = ByteBuffer.wrap(nextIntensityBytes);

		if (currentByteOrderIsBig) {
			currentIntensityBytes = currentIntensityBytes
					.order(ByteOrder.BIG_ENDIAN);
		} else {
			currentIntensityBytes = currentIntensityBytes
					.order(ByteOrder.LITTLE_ENDIAN);
		}
		return true;
	}

	/**
	 * State dependant parsing of data out of the name and value attributes of a
	 * cvParam tag
	 * 
	 * @param cvName
	 *            the value of the cvParam tag's name attribute
	 * @param cvValues
	 *            the value of the cvParam tag's value attribute
	 */
	private void cvHelper(String cvName, String cvValue) {
		//parsing dependant on being in a spectrum tag
		if (inSpectrum) {
			if (cvName.equals("mz")) {
				parentIonMassOverCharge = Double.parseDouble(cvValue);
			} else if (cvName.equals("intensity")) {
				parentIonIntensity = Double.parseDouble(cvValue);
			} else if (cvName.equals("charge")) {
				parentIonCharge = Integer.parseInt(cvValue);
			}
		}

		//parsing of processingMethod
		if (inProcessingMethod) {
			if (cvName.equals("deisotoped")) {
				deisotoped = Boolean.valueOf(cvValue).booleanValue();
			} else if (cvName.equals("chargeDeconvolved")) {
				chargeDeconvolved = Boolean.valueOf(cvValue).booleanValue();
			} else if (cvName.equals("peakProcessing")) {
				//check for both centroid and centroided, as both appear from
				// file to file
				//like myo_dta_1.05.xml -> centroid, maldi_axima-cfr.mzData ->
				// centroided
				centroided = cvValue.equals("centroid")
						|| cvValue.equals("centroided");
			}
		}

		//add any additional states to parse from... i.e. analyzer
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#close()
	 */
	public void close() throws CheckedIOException {
		try {
			parser.close();
			getInputStream().close();
		} catch (XMLStreamException xe) {
			throw new CheckedIOException("Can't close the xml parser", xe);
		} catch (IOException e) {
			throw new CheckedIOException("Can't close the source file", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() throws CheckedIOException {
		double mz = -1;
		double inten = -1;
		if (currentPrecision == 32) {
			mz = (double) currentMzBytes.getFloat();
			inten = (double) currentIntensityBytes.getFloat();

		} else {
			mz = currentMzBytes.getDouble();
			inten = currentIntensityBytes.getDouble();
		}

		// check if it is monoisotopic
		if (centroided && deisotoped) {
			GenericMonoisotopicPeak gmp = new GenericMonoisotopicPeak();
			gmp.setMassOverCharge(mz);
			gmp.setIntensity(inten);
			return gmp;
		}
		// check if it is just centroided
		else if (centroided) {
			GenericCentroidedPeak gcp = new GenericCentroidedPeak();
			gcp.setMassOverCharge(mz);
			gcp.setIntensity(inten);
			return gcp;
		}
		// fall back on a generic peak
		GenericPeak gp = new GenericPeak();
		gp.setMassOverCharge(mz);
		gp.setIntensity(inten);
		return gp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() throws CheckedIOException {
		if (currentMzBytes == null) {
			return false;
		}
		if (!centroided) {
			return false; //peak lists are centroided
		}
		if (currentMzBytes.hasRemaining()) {
			return true;
		} else {
			try { //getting more peaks from the file
				return findMorePeaks();
			} catch (XMLStreamException xe) {
				throw new CheckedIOException(
						"error reading more peaks from the xml", xe);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() throws CheckedIOException {
		if (currentMzBytes != null && centroided) {
			return currentMzBytes.position() == 0;
		} else {
			return false;
		}
	}
}
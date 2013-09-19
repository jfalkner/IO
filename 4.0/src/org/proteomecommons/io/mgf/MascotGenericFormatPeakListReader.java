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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.proteomecommons.io.*;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakListReader;

/**
 * A PeakListReader implementation designed to handle files in Mascot Generic
 * Format (.mgf), a popular format for ABI instruments.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author Dominic Battre
 *  
 */
public class MascotGenericFormatPeakListReader extends GenericPeakListReader implements TandemPeakListReader {
	// peptide mass tolerance
	private int tolerance = -1;

	// unit of tolerance
	private String unitOfTolerance = null;

	// title of peak list
	private String titleOfPeakList = null;

	// sequence qualifier
	private String sequenceQualifier = null;

	// composition qualifier
	private String compositionQualifier = null;

	/*
	 * The following are file-level meta data
	 */
	private String accession = null;

	private String charge = null;

	private String enzyme = null;

	private String searchTitle = null;

	private String database = null;

	private String format = null;

	private String icat = null;

	private String ionSeries = null;

	private String ionTolerance = null;

	private String unitOfIonTolerance = null;

	private String mass = null;

	private String fixedMods = null;

	private String variableMods = null;

	private String reportOverview = null;

	private String precursor = null;

	private String maxHits = null;

	private String reportType = null;

	private String search = null;

	private String proteinMass = null;

	private String taxonomy = null;

	private String userMail = null;

	private String userName = null;

	private int errorTolerance = -1;

	private int partials = -1;

	// End of file-level meta data

	// Flag to indicate the current line processed is within BEGIN and END IONS
	private boolean insidePeakList = false;

	// Flag set when file-level meta data has been parsed
	private boolean metaDataFound = false;

	// Flag set after file-level meta data has been stored in metaData object
	private boolean metaDataSet = false;

	// Object to store mgf file-level meta data
	private MascotGenericFormatMetaData metaData = null;

	// buffered input for the peak list
	BufferedReader in;

	// the next peak in the current peak list
	Peak next;

	// flag for if we're at the first peak of a new peak list
	boolean startOfPeakList = true;

	//Embedded PeakListReader, instantiated if FORMAT other than mgf exists
	private PeakListReader embeddedReader = null;

	/**
	 * @see org.proteomecommons.io.PeakListReader#hasNext()
	 */
	public boolean hasNext() {
		// check if the buffer has been filled
		if (next != null) {
			return true;
		}
		// try to fill the buffer
		try {
			Peak temp = next();
			if (temp == null) {
				return false;
			}
			next = temp;
		} catch (Exception e) {
			// noop
		}
		return next != null;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
	 */
	public boolean isStartOfPeakList() {
		//		if (embeddedReader != null) {
		//			return embeddedReader.isStartOfPeakList();
		//		}
		//		System.out.println("Is start of peak list: "+startOfPeakList);

		return startOfPeakList;
	}

	/**
	 * @return true if File-Level MetaData has been found in .mgf file. false
	 *         otherwise
	 */
	public boolean getMetaDataFound() {
		return metaDataFound;
	}

	/**
	 * @return MascotGenericFormatMetaData object if file-level meta data has
	 *         been parsed. null otherwise
	 */
	public MascotGenericFormatMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#close()
	 */
	public void close() {
		try {
			in.close();
		} catch (Exception e) {
			//  noop
		} finally {
			in = null;
		}
	}

	/**
	 * Sets MascotGenericFormatMetaData object with the parsed file-level meta
	 * data.Runs only once per file processing.
	 */
	private void setMetaData() {
		if (metaDataFound && !metaDataSet) {
			metaData = new MascotGenericFormatMetaData();

			metaData.setAccession(accession);
			metaData.setPeptideCharge(charge);
			metaData.setEnzyme(enzyme);
			metaData.setSearchTitle(searchTitle);
			metaData.setDatabase(database);
			metaData.setErrorTolerance(errorTolerance);
			metaData.setFormat(format);
			metaData.setIcat(icat);
			metaData.setIonSeries(ionSeries);
			metaData.setIonTolerance(ionTolerance);
			metaData.setUnitOfIonTolerance(unitOfIonTolerance);
			metaData.setMass(mass);
			metaData.setFixedMods(fixedMods);
			metaData.setVariableMods(variableMods);
			metaData.setReportOverview(reportOverview);
			metaData.setPartials(partials);
			metaData.setPrecursor(precursor);
			metaData.setMaxHits(maxHits);
			metaData.setReportType(reportType);
			metaData.setSearch(search);
			metaData.setProteinMass(proteinMass);
			metaData.setTaxonomy(taxonomy);
			metaData.setUserMail(userMail);
			metaData.setUserName(userName);
			metaDataSet = true;
		}

	}

	/**
	 * Returns a MascotGenericFormatPeakList object with an array of Peak
	 * objects that are sorted and normalized. This method does load everything
	 * in to memory, and it is not well-suited for extraordinarily large
	 * peaklist files. Additional peaklist-level meta data are set with the
	 * processed meta data
	 * 
	 * @param sort
	 *            true if the peak list should be sorted, false if not
	 * @param normailze
	 *            true if the peak list intensity values should be normalized
	 *            between 0 and 1.
	 */

	//	public PeakList getPeakList(boolean sort, boolean normalize)
	//			throws Exception {
	//
	//		// optionally use the embedded reader
	//		if (embeddedReader != null) {
	//			return embeddedReader.getPeakList(sort, normalize);
	//		}
	//
	//		// load the peaks in to memory
	//		LinkedList temp = new LinkedList();
	//
	//		/*
	//		 * isStartOfPeakList returns true even for the very first PeakList, when
	//		 * its first Peak is read. This is a change from the existing code,
	//		 * which returned true only for the subsequent new PeakLists. I made
	//		 * this change to make it consistent and for in-memory handling. - Ram
	//		 */
	//		for (Peak p = next(); p != null; p = next()) {
	//			temp.add(p);
	//		}
	//
	//		/*
	//		 * getPeakList used to return a valid PeakList object with no Peaks even
	//		 * when EOF is read. This used to cause run-away readers. I changed to
	//		 * return PeakList object only if Peaks are found. - Ram
	//		 */
	//		MascotGenericFormatPeakList peaklist = null;
	//		if (temp.size() > 0) {
	//			// make a new peaklist
	//			peaklist = new MascotGenericFormatPeakList(this);
	//			// set parent ion mass
	//			peaklist.parentIonCharge = parentIonCharge;
	//			// set parent mass over charge in daltons
	//			peaklist.parentIonMassOverChargeInDaltons =
	// parentIonMassOverChargeInDaltons;
	//			// use the same name
	//			peaklist.name = name;
	//			// convert to an array of peaks
	//			peaklist.peaks = (Peak[]) temp.toArray(new Peak[0]);
	//
	//			// by default normalize
	//			if (normalize) {
	//				peaklist.normalizePeaks();
	//			}
	//
	//			// by default sort
	//			if (sort) {
	//				peaklist.sortPeaks();
	//			}
	//		}
	//		if (peaklist != null) {
	//			peaklist.setTolerance(tolerance);
	//			peaklist.setUnitOfTolerance(unitOfTolerance);
	//			peaklist.setSequenceQualifier(sequenceQualifier);
	//			peaklist.setCompositionQualifier(compositionQualifier);
	//			peaklist.setTitleOfPeakList(titleOfPeakList);
	//			return (PeakList) peaklist;
	//		}
	//		return null;
	//	}
	/**
	 * Make a new PeakListReader designed to read Mascot Generic Format peak
	 * list files and point the reader to the file with the specified name.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public MascotGenericFormatPeakListReader(String fileName)
			throws IOException {
		this(new FileInputStream(fileName));

		setName(fileName);
	}

	/**
	 * Make a new PeakListReader designed to read Mascot Generic Format peak
	 * list files and point the reader to the java.io.InputStream with the
	 * specified name.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public MascotGenericFormatPeakListReader(InputStream is) {
		super(is);

		// load the file reference
		this.in = new BufferedReader(new InputStreamReader(getInputStream()));

		// parse meta-info and the first peak
		// No parsing when object is created. All parsing is done on demand.
		// next method will make sure meta data is parsed before looking for
		// Peaks.
		try {
			parseMetaInfo();
		} catch (Exception e) {
			// noop
		}
	}

	/**
	 * Helper method to parse meta-info. TODO: handle all the other forms of MGF
	 * meta-info.
	 * 
	 * File-level meta data and peaklist-level meta data are parsed and set in
	 * objects
	 * 
	 * @return true if there is a peak list, false if not.
	 * @throws IOException
	 */
	private String parseMetaInfo() throws IOException {
		// make a new parent peak
		setParent(new GenericPeak());
		// default to MS
		setTandemCount(1);
		
		// handle meta-information
		String string = null;
		while ((string = in.readLine()) != null) {
			// make sure lines with a single white space are recognized as an
			// empty line
			string = string.trim();

			// try to find parent mass info
			if (string.startsWith("PEPMASS")) {
				// flag as tandem
				setTandemCount(2);
				String parentMass = string.substring(string.indexOf("=") + 1);
				// allow for spaces to delimit gaps
				if (parentMass.indexOf(" ") != -1) {
					parentMass = parentMass.substring(0, parentMass
							.indexOf(" "));
				}
				// allow for tabs to delimit gaps
				else if (parentMass.indexOf("\t") != -1) {
					parentMass = parentMass.substring(0, parentMass
							.indexOf("\t"));
				}

				// set the parent ion mass
				((GenericPeak)getParent()).setMassOverCharge(Double
						.parseDouble(parentMass));
			}
			// try to handle charge
			else if (string.startsWith("CHARGE")) {
				// flag as tandem
				setTandemCount(2);
				if (insidePeakList) {
					String charge = string.substring(string.indexOf("=") + 1,
							string.indexOf("=") + 2);
					((GenericPeak)getParent()).setCharge(Integer.parseInt(charge));
				} else {
					charge = string.substring(string.indexOf("=") + 1, string
							.length());
				}
			} else if (string.startsWith("SEQ")) {
				sequenceQualifier = string.substring(string.indexOf("=") + 1,
						string.length());
			} else if (string.startsWith("COMP")) {
				compositionQualifier = string.substring(
						string.indexOf("=") + 1, string.length());
			} else if (string.startsWith("TITLE")) {
				titleOfPeakList = string.substring(string.indexOf("=") + 1,
						string.length());
			} else if (string.startsWith("TOLU")) {
				unitOfTolerance = string.substring(string.indexOf("=") + 1,
						string.length());
			} else if (string.startsWith("TOL")) {
				String tol = string.substring(string.indexOf("=") + 1, string
						.length());
				this.tolerance = Integer.parseInt(tol);

			} else if (string.startsWith("ACCESSION")) {
				accession = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("CLE")) {
				enzyme = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("COM")) {
				searchTitle = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("DB")) {
				database = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("ERRORTOLERANT")) {
				String st = string.substring(string.indexOf("=") + 1, string
						.length());
				errorTolerance = Integer.parseInt(st);
				metaDataFound = true;
			} else if (string.startsWith("FORMAT")) {
				format = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("ICAT")) {
				icat = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("INSTRUMENT")) {
				ionSeries = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("ITOLU")) {
				unitOfIonTolerance = string.substring(string.indexOf("=") + 1,
						string.length());
				metaDataFound = true;
			} else if (string.startsWith("ITOL")) {
				ionTolerance = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("MASS")) {
				mass = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("MODS")) {
				fixedMods = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("IT_MODS")) {
				variableMods = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("OVERVIEW")) {
				reportOverview = string.substring(string.indexOf("=") + 1,
						string.length());
				metaDataFound = true;
			} else if (string.startsWith("PFA")) {
				String st = string.substring(string.indexOf("=") + 1, string
						.length());
				partials = Integer.parseInt(st);
				metaDataFound = true;

			} else if (string.startsWith("PRECURSOR")) {
				precursor = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("REPORT")) {
				maxHits = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("REPTYPE")) {
				reportType = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("SEARCH")) {
				search = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("SEG")) {
				proteinMass = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("TAXONOMY")) {
				taxonomy = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("USEREMAIL")) {
				userMail = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("USERNAME")) {
				userName = string.substring(string.indexOf("=") + 1, string
						.length());
				metaDataFound = true;
			} else if (string.startsWith("BEGIN IONS")) {
				insidePeakList = true;
				startOfPeakList = true;
				//				if (startOfPeakList){
				//					setMetaData();
				//					}
			}
			// return numbers
			else {
				// skip blanks
				if (string.equals("")) {
					continue;
				}
				// skip comments
				if (string.startsWith("#")) {
					continue;
				}
				// check for numbers
				char c = string.charAt(0);
				if (c >= '0' && c <= '9') {
					// flag start of ions
					try {
						next = next(string);
						startOfPeakList = true;
					} catch (Exception e) {
						// noop
					}
					return string;
				}
			}
		}

		// if the meta-info isn't found, return null
		return null;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getTolerance()
	 */
	public int getTolerance() {
		return tolerance;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getUnitOfTolerance()
	 */
	public String getUnitOfTolerance() {
		return unitOfTolerance;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getTitleOfPeakList()
	 */
	public String getTitleOfPeakList() {
		return titleOfPeakList;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getSequenceQualifier()
	 */
	public String getSequenceQualifier() {
		return sequenceQualifier;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#getCompositionQualifier()
	 */
	public String getCompositionQualifier() {
		return compositionQualifier;
	}

	/**
	 * @see org.proteomecommons.io.PeakListReader#next()
	 */
	public Peak next() {
		// flag off the start of the peak list
		startOfPeakList = false;

		// try to use the cache.
		if (next != null) {
			Peak temp = next;
			next = null;
			return temp;
		}

		//		// use the embedded reader if appropriate
		//		if (embeddedReader != null) {
		//			return embeddedReader.next();
		//		}

		// fall back on the basic MGF reader
		try {
			return next(null);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * helper to parse peaks If not inside PeakList parsing, calls parseMetaInfo
	 * to parse file-level and/or peaklist-level meta info. On EOF, returns null
	 * and resets start of next PeakList flag. parses line for Peak values and
	 * return Peak objects. On reaching END IONS, marks flags to indicate
	 * potential start of next PeakList and end of processing of this PeakList.
	 * Note that meta data parsing is not done anywhere else. It's all run from
	 * this function.
	 */
	private Peak next(String line) throws Exception {
		// try to use the cache.
		if (next != null) {
			Peak temp = next;
			next = null;
			//			// fit the next line
			// 			next = next(line);
			return temp;
		}

		//		if (!insidePeakList) {
		//			if (format != null) {
		//				if (format.endsWith("Sequest")) {
		//					embeddedReader = new SequestDTAPeakListReader(name);
		//					return embeddedReader.next();
		//				}
		//				if (format.endsWith("Micromass")) {
		//					embeddedReader = new MicromassPKLPeakListReader(name);
		//					return embeddedReader.next();
		//				}
		//			}
		//		}

		// kill deadspace/comments
		if (line == null) {
			boolean nonEmptyLine = false;
			while (nonEmptyLine == false) {
				line = in.readLine();
				// check for end of ions
				if (line == null) {
					return null;
				}
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				nonEmptyLine = true;
			}
		}

		// check for the end of the ions
		if (line.startsWith("END IONS")) {
			insidePeakList = false;
			parseMetaInfo();
			if (next != null) {
				startOfPeakList = true;
			} else {
				startOfPeakList = false;
			}
			return null;

		}
		// process ions
		String[] split = line.split("\\s+");

		//  set the first entry as parent ion mass and the associated charge
		double massOverChargeInDaltons;
		double intens;
		try {
			massOverChargeInDaltons = Double.parseDouble(split[0]);
			intens = Double.parseDouble(split[1]);
		} catch (Exception e) { // Any non-Peak data will return null (e.g. END
			// IONS)
			return null;
		}
		// return the appropriate fragment
		MGFPeak gp = new MGFPeak();
		gp.setMassOverCharge(massOverChargeInDaltons);
		gp.setIntensity(intens);
		return gp;
	}
}
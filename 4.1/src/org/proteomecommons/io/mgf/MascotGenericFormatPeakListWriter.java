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

import java.io.BufferedWriter;
import java.io.OutputStream;

import org.proteomecommons.io.*;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * A simple writer class that can serialize peak list files in MGF format,
 * optionally saving the meta-information.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class MascotGenericFormatPeakListWriter extends GenericPeakListWriter {
	OutputStream out;

	// Flag to indicate if the current line is between "BEGIN IONS" and
	// "END IONS"
	private boolean insidePeakList = false;

	// reference the meta data
	MascotGenericFormatMetaData metaData = null;

	public MascotGenericFormatPeakListWriter(OutputStream out) {
		super(out);
	}

	public void setParentIonIntensity(double intensity) {
		// noop
	}

	/**
	 * Method to write a MGF file-level meta data .
	 * 
	 * @param MascotGenericFormatMetaData
	 *            object
	 */
	public void write(MascotGenericFormatMetaData metaData) throws Exception {
		BufferedWriter osw = getBufferedWriter();
		if (metaData != null) {
			String s;
			int i;
			if ((s = metaData.getAccession()) != null) {
				osw.write("ACCESSION=" + s + "\n");
			}
			if ((s = metaData.getPeptideCharge()) != null) {
				osw.write("CHARGE=" + s + "\n");
			}
			if ((s = metaData.getEnzyme()) != null) {
				osw.write("CLE=" + s + "\n");
			}
			if ((s = metaData.getSearchTitle()) != null) {
				osw.write("COM=" + s + "\n");
			}
			if ((s = metaData.getDatabase()) != null) {
				osw.write("DB=" + s + "\n");
			}
			if ((i = metaData.getErrorTolerance()) >= 0) {
				osw.write("ERRORTOLERANT=" + i + "\n");
			}
			if ((i = metaData.getPartials()) >= 0) {
				osw.write("PFA=" + i + "\n");
			}
			if ((s = metaData.getFormat()) != null) {
				osw.write("FORMAT=" + s + "\n");
			}
			if ((s = metaData.getIcat()) != null) {
				osw.write("ICAT=" + s + "\n");
			}
			if ((s = metaData.getIonSeries()) != null) {
				osw.write("INSTRUMENT=" + s + "\n");
			}
			if ((s = metaData.getIonTolerance()) != null) {
				osw.write("ITOL=" + s + "\n");
			}
			if ((s = metaData.getUnitOfIonTolerance()) != null) {
				osw.write("ITOLU=" + s + "\n");
			}
			if ((s = metaData.getMass()) != null) {
				osw.write("MASS=" + s + "\n");
			}
			if ((s = metaData.getFixedMods()) != null) {
				osw.write("MODS=" + s + "\n");
			}
			if ((s = metaData.getVariableMods()) != null) {
				osw.write("IT_MODS=" + s + "\n");
			}
			if ((s = metaData.getReportOverview()) != null) {
				osw.write("OVERVIEW=" + s + "\n");
			}
			if ((s = metaData.getPrecursor()) != null) {
				osw.write("PRECURSOR=" + s + "\n");
			}
			if ((s = metaData.getMaxHits()) != null) {
				osw.write("REPORT=" + s + "\n");
			}
			if ((s = metaData.getReportType()) != null) {
				osw.write("REPTYPE=" + s + "\n");
			}
			if ((s = metaData.getSearch()) != null) {
				osw.write("SEARCH=" + s + "\n");
			}
			if ((s = metaData.getProteinMass()) != null) {
				osw.write("SEG=" + s + "\n");
			}
			if ((s = metaData.getTaxonomy()) != null) {
				osw.write("TAXONOMY=" + s + "\n");
			}
			if ((s = metaData.getUserMail()) != null) {
				osw.write("USEREMAIL=" + s + "\n");
			}
			if ((s = metaData.getUserName()) != null) {
				osw.write("USERNAME=" + s + "\n");
			}
			osw.flush();
		}
	}

	/**
	 * Simple method to write a MGF file from a peak list. Added meta-data at
	 * peaklist level (title, Tol, TOlU, SEQ and COMP)
	 * 
	 * @param peaklist
	 */
	public void write(PeakList peaklist) throws CheckedIOException {
		// if null, return
		if (peaklist == null){
			return;
		}
		BufferedWriter osw = getBufferedWriter();
		// if this is an MGF peak list, write the meta-info
		if (peaklist instanceof MascotGenericFormatPeakList) {
			// write the meta info
			MascotGenericFormatPeakList mgf = (MascotGenericFormatPeakList) peaklist;

			try {
				//TODO: fix meta-info write, embed it in the MGFPeakList object
				// itself!
				this.write(mgf.getMetaData());
			} catch (Exception e) {
				throw new CheckedIOException("Couldn't write meta data.");
			}
		}

		try {
			// write the headers
			osw.write("\nBEGIN IONS\n");
			String s;
			int i;
			// if it is a tandem peak list, spell it out
			if (peaklist instanceof TandemPeakList) {
				TandemPeakList tandem = (TandemPeakList) peaklist;
				Peak p = (Peak) tandem.getParent();
				// write the parent mass
				osw.write("PEPMASS=" + p.getMassOverCharge() + "\n");

				// if charged
				if (p instanceof ChargeAssignedPeak) {
					ChargeAssignedPeak cap = (ChargeAssignedPeak) p;
					if (cap.getCharge() >= 0) {
						osw
								.write("CHARGE=" + Math.abs(cap.getCharge())
										+ "+\n");
					} else {
						osw
								.write("CHARGE=" + Math.abs(cap.getCharge())
										+ "-\n");
					}
				}
			}
			//		if (peaklist instanceof MascotGenericFormatPeakList) {
			//			MascotGenericFormatPeakList peaklist =
			// (MascotGenericFormatPeakList)
			// peaklist1;
			//			if ((s = peaklist.getTitleOfPeakList()) != null) {
			//				osw.write("TITLE=" + s + "\n");
			//			}
			//			if ((i = peaklist.getTolerance()) >= 0) {
			//				osw.write("TOL=" + i + "\n");
			//			}
			//			if ((s = peaklist.getUnitOfTolerance()) != null) {
			//				osw.write("TOLU=" + s + "\n");
			//			}
			//			if ((s = peaklist.getSequenceQualifier()) != null) {
			//				osw.write("SEQ=" + s + "\n");
			//			}
			//			if ((s = peaklist.getCompositionQualifier()) != null) {
			//				osw.write("COMP=" + s + "\n");
			//			}
			//		}
			// write out the peaks
			Peak[] peaks = peaklist.getPeaks();
			for (i = 0; i < peaks.length; i++) {
				osw.write(peaks[i].getMassOverCharge() + "\t"
						+ peaks[i].getIntensity() + "\n");
			}

			// end the ions
			osw.write("END IONS\n");
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#finish() Marks end of
	 *      peak-list in in-memory processing. Resets the flag that indicates
	 *      boundaries of peaklist
	 */
	public void close() throws CheckedIOException {
		// flush the writer
		BufferedWriter osw = getBufferedWriter();
		// if
		if (insidePeakList) {
			try {
				osw.write("END IONS\n");
				insidePeakList = false;
			} catch (Exception e) {
				throw new CheckedIOException("Can't use underlying IO stream.");
			}
		}
		super.close();
	}

	/**
	 * Writes END IONS if a peaklist was processed earlier. And then starts the
	 * next peak list with BEGIN IONS. This function is retained for backward
	 * compatibility. The next function with other meta info needs to be used.
	 * 
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double)
	 */
	public void startPeakList() throws CheckedIOException {
		try {
			BufferedWriter osw = getBufferedWriter();
			if (insidePeakList) {
				osw.write("END IONS\n");
			}
			insidePeakList = true;
			osw.write("\nBEGIN IONS\n");
		} catch (Exception e) {
			throw new CheckedIOException("Can't use underlying IO stream!");
		}

	}

	public void setParentIonCharge(int charge) {
		try {
			BufferedWriter osw = getBufferedWriter();
			// serialize charge info
			if (charge >= 0) {
				osw.write("CHARGE=" + Math.abs(charge) + "+\n");
			} else {
				osw.write("CHARGE=" + Math.abs(charge) + "-\n");
			}
		} catch (Exception e) {
			// noop
		}
	}

	public void setParentIonMassOverCharge(double mz) {
		try {
			BufferedWriter osw = getBufferedWriter();
			osw.write("PEPMASS=" + mz + "\n");
		} catch (Exception e) {
			// noop
		}
	}

	//	/**
	//	 * Writes END IONS if a peaklist was processed earlier. And then starts
	// the
	//	 * next peak list with BEGIN IONS. Writes peaklist-level meta data if
	//	 * present
	//	 */
	//	public void startPeakList(double parentIonMassOverChargeInDaltons,
	//			int parentIonCharge, int tolerance, String unitOfTolerance,
	//			String titleOfPeakList, String sequenceQualifier,
	//			String compositionQualifier) throws IOException {
	//		if (osw == null) {
	//			osw = new OutputStreamWriter(out);
	//		}
	//		if (insidePeakList) {
	//			osw.write("END IONS\n");
	//		}
	//		insidePeakList = true;
	//		osw.write("\nBEGIN IONS\n");
	//		if (titleOfPeakList != null) {
	//			osw.write("TITLE=" + titleOfPeakList + "\n");
	//		}
	//		if (parentIonMassOverChargeInDaltons > 0) {
	//			osw.write("PEPMASS=" + parentIonMassOverChargeInDaltons + "\n");
	//		}
	//
	//		if (parentIonCharge >= 0) {
	//			osw.write("CHARGE=" + parentIonCharge + "+\n");
	//		} else {
	//			osw.write("CHARGE=" + parentIonCharge + "-\n");
	//		}
	//		if (tolerance >= 0) {
	//			osw.write("TOL=" + tolerance + "\n");
	//		}
	//		if (unitOfTolerance != null) {
	//			osw.write("TOLU=" + unitOfTolerance + "\n");
	//		}
	//		if (sequenceQualifier != null) {
	//			osw.write("SEQ=" + sequenceQualifier + "\n");
	//		}
	//		if (compositionQualifier != null) {
	//			osw.write("COMP=" + compositionQualifier + "\n");
	//		}
	//
	//	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 *      Writes values of the Peak passed
	 */
	public void write(Peak peak) throws CheckedIOException {
		try {
			BufferedWriter osw = getBufferedWriter();
			osw.write(peak.getMassOverCharge() + "\t" + peak.getIntensity()
					+ "\n");
		} catch (Exception e) {
			throw new CheckedIOException("Can't use underlying IO stream.");
		}
	}
}
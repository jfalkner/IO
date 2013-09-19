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
 * A simple writer class that can serialize peak list files in MGF format,
 * optionally saving the meta-information.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public class MascotGenericFormatPeakListWriter extends PeakListWriter {
	OutputStream out;

	// Flag to indicate if the current line is between "BEGIN IONS" and
	// "END IONS"
	private boolean insidePeakList = false;

	// OutputStreamWriter object at object-level. Avoids having to create
	// this object in every function call. Improves performance, particularly
	// in in-memory processing
	private OutputStreamWriter osw = null;

	public MascotGenericFormatPeakListWriter(OutputStream out) {
		this.out = out;
	}

	/**
	 * Method to write a MGF file-level meta data .
	 * 
	 * @param MascotGenericFormatMetaData
	 *            object
	 */
	public void write(MascotGenericFormatMetaData metaData) throws Exception {
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
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
	public void write(PeakList peaklist1) throws Exception {
		if (osw == null) {
			osw = new OutputStreamWriter(out);
			// if this is an MGF peak list, write the meta-info
			if (peaklist1 instanceof MascotGenericFormatPeakList) {
				// write the meta info
				MascotGenericFormatPeakList mgf = (MascotGenericFormatPeakList)peaklist1;
//TODO: fix meta-info write, embed it in the MGFPeakList object itself!
				//				this.write(((MascotGenericFormatPeakListReader)peaklist1.reader).getMetaData());
			}
		}
		// write the headers
		osw.write("\nBEGIN IONS\n");
		String s;
		int i;
		if (peaklist1.parentIonMassOverChargeInDaltons > 0) {
			osw.write("PEPMASS=" + peaklist1.parentIonMassOverChargeInDaltons
					+ "\n");
		}
		if (peaklist1.parentIonCharge >= 0) {
			osw.write("CHARGE=" + peaklist1.parentIonCharge + "+\n");
		} else {
			osw.write("CHARGE=" + peaklist1.parentIonCharge + "-\n");
		}
		if (peaklist1 instanceof MascotGenericFormatPeakList) {
			MascotGenericFormatPeakList peaklist = (MascotGenericFormatPeakList) peaklist1;
			if ((s = peaklist.getTitleOfPeakList()) != null) {
				osw.write("TITLE=" + s + "\n");
			}
			if ((i = peaklist.getTolerance()) >= 0) {
				osw.write("TOL=" + i + "\n");
			}
			if ((s = peaklist.getUnitOfTolerance()) != null) {
				osw.write("TOLU=" + s + "\n");
			}
			if ((s = peaklist.getSequenceQualifier()) != null) {
				osw.write("SEQ=" + s + "\n");
			}
			if ((s = peaklist.getCompositionQualifier()) != null) {
				osw.write("COMP=" + s + "\n");
			}
		}
		// write out the peaks
		for (i = 0; i < peaklist1.peaks.length; i++) {
			osw.write(peaklist1.peaks[i].massOverChargeInDaltons + "\t"
					+ peaklist1.peaks[i].intensity + "\n");
		}

		// end the ions
		osw.write("END IONS\n");
		osw.flush();
	}

	/**
	 * \
	 * 
	 * @see org.proteomecommons.io.PeakListWriter#finish() Marks end of
	 *      peak-list in in-memory processing. Resets the flag that indicates
	 *      boundaries of peaklist
	 */
	public void finish() throws IOException {
		// TODO Auto-generated method stub
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		if (insidePeakList) {
			osw.write("END IONS\n");
			insidePeakList = false;
		}
		osw.close();
	}

	/**
	 *      Writes END IONS if a peaklist was processed earlier. And then starts
	 *      the next peak list with BEGIN IONS. This function is retained for
	 *      backward compatibility. The next function with other meta info needs
	 *      to be used.
	 *  @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double)
	 */
	public void startPeakList(double parentIonMassOverChargeInDaltons,
			double charge) throws IOException {
		// if the writer is null, make a new one
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		if (insidePeakList) {
			osw.write("END IONS\n");
		}
		insidePeakList = true;
		osw.write("\nBEGIN IONS\n");
		if (parentIonMassOverChargeInDaltons > 0) {
			osw.write("PEPMASS=" + parentIonMassOverChargeInDaltons + "\n");
		}

		// serialize charge info
		if (charge >= 0) {
			osw.write("CHARGE=" + (int)charge + "+\n");
		} else {
			osw.write("CHARGE=" + (int)charge + "-\n");
		}
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#startPeakList(double, double,
	 *      double)
	 */
	public void startPeakList(double parentIonMassOverChargeInDaltons,
			double intensity, double charge) throws IOException {
		startPeakList(parentIonMassOverChargeInDaltons, charge);

	}

	/**
	 * Writes END IONS if a peaklist was processed earlier. And then starts the
	 * next peak list with BEGIN IONS. Writes peaklist-level meta data if
	 * present
	 */
	public void startPeakList(double parentIonMassOverChargeInDaltons,
			int parentIonCharge, int tolerance, String unitOfTolerance,
			String titleOfPeakList, String sequenceQualifier,
			String compositionQualifier) throws IOException {
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		if (insidePeakList) {
			osw.write("END IONS\n");
		}
		insidePeakList = true;
		osw.write("\nBEGIN IONS\n");
		if (titleOfPeakList != null) {
			osw.write("TITLE=" + titleOfPeakList + "\n");
		}
		if (parentIonMassOverChargeInDaltons > 0) {
			osw.write("PEPMASS=" + parentIonMassOverChargeInDaltons + "\n");
		}

		if (parentIonCharge >= 0) {
			osw.write("CHARGE=" + parentIonCharge + "+\n");
		} else {
			osw.write("CHARGE=" + parentIonCharge + "-\n");
		}
		if (tolerance >= 0) {
			osw.write("TOL=" + tolerance + "\n");
		}
		if (unitOfTolerance != null) {
			osw.write("TOLU=" + unitOfTolerance + "\n");
		}
		if (sequenceQualifier != null) {
			osw.write("SEQ=" + sequenceQualifier + "\n");
		}
		if (compositionQualifier != null) {
			osw.write("COMP=" + compositionQualifier + "\n");
		}

	}

	/**
	 * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
	 *      Writes values of the Peak passed
	 */
	public void write(Peak peak) throws IOException {
		// throw the error
		//		throw new IOException("Stream support not implemented for MGF
		// writer!");
		if (osw == null) {
			osw = new OutputStreamWriter(out);
		}
		osw.write(peak.massOverChargeInDaltons + "\t" + peak.intensity + "\n");
	}
}
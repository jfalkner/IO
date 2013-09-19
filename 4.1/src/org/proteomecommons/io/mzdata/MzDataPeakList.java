/*
 * Created on Jun 5, 2005
 *
 */
package org.proteomecommons.io.mzdata;

import org.proteomecommons.io.GenericPeakList;

/**
 * In addition to peak values, an MzDataPeakList encapsulates the metadata 
 * specific to each spectrum in an MzData spectrumList tag.  Specifically, 
 * this includes spectrumSettings and precursorList information.
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MzDataPeakList extends GenericPeakList {
	private MzDataPeakListSpectrumSettings spectrumSettings = null;
	private MzDataPeakListPrecursorList precursorList = null;
	/**
	 * List and descriptions of precursors to the spectrum currently being described.
	 *
	 */
	public void setPrecursorList(MzDataPeakListPrecursorList list){
		precursorList = list;
	}
	
	/**
	 * 
	 * @return the precursorList for this spectrum.
	 */
	public MzDataPeakListPrecursorList getPrecursorList(){
		return precursorList;
	}
	
	/**
	 * Both run time instrument settings and variations in software parameters all leading to the generation of the specific spectrum being described.
	 *
	 */
	public void setSpectrumSettings(MzDataPeakListSpectrumSettings settings){
		spectrumSettings = settings;
	}
	
	/**
	 * Spectrum settings for this peaklist.
	 * @return relevant spectrum settings information, or null if this spectrum does not have spectrum settings specified.
	 */
	public MzDataPeakListSpectrumSettings getSpectrumSettings(){
		return spectrumSettings;
	}
}

/*
 * Created on Jun 5, 2005
 *
 */
package org.proteomecommons.io.mzdata;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of precursorListItems that specify the precursorList in 
 * each MzDataPeakList.  This should be specified along with an 
 * MzDataPeakListSpectrumSettings for each MzDataPeakList that is crated. 
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MzDataPeakListPrecursorList {
	private ArrayList precursors;
	
	/**
	 * Construct a precursorList that contains the specifed precursor
	 * @param precursor the first precursor in the spectrum.  If needed, use add for additional precursors.
	 */
	public MzDataPeakListPrecursorList(MzDataPeakListPrecursorListItem precursor){
		precursors = new ArrayList();
		precursors.add(precursor);
	}
	
	/**
	 * Add a precursor to the list of precursors.
	 * @param precursor an additional precursor to add to the list
	 */
	public void add(MzDataPeakListPrecursorListItem precursor){
		precursors.add(precursor);
	}
	
	/**
	 * All of the precursors for this Peak List.
	 * @return a list containing MzDataPeakListPrecursorListItems.
	 */
	public List getPrecursors(){
		return precursors;
	}
}

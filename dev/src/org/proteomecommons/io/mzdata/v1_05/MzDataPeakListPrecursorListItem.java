/*
 * Created on Jun 6, 2005
 *
 */
package org.proteomecommons.io.mzdata.v1_05;

import java.util.Map;

/**
 * Information about one item in an MzData precursorList.
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MzDataPeakListPrecursorListItem {

	private int msLevel;
	private int spectrumRef;
	private Map activation = null;
	private Map ionSelection = null;

	/**
	 * Create an item in the precursorList with the required attributes.
	 * @param msLevel of the precursor
	 * @param spectrumRef for the precursor
	 */
	public MzDataPeakListPrecursorListItem(int msLevel, int spectrumRef){
		this.msLevel = msLevel;
		this.spectrumRef = spectrumRef;
	}
	
	public int msLevel(){
		return msLevel;
	}
	
	public int spectrumRef(){
		return spectrumRef;
	}
	
	/**
	 * The type and energy level used for activation.
	 * @param activation name value pairs for the CvValue tags inside the activation tag
	 */
	public void setActivation(Map activation){
		this.activation = activation;
	}
	
	/**
	 * The type and energy level used for activation.
	 * @return name value pairs for the activaion CvTags, or null if unspecified.
	 */
	public Map getActivation(){
		return activation;
	}
	
	/**
	 * This captures the type of ion selection being performed, and trigger m/z (or m/z's), neutral loss criteria etc.
	 * @param ionSelection relevant name value pairs to describe the CvValue tags in ionSelection.
	 */
	public void setIonSelection(Map ionSelection){
		this.ionSelection = ionSelection;
	}
	
	/**
	 * Information about the type of ion selection performed.
	 * @return name value pairs for ionSelection CvTags, or null if unspecified.
	 */
	public Map getIonSelection(){
		return ionSelection;
	}
}

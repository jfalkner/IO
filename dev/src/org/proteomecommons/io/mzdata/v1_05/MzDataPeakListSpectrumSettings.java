/*
 * Created on Jun 5, 2005
 *
 */
package org.proteomecommons.io.mzdata.v1_05;

import java.util.Map;

/**
 * The spectrumSettings component of spectrumDesc in an mzData document.  
 * This should be specified along with a precursorList for each 
 * MzDataPeakList that is created. <br>
 * The instrument's 'run time' parameters; common to the whole of this spectrum.
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */

public class MzDataPeakListSpectrumSettings {

	//spectrumSettings variables
	private int msLevel;
	private float mzRangeStart;
	private float mzRangeStop;
	private boolean rangeSpecified = false;
	private Map spectrumInstrumentParams = null;
	
	//acqSpecification variables
	private String methodOfCombination = null;
	private String spectrumType = null;
	private int acqNumber;
	private boolean acqNumberSpecified = false;
	
	//info about precursors
	/*private MzDataPeakListPrecursorList precursors = null;'*/
	
	/**
	 * Create spectrumSettings for a spectrum. 
	 * @param msLevel required int specifying the ms level in spectrumInstrument
	 */
	public MzDataPeakListSpectrumSettings(int msLevel){
		this.msLevel = msLevel;
	}
	
	/**
	 * Create spectrumSettings for a spectrum with optional mz starting and stopping ranges.
	 * @param msLevel required int specifying the ms level.
	 * @param mzRangeStart start of the mz range
	 * @param mzRangeStop end of the mz range
	 */
	public MzDataPeakListSpectrumSettings(int msLevel, float mzRangeStart, float mzRangeStop){
		this(msLevel);
		this.mzRangeStart = mzRangeStart;
		this.mzRangeStop = mzRangeStop;
		rangeSpecified = true;
	}
	/**
	 * The ms level attribute for spectrumInstrument
	 * @return the ms level of this spectrum.
	 */
	public int msLevel(){
		return msLevel;
	}

	/**
	 * Set a new ms level for spectrumInstrument
	 * @param msLevel the new ms level
	 */
	public void setMsLevel(int msLevel){
		this.msLevel = msLevel;
	}
	/**
	 * Sets the range of mz values for spectrumInstrument
	 * @param start the start of the mz values
	 * @param end the end of the mz values
	 */
	public void setMzRange(float start, float end){
		mzRangeStart = start;
		mzRangeStop = end;
		rangeSpecified = true;
	}
	
	/**
	 * 
	 * @return the start of the mz values
	 */
	public float getMzRangeStart(){
		return mzRangeStart;
	}
	
	/**
	 * 
	 * @return the end of the mz values
	 */
	public float getMzRangeEnd(){
		return mzRangeStop;
	}
	
	/**
	 * Find out if this spectrumSettings has the mz range specified.
	 * @return true if values for mzRangeStart and mzRangeEnd have been specified.
	 */
	public boolean mzRangeIsSpecified(){
		return rangeSpecified;
	}
	
	/**
	 * Specify a map of name value pairs for the cvParams in spectrumInstrument
	 * @param params
	 */
	public void setSpectrumInstrumentParameters(Map params){
		spectrumInstrumentParams = params;
	}
	
	/**
	 * Get the name value pairs representing the cvParam tags in spectrumInstrument.
	 * @return relevant spectrumInstrument name value pairs, or null if no params exist.
	 */
	public Map getSpectrumInstrumentParamaters(){
		return spectrumInstrumentParams;
	}
	
	/**
	 * Set relevant paramaters for the acqSpecification tag
	 * @param methodOfCombination set the methodOfCombination property, often sum
	 * @param spectrumType set the spectrumType property, often discrete
	 */
	public void setAcqSpecification(String methodOfCombination, String spectrumType){
		this.methodOfCombination = methodOfCombination;
		this.spectrumType = spectrumType;
	}
	
	/**
	 * 
	 * @return the methodOfCombination attribute for acqSpecification, or null if the combination method is unspecified.
	 */
	public String methodOfCombination(){
		return methodOfCombination;
	}
	
	/**
	 * 
	 * @return the sepctrumType attribute for acqSpecification, or null if the spectrum type is unspecified.
	 */
	public String spectrumType(){
		return spectrumType;
	}
	
	/**
	 * 
	 * @param acqNum the acqNumber attribute of the acquisition tag within acqSpecification
	 */
	public void setAcqNumber(int acqNum){
		acqNumber = acqNum;
		acqNumberSpecified = true;
	}
	
	/**
	 * 
	 * @return true if acqNumber has been specified.
	 */
	public boolean acqNumberIsSpecified(){
		return acqNumberSpecified;
	}
	
	/**
	 * 
	 * @return the value of acqNumber for the acquisition tag.
	 */
	public int acqNumber(){
		return acqNumber;
	}
}

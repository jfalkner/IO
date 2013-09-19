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

import org.proteomecommons.io.*;

/**
 * A MGF-specific peak list class. This class carries meta-info that is specific
 * to the Mascot search engine.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MascotGenericFormatPeakList extends GenericPeakList implements TandemPeakList{
	// Peptide mass tolerance. Null if not known. (.mgf)
	private int tolerance = -1;

	// reference the meta data
	private MascotGenericFormatMetaData metaData = null;

	public MascotGenericFormatMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MascotGenericFormatMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Unit of tolerance. Null if not known(.mgf)
	 */
	private String unitOfTolerance = null;

	/**
	 * Title of peak list. Null if not known(.mgf)
	 */
	private String titleOfPeakList = null;

	/**
	 * Sequence qualifier. Null if not known(.mgf)
	 */
	private String sequenceQualifier = null;

	/**
	 * Composition qualifier. Null if not known(.mgf)
	 */
	private String compositionQualifier = null;

	/**
	 * Utility method to set title of the Peak List
	 * 
	 * @param Title
	 *            of the Peak List
	 */
	public void setTitleOfPeakList(String s) {
		titleOfPeakList = s;
	}

	/**
	 * Utility method to get title of the Peak List
	 * 
	 * @return Title of the Peak List
	 */
	public String getTitleOfPeakList() {
		return titleOfPeakList;
	}

	/**
	 * Utility method to set tolerance value for the peak list
	 * 
	 * @param Tolerance
	 *            of the Peak List
	 */
	public void setTolerance(int i) {
		tolerance = i;
	}

	/**
	 * Utility method to get tolerance value of the Peak List
	 * 
	 * @return Tolerance of the Peak List
	 */
	public int getTolerance() {
		return tolerance;
	}

	/**
	 * Utility method to set tolerance unit for the peak list
	 * 
	 * @param Tolerance
	 *            Unit of the Peak List
	 */
	public void setUnitOfTolerance(String s) {
		unitOfTolerance = s;
	}

	/**
	 * Utility method to get tolerance unit of the Peak List
	 * 
	 * @return Tolerance Unit of the Peak List
	 */
	public String getUnitOfTolerance() {
		return unitOfTolerance;
	}

	/**
	 * Utility method to set sequence qualifier for the peak list
	 * 
	 * @param Sequence
	 *            Qualifier of the Peak List
	 */
	public void setSequenceQualifier(String s) {
		sequenceQualifier = s;
	}

	/**
	 * Utility method to get sequence qualifier of the Peak List
	 * 
	 * @return Sequence Qualifier of the Peak List
	 */
	public String getSequenceQualifier() {
		return sequenceQualifier;
	}

	/**
	 * Utility method to set composition qualifier for the peak list
	 * 
	 * @param Composition
	 *            Qualifier of the Peak List
	 */
	public void setCompositionQualifier(String s) {
		compositionQualifier = s;
	}

	/**
	 * Utility method to get composition qualifier of the Peak List
	 * 
	 * @return Composition Qualifier of the Peak List
	 */
	public String getCompositionQualifier() {
		return compositionQualifier;
	}

}
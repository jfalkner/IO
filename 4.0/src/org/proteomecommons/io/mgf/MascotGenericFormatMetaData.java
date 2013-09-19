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
import java.io.*;

/**
 * Contains Meta Data of Mascot Generic Format file (.mgf). These are file-level
 * meta data. Note that meta data within PeakLists in .mgf files are stored in
 * PeakList instances.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */

public class MascotGenericFormatMetaData {
	private String accession = null;

	private String peptideCharge = null;

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

	/**
	 * Utility method to set accession for the peak list
	 * 
	 * @param accession
	 *            of the Peak List
	 */
	public void setAccession(String s) {
		accession = s;
	}

	/**
	 * Utility method to get accession of the Peak List
	 * 
	 * @return accession of the Peak List
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * Utility method to set Peptide Charge for the peak list
	 * 
	 * @param Peptide
	 *            Charge of the Peak List
	 */
	public void setPeptideCharge(String s) {
		peptideCharge = s;
	}

	/**
	 * Utility method to get Peptide Charge of the Peak List
	 * 
	 * @return Peptide Charge of the Peak List
	 */
	public String getPeptideCharge() {
		return peptideCharge;
	}

	/**
	 * Utility method to set enzyme for the peak list
	 * 
	 * @param enzyme
	 *            of the Peak List
	 */
	public void setEnzyme(String s) {
		enzyme = s;
	}

	/**
	 * Utility method to get enzyme of the Peak List
	 * 
	 * @return enzyme of the Peak List
	 */
	public String getEnzyme() {
		return enzyme;
	}

	/**
	 * Utility method to set Search Title for the peak list
	 * 
	 * @param Search
	 *            Title of the Peak List
	 */
	public void setSearchTitle(String s) {
		searchTitle = s;
	}

	/**
	 * Utility method to get Search Title of the Peak List
	 * 
	 * @return Search Title of the Peak List
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Utility method to set Database for the peak list
	 * 
	 * @param Database
	 *            of the Peak List
	 */
	public void setDatabase(String s) {
		database = s;
	}

	/**
	 * Utility method to get Database of the Peak List
	 * 
	 * @return Database of the Peak List
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Utility method to set Format for the peak list
	 * 
	 * @param Format
	 *            of the Peak List
	 */
	public void setFormat(String s) {
		format = s;
	}

	/**
	 * Utility method to get Format of the Peak List
	 * 
	 * @return Format of the Peak List
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Utility method to set icat for the peak list
	 * 
	 * @param icat
	 *            of the Peak List
	 */
	public void setIcat(String s) {
		icat = s;
	}

	/**
	 * Utility method to get icat of the Peak List
	 * 
	 * @return icat of the Peak List
	 */
	public String getIcat() {
		return icat;
	}

	/**
	 * Utility method to set Ion Series for the peak list
	 * 
	 * @param Ion
	 *            Series of the Peak List
	 */
	public void setIonSeries(String s) {
		ionSeries = s;
	}

	/**
	 * Utility method to get Ion Series of the Peak List
	 * 
	 * @return Ion Series of the Peak List
	 */
	public String getIonSeries() {
		return ionSeries;
	}

	/**
	 * Utility method to set Ion Tolerance for the peak list
	 * 
	 * @param Ion
	 *            Tolerance of the Peak List
	 */
	public void setIonTolerance(String s) {
		ionTolerance = s;
	}

	/**
	 * Utility method to get Ion Tolerance of the Peak List
	 * 
	 * @return Ion Tolerance of the Peak List
	 */
	public String getIonTolerance() {
		return ionTolerance;
	}

	/**
	 * Utility method to set Ion tolerance unit for the peak list
	 * 
	 * @param Ion
	 *            Tolerance Unit of the Peak List
	 */
	public void setUnitOfIonTolerance(String s) {
		unitOfIonTolerance = s;
	}

	/**
	 * Utility method to get Ion tolerance unit of the Peak List
	 * 
	 * @return Ion Tolerance Unit of the Peak List
	 */
	public String getUnitOfIonTolerance() {
		return unitOfIonTolerance;
	}

	/**
	 * Utility method to set mass for the peak list
	 * 
	 * @param Mass
	 *            of the Peak List
	 */
	public void setMass(String s) {
		mass = s;
	}

	/**
	 * Utility method to get Mass of the Peak List
	 * 
	 * @return Mass of the Peak List
	 */
	public String getMass() {
		return mass;
	}

	/**
	 * Utility method to set Fixed Mods for the peak list
	 * 
	 * @param Fixed
	 *            Mods of the Peak List
	 */
	public void setFixedMods(String s) {
		fixedMods = s;
	}

	/**
	 * Utility method to get Fixed Mods of the Peak List
	 * 
	 * @return Fixed Mods of the Peak List
	 */
	public String getFixedMods() {
		return fixedMods;
	}

	/**
	 * Utility method to set Variable Mods for the peak list
	 * 
	 * @param Variable
	 *            Mods of the Peak List
	 */
	public void setVariableMods(String s) {
		variableMods = s;
	}

	/**
	 * Utility method to get Variable Mods of the Peak List
	 * 
	 * @return Variable Mods of the Peak List
	 */
	public String getVariableMods() {
		return variableMods;
	}

	/**
	 * Utility method to set Report Overview for the peak list
	 * 
	 * @param Report
	 *            Overview of the Peak List
	 */
	public void setReportOverview(String s) {
		reportOverview = s;
	}

	/**
	 * Utility method to get Report Overview of the Peak List
	 * 
	 * @return Report Overview of the Peak List
	 */
	public String getReportOverview() {
		return reportOverview;
	}

	/**
	 * Utility method to set Precursor for the peak list
	 * 
	 * @param Precursor
	 *            of the Peak List
	 */
	public void setPrecursor(String s) {
		precursor = s;
	}

	/**
	 * Utility method to get Precursor of the Peak List
	 * 
	 * @return Precursor of the Peak List
	 */
	public String getPrecursor() {
		return precursor;
	}

	/**
	 * Utility method to set Maximum Hits for the peak list
	 * 
	 * @param Maximum
	 *            Hits of the Peak List
	 */
	public void setMaxHits(String s) {
		maxHits = s;
	}

	/**
	 * Utility method to get Maximum Hits of the Peak List
	 * 
	 * @return Maximum Hits of the Peak List
	 */
	public String getMaxHits() {
		return maxHits;
	}

	/**
	 * Utility method to set Report Type for the peak list
	 * 
	 * @param Report
	 *            Type of the Peak List
	 */
	public void setReportType(String s) {
		reportType = s;
	}

	/**
	 * Utility method to get Report Type of the Peak List
	 * 
	 * @return Report Type of the Peak List
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * Utility method to set Search for the peak list
	 * 
	 * @param Search
	 *            of the Peak List
	 */
	public void setSearch(String s) {
		search = s;
	}

	/**
	 * Utility method to get Search of the Peak List
	 * 
	 * @return Search of the Peak List
	 */
	public String getSearch() {
		return search;
	}

	/**
	 * Utility method to set Protein Mass for the peak list
	 * 
	 * @param Protein
	 *            Mass of the Peak List
	 */
	public void setProteinMass(String s) {
		proteinMass = s;
	}

	/**
	 * Utility method to get Protein Mass of the Peak List
	 * 
	 * @return Protein Mass of the Peak List
	 */
	public String getProteinMass() {
		return proteinMass;
	}

	/**
	 * Utility method to set Taxonomy for the peak list
	 * 
	 * @param Taxonomy
	 *            of the Peak List
	 */
	public void setTaxonomy(String s) {
		taxonomy = s;
	}

	/**
	 * Utility method to get Taxonomy of the Peak List
	 * 
	 * @return Taxonomy of the Peak List
	 */
	public String getTaxonomy() {
		return taxonomy;
	}

	/**
	 * Utility method to set User Mail for the peak list
	 * 
	 * @param User
	 *            Mail of the Peak List
	 */
	public void setUserMail(String s) {
		userMail = s;
	}

	/**
	 * Utility method to get User Mail of the Peak List
	 * 
	 * @return User Mail of the Peak List
	 */
	public String getUserMail() {
		return userMail;
	}

	/**
	 * Utility method to set User Name for the peak list
	 * 
	 * @param User
	 *            Name of the Peak List
	 */
	public void setUserName(String s) {
		userName = s;
	}

	/**
	 * Utility method to get User Name of the Peak List
	 * 
	 * @return User Name of the Peak List
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Utility method to set Error Tolerance for the peak list
	 * 
	 * @param Error
	 *            Tolerance of the Peak List
	 */
	public void setErrorTolerance(int i) {
		errorTolerance = i;
	}

	/**
	 * Utility method to get Error Tolerance of the Peak List
	 * 
	 * @return Error Tolerance of the Peak List
	 */
	public int getErrorTolerance() {
		return errorTolerance;
	}

	/**
	 * Utility method to set Partials for the peak list
	 * 
	 * @param Partials
	 *            of the Peak List
	 */
	public void setPartials(int i) {
		partials = i;
	}

	/**
	 * Utility method to get Partials of the Peak List
	 * 
	 * @return Partials of the Peak List
	 */
	public int getPartials() {
		return partials;
	}

}
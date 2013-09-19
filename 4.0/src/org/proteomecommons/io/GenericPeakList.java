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
package org.proteomecommons.io;

import java.util.Arrays;
import org.proteomecommons.io.filter.*;

/**
 * Abstraction for a centroided but possibly not monoisotopic spectrum (i.e.
 * peak list). There isn't much to this class. If the parent ion charge is
 * known, its value is in parentIonCharge. If the parent ion's m/z is known, it
 * is in parentIonMassOverChargeInDaltons. The peaks can be obtained using the
 * getPeaks() method. Use the getMonoisotopic() method to determine if the peaks
 * represented by this peak list are monoisotopic or averaged.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GenericPeakList implements PeakList {
	private double intensity = Peak.UNKNOWN_INTENSITY;

	private int tandemCount = TandemPeakList.UNKNOWN_TANDEM_COUNT;

	private int averaged = MonoisotopicPeak.UNKNOWN_AVERAGED;

	/**
	 * The parent ion's m/z. PeakList.UNKNOWN if not known.
	 */
	private double parentIonMassOverChargeInDaltons = Peak.UNKNOWN_MZ;

	/**
	 * The parent ion's charge. PeakList.UNKNOWN if not known.
	 */
	private int parentIonCharge = MonoisotopicPeak.UNKNOWN_CHARGE;

	private PeakList parentPeakList = null;

	private Peak parent;

	/**
	 * The peaks in the peak list.
	 * 
	 * @see org.proteomecommons.io.PeakList
	 */
	private Peak[] peaks = null;

	public Peak[] getPeaks() {
		return peaks;
	}

	public void setPeaks(Peak[] peaks) {
		this.peaks = peaks;
	}

	/**
	 * An Arbitrary name for the peak list
	 */
	private String name = "Unnamed Peak List";

	/**
	 * Returns PeakList.AVERAGED if the centroided value was determined from
	 * averaging or PeakList.RESOLVED if the centroided mass represents a
	 * single, resolved peak.
	 * 
	 * @return
	 */
	public int getAveraged() {
		return averaged;
	}

	/**
	 * Sets the averaged flag.
	 * 
	 * @param averaged
	 */
	public void setAveraged(int averaged) {
		this.averaged = averaged;
	}

	/**
	 * Getter method for the parent ion's mass in daltons.
	 * 
	 * @return Parent ion's mass in daltons.
	 */
	public Peak getParent() {
		return parent;
	}

	public void setParent(Peak parent) {
		this.parent = parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the arbitrary name for this peak list. There is no garuntee that
	 * this name will be unique.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	//	/**
	//	 * Helper method to return the total ion count (TIC), i.e. the commonly
	//	 * displayed metric when determining what is eluting off an LC run.
	//	 *
	//	 * @return
	//	 */
	//	public double getTotalIonCount() {
	//		double tic = 0;
	//		for (int i = 0; i < peaks.length; i++) {
	//			tic += peaks[i].intensity;
	//		}
	//		return tic;
	//	}
	//
	//	/**
	//	 * Helper method to return the base ion count (BIC). The BIC is the
	//	 * intensity of the highest ion in this peak list, and it is a good way of
	//	 * determining if one is seeing signal versus noise. Noise is normally a
	//	 * bunch of weak ions.
	//	 *
	//	 * @return
	//	 */
	//	public double getBaseIonCount() {
	//		double bic = 0;
	//		for (int i = 0; i < peaks.length; i++) {
	//			if (peaks[i].intensity > bic) {
	//				bic = peaks[i].intensity;
	//			}
	//		}
	//		return bic;
	//	}

	/**
	 * Returns TandemPeakList.UNKNOWN_TANDEM_COUNT.
	 */
	public int getTandemCount() {
		return tandemCount;
	}

	public void setTandemCount(int tandemCount) {
		this.tandemCount = tandemCount;
	}

	/**
	 * Retuns null.
	 */
	public PeakList getParentPeakList() {
		return parentPeakList;
	}

	public void setParentPeakList(PeakList parentPeakList) {
		this.parentPeakList = parentPeakList;
	}

	/**
	 * Helper method to make a duplicate of the given TandemPeakList. The
	 * duplicate's values will be completely independent from the original's.
	 * 
	 * @param peaklist
	 */
	public static PeakList duplicate(PeakList peaklist) {
		GenericPeakList gpl = null;
		// if tandem dupe tandem
		if (peaklist instanceof TandemPeakList){
			gpl = new GenericTandemPeakList();
			TandemPeakList tpl = (TandemPeakList)peaklist;
			GenericTandemPeakList gtpl = (GenericTandemPeakList)gpl;
			gtpl.setParent(tpl.getParent());
			gtpl.setTandemCount(tpl.getTandemCount());
		}
		// else use normal
		else {
			gpl = new GenericPeakList();
		}
		// copy the peaks
		gpl.setPeaks(peaklist.getPeaks());
		return gpl;
	}
}
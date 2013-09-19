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
package org.proteomecommons.io.filter;

import org.proteomecommons.io.*;
import org.proteomecommons.jaf.*;
import java.util.*;

/**
 * A filter that can remove known ions from the peak list. This is handy for
 * cleaning up spectra that you know has contaimination peaks, e.g. filtering
 * out known trypsin peaks.
 * 
 * TODO: This class should be able to filter things based on relative
 * intensities. Fix this class to calibrate the known masses and relatively
 * filter out the masses.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class RemoveIonsPeakListFilter implements PeakListFilter {
	private int maxCharge = 4;

	private double ppmTolerance = 10;

	private double minTolerance = 0.05;

	// double array, first mass is the uncharged peptide mass and the second is
	// the relative intensity
	private double[][] masses = null;

	public static final double UNKNOWN_RELATIVE_INTENSITY = Double.MAX_VALUE;

	/**
	 * Set the masses that should be ignored.
	 * 
	 * @param masses
	 */
	public void setMasses(double[] masses) {
		// set the masses
		this.masses = new double[masses.length * maxCharge][2];
		for (int i = 0; i < masses.length; i++) {
			// set the masses using charge
			for (int j = 0; j < maxCharge; j++) {
				// set the singlely charged mass
				this.masses[i * maxCharge + j][0] = (masses[i] + (j + 1)
						* Atom.H.getMassInDaltons())
						/ (j + 1);
				this.masses[i * maxCharge + j][1] = UNKNOWN_RELATIVE_INTENSITY;
			}
		}
	}

	/**
	 * Set the max charge state that should be considered by this filter.
	 * 
	 * @param charge
	 */
	public void setMaxCharge(int charge) {
		this.maxCharge = charge;
	}

	/**
	 * Get the current max charge state considered.
	 * 
	 * @return
	 */
	public int getMaxCharge() {
		return maxCharge;
	}

	/**
	 * Set the current ppm tolerance considered.
	 * 
	 * @param ppm
	 */
	public void setPPMTolerance(double ppm) {
		this.ppmTolerance = ppm;
	}

	/**
	 * Get the max ppm tolerance.
	 * 
	 * @return
	 */
	public double getPPMTolerance() {
		return ppmTolerance;
	}

	/**
	 * Filter the peak list and remove the known masses.
	 */
	public PeakList filter(PeakList peaklist) {
		// remove the given masses
		LinkedList peaks = new LinkedList();
		Peak[] ps = peaklist.getPeaks();
		for (int i = 0; i < ps.length; i++) {
			peaks.add(ps[i]);
		}

		// remove matched peaks
		for (Iterator it = peaks.iterator(); it.hasNext();) {
			Peak peak = (Peak) it.next();
			// check against all known filter masses
			for (int j = 0; j < masses.length; j++) {
				double tol = ppmTolerance * peak.getMassOverCharge()
						/ 1000000;
				// make sure the tolerance is at least the min
				if (tol < minTolerance) {
					tol = minTolerance;
				}
				if (Math.abs(peak.getMassOverCharge() - masses[j][0]) <= tol) {
					it.remove();
					//					System.out.println("Removing
					// "+peak.massOverChargeInDaltons+" "+masses[j][0]);
					break;
				}
			}
		}

		// construct the finished peak list
		if(peaklist instanceof GenericPeakList) {
		 ((GenericPeakList)peaklist).setPeaks((Peak[]) peaks.toArray(new Peak[peaks.size()]));
		}

		// return the new peaklist
		return peaklist;
	}
}
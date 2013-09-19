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

/**
 * Abstraction of a peptide fragment, i.e. a "peak" in either MS/MS or MS
 * spectra. This class is read-only to ensure that you can't possibly mutate
 * information that other classes rely on.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public interface Peak {
    public static final double UNKNOWN_MZ = Double.MIN_VALUE;
    public static final double UNKNOWN_INTENSITY = Double.MIN_VALUE;
    public static final int UNKNOWN_CHARGE = Integer.MIN_VALUE;
    public static final int UNKNOWN_CENTROIDED = Integer.MIN_VALUE;
    public static final int UNKNOWN_AVERAGED = Integer.MIN_VALUE;
    public static final int UNKNOWN_MONOISOTOPIC = Integer.MIN_VALUE;
    public static final int UNKNOWN_DEISOTOPED = Integer.MIN_VALUE;
    public static final int CENTROIDED = -1;
    public static final int AVERAGED = -1;
    public static final int MONOISOTOPIC = -1;
    public static final int DEISOTOPED = -1;
    public static final int NOT_CENTROIDED = -2;
    public static final int NOT_AVERAGED = -2;
    public static final int NOT_MONOISOTOPIC = -2;
    public static final int NOT_DEISOTOPED = -2;
    
    /**
     * Returns the intensity of the peak.
     * @return A value [0-Double.MAX_VALUE] representing this peak's intensity or Peak.UNKNOWN_INTENSITY if the intensity isn't known.
     */
    public double getIntensity();
    
    /**
     * Returns the mass over charge in daltons.
     * @return The mass over charge in daltons or Peak.UNKNOWN_MZ if the mass over charge of this peak isn't known.
     */
    public double getMassOverCharge();
    
    /**
     * Returns charge information for this peak.
     * @return Returns the charge as an integer or Peak.UNKNOWN_CHARGE if the charge state isn't known.
     */
    public int getCharge();
    
    /**
     * Gets the centroided information.
     * @return Peak.CENTROIDED if centroided, Peak.NOT_CENTROIDED if not centroided, and Peak.UNKNOWN_CENTROIDED if centroid information isn't known.
     */
    public int getCentroided();
    /**
     * Gets monisotpic information about this peak.
     * @return Returns Peak.MONOISOTOPIC if the peak is monoisotopic, Peak.NOT_MONOISOTOPCI if it is not, and Peak.UNKNOWN_MONOISOTOPIC if it is unknown.
     */
    public int getMonoisotopic();
    /**
     * Gets averaged information about this peak.
     * @return Returns Peak.AVERAGED if this peak's m/z was averaged or Peak.NOT_AVERAGED if it was not or Peak.AVERAGED_UNKNOWN if averaging information isn't known.
     */
    public int getAveraged();
}
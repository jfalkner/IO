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
 * <p>Abstraction for MS output. This interface represents all types of MS information and users can query the getTandemCount() method to inspect if it is MS, MSMS, MSMSMS, etc.</p>
 *
 * <p>This class is read-only so that users can't change important values.</p>
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public interface PeakList {
    public static final int UNKNOWN_TANDEM_COUNT = 0;
    /**
     * Returns the number of collisions that were used when obtaining this peak list, i.e. 1 is MS, 2 is MSMS, 3 is MSMSMS, etc.
     * @return PeakList.UNKNOWN_TANDEM_COUNT if it is unknown, 1 if this is MS data, 2 if this is MSMS data, 3 if this is MSMSMS data, etc.
     */
    public int getTandemCount();
    /**
     * Gets the parent peak, if one exists.
     * @return The PeakList object representing the parent peak or null if no parent peak information is available.
     */
    public Peak getParentPeak();
    /**
     * Gets the parent peak list, if one exists.
     * @return The PeakList object representing the parent MS scan or null if no parent peak list is available.
     */
    public PeakList getParentPeakList();
    /**
     * Returns the an array of Peak objects that represents the peaks of this
     * peak list.
     *
     * @return The array of peaks that represent this peak list.
     */
    public Peak[] getPeaks();
}
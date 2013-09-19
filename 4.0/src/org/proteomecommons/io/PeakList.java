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
 * <p>
 * Abstraction for MS output. The peak list may or may not represent a tandem MS
 * run. You should use the instanceof operator to determine if this peak list
 * instance is a TandemPeakList.
 * </p>
 * 
 * <p>
 * This class is read-only so that users can't change important values.
 * </p>
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public interface PeakList {
	/**
	 * Returns the an array of Peak objects that represents the peaks of this
	 * peak list.
	 * 
	 * @return The array of peaks that represent this peak list.
	 */
	public Peak[] getPeaks();
}
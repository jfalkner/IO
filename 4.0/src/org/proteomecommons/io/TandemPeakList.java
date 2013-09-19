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
 * This class abstracts a tandem MS peak list. This usually means MS <sup>2
 * </sup> but it may also mean MS <sup>n </sup>. You can use the
 * getTandemCount() method to determine what MS this represents, and you can use
 * the getParent() method to get information about a parent peak list.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public interface TandemPeakList extends PeakList {
	/**
	 * A flag representing that the number of tandem MS runs is unknown.
	 */
	public static int UNKNOWN_TANDEM_COUNT = -1;

	/**
	 * Returns TandemPeakList.UNKNOWN_TANDEM_COUNT.
	 */
	public int getTandemCount();

	/**
	 * Retuns the ion that was fragmented to make this peak list. Null is
	 * returned if no information is known about the parent peak.
	 */
	public Peak getParent();

	/**
	 * Returns the parent peak list. If the parent peak list is not available,
	 * null is returned.
	 * 
	 * @return
	 */
	public PeakList getParentPeakList();
}
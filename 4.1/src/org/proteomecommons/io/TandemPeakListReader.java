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
 * The abstact base class for reading peak list information from a file. This
 * class supports batch reading of entire peak lists or streaming reading of
 * individual peaks.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public interface TandemPeakListReader extends PeakListReader {

	/**
	 * Returns the current level of MS that is being read.
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
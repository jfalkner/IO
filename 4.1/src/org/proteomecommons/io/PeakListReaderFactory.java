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

import java.io.InputStream;

/**
 * Factory for creating peak lists. Instances of this class can be registered
 * with the PeakListReader class in order to manage reading files of a
 * particular format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public interface PeakListReaderFactory {
	/**
	 * Requests a new PeakListReader instance using the data from the
	 * InputStream.
	 * 
	 * @param is
	 * @return
	 */
	public PeakListReader newInstance(InputStream is);
}
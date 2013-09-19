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
package org.proteomecommons.io.wiff;


import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 * This is a factory for making PeakListReader instances that can handle
 * AnalystQS WIFF file exports to CSV format.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class AnalystQSWIFFToCSVPeakListReaderFactory implements PeakListReaderFactory {

	/**
	 * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
	 */
	public PeakListReader newInstance(String filename) {
		return new AnalystQSWIFFToCSVPeakListReader(filename);
	}

	/**
	 * Return the regular expression for what this reader can handle.
	 * 
	 * @return
	 */
	public String getRegularExpression() {
		return ".*csv|.*CSV";
	}
}
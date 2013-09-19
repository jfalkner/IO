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
public interface PeakListReader {

	/**
	 * Sets the name of this PeakListReader. If you are parsing a file, and you
	 * are using one of the helper methods of this class, the name is
	 * automatically set to be the name of the file.
	 * 
	 * @param name
	 *            The name of this PeakListReader
	 */
	public void setName(String name) throws CheckedIOException;

	/**
	 * A method to notify the PeakListReader instance that no more Peak objects
	 * or PeakList objects are going to be read. This gives the reader a change
	 * to free up any unneeded resources that are allocated.
	 *  
	 */
	public void close() throws CheckedIOException;

	/**
	 * Returns the name of this PeakListReader object. If this PeakListReader
	 * was derived from a file, the name of the file is returned. For all other
	 * cases the name is undefined.
	 * 
	 * @return
	 */
	public String getName() throws CheckedIOException;

	/**
	 * Stream reading support for peaks. This method returns the next Peak in
	 * the peak list and if there are more than one peak list in the file and
	 * this was the last peak of one of the peak lists, the meta-information for
	 * the next peak list will be parsed and the isStartOfPeakList() method will
	 * return true.
	 * 
	 * @return The next peak, may or may not be sequential.
	 */
	public Peak next() throws CheckedIOException;

	/**
	 * A method to check if there are more peaks available in this peak list
	 * file. Note more than one peak list may be in a file, and you will need to
	 * use the isStartOfPeakList() method to determine if you are at the start
	 * of a new peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public boolean hasNext() throws CheckedIOException;

	/**
	 * A method to check if this stream is at the start of a new peak list. This
	 * method returns true only when you are at the start of a peak list.
	 * 
	 * @return true if there are more peaks, false if there are not.
	 */
	public boolean isStartOfPeakList() throws CheckedIOException;

	/**
	 * Returns a PeakList object with an array of Peak objects that are sorted
	 * and normalized. This method does load everything in to memory, and it is
	 * not well-suited for extraordinarily large peaklist files.
	 *  
	 */
	public PeakList getPeakList() throws CheckedIOException;
}
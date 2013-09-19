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

import java.io.IOException;

/**
 * An abstract base class for serializing peak lists. This class has two methods
 * of peak list serialization: in memory and stream. "in memory" is a
 * all-at-once approach where the entire peak list is parsed in to memory, i.e.
 * a PeakList object, and serialized to disk via invokation of the
 * write(PeakList) or write(PeakList[]) methods. The "stream" implementation is
 * a memory sensitive implementation where parts of the peak list are serialized
 * to disk as they are avaiable via invokation of the startPeakList(),
 * write(Peak), and close() methods. In general the in-memory approach is
 * simple and sufficient; however, the stream approach is helpful for
 * serializing large peak list files, especially files too large to keep in
 * memory all at once.
 * 
 * See the documentation provided with this project for information on
 * individual peak list formats. Also note that individual implementations fot
 * he PeakListReader and PeakListWriter classes may provide file-format specific
 * functionality.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public interface PeakListWriter {

	/**
	 * Method for writing an entire peak list, all at once.
	 * 
	 * @param peaklist
	 * @throws IOException
	 */
	public abstract void write(PeakList peaklist) throws CheckedIOException;

	/**
	 * Start method for writing a MSMS peak list with known m/z and charge.
	 * Multiple
	 * 
	 * @param parentIonMassOverChargeInDaltons
	 * @param charge
	 *            The charge of the parent ion.
	 */
	public abstract void startPeakList() throws CheckedIOException;

	/**
	 * Write a peak to the peak list. You must set this value after invoking startPeakList().
	 * 
	 * @param peak
	 *            The peak to write.
	 */
	public abstract void write(Peak peak) throws CheckedIOException;

	/**
	 * Bulk write method.
	 * @param peaks Array of peaks to write.
	 */
	void write(Peak[] peaks) throws CheckedIOException;
	/**
	 * Bulk write method with offsets.
	 * @param peaks Array of peaks to write.
	 * @param offset The offset to start at.
	 * @param length The number of peaks from the offset to write.
	 */
	void write(Peak[] peaks, int offset, int length) throws CheckedIOException;
	/**
	 * Finish writing the entired peak list file, including all sub-peak lists.
	 * Do not use this method to delimit the end of a single peak list when more
	 * peak lists are to occur after it (e.g. a LC MS run). Use multiple
	 * invokations of startPeakList().
	 *  
	 */
	public abstract void close() throws CheckedIOException;
}
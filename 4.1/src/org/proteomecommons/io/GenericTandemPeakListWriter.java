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

import java.io.*;

/**
 * A helper class for making instances of the TandemPeakListWriter interface.
 * The class keeps all the PeakListWriter functionality, but it also allows for
 * specifying a parent ion, the parent ion's peaklist, and at what level MSMS
 * this is.
 * 
 * @author Jayson Falkner - jfalkner@umich.edu
 *  
 */
public abstract class GenericTandemPeakListWriter extends GenericPeakListWriter
		implements TandemPeakListWriter {
	private Peak parent;

	private PeakList parentPeakList;

	private int tandemCount;

	/**
	 * Public constructor. This method passes the output stream to the parent
	 * class.
	 * 
	 * @param out
	 */
	public GenericTandemPeakListWriter(OutputStream out) {
		super(out);
	}

	/**
	 * @see org.proteomecommons.io.TandemPeakListWriter#setParent(org.proteomecommons.io.Peak)
	 */
	public void setParent(Peak parent) throws CheckedIOException {
		this.parent = parent;
	}

	public Peak getParent() {
		return parent;
	}

	/**
	 * @see org.proteomecommons.io.TandemPeakListWriter#setParentPeakList(org.proteomecommons.io.PeakList)
	 */
	public void setParentPeakList(PeakList peaklist) throws CheckedIOException {
		this.parentPeakList = peaklist;
	}

	public PeakList getParentPeakList() {
		return parentPeakList;
	}

	/**
	 * @see org.proteomecommons.io.TandemPeakListWriter#setTandemCount(int)
	 */
	public void setTandemCount(int count) throws CheckedIOException {
		this.tandemCount = count;
	}

	public int getTandemCount() {
		return tandemCount;
	}
}
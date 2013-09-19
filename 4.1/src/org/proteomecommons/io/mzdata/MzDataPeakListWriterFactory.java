/*
 * Created on Mar 26, 2005
 *
 */
package org.proteomecommons.io.mzdata;

import java.io.OutputStream;

import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 * Creates writers for mzData documents. 
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 */
public class MzDataPeakListWriterFactory implements PeakListWriterFactory {

	/* (non-Javadoc)
	 * @see org.proteomecommons.io.PeakListWriterFactory#newInstance(java.io.OutputStream)
	 */
	public PeakListWriter newInstance(OutputStream is) {
		return new MzDataPeakListWriter(is);
	}

	/* (non-Javadoc)
	 * @see org.proteomecommons.io.PeakListWriterFactory#getFileExtension()
	 */
	public String getFileExtension() {
		return "mzdata";
	}

	/* (non-Javadoc)
	 * @see org.proteomecommons.io.PeakListWriterFactory#getName()
	 */
	public String getName() {
		return "mzData";
	}

}

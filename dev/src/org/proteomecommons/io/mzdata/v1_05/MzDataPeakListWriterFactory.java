package org.proteomecommons.io.mzdata.v1_05;


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
	public PeakListWriter newInstance(String filename) {
		return new MzDataPeakListWriter(filename);
	}

	/* (non-Javadoc)
	 * @see org.proteomecommons.io.PeakListWriterFactory#getFileExtension()
	 */
	public String getFileExtension() {
		return ".mzdata.xml";
	}

	/* (non-Javadoc)
	 * @see org.proteomecommons.io.PeakListWriterFactory#getName()
	 */
	public String getName() {
		return "mzData 1.0.5";
	}

}

package org.proteomecommons.io.pkl;

import java.io.OutputStream;

import org.proteomecommons.io.*;

/**
 * A PeakListWriterFactory instance for creating PeakListWriter objects that can
 * serialized peak lists in PKL format.
 * 
 * @author Jayon Falkner - jfalkner@umich.edu
 */
public class MicromassPKLPeakListWriterFactory implements PeakListWriterFactory {

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#getFileExtension()
	 */
	public String getFileExtension() {
		return ".pkl";
	}
	
	public String getName() {
		return "Micromass PKL";
	}

	/**
	 * @see org.proteomecommons.io.PeakListWriterFactory#newInstance(java.io.OutputStream)
	 */
	public PeakListWriter newInstance(OutputStream os) {
		return new MicromassPKLPeakListWriter(os);
	}
}
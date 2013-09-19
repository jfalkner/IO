package org.proteomecommons.io.mzxml;


import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;
import org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class MzXMLPeakListWriterFactory implements PeakListWriterFactory {
    
    public PeakListWriter newInstance(String filename) {
        // default to the MZXML 2.1 writer
        MzXMLPeakListWriter writer = new MzXMLPeakListWriter(filename);
        // return the writer
        return writer;
    }
    
    public String getFileExtension() {
        return ".mzxml.xml";
    }
    
    public String getName() {
        return "mzXML";
    }
    
}

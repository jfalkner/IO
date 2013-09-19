package org.proteomecommons.io.mzxml.v1_1_1;


import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 * An MZXML 2.0 compatible writer. This is based off the 1.1.1 schema.
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class MzXMLPeakListWriterFactory implements PeakListWriterFactory {
    
    public PeakListWriter newInstance(String filename) {
        // get the writer
        MzXMLPeakListWriter writer = new MzXMLPeakListWriter(filename);
        // return the writer
        return writer;
    }
    
    public String getFileExtension() {
        return ".mzxml.xml";
    }
    
    public String getName() {
        return "mzXML version 1.1.1";
    }
    
}

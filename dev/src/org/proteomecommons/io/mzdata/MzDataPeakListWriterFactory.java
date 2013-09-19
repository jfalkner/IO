package org.proteomecommons.io.mzdata;


import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;
import org.proteomecommons.io.mzdata.v1_05.MzDataPeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class MzDataPeakListWriterFactory implements PeakListWriterFactory {
    
    public PeakListWriter newInstance(String filename) {
        // default to the mzData 1.0.5 writer
        MzDataPeakListWriter writer = new MzDataPeakListWriter(filename);
        // return the writer
        return writer;
    }
    
    public String getFileExtension() {
        return ".mzdata.xml";
    }
    
    public String getName() {
        return "mzData";
    }
}
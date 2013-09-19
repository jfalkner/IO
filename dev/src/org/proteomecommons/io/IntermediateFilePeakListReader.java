package org.proteomecommons.io;

import java.io.File;

/**
 * An interface representing tools that generate an intermediate file. The GUI has an option for users to keep this file instead of having the IO Framework make a similar file.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public interface IntermediateFilePeakListReader {
    /**
     * Returns the location of the intermediate file that was generated by the underlying tool.
     * @return The location of the intermediate file that was generated by the underlying tool.
     */
    File getIntermediateFile();
    /**
     * Returns the extension of the intermediate file generated by the underlying tool.
     * @return Returns a extension that is appropriate for the file generated by the underlying tool.
     */
    String getIntermediateExtension();
}
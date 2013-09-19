package org.proteomecommons.io.dta;

import java.io.File;
import junit.framework.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.*;
import org.proteomecommons.jaf.Atom;

/**
 *
 * @author Jarret Falkner
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDTAPeakListReaderTest extends TestCase {
    
    public SequestDTAPeakListReaderTest(String testName) {
        super(testName);
    }
    
    public void testNoop() throws Exception {
        // noop
    }
    
//    /**
//     *A test that ensures empty files raise an InvalidFileFormatException.
//     */
//    public void testEmptyFile() throws Exception{
//        File temp =null;
//        try {
//            // make a blank file
//            temp= File.createTempFile("1b.642.3.2129.2129.2",".dta");
//
//            try {
//                // try to get a reader
//                PeakListReader read = GenericPeakListReader.getPeakListReader(temp.getCanonicalPath());
//
//                // fail if we make it here
//                fail("Reader should have failed!");
//            } catch (InvalidFileFormatException iffe) {
//                // noop -- we expect this
//            }
//        } finally {
//            try{temp.delete();} catch(Exception e){}
//        }
//
//    }
    
}

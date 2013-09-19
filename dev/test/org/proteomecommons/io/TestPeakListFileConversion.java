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

import java.io.File;
import junit.framework.TestCase;
import org.proteomecommons.io.dta.SequestDTAPeakListReaderFactory;
import org.proteomecommons.io.dta.SequestDTAPeakListWriterFactory;
import org.proteomecommons.io.mgf.MascotGenericFormatPeakListReaderFactory;
import org.proteomecommons.io.mgf.MascotGenericFormatPeakListWriterFactory;
import org.proteomecommons.io.msp.MSPPeakListReaderFactory;
import org.proteomecommons.io.mzdata.v1_05.MzDataPeakListWriterFactory;
import org.proteomecommons.io.pkl.MicromassPKLPeakListReaderFactory;
import org.proteomecommons.io.pkl.MicromassPKLPeakListWriterFactory;
import org.proteomecommons.io.raw.RawViaReadwFactory;
import org.proteomecommons.io.txt.PlainTextNotepadFriendlyPeakListWriterFactory;
import org.proteomecommons.io.txt.PlainTextPeakListWriterFactory;
import org.proteomecommons.io.yep.YEPPeakListReaderFactory;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class TestPeakListFileConversion extends TestCase {
    // helper to make custom directories
    private static int testConversionCount = 0;
    
    public void testCleanThingsUp() throws Exception {
        File dir = new File("/todelete/testFileConversion/");
        DevUtil.recursiveDelete(dir);
    }
    
    public void testFileConversion(String filename, PeakListReaderFactory plrf, PeakListWriterFactory plwf) throws Exception {
        // inc each conversion to make unique, empty directories
        testConversionCount++;
        // make a blank directory
        File dir = new File("/todelete/testFileConversion/"+testConversionCount);
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // make the file
        String[] split = filename.split("/");
        File file = new File(dir, split[split.length-1]);
        File outputFile = new File(dir, "output"+plwf.getFileExtension());
        
        // copy the file
        DevUtil.copyOver(filename, TestPeakListFileConversion.class, file);
        
        // read the file
        PeakListReader plr = plrf.newInstance(file.getCanonicalPath());
        // write the file out in the specified format
        PeakListWriter plw = plwf.newInstance(outputFile.getCanonicalPath());
        // read the peak list
        for (PeakList pl = plr.getPeakList(); pl != null; pl = plr.getPeakList()) {
            plw.write(pl);
        }
        // close all
        plr.close();
        plw.close();
        
        // read the file back
        PeakListReader a = plrf.newInstance(file.getCanonicalPath());
        PeakListReader b = GenericPeakListReader.getPeakListReader(outputFile.getCanonicalPath());
        try {
            PeakList pa = a.getPeakList();
            PeakList pb = b.getPeakList();
            while (pa != null && pb != null) {
                // conditionally skip tandem peak lists
                if (plw instanceof OnlyTandemPeakListWriter && pa.getParentPeak() == null) {
                    while (pa.getParentPeak()==null) {
                        pa = a.getPeakList();
                        // if null, exit
                        if (pa == null) {
                            fail("Not enough matching peak lists!");
                        }
                    }
                }
                // check parent peaks
                if (pa.getParentPeak() == null) assertTrue("Didn't expect pb to have a parent peak.", pb.getParentPeak() == null);
                if (pb.getParentPeak() == null) assertTrue("Didn't expect pa to have a parent peak.", pa.getParentPeak() == null);
                Peak ppa = pa.getParentPeak();
                Peak ppb = pb.getParentPeak();
                if (ppa != null && ppb != null) {
                    assertEquals("Same parent m/z values.", ppa.getMassOverCharge(), ppb.getMassOverCharge(), 0.01);
                }
                
                // check that they have the same number of peaks
                Peak[] psa = pa.getPeaks();
                Peak[] psb = pb.getPeaks();
                assertEquals("Same number of peaks expected.", psa.length, psb.length);
                for (int i=0;i<psa.length;i++) {
                    assertEquals("Same peak m/z value", psa[i].getMassOverCharge(), psb[i].getMassOverCharge(), 0.01);
                    assertEquals("Same peak intensity value", psa[i].getIntensity(), psb[i].getIntensity(), 0.01);
                }
                
                // read the next peakslists
                pa = a.getPeakList();
                pb = b.getPeakList();
            }
            // they should both be null
            assertEquals("Both readers should be finished.", pa, pb);
        } finally {
            try { a.close(); } catch (Exception e){}
            try { b.close(); } catch (Exception e){}
        }
        
        // try to clean up the directory
        try {
            DevUtil.recursiveDelete(dir);
        } catch (Throwable t){
            System.err.println("Warning! Can't fully delete "+dir+". "+t.getMessage());
        }
    }
    
    public void testMGFConversions() throws Exception {
        testAllConversions("test.mgf", new MascotGenericFormatPeakListReaderFactory());
    }
    
    public void testDTAConversions() throws Exception {
        testAllConversions("test.dta", new SequestDTAPeakListReaderFactory());
    }
    
    public void testPKLConversions() throws Exception {
        testAllConversions("test.pkl", new MicromassPKLPeakListReaderFactory());
    }
//    
//    public void testMSPConversions() throws Exception {
//        testAllConversions("files/nist-msp/example.msp", new MSPPeakListReaderFactory());
//    }
//    
//    public void testMzXMLv2_0Conversions() throws Exception {
//        testAllConversions("files/mzxml/v2_0/example.mzXML", new org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReaderFactory());
//    }
//    
//    public void testMzXMLv2_1Conversions() throws Exception {
//        testAllConversions("files/mzxml/v2_1/example.mzXML", new org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListReaderFactory());
//    }
//    
//    public void testRAWConversions() throws Exception {
//        testAllConversions("files/thermo-raw/example.raw", new RawViaReadwFactory());
//    }
//    
//    public void testYEPConversions() throws Exception {
//        testAllConversions("files/yep/Analysis.yep", new YEPPeakListReaderFactory());
//    }
    
    
    public void testAllConversions(String filename, PeakListReaderFactory plrf) throws Exception {
        testFileConversion(filename, plrf, new SequestDTAPeakListWriterFactory());
        testFileConversion(filename, plrf, new MicromassPKLPeakListWriterFactory());
        testFileConversion(filename, plrf, new org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListWriterFactory());
        testFileConversion(filename, plrf, new org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListWriterFactory());
        testFileConversion(filename, plrf, new org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListWriterFactory());
        testFileConversion(filename, plrf, new MascotGenericFormatPeakListWriterFactory());
        testFileConversion(filename, plrf, new MzDataPeakListWriterFactory());
        testFileConversion(filename, plrf, new PlainTextPeakListWriterFactory());
        testFileConversion(filename, plrf, new PlainTextNotepadFriendlyPeakListWriterFactory());
    }
}

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
package org.proteomecommons.io.mzxml.v2_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import junit.framework.TestCase;
import org.proteomecommons.io.DevUtil;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;
import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MZXMLv2_0Test extends TestCase {
    
    public void testCanLoadClass() throws Exception {
        File dir = new File("/todelete/mzxml/2_0/testCanLoadClass");
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // make a fake file
        File file = new File(dir, "example.2.0.mzxml.xml");
        
        // check the reader
        PeakListReaderFactory plr = GenericPeakListReader.getPeakListReaderFactory(file.getCanonicalPath());
        assertTrue("Expected to load the mzXML 2.0 factory.", plr instanceof org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReaderFactory);
        
        // check the reader
        PeakListWriterFactory plw = GenericPeakListWriter.getPeakListWriterFactory(file.getCanonicalPath());
        assertTrue("Expected to load the mzXML 2.0 factory.", plw instanceof org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListWriterFactory);
    }
    
    /**
     *Tests that the MZXML v2.0 code can successfully read a known file in that format.
     */
    public void testCanRead() throws Exception {
        // make a new directory
        File dir = new File("/todelete/mzxml/v2_0/testCanRead");
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // file to user
        File file = new File(dir, "example.mzXML");
        file.deleteOnExit();
        
        try {
            DevUtil.copyOver("files/mzxml/v2_0/example.mzXML", MZXMLv2_0Test.class, file);

            // try to read the file
            PeakListReader plr = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
            try {
                assertTrue("Expected a MzXML 2.0 reader.", plr instanceof org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReader);
                // read the peak lists
                int peakListCount = 0;
                for (PeakList pl = plr.getPeakList();pl != null;pl = plr.getPeakList()) {
                    peakListCount++;
                    // check that num 33 is a MS scan
                    if (peakListCount == 33) {
                        assertTrue("Expected a MS peak list", pl.getTandemCount() == 1);
                    }
                    // check that num 34 is a MSMS scan
                    if (peakListCount == 34) {
                        assertTrue("Expected a MSMS peak list", pl.getTandemCount() > 1);
                    }
                }
                
                // check that the correct number of peak lists are read
                assertEquals("Expected 1545 peak lists to be in the file.", 1545, peakListCount);
            } finally {
                plr.close();
            }
        } finally {
            file.delete();
        }
    }
    
    
   /**
     *Tests that the MZXML v2.0 code can successfully read/write a known file in that format.
     */
    public void testCanWrite() throws Exception {
        // make a new directory
        File dir = new File("/todelete/mzxml/v2_0/testCanWrite");
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // file to user
        File file = new File(dir, "example.mzXML");
        file.deleteOnExit();
        
        // output file
        File outputFile = new File(dir, "outputFile.2.0.mzXML.xml");
        
        try {
            DevUtil.copyOver("files/mzxml/v2_0/example.mzXML", MZXMLv2_0Test.class, file);
            
            // try to read the file
            PeakListReader plr = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
            PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(outputFile.getCanonicalPath());
            try {
                assertTrue("Expected a MZXML 2.0 reader.", plr instanceof org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReader);
                // read the peak lists
                int peakListCount = 0;
                for (PeakList pl = plr.getPeakList();pl != null;pl = plr.getPeakList()) {
                    peakListCount++;
                    // write the peak list
                    plw.write(pl);
                }
                plw.close();
            } finally {
                try { plr.close(); } catch (Exception e){}
                try { plw.close(); } catch (Exception e){}
            }
            
            // check that the file is mzXML version 2.0
            FileReader fr = new FileReader(outputFile);
            BufferedReader br = new BufferedReader(fr);
            boolean foundVersion = false;
            try {
                for (String line = br.readLine();line != null; line = br.readLine()) {
                    if (line.indexOf("http://sashimi.sourceforge.net/schema_revision/mzXML_2.0") != -1) {
                        foundVersion = true;
                        break;
                    }
                }
            } finally {
                br.close();
                fr.close();
            }
            assertTrue("Expected to find the version.", foundVersion);
            
            // check that the two peak lists match
            PeakListReader a = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
            PeakListReader b = GenericPeakListReader.getPeakListReader(outputFile.getCanonicalPath());
            PeakList pa = a.getPeakList();
            PeakList pb = b.getPeakList();
            while (pa != null && pb != null) {
                assertEquals("Same peak count expected.", pa.getPeaks().length, pb.getPeaks().length);
                // check that the other info is the same
                Peak precursorA = pa.getParentPeak();
                Peak precursorB = pb.getParentPeak();
                if (precursorA == null || precursorB == null) {
                    assertTrue("Expected both precursors to be null.", precursorA == null && precursorB == null);
                } else {
                    DevUtil.assertPeaksAreTheSame(precursorB, precursorA);
                }
                // check that child peaks are the same
                Peak[] peaksA = pa.getPeaks();
                Peak[] peaksB = pb.getPeaks();
                assertEquals("Should have had the same number of peaks.", peaksA.length, peaksB.length);
                for (int i=0;i<peaksA.length;i++) {
                    DevUtil.assertPeaksAreTheSame(peaksA[i], peaksB[i]);
                }
                pa = a.getPeakList();
                pb = b.getPeakList();
            }
            assertTrue("Both should be null", pa == pb);
        } finally {
            file.delete();
        }
    }
}

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

package org.proteomecommons.io.mgf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.raw.RawPeakListReaderTest;


/**
 * A test to verify that the plain-MSMS search works.
 * @author Jayson
 */
public class MascotGenericFormatTest extends TestCase{
    
    // test that a ficticious test works
    public void testTandemWriteAndRead() throws Exception {
        // list of values
        int[] values = new int[]{1,2,3,4,5,6,7,8,9,10};
        // buffer peaks
        LinkedList buf = new LinkedList();
        for (int i=0;i<values.length;i++){
            // make a new generic peak
            GenericPeak gp = new GenericPeak();
            gp.setIntensity(1);
            gp.setMassOverCharge(values[i]);
            // add the peak
            buf.add(gp);
        }
        // set those fragments as peaks in a peak lsit
        GenericPeakList gpl = new GenericPeakList();
        gpl.setPeaks((Peak[])buf.toArray(new Peak[0]));
        GenericPeak gp = new GenericPeak();
        gp.setCharge(72);
        gpl.setParentPeak(gp);
        
        // make a temp file
        File temp = new File(System.currentTimeMillis()+".mgf");
        PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(temp.getAbsolutePath());
        plw.write(gpl);
        plw.close();
        
        // try reading
        FileInputStream fis = new FileInputStream(temp.getAbsolutePath());
        PeakListReader plr = GenericPeakListReader.getPeakListReader(temp.getAbsolutePath());
        PeakList pl = plr.getPeakList();
        // check the peaks
        Peak[] peaks = pl.getPeaks();
        Arrays.sort(peaks);
        
        try {
            //check that we did, indeed get a mgf pkl obj back
            if(!(pl instanceof MascotGenericFormatPeakList)){
                fail("didn't get an mgf peak list back from the reader");
            } else {
                MascotGenericFormatPeakList mpl = (MascotGenericFormatPeakList)pl;
                assertTrue("Expected a charge state of 72.", mpl.getCharge().contains("72"));
            }
            
            // check that the correct number of peaks are saved/read
            assertEquals("Expected the same number of peaks.", values.length, peaks.length);
            // check each peak
            for (int i=0;i<values.length;i++){
                if (peaks[i].getMassOverCharge()!=values[i]) {
                    fail("Can't read data saved from a MGF peak list.");
                }
            }
        } finally {
            // remove temp files
            temp.delete();
        }
    }
    
    /**
     * A simple test to make sure that blank peak lists are not skipped.
     */
    public void testWontSkipBlankPeakLists() throws Exception {
        // load the file from the test JAR
        InputStream in = RawPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream("files/mgf/blank-test.mgf");
        File f = new File("blank-test.mgf");
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        
        // read through and check the content
        try {
            // make a reader
            PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
            try {
                // track how many peak lists are read
                int peakListCount = 0;
                for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
                    peakListCount++;
                    // check that the second peak list is blank, as expected
                    if (peakListCount == 2) {
                        assertTrue("Peak list #2 is supposed to be blank.", pl.getPeaks().length == 0);
                    }
                }
                assertEquals("Should have read three peak lists.", 3, peakListCount);
            } finally {
                // nicely close the reader
                try { plr.close(); } catch (Exception e){}
            }
        } finally {
            in.close();
            f.delete();
        }
    }
}

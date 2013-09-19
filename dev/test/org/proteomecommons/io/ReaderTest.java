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
import java.io.FileOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ReaderTest extends TestCase {
    
    public void testReadDTA() throws Exception {
        // load the fake DTA file
        String filename = "test.dta";
        
        File f = new File(filename);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        
        PeakList p = GenericPeakListReader.read(f.getCanonicalPath());
        // check that the m/z is 1000 (appropimately)
        int mz = (int)p.getParentPeak().getMassOverCharge();
        assertEquals("m/z should be 1000", 1000, mz);
    }
    
    public void testReadPKL() throws Exception {
        // load the fake PKL file
        String filename = "test.pkl";
        
        File f = new File(filename);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        
        try {
            PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
            PeakList p1 = plr.getPeakList();
            Peak[] peaks1 = p1.getPeaks();
            assertEquals("First peak should be 100 m/z", 100, (int)peaks1[0].getMassOverCharge());
            assertEquals("Second peak should be 200 m/z", 200, (int)peaks1[1].getMassOverCharge());
            assertEquals("Total peaks should be 2", 2, p1.getPeaks().length);
            assertEquals("m/z should be 1000", 1000, (int)p1.getParentPeak().getMassOverCharge());
            PeakList p2 = plr.getPeakList();
            Peak[] peaks2 = p2.getPeaks();
            assertEquals("First peak should be 101 m/z", 101, (int)peaks2[0].getMassOverCharge());
            assertEquals("Second peak should be 201 m/z", 201, (int)peaks2[1].getMassOverCharge());
            assertEquals("Third peak should be 301 m/z", 301, (int)peaks2[2].getMassOverCharge());
            assertEquals("Total peaks should be 3", 3, p2.getPeaks().length);
            assertEquals("m/z should be 1999", 1999, (int)p2.getParentPeak().getMassOverCharge());
        } finally {
            f.delete();
        }
    }
    
    public void testReadMGF() throws Exception {
        // load the fake MGF file
        String filename = "test.mgf";
        
        File f = new File(filename);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        
        try {
            PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
            PeakList p1 = plr.getPeakList();
            Peak[] peaks1 = p1.getPeaks();
            assertEquals("Total peaks should be 2", 2, p1.getPeaks().length);
            assertEquals("First peak should be 100 m/z", 100, (int)peaks1[0].getMassOverCharge());
            assertEquals("Second peak should be 200 m/z", 200, (int)peaks1[1].getMassOverCharge());
            assertEquals("m/z should be 1000", 1000, (int)p1.getParentPeak().getMassOverCharge());
            PeakList p2 = plr.getPeakList();
            Peak[] peaks2 = p2.getPeaks();
            assertEquals("Total peaks should be 3", 3, p2.getPeaks().length);
            assertEquals("First peak should be 101 m/z", 101, (int)peaks2[0].getMassOverCharge());
            assertEquals("Second peak should be 201 m/z", 201, (int)peaks2[1].getMassOverCharge());
            assertEquals("Third peak should be 301 m/z", 301, (int)peaks2[2].getMassOverCharge());
            assertEquals("m/z should be 1999", 1999, (int)p2.getParentPeak().getMassOverCharge());
            // check that there are no more peak lists
            assertNull("There should only be 2 peak lists total.", plr.getPeakList());
        } finally {
            f.delete();
        }
    }
    
}

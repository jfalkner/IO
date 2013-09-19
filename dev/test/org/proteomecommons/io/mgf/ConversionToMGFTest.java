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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.util.PeakMassOverChargeComparator;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ConversionToMGFTest extends TestCase {
    
    public void testConvertDTAToMGF() throws Exception {
        // load the fake DTA file
        String filename = "test.dta";
        // buffr the data
        File dir = new File("/todelete/testConvertToDTA/");
        dir.mkdirs();
        File file = new File(dir, "test.dta");
        file.deleteOnExit();
        File mgfFile = new File(dir, "test.mgf");
        mgfFile.deleteOnExit();
        try {
            {
                // copy temp file over
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
                
                for (int i=is.read();i!=-1;i=is.read()) {
                    fos.write((byte)i);
                }
                fos.flush();
                fos.close();
            }
            
            // read thee file
            PeakList p = GenericPeakListReader.read(file.getCanonicalPath());
            {
                // check that the m/z is 1000 (appropimately)
                int mz = (int)p.getParentPeak().getMassOverCharge();
                assertEquals("m/z should be 1000", 1000, mz);
            }
            {
                // check the peaks
                Peak[] peaks = p.getPeaks();
                assertEquals("expected 3 peaks.", 3, peaks.length);
            }
            
            // make a MGF writer
            MascotGenericFormatPeakListWriter plw = new MascotGenericFormatPeakListWriter(mgfFile.getCanonicalPath());
            plw.write(p);
            plw.close();
            
            // read the data back in
            PeakList pl = GenericPeakListReader.read(mgfFile.getCanonicalPath());
            Peak parentPeak = pl.getParentPeak();
            // check that the m/z hasn't changed
            assertEquals("m/z should be 1000", 1000f, parentPeak.getMassOverCharge(), 0.01);
            // check that charge hasn't changed
            assertTrue("Should be a charge assigned peak", parentPeak.getCharge() != Peak.UNKNOWN_CHARGE);
            assertEquals("charge should be 2", 2, parentPeak.getCharge());
            Peak[] peaks = pl.getPeaks();
            Arrays.sort(peaks, new PeakMassOverChargeComparator());
            Peak[] origPeaks = p.getPeaks();
            Arrays.sort(origPeaks, new PeakMassOverChargeComparator());
            assertEquals("Should have the same number of peaks.", origPeaks.length, peaks.length);
            for (int i=0;i<peaks.length;i++) {
                assertEquals("peak m/z should be the same.", peaks[i].getMassOverCharge(), origPeaks[i].getMassOverCharge(), 0.01);
                assertEquals("peak intensity should be the same.", peaks[i].getIntensity(), origPeaks[i].getIntensity(), 0.01);
            }
            
        } finally {
            file.delete();
            mgfFile.delete();
        }
    }
}

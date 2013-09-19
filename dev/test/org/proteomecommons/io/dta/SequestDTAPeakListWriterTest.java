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
package org.proteomecommons.io.dta;

import java.io.File;
import java.util.Arrays;
import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriter;

/**
 * Tests that the writer can serialize one or more TandemPeakList objects as DTA file(s).
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class SequestDTAPeakListWriterTest extends TestCase {
    
    public void testWriteDTAPeakList() throws Exception {
        // make the peak list
        GenericPeak parent = new GenericPeak();
        parent.setCharge(2);
        parent.setMassOverCharge(1000);
        
        // make the peaks
        Peak[] peaks = new Peak[5];
        for (int i=0;i<peaks.length;i++) {
            // make a new peak
            GenericPeak gp = new GenericPeak();
            gp.setIntensity(i+100);
            gp.setMassOverCharge((i+1)*100);
            
            // set the peak
            peaks[i] = gp;
        }
        
        // make the peak list
        GenericPeakList peaklist = new GenericPeakList();
        peaklist.setParentPeak(parent);
        peaklist.setPeaks(peaks);
        
        // make the temp directory
        File dir = new File("/todelete/testWriteDTAPeakList");
        dir.mkdirs();
        File file = new File(dir, "test.dta");
        
        // write out the peak list
        PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(file.getCanonicalPath());
        try {
            plw.write(peaklist);
        } finally {
            plw.close();
        }
        
        // read the peak list
        PeakListReader plr = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
        PeakList pl = plr.getPeakList();
        plr.close();
        
        // assert that the peak lists match.
        assertPeakListsMatch(pl, peaklist);
    }
    
    
    public void testWriteMultipleDTAPeakList() throws Exception {
        // make an array of peaklsits
        PeakList[] peaklists = new PeakList[3];
        for (int peakListCount = 0;peakListCount<peaklists.length;peakListCount++) {
            // make the peak list
            GenericPeak parent = new GenericPeak();
            parent.setCharge(2);
            parent.setMassOverCharge(1000);
            
            // make the peaks
            Peak[] peaks = new Peak[5];
            for (int i=0;i<peaks.length;i++) {
                // make a new peak
                GenericPeak gp = new GenericPeak();
                gp.setIntensity(i+100+peakListCount);
                gp.setMassOverCharge((i+1)*100+peakListCount);
                
                // set the peak
                peaks[i] = gp;
            }
            
            // make the peak list
            GenericPeakList peaklist = new GenericPeakList();
            peaklist.setParentPeak(parent);
            peaklist.setPeaks(peaks);
            
            // set the peak list
            peaklists[peakListCount] = peaklist;
        }
        
        // make the temp directory
        File dir = new File("/todelete/testWriteMultipleDTAPeakList");
        dir.mkdirs();
        File file = new File(dir, "test.dta");
        file.deleteOnExit();
        try {
            // write out the peak list
            PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(file.getCanonicalPath());
            try {
                for (PeakList peaklist : peaklists) {
                    plw.write(peaklist);
                }
            } finally {
                plw.close();
            }
            
            // read the peak list
            PeakListReader plr = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
            // count to ensure all are read
            int peakListCount = 0;
            try {
                for (PeakList peaklist : peaklists) {
                    PeakList pl = plr.getPeakList();
                    // assert that the peak lists match.
                    assertPeakListsMatch(pl, peaklist);
                    peakListCount++;
                }
            } finally {
                plr.close();
            }
            // assert all were read
            assertEquals("Read all the peak lists.", peaklists.length, peakListCount);
        } finally {
            file.delete();
        }
    }
    
    // helper method to assert that all parts of the peak lists match according to what a DTA expects.
    private static void assertPeakListsMatch(final PeakList pl, final PeakList peaklist) {
        // check that it matches
        Peak plParent = pl.getParentPeak();
        Peak originalParent = peaklist.getParentPeak();
        assertTrue("Parent peak must be an instance of a ChargeAssignedPeak.", plParent.getCharge() != Peak.UNKNOWN_CHARGE);
        // check that the parent m/z match
        assertEquals("Parent m/z must match.", originalParent.getMassOverCharge(), plParent.getMassOverCharge(), 0.01);
        assertEquals("Parent charge must match.", originalParent.getCharge(), plParent.getCharge(), 0.01);
        
        // check all the peaks
        Peak[] plPeaks = pl.getPeaks();
        Peak[] originalPeaks = peaklist.getPeaks();
        Arrays.sort(plPeaks);
        Arrays.sort(originalPeaks);
        assertEquals("Must have the same number of peaks.", originalPeaks.length, plPeaks.length);
        for (int i=0;i<originalPeaks.length;i++) {
            // check m/z and intensities
            assertEquals("Child peak m/z must match.", originalPeaks[i].getMassOverCharge(), plPeaks[i].getMassOverCharge(), 0.01);
            assertEquals("Child peak intensity must match.", originalPeaks[i].getIntensity(), plPeaks[i].getIntensity(), 0.01);
        }
    }
}

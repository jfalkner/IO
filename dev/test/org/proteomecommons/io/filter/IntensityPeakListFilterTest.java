/*
 * IntensityPeakListFilterTest.java
 * JUnit based test
 *
 * Created on August 27, 2006, 12:18 AM
 */

package org.proteomecommons.io.filter;

import junit.framework.*;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.*;

/**
 *
 * @author Jarret
 */
public class IntensityPeakListFilterTest extends TestCase {
    
    public IntensityPeakListFilterTest(String testName) {
        super(testName);
    }

    /**
     * Test of filter method, of class org.proteomecommons.io.filter.IntensityPeakListFilter.
     */
    public void testFilter() {
        System.out.println("filter");
        
        GenericPeakList peaklist = new GenericPeakList();
        GenericPeak gpa = new GenericPeak();
        GenericPeak gpb = new GenericPeak();
        GenericPeak parent = new GenericPeak();
        gpa.setIntensity(5);
        gpa.setMassOverCharge(2);
        
        gpb.setIntensity(1);
        gpb.setMassOverCharge(1);
        
        parent.setCharge(1);
        parent.setIntensity(4);
        parent.setMassOverCharge(5);
        peaklist.setParentPeak(parent);
        peaklist.setPeaks(new Peak[]{gpa, gpb});
        
        IntensityPeakListFilter instance = new IntensityPeakListFilter(1);
        
        PeakList res = instance.filter(peaklist);
        
        assertEquals(res.getParentPeak().getIntensity(), parent.getIntensity());
        assertEquals(res.getPeaks().length, 1);
        assertEquals(res.getPeaks()[0].getIntensity(), gpa.getIntensity());
        assertEquals(res.getPeaks()[0].getMassOverCharge(), gpa.getMassOverCharge());
        
    }
}

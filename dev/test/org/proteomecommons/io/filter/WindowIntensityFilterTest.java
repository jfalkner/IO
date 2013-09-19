/*
 * WindowIntensityFilterTest.java
 * JUnit based test
 *
 * Created on August 28, 2006, 1:31 AM
 */

package org.proteomecommons.io.filter;

import junit.framework.*;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 *
 * @author Jarret
 */
public class WindowIntensityFilterTest extends TestCase {
    
    public WindowIntensityFilterTest(String testName) {
        super(testName);
    }

    /**
     * Test of filter method, of class org.proteomecommons.io.filter.WindowIntensityFilter.
     */
    public void testFilter() {
        System.out.println("filter");
        
        GenericPeakList peaklist = new GenericPeakList();
        GenericPeak parent = new GenericPeak();
        parent.setCharge(1);
        parent.setIntensity(40);
        parent.setMassOverCharge(900);
        
        peaklist.setParentPeak(parent);
        
        GenericPeak gp1 = new GenericPeak();
        gp1.setIntensity(500);
        gp1.setMassOverCharge(120);
        GenericPeak gp2 = new GenericPeak();
        gp2.setIntensity(600);
        gp2.setMassOverCharge(130);
        GenericPeak gp3 = new GenericPeak();
        gp3.setIntensity(700);
        gp3.setMassOverCharge(140);
        peaklist.setPeaks(new Peak[]{gp1,gp2,gp3});
        System.out.println("original: " + peaklist);
        for(Peak p : peaklist.getPeaks()) 
            System.out.println(p);
        
        WindowIntensityFilter instance = new WindowIntensityFilter();
        PeakList filtered = instance.filter(peaklist);
        
        System.out.println("filtered: " + filtered);
        for(Peak p : filtered.getPeaks())
            System.out.println(p);
        
        assertEquals(filtered.getParentPeak().getIntensity(), parent.getIntensity());
        
        assertEquals(filtered.getPeaks().length, 2);
        assertTrue(filtered.getPeaks()[0] == gp2 || filtered.getPeaks()[0] == gp3);
        assertTrue(filtered.getPeaks()[1].getIntensity() == gp2.getIntensity() || filtered.getPeaks()[1].getIntensity() == gp3.getIntensity());
        
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}

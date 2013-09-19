package org.proteomecommons.io.bruker;

import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class CompassXportTest extends TestCase {
    
    public void testCompassXportReader() throws Exception {
        // try to conver the file
        PeakListReader plr = GenericPeakListReader.getPeakListReader("C:/Documents and Settings/Jayson/Desktop/1-4THF01.D/Analysis.yep");
        // assert not null
        assertNotNull("Expected a PeakListReader!", plr);
        try {
            // track how many peak lists there are
            int peakListCount = 0;
            for (PeakList pl = plr.getPeakList();pl != null; pl = plr.getPeakList()) {
                System.out.println("Found peak list of size: "+pl.getPeaks().length);
                peakListCount++;
            }
            assertTrue("Expected at least one peak list.", peakListCount > 0);
        } finally {
            try { plr.close(); } catch (Exception e){}
        }
    }
}

package org.proteomecommons.io.msp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.util.PeakMassOverChargeComparator;

/**
 *Tests that the WIFF reader works as expected.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MSPPeakListReaderTest extends TestCase {
    
    /**
     * Tests that the '.msp' file format is automatically picked up by the GenericPeakListReader class. The test also ensures that multiple peak lists can be read.
     */
    public void testAutoRecognizeMSPFormat() throws Exception {
        // the test file
        String name = "files/nist-msp/example.msp";
        InputStream in = MSPPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream("files/nist-msp/example.msp");
        try {
            File f = new File("example.msp");
            FileOutputStream fos = new FileOutputStream(f);
            for (int i=in.read();i!=-1;i=in.read()) {
                fos.write((byte)i);
            }
            fos.flush();
            fos.close();
            try {
                // make a reader
                PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
                assertNotNull("No peak list reader was returned!", plr);
                assertTrue("Expected instance of MSPPeakListReader.", plr instanceof MSPPeakListReader);
                try {
                    int peakListCount = 0;
                    for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
                        peakListCount++;
                    }
                    assertTrue("Should have read several peak lists.", peakListCount > 1);
                } finally {
                    try { plr.close(); } catch (Exception e){}
                }
            } finally {
                f.delete();
            }
        } finally {
            in.close();
        }
    }
    
    /**
     * Tests that the '.msp' file format is correctly read, including multiple peak lists.
     */
    public void testCorrectlyReadsData() throws Exception {
        // the test file
        String name = "files/nist-msp/example.msp";
        InputStream in = MSPPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream("files/nist-msp/example.msp");
        try {
            File f = new File("example.msp");
            FileOutputStream fos = new FileOutputStream(f);
            for (int i=in.read();i!=-1;i=in.read()) {
                fos.write((byte)i);
            }
            try {
                // make a reader
                PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
                try {
                    assertNotNull("No peak list reader was returned!", plr);
                    assertTrue("Expected instance of MSPPeakListReader.", plr instanceof MSPPeakListReader);
                    
                    {
                        // read the first peak list
                        PeakList pl = plr.getPeakList();
                        assertNotNull("Expected a peak list.", pl);
                        
                        // check all the values
                        Peak[] peaks = pl.getPeaks();
                        // sort
                        Arrays.sort(peaks, new PeakMassOverChargeComparator());
                        // assert we have 44 peaks
                        assertEquals("Expected 44 peaks.", 44, peaks.length);
                        // check for the peaks
                        assertEquals(peaks[0].getMassOverCharge(), 410.0, .1);
                        assertEquals(peaks[0].getIntensity(), 2102, .1);
                        assertEquals(peaks[1].getMassOverCharge(),543.0, .1);
                        assertEquals(peaks[1].getIntensity(), 1241, .1);
                        assertEquals(peaks[2].getMassOverCharge(),614.0, .1);
                        assertEquals(peaks[2].getIntensity(), 789, .1);
                        assertEquals(peaks[3].getMassOverCharge(),640.1, .1);
                        assertEquals(peaks[3].getIntensity(), 3827, .1);
                        assertEquals(peaks[4].getMassOverCharge(),727.3, .1);
                        assertEquals(peaks[4].getIntensity(), 898, .1);
                        assertEquals(peaks[5].getMassOverCharge(),731.6, .1);
                        assertEquals(peaks[5].getIntensity(), 3555, .1);
                        assertEquals(peaks[6].getMassOverCharge(),756.2, .1);
                        assertEquals(peaks[6].getIntensity(), 2543, .1);
                        assertEquals(peaks[7].getMassOverCharge(),811.5, .1);
                        assertEquals(peaks[7].getIntensity(), 992, .1);
                        assertEquals(peaks[8].getMassOverCharge(),874.3, .1);
                        assertEquals(peaks[8].getIntensity(), 1243, .1);
                        assertEquals(peaks[9].getMassOverCharge(),882.7, .1);
                        assertEquals(peaks[9].getIntensity(), 1045, .1);
                        assertEquals(peaks[10].getMassOverCharge(),911.3, .1);
                        assertEquals(peaks[10].getIntensity(), 1621, .1);
                        assertEquals(peaks[11].getMassOverCharge(),916.5, .1);
                        assertEquals(peaks[11].getIntensity(), 10000, .1);
                        assertEquals(peaks[12].getMassOverCharge(),917.7, .1);
                        assertEquals(peaks[12].getIntensity(), 2441, .1);
                        assertEquals(peaks[13].getMassOverCharge(),1029.6, .1);
                        assertEquals(peaks[13].getIntensity(), 3796, .1);
                        assertEquals(peaks[14].getMassOverCharge(),1043.7, .1);
                        assertEquals(peaks[14].getIntensity(), 806, .1);
                        assertEquals(peaks[15].getMassOverCharge(),1142.7, .1);
                        assertEquals(peaks[15].getIntensity(), 4492, .1);
                        assertEquals(peaks[16].getMassOverCharge(),1143.8, .1);
                        assertEquals(peaks[16].getIntensity(), 3174, .1);
                        assertEquals(peaks[17].getMassOverCharge(),1155.7, .1);
                        assertEquals(peaks[17].getIntensity(), 2599, .1);
                        assertEquals(peaks[18].getMassOverCharge(),1168.4, .1);
                        assertEquals(peaks[18].getIntensity(), 1698, .1);
                        assertEquals(peaks[19].getMassOverCharge(),1169.0, .1);
                        assertEquals(peaks[19].getIntensity(), 2140, .1);
                        assertEquals(peaks[20].getMassOverCharge(),1195.8, .1);
                        assertEquals(peaks[20].getIntensity(), 1726, .1);
                        assertEquals(peaks[21].getMassOverCharge(),1213.7, .1);
                        assertEquals(peaks[21].getIntensity(), 763, .1);
                        assertEquals(peaks[22].getMassOverCharge(),1225.2, .1);
                        assertEquals(peaks[22].getIntensity(), 1499, .1);
                        assertEquals(peaks[23].getMassOverCharge(),1242.3, .1);
                        assertEquals(peaks[23].getIntensity(), 968, .1);
                        assertEquals(peaks[24].getMassOverCharge(),1251.6, .1);
                        assertEquals(peaks[24].getIntensity(), 2880, .1);
                        assertEquals(peaks[25].getMassOverCharge(),1253.3, .1);
                        assertEquals(peaks[25].getIntensity(), 3186, .1);
                        assertEquals(peaks[26].getMassOverCharge(),1298.8, .1);
                        assertEquals(peaks[26].getIntensity(), 1877, .1);
                        assertEquals(peaks[27].getMassOverCharge(),1370.6, .1);
                        assertEquals(peaks[27].getIntensity(), 4898, .1);
                        assertEquals(peaks[28].getMassOverCharge(),1396.8, .1);
                        assertEquals(peaks[28].getIntensity(), 4384, .1);
                        assertEquals(peaks[29].getMassOverCharge(),1397.8, .1);
                        assertEquals(peaks[29].getIntensity(), 808, .1);
                        assertEquals(peaks[30].getMassOverCharge(),1398.8, .1);
                        assertEquals(peaks[30].getIntensity(), 1660, .1);
                        assertEquals(peaks[31].getMassOverCharge(),1424.8, .1);
                        assertEquals(peaks[31].getIntensity(), 1099, .1);
                        assertEquals(peaks[32].getMassOverCharge(),1443.5, .1);
                        assertEquals(peaks[32].getIntensity(), 2343, .1);
                        assertEquals(peaks[33].getMassOverCharge(),1509.5, .1);
                        assertEquals(peaks[33].getIntensity(), 5694, .1);
                        assertEquals(peaks[34].getMassOverCharge(),1511.7, .1);
                        assertEquals(peaks[34].getIntensity(), 1860, .1);
                        assertEquals(peaks[35].getMassOverCharge(),1525.3, .1);
                        assertEquals(peaks[35].getIntensity(), 892, .1);
                        assertEquals(peaks[36].getMassOverCharge(),1542.4, .1);
                        assertEquals(peaks[36].getIntensity(), 900, .1);
                        assertEquals(peaks[37].getMassOverCharge(),1604.7, .1);
                        assertEquals(peaks[37].getIntensity(), 1026, .1);
                        assertEquals(peaks[38].getMassOverCharge(),1622.8, .1);
                        assertEquals(peaks[38].getIntensity(), 3865, .1);
                        assertEquals(peaks[39].getMassOverCharge(),1624.5, .1);
                        assertEquals(peaks[39].getIntensity(), 1111, .1);
                        assertEquals(peaks[40].getMassOverCharge(),1783.2, .1);
                        assertEquals(peaks[40].getIntensity(), 4417, .1);
                        assertEquals(peaks[41].getMassOverCharge(),1786.1, .1);
                        assertEquals(peaks[41].getIntensity(), 1718, .1);
                        assertEquals(peaks[42].getMassOverCharge(),1807.8, .1);
                        assertEquals(peaks[42].getIntensity(), 2475, .1);
                        assertEquals(peaks[43].getMassOverCharge(),1809.2, .1);
                        assertEquals(peaks[43].getIntensity(), 2423, .1);
                    }
                    
                    // check the second peak list
                    {
                        // read the first peak list
                        PeakList pl = plr.getPeakList();
                        assertNotNull("Expected a peak list.", pl);
                        
                        // check all the values
                        Peak[] peaks = pl.getPeaks();
                        // sort
                        Arrays.sort(peaks, new PeakMassOverChargeComparator());
                        // assert we have 117 peaks
                        assertEquals("Expected 117 peaks.", 117, peaks.length);
                        // check a few select peaks
                        assertEquals(peaks[0].getMassOverCharge(), 168.9, .1);
                        assertEquals(peaks[0].getIntensity(), 627, .1);
                        assertEquals(peaks[116].getMassOverCharge(), 908.8, .1);
                        assertEquals(peaks[116].getIntensity(), 320, .1);
                    }
                    
                    // check the second peak list
                    {
                        // read the first peak list
                        PeakList pl = plr.getPeakList();
                        assertNotNull("Expected a peak list.", pl);
                        
                        // check all the values
                        Peak[] peaks = pl.getPeaks();
                        // sort
                        Arrays.sort(peaks, new PeakMassOverChargeComparator());
                        // assert we have 67 peaks
                        assertEquals("Expected 67 peaks.", 67, peaks.length);
                        // check a few select peaks
                        assertEquals(peaks[0].getMassOverCharge(), 415.5, .1);
                        assertEquals(peaks[0].getIntensity(), 1562, .1);
                        assertEquals(peaks[66].getMassOverCharge(), 1954.0, .1);
                        assertEquals(peaks[66].getIntensity(), 1161, .1);
                    }
                    
                } finally {
                    try { plr.close(); } catch (Exception e){}
                }
            } finally {
                f.delete();
            }
        } finally {
            in.close();
        }
    }
    
}

package org.proteomecommons.io.wiff;

import junit.framework.TestCase;

/**
 *Tests that the WIFF reader works as expected.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class WiffPeakListReaderTest extends TestCase {
    
    public void testAutoRecognizeWiffFormat() throws Exception {
        fail("Fixme.");
//        // the test file
//        String name = "files/abi/example.wiff";
//        InputStream in = WiffPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream("files/abi/example.wiff");
//        try {
//            // make a reader
//            PeakListReader plr = GenericPeakListReader.getPeakListReader(in, name);
//            assertNotNull("No peak list reader was returned!", plr);
//            try {
//                int peakListCount = 0;
//                for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
//                    peakListCount++;
//                    if (pl instanceof TandemPeakList) {
//                        TandemPeakList tpl = (TandemPeakList)pl;
//                        System.out.println("TandemPeakList: "+tpl.getParent().getMassOverCharge()+", peaks: "+tpl.getPeaks().length);
//                    } else {
//                        System.out.println("PeakList: "+pl.getPeaks().length);
//                    }
//                }
//                assertTrue("Should have read several peak lists.", peakListCount > 2);
//            } finally {
//                try { plr.close(); } catch (Exception e){}
//            }
//        } finally {
//            in.close();
//        }
    }
    
    public void testWiffPeakListReader() throws Exception {
          fail("Fixme.");
//      // the test file
//        String name = "files/abi/example.wiff";
//        // read it for conversion
//        InputStream in = WiffPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream(name);
//        try {
//            // make a reader
//            PeakListReader plr = GenericPeakListReader.getPeakListReader(in, name);
//            assertNotNull("No peak list reader was returned!", plr);
//            try {
//                int peakListCount = 0;
//                for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
//                    peakListCount++;
//                }
//                assertTrue("Should have read several peak lists.", peakListCount > 2);
//            } finally {
//                try { plr.close(); } catch (Exception e){}
//            }
//        } finally {
//            in.close();
//        }
    }
    
    
    public void testWiffPeakListReaderMultiPureStream() throws Exception {
        for (int i=0;i<2;i++) {
            testAutoRecognizeWiffFormat();
        }
    }
    
}

package org.proteomecommons.io.raw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class RawPeakListReaderTest extends TestCase {
    
    public void testAutoRecognizeRawFormat() throws Exception {
        InputStream in = RawPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream("files/thermo-raw/example.raw");
        File f = new File("temp.raw");
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        try {
            // make a reader
            PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
            try {
                int peakListCount = 0;
                for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
                    peakListCount++;
                    if (pl.getTandemCount() > 1) {
                        System.out.println("TandemPeakList: "+pl.getParentPeak().getMassOverCharge()+", peaks: "+pl.getPeaks().length);
                    } else {
                        System.out.println("PeakList: "+pl.getPeaks().length);
                    }
                }
                assertTrue("Should have read several peak lists.", peakListCount > 2);
            } finally {
                try { plr.close(); } catch (Exception e){}
            }
        } finally {
            in.close();
            f.delete();
        }
    }
    
    public void testRawPeakListReader() throws Exception {
        String name = "files/thermo-raw/example.raw";
        InputStream in = RawPeakListReaderTest.class.getClassLoader().getSystemResourceAsStream(name);
        
        File f = new File("temp.raw");
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[1000];
        for (int bytesRead=in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
            fos.write(buf, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        
        try {
            // make a reader
            PeakListReader plr = GenericPeakListReader.getPeakListReader(f.getCanonicalPath());
            try {
                int peakListCount = 0;
                for (PeakList pl = plr.getPeakList();pl!=null && peakListCount < 3;pl = plr.getPeakList()) {
                    peakListCount++;
                }
                assertTrue("Should have read several peak lists.", peakListCount > 2);
            } finally {
                try { plr.close(); } catch (Exception e){}
            }
        } finally {
            in.close();
            f.delete();
        }
    }
    
    
    public void testRawPeakListReaderMultiPureStream() throws Exception {
        for (int i=0;i<2;i++) {
            testAutoRecognizeRawFormat();
        }
    }
    
}

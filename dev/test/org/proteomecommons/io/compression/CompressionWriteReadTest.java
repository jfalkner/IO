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
package org.proteomecommons.io.compression;

import java.io.File;
import junit.framework.TestCase;
import org.proteomecommons.io.DevUtil;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class CompressionWriteReadTest extends TestCase {

    public void testCanWriteB2ZIP() throws Exception {
        testCanWrite(".bzip2");
    }
    public void testCanWriteLZMA() throws Exception {
        testCanWrite(".lzma");
    }
    public void testCanWriteGzip() throws Exception {
        testCanWrite(".gzip");
    }
    public void testCanWriteZip() throws Exception {
        testCanWrite(".zip");
    }
    
    /**
     *Helper method that tests the various compression encodings.
     */
    public void testCanWrite(String extension) throws Exception {
        File dir = new File("/todelete/IO/testCanWrite"+extension.replaceAll("\\.", "~"));
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // grab the MGF peak list from the classpath
        File file = new File(dir, "test.mgf");
        DevUtil.copyOver("test.mgf", CompressionWriteReadTest.class, file);
        
        // make a writer with a different name than the known peak list
        File compressedFile = new File(dir, "test.compressed.mgf"+extension);
        File uncompressedFile = new File(dir, "test.compressed.mgf");
        // get a writer that will auto-compress
        PeakListWriter plw = GenericPeakListWriter.getPeakListWriter(compressedFile.getCanonicalPath());
        try {
            // read in the MGF file
            PeakListReader plr = GenericPeakListReader.getPeakListReader(file.getCanonicalPath());
            try {
                int writeCount = 0;
                for (PeakList pl = plr.getPeakList(); pl != null; pl = plr.getPeakList()) {
                    writeCount ++;
                    plw.write(pl);
                }
                assertEquals("Expected to write two peak lists.", 2, writeCount);
            } finally {
                try { plr.close(); } catch (Exception e){}
            }
        } finally {
            try { plw.close(); } catch (Exception e){}
        }
        
        // check that the uncompressed temp file is deleted
        assertTrue("The temp file for compression should be deleted.", !uncompressedFile.exists());
        assertTrue("The compressed file should exist.", compressedFile.exists());
        
        // next read the peak lists back in to memory
        PeakListReader plr = GenericPeakListReader.getPeakListReader(compressedFile.getCanonicalPath());
        try {
            int readCount = 0;
            for (PeakList pl = plr.getPeakList(); pl != null; pl = plr.getPeakList()) {
                readCount++;
            }
            assertEquals("Expected to read two peak lists.", 2, readCount);
        } finally {
            try { plr.close(); } catch (Exception e){}
        }
    }
}

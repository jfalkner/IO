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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import junit.framework.TestCase;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DevUtil extends TestCase {
    public static File recursiveDelete(File dir) {
        // only delete if it exists
        if(dir.exists()) {
            if(dir.isDirectory()) {
                for(String fname : dir.list()) {
                    File ff = new File(dir, fname);
                    recursiveDelete(ff);
                }
            }
            
            if (!dir.delete()) {
                throw new RuntimeException("Can't delete "+dir);
            }
        }
        
        return dir;
    }
    
    /**
     * <p>A helper method to safely close the given OutputStream object via invoking flush() followed by close(). Any exceptions thorwn are discarded silently, including NullPointerExceptions that may be thrown if the reference is null.</p>
     * @param out OuputStream to safely close.
     */
    public static void safeClose(OutputStream out) {
        try {out.flush();} catch (Exception e){};
        try {out.close();} catch (Exception e){};
    }
    /**
     * <p>A helper method to safely close the given FileWriter object via invoking flush() followed by close(). Any exceptions thorwn are discarded silently, including NullPointerExceptions that may be thrown if the reference is null.</p>
     * @param out OuputStream to safely close.
     */
    public static void safeClose(Writer out) {
        try {out.flush();} catch (Exception e){};
        try {out.close();} catch (Exception e){};
    }
    
    public static void getBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[10000];
        for (int bytesRead=in.read(buf);bytesRead>-1;bytesRead=in.read(buf)){
            out.write(buf, 0, bytesRead);
        }
        // flush
        out.flush();
    }
    
    /**
     * <p>Safely closes an InputStream by invoking the close() method. Any errors thrown are silently discard, and no NullPointerException is thrown if the InputStream reference passed is null.</p>
     * @param in InpuStream to safely close.
     */
    public static void safeClose(InputStream in) {
        if(in != null) {
            try {in.close();} catch (Exception e){};
        }
    }
    
    public static void safeClose(Reader in) {
        if(in != null) {
            try {in.close();} catch (Exception e){};
        }
    }
    
    public static void copyOver(String resourceName, Class c, File file) throws IOException {
        // get the file
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            // load the reader/writer
            is = c.getClassLoader().getResourceAsStream(resourceName);
            fos = new FileOutputStream(file);
            // copy the content
            DevUtil.getBytes(is, fos);
        } finally {
            DevUtil.safeClose(is);
            DevUtil.safeClose(fos);
        }
    }
    
    public static final void assertPeaksAreTheSame(final Peak precursorB, final Peak precursorA) {
        assertEquals("Same precursor info expected.",precursorA.getMassOverCharge(), precursorB.getMassOverCharge(), 0.01);
        assertEquals("Same precursor info expected.",precursorA.getIntensity(), precursorB.getIntensity(), 0.01);
        assertEquals("Same precursor info expected.",precursorA.getCharge(), precursorB.getCharge(), 0.01);
        assertEquals("Same precursor info expected.",precursorA.getCentroided(), precursorB.getCentroided(), 0.01);
        assertEquals("Same precursor info expected.",precursorA.getMonoisotopic(), precursorB.getMonoisotopic(), 0.01);
        assertEquals("Same precursor info expected.",precursorA.getAveraged(), precursorB.getAveraged(), 0.01);
    }
    
    // just to keep JUnit happy
    public void testNothing() throws Exception {
        // noop
    }
}

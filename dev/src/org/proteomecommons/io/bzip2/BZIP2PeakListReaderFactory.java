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
package org.proteomecommons.io.bzip2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.tools.bzip2.CBZip2InputStream;

import org.proteomecommons.io.*;
import org.proteomecommons.io.util.DeleteFilesPeakListReaderWrapper;

/**
 * This is a helper PeakListReaderFactory class that knows how to automatically decompress GZIP encoded peak lists.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class BZIP2PeakListReaderFactory implements PeakListReaderFactory {
    
    /**
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        try {
            // unzip the files to that directory
            FileInputStream is = new FileInputStream(filename);
            // read the input stream as ZIP
            CBZip2InputStream compressedInputStream = new CBZip2InputStream(is);
            
            // figure our the real name
            String realName = filename.split("\\.bzip2")[0];
            
            // send the output to disk
            FileOutputStream fos = new FileOutputStream(realName);
            try {
                byte[] buffer = new byte[10000];
                for (int bytesRead = compressedInputStream.read(buffer);bytesRead != -1;bytesRead = compressedInputStream.read(buffer)) {
                    fos.write(buffer, 0, bytesRead);
                }
            } finally {
                try { compressedInputStream.close(); } catch (Exception e){}
                try { is.close(); } catch (Exception e){}
                try { fos.flush(); } catch (Exception e){}
                try { fos.close(); } catch (Exception e){}
            }
            
            // return the peak list reader
            PeakListReader plr = GenericPeakListReader.getPeakListReader(realName);
            DeleteFilesPeakListReaderWrapper plrw = new DeleteFilesPeakListReaderWrapper(plr, new File[]{new File(realName)});
            
            // return the wrapped peak list
            return plrw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Return the regular expression for what this reader can handle.
     *
     * @return
     */
    public String getRegularExpression() {
        return ".*\\.bzip2";
    }
}

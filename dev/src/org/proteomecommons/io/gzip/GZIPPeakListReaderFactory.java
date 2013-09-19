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
package org.proteomecommons.io.gzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;

import org.proteomecommons.io.*;
import org.proteomecommons.io.util.DeleteFilesPeakListReaderWrapper;

/**
 * This is a helper PeakListReaderFactory class that knows how to automatically decompress GZIP encoded peak lists.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GZIPPeakListReaderFactory implements PeakListReaderFactory {
    
    /**
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        // make a temp directory
        File dir = new File("temp_gzip_wrapper");
        for (int i=0;dir.exists();i++) {
            dir = new File("temp_gzip_wrapper_"+i);
        }
        dir.mkdirs();
        
        try {
            // unzip the files to that directory
            FileInputStream is = new FileInputStream(filename);
            // read the input stream as ZIP
            GZIPInputStream gis = new GZIPInputStream(is);
            
            // figure our the real name
            String realName = filename.split("\\.gz")[0];
            // check for '.gzip'
            if (filename.split("\\.gzip").length > 1) {
                realName = filename.split("gzip")[0];
            }
            
            // try to get a peak list reader factory
            PeakListReaderFactory plrf = GenericPeakListReader.getPeakListReaderFactory(realName);
            if (plrf == null) {
                throw new RuntimeException("Can't find the file format "+realName);
            }
            
            // extract the file
            File file = new File(dir, realName.replaceAll("C:\\\\", ""));
            // make any directories required
            file.getParentFile().mkdirs();
            // send the output to disk
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[10000];
            for (int bytesRead = gis.read(buffer);bytesRead != -1;bytesRead = gis.read(buffer)) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
            fos.close();
            
            // return the peak list reader
            PeakListReader plr = plrf.newInstance(file.getCanonicalPath());
            DeleteFilesPeakListReaderWrapper plrw = new DeleteFilesPeakListReaderWrapper(plr, new File[]{dir});
            
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
        return ".*gz|.*gzip";
    }
}

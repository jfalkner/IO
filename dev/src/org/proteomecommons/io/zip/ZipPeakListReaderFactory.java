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
package org.proteomecommons.io.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.proteomecommons.io.*;
import org.proteomecommons.io.util.DeleteFilesPeakListReaderWrapper;

/**
 * This is a factory for creating custom ZIP readers. The class is used to
 * provide seamless support for reading a known format that has been compressed
 * using ZIP format. Simply pass the compressed file and this class will take
 * care of uncompressing it and parsing the contents of the file using the
 * appropriate peak list reader object.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ZipPeakListReaderFactory implements PeakListReaderFactory {
    
    /**
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        try {
            // unzip the files to that directory
            FileInputStream is = new FileInputStream(filename);
            // read the input stream as ZIP
            ZipInputStream zis = new ZipInputStream(is);
            // get the appropriate file to handle it
            for (ZipEntry ze = zis.getNextEntry();ze != null; ze = zis.getNextEntry()) {
                try {
                    // split to min
                    String name = ze.getName();
                    String[] split = name.split("/");
                    if (split[0].equals(".")) {
                        name = split[1];
                    } else {
                        name = split[0];
                    }
                    // extract the file
                    File file = new File(name);
                    
                    // try to get a peak list reader factory
                    PeakListReaderFactory plrf = GenericPeakListReader.getPeakListReaderFactory(ze.getName());
                    
                    // send the output to disk
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[10000];
                    for (int bytesRead = zis.read(buffer);bytesRead != -1;bytesRead = zis.read(buffer)) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.flush();
                    fos.close();
                    
                    // return the peak list reader
                    PeakListReader plr = plrf.newInstance(file.getCanonicalPath());
                    DeleteFilesPeakListReaderWrapper plrw = new DeleteFilesPeakListReaderWrapper(plr, new File[]{file});
                    
                    // return the wrapped peak list
                    return plrw;
                }catch (UnknownFileFormatException ufe) {
                    // noop
                }
            }
            
            
            throw new RuntimeException("Can't find any files to read!");
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
        return ".*\\.zip";
    }
}
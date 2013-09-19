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
import java.util.zip.ZipOutputStream;
import org.proteomecommons.io.CompressedPeakListWriter;
import org.proteomecommons.io.GenericPeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ZipPeakListWriter extends CompressedPeakListWriter {
    // extensions for the compression
    String[] extensions = new String[] {
        ".zip"
    };
    String extensionToUse = null;
    File tempFile;
    
    public ZipPeakListWriter(String filename) {
        super(filename);
        
        // remove the extension
        for (String extension : extensions) {
            if (filename.toLowerCase().endsWith(extension)) {
                extensionToUse = extension;
                break;
            }
        }
        
        // if no extension was found, raise an error
        if (extensionToUse == null) {
            throw new RuntimeException(filename+" does not have a valid LZMA extension! Expected .lzma");
        }
        
        // trim the filename
        filename = filename.substring(0, filename.length()-extensionToUse.length());

        // set up the temp file
        File file = new File(filename);
        File dir = file.getParentFile();
        // make the temp file
        tempFile = new File(dir, "_todelete."+file.getName());
        tempFile.deleteOnExit();
        
        // get an embedded peak list writer
        setEmbedded(GenericPeakListWriter.getPeakListWriter(tempFile.getAbsolutePath()));
    }
    
    /**
     *Here is where the magic happens. This close() method saves the normal peak list file, makes a compressed form of it named with a new extension, then deletes the original.
     */
    public void close() {
        // close the embedded
        getEmbedded().close();
        
        // figure out where the compressed file should be
        File compressedFile = new File(getFilename());
        
        // make a zip
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(compressedFile);
            zos = new ZipOutputStream(fos);
            fis = new FileInputStream(tempFile);
            // write the entry
            ZipEntry ze = new ZipEntry(tempFile.getName().split("_todelete.")[1]);
            ze.setSize(tempFile.length());
            ze.setComment("Created by the ProteomeCommons.org IO Framework - http://www.proteomecommons.org");
            zos.putNextEntry(ze);
            byte[] buf = new byte[10000];
            for (int bytesRead = fis.read(buf);bytesRead != -1; bytesRead = fis.read(buf)) {
                zos.write(buf, 0, bytesRead);
            }
            zos.closeEntry();
            zos.finish();
            zos.flush();
            zos.close();
        } catch (Exception e){
            throw new RuntimeException("Can't create ZIP file "+compressedFile, e);
        } finally {
            try { fos.flush(); } catch (Exception e){}
            try { fis.close(); } catch (Exception e){}
            try { fos.close(); } catch (Exception e){}
            
            // most importantly delete the file
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }
}

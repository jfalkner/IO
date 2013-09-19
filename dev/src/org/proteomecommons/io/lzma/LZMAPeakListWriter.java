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
package org.proteomecommons.io.lzma;

import SevenZip.LzmaAlone;
import java.io.File;
import org.proteomecommons.io.CompressedPeakListWriter;
import org.proteomecommons.io.GenericPeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class LZMAPeakListWriter extends CompressedPeakListWriter {
    // extensions for the compression
    String[] extensions = new String[] {
        ".lzma"
    };
    String extensionToUse = null;
    File tempFile;
    
    public LZMAPeakListWriter(String filename) {
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
        try {
            // use the lzma alone class for help
            LzmaAlone.main(new String[] {"e", tempFile.getCanonicalPath(), compressedFile.getCanonicalPath()});
        } catch (Exception ex) {
            throw new RuntimeException("Can't perform LZMA compression.", ex);
        }
        
        // most importantly delete the file
        if (!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }
}

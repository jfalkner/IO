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

package org.proteomecommons.io.util;

import java.io.File;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;

/**
 *<p>A custom peak list reader that will automatically delete the list of files passed in to its constructor when the close() method is invoked.</p>
 *<p>This class is just a wrapper that nicely tries to clean up temporary files.</p>
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DeleteFilesPeakListReaderWrapper implements PeakListReader {
    // the files to close
    private File[] toDeleteOnClose;
    
    // the reader to wrap
    PeakListReader wrapped;
    
    public DeleteFilesPeakListReaderWrapper(PeakListReader plr, File[] toDeleteOnClose) {
        this.wrapped = plr;
        this.toDeleteOnClose = toDeleteOnClose;
    }
    
    public void close() {
        try { wrapped.close(); } catch (Exception e){}
        
        // try to delete the temp files
        for (File f : toDeleteOnClose) {
            try { recursivelyDelete(f); } catch (Exception e){}
        }
    }
    
    public void setName(String name) {
        wrapped.setName(name);
    }
    
    public PeakList getPeakList() {
        return wrapped.getPeakList();
    }
    
    public String getName() {
        return wrapped.getName();
    }
    
    // helper method to recursively delete.
    private void recursivelyDelete(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                recursivelyDelete(file);
            }
        } else {
            f.delete();
        }
    }
}
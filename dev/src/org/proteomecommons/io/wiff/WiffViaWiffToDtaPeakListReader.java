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

package org.proteomecommons.io.wiff;

import java.io.File;
import org.proteomecommons.io.mzxml.MzXMLPeakListReader;

/**
 *<p>A custom WIFF file reader that uses the WIFFToDTA tool. See the documentation included with the IO framework about ensuring that you have the required native Windows libraries to use this code.</p>
 *<p>This class is just a wrapper that nicely tries to clean up temporary files.</p>
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class WiffViaWiffToDtaPeakListReader extends MzXMLPeakListReader {
    private File[] toDeleteOnClose;
    
    public WiffViaWiffToDtaPeakListReader(String filename, File[] toDeleteOnClose) {
        super(filename);
        this.toDeleteOnClose = toDeleteOnClose;
    }
    
    public void close() {
        try { super.close(); } catch (Exception e){}
        
        // try to delete the temp files
        for (File f : toDeleteOnClose) {
            try {f.delete(); } catch (Exception e){}
        }
    }
}

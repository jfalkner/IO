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
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;
import org.proteomecommons.io.util.DeleteFilesPeakListReaderWrapper;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class LZMAPeakListReaderFactory  implements PeakListReaderFactory {
    /**
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        try {
            // figure our the real name
            String realName = filename.split("\\.lzma")[0];
            
            // try to get a peak list reader factory
            PeakListReaderFactory plrf = GenericPeakListReader.getPeakListReaderFactory(realName);
            if (plrf == null) {
                throw new RuntimeException("Can't find the file format "+realName);
            }
                        
            // do the extraction
            LzmaAlone la = new LzmaAlone();
            la.main(new String[]{"d", filename, realName});
            
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
        return ".*\\.lzma";
    }
}

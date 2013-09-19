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
package org.proteomecommons.io.mzdata;

import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.UnknownFileFormatException;

/**
 *A mock reader that will properly read all versions of mzXML. This way wrapper code doesn't have to know what version of mzXML is being produced.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MzDataPeakListReader extends GenericPeakListReader {
    private PeakListReader embedded;
    
    public MzDataPeakListReader(String filename) {
        super(filename);
        try {
            // set the embedded reader
            embedded = GenericPeakListReader.getPeakListReader(filename);
        } catch (UnknownFileFormatException ex) {
            throw new RuntimeException("Can't find a reader for "+filename, ex);
        }
    }
    
    public void close() {
        getEmbedded().close();
    }
    
    public PeakList getPeakList() {
        return getEmbedded().getPeakList();
    }
    
    public PeakListReader getEmbedded() {
        return embedded;
    }
}

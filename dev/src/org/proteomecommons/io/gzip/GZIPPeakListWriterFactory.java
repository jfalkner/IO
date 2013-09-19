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

import org.proteomecommons.io.PeakListWriter;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 * A factory that produces writers that will automatically compress content in GZIP format.
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GZIPPeakListWriterFactory implements PeakListWriterFactory {
    public PeakListWriter newInstance(String filename) {
        return new GZIPPeakListWriter(filename);
    }
    
    public String getName() {
        return "GZIP (Compression Format)";
    }
    
    public String getFileExtension() {
        return ".gzip";
    }
    
    public String getRegex() {
        return ".*\\.gz|.*\\.gzip";
    }
}

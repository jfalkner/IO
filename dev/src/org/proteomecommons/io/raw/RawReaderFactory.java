package org.proteomecommons.io.raw;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

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


/**
 * Factory for creating Thermo Finnigan .RAW file format readers.
 * @author Takis Papoulias - panagiot@umich.edu
 *
 */
public class RawReaderFactory implements PeakListReaderFactory {
    public PeakListReader newInstance(String filename) {
        return new RawReader(filename);
    }
}
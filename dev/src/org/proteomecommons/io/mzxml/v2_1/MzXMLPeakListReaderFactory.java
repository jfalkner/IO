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
package org.proteomecommons.io.mzxml.v2_1;


import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.xml.XMLPeakListReaderFactory;

/**
 * A custom factory for generating MZXMLPeakListReader classes.
 *
 * @author Jarret Falkner  - jar@cs.washington.edu
 */
public class MzXMLPeakListReaderFactory implements XMLPeakListReaderFactory {
    
    /**
     *
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        return new MzXMLPeakListReader(filename);
    }
    
    /**
     * Returns the schema that identifies MZXML 2.1: "http://sashimi.sourceforge.net/schema_revision/mzXML_2.1"
     */
    public String getSchemaURIRegex() {
        return ".*http://sashimi.sourceforge.net/schema_revision/mzXML_2.1.*";
    }
}
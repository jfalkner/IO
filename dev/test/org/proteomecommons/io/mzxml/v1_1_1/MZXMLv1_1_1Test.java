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
package org.proteomecommons.io.mzxml.v1_1_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.proteomecommons.io.DevUtil;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.GenericPeakListWriter;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;
import org.proteomecommons.io.PeakListWriterFactory;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MZXMLv1_1_1Test extends TestCase {
    
    public void testCanLoadClass() throws Exception {
        File dir = new File("/todelete/mzxml/1_1_1/testCanLoadClass");
        DevUtil.recursiveDelete(dir);
        dir.mkdirs();
        
        // make a fake file
        File file = new File(dir, "example.1.1.1.mzxml.xml");
        
        // check the reader
        PeakListReaderFactory plr = GenericPeakListReader.getPeakListReaderFactory(file.getCanonicalPath());
        assertTrue("Expected to load the mzXML 1.1.1 factory.", plr instanceof org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListReaderFactory);
        
        // check the reader
        PeakListWriterFactory plw = GenericPeakListWriter.getPeakListWriterFactory(file.getCanonicalPath());
        assertTrue("Expected to load the mzXML 1.1.1 factory.", plw instanceof org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListWriterFactory);
    }
}

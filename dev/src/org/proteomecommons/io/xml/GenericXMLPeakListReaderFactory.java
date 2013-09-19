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
package org.proteomecommons.io.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 * A custom factory for generating MZXMLPeakListReader classes.
 *
 * @author Jarret Falkner  - jar@cs.washington.edu
 */
public class GenericXMLPeakListReaderFactory implements PeakListReaderFactory {
    // map of factories
    private static Set<XMLPeakListReaderFactory> xmlFactories = new HashSet();
    static {
        // add the mzXML 1.1.1 reader
        addXMLPeakListReaderFactory(new org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListReaderFactory());
        // add the mzXML 2.0 reader
        addXMLPeakListReaderFactory(new org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReaderFactory());
        // add the mzXML 2.1 reader
        addXMLPeakListReaderFactory(new org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListReaderFactory());
        // add the mzData 1.05 reader
        addXMLPeakListReaderFactory(new org.proteomecommons.io.mzdata.v1_05.MzDataPeakListReaderFactory());
        // add the mzData 1.04 reader
        addXMLPeakListReaderFactory(new org.proteomecommons.io.mzdata.v1_04.MzDataPeakListReaderFactory());
    }
    
    public static synchronized void addXMLPeakListReaderFactory(XMLPeakListReaderFactory xml) {
        xmlFactories.add(xml);
    }
    
    public static synchronized void removeXMLPeakListReaderFactory(XMLPeakListReaderFactory xml) {
        xmlFactories.remove(xml);
    }
    
    public static synchronized void clearXMLPeakListReaderFactories() {
        xmlFactories.clear();
    }
    
    /**
     *
     * @see org.proteomecommons.io.PeakListReaderFactory#newInstance(java.io.InputStream)
     */
    public PeakListReader newInstance(String filename) {
        
        // find the name that matches
        for (XMLPeakListReaderFactory key : xmlFactories) {
            // get the regex
            String regex = key.getSchemaURIRegex();
            // make a pattern
            Pattern p = Pattern.compile(regex);
            // try matching each line from the file
            FileReader fr = null;
            BufferedReader br = null;
            try {
                // make the readers
                fr = new FileReader(filename);
                br = new BufferedReader(fr);
                // stop after 10 lines
                int lineCount = 0;
                // get lines until a schema is found
                for (String line = br.readLine(); lineCount < 20 && line!= null; line = br.readLine()) {
                    lineCount++;
                    // make a matcher
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
//                        System.out.println("Returning: "+key.newInstance(filename));
                        return key.newInstance(filename);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
            } finally {
                // safely close the readers
                try { fr.close(); } catch (Exception e){}
                try { br.close(); } catch (Exception e){}
            }
        }
        
        // throw a file format exception
        throw new RuntimeException("Can't file a reader for "+filename);
    }
}

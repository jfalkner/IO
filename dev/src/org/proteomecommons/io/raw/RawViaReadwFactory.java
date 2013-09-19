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
package org.proteomecommons.io.raw;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 * Factory for creating Thermo Finnigan .RAW file format readers.
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class RawViaReadwFactory implements PeakListReaderFactory {
    // config for where the ReAdW.exe is
    private static File readwLocation = new File("C:/Program Files/ProteomeCommons.org/IO/ReAdW.exe");
    
    public PeakListReader newInstance(String filename) {
        try {
            // save the IO to a file
            File inputFile = new File(filename);
            
            // pick a file for the conversion
            String path = inputFile.getCanonicalPath();
            // split on .raw to remove the extension
            String[] pathSplit = path.split("\\.raw");
            File outputFile = new File(pathSplit[0]+ ".mzXML");
            outputFile.deleteOnExit();
            
            // check for ReAdW.exe
            if (!getReadwLocation().exists()) {
                // try a known location
                File knownLocation = new File("C:/Program Files/ProteomeCommons.org/IO/ReAdW.exe");
                // check the known location
                if (knownLocation.exists()) {
                    setReadwLocation(knownLocation);
                } else {
                    // fall back on a can't file file exception
                    throw new CantLocateReAdWExecutableException(getReadwLocation());
                }
            }
            
            // use ReAdW to conver to mzXML
            String[] commands = new String[]{getReadwLocation().getCanonicalPath(), inputFile.getCanonicalPath(), "c", outputFile.getCanonicalPath()};
            // execute
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(commands);
            
//            // debug output
//            InputStream pipeIn = p.getInputStream();
//            try {
//                for (int i=pipeIn.read();i!=-1;i=pipeIn.read()) {
//                    System.out.print((char)i);
//                }
//            } finally {
//                pipeIn.close();
//            }
            
            // wait for it....
            p.waitFor();
            
            // check that the output file exists
            if (!outputFile.exists()) {
                // fall back to the input file minus .raw plus .mzxml
                String fallback = inputFile.getName().split("\\.raw|\\.RAW")[0];
                File fallbackFile = new File(inputFile.getParentFile(), fallback+".mzXML");
                if (fallbackFile.exists()) {
                    outputFile = fallbackFile;
                }
            }
            
            // send the resources and politely ask for them to be deleted
            return new RawViaReadwPeakListReader(outputFile.getCanonicalPath(), new File[]{outputFile});
        } catch (InterruptedException ex) {
            throw new RuntimeException("Conversion was prematurely ended!");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static File getReadwLocation() {
        return readwLocation;
    }
    
    public static void setReadwLocation(File aReadwLocation) {
        readwLocation = aReadwLocation;
    }
}
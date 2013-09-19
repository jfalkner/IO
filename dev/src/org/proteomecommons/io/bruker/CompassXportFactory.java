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
package org.proteomecommons.io.bruker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 * Factory for creating Bruker file format readers for .baf, fid, and .yep.
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public abstract class CompassXportFactory implements PeakListReaderFactory {
    // config for where the ReAdW.exe is
    private static File compassXportLocation = new File("C:/Program Files/ProteomeCommons.org/IO/CompassXport.exe");
    
    public PeakListReader newInstance(String filename) {
        try {
            File inputFile = new File(filename);
            
            // pick a file for the conversion
            String path = inputFile.getCanonicalPath();
            String[] split = path.split("\\.");

            // make an output file with the same name, but the .mzXML extension
            File outputFile = new File(path.substring(0, path.length()-split[split.length-1].length())+ ".mzXML");
            outputFile.deleteOnExit();
            
            // file for CompassXport.exe
            if (!compassXportLocation.exists()) {
                File check = new File("C:/Program Files/Common Files/Bruker Daltonik/AIDA/export/CompassXport.exe");
                if (check.exists()) {
                    compassXportLocation = check;
                }
            }
            
            // if no program exists, throw an exception
            if (!compassXportLocation.exists()) {
                throw new CantLocateCompassXportExecutableException(compassXportLocation);
            }
            
            // use CompassXport to convert to mzXML
            String[] commands = new String[]{compassXportLocation.getCanonicalPath(), "-log all", "-a", inputFile.getCanonicalPath(), "-o", outputFile.getCanonicalPath()};
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
            
            // return a mzXML reader
            FileInputStream fis = new FileInputStream(outputFile);
            // send the resources and politely ask for them to be deleted
            return new CompassXportPeakListReader(outputFile.getCanonicalPath(), new File[]{outputFile});
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    public static File getCompassXportLocation() {
        return compassXportLocation;
    }

    public static void setCompassXportLocation(File aCompassXportLocation) {
        compassXportLocation = aCompassXportLocation;
    }
}
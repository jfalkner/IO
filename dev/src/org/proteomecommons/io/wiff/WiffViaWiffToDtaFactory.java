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
import java.io.FileInputStream;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 *<p>A wrapping PeakListReader instance that will convert WIFF files using the wiff2dta tool.</p>
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class WiffViaWiffToDtaFactory implements PeakListReaderFactory {
    public PeakListReader newInstance(String filename) {
        try {
            // save the IO to a file
            File inputFile = new File(filename);
            
            // pick a file for the conversion
            String path = inputFile.getCanonicalPath();
            File outputFile = new File(path.substring(0, path.length()-5)+ ".mzXML");
            outputFile.deleteOnExit();
            
            // use the runtime parameter for the wiff2dta executable
            String executableName = System.getProperty("wiff2dta");
            File program = new File("wiff2dta.exe");
            if (executableName != null) {
                program = new File(executableName);
            }
            // fall back on the known versions, assume in classpath
            if (!program.exists()) {
                program = new File("wiff2dta_1108_analyst141.exe");
            }
            if (!program.exists()) {
                program = new File("wiff2dta_1108_analyst14.exe");
            }
            if (!program.exists()) {
                program = new File("wiff2dta_1108_analystqs10.exe");
            }
            if (!program.exists()) {
                program = new File("wiff2dta_1108_analystqs11.exe");
            }
            // finally try for the abstract name wiff2dta.exe
            if (!program.exists()) {
                program = new File("wiff2dta.exe");
            }
            
            // use wiff2dta to conver to mzxml
            String[] commands = new String[]{program.getCanonicalPath(), "/auto", "/wifffile", inputFile.getCanonicalPath(), "/outputmzxml", "1"};
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
            return new WiffViaWiffToDtaPeakListReader(filename, new File[]{outputFile});
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
}

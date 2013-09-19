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

package org.proteomecommons.io.fasta.hupopsi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.proteomecommons.io.fasta.AbstractFASTAWriter;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class HUPOPSIFASTAWriter extends AbstractFASTAWriter {
    
    private List <FASTAReader> orderedReaderList = new LinkedList <FASTAReader> ();
    private Map <FASTAReader,DBComponent> components = new HashMap <FASTAReader,DBComponent> ();
    
    public HUPOPSIFASTAWriter(String filename) {
        super(filename);
    }
    
    /**
     * @param FASTAReader
     */
    public void beginReader(FASTAReader reader) {
        if (components.containsKey(reader)) {
            return;
        }
        DBComponent component = new DBComponent();
        component.setId(components.size() + 1);
        orderedReaderList.add(reader);
        components.put(reader, component);
    }
    
    /**
     * @param FASTAProtein
     * @throws IOException
     */
    public void write(FASTAProtein protein) {
        try {
            try {
                String headerLine = '>' + protein.getAccession();
                for (String header : protein.getHeaders()) {
                    String value = protein.getValueOfHeader(header);
                    if (value.contains(" ")) {
                        headerLine = headerLine + " \\" + header + "=\"" + value + "\"";
                    } else {
                        headerLine = headerLine + " \\" + header + "=" + value;
                    }
                }
                
                // write the header line
                getFileWriter().append(headerLine + '\n');
                
                // write the sequence
                int count = 0;
                for (char c : protein.getSequence().toCharArray()) {
                    count++;
                    getFileWriter().append(c);
                    // limit each line of the sequence to 75 characters
                    if (count % 75 == 0) {
                        getFileWriter().append('\n');
                    }
                }
                
                if (count % 75 != 0) {
                    getFileWriter().append('\n');
                }
                
                // log this info in the latest component
                if (orderedReaderList.size() > 0) {
                    DBComponent component = components.get(orderedReaderList.get(orderedReaderList.size() - 1));
                    component.setNumberOfEntries(component.getNumberOfEntries() + 1);
                }
            } finally {
                getFileWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @param FASTAProtein
     * @param String
     * @throws IOException
     */
    public void writeReverse(FASTAProtein protein, String signifyReverse) {
        try {
            try {
                String headerLine = '>' + protein.getAccession() + signifyReverse;
                for (String header : protein.getHeaders()) {
                    String value = protein.getValueOfHeader(header);
                    if (value.contains(" ")) {
                        headerLine = headerLine + " \\" + header + "=\"" + value + "\"";
                    } else {
                        headerLine = headerLine + " \\" + header + "=" + value;
                    }
                }
                
                // write the header line
                getFileWriter().append(headerLine + '\n');
                
                // write the sequence in reverse
                char[] sequence = protein.getSequence().toCharArray();
                int count = 0;
                for (int i = sequence.length - 1; i >= 0; i--) {
                    count++;
                    getFileWriter().append(sequence[i]);
                    // limit each line of the sequence to 75 characters
                    if (count % 75 == 0 && i != 0) {
                        getFileWriter().append('\n');
                    }
                }
                
                getFileWriter().append('\n');
                
                // log this info in the latest component
                if (orderedReaderList.size() > 0) {
                    DBComponent component = components.get(orderedReaderList.get(orderedReaderList.size() - 1));
                    component.setNumberOfEntries(component.getNumberOfEntries() + 1);
                }
            } finally {
                getFileWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Finish up.
     */
    public void close() {
        // close the old stream
        try {
            getFileWriter().flush();
            getFileWriter().close();
        } catch (Exception e) {
            // do nothing
        }
        
        // copy everything with the header to a temp file, then back
        File tmpFile = null;
        try {
            tmpFile = new File("tmpFile");
            FileWriter tmpFileWriter = new FileWriter(tmpFile);
            try {
                // write the header
                for (FASTAReader reader : orderedReaderList) {
                    DBComponent component = components.get(reader);
                    for (String header : component.getHeaders()) {
                        String line = "#\\" + header + "=" + component.getValueOfHeader(header) + "\n";
                        tmpFileWriter.append(line);
                    }
                    tmpFileWriter.append("#\n");
                }
                
                // write the body of the old file to the tmp file
                FileReader fr = null;
                try {
                    fr = new FileReader(getFileName());
                    while (fr.ready()) {
                        tmpFileWriter.append((char) fr.read());
                    }
                } finally {
                    fr.close();
                }
            } catch (Exception e) {
                tmpFileWriter.flush();
                tmpFileWriter.close();
            }
            
            FileWriter fw = null;
            FileReader fr = null;
            // copy the complete temp file to the regular output location
            try {
                fw = new FileWriter(getFileName());
                fr = new FileReader(tmpFile);
                
                while (fr.ready()) {
                    fw.append((char) fr.read());
                }
            } finally {
                fw.flush();
                fw.close();
                fr.close();
            }
            
        } catch (Exception e) {
            // do nothing
        } finally {
            try {
                tmpFile.delete();
            } catch (Exception ee) {
                // do nothing
            }
        }
    }
    
}

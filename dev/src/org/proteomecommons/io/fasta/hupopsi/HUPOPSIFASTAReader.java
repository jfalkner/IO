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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.proteomecommons.io.fasta.FASTAProtein;
import org.proteomecommons.io.fasta.FASTAReader;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class HUPOPSIFASTAReader extends FASTAReader {
    
    private List <DBComponent> components = new ArrayList <DBComponent> ();
    
    /**
     * Public constructor where the InputStream instance is assumed to be the
     * FASTA sequence.
     *
     * @param is
     */
    public HUPOPSIFASTAReader(InputStream is) {
        setInputStream(is);
        
        try {
            // read the header block
            while (getInputStream().read() == '#') {
                
                DBComponent component = new DBComponent();
                
                // read the header item name and value
                String name = "", value = "";
                for (int i = getInputStream().read(); i != -1 && i != '='; i = getInputStream().read()) {
                    name = name + (char) i;
                }
                for (int i = getInputStream().read(); i != -1 && i != '\n'; i = getInputStream().read()) {
                    value = value + (char) i;
                }
                
                // remove the '\' character from the start of the name
                name = name.substring(1);
                
                component.setHeaderItem(name, value);
                components.add(component);
                
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get the next protein, or null if there is no more proteins.
     *
     * @return The next protein from the FASTA file or null if none or left.
     */
    public FASTAProtein next() {
        try {
            
            // get the name
            String headerLine = "";
            for (int i = getInputStream().read(); i != -1 && i != '\n'; i = getInputStream().read()) {
                headerLine = headerLine + (char) i;
            }
            
            // get the sequence
            String sequence = "";
            for (int i = getInputStream().read(); i != -1 && i != '>'; i = getInputStream().read()) {
                // skip whitespace
                if (i < 'A' || i > 'Z') {
                    continue;
                }
                // check for non-valid fasta characters
                if (i=='Z'||i == 'X' || i== 'J'|| i=='O' || i == 'B'){
                    continue;
                }
                sequence = sequence + (char) i;
            }
            
            // if at the end, skip
            if (sequence.length() == 0 && headerLine.length() == 0) {
                return null;
            }
            
            // make sure we did not get the header line character
            if (headerLine.charAt(0) == '>') {
                headerLine = headerLine.substring(1);
            }
            
            // make the protein
            FASTAProtein protein = null;
            
            if (headerLine.indexOf(" \\") == -1) {
                protein = new FASTAProtein(headerLine, sequence, "");
            } else {
                try {
                    protein = new FASTAProtein(headerLine.substring(0, headerLine.indexOf(" \\")), sequence, headerLine.substring(headerLine.indexOf(" \\")));
                    headerLine = headerLine.substring(headerLine.indexOf(" \\") + 2);
                    
                    String itemName, itemValue;
                    while (headerLine.indexOf(" \\") != -1) {
                        itemName = headerLine.substring(0, headerLine.indexOf('='));
                        headerLine = headerLine.substring(headerLine.indexOf('=') + 1);
                        if (headerLine.charAt(0) == '\"') {
                            itemValue = headerLine.substring(1, headerLine.indexOf("\" \\"));
                            headerLine = headerLine.substring(headerLine.indexOf("\" \\") + 3);
                        } else {
                            itemValue = headerLine.substring(0, headerLine.indexOf(" \\"));
                            headerLine = headerLine.substring(headerLine.indexOf(" \\") + 2);
                        }
                        protein.setHeaderItem(itemName, itemValue);
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
            
            return protein;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public List <DBComponent> getComponents() {
        return components;
    }
    
}
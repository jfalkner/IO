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

package org.proteomecommons.io.fasta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.proteomecommons.jaf.Protein;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class FASTAProtein extends Protein {
    
    private String headerLineAfterAC = "";
    private Map <String,String> headerLineItems = new HashMap <String,String> ();
    
    public FASTAProtein(String accession, String sequence, String headerLineAfterAC) {
        super(accession, sequence);
        setHeaderItem("LENGTH", String.valueOf(sequence.length()));
        setDescription("");
        this.headerLineAfterAC = headerLineAfterAC;
    }
    
    public String getHeaderLineAfterAC() {
        return this.headerLineAfterAC;
    }
    
    public String getValueOfHeader(String name) {
        return headerLineItems.get(name);
    }
    
    public void setHeaderItem(String name, String value) {
        headerLineItems.put(name.trim(), value.trim());
    }
    
    public Set <String> getHeaders() {
        return headerLineItems.keySet();
    }
    
    public String getAccession() {
        return super.getName();
    }
    
    public String getDescription() {
        return getValueOfHeader("DE");
    }
    
    public void setDescription(String description) {
        setHeaderItem("DE", description);
    }
    
    /*
     * This is a rough approximation of the size of this protein on disk
     */
    public long numberOfCharacters() {
        long num = 0;
        // the accession
        num += getName().length();
        // the headers
        for (String header : getHeaders()) {
            num += header.length();
            num += getValueOfHeader(header).length();
        }
        // the sequence
        num += getSequence().length();
        return num;
    }
    
}

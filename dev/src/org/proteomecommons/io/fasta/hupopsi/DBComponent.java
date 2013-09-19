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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author James "Augie" Hill - augie@828productions.com
 */
public class DBComponent {
    
    private Map <String,String> headerItems = new HashMap <String,String> ();
    
    public DBComponent() {
        setHeaderItem("DbComponent", "-1");
        setHeaderItem("NumberOfEntries", "0");
    }
    
    public Set <String> getHeaders() {
        return headerItems.keySet();
    }
    
    public String getValueOfHeader(String name) {
        return headerItems.get(name);
    }
    
    public void setHeaderItem(String name, String value) {
        headerItems.put(name.trim(), value.trim());
    }
    
    public void setId(long id) {
        setHeaderItem("DbComponent", String.valueOf(id));
    }
    
    public long getNumberOfEntries() {
        return Long.valueOf(getValueOfHeader("NumberOfEntries"));
    }
    
    public void setNumberOfEntries(long num) {
        setHeaderItem("NumberOfEntries", String.valueOf(num));
    }
    
}

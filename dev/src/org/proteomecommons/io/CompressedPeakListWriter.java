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
package org.proteomecommons.io;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public abstract class CompressedPeakListWriter implements PeakListWriter {
    private PeakListWriter embedded;
    private String filename;
    
    public CompressedPeakListWriter(String filename) {
        this.setFilename(filename);
    }
    
    public void write(PeakList peaklist) {
        getEmbedded().write(peaklist);
    }
    
    /**
     * This is where the magic happens. A normal file is written then a compressed is made then the normal is deleted.
     */
    public abstract void close();
    
    public PeakListWriter getEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(PeakListWriter embedded) {
        this.embedded = embedded;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
}

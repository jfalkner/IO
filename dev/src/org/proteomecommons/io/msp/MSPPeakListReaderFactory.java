/*
 * MSPPeakListReaderFactory.java
 *
 * Created on August 7, 2006, 11:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.proteomecommons.io.msp;

import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 *
 * @author Jarret - jar@cs.washington.edu
 */
public class MSPPeakListReaderFactory implements PeakListReaderFactory {
    public PeakListReader newInstance(String filename) {
        return new MSPPeakListReader(filename);
    }
    
    public String endings(){
        return ".*.msp";
    }
}

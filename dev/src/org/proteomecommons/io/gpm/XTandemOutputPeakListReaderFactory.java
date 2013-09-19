/*
 * QuartzPeakListReaderFactory.java
 *
 * Created on August 3, 2006, 6:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.proteomecommons.io.gpm;

import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListReaderFactory;

/**
 *
 * @author Jarret - jar@cs.washington.edu
 */
public class XTandemOutputPeakListReaderFactory implements PeakListReaderFactory{
    
    public PeakListReader newInstance(String filename) {
        return new XTandemOutputPeakListReader(filename);
    }
    
    public String endings(){
        return ".*amethyst.*.xml|.*jasper.*xml|.*opal.*xml|.*GPM.*.xml";
    }
}

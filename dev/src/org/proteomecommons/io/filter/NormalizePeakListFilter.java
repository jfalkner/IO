/*
 * NormalizePeakListFilter.java
 *
 * Created on August 31, 2006, 6:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.proteomecommons.io.filter;

import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 * Finds the max peak by intensity in the list, and divides the intensity of all peaks by the intensity of the max peak
 * @author Jarret
 */
public class NormalizePeakListFilter implements PeakListFilter{
    public PeakList filter(PeakList pl) {
        Peak[] orig = pl.getPeaks();
        double max = Double.MIN_VALUE;
        
        for(Peak p : orig){
            if(p.getIntensity() > max)
                max = p.getIntensity();
        }
        
        GenericPeak[] normal = new GenericPeak[orig.length];
        for(int mover = 0; mover<orig.length; mover++){
            normal[mover] = new GenericPeak();
            normal[mover].setIntensity(orig[mover].getIntensity()/max);
            normal[mover].setMassOverCharge(orig[mover].getMassOverCharge());
        }
        
        // make a new peak list copying the old
        GenericPeakList gpl = GenericPeakList.copyPeakListInformation(pl);
        // set the peaks we care about
        gpl.setPeaks(normal);
        // return the filtered peak list
        return gpl;
    }
    
    
}

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
package org.proteomecommons.io.filter;

import java.util.Arrays;

import org.proteomecommons.io.Peak;
import org.proteomecommons.io.*;

/**
 * A filter that will keep on the top <i>count </i> most intense peaks from a
 * peak list. If there are fewer than <i>count </i> peaks, all are kept.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class IntensityPeakListFilter implements PeakListFilter {
    // number of most intense peaks to keep
    int count;
    
    /**
     * Public constructor.
     *
     * @param count
     *            How many peaks to keep from the list.
     */
    public IntensityPeakListFilter(int count) {
        this.count = count;
    }
    
    /**
     * Filter's the peaks in a peak list and keeps on the most intense.
     */
    public PeakList filter(PeakList peaklist) {
        
        // reduce the original set of peaks by a dynamic range
        Peak[] peaks = peaklist.getPeaks();
        
        // keep only the top peaks
        if (peaks.length > count) {
            Arrays.sort(peaks, new PeakIntensityComparator());
            Peak[] bigPeaks = new Peak[count];
            for (int i = 0; i < count; i++) {
                bigPeaks[i] = peaks[i];
            }
            // set the new reference and resort
            peaks = bigPeaks;
            Arrays.sort(peaks);
        }
        
        // make a new peak list starting with a copy of the old
        GenericPeakList gpl = GenericPeakList.copyPeakListInformation(peaklist);
        // set the filtered peaks
        gpl.setPeaks(peaks);
        
        return gpl;
    }
}

//helper class to sort peaks based on intensity

class PeakIntensityComparator implements java.util.Comparator {
    
        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
    public int compare(Object a, Object b) {
        Peak aa = (Peak) a;
        Peak bb = (Peak) b;
        if (aa.getIntensity() < bb.getIntensity()) {
            return 1;
        }
        if (aa.getIntensity() > bb.getIntensity()) {
            return -1;
        }
        
        // TODO Auto-generated method stub
        return 0;
    }
}
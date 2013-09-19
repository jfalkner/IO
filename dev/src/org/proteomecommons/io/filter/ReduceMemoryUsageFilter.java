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

import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 *<p>A utility filter that will reduce peak information to just m/z and intensity, which can save significant amounts of memory usage</p>
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class ReduceMemoryUsageFilter implements PeakListFilter {
    public PeakList filter(PeakList peaklist) {
        return reducePeakList(peaklist);
    }
    
    public static PeakList reducePeakList(PeakList peaklist) {
        GenericPeakList gpl = GenericPeakList.copyPeakListInformation(peaklist);
        Peak[] oldPeaks = gpl.getPeaks();
        Peak[] peaks = new Peak[oldPeaks.length];
        for (int i=0;i<oldPeaks.length;i++) {
            MinimalPeak mp = new MinimalPeak(oldPeaks[i].getMassOverCharge(), oldPeaks[i].getIntensity());
            peaks[i] = mp;
        }
        gpl.setPeaks(peaks);
        // return the peak list
        return gpl;
    }
}

// helper method to filter the peak list
class MinimalPeak implements Peak {
    private float[] values = new float[2];
    
    MinimalPeak(double mz, double intensity) {
        values[0] = (float)mz;
        values[1] = (float)intensity;
    }
    
    public final int getMonoisotopic() {
        return Peak.UNKNOWN_MONOISOTOPIC;
    }
    
    public final double getMassOverCharge() {
        return (double)values[0];
    }
    
    public final double getIntensity() {
        return (double)values[1];
    }
    
    public final int getCharge() {
        return Peak.UNKNOWN_CHARGE;
    }
    
    public final int getCentroided() {
        return Peak.UNKNOWN_CENTROIDED;
    }
    
    public final int getAveraged() {
        return Peak.UNKNOWN_AVERAGED;
    }
}

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

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class GenericPeakList implements PeakList {
    // keep peaks as a list
    ArrayList<Peak> peaks = new ArrayList();
    
    private PeakList parentPeakList = null;
    private Peak parentPeak;
    private int tandemCount = PeakList.UNKNOWN_TANDEM_COUNT;
    
    public Peak[] getPeaks() {
        return peaks.toArray(new Peak[0]);
    }
    
    public List<Peak>getPeaksAsList() {
        return peaks;
    }
    
    public synchronized void setPeaks(Peak[] peaks) {
        // clear existing peaks
        this.peaks.clear();
        // add all the peaks
        for (Peak peak : peaks) {
            this.peaks.add(peak);
        }
        this.peaks.trimToSize();
    }
    
    /**
     * An Arbitrary name for the peak list
     */
    private String name = "Unnamed Peak List";
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the arbitrary name for this peak list. There is no garuntee that
     * this name will be unique.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public PeakList getParentPeakList() {
        return parentPeakList;
    }

    public void setParentPeakList(PeakList parentPeakList) {
        this.parentPeakList = parentPeakList;
    }

    public Peak getParentPeak() {
        return parentPeak;
    }

    public void setParentPeak(Peak parentPeak) {
        this.parentPeak = parentPeak;
    }

    public int getTandemCount() {
        if (tandemCount == PeakList.UNKNOWN_TANDEM_COUNT && parentPeak != null) {
            return 2;
        }
        return tandemCount;
    }

    public void setTandemCount(int tandemCount) {
        this.tandemCount = tandemCount;
    }
    
    public static void copyPeakListInformation(PeakList copyFrom, GenericPeakList copyTo) {
        copyTo.setParentPeak(copyFrom.getParentPeak());
        copyTo.setParentPeakList(copyFrom.getParentPeakList());
        copyTo.setTandemCount(copyFrom.getTandemCount());
        copyTo.setPeaks(copyFrom.getPeaks());
    }
    public static GenericPeakList copyPeakListInformation(PeakList copyFrom) {
        GenericPeakList gpl = new GenericPeakList();
        copyPeakListInformation(copyFrom, gpl);
        return gpl;
    }
}
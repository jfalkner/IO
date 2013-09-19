/*
 *    Copyright 2004 Jayson Falkner
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

import org.proteomecommons.io.*;
import java.util.*;

/**
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 * A filter that will keep peaks from a peaklist based on if they fall within a given dynamic range..
 */
public class DynamicRangePeakListFilter implements PeakListFilter{
double dynamicRange;
/**
 * Public constructor.
 * @param dynamicRange The dynamic range to use when trimming small peaks.
 */
	public DynamicRangePeakListFilter(double dynamicRange){
  	this.dynamicRange = dynamicRange;
  }
	
	/**
	 * Filter the given peak list, return one with peaks only in the given dynamic range. The dynamic range is assumed to be based on the highest peak in the list.
	 */
	public PeakList filter(PeakList peaklist) {
		// check if null
		if (peaklist == null){
			return null;
		}
		
		// find max
		double max = Double.MIN_VALUE;
    for (int i=0;i<peaklist.peaks.length;i++){
    	// check for new max
    	if (peaklist.peaks[i].intensity > max){
    		max = peaklist.peaks[i].intensity;
    	}
    }

    // filter out anything less than the range
    LinkedList validPeaks = new LinkedList();
    for (int i=0;i<peaklist.peaks.length;i++){
    	// check for new max
    	if (peaklist.peaks[i].intensity*dynamicRange/ max < 1){
    		continue;
    	}
    	// add to list of peaks to keep
    	validPeaks.add(peaklist.peaks[i]);
    }
    
    // make a new peaklist
    FilteredPeakList pl = new FilteredPeakList(peaklist, this);
    pl.peaks = (Peak[])validPeaks.toArray(new Peak[0]);
    
    // return the valid peaks
    return pl;
	}
}

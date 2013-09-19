/*
 * WindowIntensityFilter.java
 *
 * Created on August 28, 2006, 1:16 AM
 */

package org.proteomecommons.io.filter;

import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import java.util.*;

/**
 * A filter that moves a sliding window across the mz values of peaks in lists
 * that are being filtered.
 * @author Jarret - jar@cs.washington.edu
 * @author Jayson
 */
public class WindowIntensityFilter implements PeakListFilter{
    Comparator c =  new PeakIntensityComparator();

    private int windowSize;

    private int startMz;

    private int endMz;

    private int peaksPerWindow;
    
    /**
     * Make a filter that starts at 100m/z and, in 100m/z increments, picks up to
     * three of the most intense peaks to place into the filtered list.
     *
     */
    public WindowIntensityFilter(){
        this(100, 3000, 100, 3);
    }
    
    /**
     * @param startMz peaks below this m/z will not be considered
     * @param endMz peaks above this m/z will not be considered
     * @param windowSize width of the range to pick peaksPerWindow intense peaks from
     * @param peaksPerWindow max number of peaks to pick in a window.  Peaks will be kept by intensity
     */
    public WindowIntensityFilter(int startMz, int endMz, int windowSize, int peaksPerWindow){
        if(windowSize < 1)
            throw new IllegalArgumentException("window size needs to be > 1");
        if(startMz > endMz)
            throw new IllegalArgumentException("the start mz needs to be less than the end mz");
        this.startMz = startMz;
        this.endMz = endMz;
        this.windowSize = windowSize;
        this.peaksPerWindow = peaksPerWindow;
    }
    
    public PeakList filter(PeakList peaklist) {
        if(peaklist == null)
            return null;
        
        //make a mapping for range queries
        TreeMap<Double, Peak> mzToPeaks = new TreeMap<Double, Peak>();
        for(Peak p : peaklist.getPeaks()){
            mzToPeaks.put(p.getMassOverCharge(), p);
        }
        
        //slide the window across to figure out what to keep
        ArrayList<Peak> keepers = new ArrayList<Peak>();
        for(int low = startMz; low < endMz; low = low +windowSize){
            //get peaks in the current range
            Collection<Peak> pks = mzToPeaks.subMap(new Double(low), new Double(low+100)).values();
            
            //keep only the most intense ones
            LinkedList<Peak> toSort = new LinkedList<Peak>(pks);
            Collections.sort(toSort, c);
            for(int howMany = peaksPerWindow; howMany > 0 && toSort.size()>0; howMany--){
                keepers.add(toSort.removeFirst());
            }
        }
        
//        // the list of peaks to index
//        Set<Peak> peaksToIndex = new HashSet();
//        
//        // all the peaks
//        Peak[] peaks = peaklist.getPeaks();
//        
//        // sort by m/z
//        Arrays.sort(peaks);
//        // only take two peaks
//        int bin = 1;
//        
//        // take two peaks per 100 Da
//        while(true) {
//            // calc min/max
//            int min = bin * 100;
//            int max = min + 100;
//            // get all the peaks in the range
//            LinkedList<Peak> buf = new LinkedList();
//            for (Peak p : peaks) {
//                if (p.getMassOverCharge() <= min || p.getMassOverCharge() > max) {
//                    continue;
//                }
//                buf.add(p);
//            }
//            // break if the buffer's size is 0
//            if (min > 3000) {
//                break;
//            }
//            // take the top two peaks
//            Collections.sort(buf, new PeakIntensityComparator());
//            // add the top two
//            if (buf.size()>0) {
//                Peak kept = buf.removeFirst();
//                peaksToIndex.add(kept);
//            }
//            if (buf.size()>0)  {
//                Peak kept = buf.removeFirst();
//                peaksToIndex.add(kept);
//            }
//            
//            // inc the bin
//            bin++;
//        }
        GenericPeakList gpl = new GenericPeakList();
        gpl.setPeaks(keepers.toArray(new Peak[0]));
        return gpl;
    }
}

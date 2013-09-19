package org.proteomecommons.io.raw;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
/**
 * A PeakListReader implementation that reads RAW files.
 *
 * @author Takis Papoulias - panagio@umich.edu
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public class RawReader extends GenericPeakListReader implements PeakListReader {
    
    public RawReader(String filename) {
        super(filename);
    }
    
//    // keep track of the number of spectra
//    long numberOfSpectra = 0;
//    long currentSpectra = 1;
//    File toCleanUp = null;
//    String workingFilename = null;
//    int peakIndex = -1;
//    int peakListCacheIndex = 0;
//    double[][] peaks = new double[0][2];
//    
//    // temporary place to hold the DDL
//    static File temp = new File("RawFileGlueCode.dll");
//    
//    static int instanceCounter=0;
//    static
//    {
//        // temporarily copy the DLL to disk
//        synchronized (temp) {
//            if (!temp.exists()) {
//                try {
//                    InputStream in = RawViaReadwFactory.class.getClassLoader().getResourceAsStream("RawFileGlueCode.dll");
//                    FileOutputStream fos = new FileOutputStream(temp);
//                    try {
//                        byte[] bytes = new byte[1000];
//                        for (int bytesRead = in.read(bytes);bytesRead!=-1;bytesRead=in.read(bytes)) {
//                            fos.write(bytes, 0, bytesRead);
//                        }
//                    } finally {
//                        try { fos.flush(); } catch (Exception e){}
//                        try { fos.close(); } catch (Exception e){}
//                        try { in.close(); } catch (Exception e){}
//                    }
//                } catch (Exception e) {
//                    // noop
//                }
//            }
//        }
//        
//        // load the system library
//        System.loadLibrary(temp.getName().split(".dll")[0]);
//    }
//    
//    /**
//     * Public constructor.
//     *
//     * @param fileName
//     *            Name of the RAW file to parse.
//     * @throws IOException
//     */
//    public RawReader(String filename) {
//        // set the name
//        super(null);
//        this.setName(filename);
//        workingFilename = filename;
//        
//        // get the number of spectra
//        this.numberOfSpectra = getNumberOfSpectra(workingFilename);
//        System.out.println("RAW file: "+filename+", peakLists: "+numberOfSpectra);
//
//        // load the new spectrum
//        if (numberOfSpectra > 0) {
//            peaks = getScan(workingFilename, currentSpectra);
//        }
//    }
//    
//    public RawReader(InputStream in){
//        super(null);
//        try {
//            // write out to the known locations
//            toCleanUp = File.createTempFile("temp", ".raw");
//            // set the name
//            this.setName(toCleanUp.getCanonicalPath());
//            workingFilename = toCleanUp.getCanonicalPath();
//            // write out to that file
//            FileOutputStream fos = new FileOutputStream(toCleanUp);
//            try {
//                byte[] buf = new byte[10000];
//                for(int bytesRead = in.read(buf);bytesRead!=-1;bytesRead=in.read(buf)) {
//                    fos.write(buf, 0, bytesRead);
//                }
//            } finally {
//                try {fos.flush();} catch (Exception e){}
//                try {fos.close();} catch (Exception e){}
//            }
//        } catch(Exception ex) {
//            ex.printStackTrace();
//            throw new RuntimeException("Could Not Complete copy of the RAW file to a temporary file");
//        }
//        
//        // get the number of spectra
//        this.numberOfSpectra = getNumberOfSpectra(workingFilename);
//        System.out.println("RAW file: unknown, peakLists: "+numberOfSpectra);
//        // load the new spectrum
//        if (numberOfSpectra > 0) {
//            peaks = getScan(workingFilename, currentSpectra);
//        }
//    }
//    
//    /**
//     * @see org.proteomecommons.io.PeakListReader#hasNext()
//     */
//    public boolean hasNext() {
//        // if more spectra exist in the file, say so
//        if( currentSpectra <= numberOfSpectra) {
//            return true;
//        }
//        // fall back on false
//        return false;
//    }
//    
//    /**
//     * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
//     */
//    public boolean isStartOfPeakList() {
//        return peakIndex == -1;
//    }
//    
//    /**
//     * @see org.proteomecommons.io.PeakListReader#next()
//     */
//    public Peak next() {
//        // increment the index
//        peakIndex++;
//        // if the value exists, return it
//        if (peaks != null && peakIndex < peaks.length) {
//            GenericPeak gp = new GenericPeak();
//            gp.setMassOverCharge(peaks[peakIndex][0]);
//            gp.setIntensity(peaks[peakIndex][1]);
//            return gp;
//        }
//        // clear the old peaks
//        peaks = null;
//        while (peaks == null) {
//            // increment the spectrum index
//            this.currentSpectra++;
//            System.out.println("Getting next peak list: "+currentSpectra);
//            // if more exist, initialize the next peak list
//            if (hasNext()) {
//                // reset the peak index
//                peakIndex = -1;
//                // load the new spectrum
//                peaks = getScan(workingFilename, currentSpectra);
//            } else {
//                break;
//            }
//        }
//        
//        // return null to indicate that the peaklist is done
//        return null;
//    }
//    
//    
//    /**
//     * @see org.proteomecommons.io.PeakListReader#close()
//     */
//    public void close() {
//        // try to delete the temp file
//        try {
//            if (toCleanUp != null && toCleanUp.exists()) toCleanUp.delete();
//        } catch (Exception e){
//            // noop
//        }
//        // try to delete the dll copy
//        try {
//            if (temp != null && temp.exists()) temp.delete();
//        } catch (Exception e){
//            // noop
//        }
//    }
//    
//    // native JNI methods. these are implemented in the DLL
//    public native double[][] getScan(String filename, long scanNumber);
////    public native double[][][] getScans(String filename, long scanNumber, long length);
//    public native long getNumberOfSpectra(String filename);
    

    public PeakList getPeakList() {
        throw new RuntimeException("Not currently implemented. Use the RAWViaReAdWPeakListReader class instead.");
    }

    public void close() {
    }
}

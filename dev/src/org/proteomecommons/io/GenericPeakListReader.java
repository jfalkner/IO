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

import java.util.regex.*;
import java.util.*;
import org.proteomecommons.io.bzip2.BZIP2PeakListReaderFactory;
import org.proteomecommons.io.baf.BAFPeakListReaderFactory;
import org.proteomecommons.io.dataxml.DataXmlPeakListReaderFactory;
import org.proteomecommons.io.gpm.XTandemOutputPeakListReaderFactory;

import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.mzdata.v1_05.MzDataPeakListReaderFactory;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.fid.FIDPeakListReaderFactory;
import org.proteomecommons.io.gzip.GZIPPeakListReaderFactory;
import org.proteomecommons.io.msp.MSPPeakListReaderFactory;
import org.proteomecommons.io.pkl.*;
import org.proteomecommons.io.wiff.*;
import org.proteomecommons.io.raw.RawViaReadwFactory;
import org.proteomecommons.io.lzma.LZMAPeakListReaderFactory;
import org.proteomecommons.io.txt.PlainTextPeakListReaderFactory;
import org.proteomecommons.io.xml.GenericXMLPeakListReaderFactory;
import org.proteomecommons.io.yep.YEPPeakListReaderFactory;
import org.proteomecommons.io.zip.*;
/**
 * The abstact base class for reading peak list information from a file. This
 * class supports batch reading of entire peak lists or streaming reading of
 * individual peaks.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public abstract class GenericPeakListReader implements PeakListReader {
    // the name of this reader, usually the name of a file
    private String filename;
    
    // map of all readers
    private static LinkedHashMap readers = new LinkedHashMap();
    
    static {
        // load the sequest reader factory
        SequestDTAPeakListReaderFactory sequest = new SequestDTAPeakListReaderFactory();
        setPeakListReader(sequest, ".*.dta|.*.DTA");
        // load the micromass reader
        MicromassPKLPeakListReaderFactory micromass = new MicromassPKLPeakListReaderFactory();
        setPeakListReader(micromass, ".*pkl|.*.PKL");
        // load the mascot reader
        MascotGenericFormatPeakListReaderFactory mascot = new MascotGenericFormatPeakListReaderFactory();
        setPeakListReader(mascot, ".*.mgf|.*.MGF");
        // load the wiff csv reader
        AnalystQSWIFFToCSVPeakListReaderFactory wiffcsv = new AnalystQSWIFFToCSVPeakListReaderFactory();
        setPeakListReader(wiffcsv, wiffcsv.getRegularExpression());

        // load the plain txt peak list reader
        PlainTextPeakListReaderFactory txt = new PlainTextPeakListReaderFactory();
        setPeakListReader(txt, txt.getRegularExpression());

        // load the dataXML beta reader with a limited regex
        DataXmlPeakListReaderFactory dataXML = new DataXmlPeakListReaderFactory();
        setPeakListReader(dataXML, ".*.dataxml");

        // load the mzXML 1.1.1 reader with a limited regex
        loadReaderClass("org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListReaderFactory",".*\\.1\\.1\\.1\\.mzxml\\.xml");
        // load the mzXML 2.0 reader with a limited regex
        loadReaderClass("org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListReaderFactory",".*\\.2\\.0\\.mzxml\\.xml");
        // load the mzXML 2.1 reader with a limited regex
        loadReaderClass("org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListReaderFactory",".*\\.2\\.1\\.mzxml\\.xml");
        
        //handle quartz xml (before mzxml sngas all .*.xml)
        XTandemOutputPeakListReaderFactory xtoplrf = new XTandemOutputPeakListReaderFactory();
        setPeakListReader(xtoplrf, xtoplrf.endings());
        
        // load the mzData reader
        MzDataPeakListReaderFactory mzData = new MzDataPeakListReaderFactory();
        setPeakListReader(mzData, ".*.mzdata");
        
        // load the abstract XML reader. Also have it handle mzXML files.
        PeakListReaderFactory mzXML = new GenericXMLPeakListReaderFactory();
        setPeakListReader(mzXML, ".*\\.xml|.*\\.mzxml|.*\\.mzdata");
        
        // add support for a known format that is ZIP compressed
        ZipPeakListReaderFactory zip = new ZipPeakListReaderFactory();
        setPeakListReader(zip, zip.getRegularExpression());
        // add support for a known format that is gzip compressed
        GZIPPeakListReaderFactory gzip = new GZIPPeakListReaderFactory();
        setPeakListReader(gzip, gzip.getRegularExpression());
        // add support for a known format that is gzip compressed
        setPeakListReader(new LZMAPeakListReaderFactory(), ".*\\.lzma");
        // add support for a known format that is bzip2 compressed
        setPeakListReader(new BZIP2PeakListReaderFactory(), ".*\\.bzip2");
        
        // t2d reader (optional library)
        loadReaderClass("org.proteomecommons.io.t2d.T2DPeakListReaderFactory",".*T2D|.*t2d");
        // waters's reader (optional library)
        loadReaderClass("org.proteomecommons.io.waters.MassLynxPeakListReaderFactory",".*\\.raw.*.dat|.*\\.raw.*.idx");
        //add support for raw files
        PeakListReaderFactory raw = new RawViaReadwFactory();
        setPeakListReader(raw,".*.raw|.*.RAW");
        //add support for wiff files
        PeakListReaderFactory wiff = new WiffViaWiffToDtaFactory();
        setPeakListReader(wiff,".*.wiff|.*.WIFF");
        //and msp
        MSPPeakListReaderFactory msp = new MSPPeakListReaderFactory();
        setPeakListReader(msp, msp.endings());
        //add support for reading '.baf'
        BAFPeakListReaderFactory baf = new BAFPeakListReaderFactory();
        setPeakListReader(baf, ".*.baf|.*.BAF");
        //add support for reading '.fid'
        FIDPeakListReaderFactory fid = new FIDPeakListReaderFactory();
        setPeakListReader(fid, ".*.fid|.*.FID");
        // add support for reading '.yep' files
        YEPPeakListReaderFactory yep = new YEPPeakListReaderFactory();
        setPeakListReader(yep, ".*.yep|.*.YEP");
    }
    
    // helper method to dynamically load reader classes, this allows external libraries to be loaded without breaking things at compile time
    private static void loadReaderClass(String reader, String regex){
        try {
            PeakListReaderFactory plrf = (PeakListReaderFactory)Class.forName(reader).newInstance();
            setPeakListReader(plrf, regex);
        } catch (Exception e){
            System.err.println("Failed to load writer class "+reader+", skipping.");
        }
    }
    
    /**
     * Public constructor, automatically
     *
     * @param in
     */
    public GenericPeakListReader(String filename) {
        this.setName(filename);
    }
    
    /**
     * Helper method to register reader objects. Each factory is registered with
     * a regular expression. The regular expression is used to screen filenames
     * for matches that can be parsed by the reader.
     *
     * @param factory
     *            An instance of PeakListReaderFactory.
     * @param regex
     *            A regular expression that is used to match files to the
     *            appropriate factory.
     */
    public synchronized static void setPeakListReader(
            PeakListReaderFactory factory, String regex) {
        // treat the extension as a regular expression
        Pattern pattern = Pattern.compile(regex);
        readers.put(pattern, factory);
    }
    
    /**
     * Helper method to get an appropriate reader based on a filename.
     *
     * @param name
     */
    public synchronized static PeakListReader getPeakListReader(String filename) throws UnknownFileFormatException{
        // get the appropriate factory
        PeakListReaderFactory factory = (PeakListReaderFactory) getPeakListReaderFactory(filename);
        
        // make the reader
        PeakListReader reader = factory.newInstance(filename);
        
        // return a new PeakListReader instance
        return reader;
    }
    
    /**
     * Helper method to get an appropriate PeakListReaderFactory instance.
     *
     * @param name
     */
    public synchronized static PeakListReaderFactory getPeakListReaderFactory(String filename) throws UnknownFileFormatException {
        
        // check for the appropriate factory
        for (Iterator it = readers.keySet().iterator(); it.hasNext();) {
            // get the patter
            Pattern p = (Pattern) it.next();
            // always match against lowercase
            Matcher m = p.matcher(filename.toLowerCase());
            
            // if they match, return them
            if (m.matches()) {
                return (PeakListReaderFactory) readers.get(p);
            }
        }
        
        // get the appropriate factory
        throw new UnknownFileFormatException("Can't find a PeakListReaderFactory for "+filename);
    }
    
    /**
     * Sets the name of this PeakListReader. If you are parsing a file, and you
     * are using one of the helper methods of this class, the name is
     * automatically set to be the name of the file.
     *
     * @param name
     *            The name of this PeakListReader
     */
    public void setName(String name) {
        this.filename = name;
    }
    
    /**
     * A method to notify the PeakListReader instance that no more Peak objects
     * or PeakList objects are going to be read. This gives the reader a change
     * to free up any unneeded resources that are allocated.
     *
     */
    public abstract void close();
    
    /**
     * Returns the name of this PeakListReader object. If this PeakListReader
     * was derived from a file, the name of the file is returned. For all other
     * cases the name is undefined.
     *
     * @return
     */
    public String getName() {
        return filename;
    }
    
    /**
     * Helper method to read only the first peak list from a peak list file and
     * return the results.
     *
     * @param fileName
     * @return The first peak list in the file, or null if there is no peak
     *         list.
     */
    public static PeakList read(String fileName) {
        try {
            // make a new reader
            PeakListReader plr = getPeakListReader(fileName);
            // check if it is null
            if (plr == null) {
                return null;
            }
            plr.setName(fileName);
            
            // ref peaklist and close off resources
            PeakList pl = plr.getPeakList();
            
            // finish the resource
            plr.close();
            
            return pl;
        } catch (Exception e) {
            // noop
            //e.printStackTrace();
        }
        return null;
    }
    
//    /**
//     * Returns a PeakList object with an array of Peak objects . This method
//     * does load everything in to memory, and it is not well-suited for
//     * extraordinarily large peaklist files.
//     */
//    public PeakList getPeakList() throws CheckedIOException {
//        // if there are no more peak lists, skip
//        if (!isStartOfPeakList()) {
//            return null;
//        }
//
//        // get the current parent peak
//        Peak currentParent = getParent();
//
//        // load the peaks in to memory
//        LinkedList temp = new LinkedList();
//        // load first/null check
//        Peak p = next();
//        if (p != null) {
//            // add the first peak
//            temp.add(p);
//        }
//        // read all the peaks up to the next peak list
//        while (hasNext() && !isStartOfPeakList()) {
//            temp.add(next());
//        }
//
//        // make a new peaklist
//        if (this instanceof TandemPeakListReader) {
//            // cast to a tandem
//            TandemPeakListReader tandem = (TandemPeakListReader) this;
//            if (tandem.getTandemCount() != 1) {
//                GenericTandemPeakList gtpl = new GenericTandemPeakList();
//                gtpl.setPeaks((Peak[]) temp.toArray(new Peak[temp.size()]));
//                gtpl.setParent(currentParent);
//                gtpl.setTandemCount(tandem.getTandemCount());
//                gtpl.setParentPeakList(tandem.getParentPeakList());
//                return gtpl;
//            }
//        }
//
//        // fall back on a peaklist
//        GenericPeakList peaklist = new GenericPeakList();
//        // convert to an array of peaks
//        peaklist.setPeaks((Peak[]) temp.toArray(new Peak[temp.size()]));
//        return peaklist;
//    }
    
    /**
     * Returns a PeakList object with an array of Peak objects . This method
     * does load everything in to memory, and it is not well-suited for
     * extraordinarily large peaklist files.
     */
    public abstract PeakList getPeakList();
}
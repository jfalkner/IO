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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.proteomecommons.io.bzip2.BZIP2PeakListWriterFactory;

import org.proteomecommons.io.mgf.*;
import org.proteomecommons.io.dta.*;
import org.proteomecommons.io.gzip.GZIPPeakListWriterFactory;
import org.proteomecommons.io.lzma.LZMAPeakListWriterFactory;
import org.proteomecommons.io.pkl.*;
import org.proteomecommons.io.txt.*;
import org.proteomecommons.io.mzxml.MzXMLPeakListWriterFactory;
import org.proteomecommons.io.zip.ZipPeakListWriterFactory;
/**
 * An abstraction for serializing PeakList objects in to the various peak list file formats. Note that the general design pattern is to throw unchecked exceptions in cases of file format incompatiabilities. If you are designing a tool that uses this code, you will likely want to catch these exceptions.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 *
 */
public abstract class GenericPeakListWriter implements PeakListWriter {
    // map of all readers
    private static LinkedHashMap writers = new LinkedHashMap();
    
    // the filename
    private String filename = null;
    
    static {
        // load the sequest reader factory
        SequestDTAPeakListWriterFactory sequest = new SequestDTAPeakListWriterFactory();
        setPeakListWriter(sequest, ".*.dta|.*.DTA");
        // load the micromass reader
        MicromassPKLPeakListWriterFactory micromass = new MicromassPKLPeakListWriterFactory();
        setPeakListWriter(micromass, ".*pkl|.*.PKL");
        // load the mascot reader
        MascotGenericFormatPeakListWriterFactory mascot = new MascotGenericFormatPeakListWriterFactory();
        setPeakListWriter(mascot, ".*.mgf|.*.MGF");
        // load the plain text writers
        PlainTextNotepadFriendlyPeakListWriterFactory notepad = new PlainTextNotepadFriendlyPeakListWriterFactory();
        setPeakListWriter(notepad, ".*.notepad.txt|.*.notepad.TXT");
        // load the plain text writers
        PlainTextPeakListWriterFactory text = new PlainTextPeakListWriterFactory();
        setPeakListWriter(text, ".*.txt|.*.TXT");
        // load the mzdata writers (optional lib)
        loadWriterClass("org.proteomecommons.io.mzdata.MzDataPeakListWriterFactory", ".*.mzdata.xml|.*.mzdata.XML|.*.mzdata|.*.MZDATA|.*mzData");
        // load the t2d writer (optional lib)
        loadWriterClass("org.proteomecommons.io.t2d.T2DPeakListWriterFactory", ".*.t2d|.*.T2D");
        
        // load a writer for dataXML bets with limited filename matching
        loadWriterClass("org.proteomecommons.io.dataxml.DataXmlPeakListWriterFactory", ".*.dataxml");
        
        // load a writer for mzXML 1.1.1 with limited filename matching
        loadWriterClass("org.proteomecommons.io.mzxml.v1_1_1.MzXMLPeakListWriterFactory", ".*\\.1\\.1\\.1\\.mzxml\\.xml");
        // load a writer for mzXML 2.0 with limited filename matching
        loadWriterClass("org.proteomecommons.io.mzxml.v2_0.MzXMLPeakListWriterFactory", ".*\\.2\\.0\\.mzxml\\.xml");
        // load a writer for mzXML 2.1 with limited filename matching
        loadWriterClass("org.proteomecommons.io.mzxml.v2_1.MzXMLPeakListWriterFactory", ".*\\.2\\.1\\.mzxml\\.xml");
        // load a writer for mzXML that will intelligently pick out the version
        MzXMLPeakListWriterFactory mz = new MzXMLPeakListWriterFactory();
        setPeakListWriter(mz, ".*.mzXML|.*.mzxml|.*.mzXml|.*.MZXML|.*.mzxml.xml");
        
        // the b2zip compression writer
        BZIP2PeakListWriterFactory b2zip = new BZIP2PeakListWriterFactory();
        setPeakListWriter(b2zip, b2zip.getRegex());
        // support gzip compression
        GZIPPeakListWriterFactory gzip = new GZIPPeakListWriterFactory();
        setPeakListWriter(gzip, gzip.getRegex());
        // support LZMA compression
        LZMAPeakListWriterFactory lzma = new LZMAPeakListWriterFactory();
        setPeakListWriter(lzma, lzma.getRegex());
        // support ZIP compression
        ZipPeakListWriterFactory zip = new ZipPeakListWriterFactory();
        setPeakListWriter(zip, zip.getRegex());
    }
    
    // helper method to dynamically load writer classes, this allows external libraries to be loaded without breaking things at compile time
    private static void loadWriterClass(String writer, String regex){
        try {
            PeakListWriterFactory mzData = (PeakListWriterFactory)Class.forName(writer).newInstance();
            setPeakListWriter(mzData, regex);
        } catch (Exception e){
            System.err.println("Failed to load writer class "+writer+", skipping.");
        }
    }
    
    /**
     * Returns all the registered writers.
     * @return
     */
    public static PeakListWriterFactory[] getRegisteredWriters(){
        return (PeakListWriterFactory[])writers.values().toArray(new PeakListWriterFactory[0]);
    }
    
    /**
     * Public constructor, automatically sets up buffered IO.
     *
     * @param out
     */
    public GenericPeakListWriter(String filename) {
        this.setFilename(filename);
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
    public synchronized static void setPeakListWriter(
            PeakListWriterFactory factory, String regex) {
        // treat the extension as a regular expression
        Pattern pattern = Pattern.compile(regex);
        writers.put(pattern, factory);
    }
    
    /**
     * Helper method to get an appropriate reader based on a filename.
     *
     * @param name
     */
    public synchronized static PeakListWriter getPeakListWriter(String filename) {
        // get the appropriate factory
        PeakListWriterFactory factory = (PeakListWriterFactory) getPeakListWriterFactory(filename);
        // return null if none exist
        if (factory == null) {
            return null;
        }
        
        // make the reader
        PeakListWriter writer = factory.newInstance(filename);
        
        // return a new PeakListReader instance
        return writer;
    }
    
    /**
     * Helper method to get an appropriate PeakListReaderFactory instance.
     *
     * @param name
     */
    public synchronized static PeakListWriterFactory getPeakListWriterFactory(String filename) {
        
        // check for the appropriate factory
        for (Iterator it = writers.keySet().iterator(); it.hasNext();) {
            Pattern p = (Pattern) it.next();
            // always match against lower case
            Matcher m = p.matcher(filename.toLowerCase());
            if (m.matches()) {
                return (PeakListWriterFactory) writers.get(p);
            }
        }
        
        // get the appropriate factory
        throw new RuntimeException("Can't find a create the peak list file "+filename+". Please try a different format.");
    }
    
    /**
     * A helper implementation for writing an entire peak lists. This class will invoke the startPeakList() method and invoke the bulk write(Peak[]) method for all the peaks.
     */
    public abstract void write(PeakList peaklist);
    
    /**
     * Instructs the writer to finish writing any peak lists and closes down any associated resources.
     *
     */
    public void close() {
        // do nothing
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
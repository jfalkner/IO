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
package org.proteomecommons.io.mzxml.v2_1;

import org.proteomecommons.io.*;

import javax.xml.stream.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.proteomecommons.io.mzxml.Base64;

/**
 * This class is intended to be a complete implementation of a PeakListReader for the MZXML 2.1 file format. Users may sub-cast to a <code>org.proteomecommons.io.mzxml.v2_1.MZXMLPeakListReader</code> to take advantage of the meta-information.
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author Jarret Falkner - jar@cs.washington.edu
 *
 */
public class MzXMLPeakListReader extends GenericPeakListReader {
    //parsing sources
    private XMLStreamReader parser;
    private FileInputStream fis;
    
    // make a new msRun element
    private MsRun msRun = new MsRun();
    
    // handle the events
    private int event = XMLStreamConstants.START_DOCUMENT;
    
    // the list of parent scans
    private LinkedList<MzXMLPeakList> stack = new LinkedList();
    
    /**
     * Create an reader for peak lists in mzXML format. This reader uses the
     * StAX parser.
     *
     * @param in
     *            the input stream to read from
     */
    public MzXMLPeakListReader(String filename) {
        super(filename);
        
        // build the XML parser
        try {
            fis = new FileInputStream(filename);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLStreamReader(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * (non-Javadoc)
     *
     * @see org.proteomecommons.io.GenericPeakListReader#close()
     */
    public void close() {
        try { parser.close(); } catch (Exception e){}
        try { fis.close(); } catch (Exception e){}
    }
    
    public PeakList getPeakList() {
        // if we're at the end, return so
        if (event == XMLStreamConstants.END_DOCUMENT) {
            return null;
        }
        try {
            
            // set the parse flag to on
            boolean keepParsing = true;
            
            //state variables for parsing
            boolean inScan = false;
            boolean inPeak = false;
            boolean inPrecursorMz = false;
            boolean foundScan = false;
            
            // make the start of the peak list
            MzXMLPeakList peaklist = null;
            
            // peak info
            String precision = null;
            String byteOrder = null;
            String pairOrder = null;
            String base64EncodedValues = "";
            
            // precursor info
            String precursorIntensity = null;
            String precursorMz = null;
            
            // parse the next peak info
            for (event = parser.next(); event != XMLStreamConstants.END_DOCUMENT && keepParsing; event = parser.next()) {
                // handle the next scane
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        // handle new scan elements
                        if (parser.getLocalName().equals("scan")) {
                            // flag that we're in a scan
                            inScan = true;
                            // foundScane signifies that some data was found, i.e. return a peak list
                            foundScan = true;
                            // reset peak list
                            peaklist = new MzXMLPeakList();
                            peaklist.setMsRun(msRun);
                            // get the known attributes
                            peaklist.setNum(parser.getAttributeValue(null, "num"));
                            peaklist.setMsLevel(parser.getAttributeValue(null, "msLevel"));
                            peaklist.setPeaksCount(parser.getAttributeValue(null, "peaksCount"));
                            peaklist.setPolarity(parser.getAttributeValue(null, "polarity"));
                            peaklist.setRetentionTime(parser.getAttributeValue(null, "retentionTime"));
                            peaklist.setLowMz(parser.getAttributeValue(null, "lowMz"));
                            peaklist.setHighMz(parser.getAttributeValue(null, "highMz"));
                            peaklist.setBasePeakMz(parser.getAttributeValue(null, "basePeakMz"));
                            peaklist.setBasePeakIntensity(parser.getAttributeValue(null, "basePeakIntensity"));
                            peaklist.setTotIonCurrent(parser.getAttributeValue(null, "totIonCurrent"));
                            
                            // handle precursor scan info
                            peaklist.setPrecursorScanNum(parser.getAttributeValue(null, "precursorScanNum"));
                            // if it is null, try the stack
                            if (peaklist.getPrecursorScanNum() == null || peaklist.getPrecursorScanNum().trim().equals("")) {
                                if (stack.size() >0) {
                                    peaklist.setPrecursorScanNum(stack.getFirst().getNum());
                                }
                            }
                            // try to set tandem count to be the ms level
                            try {
                                // update ms level if appropriate
                                peaklist.setTandemCount(Integer.parseInt(peaklist.getMsLevel()));
                            } catch (NumberFormatException ex) {
                                // noop
                            } catch (NullPointerException npe) {
                                // noop
                            }
                            // if it is null, try the stack
                            if (peaklist.getMsLevel() == null || peaklist.getMsLevel().trim().equals("")) {
                                if (stack.size() >0) {
                                    peaklist.setMsLevel(Integer.toString(stack.size()+1));
                                }
                            }
                            
                            // push to the stack
                            stack.addFirst(peaklist);
                        }
                        // handle peak information
                        else if (parser.getLocalName().equals("peaks")) {
                            inPeak = true;
                            // reset old peak data
                            base64EncodedValues = "";
                            //find precision
                            precision = parser.getAttributeValue(null, "precision");
                            byteOrder = parser.getAttributeValue(null, "byteOrder");
                            pairOrder = parser.getAttributeValue(null, "pairOrder");
                        }
                        // handle the info abour precursor peak lists
                        else if (parser.getLocalName().equals("precursorMz")) {
                            // flag the state we are in
                            inPrecursorMz = true;
                            // set the known values
                            precursorIntensity = parser.getAttributeValue(null, "precursorIntensity");
                            precursorMz = parser.getAttributeValue(null, "precursorMz");
                        }
                        // handle the msRun info
                        else if (parser.getLocalName().equals("msRun")) {
                            // set the known values
                            msRun.setScanCount(parser.getAttributeValue(null, "scanCount"));
                            msRun.setStartTime(parser.getAttributeValue(null, "startTime"));
                            msRun.setEndTime(parser.getAttributeValue(null, "endTime"));
                        }
                        // handle the parentFileInfo
                        else if (parser.getLocalName().equals("parentFile")) {
                            // make a new parent file
                            ParentFile parentFile = new ParentFile();
                            // set the known values
                            parentFile.setFileName(parser.getAttributeValue(null, "fileName"));
                            parentFile.setFileType(parser.getAttributeValue(null, "fileType"));
                            parentFile.setFileSHA(parser.getAttributeValue(null, "fileSha1"));
                            // set the reference
                            msRun.addParentFile(parentFile);
                        }
                        // handle the msInstrument
                        else if (parser.getLocalName().equals("msInstrument")) {
                            // make a new parent file
                            msRun.setMsInstrument(new MsInstrument());
                        }
                        // handle the msManufacturer
                        else if (parser.getLocalName().equals("msManufacturer")) {
                            // check for the msInstrument
                            if (msRun.getMsInstrument() == null) {
                                throw new InvalidFileFormatException("mzXML 2.1 requires that you declare msManufacture elements within a msInstrument element.");
                            }
                            // get the info
                            MsManufacturer msManufacturer = new MsManufacturer();
                            msManufacturer.setCategory(parser.getAttributeValue(null, "category"));
                            msManufacturer.setValue(parser.getAttributeValue(null, "value"));
                            msRun.getMsInstrument().setMsManufacturer(msManufacturer);
                        }
                        // handle the MsModel
                        else if (parser.getLocalName().equals("msModel")) {
                            // check for the msInstrument
                            if (msRun.getMsInstrument() == null) {
                                throw new InvalidFileFormatException("mzXML 2.1 requires that you declare msModel elements within a msInstrument element.");
                            }
                            // get the info
                            MsModel msModel = new MsModel();
                            msModel.setCategory(parser.getAttributeValue(null, "category"));
                            msModel.setValue(parser.getAttributeValue(null, "value"));
                            msRun.getMsInstrument().setMsModel(msModel);
                        }
                        // handle the MsIonisation
                        else if (parser.getLocalName().equals("msIonisation")) {
                            // check for the msInstrument
                            if (msRun.getMsInstrument() == null) {
                                throw new InvalidFileFormatException("mzXML 2.1 requires that you declare msIonisation  elements within a msInstrument element.");
                            }
                            // get the info
                            MsIonisation  msIonisation  = new MsIonisation();
                            msIonisation .setCategory(parser.getAttributeValue(null, "category"));
                            msIonisation .setValue(parser.getAttributeValue(null, "value"));
                            msRun.getMsInstrument().setMsIonisation(msIonisation);
                        }
                        // handle the MsMassAnalyzer
                        else if (parser.getLocalName().equals("msMassAnalyzer")) {
                            // check for the msInstrument
                            if (msRun.getMsInstrument() == null) {
                                throw new InvalidFileFormatException("mzXML 2.1 requires that you declare msMassAnalyzer elements within a msInstrument element.");
                            }
                            // get the info
                            MsMassAnalyzer msMassAnalyzer = new MsMassAnalyzer();
                            msMassAnalyzer.setCategory(parser.getAttributeValue(null, "category"));
                            msMassAnalyzer.setValue(parser.getAttributeValue(null, "value"));
                            msRun.getMsInstrument().setMsMassAnalyzer(msMassAnalyzer);
                        }
                        // handle the MsDetector
                        else if (parser.getLocalName().equals("msDetector")) {
                            // check for the msInstrument
                            if (msRun.getMsInstrument() == null) {
                                throw new InvalidFileFormatException("mzXML 2.1 requires that you declare msDetector elements within a msInstrument element.");
                            }
                            // get the info
                            MsDetector msDetector = new MsDetector();
                            msDetector.setCategory(parser.getAttributeValue(null, "category"));
                            msDetector.setValue(parser.getAttributeValue(null, "value"));
                            msRun.getMsInstrument().setMsDetector(msDetector);
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        // handle end of scan elements
                        if (parser.getLocalName().equals("scan")) {
                            // todo, pop out of the scan stack
                            inScan = false;
                            // pop the stack
                            stack.removeFirst();
                            break;
                        }
                        
                        // handle end of peaks elements
                        if (parser.getLocalName().equals("peaks")) {
                            inPeak = false;
                            keepParsing = false;
                            break;
                        }
                        
                        // handle end of precursorMz
                        if (parser.getLocalName().equals("precursorMz")) {
                            inPrecursorMz = false;
                            break;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        // if this is peak information, add it to the batch
                        if (inPeak) {
                            base64EncodedValues += parser.getText().trim();
                        }
                        // if this is the precursor m/z handle it
                        else if (inPrecursorMz) {
                            precursorMz = parser.getText().trim();
                        }
                        break;
                }
            }
            
            // if no data was found, return null
            if (!foundScan) {
                return null;
            }
            
            // buffer all the peaks
            List<Peak> peaks = new ArrayList();
            // parse all the peaks
            byte[] peakBytes = Base64.decode(base64EncodedValues);
            // make a data input steram
            DataInputStream peakStream = new DataInputStream(new ByteArrayInputStream(peakBytes));
            // find if the data is centroided
            boolean isCentroided = false;
            boolean isChargeDeconvoluted = false;
            boolean isDeisotoped = false;
            for (DataProcessing dp : msRun.getDataProcessings()) {
                isCentroided |= dp.isCentroided();
                isChargeDeconvoluted |= dp.isChargeDeconvolution();
                isDeisotoped |= dp.isDeisotoped();
            }
            // check for peak list specific centoiding, deisotoping, and charge deconvolution
            if (peaklist.isCentroided()){
                isCentroided = true;
            }
            if (peaklist.isDeisotoped()){
                isDeisotoped = true;
            }
            if (peaklist.isChargeDeconvolution()){
                isChargeDeconvoluted = true;
            }
            try {
                while (true) {
                    double massOverChargeInDaltons;
                    double intensity;
                    if (precision == null || precision.equals("32")) {
                        massOverChargeInDaltons = (double) peakStream.readFloat();
                        intensity = (double) peakStream.readFloat();
                    } else {
                        massOverChargeInDaltons = peakStream.readDouble();
                        intensity = peakStream.readDouble();
                    }
                    
                    // make a peak
                    GenericPeak gmp = new GenericPeak();
                    gmp.setMassOverCharge(massOverChargeInDaltons);
                    gmp.setIntensity(intensity);
                    // check for centroided data
                    if (isCentroided){
                        gmp.setCentroided(Peak.CENTROIDED);
                    }
                    // check for deisotoped
                    if (isDeisotoped){
                        gmp.setDeisotoped(Peak.DEISOTOPED);
                    }
                    // check for centroided data
                    if (isChargeDeconvoluted){
                        gmp.setMonoisotopic(Peak.MONOISOTOPIC);
                    }
                    // add the peak
                    peaks.add(gmp);
                }
            } catch (EOFException eof) {
                // close the data stream
                try { peakStream.close(); } catch (Exception e){}
            }
            
            // optionally make a tandem peak list
            if (precursorMz != null) {
                // make the parent ion
                GenericPeak gp = new GenericPeak();
                // try to parse the parent m/z
                try {
                    gp.setMassOverCharge(Double.parseDouble(precursorMz));
                } catch (NumberFormatException e){
                    // noop
                }
                try {
                    gp.setIntensity(Double.parseDouble(precursorIntensity));
                } catch (NumberFormatException e){
                    // noop
                }
                // set the parent peak
                peaklist.setParentPeak(gp);
            }
            
            // set the peaks
            peaklist.setPeaks(peaks.toArray(new Peak[0]));
            // return the peak list
            return peaklist;
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Invalid XML!", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Premature end of file! Aborting.", ex);
        }
    }
    
    public MsRun getMsRun() {
        return msRun;
    }
    
    public void setMsRun(MsRun msRun) {
        this.msRun = msRun;
    }
}
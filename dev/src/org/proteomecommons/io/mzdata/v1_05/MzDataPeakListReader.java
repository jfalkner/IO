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
package org.proteomecommons.io.mzdata.v1_05;

import java.io.FileInputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.proteomecommons.io.*;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.mzxml.Base64;
import org.proteomecommons.io.util.UnicodeReader;

/**
 * Reads peak information from mzData files.
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 * @version for mzData 1.05
 */
public class MzDataPeakListReader extends GenericPeakListReader {
    //parsing sources
    private XMLStreamReader parser;
//    private FileInputStream in;
//    private InputStream in;
    private Reader in;
    
    //the current parser event
    private int event = XMLStreamConstants.SPACE;
    
    //the current state of the parser
    private boolean inProcessingMethod = false;
    
    private boolean inSpectrum = false;
    
    private boolean inMzData = false;
    
    private boolean inIntensityData = false;
    
    private boolean readyToDecodePeakData = false;
    
    private boolean nextMzUpdated = false;
    
    private boolean nextIntensityUpdated = false;
    
    //processingMethod variables
    private boolean deisotoped = false;
    
    private boolean chargeDeconvolved = false;
    
    private boolean centroided = false;
    
    //info about the current peaks
    private ByteBuffer currentMzBytes;
    
    private ByteBuffer currentIntensityBytes;
    
    // handle the two precisions differently
    private int currentMzPrecision = 0;
    private int currentIntensityPrecision = 0;
    
    private boolean currentByteOrderIsBig = false;
    
    // handle the peaks
    private GenericPeak parent = null;
    ArrayList<Peak> peaks = new ArrayList();
    
    private int tandemCount = PeakList.UNKNOWN_TANDEM_COUNT;
    
    /**
     * Create a reader for mzdata files
     *
     * @param in
     *            the input stream to read
     */
    public MzDataPeakListReader(String filename) {
        super(filename);
        try {
//            in = new UnicodeInputStream(new FileInputStream(filename), null);
            in = new UnicodeReader(new FileInputStream(filename), null);
            XMLInputFactory factory = XMLInputFactory.newInstance();
//            factory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.FALSE);
//            factory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
            parser = factory.createXMLStreamReader(in);
            
            
//            /* Find the first set of peaks to initialize the reader. */
//            findMorePeaks();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Parses the xml to update both the next mz and next intensity values.
     *
     * @return true if there were more values found, false if the file is
     *         exhausted
     */
    private boolean findMorePeaks() throws XMLStreamException {
        while (event != XMLStreamConstants.END_DOCUMENT) {
            try {
                event = parser.next();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            
            //process opening tags
            if (event == XMLStreamConstants.START_ELEMENT) {
                // handle spectrum information
                if (parser.getLocalName().equals("spectrum")) {
                    inSpectrum = true;
                    //and reset precursor info in case a previous spectrum
                    // specified it
                    parent = null;
                    tandemCount = PeakList.UNKNOWN_TANDEM_COUNT;
                    // acqInstrument in mzData version 1.04, spectrumInstrument in 1.05
                }
                // handle acquisition information
                else if (parser.getLocalName().equals("acqInstrument") ||
                        parser.getLocalName().equals("spectrumInstrument")) {
                    String msLevel = parser.getAttributeValue(null, "msLevel");
                    if (msLevel != null) tandemCount = Integer.parseInt(msLevel);
                    // handle the data
                    if (tandemCount > 1) {
                        parent = new GenericPeak();
                    } else {
                        parent = null;
                    }
                }
                // handle those cvParam elements that PSI loves to use
                else if (parser.getLocalName().equals("cvParam")) {
                    //check for precursor info
                    String cvName = parser.getAttributeValue(null, "name");
                    String cvAccession = parser.getAttributeValue(null, "accession");
                    String cvValue = parser.getAttributeValue(null, "value");
                    if (cvName != null && cvValue != null) {
                        cvHelper(cvName, cvAccession, cvValue);
                    }
                }
                // handle mz data
                else if (parser.getLocalName().equals("mzArrayBinary")) {
                    inMzData = true;
                }
                // handle intensity data
                else if (parser.getLocalName().equals("intenArrayBinary")) {
                    inIntensityData = true;
                }
                // handle data
                else if (parser.getLocalName().equals("data")) {
                    if (inMzData || inIntensityData) {
                        readyToDecodePeakData = true;
                        // check for precisions
                        if (inMzData) currentMzPrecision = Integer.parseInt(parser.getAttributeValue(null, "precision"));
                        if (inIntensityData) currentIntensityPrecision = Integer.parseInt(parser.getAttributeValue(null, "precision"));
                        
                        String nextByteOrderText = parser.getAttributeValue(null, "endian");
                        currentByteOrderIsBig = nextByteOrderText.equals("big");
                    }
                }
                // handle information about the processing method
                else if (parser.getLocalName().equals("processingMethod")) {
                    inProcessingMethod = true;
                }
            }
            
            //process text between tags
            if (event == XMLStreamConstants.CHARACTERS) {
                if (readyToDecodePeakData) {
                    if (inMzData) {
                        nextMzUpdated = updateMz(parser.getText());
                    }
                    if (inIntensityData) {
                        nextIntensityUpdated = updateIntensity(parser.getText());
                    }
                    if (nextMzUpdated && nextIntensityUpdated) {
                        //we're done. Reset state and return
                        nextMzUpdated = false;
                        nextIntensityUpdated = false;
//                        return true;
                    }
                }
            }
            
            //process closing tags
            if (event == XMLStreamConstants.END_ELEMENT) {
                if (parser.getLocalName().equals("spectrum")) {
                    inSpectrum = false;
                    // parse all of the peaks
                    peaks.clear();
                    try {
                        while (currentMzBytes.hasRemaining()) {
                            peaks.add(next());
                        }
                    } catch (Exception e) {
                        int stop = 2;
                    }
                    // return true
//                    if (parent != null) {
                    return true;
//                    }
//                    return false;
                } else if (parser.getLocalName().equals("mzArrayBinary")) {
                    inMzData = false;
                } else if (parser.getLocalName().equals("intenArrayBinary")) {
                    inIntensityData = false;
                } else if (parser.getLocalName().equals("data")) {
                    if (inMzData || inIntensityData) {
                        readyToDecodePeakData = false;
                    }
                } else if (parser.getLocalName().equals("processingMethod")) {
                    inProcessingMethod = false;
                }
            }
            
        } //end loop
        
        //Couldn't find any more peaks from the xml
        return false;
    }
    
    /**
     * Process encoded mz values
     *
     * @return true if mz is updated
     */
    private boolean updateMz(String encodedPeakData) {
        byte[] nextMzBytes = Base64.decode(encodedPeakData);
        currentMzBytes = ByteBuffer.wrap(nextMzBytes);
        
        if (currentByteOrderIsBig) {
            currentMzBytes = currentMzBytes.order(ByteOrder.BIG_ENDIAN);
        } else {
            currentMzBytes = currentMzBytes.order(ByteOrder.LITTLE_ENDIAN);
        }
        return true;
    }
    
    /**
     * Process encoded intensity values
     *
     * @return true if intensity is updated
     */
    private boolean updateIntensity(String encodedPeakData) {
        byte[] nextIntensityBytes = Base64.decode(encodedPeakData);
        currentIntensityBytes = ByteBuffer.wrap(nextIntensityBytes);
        
        if (currentByteOrderIsBig) {
            currentIntensityBytes = currentIntensityBytes
                    .order(ByteOrder.BIG_ENDIAN);
        } else {
            currentIntensityBytes = currentIntensityBytes
                    .order(ByteOrder.LITTLE_ENDIAN);
        }
        return true;
    }
    
    /**
     * State dependant parsing of data out of the name and value attributes of a
     * cvParam tag
     *
     * @param cvName
     *            the value of the cvParam tag's name attribute
     * @param cvValues
     *            the value of the cvParam tag's value attribute
     */
    private void cvHelper(String cvName, String cvAccession, String cvValue) {
        //parsing dependant on being in a spectrum tag
        if (inSpectrum) {
            if (cvAccession.equals("PSI:1000040")) {
                parent.setMassOverCharge(Double.parseDouble(cvValue));
            } else if (cvAccession.equals("PSI:1000042")) {
                parent.setIntensity(Double.parseDouble(cvValue));
            } else if (cvAccession.equals("PSI:1000041")) {
                parent.setCharge(Integer.parseInt(cvValue));
            }
        }
        
        //parsing of processingMethod
        if (inProcessingMethod) {
            if (cvName.equals("deisotoped")) {
                deisotoped = Boolean.valueOf(cvValue).booleanValue();
            } else if (cvName.equals("chargeDeconvolved")) {
                chargeDeconvolved = Boolean.valueOf(cvValue).booleanValue();
            } else if (cvName.toLowerCase().equals("peakprocessing")) {
                //check for centroid, centroided, CentroidMassSpectrum, discrete... the identifiers vary among files
                centroided = cvValue.toLowerCase().startsWith("centroid") || cvValue.toLowerCase().startsWith("discrete");
            }
        }
        
        //add any additional states to parse from... i.e. analyzer
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#close()
     */
    public void close() {
        try { parser.close(); } catch (Exception e){}
        try { in.close(); } catch (Exception e){}
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#next()
     */
    public Peak next() {
        double mz = -1;
        double inten = -1;
        if (currentMzPrecision == 32) mz = (double) currentMzBytes.getFloat();
        else mz = currentMzBytes.getDouble();
        
        // handle intensity precision differently
        if(currentIntensityPrecision == 32) inten = (double) currentIntensityBytes.getFloat();
        else inten = currentIntensityBytes.getDouble();
        
        // check if it is monoisotopic
        if (centroided && deisotoped) {
            GenericPeak gmp = new GenericPeak();
            gmp.setMassOverCharge(mz);
            gmp.setIntensity(inten);
            gmp.setCentroided(Peak.CENTROIDED);
            gmp.setMonoisotopic(Peak.MONOISOTOPIC);
            return gmp;
        }
        // check if it is just centroided
        else if (centroided) {
            GenericPeak gcp = new GenericPeak();
            gcp.setMassOverCharge(mz);
            gcp.setIntensity(inten);
            gcp.setCentroided(Peak.CENTROIDED);
            return gcp;
        }
        // fall back on a generic peak
        GenericPeak gp = new GenericPeak();
        gp.setMassOverCharge(mz);
        gp.setIntensity(inten);
        return gp;
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#hasNext()
     */
    public boolean hasNext() {
        if (currentMzBytes == null) {
            return false;
        }
        if (!centroided) {
            return false; //peak lists are centroided
        }
        if (currentMzBytes.hasRemaining()) {
            return true;
        } else {
            try { //getting more peaks from the file
                return findMorePeaks();
            } catch (XMLStreamException xe) {
                throw new RuntimeException("error reading more peaks from the xml", xe);
            }
        }
    }
    
    /**
     * @see org.proteomecommons.io.PeakListReader#isStartOfPeakList()
     */
    public boolean isStartOfPeakList() {
//        if (currentMzBytes != null && centroided) {
        if (currentMzBytes != null) {
            return currentMzBytes.position() == 0;
        } else {
            return false;
        }
    }
    
    
    /**
     * @see org.proteomecommons.io.GenericPeakListReader#getParent()
     */
    public Peak getParent() {
        return parent;
    }
    
    
    
    /**
     * TODO: refactor this to completely replace the old memory-efficient code.
     */
    public PeakList getPeakList() {
        try {
            // if there are no more peak lists, skip
//            if (!isStartOfPeakList()) {
//                return null;
//            }
            
            if (!findMorePeaks()) {
                return null;
            }
            
            // get the current parent peak
            Peak currentParent = getParent();
            
//            // load the peaks in to memory
//            LinkedList temp = new LinkedList();
//            // load first/null check
//            Peak p = next();
//            if (p != null) {
//                // add the first peak
//                temp.add(p);
//            }
//            // read all the peaks up to the next peak list
//            while (hasNext() && !isStartOfPeakList()) {
//                temp.add(next());
//            }
            
            // fall back on a peaklist
            GenericPeakList peaklist = new GenericPeakList();
            peaklist.setTandemCount(tandemCount);
            peaklist.setParentPeak(currentParent);
            // convert to an array of peaks
            peaklist.setPeaks(peaks.toArray(new Peak[0]));
            return peaklist;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
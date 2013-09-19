/*
 *    Copyright 2005-2007 The Regents of the University of Michigan
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
package org.proteomecommons.io.dataxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakList;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DataXmlPeakListReader extends GenericPeakListReader {
    File file;
    // the element
    DataXML dataXML;
    // index to the list
    int index = -1;
    List<Spectrum> spectra;
    public DataXmlPeakListReader(String filename) {
        super(filename);
        // make the file
        this.file = new File(filename);
        try {
            // load the data
            JAXBContext jaxbContext = JAXBContext.newInstance("org.proteomecommons.io.dataxml");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<DataXML> bookingElement = (JAXBElement<DataXML>) unmarshaller.unmarshal(file);
            // set the root node
            dataXML = bookingElement.getValue();
            // set the spectra
            spectra = dataXML.getSpectrumList().getSpectrum();
        } catch (JAXBException ex) {
            throw new RuntimeException("Can't load dataXML file!", ex);
        }
    }
    
    public PeakList getPeakList() {
        // increment the index
        index++;
        // if out of index, throw null
        if (index >= spectra.size()) {
            return null;
        }
        // make a peak list out of the next spectra
        Spectrum s = spectra.get(index);
        SpectrumData sd = s.getSpectrumData();
        List<BinaryDataArray> binaryData = sd.getBinaryData();
        
        // get the m/z and intensity values
        BinaryDataArray mz = null;
        BinaryDataArray intensities = null;
        for (BinaryDataArray bda : binaryData) {
            // get the mz array
            if (bda.getCvParam().get(0).getValue().equals("MassToChargeRatioArray")) {
                mz = bda;
            }
            // get the intensity array
            if (bda.getCvParam().get(0).getValue().equals("IntensityArray")) {
                intensities = bda;
            }
        }
        
        // convert to a peak list
        double[] mzValues = decode(mz);
        double[] intensityValues = decode(intensities);
        
        // start making the peak list
        GenericPeakList gpl = new GenericPeakList();
        
        // try to get precursor info
        List<Precursor> precursors = s.getSpectrumHeader().getPrecursorList().getPrecursor();
        if (precursors.size() > 0) {
            // make a parent peak
            GenericPeak parent = new GenericPeak();
            Precursor pre = precursors.get(0);
            ParamGroup ionSelect = pre.getIonSelection();
            // get all the params
            List<CVParam> params = ionSelect.getCvParam();
            for (CVParam param : params) {
                if (param.getName().equals("MassToChargeRatio"))
                    parent.setMassOverCharge(Double.parseDouble(param.getValue()));
                if (param.getName().equals("ChargeState"))
                    parent.setCharge(Integer.parseInt(param.getValue()));
                if (param.getName().equals("Intensity"))
                    parent.setIntensity(Double.parseDouble(param.getValue()));
            }
            // add to the peak list
            gpl.setParentPeak(parent);
            
        }
        
        // make a peak list
        ArrayList<Peak> peaks = new ArrayList();
        for (int i=0;i<mzValues.length;i++) {
            GenericPeak gp = new GenericPeak();
            gp.setIntensity(intensityValues[i]);
            gp.setMassOverCharge(mzValues[i]);
            peaks.add(gp);
        }
        gpl.setPeaks(peaks.toArray(new Peak[0]));
        
        // return the newly made peak list
        return gpl;
    }
    
    private double[] decode(BinaryDataArray bda) {
        // figure out how many floats are in there
        double[] toReturn = new double[bda.getArrayLength().intValue()/8];
        try {
            // get the data
            byte[] bytes = bda.getBinary();
            // optionally decompress GZIP
            if (bda.getCompressionType().equals("GZIP")) {
                // assume GZIP for now
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int i=gis.read();i!=-1;i=gis.read()) {
                    baos.write((byte)i);
                }
                bytes = baos.toByteArray();
            }
            // make a data input stream
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            for (int i=0;i<toReturn.length;i++) {
                toReturn[i] = bb.getDouble();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Can't decode the data.", ex);
        }
        
        return toReturn;
    }
    
    public void close() {
        // noop
    }
}

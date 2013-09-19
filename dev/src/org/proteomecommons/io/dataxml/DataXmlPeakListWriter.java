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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.PeakListReader;
import org.proteomecommons.io.PeakListWriter;

/**
 *
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class DataXmlPeakListWriter implements PeakListWriter {
    String filename;
    // keep a data structure for the file
    DataXML dataXML = new DataXML();
    
    public DataXmlPeakListWriter(String filename) {
        this.filename = filename;
        // set up the data XML
        dataXML.setSpectrumList(new SpectrumList());
        
        // set the IO Framework as default software
        List<Software> software = new ArrayList();
        Software s = new Software();
        s.setName("ProteomeCommons.org IO Framework");
        s.setVersion("6.3");
        s.setId("IO Framework 6.3");
        software.add(s);
        dataXML.softwareList = new SoftwareList();
        dataXML.softwareList.software = software;
        
        // make an admin
        dataXML.admin = new Admin();
        dataXML.admin.sourceFileList = new SourceFileList();
        dataXML.admin.sourceFileList.sourceFile = new ArrayList<SourceFile>();
        // add in info about the soruce-file
        List<SourceFile> sourceFiles = dataXML.admin.getSourceFileList().getSourceFile();
        SourceFile sf = new SourceFile();
        sf.setFileName(new File(filename).getName());
        sf.setFilePath(filename);
        sourceFiles.add(sf);
        
        // add in contact info
        List<Contact> contacts = dataXML.getAdmin().getContact();
        Contact contact = new Contact();
        contact.setFirst("Jayson");
        contact.setLast("Falkner");
        contact.setEmail("jfalkner@umich.edu");
        contact.setURI("http://www.proteomecommons.org/current/531");
        contacts.add(contact);
        
        // list the processing steps
        dataXML.dataProcessingList = new DataProcessingList();
        dataXML.dataProcessingList.dataProcessing = new ArrayList<DataProcessing>();
        List<DataProcessing> dataProcessing = dataXML.getDataProcessingList().getDataProcessing();
        DataProcessing dp = new DataProcessing();
        dp.setSoftwareRef("http://www.proteomecommons.org/current/531");
        dp.setId(s.getId());
        dp.setOrder(1);
        dataProcessing.add(dp);
    }
    
    public void write(PeakList peaklist) {
        try {
            // add a spectra
            Spectrum spectrum = new Spectrum();
            
            // make up parent peak info
            spectrum.setSpectrumHeader(new SpectrumHeader());
            spectrum.getSpectrumHeader().setPrecursorList(new PrecursorList());
            spectrum.getSpectrumHeader().getPrecursorList().precursor = new ArrayList<Precursor>();
            Precursor pre = new Precursor();
            spectrum.getSpectrumHeader().getPrecursorList().getPrecursor().add(pre);
            pre.setIonSelection(new ParamGroup());
            pre.getIonSelection().cvParam = new ArrayList<CVParam>();
            // check for known parent m/z
            if (peaklist.getParentPeak().getMassOverCharge() != Peak.UNKNOWN_MZ) {
                CVParam cp = new CVParam();
                cp.setAccession("PSI:1000040");
                cp.setCvLabel("psi");
                cp.setName("MassToChargeRatio");
                cp.setValue(Double.toString(peaklist.getParentPeak().getMassOverCharge()));
                pre.getIonSelection().getCvParam().add(cp);
            }
            // check for known parent intensity
            if (peaklist.getParentPeak().getIntensity() != Peak.UNKNOWN_INTENSITY) {
                CVParam cp = new CVParam();
                cp.setAccession("PSI:1000042");
                cp.setCvLabel("psi");
                cp.setName("Intensity");
                cp.setValue(Double.toString(peaklist.getParentPeak().getIntensity()));
                pre.getIonSelection().getCvParam().add(cp);
            }
            // check for known parent intensity
            if (peaklist.getParentPeak().getCharge() != Peak.UNKNOWN_CHARGE) {
                CVParam cp = new CVParam();
                cp.setAccession("PSI:1000041");
                cp.setCvLabel("psi");
                cp.setName("ChargeState");
                cp.setValue(Integer.toString(peaklist.getParentPeak().getCharge()));
                pre.getIonSelection().getCvParam().add(cp);
            }
            
            // make up the data
            SpectrumData data = new SpectrumData();
            List<BinaryDataArray> bds = data.getBinaryData();
            // make a binary array for the intensities and m/z values
            Peak[] peaks = peaklist.getPeaks();
            ByteArrayOutputStream mzOut = new ByteArrayOutputStream();
            ByteArrayOutputStream intensityOut = new ByteArrayOutputStream();
            DataOutputStream a = new DataOutputStream(mzOut);
            DataOutputStream b = new DataOutputStream(intensityOut);
            for (int i=0;i<peaks.length;i++) {
                a.writeDouble(peaks[i].getMassOverCharge());
                b.writeDouble(peaks[i].getIntensity());
            }
            // make the arrays
            BinaryDataArray mz = new BinaryDataArray();
            mz.setCompressionType("NONE");
            mz.setBinary(mzOut.toByteArray());
            mz.setArrayLength(mzOut.size());
            mz.setEncodedLength(mzOut.size());
            data.getBinaryData().add(mz);
            // add the cv param
            CVParam mzParam = new CVParam();
            mzParam.setAccession("PSI:9999920");
            mzParam.setName("DataArrayContentType");
            mzParam.setValue("MassToChargeRatioArray");
            mzParam.setCvLabel("PSI");
            mz.cvParam = new ArrayList<CVParam>();
            mz.getCvParam().add(mzParam);
            // make the intensity array too
            BinaryDataArray intensities = new BinaryDataArray();
            intensities.setCompressionType("NONE");
            intensities.setBinary(intensityOut.toByteArray());
            intensities.setArrayLength(intensityOut.size());
            intensities.setEncodedLength(intensityOut.size());
            data.getBinaryData().add(intensities);
            // add the cv param
            CVParam intensityParam = new CVParam();
            intensityParam.setAccession("PSI:9999921");
            intensityParam.setName("DataArrayContentType");
            intensityParam.setValue("IntensityArray");
            intensityParam.setCvLabel("PSI");
            intensities.cvParam = new ArrayList<CVParam>();
            intensities.getCvParam().add(intensityParam);
            // add the data
            spectrum.setSpectrumData(data);
            // add the spectrum
            dataXML.getSpectrumList().getSpectrum().add(spectrum);
        } catch (IOException ex) {
            throw new RuntimeException("Can't write peak list in dataXML format.", ex);
        }
    }
    
    public void close() {
        try {
            // serialize the whole thing
            JAXBContext jaxbContext = JAXBContext.newInstance("org.proteomecommons.io.dataxml");
            Marshaller marshaller = jaxbContext.createMarshaller();
            JAXBElement<DataXML> data = (new ObjectFactory()).createDataXML(dataXML);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            // write out to the file
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream(filename);
                bos = new BufferedOutputStream(fos);
                marshaller.marshal(data, bos);
                bos.flush();
                fos.flush();
                bos.close();
                fos.close();
            }
            finally {
                try {bos.close(); } catch (Exception e){}
                try {fos.close(); } catch (Exception e){}
            }
        } catch (Exception ex) {
            throw new RuntimeException("Can't create dataXML file!", ex);
        }
    }
    
    
    
    public static void main(String[] args) throws Exception {
        // read in the example peak list
        PeakListReader plr = GenericPeakListReader.getPeakListReader("C:/Documents and Settings/Jayson/Desktop/example.mgf");
        PeakList pl = plr.getPeakList();
        
        DataXmlPeakListWriter plw = new DataXmlPeakListWriter("C:/Documents and Settings/Jayson/Desktop/example.mgf.dataXML");
        plw.write(pl);
        plw.close();
    }
    
}

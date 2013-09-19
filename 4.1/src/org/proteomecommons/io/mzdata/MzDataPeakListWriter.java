/*
 * Created on Mar 26, 2005
 *
 */
package org.proteomecommons.io.mzdata;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.proteomecommons.io.*;
import org.proteomecommons.io.mzxml.Base64;

/**
 * Writes PeakLists out to the specified output by following the mzData convention.
 *
 * @author Jarret Falkner - jar@cs.washington.edu
 * @author Jayson Falkner - jfalkner@umich.edu
 */
public class MzDataPeakListWriter extends GenericPeakListWriter {
    //a temporary list for usage of startPeakList and write(Peak)
    private ArrayList building = null;
    
    //all of the lists that will end up in the xml
    private ArrayList peaklists = new ArrayList();
    
    private XMLStreamWriter xmlw;
    
    /**
     * Make an MzDataPeakListWriter that defaults to using Unnamed or Unspecified for all of the variable metadata in the document.
     * @param out the stream to write to
     */
    public MzDataPeakListWriter(OutputStream out){
        this(out,
                "Unnamed Sample",
                "Unnamed Source File",
                "Unspecified File Type",
                "Unspecified Path to File",
                "Unspecified Contact Info",
                "Unspecified Institution",
                "Unspecified Sample Name",
                "Unspecified Instrument",
                "Unspecified Source",
                null, //analyzerComonent
                null, //detector
                "Unspecified Software Name",
                "Unspecified Software Version",
                null); //processingMethod
        
    }
    
    /**
     * Create an MzDataPeakListWriter with the specified metadata.
     * @param out the stream to write to
     * @param sampleName A short label that is referable to the sample used to generate the dataset.
     * @param nameOfFile Name of the source file, without reference to location (either URI or local path).
     * @param fileType Type of the file if appropriate, else a description of the software or reference resource used.
     * @param pathToFile URI-formatted full path to file, without actual file name appended.
     * @param contactInfo Phone number, email, postal address or other appropriate means of contact.
     * @param institution Academic or corporate organisation with which the contact person or role is associated.
     * @param name Contact person name, or role name of the individual responsible for this dataset.
     * @param instrumentName The name of the instrument used to generate this data.
     * @param source cv tag in instrument, commonly ESI or MALDI.
     * @param analyzerComponent The first component of the mass spectrometer, add additional components using addComponentToAnalyzerList.
     * @param detector cv tag in instrument, commonly MPC or EM.
     * @param softwareName The official name for the software package used.
     * @param softwareVersion The version number of the software package.
     * @param processingMethod Name-value pairs found in the processingMethod portion of dataProcessing.
     */
    public MzDataPeakListWriter(OutputStream out,
            String sampleName,
            String nameOfFile,
            String fileType,
            String pathToFile,
            String contactInfo,
            String institution,
            String name,
            String instrumentName,
            String source,
            Map analyzerComponent,
            Map detector,
            String softwareName,
            String softwareVersion,
            Map processingMethod){
        
        super(out);
        setSampleName(sampleName);
        setSourceFileInfo(nameOfFile, fileType, pathToFile);
        setContact(name, institution, contactInfo);
        setSampleName(sampleName);
        setSource(source);
        setSoftwareInfo(softwareName, softwareVersion);
        setDetectorInfo(detector);
        addComponentToAnalyzerList(analyzerComponent);
        setProcessingMethod(processingMethod);
    }
    //variables that can be set
    private String _SampleName = null;
    
    private String _NameOfFile;
    
    private String _FileType;
    
    private String _PathToFile;
    
    private String _ContactInfo;
    
    private String _Institution;
    
    private String _Name;
    
    private String _InstrumentName;
    
    private String _Source;
    
    private ArrayList _AnalyzerList = new ArrayList();
    
    private Map _Detector;
    
    private String _SoftwareName;
    
    private String _SoftwareVersion;
    
    private Map _ProcessingMethod;
    
    private void writeMetaInfo() throws XMLStreamException{
        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        // Set namespace prefix defaulting for all created writers
        //xmlof.setProperty("javax.xml.stream.isPrefixDefaulting",Boolean.TRUE);
        
        // Create an XML stream writer
        xmlw = xmlof.createXMLStreamWriter(getOutputStream());
        ///xmlw = new javanet.staxutils.IndentingXMLEventWriter(xmlw);
        
        // Write XML prologue
        xmlw.writeStartDocument();
        
        // Root element
        xmlw.writeCharacters("\n");
        xmlw.writeStartElement("mzData");
        //Set root attributes
        xmlw.writeAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        xmlw.writeAttribute("xsi:noNamespaceSchemaLocation","http://psidev.sourceforge.net/ms/xml/mzdata/mzdata.xsd");
        xmlw.writeAttribute("version","1.05");
        
        //Set description elements
        xmlw.writeCharacters("\n\t");
        xmlw.writeStartElement("description");
        
        //Set admin elements
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeStartElement("admin");
        
        /* Sample info */
        if(_SampleName != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("sampleName");
            xmlw.writeCharacters(_SampleName);
            xmlw.writeEndElement();
        }
        
        /* Source file information */
        if(_NameOfFile != null || _FileType != null || _PathToFile != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("sourceFile");
            
            if(_NameOfFile != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("nameOfFile");
                xmlw.writeCharacters(_NameOfFile);
                xmlw.writeEndElement();
            }
            
            if(_PathToFile != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("pathToFile");
                xmlw.writeCharacters(_PathToFile);
                xmlw.writeEndElement();
            }
            
            if(_FileType != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("fileType");
                xmlw.writeCharacters(_FileType);
                xmlw.writeEndElement();
            }
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        
        /* Contact information */
        if(_ContactInfo != null || _Institution != null || _Name != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("contact");
            
            if(_Name != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("name");
                xmlw.writeCharacters(_Name);
                xmlw.writeEndElement();
            }
            
            if(_Institution != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("institution");
                xmlw.writeCharacters(_Institution);
                xmlw.writeEndElement();
            }
            
            if(_ContactInfo != null){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("contactInfo");
                xmlw.writeCharacters(_ContactInfo);
                xmlw.writeEndElement();
            }
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        //end admin section
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeEndElement();
        
        
        //Set instrument elements
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeStartElement("instrument");
        
        if(_InstrumentName != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("instrumentName");
            xmlw.writeCharacters(_InstrumentName);
            xmlw.writeEndElement();
        }
        
        /* source information */
        if(_Source != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("source");
            
            xmlw.writeCharacters("\n\t\t\t\t");
            writeCvTag("type",_Source);
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        /* analyzerList information */
        if (!_AnalyzerList.isEmpty()){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("analyzerList");
            xmlw.writeAttribute("count",(new Integer(_AnalyzerList.size())).toString());
            
            Iterator it = _AnalyzerList.iterator();
            while(it.hasNext()){
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeStartElement("analyzer");
                
                Map analyzerDisc = (Map)it.next();
                Iterator sit = analyzerDisc.keySet().iterator();
                while(sit.hasNext()){
                    Object name = sit.next();
                    xmlw.writeCharacters("\n\t\t\t\t\t");
                    writeCvTag(name.toString(), analyzerDisc.get(name).toString());
                }
                
                xmlw.writeCharacters("\n\t\t\t\t");
                xmlw.writeEndElement(); //analyzer
            }
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement(); //analyzerList
        }
        
        /* detector information */
        if(_Detector != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("detector");
            
            Iterator dit = _Detector.keySet().iterator();
            while(dit.hasNext()){
                Object name = dit.next();
                xmlw.writeCharacters("\n\t\t\t\t");
                writeCvTag(name.toString(), _Detector.get(name).toString());
            }
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeEndElement(); //end instrument section
        
        //Set dataProcessing elements
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeStartElement(null,"dataProcessing");
        
        /* Software information */
        if(_SoftwareName != null && _SoftwareVersion != null){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("software");
            
            xmlw.writeCharacters("\n\t\t\t\t");
            xmlw.writeStartElement("name");
            xmlw.writeCharacters(_SoftwareName);
            xmlw.writeEndElement();
            
            xmlw.writeCharacters("\n\t\t\t\t");
            xmlw.writeStartElement("version");
            xmlw.writeCharacters(_SoftwareVersion);
            xmlw.writeEndElement();
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        
        /* processingMethod information */
        if (_ProcessingMethod != null && !_ProcessingMethod.keySet().isEmpty()){
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeStartElement("processingMethod");
            
            Iterator pmi = _ProcessingMethod.keySet().iterator();
            
            while(pmi.hasNext()){
                xmlw.writeCharacters("\n\t\t\t\t");
                Object key = pmi.next();
                writeCvTag(key.toString(), _ProcessingMethod.get(key).toString());
            }
            
            xmlw.writeCharacters("\n\t\t\t");
            xmlw.writeEndElement();
        }
        
        //end dataProcessing section
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeEndElement();
        
        
        //end description section
        xmlw.writeCharacters("\n\t");
        xmlw.writeEndElement();
    }
    
    /**
     * Writes a CvParam tag to the xml document with the specified paramaters
     * @param name the value of the name attribute
     * @param value the value of the value attribute
     * @throws XMLStreamException
     */
    private void writeCvTag(String name, String value) throws XMLStreamException{
        xmlw.writeEmptyElement("cvParam");
        xmlw.writeAttribute("cvLabel","psi");
        xmlw.writeAttribute("accession","1");
        xmlw.writeAttribute("name", name);
        xmlw.writeAttribute("value", value);
    }
    
        /* (non-Javadoc)
         * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.PeakList)
         */
    public void write(PeakList peaklist) throws CheckedIOException {
// check the peaks
        Peak[] peaks = peaklist.getPeaks();
        for (int i=0;i<peaks.length;i++){
            checkProcessingParams(peaks[i]);
        }
        peaklists.add(peaklist);
        System.out.println("Added a full peak list of length " + peaklist.getPeaks().length);
    }
    
        /* (non-Javadoc)
         * @see org.proteomecommons.io.PeakListWriter#startPeakList()
         */
    public void startPeakList() throws CheckedIOException {
        if (building == null){
            building = new ArrayList();
        } else {
            GenericPeakList listToAdd = constructPeakListFromPeaks(building);
            constructPeakListFromPeaks(building);
            building = new ArrayList();
        }
    }
    
    private GenericPeakList constructPeakListFromPeaks(ArrayList peakList) {
        Iterator it = peakList.iterator();
        Peak[] peaks = new Peak[peakList.size()];
        for(int i = 0; i < peakList.size(); i++){
            peaks[i] = (Peak)peakList.get(i);
        }
        GenericPeakList newList = new GenericPeakList();
        newList.setPeaks(peaks);
        return newList;
    }
    
        /* (non-Javadoc)
         * @see org.proteomecommons.io.PeakListWriter#write(org.proteomecommons.io.Peak)
         */
    public void write(Peak peak) throws CheckedIOException {
        checkProcessingParams(peak);
        building.add(peak);
    }
    
    /**
     *Helper method to check if this is a monoisotopic and/or centroided peak list.
     */
    private void checkProcessingParams(Peak peak){
        // check monoisotopic/centroided
        if (!(peak instanceof CentroidedPeak)){
            // if not centroided, adjust the default setting accordingly
            _ProcessingMethod.put("peakProcessing", "none");
        }
        if (!(peak instanceof MonoisotopicPeak)){
            // if not deisotoped adjust the default setting
            _ProcessingMethod.put("deisotoped", "false");
        }
    }
    
    /**
     * Finish writing the xml.
     */
    public void close() throws CheckedIOException {
        System.out.println("I have "+ peaklists.size() +" peaklists to write.");
        //remember to add the last peak list if write(Peak) has been used
        if (building != null && !building.isEmpty()){
            constructPeakListFromPeaks(building);
        }
        
        try {
            writeMetaInfo();
        } catch (XMLStreamException e1) {
            throw new CheckedIOException( "couldn't write XML metadata.", e1);
        }
        
        try{
            //Write the contents of the spectrumList
            
            xmlw.writeCharacters("\n\t");
            xmlw.writeStartElement("spectrumList");
            
            xmlw.writeAttribute("count", new Integer(peaklists.size()).toString());
            
            //Write each peak list
            for(int spectrumId = 1; spectrumId <= peaklists.size(); spectrumId++){
                if (peaklists.get(spectrumId -1) == null) System.out.println("bum deal");
                else encodeAPeakList((PeakList)peaklists.get(spectrumId -1), spectrumId);
            }
            
            xmlw.writeCharacters("\n\t");
            xmlw.writeEndElement(); //end spectrumList tag
            
            // Write document end. This closes all open structures
            xmlw.writeCharacters("\n");
            xmlw.writeEndDocument();
            // Close the writer to flush the output
            xmlw.close();
        } catch (XMLStreamException xse){
            throw new CheckedIOException("Could not close the xml document", xse);
        }
        
        // let the superclass close now
        super.close();
    }
    
    private void encodeAPeakList(PeakList list, int spectrumId) throws XMLStreamException{
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeStartElement("spectrum");
        
        xmlw.writeAttribute("id", new Integer(spectrumId).toString());
        writeSpectrumDesc(list);
        
        //build byte arrays from the peak data
        Peak[] peaks = list.getPeaks();
        //if (peaks == null) System.out.println("found null peaks, wt?");
        int numPoints = peaks.length;
        int bytesForDoubles = numPoints*8;
        byte[] mz = new byte[bytesForDoubles];
        byte[] intensity = new byte[bytesForDoubles];
        
        //buffers for adding the doubles easily
        ByteBuffer mzBuff = ByteBuffer.wrap(mz);
        ByteBuffer intensityBuff = ByteBuffer.wrap(intensity);
        mzBuff = mzBuff.order(ByteOrder.LITTLE_ENDIAN);
        intensityBuff = intensityBuff.order(ByteOrder.LITTLE_ENDIAN);
        
        System.out.println("writing "+ list.getPeaks().length +" peaks");
        for(int i = 0; i < list.getPeaks().length; i++){
            mzBuff.putDouble(peaks[i].getMassOverCharge());
            intensityBuff.putDouble(peaks[i].getIntensity());
        }
        
        //make the base64 strings from the bytes
        String encodedMz = Base64.encodeBytes(mzBuff.array(), false);
        String encodedIntensity = Base64.encodeBytes(intensityBuff.array(), false);
        
        //add arrayBinary tags
        xmlw.writeCharacters("\n\t\t\t");
        xmlw.writeStartElement("mzArrayBinary");
        
        xmlw.writeCharacters("\n\t\t\t\t");
        xmlw.writeStartElement("data");
        xmlw.writeAttribute("precision", "64");
        xmlw.writeAttribute("endian", "little");
        xmlw.writeAttribute("length", new Integer(numPoints).toString());
        xmlw.writeCharacters(encodedMz);
        
        xmlw.writeEndElement(); //data
        
        xmlw.writeCharacters("\n\t\t\t");
        xmlw.writeEndElement(); //mzArrayBinary
        
        xmlw.writeCharacters("\n\t\t\t");
        xmlw.writeStartElement("intenArrayBinary");
        
        xmlw.writeCharacters("\n\t\t\t\t");
        xmlw.writeStartElement("data");
        xmlw.writeAttribute("precision", "64");
        xmlw.writeAttribute("endian", "little");
        xmlw.writeAttribute("length", new Integer(numPoints).toString());
        xmlw.writeCharacters(encodedIntensity);
        xmlw.writeEndElement(); //data
        
        xmlw.writeCharacters("\n\t\t\t");
        xmlw.writeEndElement(); //intenArrayBinary
        
        xmlw.writeCharacters("\n\t\t");
        xmlw.writeEndElement(); //end spectrum tag
    }
    
    private void writeSpectrumDesc(PeakList list) throws XMLStreamException {
        if(list instanceof MzDataPeakList){
            MzDataPeakList mzlist = (MzDataPeakList)list;
            
            System.out.println("processing metadata from an mzDataPeakList");
            
            if(mzlist.getSpectrumSettings() != null || mzlist.getPrecursorList() != null){
                xmlw.writeCharacters("\n\t\t\t");
                xmlw.writeStartElement("spectrumDesc");
                
                MzDataPeakListSpectrumSettings settings = mzlist.getSpectrumSettings();
                if(settings != null){
                    xmlw.writeCharacters("\n\t\t\t\t");
                    xmlw.writeStartElement("spectrumSettings");
                    
                    
                    xmlw.writeCharacters("\n\t\t\t\t\t");
                    xmlw.writeStartElement("acqSpecification");
                    
                    if(settings.spectrumType() != null){
                        xmlw.writeAttribute("spectrumType", settings.spectrumType());
                    }
                    if(settings.methodOfCombination() != null){
                        xmlw.writeAttribute("methodOfCombination", settings.methodOfCombination());
                    }
                    if(settings.acqNumberIsSpecified()){
                        xmlw.writeAttribute("count", "1");
                        
                        xmlw.writeCharacters("\n\t\t\t\t\t\t");
                        xmlw.writeEmptyElement("acquisition");
                        xmlw.writeAttribute("acqNumber", new Integer(settings.acqNumber()).toString());
                    }
                    
                    xmlw.writeCharacters("\n\t\t\t\t\t");
                    xmlw.writeEndElement(); // acqSpecification
                    
                    xmlw.writeCharacters("\n\t\t\t\t\t");
                    xmlw.writeStartElement("spectrumInstrument");
                    xmlw.writeAttribute("msLevel", new Integer(settings.msLevel()).toString());
                    
                    if(settings.mzRangeIsSpecified()){
                        xmlw.writeAttribute("mzRangeStart", new Float(settings.getMzRangeStart()).toString());
                        xmlw.writeAttribute("mzRangeStop", new Float(settings.getMzRangeEnd()).toString());
                    }
                    
                    Map instrumentParams = settings.getSpectrumInstrumentParamaters();
                    if(instrumentParams != null){
                        Set params = instrumentParams.keySet();
                        Iterator it = params.iterator();
                        while(it.hasNext()){
                            Object name = it.next();
                            xmlw.writeCharacters("\n\t\t\t\t\t\t");
                            writeCvTag(name.toString(), instrumentParams.get(name).toString());
                        }
                    }
                    
                    xmlw.writeCharacters("\n\t\t\t\t\t");
                    xmlw.writeEndElement(); // spectrumInstrument
                    
                    xmlw.writeCharacters("\n\t\t\t\t");
                    xmlw.writeEndElement(); //spectrumSettings
                }
                
                MzDataPeakListPrecursorList plist = mzlist.getPrecursorList();
                if(plist != null){
                    System.out.println("doing a plist print");
                    xmlw.writeCharacters("\n\t\t\t\t");
                    xmlw.writeStartElement("precursorList");
                    xmlw.writeAttribute("count", new Integer(plist.getPrecursors().size()).toString());
                    
                    Iterator it = plist.getPrecursors().iterator();
                    while(it.hasNext()){
                        MzDataPeakListPrecursorListItem item = (MzDataPeakListPrecursorListItem)it.next();
                        
                        xmlw.writeCharacters("\n\t\t\t\t\t");
                        xmlw.writeStartElement("precursor");
                        
                        xmlw.writeAttribute("msLevel", new Integer(item.msLevel()).toString());
                        xmlw.writeAttribute("spectrumRef", new Integer(item.spectrumRef()).toString());
                        
                        Map ionSelection = item.getIonSelection();
                        if(ionSelection != null){
                            xmlw.writeCharacters("\n\t\t\t\t\t\t");
                            xmlw.writeStartElement("ionSelection");
                            
                            Iterator iit = ionSelection.keySet().iterator();
                            while(iit.hasNext()){
                                Object name = iit.next();
                                xmlw.writeCharacters("\n\t\t\t\t\t\t\t");
                                writeCvTag(name.toString(), ionSelection.get(name).toString());
                            }
                            
                            xmlw.writeCharacters("\n\t\t\t\t\t\t");
                            xmlw.writeEndElement();
                        }
                        
                        Map activation = item.getActivation();
                        if(activation != null){
                            xmlw.writeCharacters("\n\t\t\t\t\t\t");
                            xmlw.writeStartElement("activation");
                            
                            Iterator ait = activation.keySet().iterator();
                            while(ait.hasNext()){
                                Object name = ait.next();
                                xmlw.writeCharacters("\n\t\t\t\t\t\t\t");
                                writeCvTag(name.toString(), activation.get(name).toString());
                            }
                            
                            xmlw.writeCharacters("\n\t\t\t\t\t\t");
                            xmlw.writeEndElement();
                        }
                        
                        xmlw.writeCharacters("\n\t\t\t\t\t");
                        xmlw.writeEndElement(); //precursor
                    }
                    
                    xmlw.writeCharacters("\n\t\t\t\t");
                    xmlw.writeEndElement();
                } else {
                    System.out.println("plist null");
                }
                
                xmlw.writeCharacters("\n\t\t\t");
                xmlw.writeEndElement(); //spectrumDesc
            }
        }
    }
    
    
    //The following are based on code generated from a JAXB compilation of the mzdata schema
    
    /**
     * @param value A short label that is referable to the sample used to generate the dataset.
     *
     */
    public void setSampleName(String value) {
        _SampleName = value;
    }
    
    /**
     * @param nameOfFile Name of the source file, without reference to location (either URI or local path).
     * @param fileType Type of the file if appropriate, else a description of the software or reference resource used.
     * @param pathToFile URI-formatted full path to file, without actual file name appended.
     */
    public void setSourceFileInfo(String nameOfFile, String fileType, String pathToFile){
        _NameOfFile = nameOfFile;
        _FileType = fileType;
        _PathToFile = pathToFile;
    }
    
    /**
     * @param name Contact person name, or role name of the individual responsible for this dataset.
     * @param institution Academic or corporate organisation with which the contact person or role is associated.
     * @param contactInfo Phone number, email, postal address or other appropriate means of contact.
     */
    public void setContact(String name, String institution, String contactInfo){
        _Name = name;
        _Institution = institution;
        _ContactInfo = contactInfo;
    }
    
    /**
     * Common examples are LCQ Deca XP and AXIMA-CFR.
     * @param value The name of the instrument used to generate this data.
     */
    public void setInstrumentName(String value) {
        _InstrumentName = value;
    }
    
    /**
     * @param value cv tag in instrument, commonly ESI or MALDI.
     */
    public void setSource(String value) {
        _Source = value;
    }
    
    /**
     * Add a component to the mass analyzer component list.  These components should be added in an order that reflects the physical order of the desribed components in the mass spectrometer.
     * @param component a component of the mass spectrometer.
     */
    public void addComponentToAnalyzerList(Map component){
        if (component != null){
            _AnalyzerList.add(component);
        }
    }
    
    /**
     * A map of information name-value pairs about the detector.  Each item in this map is written as a CV tag.
     * For example, type => EM
     * @param di the name-value pairs that describe the detector.
     */
    public void setDetectorInfo(Map di){
        _Detector = di;
    }
    
    /**
     * Specify the values for name and version of the software used to generate this document.
     *
     */
    public void setSoftwareInfo(String name, String version){
        _SoftwareName = name;
        _SoftwareVersion = version;
    }
    
    /**
     * Specify the name-value information to be used in the processingMethod tag.
     * For example, peakProcessing => centroided, chargeDeconvolved => true, and deisotoped => true.
     * @param pm the name-value pairs that describe the processingMethod. If pm is null, the writer assumes a deisotoped, centroided peaklist.
     */
    public void setProcessingMethod(Map pm){
        if (pm == null){ //assume some defaults to make this a centroided peaklist
            pm = new HashMap();
            pm.put("deisotoped", "true");
            pm.put("peakProcessing", "centroided");
        }
        _ProcessingMethod = pm;
        
    }
}

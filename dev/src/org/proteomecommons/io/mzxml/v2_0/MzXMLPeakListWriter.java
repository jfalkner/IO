/**
 * Properly describe this file
 * @author Jarret jar@cs.washington.edu
 */
package org.proteomecommons.io.mzxml.v2_0;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.proteomecommons.io.GenericPeakListWriter;

import org.proteomecommons.io.Peak;
import org.proteomecommons.io.PeakList;
import org.proteomecommons.io.mzxml.Base64;
/**
 *<p>An implementation of a PeakListWriter class that saves data in the MZXML 2.0 schema format.</p>
 * @author Jayson Falkner - jfalkner@umich.edu
 * @author Jarret
 *
 */
public class MzXMLPeakListWriter extends GenericPeakListWriter {
    private XMLStreamWriter xmlw;
    
    //state to know if the metadata is written for this document.
    private boolean hasWrittenMetaData = false;
    private boolean closed = false;
    
    //state for the number of the scan in the scan attribute
    private int scanNumber = 1;
    
    private MsRun msRun = null;
    
    private boolean use32BitPrecision = true;
    
    // keep track of the writers
    FileWriter fw;
    BufferedWriter bw;
    
    public MzXMLPeakListWriter(String filename){
        super(filename);
        
        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        
        // Create an XML stream writer
        try {
            // make the file
            File file = new File(filename);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            
            // make the XML writer using the BufferedWriter
            xmlw = xmlof.createXMLStreamWriter(bw);
            
            xmlw.writeStartDocument();
            
            //root element and required schema attributes
            xmlw.writeStartElement("mzXML");
            xmlw.writeAttribute("xmlns","http://sashimi.sourceforge.net/schema_revision/mzXML_2.0");
            xmlw.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            xmlw.writeAttribute("xsi:schemaLocation", "http://sashimi.sourceforge.net/schema_revision/mzXML_2.0 http://sashimi.sourceforge.net/schema_revision/mzXML_2.0/mzXML_idx_2.0.xsd");
            
            // write out the related data
            xmlw.writeStartElement("msRun");
            // skip scanCount, startTime, and endTime
        } catch (Exception e) {
            throw new RuntimeException("Can't write XML for "+filename, e);
        }
    }
    
       /* (non-Javadoc)
         * @see org.proteomecommons.io.PeakListWriter#startPeakList()
         */
    public void write(PeakList pl) {
        try {
            // TODO Auto-generated method stub
            if(!hasWrittenMetaData){
                writeMetaData(pl);
            }
            
            // write peak list
            Peak[] peaks = pl.getPeaks();
            // assume 32 bit for compatibility
            int byteBufferCapacity = (32/8)*2*peaks.length;
            // if 64 bit, recalc precision
            if (!isUse32BitPrecision()){
                //bytes needed = (bits per double/bits per byte)*2 double per peak*number of peaks
                byteBufferCapacity = (64/8)*2*peaks.length;
            }
            
            // make the buffer
            ByteBuffer bb = ByteBuffer.allocate(byteBufferCapacity);
            
            //network byte order is big endian http://mindprod.com/jgloss/endian.html
            bb = bb.order(ByteOrder.BIG_ENDIAN);
            
            //flatten the peaks into the buffer
            for(int i=0;i<peaks.length;i++){
                //dump the peak bytes into the buffer
                Peak current = (Peak)peaks[i];
                // assume 32 bit precision
                if (isUse32BitPrecision()){
                    bb.putFloat((float)current.getMassOverCharge());
                    bb.putFloat((float)current.getIntensity());
                } else {
                    bb.putDouble(current.getMassOverCharge());
                    bb.putDouble(current.getIntensity());
                }
            }
            
            //convert bytes
            String encodedBytes = Base64.encodeBytes(bb.array(), false);
            
            //finally, write out the xml
            try {
                //setup scan element
                xmlw.writeStartElement("scan");
                
                // write out a unique scan number to identify it
                xmlw.writeAttribute("num", "" + scanNumber);
                scanNumber++;
                // write out the ms level
                if (pl.getTandemCount() != PeakList.UNKNOWN_TANDEM_COUNT) {
                    xmlw.writeAttribute("msLevel", "" + pl.getTandemCount());
                }
                // number of peaks
                xmlw.writeAttribute("peaksCount", "" + peaks.length);
                
                // if it is an MZXMLPeakList write the other meta-data
                if (pl instanceof MzXMLPeakList) {
                    MzXMLPeakList mzxmlpl = (MzXMLPeakList)pl;
                    writeNonNull(xmlw, "polarity", mzxmlpl.getPolarity());
                    writeNonNull(xmlw, "scanType", mzxmlpl.getScanType());
                    writeNonNull(xmlw, "centroided", mzxmlpl.getCentroided());
                    writeNonNull(xmlw, "deisotoped", mzxmlpl.getDeisotoped());
                    writeNonNull(xmlw, "chargeDeconvoluted", mzxmlpl.getChargeDeconvoluted());
                    writeNonNull(xmlw, "retentionTime", mzxmlpl.getRetentionTime());
                    writeNonNull(xmlw, "ionisationEnergy", mzxmlpl.getIonisationEnergy());
                    writeNonNull(xmlw, "collisionEnergy", mzxmlpl.getCollisionEnergy());
                    writeNonNull(xmlw, "startMz", mzxmlpl.getStartMz());
                    writeNonNull(xmlw, "endMz", mzxmlpl.getEndMz());
                    writeNonNull(xmlw, "lowMz", mzxmlpl.getLowMz());
                    writeNonNull(xmlw, "highMz", mzxmlpl.getHighMz());
                    writeNonNull(xmlw, "basePeakMz", mzxmlpl.getBasePeakMz());
                    writeNonNull(xmlw, "basePeakIntensity", mzxmlpl.getBasePeakIntensity());
                    writeNonNull(xmlw, "totIonCurrent", mzxmlpl.getTotIonCurrent());
                }
                
                // write precursor mz info
                if (pl.getParentPeak() != null) {
                    Peak parentPeak = pl.getParentPeak();
                    if (parentPeak.getIntensity() == Peak.UNKNOWN_INTENSITY) {
                        throw new RuntimeException("mzXML requires an intensity value for precursor peaks.");
                    }
                    if (parentPeak.getMassOverCharge() == Peak.UNKNOWN_MZ) {
                        throw new RuntimeException("mzXML requires an mz value for precursor peaks.");
                    }
                    // close the precursorMz element
                    xmlw.writeStartElement("precursorMz");
                    
                    writeNonNull(xmlw, "precursorIntensity", Float.toString((float)parentPeak.getIntensity()));
                    if (parentPeak.getCharge() != Peak.UNKNOWN_CHARGE) {
                        xmlw.writeAttribute("precursorCharge", Integer.toString(parentPeak.getCharge()));
                    }
                    xmlw.writeCharacters(Float.toString((float)parentPeak.getMassOverCharge()));
                    // close the precursorMz element
                    xmlw.writeEndElement();
                }
                
                //setup peaks element
                xmlw.writeStartElement("peaks");
                
                if (isUse32BitPrecision()){
                    xmlw.writeAttribute("precision", "32");
                } else {
                    xmlw.writeAttribute("precision", "64");
                }
                // these two values are always the same
                xmlw.writeAttribute("byteOrder", "network");
                xmlw.writeAttribute("pairOrder", "m/z-int");
                xmlw.writeCharacters(encodedBytes);
                
                // end everything
                xmlw.writeEndElement(); //peaks
                xmlw.writeEndElement(); //scan
            } catch (XMLStreamException e) {
                throw new RuntimeException("Can't write peak list!", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't write peak list!", e);
        }
    }
    
    /**
     * Helper to handle metadata in the document
     * @throws CheckedIOException
     */
    private void writeMetaData(PeakList peaklist) {
        // skip if previously done
        if(hasWrittenMetaData){
            return;
        }
        // flag meta-data as writter
        hasWrittenMetaData = true;
        
        // try to get meta-data if it is available
        if (peaklist instanceof MzXMLPeakList) {
            MzXMLPeakList mzxml = (MzXMLPeakList)peaklist;
            msRun = mzxml.getMsRun();
        } else {
            msRun = new MsRun();
        }
        
        try {
            // write out all the parent files
            for (ParentFile pf : msRun.getParentFiles()){
                xmlw.writeEmptyElement("parentFile");
                writeNonNull(xmlw, "fileName", pf.getFileName());
                writeNonNull(xmlw, "fileType", pf.getFileType());
                writeNonNull(xmlw, "fileSha1", pf.getFileSHA());
            }
            
            // write out all the msInstrument information
            MsInstrument msInstrument = msRun.getMsInstrument();
            if (msInstrument != null) {
                xmlw.writeStartElement("msInstrument");
                
                // write out msManufacture
                if (msInstrument.getMsManufacturer()!=null) {
                    xmlw.writeEmptyElement("msManufacturer");
                    writeNonNull(xmlw, "category", msInstrument.getMsManufacturer().getCategory());
                    if (msInstrument.getMsManufacturer().getValue() != null)
                        writeNonNull(xmlw, "value", msInstrument.getMsManufacturer().getValue());
                }
                
                // write out msModel
                if (msInstrument.getMsModel()!=null) {
                    xmlw.writeEmptyElement("msModel");
                    writeNonNull(xmlw, "category", msInstrument.getMsModel().getCategory());
                    writeNonNull(xmlw, "value", msInstrument.getMsModel().getValue());
                }
                
                // write out msManufacture
                if (msInstrument.getMsIonisation()!=null) {
                    xmlw.writeEmptyElement("msIonisation");
                    writeNonNull(xmlw, "category", msInstrument.getMsIonisation().getCategory());
                    writeNonNull(xmlw, "value", msInstrument.getMsIonisation().getValue());
                }
                
                // write out msManufacture
                if (msInstrument.getMsMassAnalyzer()!=null) {
                    xmlw.writeEmptyElement("msMassAnalyzer");
                    writeNonNull(xmlw, "category", msInstrument.getMsMassAnalyzer().getCategory());
                    writeNonNull(xmlw, "value", msInstrument.getMsMassAnalyzer().getValue());
                }
                
                // write out msManufacture
                if (msInstrument.getMsDetector()!=null) {
                    xmlw.writeEmptyElement("msDetector");
                    writeNonNull(xmlw, "category", msInstrument.getMsDetector().getCategory());
                    writeNonNull(xmlw, "value", msInstrument.getMsDetector().getValue());
                }
                
                // write out software
                if (msInstrument.getSoftware()!=null) {
                    xmlw.writeEmptyElement("software");
                    writeNonNull(xmlw, "type", msInstrument.getSoftware().getType());
                    writeNonNull(xmlw, "name", msInstrument.getSoftware().getName());
                    writeNonNull(xmlw, "version", msInstrument.getSoftware().getVersion());
                    writeNonNull(xmlw, "completionTime", msInstrument.getSoftware().getCompletionTime());
                }
                
                // write out msResolution
                if (msInstrument.getMsResolution()!=null) {
                    xmlw.writeEmptyElement("msResolution");
                    writeNonNull(xmlw, "category", msInstrument.getMsResolution().getCategory());
                    writeNonNull(xmlw, "value", msInstrument.getMsResolution().getValue());
                }
                
                // write out operator
                if (msInstrument.getOperator()!=null) {
                    xmlw.writeEmptyElement("operator");
                    writeNonNull(xmlw, "first", msInstrument.getOperator().getFirst());
                    writeNonNull(xmlw, "last", msInstrument.getOperator().getLast());
                    writeNonNull(xmlw, "email", msInstrument.getOperator().getEmail());
                    writeNonNull(xmlw, "phone", msInstrument.getOperator().getPhone());
                    writeNonNull(xmlw, "URI", msInstrument.getOperator().getURI());
                }
                
                xmlw.writeEndElement();
            }
            
            
            // write out all the dataProcessing information
            for (DataProcessing dp : msRun.getDataProcessings()){
                xmlw.writeStartElement("dataProcessing");
                writeNonNull(xmlw, "intensityCutoff", dp.getIntensityCutoff());
                writeNonNull(xmlw, "centroided", dp.getCentroided());
                writeNonNull(xmlw, "deisotoped", dp.getDeisotoped());
                writeNonNull(xmlw, "chargeDeconvoluted", dp.getChargeDeconvoluted());
                writeNonNull(xmlw, "spotIntegration", dp.getSpotIntegration());
                
                // get all software
                Software software = dp.getSoftware();
                // write out software
                if (software != null) {
                    xmlw.writeEmptyElement("software");
                    writeNonNull(xmlw, "type", software.getType());
                    writeNonNull(xmlw, "name", software.getName());
                    writeNonNull(xmlw, "version", software.getVersion());
                    writeNonNull(xmlw, "completionTime", software.getCompletionTime());
                }
                
                xmlw.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Can't write mzXML meta-data!", e);
        }
    }
    
    private void writeNonNull(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        if (name != null && value != null)
            xmlw.writeAttribute(name, value);
    }
    
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        
        //finish off anything
        try {
            xmlw.writeEndElement(); //msRun element
            
            //declare the index does not exist
            xmlw.writeEmptyElement("indexOffset");
            xmlw.writeAttribute("xsi:nil", "1");
            
            xmlw.writeEndElement(); //mzXML element
            
            //close out the xml writer
            xmlw.flush();
            xmlw.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Can't write all of XML!",e);
        } finally {
            super.close();
        }
    }
    
    public MsRun getMsRun() {
        return msRun;
    }
    
    public void setMsRun(MsRun msRun) {
        this.msRun = msRun;
    }
    
    public boolean isUse32BitPrecision() {
        return use32BitPrecision;
    }
    
    public void setUse32BitPrecision(boolean use32BitPrecision) {
        this.use32BitPrecision = use32BitPrecision;
    }
}

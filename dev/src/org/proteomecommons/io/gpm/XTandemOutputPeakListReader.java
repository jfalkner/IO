/*
 * QuartzPeakListReader.java
 *
 * Created on August 3, 2006, 5:20 PM
 *
 *
 */

package org.proteomecommons.io.gpm;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.proteomecommons.io.GenericPeak;
import org.proteomecommons.io.GenericPeakListReader;
import org.proteomecommons.io.PeakList;

/**
 * Functionality for reading the quartz datasets including amethyst,
 * opal, and jasper from the GPM.  This might apply to generic GAML sets.
 * @author Jarret - jar@cs.washington.edu
 */
public class XTandemOutputPeakListReader extends GenericPeakListReader{
    //parsing sources
    private XMLStreamReader parser;
    
    //the current parser event
    private int event = XMLStreamConstants.SPACE;
    
    //current peaklist
    private boolean isAtStart = true;
    private LinkedList<String> masses = new LinkedList<String>();
    private LinkedList<String> intens = new LinkedList<String>();
    private int charge = 1;
    
    public XTandemOutputPeakListReader(String filename) {
        super(filename);
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            //factory.
            parser = factory.createXMLStreamReader(in);
            //parser.
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try { in.close(); } catch (Exception e){}
        }
    }
    
    public void close() {
        try {
            parser.close();
        } catch (XMLStreamException ex) {
            // ignore
        }
    }
    
    /**
     * Reads through the file, looking for gaml:xdata and gaml:ydata
     * to create a pl
     */
    public PeakList getPeakList() {
        boolean inXData = false;
        boolean inYData = false;
        HashSet<String> peptides = new HashSet<String>();
        
        try {
            for(; event != XMLStreamConstants.END_DOCUMENT; event = parser.next()){
                if(event == XMLStreamConstants.START_ELEMENT){
                    String localName = parser.getLocalName();
                    //QName qn = parser.getName();
                    //process start tags
                    if(parser.getLocalName().equals("Xdata") && 
                            parser.getAttributeValue(null, "label") != null &&
                            parser.getAttributeValue(null, "label").endsWith("spectrum")){
                        inXData = true;
                    } else if (parser.getLocalName().equals("Ydata")&& 
                            parser.getAttributeValue(null, "label") != null &&
                            parser.getAttributeValue(null, "label").endsWith("spectrum")){
                        inYData= true;
                    } else if (parser.getLocalName().equals("attribute") &&
                            parser.getAttributeValue(null,"type") != null &&
                            parser.getAttributeValue(null,"type").equals("charge")){
                        charge = Integer.parseInt(parser.getElementText());
                    } else if (parser.getLocalName().equals("values")){
                        if(inXData){
                            masses.clear();
                            String[] mzs= parser.getElementText().trim().split("\\s");
                            for(String mz : mzs)
                                masses.add(mz);
                        } else if(inYData){
                            intens.clear();
                            String[] intensities = parser.getElementText().trim().split("\\s");
                            for(String in : intensities)
                                intens.add(in);
                        }
                    } else if (parser.getLocalName().equals("domain") &&
                            parser.getAttributeValue(null, "seq") != null &&
                            parser.getAttributeValue(null, "seq").trim().length() > 0){
                        peptides.add(parser.getAttributeValue(null, "seq").trim());
                    }
                    
                } else if (event == XMLStreamConstants.END_ELEMENT){
                    //process end tags
                    if(parser.getLocalName().equals("Xdata")){
                        inXData = false;
                    } else if (parser.getLocalName().equals("Ydata")){
                        inYData= false;
                    }
                }
                
                //see if we've read enough
                if(masses.size() > 0 && intens.size() > 0){
                    LinkedList<GenericPeak> peaks = new LinkedList<GenericPeak>();
                    while(masses.size() > 0 && intens.size() > 0){
                        GenericPeak gp = new GenericPeak();
                        gp.setIntensity(Double.parseDouble(intens.removeFirst()));
                        gp.setMassOverCharge(Double.parseDouble(masses.removeFirst()));
                        gp.setCharge(charge);
                        peaks.add(gp);
                    }
                    //System.out.println("peps were " + peptides.toString());
                    return new XTandemOutputPeakList(peaks.toArray(new GenericPeak[0]), new LinkedList<String>(peptides));
                }
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}

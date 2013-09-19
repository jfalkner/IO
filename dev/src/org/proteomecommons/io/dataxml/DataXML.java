//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.11.14 at 04:04:07 AM EST 
//


package org.proteomecommons.io.dataxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dataXML complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dataXML">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admin" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}Admin" minOccurs="0"/>
 *         &lt;element name="cvList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}CVList" minOccurs="0"/>
 *         &lt;element name="paramGroupList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}ParamGroupList" minOccurs="0"/>
 *         &lt;element name="sampleList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}SampleList" minOccurs="0"/>
 *         &lt;element name="instrumentList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}InstrumentList" minOccurs="0"/>
 *         &lt;element name="softwareList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}SoftwareList" minOccurs="0"/>
 *         &lt;element name="dataProcessingList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}DataProcessingList" minOccurs="0"/>
 *         &lt;element name="dataGroupList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}DataGroupList" minOccurs="0"/>
 *         &lt;element name="spectrumList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}SpectrumList" minOccurs="0"/>
 *         &lt;element name="experimentList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}ExperimentList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="accession" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataXML", propOrder = {
    "admin",
    "cvList",
    "paramGroupList",
    "sampleList",
    "instrumentList",
    "softwareList",
    "dataProcessingList",
    "dataGroupList",
    "spectrumList",
    "experimentList"
})
public class DataXML {

    protected Admin admin;
    protected CVList cvList;
    protected ParamGroupList paramGroupList;
    protected SampleList sampleList;
    protected InstrumentList instrumentList;
    protected SoftwareList softwareList;
    protected DataProcessingList dataProcessingList;
    protected DataGroupList dataGroupList;
    protected SpectrumList spectrumList;
    protected ExperimentList experimentList;
    @XmlAttribute
    protected String accession;
    @XmlAttribute
    protected String version;

    /**
     * Gets the value of the admin property.
     * 
     * @return
     *     possible object is
     *     {@link Admin }
     *     
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * Sets the value of the admin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Admin }
     *     
     */
    public void setAdmin(Admin value) {
        this.admin = value;
    }

    /**
     * Gets the value of the cvList property.
     * 
     * @return
     *     possible object is
     *     {@link CVList }
     *     
     */
    public CVList getCvList() {
        return cvList;
    }

    /**
     * Sets the value of the cvList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CVList }
     *     
     */
    public void setCvList(CVList value) {
        this.cvList = value;
    }

    /**
     * Gets the value of the paramGroupList property.
     * 
     * @return
     *     possible object is
     *     {@link ParamGroupList }
     *     
     */
    public ParamGroupList getParamGroupList() {
        return paramGroupList;
    }

    /**
     * Sets the value of the paramGroupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParamGroupList }
     *     
     */
    public void setParamGroupList(ParamGroupList value) {
        this.paramGroupList = value;
    }

    /**
     * Gets the value of the sampleList property.
     * 
     * @return
     *     possible object is
     *     {@link SampleList }
     *     
     */
    public SampleList getSampleList() {
        return sampleList;
    }

    /**
     * Sets the value of the sampleList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleList }
     *     
     */
    public void setSampleList(SampleList value) {
        this.sampleList = value;
    }

    /**
     * Gets the value of the instrumentList property.
     * 
     * @return
     *     possible object is
     *     {@link InstrumentList }
     *     
     */
    public InstrumentList getInstrumentList() {
        return instrumentList;
    }

    /**
     * Sets the value of the instrumentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstrumentList }
     *     
     */
    public void setInstrumentList(InstrumentList value) {
        this.instrumentList = value;
    }

    /**
     * Gets the value of the softwareList property.
     * 
     * @return
     *     possible object is
     *     {@link SoftwareList }
     *     
     */
    public SoftwareList getSoftwareList() {
        return softwareList;
    }

    /**
     * Sets the value of the softwareList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftwareList }
     *     
     */
    public void setSoftwareList(SoftwareList value) {
        this.softwareList = value;
    }

    /**
     * Gets the value of the dataProcessingList property.
     * 
     * @return
     *     possible object is
     *     {@link DataProcessingList }
     *     
     */
    public DataProcessingList getDataProcessingList() {
        return dataProcessingList;
    }

    /**
     * Sets the value of the dataProcessingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataProcessingList }
     *     
     */
    public void setDataProcessingList(DataProcessingList value) {
        this.dataProcessingList = value;
    }

    /**
     * Gets the value of the dataGroupList property.
     * 
     * @return
     *     possible object is
     *     {@link DataGroupList }
     *     
     */
    public DataGroupList getDataGroupList() {
        return dataGroupList;
    }

    /**
     * Sets the value of the dataGroupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataGroupList }
     *     
     */
    public void setDataGroupList(DataGroupList value) {
        this.dataGroupList = value;
    }

    /**
     * Gets the value of the spectrumList property.
     * 
     * @return
     *     possible object is
     *     {@link SpectrumList }
     *     
     */
    public SpectrumList getSpectrumList() {
        return spectrumList;
    }

    /**
     * Sets the value of the spectrumList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpectrumList }
     *     
     */
    public void setSpectrumList(SpectrumList value) {
        this.spectrumList = value;
    }

    /**
     * Gets the value of the experimentList property.
     * 
     * @return
     *     possible object is
     *     {@link ExperimentList }
     *     
     */
    public ExperimentList getExperimentList() {
        return experimentList;
    }

    /**
     * Sets the value of the experimentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExperimentList }
     *     
     */
    public void setExperimentList(ExperimentList value) {
        this.experimentList = value;
    }

    /**
     * Gets the value of the accession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccession(String value) {
        this.accession = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.11.14 at 04:04:07 AM EST 
//


package org.proteomecommons.io.dataxml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Admin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Admin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceFileList" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}SourceFileList"/>
 *         &lt;element name="contact" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}Contact" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Admin", propOrder = {
    "sourceFileList",
    "contact"
})
public class Admin {

    @XmlElement(required = true)
    protected SourceFileList sourceFileList;
    @XmlElement(required = true)
    protected List<Contact> contact;

    /**
     * Gets the value of the sourceFileList property.
     * 
     * @return
     *     possible object is
     *     {@link SourceFileList }
     *     
     */
    public SourceFileList getSourceFileList() {
        return sourceFileList;
    }

    /**
     * Sets the value of the sourceFileList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceFileList }
     *     
     */
    public void setSourceFileList(SourceFileList value) {
        this.sourceFileList = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Contact }
     * 
     * 
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

}

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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParamGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParamGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cvParam" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}CVParam" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="userParam" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}UserParam" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="paramGroupRef" type="{http://sashimi.sourceforge.net/schema_revision/DataXML_0.1}ParamGroupRef" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamGroup", propOrder = {
    "cvParam",
    "userParam",
    "paramGroupRef"
})
public class ParamGroup {

    @XmlElement(required = true)
    protected List<CVParam> cvParam;
    @XmlElement(required = true)
    protected List<UserParam> userParam;
    @XmlElement(required = true)
    protected List<ParamGroupRef> paramGroupRef;
    @XmlAttribute
    protected String id;

    /**
     * Gets the value of the cvParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cvParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCvParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CVParam }
     * 
     * 
     */
    public List<CVParam> getCvParam() {
        if (cvParam == null) {
            cvParam = new ArrayList<CVParam>();
        }
        return this.cvParam;
    }

    /**
     * Gets the value of the userParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserParam }
     * 
     * 
     */
    public List<UserParam> getUserParam() {
        if (userParam == null) {
            userParam = new ArrayList<UserParam>();
        }
        return this.userParam;
    }

    /**
     * Gets the value of the paramGroupRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paramGroupRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParamGroupRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamGroupRef }
     * 
     * 
     */
    public List<ParamGroupRef> getParamGroupRef() {
        if (paramGroupRef == null) {
            paramGroupRef = new ArrayList<ParamGroupRef>();
        }
        return this.paramGroupRef;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}

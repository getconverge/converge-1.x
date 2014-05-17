//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:17 PM CEST 
//


package dk.i2m.converge.nar.newsml.v1_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;choice>
 *           &lt;element ref="{}Subject"/>
 *           &lt;element ref="{}SubjectMatter"/>
 *           &lt;element ref="{}SubjectDetail"/>
 *         &lt;/choice>
 *         &lt;element ref="{}SubjectQualifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}assignment"/>
 *       &lt;attGroup ref="{}localid"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "subjectOrSubjectMatterOrSubjectDetail"
})
@XmlRootElement(name = "SubjectCode")
public class SubjectCode {

    @XmlElements({
        @XmlElement(name = "SubjectQualifier", type = SubjectQualifier.class),
        @XmlElement(name = "Subject", type = Subject.class),
        @XmlElement(name = "SubjectDetail", type = SubjectDetail.class),
        @XmlElement(name = "SubjectMatter", type = SubjectMatter.class)
    })
    protected List<Object> subjectOrSubjectMatterOrSubjectDetail;
    @XmlAttribute(name = "AssignedBy")
    protected String assignedBy;
    @XmlAttribute(name = "Importance")
    protected String importance;
    @XmlAttribute(name = "Confidence")
    protected String confidence;
    @XmlAttribute(name = "HowPresent")
    protected String howPresent;
    @XmlAttribute(name = "DateAndTime")
    protected String dateAndTime;
    @XmlAttribute(name = "Duid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String duid;
    @XmlAttribute(name = "Euid")
    protected String euid;

    /**
     * Gets the value of the subjectOrSubjectMatterOrSubjectDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subjectOrSubjectMatterOrSubjectDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubjectOrSubjectMatterOrSubjectDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubjectQualifier }
     * {@link Subject }
     * {@link SubjectDetail }
     * {@link SubjectMatter }
     * 
     * 
     */
    public List<Object> getSubjectOrSubjectMatterOrSubjectDetail() {
        if (subjectOrSubjectMatterOrSubjectDetail == null) {
            subjectOrSubjectMatterOrSubjectDetail = new ArrayList<Object>();
        }
        return this.subjectOrSubjectMatterOrSubjectDetail;
    }

    /**
     * Gets the value of the assignedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignedBy() {
        return assignedBy;
    }

    /**
     * Sets the value of the assignedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignedBy(String value) {
        this.assignedBy = value;
    }

    /**
     * Gets the value of the importance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportance() {
        return importance;
    }

    /**
     * Sets the value of the importance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportance(String value) {
        this.importance = value;
    }

    /**
     * Gets the value of the confidence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Sets the value of the confidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfidence(String value) {
        this.confidence = value;
    }

    /**
     * Gets the value of the howPresent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHowPresent() {
        return howPresent;
    }

    /**
     * Sets the value of the howPresent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHowPresent(String value) {
        this.howPresent = value;
    }

    /**
     * Gets the value of the dateAndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateAndTime() {
        return dateAndTime;
    }

    /**
     * Sets the value of the dateAndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateAndTime(String value) {
        this.dateAndTime = value;
    }

    /**
     * Gets the value of the duid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuid() {
        return duid;
    }

    /**
     * Sets the value of the duid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuid(String value) {
        this.duid = value;
    }

    /**
     * Gets the value of the euid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEuid() {
        return euid;
    }

    /**
     * Sets the value of the euid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEuid(String value) {
        this.euid = value;
    }

}

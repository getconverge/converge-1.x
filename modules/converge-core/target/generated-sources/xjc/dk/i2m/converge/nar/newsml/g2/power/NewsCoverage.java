//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:16 PM CEST 
//


package dk.i2m.converge.nar.newsml.g2.power;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="planning">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="g2contentType" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}itemClass" minOccurs="0"/>
 *                   &lt;element name="itemCount" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
 *                           &lt;attribute name="rangefrom" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="rangeto" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="assignedTo" type="{http://iptc.org/std/nar/2006-10-01/}Flex1PartyPropType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="scheduled" type="{http://iptc.org/std/nar/2006-10-01/}ApproximateDateTimePropType" minOccurs="0"/>
 *                   &lt;element name="service" type="{http://iptc.org/std/nar/2006-10-01/}QualPropType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;group ref="{http://iptc.org/std/nar/2006-10-01/}DescriptiveMetadataGroup" minOccurs="0"/>
 *                   &lt;element name="edNote" type="{http://iptc.org/std/nar/2006-10-01/}BlockType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}delivery" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}persistentEditAttributes"/>
 *       &lt;anyAttribute namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "planning",
    "delivery",
    "any"
})
@XmlRootElement(name = "newsCoverage")
public class NewsCoverage {

    @XmlElement(required = true)
    protected NewsCoverage.Planning planning;
    protected Delivery delivery;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute
    protected String creator;
    @XmlAttribute
    protected String modified;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the planning property.
     * 
     * @return
     *     possible object is
     *     {@link NewsCoverage.Planning }
     *     
     */
    public NewsCoverage.Planning getPlanning() {
        return planning;
    }

    /**
     * Sets the value of the planning property.
     * 
     * @param value
     *     allowed object is
     *     {@link NewsCoverage.Planning }
     *     
     */
    public void setPlanning(NewsCoverage.Planning value) {
        this.planning = value;
    }

    /**
     * Gets the value of the delivery property.
     * 
     * @return
     *     possible object is
     *     {@link Delivery }
     *     
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * Sets the value of the delivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link Delivery }
     *     
     */
    public void setDelivery(Delivery value) {
        this.delivery = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
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

    /**
     * Gets the value of the creator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the value of the creator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModified(String value) {
        this.modified = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="g2contentType" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}itemClass" minOccurs="0"/>
     *         &lt;element name="itemCount" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
     *                 &lt;attribute name="rangefrom" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="rangeto" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="assignedTo" type="{http://iptc.org/std/nar/2006-10-01/}Flex1PartyPropType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="scheduled" type="{http://iptc.org/std/nar/2006-10-01/}ApproximateDateTimePropType" minOccurs="0"/>
     *         &lt;element name="service" type="{http://iptc.org/std/nar/2006-10-01/}QualPropType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;group ref="{http://iptc.org/std/nar/2006-10-01/}DescriptiveMetadataGroup" minOccurs="0"/>
     *         &lt;element name="edNote" type="{http://iptc.org/std/nar/2006-10-01/}BlockType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "g2ContentType",
        "itemClass",
        "itemCount",
        "assignedTo",
        "scheduled",
        "service",
        "languageOrGenreOrKeyword",
        "edNote"
    })
    public static class Planning {

        @XmlElement(name = "g2contentType")
        protected NewsCoverage.Planning.G2ContentType g2ContentType;
        protected QualPropType itemClass;
        protected NewsCoverage.Planning.ItemCount itemCount;
        protected List<Flex1PartyPropType> assignedTo;
        protected ApproximateDateTimePropType scheduled;
        protected List<QualPropType> service;
        @XmlElements({
            @XmlElement(name = "keyword", type = Keyword.class),
            @XmlElement(name = "subject", type = Subject.class),
            @XmlElement(name = "slugline", type = Slugline.class),
            @XmlElement(name = "language", type = Language.class),
            @XmlElement(name = "description", type = Description.class),
            @XmlElement(name = "creditline", type = Creditline.class),
            @XmlElement(name = "genre", type = Genre.class),
            @XmlElement(name = "by", type = By.class),
            @XmlElement(name = "dateline", type = Dateline.class),
            @XmlElement(name = "headline", type = Headline.class)
        })
        protected List<Object> languageOrGenreOrKeyword;
        protected List<BlockType> edNote;

        /**
         * Gets the value of the g2ContentType property.
         * 
         * @return
         *     possible object is
         *     {@link NewsCoverage.Planning.G2ContentType }
         *     
         */
        public NewsCoverage.Planning.G2ContentType getG2ContentType() {
            return g2ContentType;
        }

        /**
         * Sets the value of the g2ContentType property.
         * 
         * @param value
         *     allowed object is
         *     {@link NewsCoverage.Planning.G2ContentType }
         *     
         */
        public void setG2ContentType(NewsCoverage.Planning.G2ContentType value) {
            this.g2ContentType = value;
        }

        /**
         * The nature of the G2 item, set in accordance with the structure of its content.
         * 
         * @return
         *     possible object is
         *     {@link QualPropType }
         *     
         */
        public QualPropType getItemClass() {
            return itemClass;
        }

        /**
         * Sets the value of the itemClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link QualPropType }
         *     
         */
        public void setItemClass(QualPropType value) {
            this.itemClass = value;
        }

        /**
         * Gets the value of the itemCount property.
         * 
         * @return
         *     possible object is
         *     {@link NewsCoverage.Planning.ItemCount }
         *     
         */
        public NewsCoverage.Planning.ItemCount getItemCount() {
            return itemCount;
        }

        /**
         * Sets the value of the itemCount property.
         * 
         * @param value
         *     allowed object is
         *     {@link NewsCoverage.Planning.ItemCount }
         *     
         */
        public void setItemCount(NewsCoverage.Planning.ItemCount value) {
            this.itemCount = value;
        }

        /**
         * Gets the value of the assignedTo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the assignedTo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAssignedTo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Flex1PartyPropType }
         * 
         * 
         */
        public List<Flex1PartyPropType> getAssignedTo() {
            if (assignedTo == null) {
                assignedTo = new ArrayList<Flex1PartyPropType>();
            }
            return this.assignedTo;
        }

        /**
         * Gets the value of the scheduled property.
         * 
         * @return
         *     possible object is
         *     {@link ApproximateDateTimePropType }
         *     
         */
        public ApproximateDateTimePropType getScheduled() {
            return scheduled;
        }

        /**
         * Sets the value of the scheduled property.
         * 
         * @param value
         *     allowed object is
         *     {@link ApproximateDateTimePropType }
         *     
         */
        public void setScheduled(ApproximateDateTimePropType value) {
            this.scheduled = value;
        }

        /**
         * Gets the value of the service property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the service property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getService().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link QualPropType }
         * 
         * 
         */
        public List<QualPropType> getService() {
            if (service == null) {
                service = new ArrayList<QualPropType>();
            }
            return this.service;
        }

        /**
         * Gets the value of the languageOrGenreOrKeyword property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the languageOrGenreOrKeyword property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLanguageOrGenreOrKeyword().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Keyword }
         * {@link Subject }
         * {@link Slugline }
         * {@link Language }
         * {@link Description }
         * {@link Creditline }
         * {@link Genre }
         * {@link By }
         * {@link Dateline }
         * {@link Headline }
         * 
         * 
         */
        public List<Object> getLanguageOrGenreOrKeyword() {
            if (languageOrGenreOrKeyword == null) {
                languageOrGenreOrKeyword = new ArrayList<Object>();
            }
            return this.languageOrGenreOrKeyword;
        }

        /**
         * Gets the value of the edNote property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the edNote property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEdNote().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BlockType }
         * 
         * 
         */
        public List<BlockType> getEdNote() {
            if (edNote == null) {
                edNote = new ArrayList<BlockType>();
            }
            return this.edNote;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class G2ContentType {

            @XmlValue
            protected String value;
            @XmlAttribute
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute
            protected String creator;
            @XmlAttribute
            protected String modified;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
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

            /**
             * Gets the value of the creator property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCreator() {
                return creator;
            }

            /**
             * Sets the value of the creator property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCreator(String value) {
                this.creator = value;
            }

            /**
             * Gets the value of the modified property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getModified() {
                return modified;
            }

            /**
             * Sets the value of the modified property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setModified(String value) {
                this.modified = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}editAttributes"/>
         *       &lt;attribute name="rangefrom" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="rangeto" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ItemCount {

            @XmlAttribute(required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger rangefrom;
            @XmlAttribute(required = true)
            @XmlSchemaType(name = "positiveInteger")
            protected BigInteger rangeto;
            @XmlAttribute
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlID
            @XmlSchemaType(name = "ID")
            protected String id;
            @XmlAttribute
            protected String creator;
            @XmlAttribute
            protected String modified;

            /**
             * Gets the value of the rangefrom property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getRangefrom() {
                return rangefrom;
            }

            /**
             * Sets the value of the rangefrom property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setRangefrom(BigInteger value) {
                this.rangefrom = value;
            }

            /**
             * Gets the value of the rangeto property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getRangeto() {
                return rangeto;
            }

            /**
             * Sets the value of the rangeto property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setRangeto(BigInteger value) {
                this.rangeto = value;
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

            /**
             * Gets the value of the creator property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCreator() {
                return creator;
            }

            /**
             * Sets the value of the creator property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCreator(String value) {
                this.creator = value;
            }

            /**
             * Gets the value of the modified property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getModified() {
                return modified;
            }

            /**
             * Sets the value of the modified property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setModified(String value) {
                this.modified = value;
            }

        }

    }

}

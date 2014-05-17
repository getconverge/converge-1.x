//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:15 PM CEST 
//


package dk.i2m.converge.nar.newsml.g2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element name="created" type="{http://iptc.org/std/nar/2006-10-01/}TruncatedDateTimePropType" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}copyrightNotice"/>
 *           &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}creator"/>
 *         &lt;/choice>
 *         &lt;element name="ceasedToExist" type="{http://iptc.org/std/nar/2006-10-01/}TruncatedDateTimePropType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "created",
    "copyrightNoticeOrCreator",
    "ceasedToExist",
    "any"
})
@XmlRootElement(name = "objectDetails")
public class ObjectDetails {

    protected TruncatedDateTimePropType created;
    @XmlElements({
        @XmlElement(name = "creator", type = FlexPropType.class),
        @XmlElement(name = "copyrightNotice", type = BlockType.class)
    })
    protected List<Object> copyrightNoticeOrCreator;
    protected TruncatedDateTimePropType ceasedToExist;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link TruncatedDateTimePropType }
     *     
     */
    public TruncatedDateTimePropType getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link TruncatedDateTimePropType }
     *     
     */
    public void setCreated(TruncatedDateTimePropType value) {
        this.created = value;
    }

    /**
     * Gets the value of the copyrightNoticeOrCreator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the copyrightNoticeOrCreator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCopyrightNoticeOrCreator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlexPropType }
     * {@link BlockType }
     * 
     * 
     */
    public List<Object> getCopyrightNoticeOrCreator() {
        if (copyrightNoticeOrCreator == null) {
            copyrightNoticeOrCreator = new ArrayList<Object>();
        }
        return this.copyrightNoticeOrCreator;
    }

    /**
     * Gets the value of the ceasedToExist property.
     * 
     * @return
     *     possible object is
     *     {@link TruncatedDateTimePropType }
     *     
     */
    public TruncatedDateTimePropType getCeasedToExist() {
        return ceasedToExist;
    }

    /**
     * Sets the value of the ceasedToExist property.
     * 
     * @param value
     *     allowed object is
     *     {@link TruncatedDateTimePropType }
     *     
     */
    public void setCeasedToExist(TruncatedDateTimePropType value) {
        this.ceasedToExist = value;
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

}

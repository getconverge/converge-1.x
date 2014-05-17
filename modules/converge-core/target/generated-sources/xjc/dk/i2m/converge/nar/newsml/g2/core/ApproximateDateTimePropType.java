//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:15 PM CEST 
//


package dk.i2m.converge.nar.newsml.g2.core;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * The type of a calendar date with an optional time part and with an optional approximation range for the date.
 * 
 * <p>Java class for ApproximateDateTimePropType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApproximateDateTimePropType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://iptc.org/std/nar/2006-10-01/>UnionDateTimeType">
 *       &lt;attribute name="approxstart" type="{http://iptc.org/std/nar/2006-10-01/}TruncatedDateTimeType" />
 *       &lt;attribute name="approxend" type="{http://iptc.org/std/nar/2006-10-01/}TruncatedDateTimeType" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproximateDateTimePropType", propOrder = {
    "value"
})
public class ApproximateDateTimePropType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected String approxstart;
    @XmlAttribute
    protected String approxend;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * The base type for approximate dates.
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
     * Gets the value of the approxstart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApproxstart() {
        return approxstart;
    }

    /**
     * Sets the value of the approxstart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApproxstart(String value) {
        this.approxstart = value;
    }

    /**
     * Gets the value of the approxend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApproxend() {
        return approxend;
    }

    /**
     * Sets the value of the approxend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApproxend(String value) {
        this.approxend = value;
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:16 PM CEST 
//


package dk.i2m.converge.nar.newsml.g2.power;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * The type for identifying a related concept
 * 
 * <p>Java class for FlexRelatedConceptType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlexRelatedConceptType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://iptc.org/std/nar/2006-10-01/}RelatedConceptType">
 *       &lt;sequence>
 *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}bag" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://iptc.org/std/nar/2006-10-01/}arbitraryValueAttributes"/>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlexRelatedConceptType", propOrder = {
    "bag"
})
public class FlexRelatedConceptType
    extends RelatedConceptType
{

    protected Bag bag;
    @XmlAttribute
    protected String value;
    @XmlAttribute
    protected QName valuedatatype;
    @XmlAttribute
    protected String valueunit;

    /**
     * Gets the value of the bag property.
     * 
     * @return
     *     possible object is
     *     {@link Bag }
     *     
     */
    public Bag getBag() {
        return bag;
    }

    /**
     * Sets the value of the bag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bag }
     *     
     */
    public void setBag(Bag value) {
        this.bag = value;
    }

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
     * Gets the value of the valuedatatype property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getValuedatatype() {
        return valuedatatype;
    }

    /**
     * Sets the value of the valuedatatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setValuedatatype(QName value) {
        this.valuedatatype = value;
    }

    /**
     * Gets the value of the valueunit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueunit() {
        return valueunit;
    }

    /**
     * Sets the value of the valueunit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueunit(String value) {
        this.valueunit = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:16 PM CEST 
//


package dk.i2m.converge.nar.nitf.v3_4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlMixed;
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
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}chron"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}classifier"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}copyrite"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}event"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}function"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}location"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}money"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}num"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}object.title"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}org"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}person"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}postaddr"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}virtloc"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}a"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}br"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}em"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}lang"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}pronounce"/>
 *         &lt;element ref="{http://iptc.org/std/NITF/2006-10-18/}q"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://iptc.org/std/NITF/2006-10-18/}commonNITFAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "hl2")
public class Hl2 {

    @XmlElementRefs({
        @XmlElementRef(name = "a", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = A.class),
        @XmlElementRef(name = "money", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Money.class),
        @XmlElementRef(name = "chron", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Chron.class),
        @XmlElementRef(name = "em", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Em.class),
        @XmlElementRef(name = "object.title", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = ObjectTitle.class),
        @XmlElementRef(name = "function", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Function.class),
        @XmlElementRef(name = "virtloc", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Virtloc.class),
        @XmlElementRef(name = "num", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Num.class),
        @XmlElementRef(name = "q", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Q.class),
        @XmlElementRef(name = "lang", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Lang.class),
        @XmlElementRef(name = "classifier", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Classifier.class),
        @XmlElementRef(name = "pronounce", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Pronounce.class),
        @XmlElementRef(name = "br", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Br.class),
        @XmlElementRef(name = "location", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Location.class),
        @XmlElementRef(name = "person", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Person.class),
        @XmlElementRef(name = "postaddr", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Postaddr.class),
        @XmlElementRef(name = "copyrite", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Copyrite.class),
        @XmlElementRef(name = "event", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Event.class),
        @XmlElementRef(name = "org", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Org.class)
    })
    @XmlMixed
    protected List<Object> content;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute
    protected String style;
    @XmlAttribute(name = "xml_lang")
    @XmlSchemaType(name = "anySimpleType")
    protected String xmlLang;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Money }
     * {@link A }
     * {@link Em }
     * {@link Chron }
     * {@link ObjectTitle }
     * {@link Function }
     * {@link Virtloc }
     * {@link Num }
     * {@link Q }
     * {@link Lang }
     * {@link Pronounce }
     * {@link Classifier }
     * {@link Br }
     * {@link Location }
     * {@link Person }
     * {@link String }
     * {@link Postaddr }
     * {@link Event }
     * {@link Copyrite }
     * {@link Org }
     * 
     * 
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
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
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the xmlLang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlLang() {
        return xmlLang;
    }

    /**
     * Sets the value of the xmlLang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

}

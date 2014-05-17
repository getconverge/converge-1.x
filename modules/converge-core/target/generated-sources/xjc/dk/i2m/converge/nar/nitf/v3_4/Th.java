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
import javax.xml.bind.annotation.XmlAnyElement;
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
 *         &lt;group ref="{http://iptc.org/std/NITF/2006-10-18/}enrichedText"/>
 *         &lt;group ref="{http://iptc.org/std/NITF/2006-10-18/}blockContent"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://iptc.org/std/NITF/2006-10-18/}commonNITFAttributes"/>
 *       &lt;attGroup ref="{http://iptc.org/std/NITF/2006-10-18/}cellAlign"/>
 *       &lt;attGroup ref="{http://iptc.org/std/NITF/2006-10-18/}cellVAlign"/>
 *       &lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="axes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nowrap">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="nowrap"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="rowspan" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="colspan" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
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
@XmlRootElement(name = "th")
public class Th {

    @XmlElementRefs({
        @XmlElementRef(name = "money", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Money.class),
        @XmlElementRef(name = "chron", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Chron.class),
        @XmlElementRef(name = "em", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Em.class),
        @XmlElementRef(name = "function", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Function.class),
        @XmlElementRef(name = "virtloc", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Virtloc.class),
        @XmlElementRef(name = "num", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Num.class),
        @XmlElementRef(name = "nitf-table", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = NitfTable.class),
        @XmlElementRef(name = "p", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = P.class),
        @XmlElementRef(name = "ul", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Ul.class),
        @XmlElementRef(name = "fn", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Fn.class),
        @XmlElementRef(name = "pre", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Pre.class),
        @XmlElementRef(name = "lang", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Lang.class),
        @XmlElementRef(name = "br", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Br.class),
        @XmlElementRef(name = "location", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Location.class),
        @XmlElementRef(name = "person", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Person.class),
        @XmlElementRef(name = "postaddr", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Postaddr.class),
        @XmlElementRef(name = "dl", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Dl.class),
        @XmlElementRef(name = "a", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = A.class),
        @XmlElementRef(name = "object.title", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = ObjectTitle.class),
        @XmlElementRef(name = "media", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Media.class),
        @XmlElementRef(name = "ol", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Ol.class),
        @XmlElementRef(name = "q", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Q.class),
        @XmlElementRef(name = "bq", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Bq.class),
        @XmlElementRef(name = "hl2", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Hl2 .class),
        @XmlElementRef(name = "note", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Note.class),
        @XmlElementRef(name = "pronounce", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Pronounce.class),
        @XmlElementRef(name = "classifier", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Classifier.class),
        @XmlElementRef(name = "table", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Table.class),
        @XmlElementRef(name = "hr", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Hr.class),
        @XmlElementRef(name = "event", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Event.class),
        @XmlElementRef(name = "copyrite", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Copyrite.class),
        @XmlElementRef(name = "org", namespace = "http://iptc.org/std/NITF/2006-10-18/", type = Org.class)
    })
    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;
    @XmlAttribute
    protected String axis;
    @XmlAttribute
    protected String axes;
    @XmlAttribute
    protected String nowrap;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String rowspan;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String colspan;
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
    @XmlAttribute
    protected String align;
    @XmlAttribute(name = "char")
    protected String _char;
    @XmlAttribute
    protected String charoff;
    @XmlAttribute
    protected String valign;

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
     * {@link Chron }
     * {@link Em }
     * {@link Function }
     * {@link Virtloc }
     * {@link Num }
     * {@link NitfTable }
     * {@link P }
     * {@link Ul }
     * {@link Fn }
     * {@link Pre }
     * {@link Lang }
     * {@link Br }
     * {@link Location }
     * {@link Person }
     * {@link Postaddr }
     * {@link Dl }
     * {@link A }
     * {@link ObjectTitle }
     * {@link Media }
     * {@link Ol }
     * {@link Q }
     * {@link Bq }
     * {@link Hl2 }
     * {@link Object }
     * {@link Classifier }
     * {@link Pronounce }
     * {@link Note }
     * {@link Table }
     * {@link String }
     * {@link Hr }
     * {@link Copyrite }
     * {@link Event }
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
     * Gets the value of the axis property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAxis() {
        return axis;
    }

    /**
     * Sets the value of the axis property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAxis(String value) {
        this.axis = value;
    }

    /**
     * Gets the value of the axes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAxes() {
        return axes;
    }

    /**
     * Sets the value of the axes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAxes(String value) {
        this.axes = value;
    }

    /**
     * Gets the value of the nowrap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNowrap() {
        return nowrap;
    }

    /**
     * Sets the value of the nowrap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNowrap(String value) {
        this.nowrap = value;
    }

    /**
     * Gets the value of the rowspan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowspan() {
        return rowspan;
    }

    /**
     * Sets the value of the rowspan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowspan(String value) {
        this.rowspan = value;
    }

    /**
     * Gets the value of the colspan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColspan() {
        return colspan;
    }

    /**
     * Sets the value of the colspan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColspan(String value) {
        this.colspan = value;
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

    /**
     * Gets the value of the align property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlign() {
        return align;
    }

    /**
     * Sets the value of the align property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlign(String value) {
        this.align = value;
    }

    /**
     * Gets the value of the char property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChar() {
        return _char;
    }

    /**
     * Sets the value of the char property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChar(String value) {
        this._char = value;
    }

    /**
     * Gets the value of the charoff property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharoff() {
        return charoff;
    }

    /**
     * Sets the value of the charoff property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharoff(String value) {
        this.charoff = value;
    }

    /**
     * Gets the value of the valign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValign() {
        return valign;
    }

    /**
     * Sets the value of the valign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValign(String value) {
        this.valign = value;
    }

}

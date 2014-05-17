//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.17 at 08:23:16 PM CEST 
//


package dk.i2m.converge.nar.newsml.g2.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://iptc.org/std/nar/2006-10-01/}AnyItemType">
 *       &lt;sequence>
 *         &lt;element name="contentMeta" type="{http://iptc.org/std/nar/2006-10-01/}ContentMetadataAcDType" minOccurs="0"/>
 *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}partMeta" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}assert" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}inlineRef" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="conceptSet" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}concept" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contentMeta",
    "partMeta",
    "_assert",
    "inlineRef",
    "conceptSet"
})
@XmlRootElement(name = "knowledgeItem")
public class KnowledgeItem
    extends AnyItemType
{

    protected ContentMetadataAcDType contentMeta;
    protected List<PartMeta> partMeta;
    @XmlElement(name = "assert")
    protected List<AssertType> _assert;
    protected List<InlineRef> inlineRef;
    protected KnowledgeItem.ConceptSet conceptSet;

    /**
     * Gets the value of the contentMeta property.
     * 
     * @return
     *     possible object is
     *     {@link ContentMetadataAcDType }
     *     
     */
    public ContentMetadataAcDType getContentMeta() {
        return contentMeta;
    }

    /**
     * Sets the value of the contentMeta property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentMetadataAcDType }
     *     
     */
    public void setContentMeta(ContentMetadataAcDType value) {
        this.contentMeta = value;
    }

    /**
     * Gets the value of the partMeta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partMeta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartMeta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartMeta }
     * 
     * 
     */
    public List<PartMeta> getPartMeta() {
        if (partMeta == null) {
            partMeta = new ArrayList<PartMeta>();
        }
        return this.partMeta;
    }

    /**
     * Gets the value of the assert property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assert property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssert().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssertType }
     * 
     * 
     */
    public List<AssertType> getAssert() {
        if (_assert == null) {
            _assert = new ArrayList<AssertType>();
        }
        return this._assert;
    }

    /**
     * Gets the value of the inlineRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inlineRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInlineRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InlineRef }
     * 
     * 
     */
    public List<InlineRef> getInlineRef() {
        if (inlineRef == null) {
            inlineRef = new ArrayList<InlineRef>();
        }
        return this.inlineRef;
    }

    /**
     * Gets the value of the conceptSet property.
     * 
     * @return
     *     possible object is
     *     {@link KnowledgeItem.ConceptSet }
     *     
     */
    public KnowledgeItem.ConceptSet getConceptSet() {
        return conceptSet;
    }

    /**
     * Sets the value of the conceptSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link KnowledgeItem.ConceptSet }
     *     
     */
    public void setConceptSet(KnowledgeItem.ConceptSet value) {
        this.conceptSet = value;
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
     *         &lt;element ref="{http://iptc.org/std/nar/2006-10-01/}concept" maxOccurs="unbounded" minOccurs="0"/>
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
        "concept"
    })
    public static class ConceptSet {

        protected List<Concept> concept;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * A set of properties defining a concept Gets the value of the concept property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the concept property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getConcept().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Concept }
         * 
         * 
         */
        public List<Concept> getConcept() {
            if (concept == null) {
                concept = new ArrayList<Concept>();
            }
            return this.concept;
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

}

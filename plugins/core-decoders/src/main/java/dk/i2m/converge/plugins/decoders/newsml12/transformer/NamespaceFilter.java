/*
 *  Copyright (C) 2012 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.decoders.newsml12.transformer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * XML Filter for adding or removing name spaces. This is convenient when
 * processing XML with missing name space declarations.
 *
 * @author Allan Lykke Christensen
 */
public class NamespaceFilter extends XMLFilterImpl {

    /**
     * URI of the name space to add.
     */
    private String usedNamespaceUri;
    /**
     * Add or remove the name space.
     */
    private boolean addNamespace;
    /**
     * State variable determining whether the name space has been added. Used to
     * avoid adding the name space multiple times.
     */
    private boolean addedNamespace = false;

    /**
     * Create a new instance of {@link NamespaceFilter}.
     *
     * @param namespaceUri URI of the name space to add or remove
     * @param addNamespace Add ({@code true}) or remove ({@code false}) the name
     * space
     */
    public NamespaceFilter(String namespaceUri, boolean addNamespace) {
        super();

        if (addNamespace) {
            this.usedNamespaceUri = namespaceUri;
        } else {
            this.usedNamespaceUri = "";
        }
        this.addNamespace = addNamespace;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        if (addNamespace) {
            startControlledPrefixMapping();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(this.usedNamespaceUri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(this.usedNamespaceUri, localName, qName);
    }

    @Override
    public void startPrefixMapping(String prefix, String url) throws SAXException {
        if (addNamespace) {
            this.startControlledPrefixMapping();
        }
    }

    /**
     * Adds the name space and sets the name space state variable.
     *
     * @throws SAXException If the name space could not be set
     */
    private void startControlledPrefixMapping() throws SAXException {
        if (this.addNamespace && !this.addedNamespace) {
            super.startPrefixMapping("", this.usedNamespaceUri);
            this.addedNamespace = true;
        }
    }
}
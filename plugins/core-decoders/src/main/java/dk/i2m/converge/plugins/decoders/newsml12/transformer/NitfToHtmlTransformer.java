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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Transformer for turning NITF markup to HTML markup.
 *
 * @author Allan Lykke Christensen
 */
public class NitfToHtmlTransformer {

    /**
     * Link to the XSL transforming NITF to HTML.
     */
    private static final String NITF_TO_HTML = "/dk/i2m/converge/plugins/decoders/newsml12/styles/nitf-to-html.xsl";
    /**
     * Transformer used for transforming NITF to HTML.
     */
    private Transformer transformer = null;

    /**
     * Transform a NITF marked-up String to an HTML marked-up String.
     *
     * @param nitf NITF marked-up String
     * @return HTML marked-up String
     * @throws TransformerException If the String could not be turned into an
     * HTML marked-up string. This could be due to invalid input (not NITF) or
     * missing style sheet.
     */
    public String transform(String nitf) throws TransformerException {
        StringWriter writer = new StringWriter();
        getTransformer().transform(new StreamSource(new StringReader(nitf)), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Obtain a transformer for turning NITF into HTML.
     *
     * @return {@link Transformer} for turning NITF into HTML.
     * @throws TransformerConfigurationException If the {@link Transformer}
     * could not be created or configured
     */
    private Transformer getTransformer() throws TransformerConfigurationException {
        if (transformer == null) {
            TransformerFactory factory = TransformerFactory.newInstance();
            InputStream style = getClass().getResourceAsStream(NITF_TO_HTML);

            if (style == null) {
                throw new TransformerConfigurationException("XSL "
                        + NITF_TO_HTML + " could not be found");
            }

            transformer = factory.newTransformer(new StreamSource(style));
        }
        return transformer;
    }
}

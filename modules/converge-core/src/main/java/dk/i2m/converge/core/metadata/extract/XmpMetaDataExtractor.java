/*
 * Copyright (C) 2015 Allan Lykke Christensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.metadata.extract;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.properties.XMPProperty;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

/**
 * Meta data extractor for files containing XMP headers.
 *
 * @author Allan Lykke Christensen
 */
public class XmpMetaDataExtractor implements MetaDataExtractor {

    /**
     * Meta data property containing the title.
     */
    public static final String META_TITLE = "title";
    /**
     * Meta data property containing the headline.
     */
    public static final String META_HEADLINE = "headline";
    /**
     * Meta data property containing the description.
     */
    public static final String META_DESCRIPTION = "description";
    /**
     * Meta data property containing the subject. The property must be combined
     * with an index (e.g. META_SUBJECT_X + "1").
     */
    public static final String META_SUBJECT_X = "subject-";
    /**
     * Name space of the Dublin Core.
     */
    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";
    /**
     * Description field in the Dublin Core name space.
     */
    private static final String NS_DC_DESCRIPTION = "description";
    /**
     * Title field in the Dublin Core name space.
     */
    private static final String NS_DC_TITLE = "title";
    /**
     * Subject field in the Dublic Core name space.
     */
    private static final String NS_DC_SUBJECT = "subject";
    /**
     * Name space of Photoshop tags.
     */
    private static final String NS_PHOTOSHOP = "http://ns.adobe.com/photoshop/1.0/";
    /**
     * Headline field in the Photoshop namespace.
     */
    private static final String NS_PHOTOSHOP_HEADLINE = "Headline";
    /**
     * First item index of the XML properties.
     */
    private static final int FIRST = 1;

    private final Map<String, String> meta = new HashMap<String, String>();

    /**
     * Extracts the XMP meta data from the given {@code file} and returns a
     * {@link Map} of meta data.
     *
     * @param file {@link File} to extract meta data
     * @return {@link Map} of meta data extracted from the {@code file}
     * @throws CannotExtractMetaDataException If meta data could not be
     * extracted from the {@code file}
     */
    @Override
    public Map<String, String> extract(File file) throws CannotExtractMetaDataException {
        meta.clear();

        try {
            String xml = Sanselan.getXmpXml(file);

            if (xml == null) {
                return meta;
            }

            XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xml);

            if (xmpMeta.doesPropertyExist(NS_PHOTOSHOP, NS_PHOTOSHOP_HEADLINE)) {
                XMPProperty headlineProperty = xmpMeta.getProperty(NS_PHOTOSHOP, NS_PHOTOSHOP_HEADLINE);
                meta.put(META_HEADLINE, ((String) headlineProperty.getValue()).trim());
            }

            if (xmpMeta.doesArrayItemExist(NS_DC, NS_DC_DESCRIPTION, FIRST)) {
                XMPProperty descriptionProperty = xmpMeta.getArrayItem(NS_DC, NS_DC_DESCRIPTION, FIRST);
                meta.put(META_DESCRIPTION, ((String) descriptionProperty.getValue()).trim());
            }

            if (xmpMeta.doesArrayItemExist(NS_DC, NS_DC_TITLE, FIRST)) {
                XMPProperty titleProperty = xmpMeta.getArrayItem(NS_DC, NS_DC_TITLE, FIRST);
                meta.put(META_TITLE, ((String) titleProperty.getValue()).trim());
            }

            int subjectCount = xmpMeta.countArrayItems(NS_DC, NS_DC_SUBJECT);
            if (subjectCount > 0) {

                for (int i = FIRST; i <= subjectCount; i++) {
                    XMPProperty subjectProperty = xmpMeta.getArrayItem(NS_DC, NS_DC_SUBJECT, i);
                    meta.put(META_SUBJECT_X + i, ((String) subjectProperty.getValue()).trim());
                }
            }

        } catch (XMPException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return meta;
    }

}

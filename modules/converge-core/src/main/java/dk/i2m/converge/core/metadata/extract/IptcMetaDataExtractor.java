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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.ImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;

/**
 * Meta data extractor for files containing IPTC headers.
 *
 * @author Allan Lykke Christensen
 */
public class IptcMetaDataExtractor implements MetaDataExtractor {

    private static final Logger LOG = Logger.getLogger(IptcMetaDataExtractor.class.getName());

    private final Map<String, String> meta = new HashMap<String, String>();

    /**
     * Extracts the IPTC meta data from the given {@code file} and returns a
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
            IImageMetadata imageMetaData = Sanselan.getMetadata(file);

            if (imageMetaData != null) {
                ArrayList items = imageMetaData.getItems();
                for (int i = 0; i < items.size(); i++) {
                    try {
                        ImageMetadata.Item item = (ImageMetadata.Item) items.get(i);

                        if (item instanceof TiffImageMetadata.Item) {
                            TiffImageMetadata.Item tiff = (TiffImageMetadata.Item) item;
                            meta.put(tiff.getTiffField().getTagName(), "" + tiff.getTiffField().getValue());
                        } else {
                            meta.put(item.getKeyword(), item.getText());
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                        LOG.log(Level.FINE, "", ex);
                    }
                }
            }
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return meta;
    }
}

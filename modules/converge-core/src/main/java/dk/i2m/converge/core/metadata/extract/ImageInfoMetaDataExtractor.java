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
import java.util.HashMap;
import java.util.Map;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

/**
 * Extracts information about an image.
 *
 * @author Allan Lykke Christensen
 */
public class ImageInfoMetaDataExtractor implements MetaDataExtractor {

    private final Map<String, String> meta = new HashMap<String, String>();
    /**
     * Meta property containing the color space of the image.
     */
    public static final String META_COLOR_SPACE = "colourSpace";
    /**
     * Meta property containing the height of the image.
     */
    public static final String META_HEIGHT = "height";
    /**
     * Meta property containing the width of the image.
     */
    public static final String META_WIDTH = "width";
    /**
     * Meta property containing whether the image is progressive.
     */
    public static final String META_PROGRESSIVE = "progressive";

    /**
     * Extracts the image meta data from the given {@link file}.
     *
     * @param file Image to extract meta data
     * @return {link Map} of meta data properties of the given image
     * @throws CannotExtractMetaDataException If no meta data could be extracted
     */
    @Override
    public Map<String, String> extract(File file) throws CannotExtractMetaDataException {
        meta.clear();

        try {
            ImageInfo imageInfo = Sanselan.getImageInfo(file);

            if (imageInfo != null) {
                meta.put(META_COLOR_SPACE, imageInfo.getColorTypeDescription());
                meta.put(META_HEIGHT, String.valueOf(imageInfo.getHeight()));
                meta.put(META_WIDTH, String.valueOf(imageInfo.getWidth()));
                meta.put(META_PROGRESSIVE, String.valueOf(imageInfo.getIsProgressive()));
            }
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return meta;
    }

}

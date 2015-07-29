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
package dk.i2m.converge.core.content.metadata.extract;

import dk.i2m.converge.core.metadata.extract.CannotExtractMetaDataException;
import dk.i2m.converge.core.metadata.extract.ImageInfoMetaDataExtractor;
import dk.i2m.converge.core.metadata.extract.MetaDataExtractor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ImageInfoMetaDataExtractor}.
 *
 * @author Allan Lykke Christensen
 */
public class ImageInfoMetaDataExtractorTest {

    @Test
    public void imageInfoMetaDataExtractor_validImage_propertiesExtracted() throws IOException {
        // Arrange
        MetaDataExtractor imageInfo = new ImageInfoMetaDataExtractor();
        File file = getValidImageFile();
        Map<String, String> metadata = new HashMap<String, String>();

        // Act
        try {
            metadata = imageInfo.extract(file);
        } catch (CannotExtractMetaDataException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertTrue(metadata.containsKey(ImageInfoMetaDataExtractor.META_COLOR_SPACE));
        assertTrue(metadata.containsKey(ImageInfoMetaDataExtractor.META_HEIGHT));
        assertTrue(metadata.containsKey(ImageInfoMetaDataExtractor.META_PROGRESSIVE));
        assertTrue(metadata.containsKey(ImageInfoMetaDataExtractor.META_WIDTH));
        assertEquals("2448", metadata.get(ImageInfoMetaDataExtractor.META_HEIGHT));
        assertEquals("3264", metadata.get(ImageInfoMetaDataExtractor.META_WIDTH));
        assertEquals("RGB", metadata.get(ImageInfoMetaDataExtractor.META_COLOR_SPACE));
        assertEquals("false", metadata.get(ImageInfoMetaDataExtractor.META_PROGRESSIVE));
    }

    private File getValidImageFile() throws IOException {
        File file = File.createTempFile("ImageInfoMetaDataExtractorTest", "validImageFile");
        InputStream stream = getClass().getResourceAsStream("/dk/i2m/converge/core/content/metadata/extract/test-image-info.JPG");
        FileUtils.copyInputStreamToFile(stream, file);
        return file;
    }
}

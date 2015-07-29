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
import dk.i2m.converge.core.metadata.extract.MetaDataExtractor;
import dk.i2m.converge.core.metadata.extract.Mp3MetaDataExtractor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Mp3MetaDataExtractor}.
 *
 * @author Allan Lykke Christensen
 */
public class Mp3MetaDataExtractorTest {

    @Test
    public void mp3MetaDataExtractor_validMp3_propertiesExtracted() throws IOException {
        // Arrange
        MetaDataExtractor mp3 = new Mp3MetaDataExtractor();
        File file = getValidMp3File();
        Map<String, String> metadata = new HashMap<String, String>();

        // Act
        try {
            metadata = mp3.extract(file);
        } catch (CannotExtractMetaDataException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertTrue(metadata.containsKey(Mp3MetaDataExtractor.META_TITLE));
        assertTrue(metadata.containsKey(Mp3MetaDataExtractor.META_HEADLINE));
        assertTrue(metadata.containsKey(Mp3MetaDataExtractor.META_DESCRIPTION));
        assertEquals("The test song", metadata.get(Mp3MetaDataExtractor.META_TITLE));
        assertEquals("The test song", metadata.get(Mp3MetaDataExtractor.META_HEADLINE));
        assertEquals("Just a strange song created ", metadata.get(Mp3MetaDataExtractor.META_DESCRIPTION));
    }

    private File getValidMp3File() throws IOException {
        File file = File.createTempFile("Mp3MetaDataExtractorTest", "validMp3File");
        InputStream mp3File = getClass().getResourceAsStream("/dk/i2m/converge/core/content/metadata/extract/test-song.mp3");
        FileUtils.copyInputStreamToFile(mp3File, file);
        return file;
    }
}

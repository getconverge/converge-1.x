/*
 * Copyright (C) 2014 Fred Ondieki, Allan Lykke Christensen
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
package dk.i2m.converge.core.content.catalogue;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link MediaItemRendition}.
 *
 * @author Fred Ondieki
 * @author Allan Lykke Christensen
 */
public class MediaItemRenditionTest {

    @Test
    public void mediaItemRendition_getAbsolutePathWin_correctAbsolutePathReturned() {
        // Arrange
        String webAccesPath = "http://localhost:8282/photos";
        String path = "2014\\7\\3\\934151";
        Catalogue catalogue = new Catalogue();
        catalogue.setWebAccess(webAccesPath);
        MediaItem mediaItem = new MediaItem();
        mediaItem.setTitle("Test");
        mediaItem.setCatalogue(catalogue);
        MediaItemRendition rendition = new MediaItemRendition();
        rendition.setPath(path);
        rendition.setFilename("3.png");
        rendition.setMediaItem(mediaItem);
        mediaItem.getRenditions().add(rendition);

        // Act
        String actual = rendition.getAbsoluteFilename();

        // Assert
        String expected = "http://localhost:8282/photos/2014/7/3/934151/3.png";
        assertEquals(expected, actual);
    }

    @Test
    public void mediaItemRendition_getAbsolutePathUnix_correctAbsolutePathReturned() {
        // Arrange
        String webAccesPath = "http://localhost:8282/photos";
        String path = "2014/7/3/934151";
        Catalogue catalogue = new Catalogue();
        catalogue.setWebAccess(webAccesPath);
        MediaItem mediaItem = new MediaItem();
        mediaItem.setTitle("Test");
        mediaItem.setCatalogue(catalogue);
        MediaItemRendition rendition = new MediaItemRendition();
        rendition.setPath(path);
        rendition.setFilename("3.png");
        rendition.setMediaItem(mediaItem);
        mediaItem.getRenditions().add(rendition);

        // Act
        String actual = rendition.getAbsoluteFilename();

        // Assert
        String expected = "http://localhost:8282/photos/2014/7/3/934151/3.png";
        assertEquals(expected, actual);
    }

    @Test
    public void mediaItemRendition_renditionWithoutFilename_returnsHash() {
        // Arrange
        Catalogue c = new Catalogue();
        MediaItem item = new MediaItem();
        item.setCatalogue(c);
        MediaItemRendition rendition = new MediaItemRendition();
        rendition.setFilename(null);
        rendition.setMediaItem(item);

        // Act
        String actual = rendition.getAbsoluteFilename();

        // Assert
        assertEquals("#", actual);
    }

    @Test
    public void mediaItemRendition_withoutMediaItem_returnsHash() {
        // Arrange
        MediaItemRendition rendition = new MediaItemRendition();
        rendition.setMediaItem(null);

        // Act
        String actual = rendition.getAbsoluteFilename();

        // Assert
        assertEquals("#", actual);
    }
}

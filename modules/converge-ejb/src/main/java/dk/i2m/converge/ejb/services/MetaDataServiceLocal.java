/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.EnrichException;
import javax.ejb.Local;

/**
 * Local interface for {@link MetaDataServiceBean} responsible for providing
 * services for the enterprise java beans acting as facades.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface MetaDataServiceLocal {

    /**
     * Extract meta data from any file.
     * <p/>
     * @param location Location of the file
     * @return {@link Map} of meta data
     */
    java.util.Map<java.lang.String, java.lang.String> extract(
            java.lang.String location);

    /**
     * Extract MP3 meta data from audio file.
     * <p/>
     * @param location Location of the file
     * @return {@link Map} of MP3 meta data
     * @throws CannotExtractMetaDataException If meta data could not be extracted from the given file
     */
    java.util.Map<java.lang.String, java.lang.String> extractFromMp3(
            java.lang.String location) throws
            dk.i2m.converge.ejb.services.CannotExtractMetaDataException;

    /**
     * Extract XMP meta data from a media file.
     * <p/>
     * @param location Location of the file
     * @return {@link Map} of XMP meta data
     * @throws CannotExtractMetaDataException If meta data could not be extracted from the given file
     */
    java.util.Map<java.lang.String, java.lang.String> extractXmp(
            java.lang.String location) throws
            dk.i2m.converge.ejb.services.CannotExtractMetaDataException;

    /**
     * Extract IPTC meta data from an image file.
     * <p/>
     * @param location Location of the file
     * @return {@link Map} of IPTC meta data
     * @throws CannotExtractMetaDataException If meta data could not be extracted from the given file
     */
    java.util.Map<java.lang.String, java.lang.String> extractIPTC(
            java.lang.String location) throws
            dk.i2m.converge.ejb.services.CannotExtractMetaDataException;

    /**
     * Extract IPTC meta data from an image file.
     * <p/>
     * @param location Location of the file
     * @return {@link Map} of IPTC meta data
     * @throws CannotExtractMetaDataException If meta data could not be extracted from the given file
     */
    java.util.Map<java.lang.String, java.lang.String> extractImageInfo(
            java.lang.String location) throws
            dk.i2m.converge.ejb.services.CannotExtractMetaDataException;

    /**
     * Gets {@link Concept}s from the given story using the OpenCalais service.
     * <p/>
     * @param story Story for which to get {@link Concept}s
     * @return {@link List} of {@link Concept}s matching the story
     * @throws EnrichException If {@link Concept}s could not be extracted
     */
    java.util.List<dk.i2m.converge.core.metadata.Concept> enrich(
            java.lang.String story) throws EnrichException;

    /**
     * Extracts the content of a given {@link MediaItemRendition}. This is only
     * possible for Microsoft Word and Adobe PDF documents containing text.
     * 
     * @param mir {@link MediaItemRendition} from which to extract content
     * @return {@link String} containing the extractable content of the {@link MediaItemRendition}
     */
    java.lang.String extractContent(
            dk.i2m.converge.core.content.catalogue.MediaItemRendition mir);
}

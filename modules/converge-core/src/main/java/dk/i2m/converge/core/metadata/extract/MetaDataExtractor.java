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
import java.util.Map;

/**
 * Interface to implement for extracting meta data from a file.
 *
 * @author Allan Lykke Christensen
 */
public interface MetaDataExtractor {

    /**
     * Extracts meta data from the given {@code file} and returns a {@link Map}
     * of keys and values containing the meta data.
     *
     * @param file {@link File} to extract meta data
     * @return {@link Map} of keys and values containing meta data
     * @throws CannotExtractMetaDataException If meta data could not be
     * extracted from the file
     */
    Map<String, String> extract(File file) throws CannotExtractMetaDataException;
}

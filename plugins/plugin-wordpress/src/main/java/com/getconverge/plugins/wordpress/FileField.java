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
package com.getconverge.plugins.wordpress;

/**
 * Fields of a Wordpress media file.
 *
 * @author Allan Lykke Christensen
 */
public enum FileField {

    /**
     * Name of the file, used during upload.
     */
    NAME,
    /**
     * MIME type of the file, used during upload.
     */
    TYPE,
    /**
     * Binary content of the file, used during upload.
     */
    BITS,
    /**
     * Should an existing file be overwritten, used during upload.
     */
    OVERWRITE,
    /**
     * Id of the post where the file should be attached, used during upload.
     */
    POST_ID,
    /**
     * Id of the file after upload.
     */
    ID,
    /**
     * Name of the file after upload.
     */
    FILE,
    /**
     * URL of the file after upload.
     */
    URL;

    /**
     * Returns a {@link String} representation of the {@link FileField}. The
     * {@link String} representation is a lower case version of the enumeration
     * name.
     *
     * @return Lower case version of the enumeration name
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

}

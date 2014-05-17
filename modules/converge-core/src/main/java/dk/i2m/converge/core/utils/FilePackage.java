/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.utils;

import java.io.Serializable;

/**
 * Model containing information about a file that that should be downloaded via
 * an HTTP response.
 *
 * @author Allan Lykke Christensen
 */
public class FilePackage implements Serializable {

    /** Name of the file. */
    private String filename;

    /** Content type of the file. */
    private String contentType;

    /** Binary representation of the file. */
    private byte[] binary;

    /**
     * Creates a new instance of {@link FilePackage}.
     */
    public FilePackage() {
        this.filename = "";
        this.contentType = "";
    }

    /**
     * Creates a new instance of {@link FilePackage}.
     *
     * @param filename
     *          Name of the file
     * @param contentType
     *          Content type of the file
     * @param binary
     *          Binary representation of the file
     */
    public FilePackage(final String filename,
            final String contentType,
            final byte[] binary) {
        this.filename = filename;
        this.contentType = contentType;
        this.binary = binary;
    }

    /**
     * Gets the binary representation of the file.
     *
     * @return Binary representation of the file.
     */
    public byte[] getBinary() {
        return binary;
    }

    /**
     * Sets the binary representation of the file.
     *
     * @param binary
     *          Binary representation of the file.
     */
    public void setBinary(final byte[] binary) {
        this.binary = binary;
    }

    /**
     * Gets the content type of the file.
     *
     * @return Content type of the file
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of the file.
     *
     * @param contentType
     *          Content type of the file
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the file name of the file.
     *
     * @return File name of the file
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the file name of the file.
     *
     * @param filename
     *          File name of the file
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}

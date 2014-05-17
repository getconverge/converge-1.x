/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.plugins.joomla;

/**
 * Media file uploaded through the Joomla plug-ins.
 *
 * @author Allan Lykke Christensen
 */
public class UploadedMediaFile {

    private String url = "";

    private String caption = "";

    /**
     * Creates a new instance of {@link UploadedMediaFile}.
     */
    public UploadedMediaFile() {
        url = "";
        caption = "";
    }

    /**
     * Creates a new instance of {@link UploadedMediaFile}.
     * 
     * @param url
     *          URL of the media file
     * @param caption 
     *          Caption of the media file
     */
    public UploadedMediaFile(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }

    /**
     * Gets the caption of the media file.
     * 
     * @return Caption of the media file
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the media file.
     * 
     * @param caption 
     *          Caption of the media file
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the URL of the media file.
     * 
     * @return URL of the media file
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the media file.
     * 
     * @param url 
     *          URL of the media file
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.core.activitystream;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Description of a resource providing a media representation of an activity,
 * intended for human consumption.
 *
 * @author Allan Lykke Christensen
 */
@XmlType
@XmlRootElement(name = "image")
public class MediaLink {

    private String url;
    private Integer width = null;
    private Integer height = null;

    /**
     * Creates a new instance of {@link MediaLink}.
     */
    public MediaLink() {
    }

    /**
     * Creates a new instance of {@link MediaLink}.
     *
     * @param url URL of the media resource
     * @param width Width (in pixels) of the media resource
     * @param height Height (in pixels) of the media resource
     */
    public MediaLink(String url, Integer width, Integer height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    /**
     * The IRI of the media resource being linked.
     *
     * @return URL of the media resource
     */
    public String getUrl() {
        return url;
    }

    /**
     * The IRI of the media resource being linked.
     *
     * @param url URL of the media resource
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * A hint to the consumer about the width, in pixels, of the media resource
     * identified by the url property. A media link MAY contain a width property
     * when the target resource is a visual media item such as an image, video
     * or embeddable HTML page.
     *
     * @return Width (in pixels) of the media resource
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * A hint to the consumer about the width, in pixels, of the media resource
     * identified by the url property. A media link MAY contain a width property
     * when the target resource is a visual media item such as an image, video
     * or embeddable HTML page.
     *
     * @param width Width (in pixels) of the media resource
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * A hint to the consumer about the height, in pixels, of the media resource
     * identified by the url property. A media link MAY contain a height
     * property when the target resource is a visual media item such as an
     * image, video or embeddable HTML page.
     *
     * @return Height (in pixels) of the media resource
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * A hint to the consumer about the height, in pixels, of the media resource
     * identified by the url property. A media link MAY contain a height
     * property when the target resource is a visual media item such as an
     * image, video or embeddable HTML page.
     *
     * @param height Height (in pixels) of the media resource
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

}

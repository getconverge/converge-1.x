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

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * An {@link Activity} tells the story of a person performing an object on or
 * with an object.
 *
 * @author Allan Lykke Christensen
 */
@XmlType
@XmlRootElement(name = "activity")
public class Activity {

    private Long id;
    private String content;
    private Date published;
    private Person author;
    private String url;

    /**
     * Provides a permanent, universally unique identifier for the activity.
     *
     * @return Unique identifier of the {@link Activity}
     */
    public Long getId() {
        return id;
    }

    /**
     * Provides a permanent, universally unique identifier for the activity.
     *
     * @param id Unique identifier of the {@link Activity}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Natural-language description of the activity encoded as a single field.
     * String containing HTML markup.
     *
     * @return Natural-language description of the activity encoded as HTML
     */
    public String getContent() {
        return content;
    }

    /**
     * Natural-language description of the activity encoded as a single field.
     * String containing HTML markup.
     *
     * @param content Natural-language description of the activity encoded as
     * HTML
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Date and time at which the activity was published.
     *
     * @return Date and time at which the activity was published
     */
    public Date getPublished() {
        return published;
    }

    /**
     * Date and time at which the activity was published.
     *
     * @param published Date and time at which the activity was published
     */
    public void setPublished(Date published) {
        this.published = published;
    }

    /**
     * Describes the author that created or authored the activity.
     *
     * @return Author that created the activity
     */
    public Person getAuthor() {
        return author;
    }

    /**
     * Describes the author that created or authored the activity.
     *
     * @param author Author that created the activity
     */
    public void setAuthor(Person author) {
        this.author = author;
    }

    /**
     * URL identifying a resource providing an HTML representation of the
     *
     * @return URL of the HTML representation of the activity
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL identifying a resource providing an HTML representation of the
     *
     * @param url URL of the HTML representation of the activity
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

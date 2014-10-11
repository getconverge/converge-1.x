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
 *
 * @author Allan Lykke Christensen
 */
@XmlType
@XmlRootElement(name = "person")
public class Person {

    private static final String OBJECT_TYPE = "person";
    private String id;
    private String url;
    private MediaLink image;
    private String displayName;

    public Person() {
    }

    public Person(String id, String url, MediaLink image, String displayName) {
        this.id = id;
        this.url = url;
        this.image = image;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the object type of the person.
     *
     * @return {@code person}
     */
    public String getObjectType() {
        return OBJECT_TYPE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Description of a resource providing a visual representation of the
     * person, intended for human consumption. The image SHOULD have an aspect
     * ratio of one (horizontal) to one (vertical) and SHOULD be suitable for
     * presentation at a small size.
     *
     * @return Resource providing a visual representation of the person
     */
    public MediaLink getImage() {
        return image;
    }

    /**
     * Description of a resource providing a visual representation of the
     * person, intended for human consumption. The image SHOULD have an aspect
     * ratio of one (horizontal) to one (vertical) and SHOULD be suitable for
     * presentation at a small size.
     *
     * @param image Resource providing a visual representation of the person
     */
    public void setImage(MediaLink image) {
        this.image = image;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}

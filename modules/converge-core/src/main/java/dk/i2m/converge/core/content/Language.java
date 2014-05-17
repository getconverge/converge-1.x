/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.content;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

/**
 * Definition of a {@link Language} that can be associated with {@link Outlet}s
 * and content items.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "content_language")
@NamedQueries({})
public class Language implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    /**
     * Creates a new instance of {@link Language}.
     */
    public Language() {
        this("", "");
    }

    /**
     * Creates a new instance of {@link Language}.
     * 
     * @param name
     *          Name of the {@link Language}
     * @param code
     *          Code of the {@link Language}
     */
    public Language(String name, String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Gets the unique identifier of the {@link Language}.
     *
     * @return Unique identifier of the {@link Language}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Language}. This method should
     * not be invoked manually as it is set automatically by JPA.
     *
     * @param id
     *          Unique identifier of the {@link Language}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ISO 639-1 code of the {@link Language}.
     *
     * @return ISO 639-1 code of the {@link Language}
     * @see <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1 codes on Wikipedia</a>
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the ISO 639-1 code of the {@link Language}.
     *
     * @param code
     *          ISO 639-1 code of the {@link Language}
     * @see <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1 codes on Wikipedia</a>
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the name of the {@link Language}.
     *
     * @return Name of the {@link Language}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the {@link Language}.
     *
     * @param name
     *          Name of the {@link Language}
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Language other = (Language) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "/name=" + name + "/code=" + code + ']';
    }
}

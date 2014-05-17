/*
 *  Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.core.security;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * {@link Privilege} protected by a {@link UserRole}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "id")
    private SystemPrivilege id;

    /**
     * Creates a new {@link Privilege}.
     */
    public Privilege() {
    }

    /**
     * Creates a new {@link Privilege}.
     *
     * @param id
     *      {@link SystemPrivilege}
     */
    public Privilege(SystemPrivilege id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the {@link Privilege}.
     *
     * @return Unique identifier of the {@link Privilege}
     */
    public SystemPrivilege getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Privilege}.
     *
     * @param id
     *          Unique identifier of the {@link Privilege}
     */
    public void setId(SystemPrivilege id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Privilege other = (Privilege) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

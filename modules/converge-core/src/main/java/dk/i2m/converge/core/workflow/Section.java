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
package dk.i2m.converge.core.workflow;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Section that is part of an {@link Outlet}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "outlet_section")
@NamedQueries({
    @NamedQuery(name = Section.FIND_BY_OUTLET_AND_NAME, query = "SELECT s FROM Section s WHERE s.outlet = :outlet AND s.name LIKE :sectionName")
})
public class Section implements Serializable {

    public static final String FIND_BY_OUTLET_AND_NAME = "Section.findByOutletAndName";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name = "";

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Section parent = null;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    private Outlet outlet = null;

    @Column(name = "description") @Lob
    private String description;

    @Column(name = "active")
    private boolean active = true;

    @javax.persistence.Version
    @Column(name = "opt_lock")
    private int versionIdentifier;

    /**
     * Creates a new instance of {@link EditionSection}.
     */
    public Section() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder("");

        Section current = this;
        fullName.insert(0, current.getName());

        while (current.getParent() != null) {
            current = current.getParent();
            fullName.insert(0, " >> ");
            fullName.insert(0, current.getName());
        }

        return fullName.toString();
    }

    public Section getParent() {
        return parent;
    }

    public void setParent(Section parent) {
        this.parent = parent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getVersionIdentifier() {
        return versionIdentifier;
    }

    public void setVersionIdentifier(int versionIdentifier) {
        this.versionIdentifier = versionIdentifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Section other = (Section) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + " / Full name=" + getFullName() + "]";
    }
}

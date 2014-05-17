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
package dk.i2m.converge.core.metadata;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 *
 *
 * @author Allan Lykke Christensen
 */
@Entity
@DiscriminatorValue("ORGANISATION")
public class Organisation extends Concept {

    @Column(name = "org_founded")
    private String founded;

    @Column(name = "org_dissolved")
    private String dissolved;

    @ManyToMany
    @JoinTable(name = "organisation_location",
        joinColumns = {@JoinColumn(referencedColumnName = "id", name = "organisation_id", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "location_id", nullable = false)})
    private List<GeoArea> locations = new ArrayList<GeoArea>();

    @ManyToMany(mappedBy = "affilliation")
    private List<Person> persons;

    /**
     * Create a new instance of {@link Organisation}.
     */
    public Organisation() {
    }

    /**
     * Create a new instance of {@link Organisation}.
     *
     * @param name
     *          Name of the {@link Organisation}
     * @param description
     *          Description of the {@link Organisation}
     */
    public Organisation(String name, String description) {
        setName(name);
        setDefinition(description);
    }

    public String getDissolved() {
        return dissolved;
    }

    public void setDissolved(String dissolved) {
        this.dissolved = dissolved;
    }

    public String getFounded() {
        return founded;
    }

    public void setFounded(String founded) {
        this.founded = founded;
    }

    public List<GeoArea> getLocations() {
        return locations;
    }

    public void setLocations(List<GeoArea> locations) {
        this.locations = locations;
    }

    /** {@inheritDoc } */
    @Override
    public String getTypeId() {
        return "organisation";
    }
}

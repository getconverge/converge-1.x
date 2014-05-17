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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 *
 * @author Allan Lykke Christensen
 */
@Entity
@DiscriminatorValue("GEOAREA")
public class GeoArea extends Concept {

    @Column(name = "geo_latitude")
    private double latitude;

    @Column(name = "geo_longitude")
    private double longitude;

    @Column(name = "geo_altitude")
    private int altitude;

    @ManyToMany(mappedBy = "locations")
    private List<Organisation> organisations;

    /**
     * Creates a new instance of {@link GeoArea}.
     */
    public GeoArea() {
    }

    /**
     * Creates a new instance of {@link GeoArea}.
     *
     * @param name
     *          Name of the location
     * @param description
     *          Description of the location
     */
    public GeoArea(String name, String description) {
        setName(name);
        setDefinition(description);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeId() {
        return "geoArea";
    }
}

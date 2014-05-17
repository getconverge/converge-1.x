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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A Point of Interest (POI) is a place “on the map” of interest to people,
 * which is not necessarily a geographical feature, for example concert venue,
 * cinema, sports stadium.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@DiscriminatorValue("POI")
public class PointOfInterest extends Concept {

    @Column(name = "poi_open_hours")
    private String openHours;

    @Column(name = "poi_capacity")
    private String capacity;

    @Column(name = "poi_contact_info")
    private String contactInfo;

    @Column(name = "poi_address_info")
    private String addressInfo;

    @Column(name = "poi_latitude")
    private double latitude;

    @Column(name = "poi_longitude")
    private double longitude;

    /**
     * Creates a new instance of {@link PointOfInterest}.
     */
    public PointOfInterest() {
    }

    /**
     * Creates a new instance of {@link PointOfInterest}.
     *
     * @param name
     *          Name of the point of interest
     * @param description
     *          Description of the point of interest
     */
    public PointOfInterest(String name, String description) {
        setName(name);
        setDefinition(description);
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    /** {@inheritDoc } */
    @Override
    public String getTypeId() {
        return "poi";
    }
}

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
 * @author Allan Lykke Christensen
 */
@Entity
@DiscriminatorValue("PERSON")
public class Person extends Concept {

    @Column(name="person_born")
    private String born = "";

    @Column(name="person_died")
    private String died = "";

    @ManyToMany
    @JoinTable(name = "person_affiliation",
        joinColumns = {@JoinColumn(referencedColumnName = "id", name = "person_id", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "organisation_id", nullable = false)})
    private List<Organisation> affilliation = new ArrayList<Organisation>();

    /**
     * Create a new instance of {@link Person}.
     */
    public Person() {
    }

    /**
     * Create a new instance of {@link Person}.
     *
     * @param name
     *          Name of the {@link Person}
     * @param description
     *          Description of the {@link Person}
     */
    public Person(String name, String description) {
        setName(name);
        setDefinition(description);
    }


    public List<Organisation> getAffilliation() {
        return affilliation;
    }

    public void setAffilliation(List<Organisation> affilliation) {
        this.affilliation = affilliation;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getDied() {
        return died;
    }

    public void setDied(String died) {
        this.died = died;
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeId() {
        return "person";
    }
}

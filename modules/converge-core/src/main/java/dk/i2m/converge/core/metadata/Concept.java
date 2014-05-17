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

import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * A {@link Concept} conveys knowledge about a single concept, whether a
 * real-world entity such as a person, or an abstract concept such as a subject
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "concept")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 20)
@NamedQueries({
    @NamedQuery(name = Concept.FIND_BY_NAME, query = "SELECT c FROM Concept AS c WHERE c.name=:name"),
    @NamedQuery(name = Concept.FIND_BY_LIKE_NAME, query = "SELECT c FROM Concept AS c WHERE c.name LIKE :name"),
    @NamedQuery(name = Concept.FIND_BY_NAME_OR_DEFINITION, query = "SELECT c FROM Concept AS c WHERE c.name LIKE :keyword OR c.definition LIKE :keyword"),
    @NamedQuery(name = Concept.FIND_RECENTLY_ADDED, query = "SELECT c FROM Concept AS c ORDER BY c.created DESC"),
    @NamedQuery(name = Concept.FIND_BY_PARENT, query = "SELECT c FROM Concept AS c WHERE :parent MEMBER OF c.broader"),
    @NamedQuery(name = Concept.FIND_BY_ORPHANS, query = "SELECT c FROM Concept AS c WHERE c.broader IS EMPTY"),
    @NamedQuery(name = Concept.FIND_PARENT_SUBJECTS, query = "SELECT s FROM Subject AS s WHERE s.broader IS EMPTY"),
    @NamedQuery(name = Concept.FIND_BY_CODE, query = "SELECT c FROM Concept AS c WHERE c.code=:code")
})
public abstract class Concept implements Serializable {

    /** Query identifier for finding all {@link Concept}s by a given parent. */
    public static final String FIND_BY_PARENT = "Concept.findByParent";

    /** Query identifier for finding all orphan {@link Concept}s. */
    public static final String FIND_BY_ORPHANS = "Concept.findByOrphans";

    /** Query identifier for finding all orphan {@link Concept}s. */
    public static final String FIND_PARENT_SUBJECTS = "Concept.findParentSubjects";

    /** Query identifier for finding a {@link Concept} by its unique code. */
    public static final String FIND_BY_CODE = "Concept.findByCode";

    /** Query identifier for finding a {@link Concept} by name. */
    public static final String FIND_BY_NAME = "Concept.findByName";

    /** Query identifier for finding {@link Concept}s by the like of their name. */
    public static final String FIND_BY_LIKE_NAME = "Concept.findByLikeName";

    /** Query identifier for finding a {@link Concept} by name or definition. */
    public static final String FIND_BY_NAME_OR_DEFINITION = "Concept.findByNameOrDefinition";

    /** Query identifier for finding recently added {@link Concept}s. */
    public static final String FIND_RECENTLY_ADDED = "Concept.findRecentlyAdded";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", unique=true)
    private String code = "";

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Calendar created;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Calendar updated;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private UserAccount updatedBy;

    @Column(name = "name")
    private String name = "";

    @Column(name = "definition") @Lob
    private String definition = "";

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "concept_same_as", 
        joinColumns = { @JoinColumn(referencedColumnName = "id", name = "concept_id1", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "concept_id2", nullable = false)})
    private List<Concept> sameAs = new ArrayList<Concept>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "concept_related",
        joinColumns = {@JoinColumn(referencedColumnName = "id", name = "concept_id1", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "concept_id2", nullable = false)})
    private List<Concept> related = new ArrayList<Concept>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "concept_broader",
        joinColumns = {@JoinColumn(referencedColumnName = "id", name = "broader_id", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "narrower_id", nullable = false)})
    private List<Concept> broader = new ArrayList<Concept>();

    @ManyToMany(mappedBy = "broader")
    private List<Concept> narrower = new ArrayList<Concept>();

    @javax.persistence.Version
    @Column(name = "opt_lock")
    private int versionIdentifier;

    /**
     * Gets the unique identifier of the {@link Concept}.
     *
     * @return Unique identifier of the {@link Concept}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Concept}.
     *
     * @param id
     *          Unique identifier of the {@link Concept}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the unique code of the {@link Concept}.
     *
     * @return Unique code of the {@link Concept}
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the unique code of the {@link Concept}.
     *
     * @param code
     *          Unique code of the {@link Concept}
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the definition of the {@link Concept}.
     *
     * @return Definition of the {@link Concept}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the definition of the {@link Concept}.
     *
     * @param definition
     *          Definition of {@link Concept}
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * Get the {@link Subject} title including the names of the parent
     * {@link Subject}s.
     *
     * @return Title of the {@link Subject} including the names of the parent
     *         {@link Subject}s.
     */
    public String getFullTitle() {
        StringBuilder subjectName = new StringBuilder("");

        Concept current = this;
        subjectName.insert(0, current.getName());

        while (current.getBroader() != null && !current.getBroader().isEmpty()) {
            current = current.getBroader().iterator().next();
            subjectName.insert(0, " >> ");
            subjectName.insert(0, current.getName());
        }

        return subjectName.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Concept> getRelated() {
        return related;
    }

    public void setRelated(List<Concept> related) {
        this.related = related;
    }

    public List<Concept> getSameAs() {
        return sameAs;
    }

    public void setSameAs(List<Concept> sameAs) {
        this.sameAs = sameAs;
    }

    public List<Concept> getNarrower() {
        return narrower;
    }

    public void setNarrower(List<Concept> narrower) {
        this.narrower = narrower;
    }

    public List<Concept> getBroader() {
        return broader;
    }

    public void setBroader(List<Concept> broader) {
        this.broader = broader;
    }


    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    /**
     * Gets the type of {@link Concept}. The type is returned as the class name
     * of the {@link Concept}, e.g. {@code dk.i2m.converge.domain.meta.GeoArea}.
     * 
     * @return Class name of the type of {@link Concept}
     */
    public String getType() {
        return getClass().getName();
    }

    public UserAccount getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserAccount updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getVersionIdentifier() {
        return versionIdentifier;
    }

    /**
     * Gets the ID of the type. This is a shorthand for the type of
     * {@link Concept} also used in the QCode.
     *
     * @return ID of the type
     */
    public abstract String getTypeId();

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Concept other = (Concept) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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

import dk.i2m.converge.core.content.Language;
import dk.i2m.converge.core.security.UserRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.*;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity representing an outlet for news. Responsible for containing information
 * about a single outlet of news.
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
@Entity
@Table(name = "outlet")
@NamedQueries({})
public class Outlet implements Serializable {

    private static final long serialVersionUID = 4L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title = "";
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OutletType type;
    @OneToMany(mappedBy = "outlet", fetch = FetchType.LAZY)
    private List<Edition> editions = new ArrayList<Edition>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "outlet_role", joinColumns = {
        @JoinColumn(referencedColumnName = "id", name = "outlet_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(referencedColumnName = "id", name = "role_id", nullable = false)})
    private List<UserRole> roles = new ArrayList<UserRole>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
    @OneToMany(mappedBy = "outlet", fetch = FetchType.LAZY)
    @PrivateOwned
    private List<Section> sections = new ArrayList<Section>();
    @OneToMany(mappedBy = "outlet", fetch = FetchType.LAZY)
    @PrivateOwned
    private List<OutletEditionAction> editionActions = new ArrayList<OutletEditionAction>();
    @OneToMany(mappedBy = "outlet", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @PrivateOwned
    private List<EditionPattern> editionPatterns = new ArrayList<EditionPattern>();
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;


    /**
     * Creates a new instance of {@link Outlet}.
     */
    public Outlet() {
    }

    /**
     * Gets the unique identifier of the {@link Outlet}.
     *
     * @return Unique identifier of the {@link Outlet}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Outlet}.
     *
     * @param id Unique identifier of the {@link Outlet}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the {@link Outlet}.
     *
     * @return Title of the {@link Outlet}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the {@link Outlet}.
     *
     * @param title Title of the {@link Outlet}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public OutletType getType() {
        return type;
    }

    public void setType(OutletType type) {
        this.type = type;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public List<Edition> getEditions() {
        return editions;
    }

    public void setEditions(List<Edition> editions) {
        this.editions = editions;
    }

    public List<Section> getSections() {
        return sections;
    }

    /**
     * Gets the active {@link Section}s of the {@link Outlet} sorted by their
     * full names.
     *
     * @return {@link List} of active {@link Section}s sorted by their full
     * names
     */
    public List<Section> getActiveSections() {
        List<Section> activeSections = new ArrayList<Section>();
        for (Section section : getSections()) {
            if (section.isActive()) {
                activeSections.add(section);
            }
        }

        Collections.sort(activeSections, new Comparator<Section>() {
            @Override
            public int compare(Section s1, Section s2) {
                return s1.getFullName().compareTo(s2.getFullName());
            }
        });

        return activeSections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    /**
     * Gets a {@link List} of action that should be executed automatically upon
     * closing.
     * <p/>
     * @return {@link List} of action that should be executed upon closing
     */
    public List<OutletEditionAction> getAutomaticEditionActions() {
        List<OutletEditionAction> auto = new ArrayList<OutletEditionAction>();
        for (OutletEditionAction action : editionActions) {
            if (!action.isManualAction()) {
                auto.add(action);
            }
        }
        return auto;
    }

    public List<OutletEditionAction> getEditionActions() {
        return editionActions;
    }

    public void setEditionActions(List<OutletEditionAction> editionActions) {
        this.editionActions = editionActions;
    }

    /**
     * Gets the patterns used for generating {@link Edition}s.
     *
     * @return {@link List} of {@link EditionPattern}s used for generating
     * {@link Edition}s
     */
    public List<EditionPattern> getEditionPatterns() {
        return editionPatterns;
    }

    public void setEditionPatterns(List<EditionPattern> editionPatterns) {
        this.editionPatterns = editionPatterns;
    }

    /**
     * Gets the {@link Language} of the {@link Outlet}.
     *
     * @return {@link Language} of the {@link Outlet}
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the {@link Language} of the {@link Outlet}.
     *
     * @param language {@link Language} of the {@link Outlet}
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Determines if the {@link Outlet} is valid and operational. An
     * {@link Outlet} is valid and operational if all the necessary fields have
     * been set.
     * <p/>
     * @return {@code true} if the {@link Outlet} is valid, otherwise
     * {@code false}
     */
    public boolean isValid() {
        if (getWorkflow() == null) {
            return false;
        }
        if (getLanguage() == null) {
            return false;
        }

        if (!getWorkflow().isValid()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Outlet other = (Outlet) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + "id=" + id + "]";
    }
}

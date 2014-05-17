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

import dk.i2m.converge.core.security.UserRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * {@link Workflow} for processing a news item. {@link Workflow}s are used by
 * news items of {@link Outlet}s. Therefore each {@link Outlet} will have a
 * specific {@link Workflow} assigned.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow")
public class Workflow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description") @Lob
    private String description;

    /** States available for this workflow. */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "workflow", cascade = CascadeType.ALL)
    @OrderBy("order ASC")
    private List<WorkflowState> states = new ArrayList<WorkflowState>();

    /** State given to article submitted for review using this workflow. */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "workflow_state_start")
    private WorkflowState startState;

    /** State of the article when it has completed the workflow. */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "workflow_state_end")
    private WorkflowState endState;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "workflow_state_trash")
    private WorkflowState trashState;

    /**
     * Creates a new instance of {@link Workflow}.
     */
    public Workflow() {
    }

    /**
     * Gets the unique identifier of the {@link Workflow}.
     *
     * @return Unique identifier of the {@link Workflow}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Workflow}.
     *
     * @param id
     *          Unique identifier of the {@link Workflow}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the {@link Workflow}. The name is a user friendly
     * identifier of the {@link Workflow}.
     *
     * @return Name of the {@link Workflow}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the {@link Workflow}.
     *
     * @param name
     *          Name of the {@link Workflow}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the {@link Workflow}. The description is a note
     * to the administrator of the purpose of the {@link Workflow}.
     *
     * @return Description of the {@link Workflow}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the {@link Workflow}. The description is a note
     * to the administrator of the purpose of the {@link Workflow}.
     *
     * @param description
     *          Description of the {@link Workflow}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public WorkflowState getStartState() {
        return startState;
    }

    public void setStartState(WorkflowState startState) {
        this.startState = startState;
    }

    public WorkflowState getEndState() {
        return endState;
    }

    public void setEndState(WorkflowState endState) {
        this.endState = endState;
    }

    /**
     * Gets the {@link WorkflowState} that represents the trash state of the
     * {@link Workflow}.
     *
     * @return {@link WorkflowState} representing the trash state of the
     *         {@link Workflow}
     */
    public WorkflowState getTrashState() {
        return trashState;
    }

    /**
     * Sets the {@link WorkflowState} that represents the trash state of the
     * {@link Workflow}.
     *
     * @param trashState
     *          {@link WorkflowState} representing the trash state of the
     *          {@link Workflow}
     */
    public void setTrashState(WorkflowState trashState) {
        this.trashState = trashState;
    }

    /**
     * Adds a new state to the {@link Workflow}. This method associates the
     * state with the workflow automatically.
     *
     * @param state
     *          State to add to the workflow
     * @return {@code true} if it was added, otherwise {@code false}
     */
    public boolean addState(WorkflowState state) {
        state.setWorkflow(this);
        return getStates().add(state);
    }

    public List<WorkflowState> getStates() {
        return states;
    }

    public void setStates(List<WorkflowState> states) {
        this.states = states;
    }

    /**
     * Get a {@link List} of {@link UserRole}s used by the
     * {@like WorkflowState}s of the {@link Workflow}.
     *
     * @return {@link List} of {@link UserRole}s used by the
     *         {@like WorkflowState}s of the {@link Workflow}
     */
    public List<UserRole> getUserRolesInWorkflowStates() {
        List<UserRole> roles = new ArrayList<UserRole>();
        for (WorkflowState state : getStates()) {
            if (!roles.contains(state.getActorRole())) {
                roles.add(state.getActorRole());
            }
        }
        return roles;
    }

    /**
     * Determines if the {@link Workflow} is properly configured. A valid
     * workflow has the {@link Workflow#getStartState()}, 
     * {@link Workflow#getEndState()}, and {@link Workflow#getTrashState()} set.
     *
     * @return {@code true} is returned the {@link Workflow} is configured
     *          correctly, otherwise {@code false} is returned.
     */
    public boolean isValid() {
        if (startState == null || endState == null || trashState == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Workflow other = (Workflow) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

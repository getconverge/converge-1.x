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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * State of a news item. Belongs to a {@link Workflow} and define possible
 * {@link WorkflowStep}s that can be activated to transition to another state.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow_state")
@NamedQueries({
    @NamedQuery(name = WorkflowState.FIND_BY_WORKFLOW, query = "SELECT ws FROM WorkflowState ws WHERE ws.workflow = :workflow")
})
public class WorkflowState implements Serializable {

    /** Query for finding the {@link WorkflowState}s for a given workflow. */
    public static final String FIND_BY_WORKFLOW = "WorkflowState.findByWorkflow";

    /** Unique ID of the {@link WorkflowState}. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Name of the state. */
    @Column(name = "state_name")
    private String name = "";

    /** Description of the state. */
    @Column(name = "state_description")
    private String description = "";

    /** Order of the state when listed. */
    @Column(name = "display_order")
    private int order = 0;

    /** Name of the role of the actor when in this state. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "role")
    private UserRole actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "permision")
    private WorkflowStatePermission permission = WorkflowStatePermission.GROUP;

    /** {@link List} of options for progressing to a different state. */
    @OneToMany(mappedBy = "fromState", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("order ASC")
    private List<WorkflowStep> nextStates = new ArrayList<WorkflowStep>();

    @OneToMany(mappedBy = "workflowState", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<NewsItemFieldVisible> visibleFields = new ArrayList<NewsItemFieldVisible>();

    /** {@link Workflow} owning the state. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Column(name = "pullback_enabled")
    private boolean pullbackEnabled = false;

    @Column(name = "department_assigned")
    private boolean departmentAssigned = false;

    @Column(name = "show_in_inbox")
    private boolean showInInbox = true;


    /**
     * Creates a new instance of {@link WorkflowState}.
     */
    public WorkflowState() {
    }

    public WorkflowState(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserRole getActorRole() {
        return actorRole;
    }

    public void setActorRole(UserRole actorRole) {
        this.actorRole = actorRole;
    }

    /**
     * Gets a {@link List} of valid steps from this 
     * {@link WorkflowState}.
     * 
     * @return {@link List} of valid {@link WorkflowStep}s from
     *         this {@link WorkflowState}
     */
    public List<WorkflowStep> getNextStates() {
        return nextStates;
    }

    public void setNextStates(List<WorkflowStep> nextStates) {
        this.nextStates = nextStates;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Determine if news items in this state should be shown in the inbox.
     * 
     * @return {@code true} if news items should be shown in the inbox, 
     *         otherwise {@code false}
     */
    public boolean isShowInInbox() {
        return showInInbox;
    }

    /**
     * Sets the inbox status.
     * 
     * @param showInInbox
     *          {@code true} if news items should appear in the inbox,
     *          otherwise {@code false}
     */
    public void setShowInInbox(boolean showInInbox) {
        this.showInInbox = showInInbox;
    }
    
    /**
     * Determines if this state allows the previous user to pull back the story.
     *
     * @return {@code true} if this state allow pullback, otherwise {@code false}
     */
    public boolean isPullbackEnabled() {
        return pullbackEnabled;
    }

    /**
     * Sets the pullback indicator.
     *
     * @param pullbackEnabled
     *          {@code true} if the state should allow pullback, otherwise
     *          {@code false}
     */
    public void setPullbackEnabled(boolean pullbackEnabled) {
        this.pullbackEnabled = pullbackEnabled;
    }

    /**
     * Gets the user permission to news items with this state.
     *
     * @return User permission to news items with this state
     */
    public WorkflowStatePermission getPermission() {
        return permission;
    }

    /**
     * Sets the user permission to news items with this state.
     *
     * @param permission
     *          User permission to news items with this state
     */
    public void setPermission(WorkflowStatePermission permission) {
        this.permission = permission;
    }

    /**
     * Gets the type of workflow. Workflow type is read-only and determined by
     * the start and end state of the {@link Workflow} that the state belongs.
     *
     * @return {@link WorkflowStateType#START} if the owning {@link Workflow}
     *         has this state set as the start state,
     *         {@link WorkflowStateType#END} if the owning {@link Workflow} has
     *         this state set as the end state, otherwise
     *         {@link WorkflowStateType#MIDDLE}
     */
    public WorkflowStateType getType() {
        if (getWorkflow() == null) {
            return WorkflowStateType.MIDDLE;
        }

        if (getWorkflow().getStartState() != null && getWorkflow().getStartState().equals(this)) {
            return WorkflowStateType.START;
        }

        if (getWorkflow().getEndState() != null && getWorkflow().getEndState().equals(this)) {
            return WorkflowStateType.END;
        }

        if (getWorkflow().getTrashState() != null && getWorkflow().getTrashState().equals(this)) {
            return WorkflowStateType.TRASH;
        }

        return WorkflowStateType.MIDDLE;
    }

    public boolean isWorkflowStart() {
        return getType().equals(WorkflowStateType.START);
    }

    public boolean isWorkflowEnd() {
        return getType().equals(WorkflowStateType.END);
    }

    public boolean isWorkflowMiddle() {
        return getType().equals(WorkflowStateType.MIDDLE);
    }

    public boolean isWorkflowTrash() {
        return getType().equals(WorkflowStateType.TRASH);
    }

    public List<NewsItemFieldVisible> getVisibleFields() {
        return visibleFields;
    }

    public void setVisibleFields(List<NewsItemFieldVisible> visibleFields) {
        this.visibleFields = visibleFields;
    }

    /**
     * Gets the number of options available from this state.
     *
     * @return Number of options available
     */
    public int getNumberOfOptions() {
        return this.nextStates.size();
    }

    /**
     * Determines if the {@link WorkflowState} is for the current NewsItem user.
     *
     * @return {@code true} if the {@link WorkflowState} is for the current
     *         NewsItem user, otherwise {@code false}
     */
    public boolean isUserPermission() {
        if (getPermission() == null) {
            return false;
        } else {
            return getPermission().equals(WorkflowStatePermission.USER);
        }
    }

    /**
     * Determines if the {@link WorkflowState} is for the current NewsItem group.
     *
     * @return {@code true} if the {@link WorkflowState} is for the current
     *         NewsItem group, otherwise {@code false}
     */
    public boolean isGroupPermission() {
        if (getPermission() == null) {
            return false;
        } else {
            return getPermission().equals(WorkflowStatePermission.GROUP);
        }
    }

    public boolean isDepartmentAssigned() {
        return departmentAssigned;
    }

    public void setDepartmentAssigned(boolean departmentAssigned) {
        this.departmentAssigned = departmentAssigned;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkflowState other = (WorkflowState) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}

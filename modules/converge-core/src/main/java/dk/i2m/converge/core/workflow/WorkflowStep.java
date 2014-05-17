/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity representing a step that can make a transition from one
 * {@link WorkflowState} to another.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow_step")
@NamedQueries({
    @NamedQuery(name = WorkflowStep.FIND_BY_WORKFLOW_STATE, query = "SELECT ws FROM WorkflowStep ws WHERE ws.fromState = :workflowState")
})
public class WorkflowStep implements Serializable {

    public static final String FIND_BY_WORKFLOW_STATE = "WorkflowStep.findByWorkflowState";

    private static final long serialVersionUID = 2L;

    /** Unique ID of the next state. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Order of precedence. */
    @Column(name = "display_order")
    private Integer order = 0;

    /** Name of the option. Used for showing to the user. */
    @Column(name = "name")
    private String name = "";

    /** Description of the next step. */
    @Column(name = "description") @Lob
    private String description = "";

    /** Next possible state. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_state_id")
    private WorkflowState toState;

    /** Previous state. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_state_id")
    private WorkflowState fromState;

    @Column(name = "submitted")
    private boolean treatAsSubmitted = false;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workflow_step_user_role",
        joinColumns = {@JoinColumn(referencedColumnName = "id", name = "workflow_step_id", nullable = false)},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "user_role_id", nullable = false)})
    private List<UserRole> validFor = new ArrayList<UserRole>();

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<WorkflowStepValidator> validators = new ArrayList<WorkflowStepValidator>();

    @OneToMany(mappedBy = "workflowStep", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    @OrderBy(value = "executeOrder ASC")
    private List<WorkflowStepAction> actions = new ArrayList<WorkflowStepAction>();

    /**
     * Creates a new instance of {@link WorkflowStep}.
     */
    public WorkflowStep() {
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

    public WorkflowState getFromState() {
        return fromState;
    }

    public void setFromState(WorkflowState fromState) {
        this.fromState = fromState;
    }

    public WorkflowState getToState() {
        return toState;
    }

    public void setToState(WorkflowState toState) {
        this.toState = toState;
    }

    /**
     * If this {@link WorkflowStep} is selected, submitted will be set
     * on the {@link WorkflowStateTransition} if this method returns
     * {@code true}.
     * 
     * @return {@link true} if {@link WorkflowStateTransition#submitted}
     *         should be set, otherwise {@link false}
     */
    public boolean isTreatAsSubmitted() {
        return treatAsSubmitted;
    }

    /**
     * If this {@link WorkflowStep} is selected, submitted will be set
     * on the {@link WorkflowStateTransition} if this method returns
     * {@code true}.
     * 
     * @param treatAsSubmitted
     *         {@link true} if {@link WorkflowStateTransition#submitted}
     *         should be set, otherwise {@link false}
     */
    public void setTreatAsSubmitted(boolean treatAsSubmitted) {
        this.treatAsSubmitted = treatAsSubmitted;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<WorkflowStepAction> getActions() {
        return actions;
    }

    public void setActions(List<WorkflowStepAction> actions) {
        this.actions = actions;
    }

    public List<WorkflowStepValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<WorkflowStepValidator> validators) {
        this.validators = validators;
    }

    /**
     * Determine if the {@link WorkflowStep} is valid for
     * all {@link UserRole}s.
     * 
     * @return {@link true} if the {@link WorkflowStep} is
     *         valid for all, otherwise {@link false}
     */
    public boolean isValidForAll() {
        return validFor.isEmpty();
    }

    /**
     * Gets a {@link List} of {@link UserRole}s that can 
     * execute this {@link WorkflowStep}. Note, if the
     * {@link List} is empty, the step is valid for
     * all {@link UserRole}s.
     * 
     * @return {@link List} of {@link UserRole}s that can
     *         execute this {@link WorkflowStep}
     */
    public List<UserRole> getValidFor() {
        return validFor;
    }

    /**
     * Sets the {@link List} of {@link UserRole}s that can
     * execute this {@link WorkflowStep}.
     * 
     * @param validFor 
     *         {@link List} of {@link UserRole}s that can
     *         execute this {@link WorkflowStep}
     */
    public void setValidFor(List<UserRole> validFor) {
        this.validFor = validFor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkflowStep other = (WorkflowStep) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
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
}

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
package dk.i2m.converge.core.workflow;

import dk.i2m.converge.core.plugin.WorkflowAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * An action that should occur when the belong WorkflowStep is executed.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow_step_action")
public class WorkflowStepAction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_step")
    private WorkflowStep workflowStep;

    @Column(name = "execute_order")
    private Integer executeOrder = 1;

    @Column(name = "label")
    private String label = "";

    @Column(name = "action_class")
    private String actionClass = null;

    @OneToMany(mappedBy = "workflowStepAction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<WorkflowStepActionProperty> properties = new ArrayList<WorkflowStepActionProperty>();

    /**
     * Creates a new instance of {@link WorkflowStepAction}.
     */
    public WorkflowStepAction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(Integer executeOrder) {
        this.executeOrder = executeOrder;
    }

    public WorkflowStep getWorkflowStep() {
        return workflowStep;
    }

    public void setWorkflowStep(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public String getActionClass() {
        return actionClass;
    }

    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    /**
     * Creates an instance of the action specified in {@link WorkflowStepAction#getActionClass()}.
     *
     * @return Instance of the action
     * @throws WorkflowActionException
     *          If the action could not be instantiated
     */
    public WorkflowAction getAction() throws WorkflowActionException {
        try {
            Class c = Class.forName(getActionClass());
            WorkflowAction action = (WorkflowAction) c.newInstance();
            return action;
        } catch (ClassNotFoundException ex) {
            throw new WorkflowActionException("Could not find action: " + getActionClass(), ex);
        } catch (InstantiationException ex) {
            throw new WorkflowActionException("Could not instantiate action [" + getActionClass() + "]. Check to ensure that the action has a public contructor with no arguments", ex);
        } catch (IllegalAccessException ex) {
            throw new WorkflowActionException("Could not access action: " + getActionClass(), ex);
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<WorkflowStepActionProperty> getProperties() {
        return properties;
    }

    public Map<String, String> getPropertiesAsMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (WorkflowStepActionProperty property : properties) {
            map.put(property.getKey(), property.getValue());
        }
        return map;
    }

    public void setProperties(List<WorkflowStepActionProperty> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WorkflowStepAction)) {
            return false;
        }
        WorkflowStepAction other = (WorkflowStepAction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}

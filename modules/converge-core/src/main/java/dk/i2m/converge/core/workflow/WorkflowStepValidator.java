/*
 * Copyright (C) 2011 Interactive Media Management
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

import dk.i2m.converge.core.plugin.WorkflowValidator;
import dk.i2m.converge.core.plugin.WorkflowValidatorException;
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
 * Validator for {@link WorkflowStep}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow_step_validator")
public class WorkflowStepValidator implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_step_id")
    private WorkflowStep step;

    @Column(name = "execute_order")
    private Integer executeOrder = 1;

    @Column(name = "label")
    private String label = "";

    @Column(name = "validator_class")
    private String validatorClass = null;

    @OneToMany(mappedBy = "workflowStepValidator", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<WorkflowStepValidatorProperty> properties = new ArrayList<WorkflowStepValidatorProperty>();

    public WorkflowStepValidator() {
    }

    public Integer getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(Integer executeOrder) {
        this.executeOrder = executeOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<WorkflowStepValidatorProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<WorkflowStepValidatorProperty> properties) {
        this.properties = properties;
    }

    public WorkflowStep getStep() {
        return step;
    }

    public void setStep(WorkflowStep step) {
        this.step = step;
    }

    public String getValidatorClass() {
        return validatorClass;
    }

    public void setValidatorClass(String validatorClass) {
        this.validatorClass = validatorClass;
    }
    
    public Map<String, String> getPropertiesAsMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (WorkflowStepValidatorProperty property : properties) {
            map.put(property.getKey(), property.getValue());
        }
        return map;
    }

    /**
     * Creates an instance of the action specified in {@link WorkflowStepValidator#getValidatorClass()}.
     *
     * @return Instance of the validator
     * @throws WorkflowValidatorException
     *          If the validator could not be instantiated
     */
    public WorkflowValidator getValidator() throws WorkflowValidatorException {
        try {
            Class c = Class.forName(getValidatorClass());
            WorkflowValidator validator = (WorkflowValidator) c.newInstance();
            return validator;
        } catch (ClassNotFoundException ex) {
            throw new WorkflowValidatorException("Could not find validator: " + getValidatorClass(), ex);
        } catch (InstantiationException ex) {
            throw new WorkflowValidatorException("Could not instantiate validator [" + getValidatorClass() + "]. Check to ensure that the action has a public contructor with no arguments", ex);
        } catch (IllegalAccessException ex) {
            throw new WorkflowValidatorException("Could not access validator: " + getValidatorClass(), ex);
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
        final WorkflowStepValidator other = (WorkflowStepValidator) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + "id=" + id + "]";
    }
}

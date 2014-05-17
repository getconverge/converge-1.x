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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Property for a {@link WorkflowStepValidator}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "workflow_step_validator_property")
public class WorkflowStepValidatorProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workflow_step_validator_id")
    private WorkflowStepValidator workflowStepValidator;

    @Column(name = "property_key")
    private String key;

    @Column(name = "property_value") @Lob
    private String value;

    public WorkflowStepValidatorProperty() {
    }

    public WorkflowStepValidatorProperty(WorkflowStepValidator workflowStepValidator, String key, String value) {
        this.workflowStepValidator = workflowStepValidator;
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WorkflowStepValidator getWorkflowStepValidator() {
        return workflowStepValidator;
    }

    public void setWorkflowStepValidator(WorkflowStepValidator workflowStepValidator) {
        this.workflowStepValidator = workflowStepValidator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkflowStepValidatorProperty other = (WorkflowStepValidatorProperty) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + " / workflowStepValidator=" + workflowStepValidator + " / key=" + key + " / value=" + value + "]";
    }
}

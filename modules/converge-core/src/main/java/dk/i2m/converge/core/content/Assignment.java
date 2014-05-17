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
package dk.i2m.converge.core.content;

import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * Assignment given to a user.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "assignment")
@NamedQueries({})
public class Assignment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private UserAccount assignedBy;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "deadline")
    private Calendar deadline;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private UserAccount assignedTo;

    @Column(name = "assignment_briefing") @Lob
    private String assignmentBriefing = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssignmentType type;

    /**
     * Creates an instance of {@link Assignment}.
     */
    public Assignment() {
    }

    /**
     * Gets the unique identifier of the {@link Assignment}.
     *
     * @return Unique identifier of the {@link Assignment}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Assignment}. This method should
     * not be invoked by the developer as the unique identifier is set by
     * the underlying RDBMS.
     *
     * @param id
     *          Unique identifier if the {@link Assignment}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserAccount assignedBy) {
        this.assignedBy = assignedBy;
    }

    public UserAccount getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserAccount assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignmentBriefing() {
        return assignmentBriefing;
    }

    public void setAssignmentBriefing(String assignmentBriefing) {
        this.assignmentBriefing = assignmentBriefing;
    }

    public AssignmentType getType() {
        return type;
    }

    public void setType(AssignmentType type) {
        this.type = type;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    /**
     * Determines if the {@link Assignment} is self-assignment.
     *
     * @return {@code true} if the {@link Assignment} is self-assigned,
     *         otherwise {@coder false}.
     */
    public boolean isSelfAssignment() {
        if (assignedTo.equals(assignedBy)) {
            return true;
        } else {
            return false;
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
        final Assignment other = (Assignment) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

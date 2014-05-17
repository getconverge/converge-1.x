/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.core.views;

import java.util.Calendar;

/**
 * View for displaying current assignments.
 *
 * @author Allan Lykke Christensen
 */
public class CurrentAssignment {

    private Long id;

    private String title;

    private Integer targetWordCount;

    private Calendar deadline;

    private String briefing;

    private Calendar checkedOut;

    private String checkedOutBy;

    public CurrentAssignment(Long id, String title, Integer targetWordCount, Calendar deadline, String briefing, Calendar checkedOut, String checkedOutBy) {
        this.title = title;
        this.targetWordCount = targetWordCount;
        this.deadline = deadline;
        this.briefing = briefing;
        this.id = id;
        this.checkedOut = checkedOut;
        this.checkedOutBy = checkedOutBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBriefing() {
        return briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public Integer getTargetWordCount() {
        return targetWordCount;
    }

    public void setTargetWordCount(Integer targetWordCount) {
        this.targetWordCount = targetWordCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLocked() {
        if (getCheckedOut() == null) {
            return false;
        } else {
            return true;
        }
    }

    public Calendar getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(Calendar checkedOut) {
        this.checkedOut = checkedOut;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    public void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CurrentAssignment other = (CurrentAssignment) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

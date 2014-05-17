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
package dk.i2m.converge.core.dto;

import java.util.Calendar;
import java.util.Date;

/**
 * Data transfer object for stories in an edition.
 *
 * @author Allan Lykke Christensen
 */
public class EditionAssignmentView {

    private Long id;

    private String title;

    private String slugline;

    private Long wordCount;

    private Integer targetWordCount;

    private String actor;

    private String status;

    private String section;

    private int start;

    private int position;

    private Date deadline;

    private Date updated;

    private Date checkedOut;

    private String checkedOutBy;

    private String briefing;

    private boolean endState = false;

    private boolean trashState = false;

    private boolean inprogressState = false;

    private boolean locked = false;

    private String lockedBy;

    private Long placementId;

    public EditionAssignmentView() {
    }

    public EditionAssignmentView(Long id, Long placementId, String title, String slugline, Integer targetWordCount, Long wordCount, String actor, String status, String section, Integer start, Integer position, Calendar deadline, Calendar updated, Calendar checkedOut, String checkedOutBy, String briefing, Long currentStatusId, Long trashStatusId, Long endStatusId) {
        this.id = id;
        this.placementId = placementId;
        this.title = title;
        this.slugline = slugline;
        this.wordCount = wordCount;
        this.targetWordCount = targetWordCount;
        this.actor = actor;
        this.status = status;
        this.section = section;
        this.start = start;
        this.position = position;
        if (deadline != null) {
            this.deadline = deadline.getTime();
        }
        if (updated != null) {
            this.updated = updated.getTime();
        }
        if (checkedOut != null) {
            this.checkedOut = checkedOut.getTime();
            this.locked = true;
            this.checkedOutBy = checkedOutBy;
        } else {
            this.locked = false;
            this.checkedOutBy = "";
        }
        
        this.briefing = briefing;

        if (endStatusId == null) {
            endStatusId = -1L;
        }

        if (trashStatusId == null) {
            trashStatusId = -1L;
        }

        if (currentStatusId.longValue() == endStatusId.longValue()) {
            this.endState = true;
        } else if (currentStatusId.longValue() == trashStatusId.longValue()) {
            this.trashState = true;
        } else {
            this.inprogressState = true;
        }
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getBriefing() {
        return briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public Date getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(Date checkedOut) {
        this.checkedOut = checkedOut;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    public void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSlugline() {
        return slugline;
    }

    public void setSlugline(String slugline) {
        this.slugline = slugline;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Long getWordCount() {
        return wordCount;
    }

    public void setWordCount(Long wordCount) {
        this.wordCount = wordCount;
    }

    public boolean isEndState() {
        return endState;
    }

    public void setEndState(boolean endState) {
        this.endState = endState;
    }

    public boolean isInprogressState() {
        return inprogressState;
    }

    public void setInprogressState(boolean inprogressState) {
        this.inprogressState = inprogressState;
    }

    public boolean isTrashState() {
        return trashState;
    }

    public void setTrashState(boolean trashState) {
        this.trashState = trashState;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(Long placementId) {
        this.placementId = placementId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EditionAssignmentView other = (EditionAssignmentView) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

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
package dk.i2m.converge.core.views;

import java.util.Calendar;
import java.util.Date;

/**
 * View of the inbox.
 *
 * @author Allan Lykke Christensen
 */
public class InboxView {

    private Long id;

    private String title;

    private String slugline;

    private Long wordCount;

    private Integer targetWordCount;

    private String actor;

    private String status;

    private String outlet;

    private Date deadline;

    private Date updated;

    private Date checkedOut;

    private String checkedOutBy;

    private String briefing;

    public InboxView() {
    }

    public InboxView(Long id, String title, String slugline, Integer targetWordCount, Long wordCount, String actor, String status, String outlet, Calendar deadline, Calendar updated, Calendar checkedOut, String checkedOutBy, String briefing) {
        this.id = id;
        this.title = title;
        this.slugline = slugline;
        this.targetWordCount = targetWordCount;
        this.wordCount = wordCount;
        this.actor = actor;
        this.status = status;
        this.outlet = outlet;
        if (deadline != null) {
            this.deadline = deadline.getTime();
        }
        if (updated != null) {
            this.updated = updated.getTime();
        }
        if (checkedOut != null) {
            this.checkedOut = checkedOut.getTime();
        }
        this.checkedOutBy = checkedOutBy;
        this.briefing = briefing;
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

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getSlugline() {
        return slugline;
    }

    public void setSlugline(String slugline) {
        this.slugline = slugline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getTargetWordCount() {
        return targetWordCount;
    }

    public void setTargetWordCount(Integer targetWordCount) {
        this.targetWordCount = targetWordCount;
    }

    public boolean isLocked() {
        if (getCheckedOut() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isBriefingAvailable() {
        if (briefing == null || briefing.isEmpty()) {
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
        final InboxView other = (InboxView) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity representing a workflow transition of a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "news_item_workflow_state_transition")
public class WorkflowStateTransition implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "news_item_id")
    private NewsItem newsItem;

    @Column(name = "transition_timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Calendar timestamp;

    @Column(name = "comment") @Lob
    private String comment = "";

    @Column(name = "story_version") @Lob
    private String storyVersion = "";

    @Column(name = "headline_version") @Lob
    private String headlineVersion = "";

    @Column(name = "brief_version") @Lob
    private String briefVersion = "";

    @ManyToOne()
    @JoinColumn(name = "state_id")
    private WorkflowState state;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount user;

    @Column(name = "submitted")
    private boolean submitted;

    /**
     * Creates a new instance of {@link WorkflowStateTransition}.
     */
    public WorkflowStateTransition() {
    }

    /**
     * Creates a new instance of {@link WorkflowStateTransition}.
     *
     * @param newsItem
     *          {@link NewsItem} that has transitioned
     * @param timestamp
     *          Time when the {@link NewsItem} transitioned
     * @param state
     *          New {@link WorkflowState} of the {@link NewsItem}
     * @param user
     *          {@link UserAccount} who initiated the transition
     */
    public WorkflowStateTransition(NewsItem newsItem, Calendar timestamp,
            WorkflowState state, UserAccount user) {
        this.newsItem = newsItem;
        this.timestamp = timestamp;
        this.state = state;
        this.user = user;
    }

    /**
     * Sets the unique identifier of the transition.
     *
     * @param id
     *          Unique identifier of the transition
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the transition.
     *
     * @return Unique identifier of the transition
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the {@link NewsItem} for which the transition belongs.
     *
     * @return {@link NewsItem} for which the transition belongs
     */
    public NewsItem getNewsItem() {
        return newsItem;
    }

    /**
     * Sets the {@link NewsItem} for which the transition belongs.
     *
     * @param newsItem
     *          {@link NewsItem} for which the transition belongs
     */
    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    /**
     * Gets the new workflow state of the {@link NewsItem}.
     *
     * @return new {@link WorkflowState} of the {@link NewsItem}
     */
    public WorkflowState getState() {
        return state;
    }

    /**
     * Sets the new workflow state of the {@link NewsItem}.
     *
     * @param state
     *          New {@link WorkflowState} of the {@link NewsItem}
     */
    public void setState(WorkflowState state) {
        this.state = state;
    }

    /**
     * Gets the date and time when the transition occurred.
     *
     * @return Date and time when the transition occurred
     */
    public Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the date and time when the transition occurred.
     *
     * @param timestamp
     *          Date and time when the transition occurred
     */
    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the comment passed by the previous actor.
     *
     * @return Comment passed by the previous actor
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment passed by the previous actor.
     *
     * @param comment
     *          Comment passed by the previous actor
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * Determine if there is a comment available in the
     * {@link WorkflowStateTransition}.
     * 
     * @return {@code true} if there is a comment available,
     *         otherwise {@code false}
     */
    public boolean isCommentAvailable() {
        if (this.comment == null || this.comment.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the content of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @return Content of the {@link NewsItem} story when the transition
     *         occurred
     */
    public String getStoryVersion() {
        return storyVersion;
    }

    /**
     * Sets the content of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @param storyVersion
     *          Content of the {@link NewsItem} story when the transition
     *          occurred
     */
    public void setStoryVersion(String storyVersion) {
        this.storyVersion = storyVersion;
    }

    /**
     * Gets the brief of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @return Brief of the {@link NewsItem} story when the transition
     *         occurred
     */
    public String getBriefVersion() {
        return briefVersion;
    }

    /**
     * Sets the brief of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @param briefVersion
     *          Brief of the {@link NewsItem} story when the transition
     *          occurred
     */
    public void setBriefVersion(String briefVersion) {
        this.briefVersion = briefVersion;
    }

    /**
     * Gets the headline of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @return Headline of the {@link NewsItem} story when the transition
     *         occurred
     */
    public String getHeadlineVersion() {
        return headlineVersion;
    }

    /**
     * Sets the headline of the {@link NewsItem} story when the transition
     * occurred.
     *
     * @param headlineVersion
     *          Headline of the {@link NewsItem} story when the transition
     *          occurred
     */
    public void setHeadlineVersion(String headlineVersion) {
        this.headlineVersion = headlineVersion;
    }

    /**
     * Gets the {@link UserAccount} who initiated the transition.
     *
     * @return {@link UserAccount} who initiated the transition
     */
    public UserAccount getUser() {
        return user;
    }

    /**
     * Sets the {@link UserAccount} who initiated the transition.
     *
     * @param user
     *          {@link UserAccount} who initiated the transition
     */
    public void setUser(UserAccount user) {
        this.user = user;
    }

    /**
     * Determine if the {@link NewsItem} has been submitted.
     * 
     * @return {@code true} if the {@link NewsItem} was submitted
     *         at this transition, otherwise {@code false}
     */
    public boolean isSubmitted() {
        return this.submitted;
    }

    /**
     * Sets the submitted flag for the {@link NewsItem}.
     * 
     * @param submitted
     *          {@code true} if this state transition represents
     *          the submission of the {@link NewsItem}
     */
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WorkflowStateTransition)) {
            return false;
        }
        WorkflowStateTransition other = (WorkflowStateTransition) object;
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

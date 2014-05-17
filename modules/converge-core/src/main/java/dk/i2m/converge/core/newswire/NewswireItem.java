/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.newswire;

import dk.i2m.converge.core.content.ContentTag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.*;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Domain class representing a news item from a newswire service.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "newswire_item")
@NamedQueries({
    @NamedQuery(name = NewswireItem.FIND_BY_EXTERNAL_ID, query = "SELECT n FROM NewswireItem AS n WHERE n.externalId=:externalId"),
    @NamedQuery(name = NewswireItem.FIND_BY_DATE, query = "SELECT n FROM NewswireItem AS n WHERE n.date BETWEEN :start AND :end OR n.updated BETWEEN :start AND :end"),
    @NamedQuery(name = NewswireItem.FIND_BY_USER, query = "SELECT DISTINCT n FROM NewswireItem n JOIN n.newswireService ns WHERE :user MEMBER OF ns.subscribers"),
    @NamedQuery(name = NewswireItem.FIND_BY_SERVICE, query = "SELECT n FROM NewswireItem AS n WHERE n.newswireService=:newswireService"),
    @NamedQuery(name = NewswireItem.SEARCH, query = "SELECT DISTINCT n FROM NewswireItem n JOIN n.newswireService ns WHERE :user MEMBER OF ns.subscribers AND (n.title LIKE :keyword OR n.summary LIKE :keyword)"),
    @NamedQuery(name = NewswireItem.DELETE_BY_SERVICE, query = "DELETE FROM NewswireItem n WHERE n.newswireService = :newswireService")
})
public class NewswireItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Query for finding a newswire item by its external identifier. Parameters {@code externalId}. */
    public static final String FIND_BY_EXTERNAL_ID = "NewswireItem.findByExternalId";

    /** Query for finding newswire items for a particular date/time interval. Parameters {@code start}: start date/time, {@code end}: end date/time. */
    public static final String FIND_BY_DATE = "NewswireItem.findByDate";

    public static final String FIND_BY_USER = "NewswireItem.findByUser";

    public static final String FIND_BY_SERVICE = "NewswireItem.findByService";

    public static final String SEARCH = "NewswireItem.search";

    public static final String DELETE_BY_SERVICE = "NewswireItem.deleteByService";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "title") @Lob
    private String title = "";

    @Column(name = "summary") @Lob
    private String summary = "";

    @Column(name = "content") @Lob
    private String content = "";

    @Column(name = "author") @Lob
    private String author = "";

    @Column(name = "thumbnail_url") @Lob
    private String thumbnailUrl = "";

    @OneToMany(mappedBy = "newswireItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrivateOwned
    private List<NewswireItemAttachment> attachments = new ArrayList<NewswireItemAttachment>();

    @Column(name = "url") @Lob
    private String url;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Calendar date;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Calendar updated;

    @ManyToOne
    @JoinColumn(name = "newswire_service_id")
    private NewswireService newswireService;

    @ManyToMany
    @JoinTable(name = "newswire_item_tag",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_item_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "content_tag_id", nullable = false)})
    private List<ContentTag> tags = new ArrayList<ContentTag>();

    /**
     * Creates a new instance of {@link NewswireItem}.
     */
    public NewswireItem() {
        updated = Calendar.getInstance();
    }

    /**
     * Gets the unique identifier of the {@link NewswireItem}. The unique
     * identifier is assigned by the database.
     *
     * @return Unique identifier of the {@link NewswireItem}.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link NewswireItem}. This method is
     * provided for the JPA framework, and should not be set manually.
     *
     * @param id
     *          Unique identifier of the {@link NewswireItem}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the external identifier of the newswire item. The identifier is used
     * for identifying the newswire in the newswire service. If a newswire item
     * is created with an existing external identifier, it will be overwritten
     * in the database.
     *
     * @return External identifier of the newswire item
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Sets the external identifier of the newswire item.
     *
     * @param externalId
     *          External identifier of the newswire item
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public NewswireService getNewswireService() {
        return newswireService;
    }

    public void setNewswireService(NewswireService newswireService) {
        this.newswireService = newswireService;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Adds content to the existing {@link NewswireItem} summary.
     *
     * @param newContent
     *          Content to add
     */
    public void addSummary(String newContent) {
        this.summary += newContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Adds content to the existing {@link NewswireItem} content.
     *
     * @param newContent
     *          Content to add
     */
    public void addContent(String newContent) {
        this.content += newContent;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public boolean isThumbnailAvailable() {
        if (this.thumbnailUrl == null || this.thumbnailUrl.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public List<NewswireItemAttachment> getAttachments() {
        return attachments;
    }

    public List<NewswireItemAttachment> getImageAttachments() {
        List<NewswireItemAttachment> images = new ArrayList<NewswireItemAttachment>();
        for (NewswireItemAttachment attachment : attachments) {
            if (attachment.getContentType().startsWith("image/")) {
                images.add(attachment);
            }
        }
        return images;
    }

    public void setAttachments(List<NewswireItemAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<ContentTag> getTags() {
        return tags;
    }

    public void setTags(List<ContentTag> tags) {
        this.tags = tags;
    }

    /**
     * Determines if the item is a summary only. A summary newswire item does
     * not have attachments and its contents is not available. Instead
     * {@link NewswireItem#getUrl()} must be followed to get the contents or
     * attachments.
     *
     * @return {@code true} if the newswire item is a summary, otherwise
     *         {@code false}
     */
    public boolean isSummarised() {
        if (attachments.isEmpty() && (this.content == null || this.content.trim().isEmpty())) {
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
        final NewswireItem other = (NewswireItem) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + ", externalId=" + externalId + "]";
    }
}

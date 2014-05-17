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
package dk.i2m.converge.core.newswire;

import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * User newswire alert. With this object a user can monitor newswires
 * for interesting content.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "newswire_basket")
@NamedQueries({
    @NamedQuery(name = NewswireBasket.FIND_BY_USER, query = "SELECT b FROM NewswireBasket b WHERE b.owner.id = :uid"),
    @NamedQuery(name = NewswireBasket.FIND_BY_EMAIL_DISPATCH, query = "SELECT b FROM NewswireBasket b WHERE b.mailDelivery=true AND (b.nextDelivery < CURRENT_TIMESTAMP)")
})
public class NewswireBasket implements Serializable {

    /** Query for obtaining all newswire baskets of a particular user. */
    public static final String FIND_BY_USER = "NewswireBasket.findByUser";

    /** Query for obtaining all newswire baskets that must be dispatched via e-mail. */
    public static final String FIND_BY_EMAIL_DISPATCH = "NewswireBasket.findByEmailDispatch";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "search_term")
    @Lob
    private String searchTerm;

    @ManyToMany
    @JoinTable(name = "newswire_service_baskets",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_basket_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_service_id", nullable = false)})
    private List<NewswireService> appliesTo = new ArrayList<NewswireService>();

    @ManyToMany
    @JoinTable(name = "newswire_baskets_tags",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_basket_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "content_tag_id", nullable = false)})
    private List<ContentTag> tags = new ArrayList<ContentTag>();

    @Column(name = "any_tags")
    private boolean anyTags = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner")
    private UserAccount owner;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    @Column(name = "mail_delivery")
    private boolean mailDelivery = false;

    @Column(name = "mail_frequency")
    private int mailFrequency = 24;

    @Column(name = "first_delivery_hour")
    private int hourFirstDelivery = 9;

    @Column(name = "last_delivery")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastDelivery;

    @Column(name = "next_delivery")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date nextDelivery;

    /**
     * Creates a new instance of {@link NewswireBasket}.
     */
    public NewswireBasket() {
        this("", "", new ArrayList<NewswireService>(), null);
    }

    /*
     * Creates a new instance of {@link NewswireBasket}.
     */
    public NewswireBasket(String title, String searchTerm,
            List<NewswireService> appliesTo, UserAccount owner) {
        this.title = title;
        this.searchTerm = searchTerm;
        this.appliesTo = appliesTo;
        this.owner = owner;
        Calendar now = Calendar.getInstance();
        this.created = now.getTime();
        this.updated = now.getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Determines if the alert should apply to all
     * {@link NewswireService}s.
     * 
     * @return {@code true} if the alert should apply
     *         to all {@link NewswireService}s, otherwise
     *         {@code false}
     */
    public boolean isAppliesToAll() {
        if (appliesTo == null || appliesTo.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAnyTags() {
        return anyTags;
    }

    public void setAnyTags(boolean anyTags) {
        this.anyTags = anyTags;
    }

    /**
     * Gets the {@link NewswireService}s that this alert
     * applies to. If the {@link List} is empty, the alert
     * will apply to all {@link NewswireService}s.
     * 
     * @return {@link List} of {@link NewswireService}s that the
     *         alert should be applied to
     */
    public List<NewswireService> getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(List<NewswireService> appliesTo) {
        this.appliesTo = appliesTo;
    }

    public List<ContentTag> getTags() {
        return tags;
    }

    public void setTags(List<ContentTag> tags) {
        this.tags = tags;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date date) {
        this.created = date;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getHourFirstDelivery() {
        return hourFirstDelivery;
    }

    public void setHourFirstDelivery(int hourFirstDelivery) {
        this.hourFirstDelivery = hourFirstDelivery;
    }

    public Date getLastDelivery() {
        return lastDelivery;
    }

    public void setLastDelivery(Date lastDelivery) {
        this.lastDelivery = lastDelivery;
    }

    public boolean isMailDelivery() {
        return mailDelivery;
    }

    public void setMailDelivery(boolean mailDelivery) {
        this.mailDelivery = mailDelivery;
    }

    public int getMailFrequency() {
        return mailFrequency;
    }

    public void setMailFrequency(int mailFrequency) {
        this.mailFrequency = mailFrequency;
    }

    public Date getNextDelivery() {
        return nextDelivery;
    }

    public void setNextDelivery(Date nextDelivery) {
        this.nextDelivery = nextDelivery;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NewswireBasket other = (NewswireBasket) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "NewswireBasket{" + "id=" + id + ", title=" + title + '}';
    }

    /**
     * Gets the query to be passed to the search engine.
     * 
     * @return  Query to be passed to he search engine
     */
    public String getQuery() {
        StringBuilder query = new StringBuilder();
        query.append(getSearchTerm());

        if (!isAppliesToAll()) {
            if (query.length() > 0) {
                query.append(" && ");
            }

            query.append("(");

            boolean firstService = true;
            for (NewswireService service : getAppliesTo()) {

                if (firstService) {
                    firstService = !firstService;
                } else {
                    query.append(" || ");
                }

                query.append("provider-id:").append(service.getId());
            }

            query.append(")");
        }


        if (!getTags().isEmpty()) {
            if (query.length() > 0) {
                query.append(" && ");
            }

            query.append("(");

            boolean first = true;
            for (ContentTag tag : this.getTags()) {
                if (first) {
                    first = !first;
                } else {
                    if (isAnyTags()) {
                        query.append(" || ");
                    } else {
                        query.append(" && ");
                    }
                }
                query.append("tag:\"").append(tag.getTag().toLowerCase()).append("\"");
            }

            query.append(")");
        }

        return query.toString();
    }
}

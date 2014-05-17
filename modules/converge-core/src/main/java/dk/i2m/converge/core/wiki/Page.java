/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.core.wiki;

import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * Wiki {@link Page} for displaying help or custom content inside Converge.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "wiki_page")
@NamedQueries({
    @NamedQuery(name = Page.FIND_SUBMENU, query = "SELECT p FROM Page p WHERE p.submenu=true ORDER BY p.displayOrder DESC"),
    @NamedQuery(name = Page.FIND_BY_TITLE, query = "SELECT p FROM Page p WHERE p.title = :" + Page.PARAMETER_TITLE)
})
public class Page implements Serializable {
    
    /** Query for getting all the pages that should be displayed on the sub-menu. No parameters available. */
    public static final String FIND_SUBMENU = "Page.findSubmenu";
    
    /** Query for getting a page by its title. Use {@link Page#PARAMETER_TITLE} to specify the title. */
    public static final String FIND_BY_TITLE = "Page.findByTitle";
    
    /** Parameter used to specify the title. */
    public static final String PARAMETER_TITLE = "title";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "show_submenu")
    private boolean submenu;

    @Column(name = "submenu_style")
    private String submenuStyle;

    @Column(name = "title")
    private String title;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "page_content")
    @Lob
    private String pageContent;

    @ManyToOne
    @JoinColumn(name = "last_updater")
    private UserAccount lastUpdater;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    @Column(name = "created")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    public Page() {
        this(null, false, "", "", 1, "", null, null, null);
    }

    public Page(Long id, boolean submenu, String submenuStyle, String title,
            int displayOrder, String pageContent, UserAccount lastUpdater,
            Date updated, Date created) {
        this.id = id;
        this.submenu = submenu;
        this.submenuStyle = submenuStyle;
        this.title = title;
        this.displayOrder = displayOrder;
        this.pageContent = pageContent;
        this.lastUpdater = lastUpdater;
        this.updated = updated;
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(UserAccount lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public boolean isSubmenu() {
        return submenu;
    }

    public void setSubmenu(boolean submenu) {
        this.submenu = submenu;
    }

    public String getSubmenuStyle() {
        return submenuStyle;
    }

    public void setSubmenuStyle(String submenuStyle) {
        this.submenuStyle = submenuStyle;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Page other = (Page) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

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
package dk.i2m.converge.core.content;

import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Section;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Placement of a {@link NewsItem} in an {@link Edition}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "news_item_placement")
@NamedQueries({
    @NamedQuery(name = NewsItemPlacement.VIEW_EDITION_ASSIGNMENTS, query = 
        "SELECT DISTINCT NEW dk.i2m.converge.core.dto.EditionAssignmentView(n.id, p.id, n.title, n.slugline, n.targetWordCount, n.precalculatedWordCount, n.precalculatedCurrentActor, n.currentState.name, p.section.name, p.start, p.position, n.deadline, n.updated,n.checkedOut, cob.fullName, n.assignmentBriefing, n.currentState.id, n.outlet.workflow.trashState.id, n.outlet.workflow.endState.id) " +
        "FROM NewsItemPlacement p JOIN p.newsItem n LEFT JOIN n.checkedOutBy cob JOIN n.actors a " +
        "WHERE p.edition.id=:edition " +
        "ORDER BY p.start ASC, p.position ASC")
})
public class NewsItemPlacement implements Serializable {

    private static final long serialVersionUID = 2L;
    
    /** Query for obtaining all the assignments for a given {@link Ediiton}. */
    public static final String VIEW_EDITION_ASSIGNMENTS = "NewsItemPlacement.viewEditionAssignments";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "news_item_id")
    private NewsItem newsItem;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne()
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @Column(name = "sub_position")
    private Integer position;

    @Column(name = "start_position")
    private Integer start;

    public NewsItemPlacement() {
        this(null, null, null, null, 0, 0);
    }

    public NewsItemPlacement(NewsItem newsItem, Outlet outlet, Section section, Edition edition, Integer position, Integer start) {
        this.newsItem = newsItem;
        this.outlet = outlet;
        this.section = section;
        this.edition = edition;
        this.position = position;
        this.start = start;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NewsItemPlacement other = (NewsItemPlacement) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * String representation of the placement, containing the outlet,
     * its publication time and section.
     * 
     * @return Outlet title followed by edition publication date/time
     *         and full section name
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d. MMMM yyyy HH:mm zzz");
        StringBuilder out = new StringBuilder();
        try {
            out.append(getOutlet().getTitle());
        } catch (Exception ex) {
        }

        try {
            out.append(" - ");
            out.append(formatter.format(getEdition().getPublicationDate().getTime()));
        } catch (Exception ex) {
        }
        try {
            out.append(" - ");
            out.append(getSection().getFullName());
        } catch (Exception ex) {
        }

        return out.toString();
    }
}

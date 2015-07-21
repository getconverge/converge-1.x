/*
 * Copyright (C) 2012 Interactive Media Management
 * Copyright (C) 2015 Allan Lykke Christensen
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
package dk.i2m.converge.core.content;

import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.workflow.Edition;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Tracking state of a {@link NewsItem} in an {@link EditionAction}.
 *
 * @author Raymond Wanyoike
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "news_item_edition_state")
@NamedQueries({
    @NamedQuery(name = NewsItemEditionState.FIND_BY_EDITION_NEWSITEM_PROPERTY, query = "SELECT n FROM NewsItemEditionState AS n WHERE n.edition.id = :" + NewsItemEditionState.PARAM_EDITION_ID + " AND n.newsItem.id = :" + NewsItemEditionState.PARAM_NEWS_ITEM_ID + " AND n.property = :" + NewsItemEditionState.PARAM_PROPERTY),})
public class NewsItemEditionState implements Serializable {

    /**
     * Query for locating a {@link NewsItemEditionSate} based on the Edition,
     * NewsItem and Property name using
     * {@link NewsItemEditionState.PARAM_EDITION_ID}, {@link NewsItemEditionState.PARAM_NEWS_ITEM_ID}
     * and {@link NewsItemEditionState.PARAM_PROPERTY}.
     */
    public static final String FIND_BY_EDITION_NEWSITEM_PROPERTY = "NewsItemEditionState.findByEditionNewsItemProperty";
    /**
     * Query parameter for specifying the ID of an Edition.
     */
    public static final String PARAM_EDITION_ID = "editionId";
    /**
     * Query parameter for specifying the ID of a News Item.
     */
    public static final String PARAM_NEWS_ITEM_ID = "newsItemId";
    /**
     * Query parameter for specifying the ID of a Property.
     */
    public static final String PARAM_PROPERTY = "property";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "eid")
    private Edition edition;
    @ManyToOne
    @JoinColumn(name = "nid")
    private NewsItem newsItem;
    @Column(name = "label")
    private String label;
    @Column(name = "property")
    private String property;
    @Column(name = "value")
    private String value;
    @Column(name = "visible")
    private boolean visible;

    public NewsItemEditionState() {
    }

    public NewsItemEditionState(Edition edition, NewsItem newsItem, String label, String property, String value, boolean visible) {
        this.edition = edition;
        this.newsItem = newsItem;
        this.label = label;
        this.property = property;
        this.value = value;
        this.visible = visible;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NewsItemEditionState)) {
            return false;
        }
        NewsItemEditionState other = (NewsItemEditionState) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ id=" + id + " ]";
    }
}

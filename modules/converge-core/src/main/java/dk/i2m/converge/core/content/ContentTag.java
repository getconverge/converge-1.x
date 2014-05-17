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
package dk.i2m.converge.core.content;

import dk.i2m.converge.core.newswire.NewswireBasket;
import dk.i2m.converge.core.newswire.NewswireItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 * Meta data tag of a piece of content.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "content_tag")
@NamedQueries({
    @NamedQuery(name = ContentTag.FIND_BY_NAME, query =
    "SELECT c FROM ContentTag c WHERE c.tag = :name"),
    @NamedQuery(name = ContentTag.FIND_LIKE_NAME, query =
    "SELECT c FROM ContentTag c WHERE c.tag LIKE :name")
})
public class ContentTag implements Serializable {

    public static final String FIND_BY_NAME = "ContentTag.findByName";

    public static final String FIND_LIKE_NAME = "ContentTag.findLikeName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tag")
    private String tag;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<NewswireItem> newswireItems = new ArrayList<NewswireItem>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<NewswireBasket> baskets = new ArrayList<NewswireBasket>();

    public ContentTag() {
        this("");
    }

    public ContentTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContentTag other = (ContentTag) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.tag == null) ? (other.tag != null) : !this.tag.equals(
                other.tag)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.tag != null ? this.tag.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass() + "[id=" + id + ", tag=" + tag + "]";
    }
}

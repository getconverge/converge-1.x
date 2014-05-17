/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.content.catalogue;

import java.util.Date;

/**
 * Non-persisted model containing the details of a single usage of a
 * {@link MediaItem}.
 *
 * @author Allan Lykke Christensen
 */
public class MediaItemUsage {

    private boolean published = false;

    private Long newsItemId;

    private String title;

    private Date date;

    private String caption;

    private String outlet;

    private String section;

    private Integer start;

    private Integer position;

    public MediaItemUsage() {
        this(0L, "", null, "", "", "", 0, 0, false);
    }

    public MediaItemUsage(Long newsItemId, String title, Date date, String caption, String outlet, String section, Integer start, Integer position, boolean published) {
        this.newsItemId = newsItemId;
        this.title = title;
        this.date = date;
        this.caption = caption;
        this.outlet = outlet;
        this.section = section;
        this.start = start;
        this.position = position;
        this.published = published;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getNewsItemId() {
        return newsItemId;
    }

    public void setNewsItemId(Long newsItemId) {
        this.newsItemId = newsItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}

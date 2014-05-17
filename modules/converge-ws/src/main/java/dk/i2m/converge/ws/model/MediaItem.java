/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.ws.model;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MediaItem} representing a file attached to a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
public class MediaItem {

    private Long id;

    private String title;

    private String caption;

    private String contentType;

    private Integer priority = 0;

    private List<MediaItemRendition> renditions =
            new ArrayList<MediaItemRendition>();

    public MediaItem() {
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<MediaItemRendition> getRenditions() {
        return renditions;
    }

    public void setRenditions(List<MediaItemRendition> renditions) {
        this.renditions = renditions;
    }
}

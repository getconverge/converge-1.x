/*
 *  Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.content.ContentItemPermission;
import dk.i2m.converge.core.content.NewsItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A value object containing information about a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemHolder {

    private NewsItem newsItem;

    private List<NewsItem> versions = new ArrayList<NewsItem>();

    private ContentItemPermission permission;

    private boolean readOnly;

    private boolean checkedOut;

    private boolean pullbackAvailable;

    private Map<String, Boolean> fieldVisibility = new HashMap<String, Boolean>();

    public NewsItemHolder(NewsItem newsItem, List<NewsItem> versions, ContentItemPermission permission, boolean readOnly, boolean checkedOut, boolean pullbackAvailable, Map<String, Boolean> fieldVisibility) {
        this.newsItem = newsItem;
        this.permission = permission;
        this.readOnly = readOnly;
        this.checkedOut = checkedOut;
        this.versions = versions;
        this.pullbackAvailable = pullbackAvailable;
        this.fieldVisibility = fieldVisibility;
    }

    public NewsItem getNewsItem() {
        return this.newsItem;
    }

    public ContentItemPermission getPermission() {
        return permission;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public List<NewsItem> getVersions() {
        return versions;
    }

    public boolean isPullbackAvailable() {
        return pullbackAvailable;
    }

    public Map<String, Boolean> getFieldVisibility() {
        return fieldVisibility;
    }

    public void setFieldVisibility(Map<String, Boolean> fieldVisibility) {
        this.fieldVisibility = fieldVisibility;
    }
}

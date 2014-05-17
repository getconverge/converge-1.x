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
package dk.i2m.converge.jsf.components.tags;

import dk.i2m.converge.core.content.AssignmentType;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.content.Assignment;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.NewsItem;
import java.util.Calendar;

/**
 * Information holder for creating a self-assignment.
 *
 * @author Allan Lykke Christensen
 */
public class DialogSelfAssignment {

    private String title = "";

    private Assignment assignment = new Assignment();

    private NewsItem newsItem = new NewsItem();

    private MediaItem mediaItem = new MediaItem();

    private boolean nextEdition = false;

    public DialogSelfAssignment() {
    }

    public DialogSelfAssignment(String title, Outlet outlet, Catalogue mediaRepository, Calendar deadline, AssignmentType type) {
        this.title = title;
        this.newsItem.setOutlet(outlet);
        this.mediaItem.setCatalogue(mediaRepository);
        this.assignment.setDeadline(deadline);
        this.assignment.setType(type);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    public boolean isNextEdition() {
        return nextEdition;
    }

    public void setNextEdition(boolean nextEdition) {
        this.nextEdition = nextEdition;
    }
}

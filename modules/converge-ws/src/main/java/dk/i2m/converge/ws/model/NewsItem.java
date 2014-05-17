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
package dk.i2m.converge.ws.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Web service model containing a simplified version of a
 * news item.
 *
 * @author Allan Lykke Christensen
 */
@XmlRootElement
public class NewsItem implements Serializable {

    private Long id;

    private String title;

    private String brief;

    private String story;

    private String byLine;

    private String dateLine;

    private int start;

    private int displayOrder;

    private Section section;

    private List<MediaItem> media;

    private List<NewsItemActor> actors;

    private List<WorkflowOption> workflowOptions;

    /**
     * Creates a new instance of {@link NewsItem}.
     */
    public NewsItem() {
        this.id = 0L;
        this.title = "";
        this.brief = "";
        this.byLine = "";
        this.dateLine = "";
        this.start = 0;
        this.displayOrder = 0;
        this.media = new ArrayList<MediaItem>();
        this.actors = new ArrayList<NewsItemActor>();
        this.workflowOptions = new ArrayList<WorkflowOption>();
    }

    /**
     * Gets the unique identifier of the {@link NewsItme}.
     * 
     * @return Unique identifier of the {@link NewsItem}
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getByLine() {
        return byLine;
    }

    public void setByLine(String byLine) {
        this.byLine = byLine;
    }

    public String getDateLine() {
        return dateLine;
    }

    public void setDateLine(String dateLine) {
        this.dateLine = dateLine;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<MediaItem> getMedia() {
        return media;
    }

    public void setMedia(List<MediaItem> media) {
        this.media = media;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Gets a {@link List} of actors attached to this {@link NewsItem}.
     * 
     * @return {@link List} of actors attached to this {@link NewsItem}
     */
    public List<NewsItemActor> getActors() {
        return actors;
    }

    /**
     * Sets the {@link List} of actors attached to this {@link NewsItem}.
     * 
     * @param actors
     *          {@link List} of actors attached to this {@link NewsItem}
     */
    public void setActors(List<NewsItemActor> actors) {
        this.actors = actors;
    }

    /**
     * Gets the {@link List} of available {@link WorkflowOption}s for
     * the {@link NewsItem}.
     * 
     * @return {@link List} of available {@link WorkflowOption}s
     */
    public List<WorkflowOption> getWorkflowOptions() {
        return workflowOptions;
    }

    /**
     * Sets the {@link List} of available {@link WorkflowOption}s for
     * the {@link NewsItem}.
     * 
     * @param workflowOptions 
     *          {@link List} of {@link WorkflowOption}s
     */
    public void setWorkflowOptions(List<WorkflowOption> workflowOptions) {
        this.workflowOptions = workflowOptions;
    }
}

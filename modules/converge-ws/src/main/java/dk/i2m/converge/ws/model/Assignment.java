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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Assignment representing a {@link dk.i2m.converge.core.content.NewsItem} 
 * combined with its {@link dk.i2m.converge.core.content.NewsItemPlacement}.
 *
 * @author Allan Lykke Christensen
 */
@XmlRootElement
public class Assignment {

    private Long id;

    private String title;
    
    private String byline;

    private Date received;

    private String outlet;

    private String section;

    private int page;

    private int priority;

    private String brief;

    private String story;

    private List<NewsItemActor> actors = new ArrayList<NewsItemActor>();

    private List<WorkflowOption> options = new ArrayList<WorkflowOption>();

    private String xmlUrl;

    private String viewUrl;

    public Assignment() {
    }

    public Assignment(Long id, String title, Date received, String outlet, String section, int page, int priority, String brief, String story, String xmlUrl, String viewUrl) {
        this.id = id;
        this.title = title;
        this.received = received;
        this.outlet = outlet;
        this.section = section;
        this.page = page;
        this.priority = priority;
        this.brief = brief;
        this.story = story;
        this.xmlUrl = xmlUrl;
        this.viewUrl = viewUrl;
    }

    /**
     * Gets the unique identifier of the {@link dk.i2m.converge.core.content.NewsItem}.
     * 
     * @return Unique identifier of the {@link dk.i2m.converge.core.content.NewsItem}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link dk.i2m.converge.core.content.NewsItem}.
     * 
     * @param id
     *          Unique identifier of the {@link dk.i2m.converge.core.content.NewsItem}
     */
    public void setId(Long id) {
        this.id = id;
    }

    public List<NewsItemActor> getActors() {
        return actors;
    }

    public void setActors(List<NewsItemActor> actors) {
        this.actors = actors;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public List<WorkflowOption> getOptions() {
        return options;
    }

    public void setOptions(List<WorkflowOption> options) {
        this.options = options;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
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

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }
}

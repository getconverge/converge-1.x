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
package dk.i2m.converge.domain.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A single result from the search engine.
 *
 * @author Allan Lykke Christensen
 */
public class SearchResult {

    private Long id;

    private List<Date> dates = new ArrayList<Date>();

    private String title;

    private String description;

    private String note;

    private String link;

    private String directLink;

    private String type;

    private String repository;

    private String[] tags;

    private String people;

    private boolean preview = false;

    private String previewLink;

    private String format = "";

    private float score = 0;

    private String previewContentType = "";

    /**
     * Creates a new instance of {@link SearchResult}.
     */
    public SearchResult() {
        this(0L, "", "", "", "", "", "", new String[]{}, "", "", "", "");
    }

    /**
     * Creates a new instance of {@link SearchResult}.
     * <p/>
     * @param id          Unique identifier of the content
     * @param title       Title of the search result
     * @param description Description of the search result
     * @param note        Note of the search result
     * @param link        Link to the content
     * @param type        Type of content
     * @param repository  Media repository
     * @param tags        Tags of the search result
     * @param people      People of the search result
     * @param previewLink Preview link
     */
    public SearchResult(Long id, String title, String description, String note,
            String link, String type, String repository, String[] tags,
            String people, String previewLink, String directLink, String format) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.note = note;
        this.link = link;
        this.type = type;
        this.repository = repository;
        this.tags = tags;
        this.people = people;
        this.previewLink = previewLink;
        this.directLink = directLink;
        this.format = format;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public void addDate(Date date) {
        this.dates.add(date);
    }

    public Date getEarliestDate() {
        Date earliest = null;
        for (Date date : dates) {
            if (earliest == null) {
                earliest = date;
            } else {
                if (date.before(earliest)) {
                    earliest = date;
                }
            }
        }
        return earliest;
    }

    public Date getLatestDate() {
        Date latest = null;
        for (Date date : dates) {
            if (latest == null) {
                latest = date;
            } else {
                if (date.after(latest)) {
                    latest = date;
                }
            }
        }
        return latest;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets a note of the search result. The note contains information
     * about which outlet and section the item was published.
     *
     * @return Note of the search result
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets a note for the search result.
     *
     * @param note
* Note of the search result
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Determines if there is a graphical preview of the result (such as an
     * image).
     *
     * @return {@code true} if there is an image preview, otherwise {@code false}
     */
    public boolean isPreview() {
        return preview;
    }

    /**
     * Sets the preview availability indicator.
     *
     * @param preview
* {@code true} if there is an image preview, otherwise
     *          {@code false}
     */
    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    /**
     * Gets the link to the preview.
     *
     * @return Hyperlink to the preview
     */
    public String getPreviewLink() {
        return previewLink;
    }

    /**
     * Sets the hyperlink to the preview.
     *
     * @param previewLink Hyperlink to the preview
     */
    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getDirectLink() {
        return directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;

    }

    public String getPreviewContentType() {
        return previewContentType;
    }

    public void setPreviewContentType(String previewContentType) {
        this.previewContentType = previewContentType;
    }

    public boolean isPreviewVideo() {
        if (previewContentType.startsWith("video/")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPreviewAudio() {
        if (previewContentType.startsWith("audio/")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVideo() {

        if ("Video".equalsIgnoreCase(this.format)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAudio() {
        if ("Audio".equalsIgnoreCase(this.format)) {
            return true;
        } else {
            return false;
        }
    }
}

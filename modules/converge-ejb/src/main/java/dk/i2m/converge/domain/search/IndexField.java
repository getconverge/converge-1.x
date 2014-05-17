/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.domain.search;

/**
 * Fields used by {@link Indexer}s to index
 * {@link org.apache.lucene.document.Document}s.
 *
 * @author Allan Lykke Christensen
 */
public enum IndexField {

    ID("id"),
    TITLE("title"),
    TYPE("type"),
    BYLINE("byline"),
    BRIEF("brief"),
    STORY("story"),
    LANG("lang"),
    LANGUAGE("language"),
    DATE("date"),
    EDITION_NUMBER("edition-number"),
    EDITION_VOLUME("edition-volume"),
    SECTION("section"),
    OUTLET("outlet"),
    WORD_COUNT("wordcount"),
    PLACEMENT("placement"),
    ACTOR("actor"),
    CONCEPT("concept"),
    SUBJECT("subject"),
    ORGANISATION("organisation"),
    PERSON("person"),
    POINT_OF_INTEREST("poi"),
    LOCATION("location"),
    CAPTION("caption"),
    CONTENT_TYPE("content-type"),
    REPOSITORY("repository"),
    THUMB_URL("thumb-url"),
    DIRECT_URL("direct-url"),
    MEDIA_FORMAT("media-format");

    private String name;

    IndexField(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

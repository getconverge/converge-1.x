/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
package com.getconverge.plugins.atompub;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Edition;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.w3c.tidy.Tidy;

/**
 * Type converter from {@link NewsItemPlacement} to Atom {@link Entry}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemPlacementToAtomConverter {

    private static final String ENCODING = "UTF-8";
    private final Abdera atompub;

    /**
     * Creates a new instance of the {@link NewsItemPlacementToAtomConverter}.
     *
     * @param atompub Intantiated {@link Abdera} API used for creating the Atom
     * {@link Entry}
     */
    public NewsItemPlacementToAtomConverter(Abdera atompub) {
        this.atompub = atompub;
    }

    /**
     * Converts a {@link NewsItemPlacement} to an Atom {@link Entry}.
     *
     * @param placement {@link NewsItemPlacement} to convert
     * @return Atom {@link Entry} representing the {@code placement}
     */
    public Entry convert(NewsItemPlacement placement) {
        NewsItem newsItem = placement.getNewsItem();
        Edition edition = placement.getEdition();
        Entry entry = atompub.newEntry();
        entry.setId("" + newsItem.getId());
        entry.setTitle(newsItem.getTitle());
        String story = cleanupHtml(newsItem.getStory());
        entry.setContentAsXhtml(story);
        entry.setPublished(edition.getPublicationDate().getTime());
        entry.setUpdated(newsItem.getUpdated().getTime());

        return entry;
    }

    private String cleanupHtml(String story) {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(ENCODING);
        tidy.setOutputEncoding(ENCODING);
        tidy.setPrintBodyOnly(true);
        tidy.setXmlOut(true);
        tidy.setSmartIndent(false);
        tidy.setBreakBeforeBR(false);
        tidy.setMakeBare(true);
        tidy.setMakeClean(true);
        tidy.setNumEntities(true);
        tidy.setWraplen(0);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(story);
        tidy.parse(reader, writer);
        return writer.toString();
    }

}

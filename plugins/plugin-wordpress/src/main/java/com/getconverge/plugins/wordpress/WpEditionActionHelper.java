/*
 * Copyright (C) 2015 Allan Lykke Christensen
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
package com.getconverge.plugins.wordpress;

import com.getconverge.plugins.wordpress.client.FileField;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.workflow.Edition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Helper class for the {@link WpEditionAction} plug-in.
 *
 * @author Allan Lykke Christensen
 */
public class WpEditionActionHelper {

    public static String[] getConcepts(NewsItem newsItem) {
        String[] out = new String[newsItem.getConcepts().size()];
        int index = 0;
        for (Concept c : newsItem.getConcepts()) {
            out[index++] = c.getName();
        }
        return out;
    }

    public static String[] getCategories(Edition edition, NewsItem newsItem) {
        List<String> sections = new ArrayList<String>();
        for (NewsItemPlacement nip : edition.getPlacements()) {
            if (nip.getNewsItem().equals(newsItem)) {
                sections.add(nip.getSection().getName());
            }
        }
        return sections.toArray(new String[sections.size()]);
    }

    public static String getImageHtml(Map<String, Object> uploadedFile, NewsItemMediaAttachment attachment) {
        String id = (String) uploadedFile.get(FileField.ID.toString());
        String url = (String) uploadedFile.get(FileField.URL.toString());
        StringBuilder html = new StringBuilder();
        html.append("<img src=\"")
                .append(url)
                .append("\" alt=\"")
                .append(StringEscapeUtils.escapeHtml(attachment.getCaption()))
                .append("\" class=\"size-full wp-image-")
                .append(id)
                .append("\">");

        return html.toString();
    }

}

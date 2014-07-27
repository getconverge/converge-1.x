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
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Section;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import junit.framework.Assert;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.ParseException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for {@link AtomPubEditionAction}.
 *
 * @author Allan Lykke Christensen
 */
public class AtomPubEditionActionTest {

    @Test
    public void atomPubEditionAction_instance_supportsEditionExecution() {
        // Arrange
        AtomPubEditionAction plugin = new AtomPubEditionAction();
        boolean expResult = true;

        // Act
        boolean supportEdition = plugin.isSupportEditionExecute();

        // Assert
        assertEquals(expResult, supportEdition);
    }

    @Test
    public void atomPubEditionAction_instance_supportsPlacementExecution() {
        // Arrange
        AtomPubEditionAction plugin = new AtomPubEditionAction();
        boolean expResult = true;

        // Act
        boolean supportPlacement = plugin.isSupportPlacementExecute();

        // Assert
        assertEquals(expResult, supportPlacement);
    }

    @Test
    public void atomPubEditionAction_instance_metadataAvailable() {
        // Arrange
        AtomPubEditionAction plugin = new AtomPubEditionAction();

        // Act
        String name = plugin.getName();
        String about = plugin.getAbout();
        ResourceBundle bundle = plugin.getBundle();
        Date date = plugin.getDate();
        String description = plugin.getDescription();
        String vendor = plugin.getVendor();

        // Assert
        assertNotNull(name);
        assertNotNull(about);
        assertNotNull(bundle);
        assertNotNull(date);
        assertNotNull(description);
        assertNotNull(vendor);
    }

    @Test
    public void atomPubEditionAction_instance_propertiesAvailable() {
        // Arrange
        AtomPubEditionAction plugin = new AtomPubEditionAction();

        // Act
        Map<String, String> availableProperties = plugin.getAvailableProperties();

        // Assert
        assertEquals(1, availableProperties.size());
    }

    @Test
    public void atomPubEditionAction_instance_serviceUrlPropertyAvailable() {
        // Arrange
        AtomPubEditionAction plugin = new AtomPubEditionAction();

        // Act
        boolean serviceUrlpropertyExist = plugin.getAvailableProperties().containsValue(AtomPubEditionAction.Property.SERVICE_URL.name());

        // Assert
        assertTrue(serviceUrlpropertyExist);
    }

    @Test
    public void newsItemPlacementToAtomConverter_newsItemWithHtmlInContent_validAtomEntry() {
        // Arrange
        String id = "206413";
        String byLine = "London";
        String title = "Ramires on the way to Chelsea inSh2.26bn switch";
        String story = "<br /><br /><br />Chelsea target Ramires will undergo a medical on Tuesday ahead of completing an &pound;18million (Sh2.26bn) move from Benfica to Stamford Bridge. It was revealed last month that manager Carlo Ancelotti wanted to sign the Brazil midfielder following his impressive performances at the World Cup and in Benfica&rsquo;s title-winning campaign. Ramires is expected to sign a four-year deal having spent one season in Portugal following a move from Cruzeiro in Brazil. <br />Chelsea will then turn their attention to capturing his team-mate David Luiz, rated at &pound;27m (Sh3.40bn), and who played in Benfica&rsquo;s friendly against Tottenham on Tuesday. Chelsea sources have expressed concerns that Ramires may fail to obtain a work permit at the first time of asking as he has not played the required 75 per cent of games for Brazil in the past two years. However, Chelsea are confident that the 23-year-old&rsquo;s emergence as one of the most sought-after midfielders in Europe will see him granted a permit on appeal thanks to the &ldquo;special circumstances&rdquo; clause.";
        String sectionName = "Sports";
        DateTime publicationDate = new DateTime(2014, 7, 19, 10, 15, DateTimeZone.UTC);
        DateTime updatedDate = new DateTime(2014, 7, 20, 13, 23, DateTimeZone.UTC);
        String formattedPublicationDate = ISODateTimeFormat.dateTime().print(publicationDate);
        String formattedUpdatedDate = ISODateTimeFormat.dateTime().print(updatedDate);
        NewsItemPlacementToAtomConverter converter = new NewsItemPlacementToAtomConverter(new Abdera());
        Outlet outlet = new Outlet();
        NewsItem newsItem = new NewsItem();
        newsItem.setId(Long.valueOf(id));
        newsItem.setByLine(byLine);
        newsItem.setTitle(title);
        newsItem.setStory(story);
        newsItem.setUpdated(updatedDate.toGregorianCalendar());
        Section section = new Section();
        section.setName(sectionName);
        section.setOutlet(outlet);
        Edition edition = new Edition();
        edition.setPublicationDate(publicationDate.toGregorianCalendar());
        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setOutlet(outlet);
        placement.setPosition(1);
        placement.setStart(1);
        placement.setSection(section);
        placement.setEdition(edition);
        placement.setNewsItem(newsItem);
        edition.getPlacements().add(placement);
        String expected = "<entry xmlns=\"http://www.w3.org/2005/Atom\"><id>" + id + "</id><title type=\"text\">" + newsItem.getTitle() + "</title>"
                + "<content type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\"><br/>\n<br/>\n<br/>\n"
                + "Chelsea target Ramires will undergo a medical on Tuesday ahead of completing an £18million (Sh2.26bn) move from Benfica to Stamford Bridge. It was revealed last month that manager Carlo Ancelotti wanted to sign the Brazil midfielder following his impressive performances at the World Cup and in Benfica's title-winning campaign. Ramires is expected to sign a four-year deal having spent one season in Portugal following a move from Cruzeiro in Brazil. \n"
                + "<br/>\n"
                + "Chelsea will then turn their attention to capturing his team-mate David Luiz, rated at £27m (Sh3.40bn), and who played in Benfica's friendly against Tottenham on Tuesday. Chelsea sources have expressed concerns that Ramires may fail to obtain a work permit at the first time of asking as he has not played the required 75 per cent of games for Brazil in the past two years. However, Chelsea are confident that the 23-year-old's emergence as one of the most sought-after midfielders in Europe will see him granted a permit on appeal thanks to the \"special circumstances\" clause.\n"
                + "</div></content>"
                + "<published>" + formattedPublicationDate + "</published>"
                + "<updated>" + formattedUpdatedDate + "</updated></entry>";

        // Act
        Entry atomEntry = converter.convert(placement);

        // Assert
        Assert.assertEquals(expected, atomEntry.toString());
    }
}

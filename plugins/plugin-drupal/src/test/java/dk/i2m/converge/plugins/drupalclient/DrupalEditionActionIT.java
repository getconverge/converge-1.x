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
package dk.i2m.converge.plugins.drupalclient;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.Section;
import java.util.Calendar;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Integration tests for {@link DrupalEditionAction} and
 * {@code http://int.drupal.getconverge.com}.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalEditionActionIT {

    @Test
    public void drupalEditionAction_unspecified() throws Exception {
        // Arrange
        DrupalEditionAction plugin = new DrupalEditionAction();

        // Act
        NewsItemPlacement newsItemPlacement = getNewsItemPlacement();
        newsItemPlacement.setEdition(getEdition());
        plugin.executePlacement(getPluginContext(), newsItemPlacement, newsItemPlacement.getEdition(), getAction());

        // Assert
    }

    public PluginContext getPluginContext() {
        PluginContext ctx = mock(PluginContext.class);
        when(ctx.addNewsItemEditionState(anyLong(), anyLong(), anyString(), anyString())).thenReturn(new NewsItemEditionState());
        return ctx;
    }

    private Edition getEdition() {
        Calendar publicationDate = Calendar.getInstance();
        Calendar expirationDate = (Calendar) publicationDate.clone();
        expirationDate.add(Calendar.DAY_OF_MONTH, 1);
        Edition edition = new Edition();
        edition.setId(123L);
        edition.setCloseDate(publicationDate.getTime());
        edition.setPublicationDate(publicationDate);
        edition.setExpirationDate(expirationDate);
        edition.setOpen(true);
        edition.getPlacements().add(new NewsItemPlacement());

        return edition;
    }

    private NewsItemPlacement getNewsItemPlacement() {
        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setId(1003L);
        placement.setStart(1);
        placement.setPosition(1);
        placement.setSection(getSectionSports());
        placement.setNewsItem(getNewsItem1());
        placement.setOutlet(getOutlet());
        return placement;
    }

    private OutletEditionAction getAction() {
        OutletEditionAction action = new OutletEditionAction();
        action.setId(1L);
        action.setLabel("Upload to Drupal Site");
        action.setActionClass(DrupalEditionAction.class.getName());
        action.setManualAction(true);
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.URL.name(), "http://int.drupal.getconverge.com"));
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.SERVICE_ENDPOINT.name(), "converge"));
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.USERNAME.name(), "converge"));
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.PASSWORD.name(), "c0nv3rg3"));
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.SECTION_MAPPING.name(), "1004:1"));
        action.getProperties().add(new OutletEditionActionProperty(action, DrupalEditionAction.Property.NODE_TYPE.name(), "newsitem"));
        return action;
    }

    public Section getSectionSports() {
        Section section = new Section();
        section.setId(1004L);
        section.setName("Sports");
        section.setActive(true);
        return section;
    }

    public NewsItem getNewsItem1() {
        NewsItem newsItem = mock(NewsItem.class);
        when(newsItem.getId()).thenReturn(1001L);
        when(newsItem.getTitle()).thenReturn("Sample sports story");
        when(newsItem.getStory()).thenReturn("This is a sample story");
        when(newsItem.getByLine()).thenReturn("By Reporter");
        when(newsItem.isEndState()).thenReturn(true);
        return newsItem;
    }

    private Outlet getOutlet() {
        Outlet outlet = new Outlet();
        outlet.setId(1002L);
        outlet.setTitle("My Outlet");
        return outlet;
    }
}

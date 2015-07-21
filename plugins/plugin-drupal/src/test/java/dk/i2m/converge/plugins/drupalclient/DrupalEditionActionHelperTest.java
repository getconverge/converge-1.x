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
package dk.i2m.converge.plugins.drupalclient;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.workflow.Edition;
import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link DrupalEditionActionHelper}.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalEditionActionHelperTest {

    @Test
    public void drupalEditionActionHelper_uploadedNewsItem_isDrupalNodeOutdatedIsFalse() {
        // Arrange        
        NewsItem newsItem = getUploadedNewsItem();
        NewsItemEditionState state = getNewsItemEditionState();
        state.setNewsItem(newsItem);

        // Act
        boolean isDrupalNodeOutdated = DrupalEditionActionHelper.isDrupalNodeOutdated(newsItem, state);

        // Assert
        assertFalse(isDrupalNodeOutdated);
    }

    @Test
    public void drupalEditionActionHelper_updatedNewsItem_isDrupalNodeOutdatedIsTrue() {
        // Arrange        
        NewsItem newsItem = getUpdatedNewsItem();
        NewsItemEditionState state = getNewsItemEditionState();
        state.setNewsItem(newsItem);

        // Act
        boolean isDrupalNodeOutdated = DrupalEditionActionHelper.isDrupalNodeOutdated(newsItem, state);

        // Assert
        assertTrue(isDrupalNodeOutdated);
    }

    private final String UPLOADED_LAST_UPDATED = "1373522590000";
    private final String UPDATED_LAST_UPDATED = "1473522590000";

    private NewsItem getUploadedNewsItem() {
        NewsItem newsItem = new NewsItem();
        Calendar updated = Calendar.getInstance();
        updated.setTimeInMillis(Long.valueOf(UPLOADED_LAST_UPDATED));
        newsItem.setUpdated(updated);
        return newsItem;
    }

    private NewsItem getUpdatedNewsItem() {
        NewsItem newsItem = new NewsItem();
        Calendar updated = Calendar.getInstance();
        updated.setTimeInMillis(Long.valueOf(UPDATED_LAST_UPDATED));
        newsItem.setUpdated(updated);
        return newsItem;
    }

    private NewsItemEditionState getNewsItemEditionState() {
        NewsItemEditionState state = new NewsItemEditionState();
        state.setEdition(new Edition());
        state.setId(1L);
        state.setProperty(DrupalEditionAction.STATE_LAST_UPDATED);
        state.setValue(UPLOADED_LAST_UPDATED);

        return state;
    }
}

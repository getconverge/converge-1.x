/*
 * Copyright (C) 2015 Raymond Wanyoike
 *
 * This file is part of Converge.
 *
 * Converge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Converge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Converge. If not, see <http://www.gnu.org/licenses/>.
 */

package dk.i2m.converge.plugins.drupal;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.plugins.drupal.wrappers.DateWrapper;
import dk.i2m.converge.plugins.drupal.wrappers.FieldWrapper;
import dk.i2m.converge.plugins.drupal.wrappers.VocabWrapper;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DrupalUtils}.
 */
public class DrupalUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void checkProperties_noException() throws Exception {
        OutletEditionAction action = new OutletEditionAction();
        action.getProperties().add(new OutletEditionActionProperty(action, Property.SERVICE_ENDPOINT.name(), "X"));
        action.getProperties().add(new OutletEditionActionProperty(action, Property.USERNAME.name(), "X"));
        action.getProperties().add(new OutletEditionActionProperty(action, Property.PASSWORD.name(), "X"));
        action.getProperties().add(new OutletEditionActionProperty(action, Property.NODE_TYPE.name(), "X"));

        List<String> names = new ArrayList<String>();
        names.add(Property.SERVICE_ENDPOINT.name());
        names.add(Property.USERNAME.name());
        names.add(Property.PASSWORD.name());
        names.add(Property.NODE_TYPE.name());

        DrupalUtils.checkProperties(action.getPropertiesAsMap(), names);
    }

    @Test
    public void checkProperties_nullProperties_throwException() throws Exception {
        OutletEditionAction action = new OutletEditionAction();

        List<String> names = new ArrayList<String>();
        names.add(Property.MAPPING_FIELD.name());
        names.add(Property.MAPPING_SECTION.name());
        names.add(Property.STATE_UPLOAD.name());
        names.add(Property.STATE_UPLOADED.name());
        names.add(Property.STATE_FAILED.name());

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Property.MAPPING_FIELD.name());
        thrown.expectMessage(Property.MAPPING_SECTION.name());
        thrown.expectMessage(Property.STATE_UPLOAD.name());
        thrown.expectMessage(Property.STATE_UPLOADED.name());
        thrown.expectMessage(Property.STATE_FAILED.name());

        DrupalUtils.checkProperties(action.getPropertiesAsMap(), names);
    }

    @Test
    public void convertStringArrayA_returnArray() throws Exception {
        // Inner spaces are not stripped
        String[] expected = {"THIS_FIELD : field_this ", " THAT_FIELD : field_that"};

        // Include arbitrary spaces
        String string = " THIS_FIELD : field_this , THAT_FIELD : field_that ";
        String[] actual = DrupalUtils.convertStringArrayA(string);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void convertStringArrayB_returnArray() throws Exception {
        // Inner spaces are not stripped
        String[] expected = {"THIS_FIELD ", " field_this"};

        // Include arbitrary spaces
        String string = " THIS_FIELD : field_this ";
        String[] actual = DrupalUtils.convertStringArrayB(string);

        assertArrayEquals(expected, actual);
    }


    @Test
    public void convertStringMap_returnMap() throws Exception {
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("THIS_FIELD", "field_this");
        expected.put("THAT_FIELD", "field_that");
        expected.put("BOTH_FIELD", "field_both");

        // Include arbitrary spaces
        String string = " THIS_FIELD : field_this , THAT_FIELD : field_that , BOTH_FIELD : field_both ";
        Map<String, String> actual = DrupalUtils.convertStringMap(string);

        assertEquals(expected, actual);
    }

    @Test
    public void convertStringMap_badString_returnNull() throws Exception {
        // Badly formatted schema
        String string = " THIS_FIELD , THAT_FIELD : field_that , field_both ";
        Map<String, String> map = DrupalUtils.convertStringMap(string);

        assertNull(map);
    }

    @Test
    public void getKeyValue_returnValue() throws Exception {
        String string = "THIS_FIELD:field_this, THAT_FIELD:field_that";
        String[] array = DrupalUtils.convertStringArrayA(string);
        String actual = DrupalUtils.getKeyValue(array, "THAT_FIELD");

        assertEquals("field_that", actual);
    }

    @Test
    public void getKeyValue_missingKey_returnNull() throws Exception {
        String string = "THIS_FIELD:field_this, THAT_FIELD:field_that";
        String[] mapping = DrupalUtils.convertStringArrayA(string);
        String keyValue = DrupalUtils.getKeyValue(mapping, "BOTH_FIELD");

        assertNull(keyValue);
    }

    @Test
    public void getKeyValue_badKey_returnNull() throws Exception {
        // Badly formatted schema
        String string = " THIS_FIELD , THAT_FIELD : field_that , field_both ";
        String[] mapping = DrupalUtils.convertStringArrayA(string);
        String keyValue = DrupalUtils.getKeyValue(mapping, "BOTH_FIELD");

        assertNull(keyValue);
    }


    @Test
    public void nodeParams_returnMap() throws Exception {
        Calendar calendar = new GregorianCalendar(2010, Calendar.JULY, 3);
        SimpleDateFormat sdf = new SimpleDateFormat(DrupalUtils.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        String title = "Test Sports Story";
        String nodeType = "article";
        String body = "<p>This is a test sports story</p>";
        String date = sdf.format(calendar.getTime());
        String byline = "By Test";
        Long sectionId = 12L;
        Long newsItemId = 21L;
        Long editionId = 31L;
        Integer start = 2;
        Integer position = 4;

        String fieldTitle = "title";
        String fieldType = "type";
        String fieldBody = "field_body";
        String fieldDate = "field_date";
        String fieldByline = "field_byline";
        String fieldSection = "field_section_id";
        String fieldNewsItemId = "field_newsitem_id";
        String fieldEditionId = "field_edition_id";
        String fieldStart = "field_start";
        String fieldPosition = "field_position";

        String fieldMapping = String.format("%s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s",
                DrupalUtils.KEY_BODY, fieldBody,
                DrupalUtils.KEY_DATE, fieldDate,
                DrupalUtils.KEY_BYLINE, fieldByline,
                DrupalUtils.KEY_SECTION, fieldSection,
                DrupalUtils.KEY_NEWSITEM_ID, fieldNewsItemId,
                DrupalUtils.KEY_EDITION_ID, fieldEditionId,
                DrupalUtils.KEY_START, fieldStart,
                DrupalUtils.KEY_POSITION, fieldPosition);

        Edition edition = mock(Edition.class);
        when(edition.getId()).thenReturn(editionId);
        when(edition.getPublicationDate()).thenReturn(calendar);

        Section section = mock(Section.class);
        when(section.getId()).thenReturn(sectionId);

        NewsItem newsItem = mock(NewsItem.class);
        when(newsItem.getId()).thenReturn(newsItemId);
        when(newsItem.getTitle()).thenReturn(title);
        when(newsItem.getStory()).thenReturn(body);
        when(newsItem.getAuthors()).thenReturn(byline);

        NewsItemPlacement placement = mock(NewsItemPlacement.class);
        when(placement.getStart()).thenReturn(start);
        when(placement.getPosition()).thenReturn(position);
        when(placement.getEdition()).thenReturn(edition);
        when(placement.getSection()).thenReturn(section);
        when(placement.getNewsItem()).thenReturn(newsItem);

        FieldWrapper fieldWrapper = new FieldWrapper();
        DateWrapper dateWrapper = new DateWrapper();
        VocabWrapper vocabWrapper = new VocabWrapper();

        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put(fieldTitle, title);
        expected.put(fieldType, nodeType);
        expected.putAll(fieldWrapper.wrap(fieldBody, body));
        expected.putAll(dateWrapper.wrap(fieldDate, date));
        expected.putAll(fieldWrapper.wrap(fieldByline, byline));
        expected.putAll(vocabWrapper.wrap(fieldSection, 404));
        expected.putAll(fieldWrapper.wrap(fieldNewsItemId, newsItemId));
        expected.putAll(fieldWrapper.wrap(fieldEditionId, editionId));
        expected.putAll(fieldWrapper.wrap(fieldStart, start));
        expected.putAll(fieldWrapper.wrap(fieldPosition, position));

        String[] fields = DrupalUtils.convertStringArrayA(fieldMapping);
        String sectionMapping = TestHelper.getSectionMapping(sectionId, 404);
        Map<String, String> sections = DrupalUtils.convertStringMap(sectionMapping);
        Map<String, String> actual = DrupalUtils.nodeParams(placement, nodeType, fields, sections);

        // Remove null values (added by Gson)
        for (Iterator<Map.Entry<String, String>> it = actual.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue() == null) {
                it.remove();
            }
        }

        assertEquals(expected, actual);
    }

    @Test
    public void nodeParams_partialMap_returnMap() throws Exception {
        Calendar calendar = new GregorianCalendar(2010, Calendar.JULY, 3);
        SimpleDateFormat sdf = new SimpleDateFormat(DrupalUtils.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        String title = "Test NewsItem title";
        String nodeType = "article";
        String body = "<p>Test NewsItem story.</p>";
        String date = sdf.format(calendar.getTime());

        String fieldTitle = "title";
        String fieldType = "type";
        String fieldBody = "field_body";
        String fieldDate = "field_date";

        Edition edition = mock(Edition.class);
        when(edition.getPublicationDate()).thenReturn(calendar);

        NewsItem newsItem = mock(NewsItem.class);
        when(newsItem.getTitle()).thenReturn(title);
        when(newsItem.getStory()).thenReturn(body);

        NewsItemPlacement placement = mock(NewsItemPlacement.class);
        when(placement.getEdition()).thenReturn(edition);
        when(placement.getNewsItem()).thenReturn(newsItem);

        FieldWrapper fieldWrapper = new FieldWrapper();
        DateWrapper dateWrapper = new DateWrapper();

        Map<String, String> expected = new LinkedHashMap<String, String>();
        expected.put(fieldTitle, title);
        expected.put(fieldType, nodeType);
        expected.putAll(fieldWrapper.wrap(fieldBody, body));
        expected.putAll(dateWrapper.wrap(fieldDate, date));

        String fieldMapping = String.format("%s:%s, %s:%s",
                DrupalUtils.KEY_BODY, fieldBody,
                DrupalUtils.KEY_DATE, fieldDate);
        String[] fields = DrupalUtils.convertStringArrayA(fieldMapping);
        Map<String, String> actual = DrupalUtils.nodeParams(placement, nodeType, fields, null);

        // Remove null values (added by Gson)
        for (Iterator<Map.Entry<String, String>> it = actual.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue() == null) {
                it.remove();
            }
        }

        assertEquals(expected, actual);
    }

    @Test
    public void fileParams_returnMap() throws Exception {
        File testFile = File.createTempFile("test_file", ".tmp");
        testFile.deleteOnExit();

        Long mediaItemId = 7357L;

        String extension = FilenameUtils.getExtension(testFile.getAbsolutePath());
        String fileName = String.format("%d.%s", mediaItemId, extension);
        String caption = "Test NewsItem file";
        String renditionName = "test";

        MediaItemRendition rendition = mock(MediaItemRendition.class);
        when(rendition.getFileLocation()).thenReturn(testFile.getAbsolutePath());
        when(rendition.getExtension()).thenReturn(extension);

        MediaItem mediaItem = mock(MediaItem.class);
        when(mediaItem.getId()).thenReturn(mediaItemId);
        when(mediaItem.isRenditionsAttached()).thenReturn(true);
        when(mediaItem.findRendition(renditionName)).thenReturn(rendition);

        NewsItemMediaAttachment mediaAttachment = new NewsItemMediaAttachment();
        mediaAttachment.setMediaItem(mediaItem);
        mediaAttachment.setCaption(caption);

        NewsItem newsItem = TestHelper.getNewsItem(1L);
        newsItem.getMediaAttachments().add(mediaAttachment);

        NamedTypedFile typedFile = new NamedTypedFile("application/octet-stream", testFile, fileName);

        Map<String, Object> expected = new LinkedHashMap<String, Object>();
        expected.put(String.format("files[%d]", 0), typedFile);
        expected.put(String.format("field_values[%d][alt]", 0), caption);
        expected.put(String.format("field_values[%d][title]", 0), caption);

        Map<String, Object> actual = DrupalUtils.fileParams(newsItem, renditionName, null);

        assertEquals(expected, actual);
    }

    @Test
    public void fileParams_noRendition_throwException() throws Exception {
        String renditionName = "test";

        MediaItem mediaItem = mock(MediaItem.class);
        when(mediaItem.getId()).thenReturn(7357L);
        when(mediaItem.isRenditionsAttached()).thenReturn(true);
        when(mediaItem.findRendition(renditionName)).thenThrow(new RenditionNotFoundException());

        NewsItemMediaAttachment mediaAttachment = new NewsItemMediaAttachment();
        mediaAttachment.setMediaItem(mediaItem);

        NewsItem newsItem = TestHelper.getNewsItem(1L);
        newsItem.getMediaAttachments().add(mediaAttachment);

        Map<String, Object> params = DrupalUtils.fileParams(newsItem, renditionName, null);

        assertTrue(params.isEmpty());
    }

    @Test
    public void fileParams_badFile_throwException() throws Exception {
        String renditionName = "test";

        MediaItemRendition rendition = mock(MediaItemRendition.class);
        when(rendition.getFileLocation()).thenReturn("/this/file/is/404");

        MediaItem mediaItem = mock(MediaItem.class);
        when(mediaItem.getId()).thenReturn(7357L);
        when(mediaItem.findRendition(renditionName)).thenReturn(rendition);

        NewsItemMediaAttachment mediaAttachment = new NewsItemMediaAttachment();
        mediaAttachment.setMediaItem(mediaItem);

        NewsItem newsItem = TestHelper.getNewsItem(1L);
        newsItem.getMediaAttachments().add(mediaAttachment);

        Map<String, Object> params = DrupalUtils.fileParams(newsItem, renditionName, null);

        assertTrue(params.isEmpty());
    }
}

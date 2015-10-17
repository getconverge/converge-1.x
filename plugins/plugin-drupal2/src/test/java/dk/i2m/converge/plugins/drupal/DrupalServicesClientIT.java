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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.plugins.drupal.entities.NodeEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link DrupalServicesClient}.
 */
public class DrupalServicesClientIT {

    private static final Logger LOG = Logger.getLogger(DrupalServicesClientIT.class.getName());

    private static final Long NEWSITEM_ID = (long) (new Random().nextInt((1000 - 100) + 1) + 100);
    private static final Long SECTION_ID = (long) (new Random().nextInt((1000 - 100) + 1) + 100);
    private static final Integer TAXONOMY_ID = 2;

    private static boolean execute;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DrupalServicesClient servicesClient;

    @BeforeClass
    public static void beforeClass() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Helper.SERVICE_ENDPOINT)
                .build();

        try {
            Response response = client.newCall(request).execute();
            execute = response.code() == 200;
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Skipping test. Test site \"{0}\" is not available: {1}", new Object[]{
                    Helper.SERVICE_ENDPOINT, ex.getMessage()});
        }
    }

    @Before
    public void setUp() throws Exception {
        if (execute) {
            servicesClient = new DrupalServicesClient(Helper.SERVICE_ENDPOINT, Helper.USERNAME, Helper.PASSWORD);
            servicesClient.setNodeAlias(Helper.NODE_ALIAS);
            servicesClient.setUserAlias(Helper.USER_ALIAS);
            servicesClient.loginUser();
        }
    }

    @After
    public void tearDown() throws Exception {
        if (execute) {
            servicesClient.logoutUser();
        }
    }

    @Test
    public void createNode_returnEntity() throws Exception {
        Assume.assumeTrue(execute);

        NodeEntity create = servicesClient.createNode(getNodeParams());

        assertNotNull(create.getId());
    }

    @Test
    public void updateNode_returnEntity() throws Exception {
        Assume.assumeTrue(execute);

        Map<String, String> params = getNodeParams();
        NodeEntity create = servicesClient.createNode(params);
        NodeEntity update = servicesClient.updateNode(create.getId(), params);

        assertNotNull(update.getId());
    }

    @Test
    public void indexNode_returnList() throws Exception {
        Assume.assumeTrue(execute);

        servicesClient.createNode(getNodeParams());

        String newsItemIdField = DrupalUtils.getKeyValue(getFieldMapping(), DrupalUtils.KEY_NEWSITEM_ID);
        Map<String, String> options = new LinkedHashMap<String, String>();
        options.put("parameters[type]", Helper.NODE_TYPE);
        options.put(String.format("parameters[%s]", newsItemIdField), String.valueOf(NEWSITEM_ID));
        List<NodeEntity> nodeEntities = servicesClient.indexNode(options);

        assertFalse(nodeEntities.isEmpty());
        assertNotNull(nodeEntities.get(0).getId());
    }

    @Test
    public void attachFiles_noException() throws Exception {
        Assume.assumeTrue(execute);

        URL url = getClass().getClassLoader().getResource("dk/i2m/converge/plugins/drupal/converge.png");
        File file = FileUtils.toFile(url);
        String extension = FilenameUtils.getExtension(file.getName());
        String renditionName = "test";

        MediaItemRendition rendition = mock(MediaItemRendition.class);
        when(rendition.getFileLocation()).thenReturn(file.getAbsolutePath());
        when(rendition.getExtension()).thenReturn(extension);

        MediaItem mediaItem = mock(MediaItem.class);
        when(mediaItem.getId()).thenReturn(7357L);
        when(mediaItem.isRenditionsAttached()).thenReturn(true);
        when(mediaItem.findRendition(renditionName)).thenReturn(rendition);

        NewsItemMediaAttachment mediaAttachment = new NewsItemMediaAttachment();
        mediaAttachment.setMediaItem(mediaItem);
        mediaAttachment.setCaption("Test file");

        NewsItem newsItem = Helper.getNewsItem(1L);
        newsItem.getMediaAttachments().add(mediaAttachment);

        NodeEntity create = servicesClient.createNode(getNodeParams());
        String imageField = DrupalUtils.getKeyValue(getFieldMapping(), DrupalUtils.KEY_IMAGE);
        Map<String, Object> params = DrupalUtils.fileParams(newsItem, renditionName, null);

        servicesClient.attachFiles(create, imageField, params);
    }

    private Map<String, String> getNodeParams() {
        NewsItemPlacement placement = Helper.getPlacement(1L);
        placement.setEdition(Helper.getEdition(1L));
        placement.setSection(Helper.getSection(SECTION_ID));
        placement.setNewsItem(Helper.getNewsItem(NEWSITEM_ID));

        String[] fields = getFieldMapping();
        String sectionMapping = Helper.getSectionMapping(SECTION_ID, TAXONOMY_ID);
        Map<String, String> sections = DrupalUtils.convertStringMap(sectionMapping);

        return DrupalUtils.nodeParams(placement, Helper.NODE_TYPE, fields, sections);
    }

    private String[] getFieldMapping() {
        String mapping = Helper.getFieldMapping();

        return DrupalUtils.convertStringArrayA(mapping);
    }
}

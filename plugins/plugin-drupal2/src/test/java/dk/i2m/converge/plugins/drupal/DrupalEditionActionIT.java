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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.plugins.drupal.entities.NodeEntity;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link DrupalEditionAction}.
 */
public class DrupalEditionActionIT {

    private static final Logger LOG = Logger.getLogger(DrupalEditionActionIT.class.getName());

    private static final Long EDITION_ID = (long) (new Random().nextInt((1000 - 100) + 1) + 100);
    private static final Long NEWSITEM_ID = (long) (new Random().nextInt((1000 - 100) + 1) + 100);
    private static final Long SECTION_ID = (long) (new Random().nextInt((1000 - 100) + 1) + 100);
    private static final Long STATE_UPLOAD = 20L;
    private static final Long STATE_UPLOADED = 21L;
    private static final Long STATE_FAILED = 22L;
    private static final Integer TAXONOMY_ID = 2;

    private static final String MAPPING_FIELD = TestHelper.getFieldMapping();
    private static final String MAPPING_SECTION = TestHelper.getSectionMapping(SECTION_ID, TAXONOMY_ID);

    private static boolean execute;

    @BeforeClass
    public static void beforeClass() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(TestHelper.SERVICE_ENDPOINT)
                .build();

        try {
            Response response = client.newCall(request).execute();
            execute = response.code() == 200;
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Skipping test. Test site \"{0}\" is not available: {1}", new Object[]{
                    TestHelper.SERVICE_ENDPOINT, ex.getMessage()});
        }
    }

    @Test
    public void executePlacement_entityCreated() throws Exception {
        Assume.assumeTrue(execute);

        DrupalEditionAction plugin = new DrupalEditionAction();
        NewsItemPlacement placement = TestHelper.getPlacement(1L);
        placement.setOutlet(TestHelper.getOutlet(1L));
        placement.setEdition(TestHelper.getEdition(EDITION_ID));
        placement.setSection(TestHelper.getSection(SECTION_ID));
        NewsItem newsItem = TestHelper.getNewsItem(NEWSITEM_ID);
        newsItem.setCurrentState(getCurrentState());
        placement.setNewsItem(newsItem);

        plugin.executePlacement(getPluginContext(), placement, placement.getEdition(), getAction());

        DrupalServicesClient servicesClient = new DrupalServicesClient(
                TestHelper.SERVICE_ENDPOINT, TestHelper.USERNAME, TestHelper.PASSWORD);
        servicesClient.loginUser();

        String[] fields = DrupalUtils.convertStringArrayA(TestHelper.getFieldMapping());

        String newsItemIdField = DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_NEWSITEM_ID);
        Map<String, String> options = new LinkedHashMap<String, String>();
        options.put("parameters[type]", TestHelper.NODE_TYPE);
        options.put(String.format("parameters[%s]", newsItemIdField), String.valueOf(NEWSITEM_ID));
        List<NodeEntity> nodeEntities = servicesClient.indexNode(options);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format("%s/%s/%d", TestHelper.SERVICE_ENDPOINT, TestHelper.NODE_ALIAS,
                        nodeEntities.get(0).getId()))
                .build();

        Response response = client.newCall(request).execute();

        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response.body().string());
        String title = jsonObject.get("title").getAsString();
        String body = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_BODY))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsString();
        String date = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_DATE))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsString();
        String byline = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_BYLINE))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsString();
        Integer section = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_SECTION))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("tid").getAsInt();
        Integer editionId = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_EDITION_ID))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsInt();
        Integer newsItemId = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_NEWSITEM_ID))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsInt();
        Integer start = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_START))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsInt();
        Integer position = jsonObject.get(DrupalUtils.getKeyValue(fields, DrupalUtils.KEY_POSITION))
                .getAsJsonObject()
                .get("und").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsInt();

        SimpleDateFormat sdf = new SimpleDateFormat(DrupalUtils.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        assertEquals(placement.getNewsItem().getTitle(), title);
        assertEquals(placement.getNewsItem().getStory(), body);
        assertEquals(sdf.format(placement.getEdition().getPublicationDate().getTime()), date);
        assertEquals(placement.getNewsItem().getByLine(), byline);
        assertEquals(TAXONOMY_ID, section);
        assertEquals(placement.getEdition().getId(), Long.valueOf(editionId));
        assertEquals(placement.getNewsItem().getId(), Long.valueOf(newsItemId));
        assertEquals(placement.getStart(), start);
        assertEquals(placement.getPosition(), position);
    }

    private OutletEditionAction getAction() {
        OutletEditionAction action = new OutletEditionAction();
        action.setId(1L);
        action.setLabel("Upload to Test Site");
        action.setActionClass(DrupalEditionAction.class.getName());
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.SERVICE_ENDPOINT.name(), TestHelper.SERVICE_ENDPOINT));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.NODE_TYPE.name(), TestHelper.NODE_TYPE));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.ALIAS_NODE.name(), TestHelper.NODE_ALIAS));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.ALIAS_USER.name(), TestHelper.USER_ALIAS));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.USERNAME.name(), TestHelper.USERNAME));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.PASSWORD.name(), TestHelper.PASSWORD));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.MAPPING_FIELD.name(), MAPPING_FIELD));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.MAPPING_SECTION.name(), MAPPING_SECTION));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.STATE_UPLOAD.name(), String.valueOf(STATE_UPLOAD)));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.STATE_UPLOADED.name(), String.valueOf(STATE_UPLOADED)));
        action.getProperties().add(new OutletEditionActionProperty(
                action, Property.STATE_FAILED.name(), String.valueOf(STATE_FAILED)));

        return action;
    }

    private PluginContext getPluginContext() {
        PluginContext context = mock(PluginContext.class);
        when(context.addNewsItemActionState(anyLong(), anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(new NewsItemActionState());
        when(context.findNewsItemActionStateOrCreate(anyLong(), anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(new NewsItemActionState());

        return context;
    }

    private WorkflowState getCurrentState() {
        WorkflowState state = new WorkflowState();
        state.setId(STATE_UPLOAD);
        state.setName("Upload to Test");
        state.getNextStates().add(getOptionSuccess(state));
        state.getNextStates().add(getOptionFailed(state));

        return state;
    }

    private WorkflowStep getOptionSuccess(WorkflowState state) {
        WorkflowStep step = new WorkflowStep();
        step.setId(STATE_UPLOADED);
        step.setName("Upload Successful");
        step.setFromState(state);

        return step;
    }

    private WorkflowStep getOptionFailed(WorkflowState state) {
        WorkflowStep step = new WorkflowStep();
        step.setId(STATE_FAILED);
        step.setName("Upload Failed");
        step.setFromState(state);

        return step;
    }
}

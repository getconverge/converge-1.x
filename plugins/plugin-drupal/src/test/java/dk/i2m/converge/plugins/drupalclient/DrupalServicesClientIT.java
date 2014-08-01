/*
 * Copyright (C) 2012 - 2013 Interactive Media Management
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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for testing the {@link DrupalServicesClient}. This
 * integration test must be executed with a pre-configured Drupal installation
 * located at {@link #DRUPAL_URL}. If the Drupal installation is not available
 * the test cases will be ignored.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalServicesClientIT {

    private static final Logger LOG = Logger.getLogger(DrupalServicesClientIT.class.getName());
    private static final DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DRUPAL_URL = "http://int.drupal.getconverge.com";
    public static final String SERVICE_END_POINT = "converge";
    public static final String DRUPAL_UID = "converge";
    public static final String DRUPAL_PWD = "c0nv3rg3";
    public static final long EXISTING_NEWS_ITEM_ID = 145L;
    public static final long NON_EXISTING_NEWS_ITEM_ID = 9999999L;
    public static final long NID_MATCHING_EXISTING_NEWS_ITEM_ID = 1L;
    private static boolean execute = false;

    /**
     * Determine whether the test cases should be executed. The {@link #execute}
     * flag will be set based on the result of checking if the local test site
     * exists.
     */
    @BeforeClass
    public static void drupalTestInstallationAvailable() {
        HttpURLConnection http = null;
        try {
            URL url = new URL(DRUPAL_URL);
            http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            execute = statusCode == 200;

        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            execute = false;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }

        if (!execute) {
            LOG.log(Level.WARNING, "Skipping test. Test site {0} is not available", DRUPAL_URL);
        }
    }

    /**
     * Check before each test case if it should be executed (based on the
     * {@link #execute} flag.
     */
    @Before
    public void shouldExecute() {
        org.junit.Assume.assumeTrue(execute);
    }

    @Test
    public void drupalServicesClient_correctCredentials_successfulLogin() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);

        // Act
        boolean loginSuccces = client.login();
        String sessionId = client.getSessionId();
        String sessionName = client.getSessionName();

        // Assert
        assertTrue(loginSuccces);
        assertNotNull(sessionId);
        assertNotNull(sessionName);
    }

    @Test
    public void drupalServicesClient_incorrectCredentials_failedLogin() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, "incorrectuid", "incorrectpwd");

        // Act
        boolean loginStatus = client.login();

        // Assert
        assertFalse(loginStatus);
    }

    @Test
    public void drupalServicesClient_existingNewsItemExists_returnsTrue() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        // Act
        boolean exists = client.exists("newsitem", EXISTING_NEWS_ITEM_ID);

        // Assert
        assertTrue("Drupal node with NewsItem (ID) " + EXISTING_NEWS_ITEM_ID + " does not exist", exists);
    }

    @Test
    public void drupalServicesClient_nonExistingNewsItemExists_returnsFalse() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        // Act
        boolean exists = client.exists("newsitem", NON_EXISTING_NEWS_ITEM_ID);

        // Assert
        assertFalse(exists);
    }

    @Test
    public void drupalServicesClient_retrieveNodeIdFromExistingNewsItem_returnMatchingNodeId() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        // Act
        Long existingNid = client.retrieveNodeIdFromResource("newsitem", EXISTING_NEWS_ITEM_ID);

        assertEquals(NID_MATCHING_EXISTING_NEWS_ITEM_ID, existingNid.longValue());
    }

    @Test
    public void drupalServicesClient_retrieveNodeIdFromNonExistingNewsItem_throwsException() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        // Act
        try {
            client.retrieveNodeIdFromResource("newsitem", NON_EXISTING_NEWS_ITEM_ID);
            fail("Exception was not thrown upon retrieving non-existing none");
        } catch (IOException ex) {
        }
    }

    @Test
    public void drupalServicesClient_createNode_returnNodeInfo() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Calendar publishOn = Calendar.getInstance();
        publishOn.add(Calendar.HOUR_OF_DAY, 2);
        params.add(new BasicNameValuePair("type", "newsitem"));
        params.add(new BasicNameValuePair("date", DRUPAL_DATE_FORMAT.format(publishOn.getTime())));
        params.add(new BasicNameValuePair("title", "Story created by integration test. Can be deleted"));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][summary]", "This is the summary of the story. You can delete this story."));
        params.add(new BasicNameValuePair("body[und][0][value]", "This is the body. You can delete this story"));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("publish_on", DRUPAL_DATE_FORMAT.format(publishOn.getTime())));
        params.add(new BasicNameValuePair("field_author[und][0][value]", "Mr. Integration Tester"));
        params.add(new BasicNameValuePair("field_converge_id[und][0][value]", "123456"));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "1"));
        params.add(new BasicNameValuePair("field_section[und][0]", "16"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());

        // Act
        NodeInfo nodeInfo = client.createNode(entity);
        try {
            String node = client.retrieveNode(nodeInfo.getId());
            // Assert
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        // Assert
        assertNotNull(nodeInfo);
        assertNotNull(nodeInfo.getId());
        assertNotNull(nodeInfo.getUri());
        assertTrue(nodeInfo.getUri().startsWith(DRUPAL_URL));
    }

    @Test
    public void drupalServicesClient_deleteNode_nodeNoLongerExists() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", "newsitem"));
        params.add(new BasicNameValuePair("date", DRUPAL_DATE_FORMAT.format(Calendar.getInstance().getTime())));
        params.add(new BasicNameValuePair("title", "Story created for deletion by integration test"));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][summary]", "This is the summary of the story. You can delete this story."));
        params.add(new BasicNameValuePair("body[und][0][value]", "This is the body. You can delete this story"));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());
        NodeInfo nodeInfo = client.createNode(entity);

        // Act
        boolean deleted = client.delete("node", nodeInfo.getId());
        boolean exists = client.exists("node", nodeInfo.getId());

        // Assert
        assertTrue("True should have been returned from delete operation", deleted);
        assertFalse("False should have been returned from exists operation", exists);
    }

    @Test
    public void drupalServicesClient_attachFile_filesAttached() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Calendar publishOn = Calendar.getInstance();
        publishOn.add(Calendar.HOUR_OF_DAY, 3);
        params.add(new BasicNameValuePair("type", "newsitem"));
        params.add(new BasicNameValuePair("date", DRUPAL_DATE_FORMAT.format(Calendar.getInstance().getTime())));
        params.add(new BasicNameValuePair("title", "Attachment story added by integration test. Can be deleted"));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][summary]", "This is the summary with attachment."));
        params.add(new BasicNameValuePair("body[und][0][value]", "This is the body with attachment."));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("publish_on", DRUPAL_DATE_FORMAT.format(publishOn.getTime())));
        params.add(new BasicNameValuePair("field_author[und][0][value]", "Mr. Integration Tester"));
        params.add(new BasicNameValuePair("field_converge_id[und][0][value]", "123457"));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "1"));
        params.add(new BasicNameValuePair("field_section[und][0]", "16"));
        params.add(new BasicNameValuePair("field_placement_start[und][0]", "1"));
        params.add(new BasicNameValuePair("field_placement_position[und][0]", "2"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());
        NodeInfo nodeInfo = client.createNode(entity);

        // Act
        List<FileInfo> files = new ArrayList<FileInfo>();
        URL url = getClass().getClassLoader().getResource("dk/i2m/converge/plugins/drupalclient/converge.gif");
        File attachment = FileUtils.toFile(url);
        FileInfo fileInfo = new FileInfo(attachment, "Some caption");
        files.add(fileInfo);
        client.attachFile(nodeInfo.getId(), "field_image", files);
        List<DrupalFile> nodeFiles = client.getNodeFiles(nodeInfo.getId());

        // Assert
        assertEquals(1, nodeFiles.size());
    }
}

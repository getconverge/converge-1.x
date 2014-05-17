/*
 * Copyright (C) 2012 - 2013 Interactive Media Management
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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Unit test for testing the {@link DrupalServicesClient}. This unit test must
 * be executed with a pre-configured Drupal installation located at
 * {@link #DRUPAL_URL}. If the Drupal installation is not available the test
 * cases will be ignored.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalServicesClientTest {

    private static final Logger LOG = Logger.getLogger(DrupalServicesClientTest.class.getName());
    public static final String DRUPAL_URL = "http://localhost/www.the-star.co.ke";
    public static final String SERVICE_END_POINT = "converge";
    public static final String DRUPAL_UID = "converge";
    public static final String DRUPAL_PWD = "c0nv3rg3w3bs1t3";
    public static final long NON_EXISTING_NEWS_ITEM_ID = 9999L;
    public static final long EXISTING_NEWS_ITEM_ID = 3029L;
    public static final long NID_MATCHING_EXISTING_NEWS_ITEM_ID = 22L;
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean execute = false;

    /**
     * Determine whether the test cases should be executed. The {@link #execute}
     * flag will be set based on the result of checking if the local test site
     * exists.
     */
    @BeforeClass
    public static void drupalTestInstallationAvailable() {
        try {
            URL url = new URL(DRUPAL_URL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            if (statusCode == 200) {
                execute = true;
            } else {
                execute = false;
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            execute = false;
        }

        if (!execute) {
            LOG.log(Level.WARNING, "Skipping test. Test site " + DRUPAL_URL + " is not available");
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
    public void testLogin() throws Exception {
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        assertTrue(client.login());
        assertNotNull(client.getSessionId());
        assertNotNull(client.getSessionName());
        client.logout();
    }

    @Test
    public void testNewsItemExists() throws Exception {
        // Arrange
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        // Act
        boolean exists = client.exists("newsitem", EXISTING_NEWS_ITEM_ID);
        client.logout();

        // Assert
        assertTrue("Drupal node with NewsItem (ID) " + EXISTING_NEWS_ITEM_ID + " does not exist", exists);
    }

    @Test
    public void testNewsItemRetrieveFromNewsItemId() throws Exception {
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        Long existingNid = client.retrieveNodeIdFromResource("newsitem", EXISTING_NEWS_ITEM_ID);

        assertEquals(NID_MATCHING_EXISTING_NEWS_ITEM_ID, existingNid.longValue());
        client.logout();
    }

    @Test
    public void testNewsItemRetrieveFromNonExistingNewsItemId() throws Exception {
        boolean success = true;
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        try {
            client.retrieveNodeIdFromResource("newsitem", NON_EXISTING_NEWS_ITEM_ID);
            success = false;
        } catch (IOException ex) {
        } finally {
            client.logout();
        }

        assertTrue("Did not through HttpResponseException as expected", success);
    }

    @Test
    public void testNewsItemNotExists() throws Exception {
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();
        assertFalse(client.exists("newsitem", NON_EXISTING_NEWS_ITEM_ID));
        client.logout();
    }

    @Test
    public void testCreateNewsItem() throws Exception {
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        Calendar publishOn = Calendar.getInstance();
        publishOn.add(Calendar.HOUR_OF_DAY, 3);
        params.add(new BasicNameValuePair("type", "article"));
        params.add(new BasicNameValuePair("date", sdf.format(Calendar.getInstance().getTime())));
        params.add(new BasicNameValuePair("title", "My story 2"));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][summary]", "This is the summary"));
        params.add(new BasicNameValuePair("body[und][0][value]", "This is the body"));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("field_author[und][0][value]", "Name of the author"));
        params.add(new BasicNameValuePair("field_newsitem[und][0][value]", "123456"));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "1"));
        params.add(new BasicNameValuePair("field_section[und][0]", "16"));
        params.add(new BasicNameValuePair("publish_on", sdf.format(publishOn.getTime())));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());

        NodeInfo nodeInfo = client.createNode(entity);
        String node = client.retrieveNode(nodeInfo.getId());
        client.logout();
    }

    @Test
    public void testCreateNewsItemWithFile() throws Exception {
        DrupalServicesClient client = new DrupalServicesClient(DRUPAL_URL, SERVICE_END_POINT, DRUPAL_UID, DRUPAL_PWD);
        client.login();

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        Calendar publishOn = Calendar.getInstance();
        publishOn.add(Calendar.HOUR_OF_DAY, 3);
        params.add(new BasicNameValuePair("type", "article"));
        params.add(new BasicNameValuePair("date", sdf.format(Calendar.getInstance().getTime())));
        params.add(new BasicNameValuePair("title", "My story 2"));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][summary]", "This is the summary"));
        params.add(new BasicNameValuePair("body[und][0][value]", "This is the body"));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("field_author[und][0][value]", "Name of the author"));
        params.add(new BasicNameValuePair("field_newsitem[und][0][value]", "123456"));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "1"));
        params.add(new BasicNameValuePair("field_section[und][0]", "16"));
        params.add(new BasicNameValuePair("field_placement_start[und][0]", "1"));
        params.add(new BasicNameValuePair("field_placement_position[und][0]", "2"));

        params.add(new BasicNameValuePair("publish_on", sdf.format(publishOn.getTime())));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());

        NodeInfo nodeInfo = client.createNode(entity);

        List<FileInfo> files = new ArrayList<FileInfo>();

        URL url = getClass().getClassLoader().getResource("news_item_image.jpg");
        File attachment = FileUtils.toFile(url);

        FileInfo fileInfo = new FileInfo(attachment, "Some other caption");
        files.add(fileInfo);

        // Attach the file
        client.attachFile(nodeInfo.getId(), "field_image", files);

        // Remove the file
        client.removeFiles(nodeInfo.getId(), "field_image");

        client.logout();
    }
}

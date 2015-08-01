/*
 * Copyright (C) 2012 - 2014 Interactive Media Management
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfig;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class WpXmlRpcClient {

    private static final String USER_AGENT = "Converge WpXmlRpcClient 1.0";
    private static final String XML_RPC_ENDPOINT = "/xmlrpc.php";
    private static final Integer DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final Integer DEFAULT_REPLY_TIMEOUT = 60000;
    private static final String WP_API_NEW_POST = "wp.newPost";
    private static final String WP_API_EDIT_POST = "wp.editPost";
    private static final String WP_API_GET_POST = "wp.getPost";
    private static final String WP_API_DELETE_POST = "wp.deletePost";
    private static final String WP_API_UPLOAD_FILE = "wp.uploadFile";
    private static final String WP_API_GET_MEDIA_FILE = "wp.getMediaItem";
    private static final String FIELD_TAXONOMY_CATEGORY = "category";
    private static final String FIELD_TAXONOMY_POST_TAG = "post_tag";
    private static final String FIELD_FILE_NAME = "name";
    private static final String FIELD_FILE_TYPE = "type";
    private static final String FIELD_FILE_BITS = "bits";
    private static final String FIELD_FILE_OVERWRITE = "overwrite";
    private static final String FIELD_FILE_POST_ID = "post_id";
    private static final String FIELD_POST_POST_DATE = "post_date";
    private static final String FIELD_POST_TERMS_NAMES = "terms_names";
    private static final String FIELD_POST_POST_CONTENT = "post_content";
    private static final String FIELD_POST_POST_EXCERPT = "post_excerpt";
    private static final String FIELD_POST_POST_TITLE = "post_title";
    private static final String FIELD_POST_POST_STATUS = "post_status";
    private static final Integer BLOG_ID = 0;
    private Integer connectionTimeout;
    private String url;
    private String username;
    private String password;
    private Integer replyTimeout;
    private XmlRpcClientConfigImpl config = null;

    /**
     * Creates a new instance of {@link WordPresslServicesClient}.
     */
    public WpXmlRpcClient() {
        this("", "", "");
    }

    /**
     * Creates a new instance of {@link WpXmlRpcClient} with the URL, username
     * and password of the Wordpress instance pre-entered.
     *
     * @param url URL of the Wordpress installation
     * @param username Username for logging into the Wordpress installation
     * @param password Password for logging into the Wordpress installation
     */
    public WpXmlRpcClient(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        this.replyTimeout = DEFAULT_REPLY_TIMEOUT;
    }

    /**
     * Creates a new post on Wordpress.
     *
     * @param type Type of post
     * @param status Status of the post after posting
     * @param title Title of the post
     * @param postContent Content of the post
     * @param excerpt Excerpt of the post
     * @param tags Tags of the post
     * @param categories Categories of the post
     * @return Unique identifier of the created post
     * @throws WpXmlRpcClientException If the post could not be created
     */
    public Integer createPost(String type, PostStatus status, String title, String postContent, String excerpt, String[] tags, String[] categories) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();

            // Prepare terms
            Map<String, Object[]> terms = new HashMap<String, Object[]>();
            terms.put(FIELD_TAXONOMY_POST_TAG, tags);
            terms.put(FIELD_TAXONOMY_CATEGORY, categories);

            Map<String, Object> content = new HashMap<String, Object>();
            content.put(FIELD_POST_POST_STATUS, status.name().toLowerCase());
            content.put(FIELD_POST_POST_TITLE, title);
            content.put(FIELD_POST_POST_EXCERPT, excerpt);
            content.put(FIELD_POST_POST_CONTENT, postContent);
            content.put(FIELD_POST_TERMS_NAMES, terms);
            content.put(FIELD_POST_POST_DATE, new Date().toString());

            return Integer.valueOf((String) client.execute(WP_API_NEW_POST, new Object[]{0, this.username, this.password, content}));
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    public boolean editPost(Integer postId, String type, PostStatus status, String title, String postContent, String excerpt, String[] tags, String[] categories) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();

            // Prepare terms
            Map<String, Object[]> terms = new HashMap<String, Object[]>();
            terms.put(FIELD_TAXONOMY_POST_TAG, tags);
            terms.put(FIELD_TAXONOMY_CATEGORY, categories);

            Map<String, Object> content = new HashMap<String, Object>();
            content.put(FIELD_POST_POST_STATUS, status.name().toLowerCase());
            content.put(FIELD_POST_POST_TITLE, title);
            content.put(FIELD_POST_POST_EXCERPT, excerpt);
            content.put(FIELD_POST_POST_CONTENT, postContent);
            content.put(FIELD_POST_TERMS_NAMES, terms);

            return (Boolean) client.execute(WP_API_EDIT_POST, new Object[]{BLOG_ID, this.username, this.password, postId, content});
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    /**
     * Deletes an existing post on Wordpress.
     *
     * @param postId Unique identifier of the post to delete
     * @return {@code true} if the post was deleted, otherwise {@code false}
     * @throws WpXmlRpcClientException If it was not possible to delete the post
     */
    public boolean deletePost(Integer postId) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();

            return (Boolean) client.execute(WP_API_DELETE_POST, new Object[]{BLOG_ID, this.username, this.password, postId});
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    /**
     * Check if a given post exists on Wordpress.
     *
     * @param postId Unique identifier of the post to check if exists
     * @return {@code true} if the post exists, otherwise {@code false}
     * @throws WpXmlRpcClientException If it was not possible to determine if
     * the post exists
     */
    public boolean exists(Integer postId) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();
            client.execute(WP_API_GET_POST, new Object[]{BLOG_ID, this.username, this.password, postId});
            return true;
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            if (ex.code == 404) {
                return false;
            }
            throw new WpXmlRpcClientException(ex);
        }
    }

    /**
     * Gets an existing post from Wordpress.
     *
     * @param postId Unique identifier of the post
     * @return {@link Map} of properties making up the post
     * @throws WpXmlRpcClientException If the post could not be retrieved from
     * Wordpress
     */
    public Map<String, Object> getPost(Integer postId) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();
            Map<String, Object> result = (Map) client.execute(WP_API_GET_POST, new Object[]{BLOG_ID, this.username, this.password, postId});
            return result;
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    public Map<String, Object> getMediaFile(int attachmentId) throws WpXmlRpcClientException {
        try {
            XmlRpcClient client = getClient();
            return (Map<String, Object>) client.execute(WP_API_GET_MEDIA_FILE, new Object[]{BLOG_ID, this.username, this.password, attachmentId});
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    public Map<String, Object> uploadFile(String name, String type, byte[] bits, boolean overwrite, Integer postId) throws WpXmlRpcClientException {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(FIELD_FILE_NAME, name);
        request.put(FIELD_FILE_TYPE, type);
        request.put(FIELD_FILE_BITS, bits);
        request.put(FIELD_FILE_OVERWRITE, overwrite);
        request.put(FIELD_FILE_POST_ID, postId);

        try {
            XmlRpcClient client = getClient();
            return (Map<String, Object>) client.execute(WP_API_UPLOAD_FILE, new Object[]{BLOG_ID, this.username, this.password, request});
        } catch (MalformedURLException ex) {
            throw new WpXmlRpcClientException(ex);
        } catch (XmlRpcException ex) {
            throw new WpXmlRpcClientException(ex);
        }
    }

    /**
     * Gets a configured XML RPC client ready for executing remote procedure
     * calls.
     *
     * @return Configured {@link XmlRpcClient} ready for execution
     * @throws MalformedURLException If the server URL is not valid
     */
    private XmlRpcClient getClient() throws MalformedURLException {
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(getConfig());
        return client;
    }

    /**
     * Gets the configuration of the XML RPC client.
     *
     * @return Configuration of the XML RPC client
     * @throws MalformedURLException If the server URL is invalid
     */
    public XmlRpcClientConfig getConfig() throws MalformedURLException {
        if (this.config == null) {
            this.config = new XmlRpcClientConfigImpl();
            this.config.setServerURL(new URL(this.url + XML_RPC_ENDPOINT));
            this.config.setEnabledForExtensions(true);
            this.config.setEnabledForExceptions(true);
            this.config.setConnectionTimeout(this.connectionTimeout);
            this.config.setReplyTimeout(this.replyTimeout);
            this.config.setUserAgent(USER_AGENT);
        }
        return config;
    }

    /**
     * Gets the URL of the Wordpress installation.
     *
     * @return URL of the Wordpress installation
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the Wordpress installation.
     *
     * @param url URL of the Wordpress installation
     */
    public void setUrl(String url) {
        this.url = url;
        this.config = null;
    }

    /**
     * Gets the username for authenticating on the Wordpress installation.
     *
     * @return Username for authenticating on the Wordpress installation
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for authenticating on the Wordpress installation.
     *
     * @param username Username for authenticating on the Wordpress installation
     */
    public void setUsername(String username) {
        this.username = username;
        this.config = null;
    }

    /**
     * Gets the password for authenticating on the Wordpress installation.
     *
     * @return Password for authenticating on the Wordpress installation
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for authenticating on the Wordpress installation.
     *
     * @param password Password for authenticating on the Wordpress installation
     */
    public void setPassword(String password) {
        this.password = password;
        this.config = null;
    }

    /**
     * Gets the connection timeout for connecting to Wordpress expressed in ms.
     *
     * @return Connection timeout for connecting to Wordpress expressed in ms
     */
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the connection timeout for connecting to Wordpress expressed in ms.
     *
     * @param connectionTimeout Connection timeout for connecting to Wordpress
     * expressed in ms
     */
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.config = null;
    }

    /**
     * Gets the reply timeout for communicating with Wordpress expressed in ms.
     *
     * @return Reply timeout for communicating with Wordpress expressed in ms
     */
    public Integer getReplyTimeout() {
        return replyTimeout;
    }

    /**
     * Sets the reply timeout for communicating with Wordpress expressed in ms.
     *
     * @param replyTimeout Reply timeout for communicating with Wordpress
     * expressed in ms
     */
    public void setReplyTimeout(Integer replyTimeout) {
        this.replyTimeout = replyTimeout;
        this.config = null;
    }

}

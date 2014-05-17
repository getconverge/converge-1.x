/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.joomla.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.client.TimingOutCallback.TimeoutException;

/**
 * Connection to a Joomla instances with the XML-RPC Plug-in installed.
 *
 * @author Allan Lykke Christensen
 */
public class JoomlaConnection {

    /** API logger. */
    private static final Logger logger = Logger.getLogger(JoomlaConnection.class.getName());

    /** Plug-in version for which the API is compatible. */
    private static final String COMPATIBILITY = "1.0";

    /** XML RPC method for determining the version of the adapter. */
    private static final String XMLRPC_METHOD_VERSION = "converge.version";

    /** XML RPC method for obtaining a list of categories. */
    private static final String XMLRPC_METHOD_LIST_CATEGORIES = "converge.listCategories";

    /** XML RPC method for submitting a new article. */
    private static final String XMLRPC_METHOD_NEW_ARTICLE = "converge.newArticle";

    /** XML RPC method for updating an existing article. */
    private static final String XMLRPC_METHOD_UPDATE_ARTICLE = "converge.editArticle";

    /** XML RPC method for deleting an existing article. */
    private static final String XMLRPC_METHOD_DELETE_ARTICLE = "converge.deleteArticle";

    /** XML RPC method for uploading a photo. */
    private static final String XMLRPC_METHOD_NEW_MEDIA = "converge.newMedia";

    private String url;

    private String username;

    private String password;

    private TimeZone timeZone = TimeZone.getDefault();

    private int timeout = 30;
    
    private int replyTimeout = 0;

    /**
     * Creates a new instance of a {@link JoomlaConnection}.
     */
    public JoomlaConnection() {
        this.url = "";
        this.username = "";
        this.password = "";
        this.timeout = 30;
        this.replyTimeout = 0;
    }

    /**
     * Gets the URL of the Joomla XML-RPC service.
     *
     * @return URL of the Joomla XML-RPC service
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the Joomla XML-RPC service. The format is usually:
     * {@code http://joomlasite/xmlrpc/index.php}.
     *
     * @param url
     *          URL of the Joomla XML-RPC service
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the username for accessing the XML-RPC service.
     *
     * @return Username for accessing the XML-RPC service
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for accessing the XML-RPC service.
     *
     * @param username
     *          Username for accessing the XML-RPC service
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password for accessing the XML-RPC service.
     *
     * @return Password for accessing the XML-RPC service
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for accessing the XML-RPC service.
     *
     * @param password
     *          Password for accessing the XML-RPC service
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the connection timeout of calls to the XML-RPC service.
     * 
     * @return Connection timeout (in seconds)
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the connection timeout of calls to the XML-RPC service.
     * 
     * @param timeout 
     *          Connection timeout (in seconds)
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the reply timeout of calls to the XML-RPC service.
     * A reply timeout of 0 disables the reply timeout.
     * 
     * @return Reply timeout (in seconds)
     */
    public int getReplyTimeout() {
        return replyTimeout;
    }

    /**
     * Sets the reply timeout of calls to the XML-RPC service.
     * 
     * @param replyTimeout 
     *          Reply timeout (in seconds), 0 disables the timeout
     */
    public void setReplyTimeout(int replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    /**
     * Gets the {@link TimeZone} of the Joomla server. The {@link TimeZone} is
     * used for the publish and expire dates.
     *
     * @return {@link TimeZone} of the Joomla server.
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the {@link TimeZone} of the Joomla server. The {@link TimeZone} is
     * used for the publish and expire dates.
     *
     * @param timeZone
     *          {@link TimeZone} of the Joomla server
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Determines if the connection can communicate with the configured Joomla
     * instance.
     *
     * @throws JoomlaException Exception thrown if a valid connection could not be established
     * 
     */
    public void validateConnection() throws JoomlaException {
        try {
            TimingOutCallback callback = new TimingOutCallback(getTimeout() * 1000);
            XmlRpcClient client = getXmlRpcClient();
            Object[] params = new Object[]{};
            client.executeAsync(XMLRPC_METHOD_VERSION, params, callback);
            String version = (String) callback.waitForResponse();

            if (version == null) {
                throw new InvalidResponseException();
            } else {
                if (version.startsWith(COMPATIBILITY)) {
                    return;
                } else {
                    throw new IncompatibleJoomlaPluginException(version);
                }
            }
        } catch (TimeoutException e) {
            throw new JoomlaTimeoutException(e);
        } catch (Throwable t) {
            throw new InvalidResponseException(t);
        }
    }

    /**
     * Uploads a new article to the configured Joomla instance.
     *
     * @param foreignId
     *          Unique identifier of the article outside of Joomla
     * @param title
     *          Title of the article
     * @param intro
     *          Article introduction
     * @param story
     *          Full story
     * @param author
     *          Author(s) of the article
     * @param categoryId
     *          Category in which to post the article
     * @param frontPage
     *          Should the article appear on the front page
     * @param displayOrder
     *          Order in which to display the article
     * @param keywords
     *          Keywords of the article (separated with commas)
     * @param description
     *          Meta description of the article
     * @param publish
     *          Date and time when the article should be posted. If {@code null} the article will be published immediately
     * @param expire
     *          Date and time when the article should expire. If {@code null} the article will not have an expiration date
     * @return Unique identifier of the article in Joomla
     * @throws JoomlaException
     */
    public Integer newArticle(String foreignId, String title, String intro, String story, String author, String categoryId, boolean frontPage, String displayOrder, String keywords, String description, Date publish, Date expire) throws JoomlaException {
        logger.log(Level.INFO, "Executing {0} at {1} ", new Object[]{XMLRPC_METHOD_NEW_ARTICLE, url});
        try {
            TimingOutCallback callback = new TimingOutCallback(getTimeout() * 1000);
            XmlRpcClient client = getXmlRpcClient();

            String showOnFrontPage = (frontPage ? "true" : "false");
            Object publishTime = "0";
            Object expireTime = "0";

            if (publish != null) {
                publishTime = publish;
            }

            if (expire != null) {
                expireTime = expire;
            }

            Object[] params = new Object[]{username, password, foreignId, title, intro, story, author, categoryId, showOnFrontPage, displayOrder, keywords, description, publishTime, expireTime};

            client.executeAsync(XMLRPC_METHOD_NEW_ARTICLE, params, callback);

            String articleId = (String) callback.waitForResponse();

            if (articleId != null && !articleId.trim().isEmpty()) {
                return Integer.valueOf(articleId);
            } else {
                throw new JoomlaException("Article was not uploaded to Joomla. Joomla ID was not received from XML-RPC service");
            }
        } catch (TimeoutException ex) {
            throw new JoomlaException(ex);
        } catch (MalformedURLException ex) {
            throw new JoomlaException(ex);
        } catch (XmlRpcException ex) {
            throw new JoomlaException(ex);
        } catch (IOException ex) {
            throw new JoomlaException(ex);
        } catch (Throwable t) {
            throw new JoomlaException(t);
        }
    }

    /**
     * Deletes an article from the Joomla installation.
     *
     * @param foreignId
     *          Unique identifier of the article outside of Joomla
     */
    public void deleteArticle(String foreignId) throws JoomlaException {
        logger.log(Level.INFO, "Executing {0} at {1} ", new Object[]{XMLRPC_METHOD_DELETE_ARTICLE, url});
        try {
            TimingOutCallback callback = new TimingOutCallback(getTimeout() * 1000);
            XmlRpcClient client = getXmlRpcClient();
            Object[] params = new Object[]{username, password, foreignId};

            client.executeAsync(XMLRPC_METHOD_DELETE_ARTICLE, params, callback);
            //String status = (String) client.execute(XMLRPC_METHOD_DELETE_ARTICLE, params);

            String status = (String) callback.waitForResponse();
        } catch (TimeoutException ex) {
            throw new JoomlaException(ex);
        } catch (MalformedURLException ex) {
            throw new JoomlaException(ex);
        } catch (XmlRpcException ex) {
            throw new JoomlaException(ex);
        } catch (IOException ex) {
            throw new JoomlaException(ex);
        } catch (Throwable t) {
            throw new JoomlaException(t);
        }
    }

    /**
     * Gets a list of available content categories.
     *
     * @return List of categories found on the Joomla instance
     * @throws JoomlaException If the categories could not be obtained due to a connection error
     */
    public Map<Integer, String> listCategories() throws JoomlaException {
        logger.log(Level.INFO, "Executing {0} at {1} ", new Object[]{XMLRPC_METHOD_LIST_CATEGORIES, url});
        Map<Integer, String> categories = new HashMap<Integer, String>();
        try {
            int callTimeout = getTimeout() * 1000;
            TimingOutCallback callback = new TimingOutCallback(callTimeout);
            XmlRpcClient client = getXmlRpcClient();
            Object[] params = new Object[]{username, password};

            client.executeAsync(XMLRPC_METHOD_LIST_CATEGORIES, params, callback);
            //Object[] cats = (Object[]) client.execute(XMLRPC_METHOD_LIST_CATEGORIES, params);

            logger.log(Level.INFO, "Calling {0} and waiting for response (Timeout: {1} seconds)", new Object[]{XMLRPC_METHOD_LIST_CATEGORIES, callTimeout});
            Object[] cats = (Object[]) callback.waitForResponse();
            logger.log(Level.INFO, "Got response {0} from {1}. {2} results", new Object[]{XMLRPC_METHOD_LIST_CATEGORIES, url, callTimeout, cats.length});

            for (Object objCat : cats) {
                HashMap<String, String> cat = (HashMap<String, String>) objCat;
                try {
                    categories.put(Integer.valueOf(cat.get("id")), cat.get("title"));
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Unknown value found as category id: {0}. Skipping category.", cat.get("id"));
                }
            }

            return categories;
        } catch (TimeoutException ex) {
            throw new JoomlaException(ex);
        } catch (MalformedURLException ex) {
            throw new JoomlaException(ex);
        } catch (XmlRpcException ex) {
            throw new JoomlaException(ex);
        } catch (IOException ex) {
            throw new JoomlaException(ex);
        } catch (Throwable t) {
            throw new JoomlaException(t);
        }
    }

    /**
     * Uploads a media file to the Joomla instance.
     *
     * @param subfolder
     *          Subfolder to upload the file
     * @param filename
     *          Name of the file to upload
     * @param filedata
     *          Data contained in the file
     * @return Relative URL of the uploaded file
     * @throws JoomlaException
     *          If the file could not be uploaded
     */
    public String uploadMediaFile(String subfolder, String filename, byte[] filedata) throws JoomlaException {
        try {
            logger.log(Level.INFO, "Uploading {0}", new Object[]{filename});
            TimingOutCallback callback = new TimingOutCallback((getTimeout() + 120) * 1000);
            XmlRpcClient client = getXmlRpcClient();

            Object[] params = new Object[]{username, password, subfolder, filename, filedata};

            client.executeAsync(XMLRPC_METHOD_NEW_MEDIA, params, callback);
            //String location = (String) client.execute(XMLRPC_METHOD_NEW_MEDIA, params);

            String location = (String) callback.waitForResponse();

            logger.log(Level.INFO, "Media file #{0} uploaded to {1}", new Object[]{filename, location});
            return location;
        } catch (TimeoutException ex) {
            throw new JoomlaException(ex);
        } catch (XmlRpcException ex) {
            throw new JoomlaException(ex);
        } catch (IOException ex) {
            throw new JoomlaException(ex);
        } catch (Throwable t) {
            throw new JoomlaException(t);
        }
    }

    /**
     * Obtains the XmlRpcClient used for communicating with the
     * XML-RPC service.
     * 
     * @return XmlRpcClient used for communicating with the 
     *         XML-RPC service
     * @throws MalformedURLException 
     *          If the XML-RPC service URL is malformed
     */
    private XmlRpcClient getXmlRpcClient() throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(getUrl()));
        config.setConnectionTimeout(getTimeout() * 1000);
        config.setReplyTimeout(getReplyTimeout() * 1000);

        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
        client.setConfig(config);

        return client;
    }
}

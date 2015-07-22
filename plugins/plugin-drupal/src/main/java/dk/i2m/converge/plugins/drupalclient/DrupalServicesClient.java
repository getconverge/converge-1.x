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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.tika.Tika;

/**
 * API for communicating with Drupal Services. Example of using the client:  <code>
 *    DrupalServicesClient client = new DrupalServicesClient("http://mywebsite", "my_endpoint", "my_user", "my_password");
 *    if (client.login()) {
 *       // Logged in
 *    } else {
 *      // Incorrect username and/or password, or incorrect hostname and/or endpoint
 *    }
 * </code>
 *
 * @author Allan Lykke Christensen
 */
public class DrupalServicesClient {

    /**
     * Header for setting the Cross-script request forgery token.
     *
     * @since 1.1.11
     */
    private static final String HEADER_X_CSRF_TOKEN = "X-CSRF-Token";
    private static final Logger LOG = Logger.getLogger(DrupalServicesClient.class.getName());
    private static final String FORWARD_SLASH = "/";
    private static final String NODE_URL_PART = "/node";
    private static final String USER_LOGIN_URL_PART = "/user/login";
    private static final String USER_LOGOUT_URL_PART = "/user/logout";
    private String hostname;
    private String endpoint;
    private Integer connectionTimeout = 30000;
    private Integer socketTimeout = 30000;
    private String username;
    private String password;
    private HttpClient httpClient;
    private String sessionId = null;
    private String sessionName = null;
    private String csrfToken = null;

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     */
    public DrupalServicesClient() {
        this("", "", "", "");
    }

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     *
     * @param hostname Host name of the Drupal instance
     * @param endpoint Services endpoint to communicate with
     * @param username Username with privilege to access the endpoint
     * @param password Password matching the {@code username}
     */
    public DrupalServicesClient(String hostname, String endpoint, String username, String password) {
        this.hostname = hostname;
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     *
     * @param hostname Host name of the Drupal instance
     * @param endpoint Services endpoint to communicate with
     * @param username Username with privilege to access the endpoint
     * @param password Password matching the {@code username}
     * @param socketTimeout Socket timeout (ms)
     * @param connectionTimeout Connection timeout (ms)
     */
    public DrupalServicesClient(String hostname, String endpoint, String username, String password, Integer socketTimeout, Integer connectionTimeout) {
        this.hostname = hostname;
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Login to the define Drupal installation. Upon successful login the
     * session id is stored in {@link #getSessionId() } and the session name is
     * stored in {@link #getSessionName() }. The CSRF token is also obtained
     * from the server and used in subsequent requests.
     *
     * @return {@code true} if the login was successful, otherwise {@code false}
     * @throws DrupalServerConnectionException
     */
    public boolean login() throws DrupalServerConnectionException {
        try {
            URIBuilder builder = new URIBuilder(getHostnameWithEndpoint() + USER_LOGIN_URL_PART);

            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("username", this.username));
            values.add(new BasicNameValuePair("password", this.password));

            HttpPost method = new HttpPost(builder.build());
            method.setEntity(new UrlEncodedFormEntity(values, Consts.UTF_8));
            method.setHeader("Accept", "application/json");

            HttpResponse response = getHttpClient().execute(method);
            int statusCode = response.getStatusLine().getStatusCode();

            if (200 == statusCode) {
                StringWriter writer = new StringWriter();
                InputStream is = response.getEntity().getContent();
                IOUtils.copy(is, writer);
                EntityUtils.consume(response.getEntity());
                String jsonResponse = writer.toString();
                obtainSessionInfoFromJsonResponse(jsonResponse);
                obtainSessionToken();
                return true;
            } else {
                EntityUtils.consume(response.getEntity());
                // Examine the status code and determine if an exception should be thrown (e.g. 404 error)
                return false;
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
            throw new DrupalServerConnectionException("Could not login. " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not login. Server URI incorrect.", ex);
        }
    }

    /**
     * Logout from the current session.
     *
     * @throws DrupalServerConnectionException If the user could not be logged
     * out, e.g. already logged out
     */
    public void logout() throws DrupalServerConnectionException {
        try {
            HttpPost method = createHttpPost(getHostnameWithEndpoint() + USER_LOGOUT_URL_PART);
            ResponseHandler<String> handler = new BasicResponseHandler();
            getHttpClient().execute(method, handler);
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not logout.", ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not logout. URI incorrect", ex);
        }
    }

    /**
     * Determine if a given resource exists.
     *
     * @param resource Name of the resource as defined in the Drupal Services
     * module
     * @param id Unique identifier of the {@code resource}
     * @return {@code true} if the {@code resource} with the given {@code id}
     * exists, otherwise @code false}
     * @throws DrupalServerConnectionException If a connection to the server
     * could not be established
     */
    public boolean exists(String resource, Long id) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + FORWARD_SLASH + resource + FORWARD_SLASH + id;

        try {
            HttpGet method = createHttpGet(url);

            HttpResponse response = getHttpClient().execute(method);
            StatusLine statusLine = response.getStatusLine();

            EntityUtils.consume(response.getEntity());

            if (statusLine.getStatusCode() == 404) {
                return false;
            } else if (statusLine.getStatusCode() == 200) {
                return true;
            } else {
                throw new DrupalServerConnectionException("Unexpected response from Drupal server (" + url + "). " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
            }
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not determine if resource exists", ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not determine if resource exists. Server URI incorrect. ", ex);
        }
    }

    /**
     * Retrieve a Drupal node of a specific resource type.
     *
     * @param resource Type of resource
     * @param id Unique identifier of the resource
     * @return Drupal node matching the {@code resource} and {@code id}
     * @throws DrupalServerConnectionException If the resource type is incorrect
     * or unexpected response from server
     */
    public Long retrieveNodeIdFromResource(String resource, Long id) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + FORWARD_SLASH + resource + FORWARD_SLASH + id;
        try {
            URIBuilder builder = new URIBuilder(url);
            HttpGet method = new HttpGet(builder.build());

            ResponseHandler<String> handler = new BasicResponseHandler();
            String output = getHttpClient().execute(method, handler);
            NodeInfo ni = new Gson().fromJson(output, NodeInfo.class);
            return ni.getId();
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not retrieve node from Drupal server (" + url + "). " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not determine if resource exists. Server URI incorrect. ", ex);
        }
    }

    /**
     * Creates a new node.
     *
     * @param entity Entity containing the fields of the node
     * @return Response from server
     * @throws DrupalServerConnectionException If the node could not be created
     */
    public NodeInfo createNode(UrlEncodedFormEntity entity) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + NODE_URL_PART;
        try {
            HttpPost method = createHttpPost(url);
            method.setEntity(entity);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = getHttpClient().execute(method, handler);
            LOG.log(Level.FINEST, "Node created by Drupal: {0}", response);
            return new Gson().fromJson(response, NodeInfo.class);
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not create node on " + url + ". " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not create node on " + url + ". Invalid URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves a node with a given unique identifier.
     *
     * @param id Unique identifier of the node
     * @return Node matching the unique identifier
     * @throws DrupalServerConnectionException If a node could not be matched or
     * if an unexpected response was received from the server
     */
    public String retrieveNode(Long id) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + NODE_URL_PART + FORWARD_SLASH + id;
        try {
            URIBuilder builder = new URIBuilder(url);
            HttpGet method = new HttpGet(builder.build());

            ResponseHandler<String> handler = new BasicResponseHandler();

            String output = getHttpClient().execute(method, handler);
            return output;
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not retrieve node on " + url + ". " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not retrieve node on " + url + ". Invalid URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves a node path (the actual URL) with a given unique node
     * identifier.
     *
     * @param id Unique identifier of the node
     * @return Node path matching the unique identifier
     * @throws DrupalServerConnectionException If a node could not be matched or
     * if an unexpected response was received from the server
     */
    public String retrieveNodePath(Long id) throws DrupalServerConnectionException {
        String node = retrieveNode(id);
        LOG.log(Level.FINEST, "JSON object of Node #{0}: {1}", new Object[]{id, node});
        JsonParser parser = new JsonParser();
        JsonObject element = (JsonObject) parser.parse(node);
        JsonElement responseWrapper = element.get("path");
        return responseWrapper.getAsString();
    }

    /**
     * Updates an existing node.
     *
     * @param id Unique identifier of the node
     * @param entity Entity containing fields to update
     * @return Response from the server containing the Node ID and URI.
     * @throws DrupalServerConnectionException If the entity could not be
     * updated or an unexpected response from the server
     */
    public NodeInfo updateNode(Long id, UrlEncodedFormEntity entity) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + NODE_URL_PART + FORWARD_SLASH + id;
        try {
            HttpPut method = createHttpPut(url);
            method.setEntity(entity);

            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = getHttpClient().execute(method, handler);
            LOG.log(Level.FINEST, "Node updated by Drupal: {0}", response);
            return new Gson().fromJson(response, NodeInfo.class);
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not update node on " + url + ". " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not update node on " + url + ". Invalid URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes an existing node.
     *
     * @param resource Type of resource to delete, e.g. {@code node} or
     * {@code newsitem}
     * @param id Unique identifier of the resource to delete
     * @return {@code true} if the node was deleted otherwise {@code false}
     * @throws DrupalServerConnectionException If the delete URI was incorrect
     * or if an unexpected response was received from Drupal
     */
    public boolean delete(String resource, Long id) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + FORWARD_SLASH + resource + FORWARD_SLASH + id;
        try {
            HttpDelete method = createHttpDelete(url);
            HttpResponse response = getHttpClient().execute(method);
            StatusLine statusLine = response.getStatusLine();
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer);
            EntityUtils.consume(response.getEntity());

            if (200 == statusLine.getStatusCode()) {
                return true;
            } else {
                LOG.log(Level.WARNING, "{0} with id #{1} was not deleted. {2} {3}", new Object[]{resource, id, statusLine.getStatusCode(), statusLine.getReasonPhrase()});
                return false;
            }
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not delete node on " + url + ". " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not delete node on " + url + ". Invalid URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Attaches one or more files to an existing node.
     *
     * @param id Unique identifier of the node ({@code nid})
     * @param fieldName Name of the field used for storing files
     * @param files {@link List} of files to attach
     * @return Response from attaching the files
     * @throws DrupalServerConnectionException If an unexpected result is
     * returned from the Drupal service
     */
    public String attachFile(Long id, String fieldName, List<FileInfo> files) throws DrupalServerConnectionException {
        try {
            Tika tika = new Tika();

            MultipartEntity entity = new MultipartEntity();
            int i = 0;
            for (FileInfo file : files) {
                String title = file.getCaption();
                String mediaType = tika.detect(file.getFile());
                entity.addPart("files[" + i + "]", new FileBody(file.getFile(), mediaType));
                entity.addPart("field_values[" + i + "][title]", new StringBody(title));
                entity.addPart("field_values[" + i + "][alt]", new StringBody(title));
                i++;
            }
            entity.addPart("field_name", new StringBody(fieldName));
            entity.addPart("attach", new StringBody("0"));

            HttpPost method = createHttpPost(getHostnameWithEndpoint() + NODE_URL_PART + FORWARD_SLASH + id + "/attach_file");
            method.setEntity(entity);

            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = getHttpClient().execute(method, handler);
            LOG.log(Level.FINER, "Attach file response: {0}", response);
            return response;
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not attach files. " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not attach files. Invalud URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Removes all the files from an existing node.
     *
     * @param id Unique identifier of the node
     * @param fieldName Name of the field used for storing files
     * @return Response from removing all the files
     * @throws DrupalServerConnectionException If an unexpected result is
     * returned from the Drupal service
     */
    public String removeFiles(Long id, String fieldName) throws DrupalServerConnectionException {
        return attachFile(id, fieldName, new ArrayList<FileInfo>());
    }

    /**
     * Gets a {@link List} of the files attached to a node.
     *
     * @param id Unique identifier of the node
     * @return {@link List} of files attached to the given node
     * @throws DrupalServerConnectionException If the server return an
     * unexpected response
     */
    public List<DrupalFile> getNodeFiles(Long id) throws DrupalServerConnectionException {
        String url = getHostnameWithEndpoint() + NODE_URL_PART + FORWARD_SLASH + id + "/files/0";
        try {
            URIBuilder builder = new URIBuilder(url);
            HttpGet method = new HttpGet(builder.build());

            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = getHttpClient().execute(method, handler);

            return new Gson().fromJson(response, new TypeToken<List<DrupalFile>>() {
            }.getType());
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Could not get node files. " + ex.getMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Could not get node files. Invalud URI. " + ex.getMessage(), ex);
        }
    }

    /**
     * Gets the ID of the session with the Drupal instance.
     *
     * @return ID of the session with the specified Drupal instance. If no
     * session has been initiated {@code null} is returned.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the name of the session with the Drupal instance.
     *
     * @return Name of the session with the specified Drupal instance. If no
     * session has been initiated {@code null} is returned.
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * Gets the session cookie for authenticated communication with the Drupal
     * instance after login.
     *
     * @return Cookie to use to identify the authenticate session initiated upon
     * logging in
     */
    public String getSessionCookie() {
        return getSessionName() + "=" + getSessionId();
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    private HttpClient getHttpClient() {
        if (this.httpClient == null) {
            LOG.log(Level.FINER, "Creating an HttpClient");
            BasicHttpParams params = new BasicHttpParams();
            params.setParameter(AllClientPNames.CONNECTION_TIMEOUT, this.connectionTimeout)
                    .setParameter(AllClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH)
                    .setParameter(AllClientPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8)
                    .setParameter(AllClientPNames.SO_TIMEOUT, this.socketTimeout);
            this.httpClient = new DefaultHttpClient(params);
        }
        return this.httpClient;
    }

    /**
     * Gets the CSRF Session Token and store it in {@link #csrfToken}.
     *
     * @throws DrupalServerConnectionException If an invalid response was
     * received from the Drupal service
     */
    private void obtainSessionToken() throws DrupalServerConnectionException {
        try {
            URIBuilder builder = new URIBuilder(this.hostname + "/services/session/token");
            HttpGet method = new HttpGet(builder.build());
            HttpResponse response = getHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DrupalServerConnectionException("Unexpected response (" + response.getStatusLine().getStatusCode() + ") from token service");
            } else {
                StringWriter writer = new StringWriter();
                InputStream is = response.getEntity().getContent();
                IOUtils.copy(is, writer);
                EntityUtils.consume(response.getEntity());
                this.csrfToken = writer.toString();
            }

        } catch (URISyntaxException ex) {
            throw new DrupalServerConnectionException("Incorrect URI: " + ex.getMessage());
        } catch (IOException ex) {
            throw new DrupalServerConnectionException("Unexpected response (" + ex.getMessage() + ") from token service");
        }
    }

    private void obtainSessionInfoFromJsonResponse(String jsonResponse) throws DrupalServerConnectionException {
        LOG.log(Level.FINEST, jsonResponse);
        JsonParser parser = new JsonParser();
        try {
            JsonObject obj = (JsonObject) parser.parse(jsonResponse);
            this.sessionId = obj.get("sessid").getAsString();
            this.sessionName = obj.get("session_name").getAsString();
        } catch (JsonSyntaxException ex) {
            throw new DrupalServerConnectionException("Unknown JSON response. " + jsonResponse, ex);
        } catch (NullPointerException ex) {
            throw new DrupalServerConnectionException("sessid or session_name missing in JSON response", ex);
        }
    }

    private HttpPost createHttpPost(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpPost method = new HttpPost(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private HttpGet createHttpGet(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpGet method = new HttpGet(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private HttpPut createHttpPut(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpPut method = new HttpPut(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private HttpDelete createHttpDelete(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpDelete method = new HttpDelete(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private String getHostnameWithEndpoint() {
        return this.hostname + FORWARD_SLASH + this.endpoint;
    }

    @Override
    protected void finalize() throws Throwable {
        getHttpClient().getConnectionManager().shutdown();
        super.finalize();
    }

}

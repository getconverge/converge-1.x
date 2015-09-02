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

import dk.i2m.drupal.services.converters.EntityConverter;
import dk.i2m.drupal.services.entities.NodeEntity;
import dk.i2m.drupal.services.entities.SessionEntity;
import dk.i2m.drupal.services.entities.UserEntity;
import dk.i2m.drupal.services.interceptors.SessionInterceptor;
import dk.i2m.drupal.services.resources.NodeResource;
import dk.i2m.drupal.services.resources.UserResource;
import retrofit.RestAdapter;

import java.util.List;
import java.util.Map;

/**
 * Drupal Services client for uploading editions to Drupal.
 */
public class DrupalServicesClient {

    public static final String ALIAS_NODE = "node";
    public static final String ALIAS_USER = "user";

    private SessionInterceptor interceptor = new SessionInterceptor();
    private UserEntity userEntity = new UserEntity();
    private UserResource userResource;
    private NodeResource nodeResource;

    private String nodeAlias = ALIAS_NODE;
    private String userAlias = ALIAS_USER;

    public DrupalServicesClient(String endpoint, String username, String password) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setRequestInterceptor(interceptor)
                .build();

        userResource = restAdapter.create(UserResource.class);
        nodeResource = restAdapter.create(NodeResource.class);
        userEntity.setName(username);
        userEntity.setPass(password);
    }

    /**
     * Login to the defined Drupal instance. Upon successful loginUser, the session
     * and CSRF token are stored and used in subsequent requests.
     */
    public void loginUser() {
        if (interceptor.getSessionId() == null) {
            SessionEntity sessionEntity = userResource.login(userAlias,
                    new EntityConverter<UserEntity>().convert(userEntity));
            // Store the session and CSRF token
            interceptor.setSessionId(sessionEntity.getId());
            interceptor.setSessionName(sessionEntity.getName());
            interceptor.setCsrfToken(sessionEntity.getCsrf());
        }
    }

    /**
     * Logout from the current session.
     */
    public void logoutUser() {
        if (interceptor.getSessionId() != null) {
            userResource.logout(userAlias, "");
            // Clear the session and CSRF token
            interceptor.setSessionId(null);
            interceptor.setSessionName(null);
            interceptor.setCsrfToken(null);
        }
    }

    /**
     * Create a NewsItem on Drupal
     *
     * @param fields
     * @return The created entity
     */
    public NodeEntity createNode(Map<String, String> fields) {
        return nodeResource.create(nodeAlias, fields);
    }

    /**
     * Update a NewsItem on Drupal
     *
     * @param id
     * @param fields
     * @return The updated entity
     */
    public NodeEntity updateNode(Long id, Map<String, String> fields) {
        return nodeResource.update(nodeAlias, id, fields);
    }

    /**
     * Determine if the NewsItem is already on Drupal.
     *
     * @param options
     * @return Found entities
     */
    public List<NodeEntity> indexNode(Map<String, String> options) {
        return nodeResource.index(nodeAlias, options);
    }

    /**
     * Attach files to a node on Drupal.
     *
     * @param nodeEntity
     * @param field
     * @param params     Denotes name and value parts of a multi-part request
     */
    public void attachFiles(NodeEntity nodeEntity, String field, Map<String, Object> params) {
        if (!params.isEmpty()) {
            nodeResource.attachFile(nodeAlias, nodeEntity.getId(), field, 0, params);
        }
    }

    public void setNodeAlias(String nodeAlias) {
        if (nodeAlias != null) {
            this.nodeAlias = nodeAlias;
        }

    }

    public void setUserAlias(String userAlias) {
        if (userAlias != null) {
            this.userAlias = userAlias;
        }
    }
}

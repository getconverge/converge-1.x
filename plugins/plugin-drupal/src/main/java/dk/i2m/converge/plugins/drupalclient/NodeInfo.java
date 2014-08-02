/*
 * Copyright (C) 2012 Interactive Media Management
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

import com.google.gson.annotations.SerializedName;

/**
 * Model class responsible for containing identification details about a Drupal
 * node.
 *
 * @author Allan Lykke Christensen
 */
public class NodeInfo {

    @SerializedName("nid")
    private Long id;
    @SerializedName("uri")
    private String uri;

    /**
     * Creates a new instance of {@link NodeInfo}.
     */
    public NodeInfo() {
        this(0L, "");
    }

    /**
     * Creates a new instance of {@link NodeInfo} with the {@code id} and
     * {@code uri} preset.
     *
     * @param id Unique identifier of the node in Drupal
     * @param uri URI of the node
     */
    public NodeInfo(Long id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    /**
     * Unique identifier of the node in Drupal.
     *
     * @return Unique identifier of the node in Drupal
     */
    public Long getId() {
        return id;
    }

    /**
     * Unique identifier of the node in Drupal.
     *
     * @param id Unique identifier of the node in Drupal
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the URI of the node in Drupal. This can be used to linking directly
     * to the node.
     *
     * @return URI of the node in Drupal
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the URI of the node in Drupal.
     *
     * @param uri URI of the node in Drupal
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
}

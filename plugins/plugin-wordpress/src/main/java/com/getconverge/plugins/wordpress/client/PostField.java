/*
 * Copyright (C) 2015 Allan Lykke Christensen
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
package com.getconverge.plugins.wordpress.client;

/**
 * Fields of a Wordpress post.
 *
 * @author Allan Lykke Christensen
 */
public enum PostField {

    /**
     * ID of the author of the post.
     */
    POST_AUTHOR,
    /**
     * Title of the post.
     */
    POST_TITLE,
    /**
     * Contents of the post.
     */
    POST_CONTENT,
    /**
     * Excerpt of the post.
     */
    POST_EXCERPT,
    /**
     * Date of the post.
     */
    POST_DATE,
    /**
     * {@link PostStatus status} of the post.
     */
    POST_STATUS,
    /**
     * Terms of the post expressed as names, used for setting terms of a new or
     * modified post.
     */
    TERMS_NAMES,
    /**
     * Terms of the post, used for getting the terms of an existing post.
     */
    TERMS, 
    /**
     * Media item id of the thumbnail (aka Feature Image) of the post.
     */
    POST_THUMBNAIL;

    /**
     * Returns a {@link String} representation of the {@link PostField}. The
     * {@link String} representation is a lower case version of the enumeration
     * name.
     *
     * @return Lower case version of the enumeration name
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

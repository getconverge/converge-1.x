/*
 * Copyright (C) 2009 - 2011 Interactive Media Management
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
package dk.i2m.converge.core;

import java.io.Serializable;

/**
 * Keys of configuration entries.
 *
 * @author Allan Lykke Christensen
 */
public enum ConfigurationKey implements Serializable {

    INVALID,
    VERSION,
    BUILD_TIME,
    APPLICATION_NEWSFEED,
    WORKING_DIRECTORY,
    MESSAGE_BUNDLE,
    XML_MESSAGE_PACKAGE,
    LANGUAGE,
    COUNTRY,
    TIME_ZONE,
    NEWSWIRE_INTERVAL,
    NEWSWIRE_PURGE_INTERVAL,
    EDITION_INTERVAL,
    CATALOGUE_WATCH_INTERVAL,
    SEARCH_ENGINE_INDEXING_INTERVAL,
    SEARCH_ENGINE_URL,
    SEARCH_ENGINE_NEWSWIRE_URL,
    SEARCH_ENGINE_SOCKET_TIMEOUT,
    SEARCH_ENGINE_CONNECTION_TIMEOUT,
    SEARCH_ENGINE_FOLLOW_REDIRECTS,
    SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS_PER_HOST,
    SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS,
    SEARCH_ENGINE_MAX_RETRIES,
    LDAP_GROUP_USERS,
    LDAP_GROUP_ADMINISTRATORS,
    LDAP_GROUP_MAPPING_NAME,
    LDAP_GROUP_MAPPING_MEMBEROF,
    LDAP_USER_MAPPING_USERNAME,
    LDAP_USER_MAPPING_FIRST_NAME,
    LDAP_USER_MAPPING_LAST_NAME,
    LDAP_USER_MAPPING_COMMON_NAME,
    LDAP_USER_MAPPING_JOB_TITLE,
    LDAP_USER_MAPPING_MOBILE,
    LDAP_USER_MAPPING_PHONE,
    LDAP_USER_MAPPING_EMAIL,
    LDAP_USER_MAPPING_LANGUAGE,
    LDAP_USER_MAPPING_EMPLOYEE_TYPE,
    LDAP_USER_MAPPING_FEE_TYPE,
    LDAP_USER_MAPPING_JPEG_PHOTO,
    LDAP_USER_MAPPING_ORGANISATION,
    LDAP_FEE_TYPE_MAPPING_STORY,
    LDAP_FEE_TYPE_MAPPING_WORD,
    LDAP_FEE_TYPE_MAPPING_FIXED,
    LDAP_EMPLOYMENT_TYPE_MAPPING_PERMANENT,
    LDAP_EMPLOYMENT_TYPE_MAPPING_FREELANCE,
    LDAP_CONNECTION_FACTORY,
    LDAP_CONNECT_TIMEOUT,
    LDAP_READ_TIMEOUT,
    LDAP_PROVIDER_URL,
    LDAP_BASE,
    LDAP_SECURITY_PRINCIPAL,
    LDAP_SECURITY_CREDENTIALS,
    LDAP_SECURITY_AUTHENTICATION,
    NEWSWIRE_BASKET_INTERVAL,
    NEWSWIRE_BASKET_MAIL,
    CONVERGE_HOME_URL,
    SEARCH_ENGINE_ALLOW_COMPRESSION,
    OPEN_CALAIS_API_KEY,
    AUTO_SAVE_INTERVAL
}

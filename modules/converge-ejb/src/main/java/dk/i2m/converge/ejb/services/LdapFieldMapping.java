/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

/**
 * Field mapping identifiers.
 *
 * @author Allan Lykke Christensen
 */
public enum LdapFieldMapping {

    USER_MAPPING_USERNAME,
    USER_MAPPING_EMAIL,
    USER_MAPPING_COMMON_NAME,
    USER_MAPPING_FIRST_NAME,
    USER_MAPPING_LAST_NAME,
    USER_MAPPING_JOB_TITLE,
    USER_MAPPING_MOBILE,
    USER_MAPPING_ORGANISATION,
    USER_MAPPING_PHONE,
    USER_MAPPING_LANGUAGE,
    USER_MAPPING_JPEG_PHOTO,
    USER_MAPPING_EMPLOYMENT_TYPE,
    EMPLOYMENT_TYPE_MAPPING_FREELANCE,
    EMPLOYMENT_TYPE_MAPPING_PERMANENT,
    USER_MAPPING_FEE_TYPE,
    FEE_TYPE_MAPPING_STORY,
    FEE_TYPE_MAPPING_FIXED,
    FEE_TYPE_MAPPING_WORD,
    GROUP_MAPPING_NAME,
    GROUP_MAPPING_MEMBEROF;
}

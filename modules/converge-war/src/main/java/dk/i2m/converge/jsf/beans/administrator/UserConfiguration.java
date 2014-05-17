/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.jsf.JsfUtils;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 * Backing bean for {@code /administrator/UserConfiguration.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class UserConfiguration {

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private SystemFacadeLocal systemFacade;

    private String ldapConnectionFactory;

    private String ldapProviderUrl;

    private String ldapConnectTimeout;

    private String ldapReadTimeout;

    private String ldapSecurityPrincipal;

    private String ldapSecurityCredentials;

    private String ldapSecurityAuthentication;

    private String ldapBase;

    private String ldapGroupUsers;

    private String ldapGroupAdministrators;

    private String ldapUserMappingUsername;

    private String ldapUserMappingFirstName;

    private String ldapUserMappingLastName;

    private String ldapUserMappingCommonName;

    private String ldapUserMappingJobTitle;

    private String ldapUserMappingOrganisation;

    private String ldapUserMappingMobile;

    private String ldapUserMappingPhone;

    private String ldapUserMappingEmail;

    private String ldapUserMappingLanguage;

    private String ldapUserMappingJpegPhoto;

    private String ldapGroupMappingName;

    private String ldapGroupMappingMemberOf;

    @PostConstruct
    public void onInit() {
        setLdapBase(systemFacade.getProperty(ConfigurationKey.LDAP_BASE));
        setLdapConnectTimeout(systemFacade.getProperty(
                ConfigurationKey.LDAP_CONNECT_TIMEOUT));
        setLdapConnectionFactory(systemFacade.getProperty(
                ConfigurationKey.LDAP_CONNECTION_FACTORY));
        setLdapGroupAdministrators(systemFacade.getProperty(
                ConfigurationKey.LDAP_GROUP_ADMINISTRATORS));
        setLdapGroupMappingMemberOf(systemFacade.getProperty(
                ConfigurationKey.LDAP_GROUP_MAPPING_MEMBEROF));
        setLdapGroupMappingName(systemFacade.getProperty(
                ConfigurationKey.LDAP_GROUP_MAPPING_NAME));
        setLdapGroupUsers(systemFacade.getProperty(
                ConfigurationKey.LDAP_GROUP_USERS));
        setLdapProviderUrl(systemFacade.getProperty(
                ConfigurationKey.LDAP_PROVIDER_URL));
        setLdapReadTimeout(systemFacade.getProperty(
                ConfigurationKey.LDAP_READ_TIMEOUT));
        setLdapSecurityAuthentication(systemFacade.getProperty(
                ConfigurationKey.LDAP_SECURITY_AUTHENTICATION));
        setLdapSecurityCredentials(systemFacade.getProperty(
                ConfigurationKey.LDAP_SECURITY_CREDENTIALS));
        setLdapSecurityPrincipal(systemFacade.getProperty(
                ConfigurationKey.LDAP_SECURITY_PRINCIPAL));
        setLdapUserMappingCommonName(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_COMMON_NAME));
        setLdapUserMappingEmail(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_EMAIL));
        setLdapUserMappingFirstName(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_FIRST_NAME));
        setLdapUserMappingJobTitle(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_JOB_TITLE));
        setLdapUserMappingJpegPhoto(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_JPEG_PHOTO));
        setLdapUserMappingLanguage(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_LANGUAGE));
        setLdapUserMappingLastName(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_LAST_NAME));
        setLdapUserMappingMobile(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_MOBILE));
        setLdapUserMappingOrganisation(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_ORGANISATION));
        setLdapUserMappingPhone(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_PHONE));
        setLdapUserMappingUsername(systemFacade.getProperty(
                ConfigurationKey.LDAP_USER_MAPPING_USERNAME));
    }

    public void onSaveLdapSettings(ActionEvent event) {
        cfgService.set(ConfigurationKey.LDAP_BASE, ldapBase);
        cfgService.set(ConfigurationKey.LDAP_CONNECT_TIMEOUT, ldapConnectTimeout);
        cfgService.set(ConfigurationKey.LDAP_CONNECTION_FACTORY,
                ldapConnectionFactory);
        cfgService.set(ConfigurationKey.LDAP_GROUP_ADMINISTRATORS,
                ldapGroupAdministrators);
        cfgService.set(ConfigurationKey.LDAP_GROUP_USERS, ldapGroupUsers);
        cfgService.set(ConfigurationKey.LDAP_PROVIDER_URL, ldapProviderUrl);
        cfgService.set(ConfigurationKey.LDAP_READ_TIMEOUT, ldapReadTimeout);
        cfgService.set(ConfigurationKey.LDAP_SECURITY_AUTHENTICATION,
                ldapSecurityAuthentication);
        cfgService.set(ConfigurationKey.LDAP_SECURITY_CREDENTIALS,
                ldapSecurityCredentials);
        cfgService.set(ConfigurationKey.LDAP_SECURITY_PRINCIPAL,
                ldapSecurityPrincipal);

        JsfUtils.createMessage("frmUserConfiguration",
                FacesMessage.SEVERITY_INFO, "i18n",
                "administrator_UserConfiguration_LDAP_SETTINGS_SAVED", null);
    }

    public void onSaveLdapFieldMappings(ActionEvent event) {
        cfgService.set(ConfigurationKey.LDAP_GROUP_MAPPING_MEMBEROF,
                ldapGroupMappingMemberOf);
        cfgService.set(ConfigurationKey.LDAP_GROUP_MAPPING_NAME,
                ldapGroupMappingName);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_COMMON_NAME,
                ldapUserMappingCommonName);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_EMAIL,
                ldapUserMappingEmail);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_FIRST_NAME,
                ldapUserMappingFirstName);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_JOB_TITLE,
                ldapUserMappingJobTitle);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_JPEG_PHOTO,
                ldapUserMappingJpegPhoto);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_LANGUAGE,
                ldapUserMappingLanguage);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_LAST_NAME,
                ldapUserMappingLastName);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_MOBILE,
                ldapUserMappingMobile);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_ORGANISATION,
                ldapUserMappingOrganisation);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_PHONE,
                ldapUserMappingPhone);
        cfgService.set(ConfigurationKey.LDAP_USER_MAPPING_USERNAME,
                ldapUserMappingUsername);
        JsfUtils.createMessage("frmUserConfiguration",
                FacesMessage.SEVERITY_INFO, "i18n",
                "administrator_UserConfiguration_LDAP_FIELD_MAPPINGS_SAVED",
                null);
    }

    public String getLdapBase() {
        return ldapBase;
    }

    public void setLdapBase(String ldapBase) {
        this.ldapBase = ldapBase;
    }

    public String getLdapConnectTimeout() {
        return ldapConnectTimeout;
    }

    public void setLdapConnectTimeout(String ldapConnectTimeout) {
        this.ldapConnectTimeout = ldapConnectTimeout;
    }

    public String getLdapConnectionFactory() {
        return ldapConnectionFactory;
    }

    public void setLdapConnectionFactory(String ldapConnectionFactory) {
        this.ldapConnectionFactory = ldapConnectionFactory;
    }

    public String getLdapGroupAdministrators() {
        return ldapGroupAdministrators;
    }

    public void setLdapGroupAdministrators(String ldapGroupAdministrators) {
        this.ldapGroupAdministrators = ldapGroupAdministrators;
    }

    public String getLdapGroupMappingMemberOf() {
        return ldapGroupMappingMemberOf;
    }

    public void setLdapGroupMappingMemberOf(String ldapGroupMappingMemberOf) {
        this.ldapGroupMappingMemberOf = ldapGroupMappingMemberOf;
    }

    public String getLdapGroupMappingName() {
        return ldapGroupMappingName;
    }

    public void setLdapGroupMappingName(String ldapGroupMappingName) {
        this.ldapGroupMappingName = ldapGroupMappingName;
    }

    public String getLdapGroupUsers() {
        return ldapGroupUsers;
    }

    public void setLdapGroupUsers(String ldapGroupUsers) {
        this.ldapGroupUsers = ldapGroupUsers;
    }

    public String getLdapProviderUrl() {
        return ldapProviderUrl;
    }

    public void setLdapProviderUrl(String ldapProviderUrl) {
        this.ldapProviderUrl = ldapProviderUrl;
    }

    public String getLdapReadTimeout() {
        return ldapReadTimeout;
    }

    public void setLdapReadTimeout(String ldapReadTimeout) {
        this.ldapReadTimeout = ldapReadTimeout;
    }

    public String getLdapSecurityAuthentication() {
        return ldapSecurityAuthentication;
    }

    public void setLdapSecurityAuthentication(String ldapSecurityAuthentication) {
        this.ldapSecurityAuthentication = ldapSecurityAuthentication;
    }

    public String getLdapSecurityCredentials() {
        return ldapSecurityCredentials;
    }

    public void setLdapSecurityCredentials(String ldapSecurityCredentials) {
        this.ldapSecurityCredentials = ldapSecurityCredentials;
    }

    public String getLdapSecurityPrincipal() {
        return ldapSecurityPrincipal;
    }

    public void setLdapSecurityPrincipal(String ldapSecurityPrincipal) {
        this.ldapSecurityPrincipal = ldapSecurityPrincipal;
    }

    public String getLdapUserMappingCommonName() {
        return ldapUserMappingCommonName;
    }

    public void setLdapUserMappingCommonName(String ldapUserMappingCommonName) {
        this.ldapUserMappingCommonName = ldapUserMappingCommonName;
    }

    public String getLdapUserMappingEmail() {
        return ldapUserMappingEmail;
    }

    public void setLdapUserMappingEmail(String ldapUserMappingEmail) {
        this.ldapUserMappingEmail = ldapUserMappingEmail;
    }

    public String getLdapUserMappingFirstName() {
        return ldapUserMappingFirstName;
    }

    public void setLdapUserMappingFirstName(String ldapUserMappingFirstName) {
        this.ldapUserMappingFirstName = ldapUserMappingFirstName;
    }

    public String getLdapUserMappingJobTitle() {
        return ldapUserMappingJobTitle;
    }

    public void setLdapUserMappingJobTitle(String ldapUserMappingJobTitle) {
        this.ldapUserMappingJobTitle = ldapUserMappingJobTitle;
    }

    public String getLdapUserMappingJpegPhoto() {
        return ldapUserMappingJpegPhoto;
    }

    public void setLdapUserMappingJpegPhoto(String ldapUserMappingJpegPhoto) {
        this.ldapUserMappingJpegPhoto = ldapUserMappingJpegPhoto;
    }

    public String getLdapUserMappingLanguage() {
        return ldapUserMappingLanguage;
    }

    public void setLdapUserMappingLanguage(String ldapUserMappingLanguage) {
        this.ldapUserMappingLanguage = ldapUserMappingLanguage;
    }

    public String getLdapUserMappingLastName() {
        return ldapUserMappingLastName;
    }

    public void setLdapUserMappingLastName(String ldapUserMappingLastName) {
        this.ldapUserMappingLastName = ldapUserMappingLastName;
    }

    public String getLdapUserMappingMobile() {
        return ldapUserMappingMobile;
    }

    public void setLdapUserMappingMobile(String ldapUserMappingMobile) {
        this.ldapUserMappingMobile = ldapUserMappingMobile;
    }

    public String getLdapUserMappingOrganisation() {
        return ldapUserMappingOrganisation;
    }

    public void setLdapUserMappingOrganisation(
            String ldapUserMappingOrganisation) {
        this.ldapUserMappingOrganisation = ldapUserMappingOrganisation;
    }

    public String getLdapUserMappingPhone() {
        return ldapUserMappingPhone;
    }

    public void setLdapUserMappingPhone(String ldapUserMappingPhone) {
        this.ldapUserMappingPhone = ldapUserMappingPhone;
    }

    public String getLdapUserMappingUsername() {
        return ldapUserMappingUsername;
    }

    public void setLdapUserMappingUsername(String ldapUserMappingUsername) {
        this.ldapUserMappingUsername = ldapUserMappingUsername;
    }
}

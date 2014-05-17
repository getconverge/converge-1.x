/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.security.*;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.core.workflow.Department;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.jndi.ldap.LdapUtils;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import org.apache.commons.lang.StringUtils;

/**
 * Stateless session bean providing a service for managing users.
 *
 * @author Allan Lykke Christensen
 */
@Stateless(name = "UserServiceBean", mappedName = "ejb/UserServiceBean")
public class UserServiceBean implements UserServiceLocal {

    private static final Logger LOG = Logger.getLogger(UserServiceBean.class.
            getName());

    @EJB private DaoServiceLocal daoService;

    @EJB private ConfigurationServiceLocal cfgService;

    /** Identifier (user id) of an anonymous user. */
    private final String ANONYMOUS_USER = "ANONYMOUS";

    private SearchControls sc = new SearchControls();

    private String groupOfUsers = "";

    private String groupOfAdministrators = "";

    private Map<String, String> fieldMapping = new HashMap<String, String>();

    @PostConstruct
    private void startup() {
        setupUserMapping();
    }

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> getMembers(Long departmentId) {
        List<UserAccount> members = new ArrayList<UserAccount>();
        try {
            Department department = daoService.findById(Department.class,
                    departmentId);
            members = department.getUserAccounts();
        } catch (DataNotFoundException ex) {
            LOG.log(Level.FINE, "Invalid Department", ex);
        }
        return members;
    }

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> getMembers(Long outletId, SystemPrivilege privilege) {
        List<UserAccount> members = new ArrayList<UserAccount>();
        try {
            Outlet outlet = daoService.findById(Outlet.class, outletId);

            for (UserRole userRole : outlet.getRoles()) {
                for (Privilege p : userRole.getPrivileges()) {
                    if (p.getId().equals(privilege)) {
                        members.addAll(userRole.getUserAccounts());
                    }
                }
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.FINE, "Invalid Outlet", ex);
        }

        // Remove duplicates
        Set set = new HashSet(members);
        members = new ArrayList(set);

        return members;
    }

    /** {@inheritDoc } */
    @Override
    public List<UserAccount> getRoleMembers(Long roleId) {
        try {
            UserRole userRole = daoService.findById(UserRole.class, roleId);
            return userRole.getUserAccounts();
        } catch (DataNotFoundException ex) {
            LOG.log(Level.FINE, "", ex);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<UserAccount> getDirectoryMembers() throws NamingException {
        return getMembers(
                cfgService.getString(ConfigurationKey.LDAP_GROUP_USERS));
    }

    /** {@inheritDoc } */
    @Override
    public List<UserAccount> getMembers(String groupDn) throws NamingException {
        // Field containing the user unique identifier
        String uid = getFieldMapping(LdapFieldMapping.USER_MAPPING_USERNAME);
        // Field containinf the "member of" a group
        String memberOf = getFieldMapping(
                LdapFieldMapping.GROUP_MAPPING_MEMBEROF);

        List<UserAccount> members = new LinkedList<UserAccount>();

        // Set up attributes to search for
        String[] searchAttributes = new String[1];
        searchAttributes[0] = memberOf;

        DirContext dirCtx = getDirectoryConnection();

        // Get all the memberOf attributes of the "group of users"
        Attributes groupAttrs = dirCtx.getAttributes(groupDn, searchAttributes);
        if (groupAttrs != null) {

            Attribute memberAttrs = groupAttrs.get(memberOf);
            if (memberAttrs != null) {

                NamingEnumeration vals = memberAttrs.getAll();
                while (vals.hasMoreElements()) {
                    UserAccount ua;

                    // Get the DN of the next group member
                    String userDn = (String) vals.nextElement();

                    // Get all the attributes of the given user
                    try {
                        Attributes attrs = dirCtx.getAttributes(userDn);

                        // Get the UID of the user
                        String username = (String) attrs.get(uid).get();

                        // Look up user by UID in local database
                        Map<String, Object> params = QueryBuilder.with(
                                "username", username).parameters();
                        List<UserAccount> result =
                                daoService.findWithNamedQuery(
                                UserAccount.FIND_BY_UID, params);

                        if (result.isEmpty()) {
                            LOG.log(Level.FINE,
                                    "User {0} has not been setup in the local database, using information from directory",
                                    username);
                            ua = new UserAccount();
                        } else {
                            ua = result.iterator().next();
                        }

                        // Add LDAP directory attributes to user
                        updateUser(ua, attrs);
                        ua.setDistinguishedName(userDn);

                        // Add to results list
                        members.add(ua);
                    } catch (NamingException ex) {
                        LOG.log(Level.WARNING, "User {0} does not exist in LDAP",
                                userDn);
                    }
                }
            }
        } else {
            LOG.log(Level.SEVERE, "Couldn't find search attributes ({0}) in {1}",
                    new Object[]{memberOf, groupOfUsers});
        }
        closeDirectoryConnection(dirCtx);

        Collections.sort(members, new BeanComparator("fullName"));

        return members;
    }

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> findAll() {
        return daoService.findAll(UserAccount.class);
    }

    /**
     * Determines if a given user exists in the LDAP directory.
     *
     * @param id
* Unique identifier of the user
     * @return <
     * code>true</code> if the {@link UserAccount} exists in the LDAP
     * directory, otherwise
     * <code>false</code>
     */
    @Override
    public boolean exists(String id) {
        this.sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String uid = getFieldMapping(LdapFieldMapping.USER_MAPPING_USERNAME);

        String filter = "(" + uid + "=" + id + ")";

        boolean exists = false;
        try {
            DirContext dirCtx = getDirectoryConnection();
            NamingEnumeration results = dirCtx.search(this.groupOfUsers, filter,
                    this.sc);
            exists = results.hasMore();
            closeDirectoryConnection(dirCtx);
        } catch (NamingException e) {
            LOG.log(Level.WARNING, "", e);
        }

        return exists;
    }

    /** {@inheritDoc} */
    @Override
    public UserAccount syncWithDirectory(UserAccount userAccount) throws
            UserNotFoundException, DirectoryException {
        if (userAccount == null) {
            throw new UserNotFoundException(
                    "null user account passed to synchronisation");
        }

        LOG.log(Level.INFO, "Synchronising [{0}/{1}/{2}] with directory",
                new Object[]{userAccount.getUsername(), userAccount.
                    getDistinguishedName(), userAccount.getId()});
        boolean found = false;
        this.sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String id = userAccount.getUsername();
        String uid = getFieldMapping(LdapFieldMapping.USER_MAPPING_USERNAME);

        DirContext dirCtx = null;
        try {
            String base = cfgService.getString(ConfigurationKey.LDAP_BASE);
            dirCtx = getDirectoryConnection();
            NamingEnumeration results = dirCtx.search(base, "({0}={1})",
                    new Object[]{uid, id}, this.sc);

            if (results.hasMoreElements() && !found) {
                SearchResult sr = (SearchResult) results.next();

                updateUser(userAccount, sr.getAttributes());
                userAccount.setDistinguishedName(sr.getNameInNamespace());
                found = true;
            }

            closeDirectoryConnection(dirCtx);
        } catch (CommunicationException e) {
            closeDirectoryConnection(dirCtx);
            throw new DirectoryException("Could not connect to directory", e);
        } catch (NamingException e) {
            closeDirectoryConnection(dirCtx);
            throw new DirectoryException("Could not connect to directory", e);
        }


        if (!found) {
            throw new UserNotFoundException("User [" + id
                    + "] was not found in directory");
        }

        return userAccount;
    }

    /**
     * Finds a {@link UserAccount} by its unique identifier.
     *
     * @param id Unique identifier of the {@link UserAccount}
     * @return {@link UserAccount} matching the unique identifier
     * @throws UserNotFoundException If a {@link UserAccount} could not found with the given
     * <code>id</code>
     * @throws DirectoryException    If communication with the user directory failed
     */
    @Override
    public UserAccount findById(String id) throws UserNotFoundException,
            DirectoryException {

        if (ANONYMOUS_USER.equalsIgnoreCase(id)) {
            throw new UserNotFoundException(id + " is not a valid user");
        }

        UserAccount ua;

        List<UserAccount> matches = daoService.findWithNamedQuery(
                UserAccount.FIND_BY_UID, QueryBuilder.with("username", id).
                parameters());

        if (matches.size() == 1) {
            return ua = matches.iterator().next();
        } else {
            LOG.log(Level.FINE, "No user account found for {0} in database", id);
            ua = new UserAccount();
            ua.setUsername(id);
            ua.setTimeZoneAsString(cfgService.getString(
                    ConfigurationKey.TIME_ZONE));
            try {
                ua = syncWithDirectory(ua);
                LOG.log(Level.FINE, "Creating user account in database for {0}",
                        id);
                return daoService.create(ua);
            } catch (Exception ex) {
                throw new UserNotFoundException("User [" + id
                        + "] was not found in directory nor database", ex);
            }
        }
    }

    /** {@inheritDoc } */
    @Override
    public UserAccount update(UserAccount user) {
        return daoService.update(user);
    }

    /**
     * Sets the properties of a {@link UserAccount} based on a
     * {@link SearchResult} from the directory. The process will validate each
     * {@link Attribute} to ensure no
     * <code>null</code> values or exceptions
     * occur.
     *
     * @param user {@link UserAccount} to update
     * @param sr   {@link SearchResult} containing the user information
     */
    private void updateUser(UserAccount user, final Attributes attrs) {

        String fieldUid =
                getFieldMapping(LdapFieldMapping.USER_MAPPING_USERNAME);
        String fieldMail = getFieldMapping(LdapFieldMapping.USER_MAPPING_EMAIL);
        String fieldCommonName = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_COMMON_NAME);
        String fieldGivenName = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_FIRST_NAME);
        String fieldSurname = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_LAST_NAME);
        String fieldJobTitle = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_JOB_TITLE);
        String fieldMobile = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_MOBILE);
        String fieldOrganisation = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_ORGANISATION);
        String fieldPhone = getFieldMapping(LdapFieldMapping.USER_MAPPING_PHONE);
        String fieldLanguage = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_LANGUAGE);
        String fieldPhoto = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_JPEG_PHOTO);
        String fieldEmploymentType = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_EMPLOYMENT_TYPE);
        String employmentTypeFreelance = getFieldMapping(
                LdapFieldMapping.EMPLOYMENT_TYPE_MAPPING_FREELANCE);
        String employmentTypePermanent = getFieldMapping(
                LdapFieldMapping.EMPLOYMENT_TYPE_MAPPING_PERMANENT);
        String fieldFeeType = getFieldMapping(
                LdapFieldMapping.USER_MAPPING_FEE_TYPE);
        String feeTypeStory = getFieldMapping(
                LdapFieldMapping.FEE_TYPE_MAPPING_STORY);
        String feeTypeFixed = getFieldMapping(
                LdapFieldMapping.FEE_TYPE_MAPPING_FIXED);
        String feeTypeWord = getFieldMapping(
                LdapFieldMapping.FEE_TYPE_MAPPING_WORD);

        user.setUsername(LdapUtils.validateAttribute(attrs.get(fieldUid)));
        user.setEmail(LdapUtils.validateAttribute(attrs.get(fieldMail)));
        user.setFullName(LdapUtils.validateAttribute(attrs.get(fieldCommonName)));
        user.setGivenName(LdapUtils.validateAttribute(attrs.get(fieldGivenName)));
        user.setJobTitle(LdapUtils.validateAttribute(attrs.get(fieldJobTitle)));
        user.setMobile(LdapUtils.validateAttribute(attrs.get(fieldMobile)));
        user.setOrganisation(LdapUtils.validateAttribute(attrs.get(
                fieldOrganisation)));
        user.setPhone(LdapUtils.validateAttribute(attrs.get(fieldPhone)));

        // If the user does not have a preferred language, get the one specified
        // in the LDAP directory
        if (StringUtils.isBlank(user.getPreferredLanguage())) {
            user.setPreferredLanguage(LdapUtils.validateAttribute(attrs.get(
                    fieldLanguage)));
        }

        // If no preferred language was specified in the profile nor in the LDAP
        // use the default language specified for the application
        if (StringUtils.isBlank(user.getPreferredLanguage())) {
            String language = cfgService.getString(ConfigurationKey.LANGUAGE);
            user.setPreferredLanguage(language);
        }
        user.setSurname(LdapUtils.validateAttribute(attrs.get(fieldSurname)));

//        Attribute attr = attrs.get(fieldPhoto);
//        if (attr != null) {
//            try {
//                user.setPhoto((byte[]) attr.get());
//            } catch (ClassCastException e) {
//                log.log(Level.WARNING, "LDAP connection is not configured to allow binary values for the field [" + fieldPhoto + "]", e);
//            } catch (NoSuchElementException e) {
//                log.log(Level.FINER, "Attribute did not exist", e);
//            } catch (NamingException e) {
//                log.log(Level.FINER, "Attribute value could not be obtained", e);
//            }
//        }

        String employmentType = LdapUtils.validateAttribute(attrs.get(
                fieldEmploymentType));
        String feeType = LdapUtils.validateAttribute(attrs.get(fieldFeeType));
        if (employmentType.equalsIgnoreCase(employmentTypePermanent)) {
            user.setEmploymentType(EmploymentType.PERMANENT);
        } else if (employmentType.equalsIgnoreCase(employmentTypeFreelance)) {
            user.setEmploymentType(EmploymentType.FREELANCE);
        } else {
            user.setEmploymentType(EmploymentType.UNKNOWN);
        }

        if (feeType.equalsIgnoreCase(feeTypeStory)) {
            user.setFeeType(FeeType.ARTICLE);
        } else if (feeType.equalsIgnoreCase(feeTypeFixed)) {
            user.setFeeType(FeeType.FIXED);
        } else if (feeType.equalsIgnoreCase(feeTypeWord)) {
            user.setFeeType(FeeType.WORD);
        } else {
            user.setFeeType(FeeType.UNKNOWN);
        }
    }

    /**
     * Setup field mapping for the directory service.
     */
    private void setupUserMapping() {

        groupOfUsers = cfgService.getString(ConfigurationKey.LDAP_GROUP_USERS);
        groupOfAdministrators = cfgService.getString(
                ConfigurationKey.LDAP_GROUP_ADMINISTRATORS);

        addMapping(LdapFieldMapping.USER_MAPPING_USERNAME,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_USERNAME));
        addMapping(LdapFieldMapping.USER_MAPPING_EMAIL,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_EMAIL));
        addMapping(LdapFieldMapping.USER_MAPPING_COMMON_NAME, cfgService.
                getString(ConfigurationKey.LDAP_USER_MAPPING_COMMON_NAME));
        addMapping(LdapFieldMapping.USER_MAPPING_FIRST_NAME, cfgService.
                getString(ConfigurationKey.LDAP_USER_MAPPING_FIRST_NAME));
        addMapping(LdapFieldMapping.USER_MAPPING_LAST_NAME,
                cfgService.getString(
                ConfigurationKey.LDAP_USER_MAPPING_LAST_NAME));
        addMapping(LdapFieldMapping.USER_MAPPING_JOB_TITLE,
                cfgService.getString(
                ConfigurationKey.LDAP_USER_MAPPING_JOB_TITLE));
        addMapping(LdapFieldMapping.USER_MAPPING_MOBILE,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_MOBILE));
        addMapping(LdapFieldMapping.USER_MAPPING_ORGANISATION, cfgService.
                getString(ConfigurationKey.LDAP_USER_MAPPING_ORGANISATION));
        addMapping(LdapFieldMapping.USER_MAPPING_PHONE,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_PHONE));
        addMapping(LdapFieldMapping.USER_MAPPING_LANGUAGE,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_LANGUAGE));
        addMapping(LdapFieldMapping.USER_MAPPING_JPEG_PHOTO, cfgService.
                getString(ConfigurationKey.LDAP_USER_MAPPING_JPEG_PHOTO));
        addMapping(LdapFieldMapping.USER_MAPPING_EMPLOYMENT_TYPE, cfgService.
                getString(ConfigurationKey.LDAP_USER_MAPPING_EMPLOYEE_TYPE));
        addMapping(LdapFieldMapping.EMPLOYMENT_TYPE_MAPPING_FREELANCE,
                cfgService.getString(
                ConfigurationKey.LDAP_EMPLOYMENT_TYPE_MAPPING_FREELANCE));
        addMapping(LdapFieldMapping.EMPLOYMENT_TYPE_MAPPING_PERMANENT,
                cfgService.getString(
                ConfigurationKey.LDAP_EMPLOYMENT_TYPE_MAPPING_PERMANENT));
        addMapping(LdapFieldMapping.USER_MAPPING_FEE_TYPE,
                cfgService.getString(ConfigurationKey.LDAP_USER_MAPPING_FEE_TYPE));
        addMapping(LdapFieldMapping.FEE_TYPE_MAPPING_STORY,
                cfgService.getString(
                ConfigurationKey.LDAP_FEE_TYPE_MAPPING_STORY));
        addMapping(LdapFieldMapping.FEE_TYPE_MAPPING_FIXED,
                cfgService.getString(
                ConfigurationKey.LDAP_FEE_TYPE_MAPPING_FIXED));
        addMapping(LdapFieldMapping.FEE_TYPE_MAPPING_WORD,
                cfgService.getString(ConfigurationKey.LDAP_FEE_TYPE_MAPPING_WORD));
        addMapping(LdapFieldMapping.GROUP_MAPPING_NAME,
                cfgService.getString(ConfigurationKey.LDAP_GROUP_MAPPING_NAME));
        addMapping(LdapFieldMapping.GROUP_MAPPING_MEMBEROF,
                cfgService.getString(
                ConfigurationKey.LDAP_GROUP_MAPPING_MEMBEROF));
    }

    /**
     * Adds a field mapping to the mapping table.
     *
     * @param fieldIdentifier Field identifier
     * @param fieldName       Real name of the field
     */
    private void addMapping(String fieldIdentifier, String fieldName) {
        this.fieldMapping.put(fieldIdentifier, fieldName);
    }

    /**
     * Gets a field mapping from the mapping table.
     *
     * @param fieldIdentifier Field identifier
     * @return Real name of the field given
     */
    private String getFieldMapping(String fieldIdentifier) {
        if (this.fieldMapping.containsKey(fieldIdentifier)) {
            return this.fieldMapping.get(fieldIdentifier);
        } else {
            return "";
        }
    }

    private void addMapping(LdapFieldMapping fieldIdentifier, String fieldName) {
        addMapping(fieldIdentifier.name(), fieldName);
    }

    private String getFieldMapping(LdapFieldMapping fieldMapping) {
        return getFieldMapping(fieldMapping.name());
    }

    /**
     * Obtains the connection to the LDAP directory used for storing users and
     * user groups.
     *
     * @return Established connection to the used LDAP directory
     * @throws NamingException
* If the connection could not be established
     */
    private DirContext getDirectoryConnection() throws NamingException {
        LOG.log(Level.FINE, "Opening directory connection");
        DirContext dirContext = null;

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY,
                cfgService.getString(ConfigurationKey.LDAP_CONNECTION_FACTORY));
        p.put("com.sun.jndi.ldap.connect.pool", "true");
        p.put("com.sun.jndi.ldap.read.timeout",
                cfgService.getString(ConfigurationKey.LDAP_READ_TIMEOUT));
        p.put("com.sun.jndi.ldap.connect.timeout",
                cfgService.getString(ConfigurationKey.LDAP_CONNECT_TIMEOUT));

        p.put(Context.PROVIDER_URL, cfgService.getString(
                ConfigurationKey.LDAP_PROVIDER_URL));
        p.put(Context.SECURITY_AUTHENTICATION,
                cfgService.getString(
                ConfigurationKey.LDAP_SECURITY_AUTHENTICATION));
        p.put(Context.SECURITY_PRINCIPAL, cfgService.getString(
                ConfigurationKey.LDAP_SECURITY_PRINCIPAL));
        p.put(Context.SECURITY_CREDENTIALS,
                cfgService.getString(ConfigurationKey.LDAP_SECURITY_CREDENTIALS));

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "INITIAL_CONTEXT_FACTORY: {0}",
                    p.getProperty(Context.INITIAL_CONTEXT_FACTORY));
            LOG.log(Level.FINE, "PROVIDER_URL: {0}", p.getProperty(
                    Context.PROVIDER_URL));
            LOG.log(Level.FINE, "SECURITY_AUTHENTICATION: {0}",
                    p.getProperty(Context.SECURITY_AUTHENTICATION));
            LOG.log(Level.FINE, "SECURITY_PRINCIPAL: {0}",
                    p.getProperty(Context.SECURITY_PRINCIPAL));
            LOG.log(Level.FINE, "SECURITY_CREDENTIALS: {0}",
                    p.getProperty(Context.SECURITY_CREDENTIALS));
            LOG.log(Level.FINE, "LDAP_BASE: {0}",
                    cfgService.getString(ConfigurationKey.LDAP_BASE));
            LOG.log(Level.FINE, "LDAP_CONNECTION_TIMEOUT: {0}", cfgService.
                    getString(ConfigurationKey.LDAP_CONNECT_TIMEOUT));
            LOG.log(Level.FINE, "LDAP_READ_TIMEOUT: {0}",
                    cfgService.getString(ConfigurationKey.LDAP_READ_TIMEOUT));
        }

        dirContext = new InitialDirContext(p);

        return dirContext;
    }

    private void closeDirectoryConnection(DirContext dirContext) {
        LOG.log(Level.FINE, "Closing directory connection");
        if (dirContext == null) {
            LOG.log(Level.WARNING, "Could not close DirContext as it is null");
            return;
        }

        try {
            dirContext.close();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Could not close DirContext. ", ex);
        }
    }

    /** {@inheritDoc } */
    @Override
    public List<UserRole> getUserRoles() {
        return daoService.findAll(UserRole.class);
    }

    /** {@inheritDoc } */
    @Override
    public UserRole findUserRoleById(Long id) throws DataNotFoundException {
        return daoService.findById(UserRole.class, id);
    }

    /** {@inheritDoc } */
    @Override
    public List<UserAccount> findUserAccountsByUserRoleName(String name) {
        Map<String, Object> params = QueryBuilder.with("roleName", name).
                parameters();
        return daoService.findWithNamedQuery(UserAccount.FIND_BY_USER_ROLE,
                params);
    }

    /** {@inheritDoc } */
    @Override
    public void update(UserRole userRole) {
        daoService.update(userRole);
    }

    /** {@inheritDoc } */
    @Override
    public UserRole create(UserRole selected) {
        return daoService.create(selected);
    }

    /** {@inheritDoc } */
    @Override
    public void delete(UserRole userRole) {
        daoService.delete(UserRole.class, userRole.getId());
    }

    /** {@inheritDoc} */
    @Override
    public Privilege findPrivilegeById(String id) throws DataNotFoundException {
        SystemPrivilege sp = null;
        try {
            sp = SystemPrivilege.valueOf(id);
            return daoService.findById(Privilege.class, sp);
        } catch (IllegalArgumentException ex) {
            throw new DataNotFoundException(ex);
        } catch (DataNotFoundException ex) {
            Privilege p = new Privilege(sp);
            return daoService.create(p);
        }
    }
}

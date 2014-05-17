/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.core.security;

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.AssignmentType;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.newswire.NewswireBasket;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Section;
import java.awt.ComponentOrientation;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

/**
 * {@link UserAccount} with access to the system and relation news items,
 * outlets, photos, and so forth. The majority of the {@link UserAccount}
 * properties are not managed by the JPA persistence framework. Instead the are
 * retrieved from an LDAP directory upon fetching an account through the data
 * access object. The mapping of the LDAP user account to the
 * {@link UserAccount} object is defined in the application configuration.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "user_account", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@NamedQueries({
    @NamedQuery(name = UserAccount.FIND_BY_UID, query = "SELECT u FROM UserAccount u WHERE u.username=:username"),
    @NamedQuery(name = UserAccount.FIND_BY_USER_ROLE, query = "SELECT u FROM UserRole r JOIN r.userAccounts u WHERE r.name=:roleName"),
    @NamedQuery(name = UserAccount.FIND_USERS_WITH_PUBLICATIONS, query = "SELECT u FROM NewsItemActor n JOIN n.newsItem ni JOIN ni.placements p JOIN n.user u WHERE (n.role = :userRole AND p.edition.publicationDate >= :startDate AND p.edition.publicationDate <= :endDate) GROUP BY u"),
    @NamedQuery(name = UserAccount.FIND_ACTIVE_USERS_BY_ROLE, query = "SELECT DISTINCT u FROM NewsItem AS ni JOIN ni.actors AS a JOIN a.user AS u JOIN ni.history AS h WHERE (a.role = :userRole AND h.user = a.user AND h.timestamp >= :startDate AND h.timestamp <= :endDate AND h.submitted = true) ORDER BY a.user.username DESC"),
    @NamedQuery(name = UserAccount.FIND_PASSIVE_USERS_BY_ROLE, query = "SELECT DISTINCT u FROM NewsItem AS ni JOIN ni.actors AS a JOIN a.user AS u JOIN ni.history AS h WHERE (a.role = :userRole AND h.timestamp >= :startDate AND h.timestamp <= :endDate AND h.submitted = true) ORDER BY a.user.username DESC")
})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Query for finding a user account by its unique user identifier. */
    public static final String FIND_BY_UID = "UserAccount.findByUid";

    /** Query for finding a user accounts who are members of a given role. */
    public static final String FIND_BY_USER_ROLE = "UserAccount.findByUserRole";

    /** Query for generating the activity report. */
    public static final String FIND_USERS_WITH_PUBLICATIONS = "UserAccount.findUsersWithPublications";

    /** Query for generating the activity report for active users only. */
    public static final String FIND_ACTIVE_USERS_BY_ROLE = "UserAccount.findActiveUsersByRole";
    
    /** Query for generating the activity report - for passive and active users. */
    public static final String FIND_PASSIVE_USERS_BY_ROLE = "UserAccount.findPassiveUsersByRole";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 255)
    private String username = "";

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "user_status", length = 255)
    private String status = "";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_outlet")
    private Outlet defaultOutlet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_section")
    private Section defaultSection;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_assignment_type")
    private AssignmentType defaultAssignmentType;

    @ManyToOne
    @JoinColumn(name = "default_media_repository")
    private Catalogue defaultMediaRepository;

    @Column(name = "default_add_next_edition")
    private boolean defaultAddNextEdition;

    @Column(name = "default_search_engine_tags")
    private boolean defaultSearchEngineTags;

    @Column(name = "default_search_results_order_by")
    private String defaultSearchResultsSortBy;

    @Column(name = "default_search_results_order")
    private boolean defaultSearchResultsOrder;
    
    @Column(name = "default_work_day")
    private int defaultWorkDay;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clearance_level")
    private ClearanceLevel clearanceLevel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_account_newswire_services",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "user_account_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_service_id", nullable = false)})
    private List<NewswireService> newswireServices = new ArrayList<NewswireService>();

    @ManyToMany(mappedBy = "userAccounts")
    private List<UserRole> userRoles = new ArrayList<UserRole>();

    @Column(name = "dn") @Lob
    private String distinguishedName = "";

    @Column(name = "lang")
    private String preferredLanguage = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type")
    private FeeType feeType;

    @Column(name = "full_name")
    private String fullName = "";

    @Column(name = "given_name")
    private String givenName = "";

    @Column(name = "surname")
    private String surname = "";

    @Column(name = "job_title")
    private String jobTitle = "";

    @Column(name = "organisation") @Lob
    private String organisation = "";

    @Column(name = "email") @Lob
    private String email = "";

    @Column(name = "phone")
    private String phone = "";

    @Column(name = "mobile")
    private String mobile = "";

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @javax.persistence.Version
    @Column(name = "opt_lock")
    private int versionIdentifier;

    @OneToMany(mappedBy = "checkedOutBy", fetch = FetchType.LAZY)
    private List<NewsItem> checkedOut;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<NewswireBasket> newswireBaskets = new ArrayList<NewswireBasket>();

    /**
     * Creates a new instance of user.
     */
    public UserAccount() {
    }

    /**
     * Creates a new instance of {@link UserAccount}.
     * 
     * @param uid
     *      Username of the user
     */
    public UserAccount(String uid) {
        this.username = uid;
    }

    /**
     * Gets the unique identifier of the user. The unique identifier is automatically generated
     * upon the creation of the user.
     *
     * @return Unique identifier of the user
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id
     *          Unique identifier of the user
     */
    public void setId(Long id) {
        this.id = id;
    }

    public ClearanceLevel getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(ClearanceLevel clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    /**
     * Gets the distinguished name of the user in the directory service.
     *
     * @return Distinguished name of the user in the directory service
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the distinguished name of the user in the directory service.
     *
     * @param distinguishedName
     *          Distinguished name of the user in the directory service
     */
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    /**
     * Gets the username of this {@link UserAccount}.
     * 
     * @return Username of this {@link UserAccount}.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of this {@link UserAccount}.
     * 
     * @param username Username of this {@link UserAccount}.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the full or common name of this {@link UserAccount}. This property is
     * supplied by the LDAP directory.
     * 
     * @return Full or common name of this {@link UserAccount}
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full or common name of this {@link UserAccount}.
     *
     * @param fullName
     *          Full or common name of this {@link UserAccount}
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the given name of the person owning the {@link UserAccount}.
     *
     * @return Given name of the person owning the {@link UserAccount}
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the given name of the person owning the {@link UserAccount}.
     *
     * @param givenName
     *          Given name of the person owning the {@link UserAccount}
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * Gets the surname of the person owning the {@link UserAccount}.
     *
     * @return Surname of the person owning the {@link UserAccount}
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the person owning the {@link UserAccount}.
     *
     * @param surname
     *          Surname of the person owning the {@link UserAccount}
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Outlet getDefaultOutlet() {
        return defaultOutlet;
    }

    public void setDefaultOutlet(Outlet defaultOutlet) {
        this.defaultOutlet = defaultOutlet;
    }

    public Section getDefaultSection() {
        return defaultSection;
    }

    public void setDefaultSection(Section defaultSection) {
        this.defaultSection = defaultSection;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    /**
     * Gets the type of employment of the user.
     *
     * @return Employment type of the user
     */
    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    /**
     * Sets the employment type of the user.
     *
     * @param employmentType
     *          Employment type of the user
     */
    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    /**
     * Gets the type of fee paid to the user.
     *
     * @return Type of fee paid to the user
     */
    public FeeType getFeeType() {
        return feeType;
    }

    /**
     * Sets the type of fee paid to the user.
     *
     * @param feeType
     *          Type of fee paid to the user
     */
    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    /**
     * Gets the {@link TimeZone} of this {@link UserAccount}. Dates and times should be
     * adjusted to given {@link TimeZone} for this {@link UserAccount}.
     *
     * @return {@link TimeZone} of the {@link UserAccount}
     */
    public TimeZone getTimeZone() {
        if (this.timeZone == null) {
            return TimeZone.getDefault();
        } else {
            try {
                return TimeZone.getTimeZone(this.timeZone);
            } catch (Exception ex) {
                return TimeZone.getDefault();
            }
        }
    }

    /**
     * Sets the {@link TimeZone} of this {@link UserAccount}.
     *
     * @param timeZone
     *          {@link TimeZone} of this {@link UserAccount}
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone.getID();
    }

    /**
     * Sets the {@link TimeZone} of this {@link UserAccount}.
     *
     * @param timeZoneId
     *          {@link String} identifier of the {@link TimeZone}
     */
    public void setTimeZoneAsString(String timeZoneId) {
        this.timeZone = timeZoneId;
    }

    /**
     * Gets the {@link TimeZone} as a {@link String} of this {@link UserAccount}.
     *
     * @return {@link String} identifier of the {@link TimeZone}
     */
    public String getTimeZoneAsString() {
        return this.timeZone;
    }

    /**
     * Gets the current status of the user. The status is free text set by the
     * user.
     * 
     * @return Current status of the user
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the user.
     *
     * @param status
     *          Current status of the user
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets a {@link List} of the {@link UserRole}s where the user is a member.
     *
     * @return {@link List} of the {@link UserRole}s where the user is a member
     */
    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * Sets a {@link List} of the {@link UserRole}s where the user is a member.
     *
     * @param userRoles
     *          {@link List} of the {@link UserRole}s where the user is a member
     */
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    /**
     * Determines if the {@link UserAccount} has a given privilege.
     *
     * @param privilege
     *          Privilege to determine if the {@link UserAccount} has
     * @return <code>true</code> if the {@link UserAccount} has the given
     *         privilege, otherwise <code>false</code>
     */
    public boolean isPrivileged(SystemPrivilege privilege) {
        for (UserRole userRole : this.userRoles) {
            for (Privilege p : userRole.getPrivileges()) {
                if (p.getId().equals(privilege)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets a {@link List} of {@link Outlet}s where the {@link UserAccount} has
     * privileges.
     *
     * @return {@link List} of {@link Outlet}s where the {@link UserAccount} has
     *         privileges.
     */
    public List<Outlet> getPrivilegedOutlets() {
        List<Outlet> outlets = new ArrayList<Outlet>();

        for (UserRole userRole : this.userRoles) {
            outlets.addAll(userRole.getOutlets());
        }

        // Only unique outlets
        Set set = new HashSet(outlets);
        outlets = new ArrayList(set);

        return outlets;
    }

    /**
     * Gets a {@link List} of {@link Outlet}s where the {@link UserAccount} has
     * privileges.
     *
     * @return {@link List} of {@link Outlet}s where the {@link UserAccount} has
     *         privileges.
     */
    public List<Outlet> getPrivilegedOutlets(SystemPrivilege... privileges) {
        List<Outlet> outlets = new ArrayList<Outlet>();

        for (UserRole userRole : this.userRoles) {
            for (Privilege p : userRole.getPrivileges()) {

                for (SystemPrivilege privilege : privileges) {
                    if (p.getId().equals(privilege)) {
                        outlets.addAll(userRole.getOutlets());
                    }
                }
            }
        }

        // Only unique outlets
        Set set = new HashSet(outlets);
        outlets = new ArrayList(set);

        return outlets;
    }

    /**
     * Gets the preferred language of the user. The language is expressed as a two
     * character code corresponding to RFC2068.
     *
     * @return Preferred language of the user
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Sets the preferred language of the user.
     *
     * @param preferredLanguage
     *          Preferred language of the user; expressed as a two character code
     *          corresponding to RFC2068
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * Determine if the display should be rendered right-to-left for the
     * user.
     * 
     * @return {@code true} if the display should be rendered right-to-left, 
     *         otherwise {@code false}
     */
    public boolean isRightToLeft() {
        Locale l = getPreferredLocale();
        if (l == null){
            return false;
        }
        
        return !ComponentOrientation.getOrientation(l).isLeftToRight();
    }
    
    /**
     * Gets the preferred language based on the preferred language string. If
     * the language string stored in the preferred language string cannot be
     * converted to a locale null will be returned.
     *
     * @return {@link Locale} corresponding to the preferred language, or
     *         <code>null</code> if preferred language is invalid.
     */
    public Locale getPreferredLocale() {
        // Valid lengths of the preferred language string
        final int fiveCharValidLength = 5;
        final int twoCharValidLength = 2;

        // Position of the language
        final int startLanguage = 0;
        final int endLanguage = 2;

        // Position of the language speciality
        final int startLanguageSpeciality = 3;
        final int endLanguageSpeciality = 5;

        // Locale object to return
        Locale l = null;

        // A valid language string is either two or five characters: la_sp where
        // la is Language, sp is Speciality or la where la is Language
        if ((this.preferredLanguage.length() == fiveCharValidLength)) {
            l = new Locale(this.preferredLanguage.substring(startLanguage,
                    endLanguage), this.preferredLanguage.substring(
                    startLanguageSpeciality, endLanguageSpeciality));
        } else if (this.preferredLanguage.length() == twoCharValidLength) {
            l = new Locale(this.preferredLanguage);
        }

        return l;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public int getVersionIdentifier() {
        return versionIdentifier;
    }

    public List<NewswireService> getNewswireServices() {
        return newswireServices;
    }

    public void setNewswireServices(List<NewswireService> newswireServices) {
        this.newswireServices = newswireServices;
    }

    /**
     * Get a {@link List} of active {@link NewswireService} subscriptions.
     * 
     * @return {@link List} of active {@link NewswireService} subscriptions
     */
    public List<NewswireService> getActiveNewswireServices() {
        List<NewswireService> active = new ArrayList<NewswireService>();
        for (NewswireService service : getNewswireServices()) {
            if (service.isActive()) {
                active.add(service);
            }
        }
        Collections.sort(active, new BeanComparator("source"));
        return active;
    }

    /**
     * Gets the {@link List} of {@link NewswireBasket}s owned
     * by the user.
     * 
     * @return {@link List} of {@link NewswireBaket}s owned by
     *         the user.
     */
    public List<NewswireBasket> getNewswireBaskets() {
        return newswireBaskets;
    }

    /**
     * Sets the {@link List} of {@link NewswireBasket}s owned
     * by the user.
     * 
     * @param newswireBaskets 
     *          {@link List} of {@link NewswireBasket}s owned
     *          by the user.
     */
    public void setNewswireBaskets(List<NewswireBasket> newswireBaskets) {
        this.newswireBaskets = newswireBaskets;
    }

    public AssignmentType getDefaultAssignmentType() {
        return defaultAssignmentType;
    }

    public void setDefaultAssignmentType(AssignmentType defaultAssignmentType) {
        this.defaultAssignmentType = defaultAssignmentType;
    }

    public Catalogue getDefaultMediaRepository() {
        return defaultMediaRepository;
    }

    public void setDefaultMediaRepository(Catalogue defaultMediaRepository) {
        this.defaultMediaRepository = defaultMediaRepository;
    }

    /**
     * Determines if a default catalogue has been selected
     * by the user.
     * 
     * @return {@code true} if a default catalogue has been selected,
     *         otherwise {@code false}
     */
    public boolean isDefaultCatalogueSelected() {
        if (defaultMediaRepository != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the tags in the search engine should be
     * turned on by default.
     * 
     * @return {@code true} if the search engine tags should
     *         be turned on by default, otherwise {@code false}
     */
    public boolean isDefaultSearchEngineTags() {
        return defaultSearchEngineTags;
    }

    /**
     * Sets the search engine tags preference for the user.
     * 
     * @param defaultSearchEngineTags 
     *          {@code true} if the search engine tags should be on by default
     *          otherwise {@code false}
     */
    public void setDefaultSearchEngineTags(boolean defaultSearchEngineTags) {
        this.defaultSearchEngineTags = defaultSearchEngineTags;
    }

    /**
     * Determines if the search results should be ordered descending 
     * ({@code false}) or ascending ({@code true}) by default.
     * 
     * @return {@code true} if the search results should be ordered in 
     *         ascending order by default or {@code false} for descending
     *         order
     */
    public boolean isDefaultSearchResultsOrder() {
        return defaultSearchResultsOrder;
    }

    /**
     * Sets the default sort order for search results.
     * 
     * @param defaultSearchResultsOrder
     *          {@code true} if the search results should be ordered in 
     *          ascending order by default or {@code false} for descending
     *          order
     */
    public void setDefaultSearchResultsOrder(boolean defaultSearchResultsOrder) {
        this.defaultSearchResultsOrder = defaultSearchResultsOrder;
    }

    /**
     * Gets the default field for which to sort search results.
     * 
     * @return Default field for which to sort search results
     */
    public String getDefaultSearchResultsSortBy() {
        return defaultSearchResultsSortBy;
    }

    /**
     * Sets the default field for which to sort search results.
     * 
     * @param defaultSearchResultsSortBy 
     *          Default field for which to sort search results
     */
    public void setDefaultSearchResultsSortBy(String defaultSearchResultsSortBy) {
        this.defaultSearchResultsSortBy = defaultSearchResultsSortBy;
    }

    /**
     * Preference deciding if the "Add To Next Edition" checkbox should 
     * be on by default.
     * 
     * @return {@code true} if the "Add To Next Edition" checkbox shold be
     *         on by default, otherwise {@code false}.
     */
    public boolean isDefaultAddNextEdition() {
        return defaultAddNextEdition;
    }

    /**
     * Preference deciding if the "Add To Next Edition" checkbox should 
     * be on by default.
     * 
     * @param defaultAddNextEdition
     *         {@code true} if the "Add To Next Edition" checkbox shold be
     *         on by default, otherwise {@code false}.
     */
    public void setDefaultAddNextEdition(boolean defaultAddNextEdition) {
        this.defaultAddNextEdition = defaultAddNextEdition;
    }

    /**
     * Gets the {@link NewsItem}s currently checked out by the user.
     *
     * @return {@link List} of {@link NewsItem}s currently checked out
     */
    public List<NewsItem> getCheckedOut() {
        return checkedOut;
    }

    /**
     * Sets the {@link NewsItem}s current checked out by the user.
     *
     * @param checkedOut
     *          {@link List} of checked out {@link NewsItem}s
     */
    public void setCheckedOut(List<NewsItem> checkedOut) {
        this.checkedOut = checkedOut;
    }

    /**
     * Gets the default work day. The default work day is the day to show when
     * opening the planning view. This value is number of days to add/subtract
     * from todays day.
     * 
     * @return Number of days to add to todays date in the planning view
     */
    public int getDefaultWorkDay() {
        return defaultWorkDay;
    }

    /**
     * Sets the default work day. The default work day is the day to show when
     * opening the planning view. This value is number of days to add/subtract
     * from todays day.
     * 
     * @param defaultWorkDay Number of days to add to todays date in the planning view
     */
    public void setDefaultWorkDay(int defaultWorkDay) {
        this.defaultWorkDay = defaultWorkDay;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserAccount)) {
            return false;
        }

        if (object == null) {
            return false;
        }

        UserAccount other = (UserAccount) object;

        if ((this.username == null && other.username != null) || (this.username
                != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[id=").append(id).append("/username=").append(username).append("]");
        return sb.toString();
    }
}

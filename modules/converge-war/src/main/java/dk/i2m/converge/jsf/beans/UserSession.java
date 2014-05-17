/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.commons.FileUtils;
import dk.i2m.commons.ImageUtils;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.reporting.activity.UserActivitySummary;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.wiki.Page;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.jsf.beans.administrator.Catalogues;
import dk.i2m.converge.jsf.model.MenuHelper;
import dk.i2m.converge.jsf.model.MenuItem;
import dk.i2m.converge.jsf.model.MenuItems;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import org.richfaces.event.UploadEvent;

/**
 * Session scoped managed bean controlling the current user session. This class
 * is responsible for containing and providing information about the current
 * user logged in.
 *
 * @author Allan Lykke Christensen
 */
public class UserSession {

    private static final Logger LOG =
            Logger.getLogger(UserSession.class.getName());

    @EJB private UserFacadeLocal userFacade;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private ReportingFacadeLocal reportingFacade;

    @EJB private WikiFacadeBeanLocal wikiFacade;

    private UserAccount user;

    /** Menu items appearing on the page header. */
    private List<MenuItem> items = new ArrayList<MenuItem>();

    /** Sub-menu items. */
    private List<MenuItem> submenuItems = new ArrayList<MenuItem>();

    private MenuItem administration = new MenuItem();

    private MenuItem selectedMenu;

    private MenuItem sectionMenu;

    private Map<String, List<Outlet>> privilegedOutlets =
            new HashMap<String, List<Outlet>>();

    private Map<String, Boolean> privileges = new HashMap<String, Boolean>();

    private Map<Long, Boolean> catalogueEditor = new HashMap<Long, Boolean>();

    private List<Catalogue> catalogues = new ArrayList<Catalogue>();

    private Notification selectedNotification;

    private NewswireService selectedNewswireService;

    private UserActivitySummary lastMonthActivity;

    private UserActivitySummary thisMonthActivity;

    private Page displayWikiPage = null;

    private String wikiPageTitle;

    /**
     * Creates a new instance of {@link UserSession}.
     */
    public UserSession() {
    }

    @PostConstruct
    public void onInit() throws UserSessionException {
        fetchUser();
        updatePrivileges();
        generateMenu();
    }

    /**
     * Internal method for updating the user privileges.
     */
    private void updatePrivileges() {
        String uid = getUser().getUsername();
        List<UserRole> userRoles = getUser().getUserRoles();
        this.catalogues = catalogueFacade.findCataloguesByUser(uid);

        this.catalogueEditor.clear();
        List<Catalogue> allCatalogues = catalogueFacade.findAllCatalogues();
        for (Catalogue c : allCatalogues) {
            this.catalogueEditor.put(c.getId(), userRoles.contains(c.
                    getEditorRole()));
        }
    }

    /**
     * Determines if the user is an administrator.
     *
     * @return {@code true} if the user is an administrator, otherwise
     *         {@code false}
     */
    public boolean isAdministrator() {
        return isUserInRole("ADMINISTRATOR");
    }

    /**
     * Gets the current user, or {@code null} if the user is not logged in.
     *
     * @return Current user logged in
     */
    public UserAccount getUser() {
        return user;
    }

    /**
     * Determines if the current user is in a given role.
     *
     * @param role Name of the role
     * @return {@code true} if the user is in the given role, otherwise
     *         {@code false}
     */
    public boolean isUserInRole(String role) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return ctx.getExternalContext().isUserInRole(role);
    }

    /**
     * Determines if the current user is an editor of a catalogue.
     * <p/>
     * @return {@code true} if the current user is an editor of a
     * catalogue, otherwise {@code false}
     */
    public boolean isCatalogueEditor() {
        return userFacade.isCatalogueEditor(getUser().getUsername());
    }

    /**
     * Gets a {@link Map} of {@link Catalogues} with the {@link Catalogue} id
     * in the key of the {@link Map}, and a {@link Boolean} value in the value
     * of the {@link Map}. The {@link Boolean} value indicates if the current
     * user is an editor for the given {@link Catalogue}.
     * <p/>
     * @return {@link Map} of {@link Catalogues} indicator whether the current
     * user is an editor.
     */
    public Map<Long, Boolean> getCatalogueEditorRole() {
        return catalogueEditor;
    }

    public UserActivitySummary getLastMonthActivity() {
        return lastMonthActivity;
    }

    public UserActivitySummary getThisMonthActivity() {
        return thisMonthActivity;
    }

    /**
     * Resets the "active" status on all items.
     */
    public void resetActive() {
        for (MenuItem item : items) {
            item.setActive(false);
        }
        for (MenuItem item : submenuItems) {
            item.setActive(false);
        }
        this.administration.setActive(false);
    }

    /**
     * Get a {@link List} of all {@link MenuItem}s.
     *
     * @return {@link List} of all {@link MenuItem}s on the page header
     */
    public List<MenuItem> getItems() {
        return this.items;
    }

    /**
     * Get a {@link List} of all {@link MenuItem}s to be displayed in the
     * sub-menu.
     *
     * @return {@link List} of all {@link MenuItem}s on the sub-menu
     */
    public List<MenuItem> getSubmenuItems() {
        return submenuItems;
    }

    public MenuItem getAdministration() {
        return administration;
    }

    public void setAdministration(MenuItem administration) {
        this.administration = administration;
    }

    public MenuItem getSelectedMenu() {
        return selectedMenu;
    }

    public void setSelectedMenu(MenuItem selectedMenu) {
        this.selectedMenu = selectedMenu;
    }

    public MenuItem getSectionMenu() {
        return sectionMenu;
    }

    public void setSectionMenu(MenuItem sectionMenu) {
        this.sectionMenu = sectionMenu;
    }

    public boolean isShowSectionMenu() {
        if (this.sectionMenu == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Fetches user information and authorisation. This method should only be
     * executed once per user session.
     *
     * @throws UserSessionException If a user is not currently logged in or if the user could not be
     * found in the database and directory
     */
    private void fetchUser() throws UserSessionException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String uid = ctx.getExternalContext().getRemoteUser();
        String ip = ((HttpServletRequest) FacesContext.getCurrentInstance().
                getExternalContext().getRequest()).getRemoteAddr();

        LOG.log(Level.FINE, "Initialising user session for {0}", uid);
        privilegedOutlets.clear();

        // Used by the session overview to see the name of the users logged in
        JsfUtils.getHttpSession().setAttribute("uid", uid);
        JsfUtils.getHttpSession().setAttribute("ip", ip);

        if (uid != null && !uid.isEmpty()) {

            try {
                user = userFacade.findById(uid, true);
                onRefreshUserActivity();
                for (SystemPrivilege p : SystemPrivilege.values()) {
                    privilegedOutlets.put(p.name(), user.getPrivilegedOutlets(p));
                    privileges.put(p.name(), user.isPrivileged(p));
                }

                if (user.getPreferredLocale() != null) {
                    setLocale(user.getPreferredLocale());
                }

            } catch (DataNotFoundException ex) {
                throw new UserSessionException(ex);
            }
        } else {
            throw new UserSessionException("User is not logged in");
        }
    }

    private void generateMenu() {
        // Set-up menu items
        MenuItems menuItems = MenuHelper.getInstance().getMenuItems();

        for (MenuItem item : menuItems.getItems()) {
            boolean show = false;

            if (item.isPrivilegesAvailable()) {
                for (String p : item.getPrivileges()) {
                    SystemPrivilege sp = SystemPrivilege.valueOf(p);

                    if (user.isPrivileged(sp)) {
                        show = true;
                    }
                }
            } else {
                show = true;
            }

            if (show) {
                item.setMenuManager(this);
                if (item.isActive()) {
                    selectedMenu = item;
                }

                if (item.getType().equalsIgnoreCase(MenuItem.TYPE_ADMIN)) {
                    setupAdminMenu(item);
                } else if (item.getType().equalsIgnoreCase(MenuItem.TYPE_SUBMENU)) {
                    addSubmenuItem(item);
                } else {
                    addMenuItem(item);
                }
            } else {
                LOG.log(Level.FINE, "Not showing {0}", item.getId());
            }
        }

        List<Page> wikiPages = wikiFacade.findSubmenuPages();

        for (Page page : wikiPages) {
            String menuId = "lnkWikiPage" + page.getId();
            String menuAction = "wiki:" + page.getTitle();
            MenuItem item = new MenuItem(menuId, page.getTitle(), menuAction,
                    this);
            item.setStyle(page.getSubmenuStyle());
            addSubmenuItem(item);
        }
    }

    /**
     * Helper method for adding a {@link MenuItem} to the page header.
     *
     * @param menuItem {@link MenuItem} to add to the page header
     */
    private void addMenuItem(MenuItem menuItem) {
        this.items.add(menuItem);
    }

    /**
     * Helper method for adding a {@link MenuItem} to the sub-menu.
     *
     * @param menuItem {@link MenuItem} to add to the sub-menu
     */
    private void addSubmenuItem(MenuItem menuItem) {
        this.submenuItems.add(menuItem);
    }

    /**
     * Helper method for adding a {@link MenuItem} to the page header.
     *
     * @param menuItem {@link MenuItem} to add to the page header
     */
    private void setupAdminMenu(MenuItem menuItem) {
        administration = menuItem;
    }

    /**
     * Determine if the current user is allowed to create new assignments.
     * <p/>
     * @return {@code true} if the current user is allowed to create either news
     * items for outlets or media items for catalogues, otherwise
     *         {@code false}
     */
    public boolean isPrivilegedToCreateNewAssignments() {
        return !getPrivilegedOutlets().isEmpty() || !getMyCatalogues().isEmpty();
    }

    /**
     * Gets a {@link Map} of {@link Outlet}s indexed by the privileges of the
     * current user. The name of the {@link SystemPrivilege} is used as the key
     * and the value is a {@link List} of the {@link Outlet} where the user has
     * the given privilege.
     *
     * @return {@link Map} of {@link Outlet}s indexed by privilege
     */
    public Map<String, List<Outlet>> getPrivilegedOutlets() {
        return privilegedOutlets;
    }

    public Map<String, Outlet> getMyNewsItemsOutlets() {
        Map<String, Outlet> outlets = new LinkedHashMap<String, Outlet>();

        for (Outlet outlet : getUser().getPrivilegedOutlets(
                SystemPrivilege.MY_NEWS_ITEMS)) {
            outlets.put(outlet.getTitle(), outlet);
        }

        return outlets;
    }

    public Map<String, Section> getMyNewsItemsOutletSections() {
        Map<String, Section> sections = new LinkedHashMap<String, Section>();

        if (getUser().getDefaultOutlet() != null) {
            for (Section section : getUser().getDefaultOutlet().getSections()) {
                if (section.isActive()) {
                    sections.put(section.getFullName(), section);
                }
            }
        }

        return sections;
    }

    /**
     * Gets the privileged catalogues of the current user.
     * <p/>
     * @return {@link Map} of privileged catalogues of the current user
     */
    public Map<String, Catalogue> getMyCatalogues() {
        Map<String, Catalogue> mine = new LinkedHashMap<String, Catalogue>();
        for (Catalogue c : this.catalogues) {
            mine.put(c.getName(), c);
        }
        return mine;
    }

    /**
     * Refreshes the {@link UserActivitySummary} for the past two months.
     * After executing this method, the activity summary is available in
     * {@link UserSession#getLastMonthActivity()} and
     * {@link UserSession#getThisMonthActivity()}.
     */
    public void onRefreshUserActivity() {
        java.util.Calendar lastMonth = java.util.Calendar.getInstance();
        lastMonth.add(java.util.Calendar.MONTH, -1);
        java.util.Calendar thisMonth = java.util.Calendar.getInstance();

        java.util.Calendar lastMonthFirstDay = CalendarUtils.getFirstDayOfMonth(
                lastMonth);
        java.util.Calendar lastMonthLastDay = CalendarUtils.getLastDayOfMonth(
                lastMonth);

        java.util.Calendar thisMonthFirstDay = CalendarUtils.getFirstDayOfMonth(
                thisMonth);
        java.util.Calendar thisMonthLastDay = CalendarUtils.getLastDayOfMonth(
                thisMonth);

        this.lastMonthActivity =
                reportingFacade.generateUserActivitySummary(lastMonthFirstDay.
                getTime(), lastMonthLastDay.getTime(), getUser());
        this.thisMonthActivity =
                reportingFacade.generateUserActivitySummary(thisMonthFirstDay.
                getTime(), thisMonthLastDay.getTime(), getUser());
    }

    /**
     * Gets a {@link Map} of privileges and whether the current user has the
     * given privilege. The name of the {@link SystemPrivilege} is used as the
     * key and the value is a {@link Boolean} that indicates whether the user
     * has the privilege or not.
     *
     * @return {@link Map} of privileges for the current user
     */
    public Map<String, Boolean> getPrivileged() {
        return privileges;
    }

    public Notification getSelectedNotification() {
        return selectedNotification;
    }

    public void setSelectedNotification(Notification selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    public NewswireService getSelectedNewswireService() {
        return selectedNewswireService;
    }

    public void setSelectedNewswireService(
            NewswireService selectedNewswireService) {
        this.selectedNewswireService = selectedNewswireService;
    }

    /**
     * Event handler for updating the profile of the user.
     *
     * @param event Event that invoked the handler
     */
    public void onUpdateProfile(ActionEvent event) {
        userFacade.update(this.user);
        try {
            fetchUser();
        } catch (UserSessionException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                    new Object[]{ex.getMessage()});
        }

        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                "MyProfile_PROFILE_UPDATED");
    }

    /**
     * Event handler for uploading a new profile photo.
     *
     * @param event Event that invoked the handler
     */
    public void onUploadProfilePhoto(UploadEvent event) {
        if (event == null) {
            return;
        }
        org.richfaces.model.UploadItem item = event.getUploadItem();

        if (item.isTempFile()) {
            java.io.File tempFile = item.getFile();

            String workingDirectory = systemFacade.getProperty(
                    ConfigurationKey.WORKING_DIRECTORY) + System.getProperty(
                    "file.separator") + "users" + System.getProperty(
                    "file.separator");
            try {
                byte[] uploadedFile = FileUtils.getBytes(tempFile);
                byte[] thumb = ImageUtils.generateThumbnail(uploadedFile, 48, 48,
                        100);

                FileUtils.writeToFile(thumb, workingDirectory + user.getId()
                        + ".jpg");

//                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "profile_PHOTO_UPDATED_MSG");
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "", ex);
//                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, "Could not read the uploaded file");
            } catch (InterruptedException ex) {
                LOG.log(Level.WARNING, "", ex);
//                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, "Could not generate thumbnail from uploaded file. Please check the format of the file to ensure that it is an JPEG (RGB) file.");
            }

        } else {
            LOG.severe("RichFaces is not set-up to use tempFiles "
                    + "for storing file uploads");
        }
    }

    /**
     * Event handler for dismissing the selected {@link Notification}.
     *
     * @param event Event that invoked the handler
     */
    public void onDismissNotification(ActionEvent event) {
        if (selectedNotification != null) {
            userFacade.dismiss(selectedNotification);
        }
    }

    /**
     * Event handler for dismissing all {@link Notification}s.
     *
     * @param event Event that invoked the handler
     */
    public void onDismissAllNotifications(ActionEvent event) {
        userFacade.dismiss(getUser());
    }

    /**
     * Gets the URL of the user photo.
     * <p/>
     * @return URL of the user photo
     */
    public String getPhotoUrl() {
        Long unique = java.util.Calendar.getInstance().getTimeInMillis();
        return "/UserPhoto?uid=" + getUser().getId() + "&t=" + unique;
    }

    /**
     * Sets the {@link Locale} of the current user.
     * <p/>
     * @param locale {@link Locale} of the current user
     */
    private void setLocale(Locale locale) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    /**
     * Event handler (setPropertyActionListener) for selecting the wiki page
     * that should be displayed.
     * <p/>
     * @param wikiPage Name of the wiki page to be displayed.
     */
    public void setLoadWikiPage(String wikiPage) {
        this.wikiPageTitle = wikiPage;
        String pageTitle = wikiPage.replaceFirst("wiki:", "");
        try {
            setDisplayWikiPage(wikiFacade.findPageByTitle(pageTitle));
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown wiki page requested. {0}", ex.
                    getMessage());
        }
    }

    /**
     * Event handler for preparation of a new wiki page.
     * 
     * @param event Event that invoked the handler
     */
    public void onCreateWikiPage(ActionEvent event) {
        this.displayWikiPage = new Page();
    }

    /**
     * Event handler for saving the currently opened wiki page.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onSaveWikiPage(ActionEvent event) {
        displayWikiPage.setLastUpdater(getUser());
        if (displayWikiPage.getId() == null) {
            displayWikiPage.setSubmenu(true);
            displayWikiPage = wikiFacade.create(displayWikiPage);
        } else {
            wikiFacade.update(displayWikiPage);
        }
        this.items.clear();
        this.submenuItems.clear();
        generateMenu();
        
    }

    /**
     * Event handler for deleting the currently selected wiki page.
     * 
     * @param event Event that invoked the handler
     */
    public void onDeleteWikiPage(ActionEvent event) {
        wikiFacade.deletePageById(displayWikiPage.getId());
        this.items.clear();
        this.submenuItems.clear();
        generateMenu();
    }

    /**
     * Sets the wiki page that has to be displayed.
     * <p/>
     * @param displayWikiPage Wiki page to display
     */
    public void setDisplayWikiPage(Page displayWikiPage) {
        this.displayWikiPage = displayWikiPage;
    }

    /**
     * Gets the wiki page that should be displayed.
     * <p/>
     * @return Wiki page that should be displayed
     */
    public Page getDisplayWikiPage() {
        return displayWikiPage;
    }
}

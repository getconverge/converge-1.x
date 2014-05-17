/*
 * MenuItem.java
 * 
 * Copyright (C) 2009 Interactive Media Management
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
package dk.i2m.converge.jsf.model;

import dk.i2m.converge.jsf.beans.UserSession;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Menu item that can appear on the application menu.
 *
 * @author Allan Lykke Christensen
 */
@XmlRootElement()
public class MenuItem {

    public static final String TYPE_NORMAL = "";

    public static final String TYPE_ADMIN = "admin";

    public static final String TYPE_SUBMENU = "submenu";

    private String id = "";

    /** Message key of the label of the {@link MenuItem}. */
    private String label = "";

    private String action = "";

    private String type = "";
    
    private String style = "";

    /** Is this {@link MenuItem} currently selected?. */
    private boolean active = false;

    /** User roles with access to the {@link MenuItem}. */
    private String[] roles;

    private String[] privileges;

    /** {@link UserSession} controlling the {@link MenuItem}. */
    private UserSession menuManager;

    /** Should the menu be reset when clicked. */
    private boolean resetOnClick = true;

    /** Should the section menu be reset when clicked. */
    private boolean resetSectionMenu = true;

    /** Does the {@link MenuItem} provide section {@link MenuItem}s. */
    private boolean sectionMenu = false;

    /** {@link List} of child {@link MenuItem}s. */
    private List<MenuItem> children = new ArrayList<MenuItem>();

    /**
     * Create a new instance of {@link MenuItem}.
     */
    public MenuItem() {
    }

    /**
     * Create a new instance of {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     * @param label
     *          Message key of the label of the {@link MenuItem}
     * @param action
     *          Action to return if the {@link MenuItem} is activated
     * @param session
     *          {@link UserSession} controlling the {@link MenuItem}
     * @param active
     *          Is this {@link MenuItem} currently selected
     * @param roles
     *          User roles with access to the {@link MenuItem}
     */
    public MenuItem(String id, String label, String action, UserSession session,
            boolean active, String... roles) {
        this(id, label, action, session, roles);
        this.active = active;
    }

    /**
     * Create a new instance of {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     * @param label
     *          Message key of the label of the {@link MenuItem}
     * @param action
     *          Action to return if the {@link MenuItem} is activated
     * @param resetOnClick
     *          Should the menu be reset upon activating
     * @param session
     *          {@link UserSession} controlling the {@link MenuItem}
     * @param roles
     *          User roles with access to the {@link MenuItem}
     */
    public MenuItem(String id, String label, String action, boolean resetOnClick,
            UserSession session, String... roles) {
        this.id = id;
        this.label = label;
        this.action = action;
        this.roles = roles;
        this.menuManager = session;
        this.resetOnClick = resetOnClick;
    }

    /**
     * Create a new instance of {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     * @param label
     *          Message key of the label of the {@link MenuItem}
     * @param action
     *          Action to return if the {@link MenuItem} is activated
     * @param resetOnClick
     *          Should the menu be reset upon activating
     * @param sectionMenu
     *          Does the {@link MenuItem} provide children {@link MenuItem}s for
     *          a section menu
     * @param session
     *          {@link UserSession} controlling the {@link MenuItem}
     * @param roles
     *          User roles with access to the {@link MenuItem}
     */
    public MenuItem(String id, String label, String action, boolean resetOnClick,
            boolean sectionMenu, UserSession session, String... roles) {
        this.id = id;
        this.label = label;
        this.action = action;
        this.resetOnClick = resetOnClick;
        this.sectionMenu = sectionMenu;
        this.menuManager = session;
        this.roles = roles;
    }

    /**
     * Create a new instance of {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     * @param label
     *          Message key of the label of the {@link MenuItem}
     * @param action
     *          Action to return if the {@link MenuItem} is activated
     * @param resetOnClick
     *          Should the menu be reset upon activating
     * @param session
     *          {@link UserSession} controlling the {@link MenuItem}
     * @param resetSectionMenu
     *          Should the selection menu be reset upon activating
     * @param roles
     *          User roles with access to the {@link MenuItem}
     */
    public MenuItem(String id, String label, String action, boolean resetOnClick,
            UserSession session, boolean resetSectionMenu, String... roles) {
        this.id = id;
        this.label = label;
        this.action = action;
        this.resetOnClick = resetOnClick;
        this.resetSectionMenu = resetSectionMenu;
        this.menuManager = session;
        this.roles = roles;
    }

    /**
     * Create a new instance of {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     * @param label
     *          Message key of the label of the {@link MenuItem}
     * @param action
     *          Action to return if the {@link MenuItem} is activated
     * @param session
     *          {@link UserSession} controlling the {@link MenuItem}
     * @param roles
     *          User roles with access to the {@link MenuItem}
     */
    public MenuItem(String id, String label, String action, UserSession session,
            String... roles) {
        this.id = id;
        this.label = label;
        this.action = action;
        this.roles = roles;
        this.menuManager = session;
    }

    /**
     * Gets the unique identifier of the {@link MenuItem}.
     *
     * @return Unique identifier of the {@link MenuItem}
     */
    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link MenuItem}.
     *
     * @param id
     *          Unique identifier of the {@link MenuItem}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the type of menu. The type can either be <code>main</code>
     * or <code>admin</code>.
     *
     * @return Type of menu
     */
    @XmlAttribute(required = false)
    public String getType() {
        return type;
    }

    /**
     * Sets the type of menu.
     *
     * @param type
     *          Type of menu
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Action of the {@link MenuItem}.
     *
     * @return Outcome of the action
     */
    public String action() {
        if (resetOnClick) {
            this.menuManager.resetActive();
            this.menuManager.setSectionMenu(null);
            setActive(true);
        }

        if (this.resetSectionMenu) {
            this.menuManager.setSectionMenu(null);
        }

        if (sectionMenu) {
            this.menuManager.setSectionMenu(this);
        } else {
            this.menuManager.setSelectedMenu(this);
        }

        return action;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    
    
    /**
     * Sets the action to return if the {@link MenuItem} is clicked.
     *
     * @param action Action to return if the {@link MenuItem} is clicked
     */
    public void setNavigationAction(String action) {
        this.action = action;
    }

    /**
     * Gets the action to return if the {@link MenuItem} is clicked.
     *
     * @return Action to return if the {@link MenuItem} is clicked
     */
    @XmlElement(name = "action")
    public String getNavigationAction() {
        return this.action;
    }

    /**
     * Determines if the {@link MenuItem} is active.
     *
     * @return <code>true</code> if the {@link MenuItem} is active, otherwise
     *         <code>false</code>.
     */
    @XmlAttribute
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @XmlElement
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MenuItem> getChildren() {
        return children;
    }

    public void setChildren(List<MenuItem> children) {
        this.children = children;
    }

    @XmlAttribute
    public boolean isResetOnClick() {
        return resetOnClick;
    }

    public void setResetOnClick(boolean resetOnClick) {
        this.resetOnClick = resetOnClick;
    }

    @XmlAttribute
    public boolean isSectionMenu() {
        return sectionMenu;
    }

    public void setSectionMenu(boolean sectionMenu) {
        this.sectionMenu = sectionMenu;
    }

    @XmlAttribute
    public boolean isResetSectionMenu() {
        return resetSectionMenu;
    }

    public void setResetSectionMenu(boolean resetSectionMenu) {
        this.resetSectionMenu = resetSectionMenu;
    }

    @XmlElement(name = "role")
    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public boolean isRolesAvailable() {
        if (this.roles == null || this.roles.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    @XmlElement(name = "privilege")
    public String[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String[] privileges) {
        this.privileges = privileges;
    }

    /**
     * Determines if the {@link MenuItem} has required privileges associated.
     *
     * @return {@code true} if privilege requirements were specified for this
     *         item, otherwise {@code false}
     */
    public boolean isPrivilegesAvailable() {
        if (this.privileges == null || this.privileges.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAllowedForAll() {
        if (roles == null || roles.length == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isWikiPage() {
        if (action == null || !action.startsWith("wiki:")) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Sets the menu manager of the {@link MenuItem}.
     *
     * @param session
     *          {@link UserSession} managing the {@link MenuItem}
     */
    public void setMenuManager(UserSession session) {
        this.menuManager = session;

        for (MenuItem child : getChildren()) {
            child.setMenuManager(session);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuItem other = (MenuItem) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

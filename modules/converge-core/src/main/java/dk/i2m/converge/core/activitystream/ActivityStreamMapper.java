/*
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
package dk.i2m.converge.core.activitystream;

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.security.UserAccount;

/**
 * Maps activity stream classes from one type to another.
 *
 * @author Allan Lykke Christensen
 */
public final class ActivityStreamMapper {

    /**
     * Translates a {@link Notification} into an {@link Activity}.
     *
     * @param notification {@link Notification} to translate
     * @return {@link Activity} based on the {@link Notification}
     */
    public static Activity from(Notification notification) {
        Activity activity = new Activity();
        activity.setId(notification.getId());
        activity.setPublished(notification.getAdded().getTime());
        activity.setUrl(notification.getLink());
        activity.setContent(notification.getMessage());
        activity.setAuthor(from(notification.getSender()));
        return activity;
    }

    /**
     * Translates a {@link UserAccount} into a {@link Person}.
     *
     * @param userAccount {@link UserAccount} to translate
     * @return {@link Person} based on the {@link UserAccount}
     */
    public static Person from(UserAccount userAccount) {
        Person person = new Person();
        person.setDisplayName(userAccount.getFullName());
        person.setId(userAccount.getUsername());
        person.setUrl("UserProfile.xhtml?id=" + userAccount.getId());
        person.setImage(new MediaLink());
        person.getImage().setUrl("/UserPhoto?uid=" + userAccount.getId() + "&t=" + System.currentTimeMillis());
        return person;
    }

}

/*
 *  Copyright (C) 2010 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.security.UserAccount;
import javax.ejb.Local;

/**
 * Interface for the notification service bean.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface NotificationServiceLocal {

    /**
     * Creates a new {@link Notification}.
     *
     * @param notification
     *          Notification to create
     * @return Created {@link Notification}
     */
    Notification create(Notification notification);

    /**
     * Creates a new {@link Notification}.
     *
     * @param recipient
     *          Recipient of the {@link Notification}
     * @param message
     *          Message in the {@link Notification}
     * @return Created {@link Notification}
     */
    Notification create(UserAccount recipient, String message);

    /**
     * Dismisses an existing {@link Notification}.
     *
     * @param notification
     *          {@link Notification} to dismiss
     */
    void dismiss(Notification notification);

    /**
     * Dispatches a plain-text e-mail to a given person with a given subject
     * and body.
     *
     * @param to
     *          Recipient e-mail
     * @param from
     *          Sender e-mail
     * @param subject
     *          E-mail subject
     * @param content
     *          E-mail content body
     */
    void dispatchMail(String to, String from, String subject, String content);

    void dispatchMail(java.lang.String to, java.lang.String from, java.lang.String subject, java.lang.String htmlContent, java.lang.String plainContent);
}

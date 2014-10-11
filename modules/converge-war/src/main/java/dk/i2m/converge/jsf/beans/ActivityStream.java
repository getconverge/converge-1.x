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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author Allan Lykke Christensen
 */
public class ActivityStream {

    private static final int BATCH_SIZE = 25;

    @EJB
    private UserFacadeLocal userFacade;

    private int show = BATCH_SIZE;

    private DataModel notifications = null;

    private Long total = 0L;

    
//    public void onDismissAll(ActionEvent event) {
//        userFacade.dismiss(getUser());
//        this.notifications = null;
//    }
//
//    public void onCheckForUpdates(ActionEvent event) {
//        Long count = userFacade.getNotificationCount(getUser().getUsername());
//        if (count != total) {
//            this.notifications = null;
//        }
//    }
//
//    public void onShowMore(ActionEvent event) {
//        show += BATCH_SIZE;
//        this.notifications = null;
//    }
//
//    public void setDeleteNotification(Notification notification) {
//        userFacade.dismiss(notification);
//        this.notifications = null;
//    }
//
//    public boolean isShowMore() {
//        return total > show;
//    }
//
//    private UserAccount getUser() {
//        final String valueExpression = "#{userSession.user}";
//        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
//    }
}

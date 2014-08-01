/*
 *  Copyright (C) 2010 Interactive Media Management
 *  Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.contacts.Contact;
import dk.i2m.converge.core.contacts.ContactAddress;
import dk.i2m.converge.core.contacts.ContactEmail;
import dk.i2m.converge.core.contacts.ContactPhone;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.DirectoryException;
import dk.i2m.converge.ejb.services.QueryBuilder;
import dk.i2m.converge.ejb.services.UserNotFoundException;
import dk.i2m.converge.ejb.services.UserServiceLocal;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class ContactsFacadeBean implements ContactsFacadeLocal {

    private static final Logger LOG = Logger.getLogger(ContactsFacadeBean.class.getName());
    private static final String LOG_MSG_COULD_NOT_CONTACT_USER_DIRECTORY = "Could not contact user directory. {0}";
    private static final String LOG_MSG_COULD_NOT_FIND_CONTACT_CREATOR = "Could not find contact creator. {0}";
    private static final String LOG_MSG_COULD_NOT_FIND_CONTACT_UPDATER = "Could not find contact updater. {0}";
    private static final String LOG_MSG_X_DELETED_CONTACT_Y = "{0} deleted contact #{1}";

    @EJB
    private DaoServiceLocal daoService;

    @EJB
    private UserServiceLocal userService;

    @Resource
    private SessionContext ctx;

    @Override
    public List<Contact> getContacts() {
        return daoService.findAll(Contact.class);
    }

    @Override
    public List<Contact> findByName(String name) {
        Map<String, Object> params = QueryBuilder.with("name", name).parameters();
        return daoService.findWithNamedQuery(Contact.FIND_BY_NAME, params);
    }

    @Override
    public List<Contact> findByOrganisation(String organisation) {
        Map<String, Object> params = QueryBuilder.with("organisation", organisation).parameters();
        return daoService.findWithNamedQuery(Contact.FIND_BY_ORGANISATION, params);
    }

    @Override
    public Contact create(Contact contact) {
        String uid = ctx.getCallerPrincipal().getName();
        UserAccount user = null;
        try {
            user = userService.findById(uid);
        } catch (UserNotFoundException ex) {
            LOG.log(Level.WARNING, LOG_MSG_COULD_NOT_FIND_CONTACT_CREATOR, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.WARNING, LOG_MSG_COULD_NOT_CONTACT_USER_DIRECTORY, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
        }

        Calendar now = Calendar.getInstance();
        contact.setCreated(now);
        contact.setUpdated(now);
        contact.setCreatedBy(user);
        contact.setUpdatedBy(user);
        return daoService.create(contact);
    }

    @Override
    public Contact update(Contact contact) {
        String uid = ctx.getCallerPrincipal().getName();
        UserAccount user = null;
        try {
            user = userService.findById(uid);
        } catch (UserNotFoundException ex) {
            LOG.log(Level.WARNING, LOG_MSG_COULD_NOT_FIND_CONTACT_UPDATER, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.WARNING, LOG_MSG_COULD_NOT_CONTACT_USER_DIRECTORY, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
        }

        Calendar now = Calendar.getInstance();

        contact.setUpdated(now);
        contact.setUpdatedBy(user);

        return daoService.update(contact);
    }

    @Override
    public void delete(Long id) {
        String uid = ctx.getCallerPrincipal().getName();
        daoService.delete(Contact.class, id);
        LOG.log(Level.INFO, LOG_MSG_X_DELETED_CONTACT_Y, new Object[]{uid, id});
    }

    @Override
    public ContactPhone create(ContactPhone contactPhone) {
        return daoService.create(contactPhone);
    }

    @Override
    public void deleteContactPhone(Long id) {
        daoService.delete(ContactPhone.class, id);
    }

    @Override
    public ContactEmail create(ContactEmail contactEmail) {
        return daoService.create(contactEmail);
    }

    @Override
    public void deleteContactEmail(Long id) {
        daoService.delete(ContactEmail.class, id);
    }

    @Override
    public ContactAddress create(ContactAddress contactAddress) {
        return daoService.create(contactAddress);
    }

    @Override
    public void deleteContactAddress(Long id) {
        daoService.delete(ContactAddress.class, id);
    }

    @Override
    public Contact findById(Long id) throws DataNotFoundException {
        return daoService.findById(Contact.class, id);
    }
}

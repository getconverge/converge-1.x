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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.contacts.Contact;
import dk.i2m.converge.core.contacts.ContactAddress;
import dk.i2m.converge.core.contacts.ContactEmail;
import dk.i2m.converge.core.contacts.ContactPhone;
import dk.i2m.converge.ejb.facades.ContactsFacadeLocal;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code Contacts.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Contacts {

    @EJB private ContactsFacadeLocal contactsFacade;

    private DataModel contacts = null;

    private Contact selectedContact = null;

    private ContactPhone selectedContactPhone = new ContactPhone();

    private ContactPhone moveContactPhone = new ContactPhone();

    private ContactEmail selectedContactEmail = new ContactEmail();

    private ContactAddress selectedContactAddress = new ContactAddress();

    public DataModel getContacts() {
        if (contacts == null) {
            onRefresh(null);
        }
        return contacts;
    }

    public void onDelete(ActionEvent event) {
        if (selectedContact != null) {
            contactsFacade.delete(selectedContact.getId());
            onRefresh(event);
        }
    }

    public void onNew(ActionEvent event) {
        selectedContact = new Contact();
        selectedContact.setTitle("Mr.");
    }

    public void onRefresh(ActionEvent event) {
        contacts = new ListDataModel(contactsFacade.getContacts());
    }

    public void onSave(ActionEvent event) {
        if (selectedContact != null) {
            if (selectedContact.getId() == null) {
                selectedContact = contactsFacade.create(selectedContact);
            } else {
                selectedContact = contactsFacade.update(selectedContact);
            }
            onRefresh(event);
        }
    }

    public void onMoveContactPhoneUp(ActionEvent event) {
        selectedContact.movePhone(false, moveContactPhone);
        selectedContact = contactsFacade.update(selectedContact);
    }

    public void onMoveContactPhoneDown(ActionEvent event) {
        selectedContact.movePhone(true, moveContactPhone);
        selectedContact = contactsFacade.update(selectedContact);
    }

    /**
     * Event handler for adding a new phone number to a contact.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSavePhone(ActionEvent event) {
        if (selectedContactPhone != null) {
            selectedContact.addPhone(selectedContactPhone);
            selectedContact = contactsFacade.update(selectedContact);
            selectedContactPhone = new ContactPhone();
        }
    }

    public void onSaveEmail(ActionEvent event) {
        if (selectedContactEmail != null) {
            selectedContactEmail.setContact(selectedContact);
            //selectedContactEmail = contactsFacade.create(selectedContactEmail);
            //selectedContactEmail = new ContactEmail();
            selectedContact.getEmails().add(selectedContactEmail);

            selectedContact = contactsFacade.update(selectedContact);
        }
    }

    public void onSaveAddress(ActionEvent event) {
        if (selectedContactAddress != null) {
            selectedContactAddress.setContact(selectedContact);
            selectedContactAddress = contactsFacade.create(selectedContactAddress);
            selectedContactAddress = new ContactAddress();

            try {
                selectedContact = contactsFacade.findById(selectedContact.getId());

            } catch (DataNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onDeleteContactPhone(ActionEvent event) {
        selectedContact.getPhones().remove(selectedContactPhone);
        selectedContact = contactsFacade.update(selectedContact);

//        contactsFacade.deleteContactPhone(selectedContactPhone.getId());
//        selectedContactPhone = new ContactPhone();
//        try {
//            selectedContact = contactsFacade.findById(selectedContact.getId());
//
//        } catch (DataNotFoundException ex) {
//            ex.printStackTrace();
//        }
    }

    public void onDeleteContactEmail(ActionEvent event) {
        contactsFacade.deleteContactEmail(selectedContactEmail.getId());
        selectedContactEmail = new ContactEmail();
        try {
            selectedContact = contactsFacade.findById(selectedContact.getId());

        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void onDeleteContactAddress(ActionEvent event) {
        contactsFacade.deleteContactAddress(selectedContactAddress.getId());
        selectedContactAddress = new ContactAddress();
        try {
            selectedContact = contactsFacade.findById(selectedContact.getId());

        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Contact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public ContactAddress getSelectedContactAddress() {
        return selectedContactAddress;
    }

    public void setSelectedContactAddress(ContactAddress selectedContactAddress) {
        this.selectedContactAddress = selectedContactAddress;
    }

    public ContactEmail getSelectedContactEmail() {
        return selectedContactEmail;
    }

    public void setSelectedContactEmail(ContactEmail selectedContactEmail) {
        this.selectedContactEmail = selectedContactEmail;
    }

    public ContactPhone getSelectedContactPhone() {
        return selectedContactPhone;
    }

    public void setSelectedContactPhone(ContactPhone selectedContactPhone) {
        this.selectedContactPhone = selectedContactPhone;
    }

    public ContactPhone getMoveContactPhone() {
        return moveContactPhone;
    }

    public void setMoveContactPhone(ContactPhone moveContactPhone) {
        this.moveContactPhone = moveContactPhone;
    }
}

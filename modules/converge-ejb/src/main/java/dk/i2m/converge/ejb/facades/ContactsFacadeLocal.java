/*
 *  Copyright (C) 2010 alc
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
import dk.i2m.converge.core.DataNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author alc
 */
@Local
public interface ContactsFacadeLocal {

    public java.util.List<dk.i2m.converge.core.contacts.Contact> findByOrganisation(java.lang.String organisation);

    public java.util.List<dk.i2m.converge.core.contacts.Contact> findByName(java.lang.String name);

    public java.util.List<dk.i2m.converge.core.contacts.Contact> getContacts();

    public dk.i2m.converge.core.contacts.Contact create(dk.i2m.converge.core.contacts.Contact contact);

    public dk.i2m.converge.core.contacts.Contact update(dk.i2m.converge.core.contacts.Contact contact);

    public void delete(java.lang.Long id);

    public dk.i2m.converge.core.contacts.ContactPhone create(dk.i2m.converge.core.contacts.ContactPhone contactPhone);

    public void deleteContactPhone(java.lang.Long id);

    public dk.i2m.converge.core.contacts.ContactEmail create(dk.i2m.converge.core.contacts.ContactEmail contactEmail);

    public void deleteContactEmail(java.lang.Long id);

    public dk.i2m.converge.core.contacts.ContactAddress create(dk.i2m.converge.core.contacts.ContactAddress contactAddress);

    public void deleteContactAddress(java.lang.Long id);

    public Contact findById(Long id) throws DataNotFoundException;
}

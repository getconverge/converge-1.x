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
package dk.i2m.converge.core.contacts;

import dk.i2m.converge.core.contacts.Contact;
import dk.i2m.converge.core.contacts.ContactPhone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Allan Lykke Christensen
 */
public class ContactPhoneTest {

    private Contact c;

    private Contact c2;

    private ContactPhone cpMove;

    private ContactPhone cpMove2;
    private ContactPhone cpLast2;

    public ContactPhoneTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        c = new Contact();
        cpMove = new ContactPhone(3L, 3, "L3", "12345678", c);
        c.getPhones().add(new ContactPhone(1L, 1, "L1", "12345678", c));
        c.getPhones().add(new ContactPhone(2L, 2, "L2", "12345678", c));
        c.getPhones().add(cpMove);
        c.getPhones().add(new ContactPhone(4L, 4, "L4", "12345678", c));

        c2 = new Contact();
        cpMove2 = new ContactPhone(3L, 20, "L3", "12345678", c2);
        cpLast2 = new ContactPhone(1L, 141, "L1", "12345678", c2);
        c2.getPhones().add(cpLast2);
        c2.getPhones().add(new ContactPhone(2L, 24, "L2", "12345678", c2));
        c2.getPhones().add(cpMove2);
        c2.getPhones().add(new ContactPhone(4L, 10, "L4", "12345678", c2));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void moveDownLast() {
        c2.movePhone(true, cpLast2);

        boolean passed = false;
        for (ContactPhone phone : c2.getPhones()) {
            if (phone.equals(cpLast2) && phone.getDisplayOrder() == 141) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    @Test
    public void moveUpLast() {
        c2.movePhone(false, cpLast2);

        boolean passed = false;
        for (ContactPhone phone : c2.getPhones()) {
            if (phone.equals(cpLast2) && phone.getDisplayOrder() == 24) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    @Test
    public void moveUpOrdered() {
        c.movePhone(false, cpMove);

        boolean passed = false;
        for (ContactPhone phone : c.getPhones()) {
            if (phone.equals(cpMove) && phone.getDisplayOrder() == 2) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    @Test
    public void moveUpUnordered() {
        c2.movePhone(false, cpMove2);

        boolean passed = false;
        for (ContactPhone phone : c2.getPhones()) {
            if (phone.equals(cpMove2) && phone.getDisplayOrder() == 10) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    @Test
    public void moveDownOrdered() {
        c.movePhone(true, cpMove);

        boolean passed = false;
        for (ContactPhone phone : c.getPhones()) {
            if (phone.equals(cpMove) && phone.getDisplayOrder() == 4) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    @Test
    public void moveDownUnordered() {
        c2.movePhone(true, cpMove2);

        boolean passed = false;
        for (ContactPhone phone : c2.getPhones()) {
            if (phone.equals(cpMove2) && phone.getDisplayOrder() == 24) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }
}

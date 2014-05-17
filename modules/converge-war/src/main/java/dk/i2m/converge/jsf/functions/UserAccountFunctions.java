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
package dk.i2m.converge.jsf.functions;

import dk.i2m.converge.core.security.UserAccount;
import java.util.List;

/**
 * EL utility functions for working with {@link UserAccount}s.
 *
 * @author Allan Lykke Christensen
 */
public final class UserAccountFunctions {

    public static String separateUserAccounts(List<UserAccount> users, String separator) {
        StringBuilder output = new StringBuilder();

        boolean first = true;

        for (UserAccount user : users) {
            if (!first) {
                output.append(separator);
            } else {
                first = false;
            }
            output.append(user.getFullName());
        }

        return output.toString();
    }
}

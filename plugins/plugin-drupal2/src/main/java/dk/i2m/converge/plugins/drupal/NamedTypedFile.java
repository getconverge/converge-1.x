/*
 * Copyright (C) 2015 Raymond Wanyoike
 *
 * This file is part of Converge.
 *
 * Converge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Converge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Converge. If not, see <http://www.gnu.org/licenses/>.
 */

package dk.i2m.converge.plugins.drupal;

import retrofit.mime.TypedFile;

import java.io.File;

public class NamedTypedFile extends TypedFile {

    private String fileName;

    public NamedTypedFile(String mimeType, File file, String fileName) {
        super(mimeType, file);
        this.fileName = fileName;
    }

    @Override
    public String fileName() {
        if (fileName != null) {
            return fileName;
        } else {
            return super.fileName();
        }
    }
}

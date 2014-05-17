/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility library for working with files.
 *
 * @author Allan Lykke Christensen
 */
public class FileUtils {

    /**
     * {@link FileUtils} is a static class and is not instantiable.
     */
    private FileUtils() {
    }

    public static String getFilename(final String filename) {
        int pos = filename.lastIndexOf(File.separator);

        if (pos != -1) {
            return filename.substring(pos + 1);
        } else {
            return filename;
        }
    }

    public static String getFolder(final String filename) {
        int pos = filename.lastIndexOf(File.separator);

        if (pos != -1) {
            return getFilename(filename.substring(0, pos));
        } else {
            return "";
        }
    }

    /**
     * Writes a file to the operating system.
     *
     * @param content 
     *          Content of the file to write
     * @param fileName 
     *          Name of the file to write
     * @throws java.io.IOException
     *          If the byte array could not be written to the file.
     */
    public static void writeToFile(byte[] content, String fileName) throws
            IOException {
        FileOutputStream out = null;
        out = new FileOutputStream(fileName);
        try {
            out.write(content);
        } finally {
            out.close();
        }
    }

    /**
     * Turns an {@link InputStream} into a byte array.
     *
     * @param is 
     *          {@link InputStream} to convert
     * @return Byte array of the {@link InputStream}.
     * @throws java.io.IOException
     *          If the {@link InputStream} could not be read.
     */
    public static byte[] getBytes(InputStream is) throws IOException {
        byte out[] = new byte[is.available()];

        int bytesread = 0;
        int i;
        while (bytesread < out.length) {
            i = is.read(out, bytesread, out.length - bytesread);
            if (i < 0) {
                throw new IOException("Ran out of bytes to read!");
            }
            bytesread += i;
        }

        return out;
    }

    /**
     * Turns a {@link java.io.File} into a byte array.
     *
     * @param file
     *          {@link java.io.File} to turn into a byte array
     * @return Byte array of the {@link java.io.File}
     * @throws java.io.IOException
     *              If the file could not be converted
     */
    public static byte[] getBytes(java.io.File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File size is too big");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset,
                bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Turns a {@link URL} into a byte array.
     *
     * @param file
     *          {@link URL} to turn into a byte array
     * @return Byte array of the {@link URL}
     * @throws java.io.IOException
     *              If the URL could not be converted
     */
    public static byte[] getBytes(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        // Since you get a URLConnection, use it to get the InputStream
        InputStream in = connection.getInputStream();
        // Now that the InputStream is open, get the content length
        int contentLength = connection.getContentLength();

        // To avoid having to resize the array over and over and over as
        // bytes are written to the array, provide an accurate estimate of
        // the ultimate size of the byte array
        ByteArrayOutputStream tmpOut;
        if (contentLength != -1) {
            tmpOut = new ByteArrayOutputStream(contentLength);
        } else {
            tmpOut = new ByteArrayOutputStream(16384); // Pick some appropriate size
        }

        byte[] buf = new byte[512];
        while (true) {
            int len = in.read(buf);
            if (len == -1) {
                break;
            }
            tmpOut.write(buf, 0, len);
        }
        in.close();
        tmpOut.close(); // No effect, but good to do anyway to keep the metaphor alive

        byte[] array = tmpOut.toByteArray();

        //Lines below used to test if file is corrupt
        //FileOutputStream fos = new FileOutputStream("C:\\abc.pdf");
        //fos.write(array);
        //fos.close();

        return array;
    }

    public static String getString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(StringUtils.LINE_BREAK);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw e;
            }
        }

        return sb.toString();
    }
}

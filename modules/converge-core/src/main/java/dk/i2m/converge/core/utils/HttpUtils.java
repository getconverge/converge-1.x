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

import java.io.IOException;
import java.security.MessageDigest;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Utility methods for working with {@link HttpServletRequest}s and
 * {@link HttpServletResponse}s.
 *
 * @author Allan Lykke Christensen
 */
public class HttpUtils {

    /** Name of the parameter to use for storing the token by default. */
    public static final String TOKEN_PARAM = "token";

    /** Digest used to construct the token. */
    private static final String TOKEN_DIGEST = "MD5";

    /**
     * Protection against instantiation of the class.
     */
    private HttpUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the value of a parameter from an {@link HttpServletRequest}. If the
     * parameter does not exist in the request, the value specified in
     * <code>defValue</code> will be returned.
     *
     * @param request
     *            {@link HttpServletRequest} containing the parameter
     * @param parameter
     *            Name of the parameter to retrieve from the request
     * @param defValue
     *            Default value to return if the parameter does not exist
     * @return The value of the parameter from the {@link HttpServletRequest},
     *         or <code>defValue</code> if the parameter does not exist
     */
    public static Integer getParameterAsInteger(final HttpServletRequest request,
            final String parameter, final Integer defValue) {

        if (request.getParameter(parameter) == null) {
            return defValue;
        } else {
            String paramValue = request.getParameter(parameter);

            try {
                return Integer.valueOf(paramValue);
            } catch (Exception e) {
                return defValue;
            }

        }
    }

    /**
     * Gets the value of a parameter from an {@link HttpServletRequest}. If the
     * parameter does not exist in the request, the value specified in
     * <code>defValue</code> will be returned.
     *
     * @param request
     *            {@link HttpServletRequest} containing the parameter
     * @param parameter
     *            Name of the parameter to retrieve from the request
     * @param defValue
     *            Default value to return if the parameter does not exist
     * @return The value of the parameter from the {@link HttpServletRequest},
     *         or <code>defValue</code> if the parameter does not exist
     */
    public static Long getParameterAsLong(final HttpServletRequest request,
            final String parameter, final Long defValue) {

        if (request.getParameter(parameter) == null) {
            return defValue;
        } else {
            String paramValue = request.getParameter(parameter);

            try {
                return Long.valueOf(paramValue);
            } catch (Exception e) {
                return defValue;
            }

        }
    }

    /**
     * Gets the value of a parameter from an {@link HttpServletRequest}. If the
     * parameter does not exist in the request, the value specified in
     * <code>defValue</code> will be returned.
     *
     * @param request
     *            {@link HttpServletRequest} containing the parameter
     * @param parameter
     *            Name of the parameter to retrieve from the request
     * @param defValue
     *            Default value to return if the parameter does not exist
     * @return The value of the parameter from the {@link HttpServletRequest},
     *         or <code>defValue</code> if the parameter does not exist
     */
    public static Double getParameterAsDouble(final HttpServletRequest request,
            final String parameter, final Double defValue) {

        if (request.getParameter(parameter) == null) {
            return defValue;
        } else {
            String paramValue = request.getParameter(parameter);

            try {
                return Double.valueOf(paramValue);
            } catch (Exception e) {
                return defValue;
            }

        }
    }

    /**
     * Gets the value of a parameter from an {@link HttpServletRequest}. If the
     * parameter does not exist in the request, the value specified in
     * <code>defValue</code> will be returned.
     *
     * @param request
     *            {@link HttpServletRequest} containing the parameter
     * @param parameter
     *            Name of the parameter to retrieve from the request
     * @param defValue
     *            Default value to return if the parameter does not exist
     * @return The value of the parameter from the {@link HttpServletRequest},
     *         or <code>defValue</code> if the parameter does not exist
     */
    public static String getParameter(final HttpServletRequest request,
            final String parameter, final String defValue) {

        if (request.getParameter(parameter) == null) {
            return defValue;
        } else {
            return request.getParameter(parameter);
        }
    }

    /**
     * Gets the value of a parameter from an {@link HttpServletRequest}. If the
     * parameter does not exist in the request, an empty string will be
     * returned.
     *
     * @param request
     *            {@link HttpServletRequest} containing the parameter
     * @param parameter
     *            Name of the parameter to retrieve from the request
     * @return The value of the parameter from the {@link HttpServletRequest},
     *         or an empty string if the parameter does not exist
     */
    public static String getParameter(final HttpServletRequest request,
            final String parameter) {

        return getParameter(request, parameter, "");
    }

    /**
     * Gets the actual filename of a file uploaded from the client. On
     * Windows-based systems the full path of the file is included and will
     * therefore be cut off.
     *
     * @param filename
     *            Full file name and path of the uploaded file
     * @return The file name only
     */
    public static String getFilename(final String filename) {
        int pos = filename.lastIndexOf("\\");

        if (pos != -1) {
            return filename.substring(pos + 1);
        } else {
            return filename;
        }
    }

    /**
     * Gets the full context path of a given request. The output format is
     * <code>schema://servername:serverport/contextpath/</code> e.g.
     * <code>http://localhost:8080/myapp/</code>
     *
     * @param request
     *            Request to get the full context path for
     * @return URL of the request formatted as
     *         <code>schema://servername:serverport/contextpath/</code>
     */
    public static String getFullContextPath(final HttpServletRequest request) {

        StringBuffer path = new StringBuffer();

        path.append(request.getScheme());
        path.append("://");
        path.append(request.getServerName());
        path.append(":");
        path.append(request.getServerPort());
        path.append(request.getContextPath());
        path.append("/");

        return path.toString();
    }

    /**
     * Sets a token on a page to avoid resubmitting of forms. The parameter
     * containing the token is value stored in {@link RequestUtils#TOKEN_PARAM}.
     *
     * @param request
     *            The request to set the token on.
     */
    public static void setToken(HttpServletRequest request) {
        setToken(request, TOKEN_PARAM);
    }

    /**
     * Sets a token on a page to avoid resubmitting of forms.
     *
     * @param request
     *            The request to set the token on.
     * @param paramName
     *            Name of the parameter to store the token.
     */
    public static void setToken(HttpServletRequest request, String paramName) {

        // Get the session from the request (or create one if non exists)
        HttpSession session = request.getSession(true);

        // Generate the two parameters used in the digest
        long systime = System.currentTimeMillis();
        byte[] time = new Long(systime).toString().getBytes();
        byte[] id = session.getId().getBytes();

        // Create and set the digest
        try {
            MessageDigest md5 = MessageDigest.getInstance(TOKEN_DIGEST);
            md5.update(id);
            md5.update(time);
            String token = toHex(md5.digest());

            request.setAttribute(paramName, token);
            session.setAttribute(paramName, token);
        } catch (Exception e) {
        }
    }

    /**
     * Determines if a token set with
     * {@link RequestUtils#setToken(HttpServletRequest)} is valid.
     *
     * @param request
     *            Request containing the flow to validate
     * @return <code>true</code>, if the request flow has a valid token,
     *         otherwise <code>false</code;
     */
    public static boolean isTokenValid(HttpServletRequest request) {
        return isTokenValid(request, TOKEN_PARAM);
    }

    /**
     * Determines if a token set with
     * {@link RequestUtils#setToken(HttpServletRequest, String)} is valid.
     *
     * @param request
     *            Request containing the flow to validate
     * @param paramName
     *            Name of the parameter containing the token
     * @return <code>true</code>, if the request flow has a valid token,
     *         otherwise <code>false</code;
     */
    public static boolean isTokenValid(HttpServletRequest request,
            String paramName) {

        HttpSession session = request.getSession(true);

        String requestToken = request.getParameter(paramName);
        String sessionToken = (String) session.getAttribute(paramName);

        // Check if a token was set
        if (requestToken == null || sessionToken == null) {
            return false;
        } else {
            // Compare the two tokens
            return requestToken.equals(sessionToken);
        }
    }

    /**
     * Converts a given byte array to a hex string.
     *
     * @param byteArray
     *            Byte array to convert into a hex string.
     * @return String containing hex translation of the byte array.
     */
    private static String toHex(byte[] byteArray) {

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            hexString.append(Integer.toHexString((int) byteArray[i] & 0x00ff));
        }

        return hexString.toString();
    }

    /**
     * Sends a binary file to the user through a {@link HttpServletResponse}.
     * The response will be encoded as binary, disregarding any cache on the
     * client side.
     *
     * @param response
     *          {@link HttpServletResponse} to send the binary data through
     * @param filename
     *          Name of the file to send
     * @param filedata
     *          Binary data to send
     * @throws IOException
     *          If the {@link HttpServletResponse} could not be manipulated
     */
    public static void sendBinary(HttpServletResponse response, String filename,
            byte[] filedata) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                filename + "\"");
        response.setHeader("Content-Transfer-Encoding", "Binary");
        response.setHeader("Pragma", "private");
        response.setHeader("cache-control", "private, must-revalidate");

        ServletOutputStream outs = response.getOutputStream();
        outs.write(filedata);
        outs.flush();
        outs.close();
        response.flushBuffer();
    }

    /**
     * Sends a binary file to the user through a {@link HttpServletResponse}.
     * The response will be encoded as binary, disregarding any cache on the
     * client side.
     *
     * @param response
     *          {@link HttpServletResponse} to send the binary data through
     * @param filename
     *          Name of the file to send
     * @param filedata
     *          Binary data to send
     * @param contentType
     *          Content type of the file
     * @throws IOException
     *          If the {@link HttpServletResponse} could not be manipulated
     */
    public static void sendBinary(HttpServletResponse response, String filename,
            byte[] filedata, String contentType) throws IOException {
        response.setHeader("Content-Disposition", "inline; filename=\"" +
                filename + "\"");
        response.setHeader("Content-Type", contentType);
        response.setHeader("Pragma", "private");
        response.setHeader("cache-control", "private, must-revalidate");

        ServletOutputStream outs = response.getOutputStream();
        outs.write(filedata);
        outs.flush();
        outs.close();
        response.flushBuffer();
    }

    /**
     * Sends a binary file to the user through a {@link HttpServletResponse}.
     * The response will be encoded as binary, disregarding any cache on the
     * client side.
     *
     * @param response
     *          {@link HttpServletResponse} to send the binary data through

     * @param filePackage
     *          {@link FilePackage} to send
     * @param forceDownload
     *          Force the user to download the file rather than displaying it
     *          in-line
     * @throws IOException
     *          If the {@link HttpServletResponse} could not be manipulated
     */
    public static void sendBinary(HttpServletResponse response,
            FilePackage filePackage,
            boolean forceDownload) throws IOException {
        if (forceDownload) {
            sendBinary(response, filePackage.getFilename(), filePackage.
                    getBinary());
        } else {
            sendBinary(response, filePackage.getFilename(), filePackage.
                    getBinary(), filePackage.getContentType());
        }
    }
}

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
package dk.i2m.converge.web.servlets;

import dk.i2m.spellchecker.LanguageNotSupportedException;
import dk.i2m.spellchecker.SpellChecker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

/**
 * {@link HttpServlet} implementing the TinyMCE spellchecking plug-in protocol.
 * The servlet receives and responds with JSON messages.
 * <p>The incoming message must have a <code>method</code> parameter and
 * <code>params</code> array. The <code>method</code> must be either
 * <code>checkWords</code> or <code>getSuggestions</code>. The
 * <code>params</code> array must contain the language identifier as the first
 * entry and the second entry must be an array of the words to check.<p>
 *
 * @author Allan Lykke Christensen
 */
public class SpellCheckerServlet extends HttpServlet {

    private static final String DEFAULT_LANGUAGE = "en";

    private static final String METHOD = "method";

    private static final String METHOD_CHECK_WORDS = "checkWords";

    private static final String METHOD_GET_SUGGESTIONS = "getSuggestions";

    private static final String PARAMS = "params";

    private static final String RESPONSE_ID = "id";

    private static final String RESPONSE_RESULT = "result";

    private static final String RESPONSE_ERROR = "error";

    /** Application logger. */
    private static final Logger logger = Logger.getLogger(SpellCheckerServlet.class.getName());

    /**
     * Processes the JSON request received.
     *
     * @param request
     *          Request to process
     * @return Response to the request
     * @throws org.richfaces.json.JSONException
     *          If the request is not valid JSON
     */
    public JSONObject process(JSONObject request) throws JSONException {
        JSONObject response = new JSONObject();
        String cmd = request.getString(METHOD);
        String lang = request.getJSONArray(PARAMS).getString(0);
        if (lang == null) {
            lang = DEFAULT_LANGUAGE;
        }

        SpellChecker sc;
        try {
            String textToCheck;
            sc = SpellChecker.getInstance(lang);
            String[] output;
            if (METHOD_CHECK_WORDS.equalsIgnoreCase(cmd)) {
                JSONArray words = request.getJSONArray(PARAMS).getJSONArray(1);
                textToCheck = words.join(" ").replaceAll("\"", "");
                output = sc.getMisspelledWords(textToCheck);

                // If there are no spelling mistakes, TinyMCE pops up a lame
                // dialog that blocks for entry on Safari - therefore add a
                // dummy misspelledword
                //List<String> modifiedOutput = new ArrayList<String>(Arrays.asList(output));

                //modifiedOutput.add("xyzq");
                //output = modifiedOutput.toArray(new String[modifiedOutput.size()]);
            } else if (METHOD_GET_SUGGESTIONS.equalsIgnoreCase(cmd)) {
                textToCheck = request.getJSONArray(PARAMS).getString(1);
                output = sc.getSuggestions(textToCheck);
            } else {
                output = new String[0];
            }

            response.put(RESPONSE_ID, JSONObject.NULL);
            response.put(RESPONSE_RESULT, Arrays.asList(output));
            response.put(RESPONSE_ERROR, JSONObject.NULL);
        } catch (LanguageNotSupportedException ex) {
            response.put(RESPONSE_ERROR, ex.getMessage());
        }
        return response;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        try {

            JSONObject requestJSONdata = decode(request.getInputStream());
            logger.log(Level.FINE, "Received request: {0}", requestJSONdata.toString());

            JSONObject jsonResponse = process(requestJSONdata);
            logger.log(Level.FINE, "Generated response: {0}", jsonResponse.toString());

            response.getOutputStream().print(jsonResponse.toString());
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Decodes the content from the given {@link InputStream} to a
     * {@link JSONObject}.
     *
     * @param is
     *          {@link InputStream} to decode
     * @return {@link JSONObject} containing the content from the
     *         {@link InputStream}
     * @throws java.io.IOException
     *          If the content could not be read from the {@link InputStream}
     * @throws org.richfaces.json.JSONException
     *          If the content is not valid JSON
     */
    private JSONObject decode(InputStream is) throws IOException, JSONException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String jsonString;
        StringBuilder jsonStringBuffer = new StringBuilder();

        while ((jsonString = br.readLine()) != null) {
            jsonStringBuffer.append(jsonString);
        }

        return new JSONObject(jsonStringBuffer.toString());
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *          servlet request
     * @param response
     *          servlet response
     * @throws ServletException
     *          if a servlet-specific error occurs
     * @throws IOException
     *          if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *          servlet request
     * @param response
     *          servlet response
     * @throws ServletException
     *          if a servlet-specific error occurs
     * @throws IOException
     *          if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Spellchecker Servlet for TinyMCE";
    }
}
/*
 * Copyright (C) 2010 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.textoutput;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.plugin.WorkflowAction;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Workflow plug-in capable of generating textual output and storing it on a
 * local disk or transfer it by FTP. The plug-in could be used for generating
 * any kind of textual output of a news item or collection of news items.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowAction
public class TextOutputAction implements WorkflowAction {

    public static final String PROPERTY_OUTPUT_MODE = "output.mode";

    public static final String PROPERTY_OUTPUT_TYPE = "output.type";

    public static final String PROPERTY_OUTPUT_TEMPLATE = "output.template";

    public static final String PROPERTY_OUTPUT_COLLECTION_STATE =
            "output.collection.state";

    public static final String PROPERTY_OUTPUT_FILE_LOCATION =
            "output.file.location";

    public static final String PROPERTY_OUTPUT_FILE_FILENAME =
            "output.file.filename";

    public static final String PROPERTY_OUTPUT_FTP_ADDRESS =
            "output.ftp.address";

    public static final String PROPERTY_OUTPUT_FTP_PORT = "output.ftp.port";

    public static final String PROPERTY_OUTPUT_FTP_USERNAME =
            "output.ftp.username";

    public static final String PROPERTY_OUTPUT_FTP_PASSWORD =
            "output.ftp.password";

    public static final String PROPERTY_OUTPUT_FTP_LOCATION =
            "output.ftp.location";

    public static final String PROPERTY_OUTPUT_FTP_FILENAME =
            "output.ftp.filename";

    private static final Logger LOG = Logger.getLogger(TextOutputAction.class.
            getName());

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.textoutput.Messages");

    private Calendar releaseDate = new GregorianCalendar(2010, Calendar.JULY,
            30, 04, 00);

    private Map<String, String> properties = new HashMap<String, String>();

    private Map<String, String> availableProperties = null;

    @Override
    public void execute(PluginContext ctx, NewsItem item,
            WorkflowStepAction stepAction, UserAccount user) {
        this.properties = stepAction.getPropertiesAsMap();

        if (!validateProperties()) {
            LOG.log(Level.WARNING,
                    "Invalid properties. Cancel execution of {0}",
                    TextOutputAction.class.getName());
            return;
        }

        Map<String, Object> templateAttributes = new HashMap<String, Object>();
        templateAttributes.put("initiator", user);
        templateAttributes.put("newsitem", item);
        if (this.properties.get(PROPERTY_OUTPUT_TYPE).equalsIgnoreCase(
                "individual")) {
        } else if (this.properties.get(PROPERTY_OUTPUT_TYPE).equalsIgnoreCase(
                "collection")) {
            String stateName = this.properties.get(
                    PROPERTY_OUTPUT_COLLECTION_STATE);
            List<NewsItem> items = ctx.findNewsItemsByStateAndOutlet(stateName,
                    item.getOutlet());
            templateAttributes.put("newsitems", items);
        }

        String outputText = compileTemplate(this.properties.get(
                PROPERTY_OUTPUT_TEMPLATE), templateAttributes);

        String mode = this.properties.get(PROPERTY_OUTPUT_MODE);

        if ("file".equalsIgnoreCase(mode)) {
            saveToDisk(outputText);
        } else if ("ftp".equalsIgnoreCase(mode)) {
            sendToFtp(outputText);
        }
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            addProperty(PROPERTY_OUTPUT_MODE);
            addProperty(PROPERTY_OUTPUT_TYPE);
            addProperty(PROPERTY_OUTPUT_TEMPLATE);
            addProperty(PROPERTY_OUTPUT_COLLECTION_STATE);
            addProperty(PROPERTY_OUTPUT_FILE_LOCATION);
            addProperty(PROPERTY_OUTPUT_FILE_FILENAME);
            addProperty(PROPERTY_OUTPUT_FTP_ADDRESS);
            addProperty(PROPERTY_OUTPUT_FTP_PORT);
            addProperty(PROPERTY_OUTPUT_FTP_USERNAME);
            addProperty(PROPERTY_OUTPUT_FTP_PASSWORD);
            addProperty(PROPERTY_OUTPUT_FTP_LOCATION);
            addProperty(PROPERTY_OUTPUT_FTP_FILENAME);
        }
        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    @Override
    public Date getDate() {
        return releaseDate.getTime();
    }

    /**
     * Validates that sufficient properties has been provided to the plug-in
     * upon execution.
     *
     * @return {@code true} if sufficient properties has been provided for the
     *         plug-in to be executed, otherwise {@code false}
     */
    private boolean validateProperties() {
        if (!this.properties.containsKey(PROPERTY_OUTPUT_MODE)) {
            LOG.log(Level.WARNING, "{0} property missing",
                    PROPERTY_OUTPUT_MODE);
            return false;
        }

        if (!this.properties.containsKey(PROPERTY_OUTPUT_TYPE)) {
            LOG.log(Level.WARNING, "{0} property missing",
                    PROPERTY_OUTPUT_TYPE);
            return false;
        }

        if (!this.properties.containsKey(PROPERTY_OUTPUT_TEMPLATE)) {
            LOG.log(Level.WARNING, "{0} property missing",
                    PROPERTY_OUTPUT_TEMPLATE);
            return false;
        }

        String mode = this.properties.get(PROPERTY_OUTPUT_MODE);

        if (mode.equalsIgnoreCase("file")) {
            if (!this.properties.containsKey(PROPERTY_OUTPUT_FILE_LOCATION)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_FILE_LOCATION);
                return false;
            }
            if (!this.properties.containsKey(PROPERTY_OUTPUT_FILE_FILENAME)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_FILE_FILENAME);
                return false;
            }
        } else if (mode.equalsIgnoreCase("ftp")) {
            if (!this.properties.containsKey(PROPERTY_OUTPUT_FTP_ADDRESS)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_FTP_ADDRESS);
                return false;
            }
            if (!this.properties.containsKey(PROPERTY_OUTPUT_FTP_PORT)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_FTP_PORT);
                return false;
            }
            if (!this.properties.containsKey(PROPERTY_OUTPUT_FTP_FILENAME)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_FTP_FILENAME);
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Invalid value ''{0}'' provided for {1}",
                    new Object[]{mode, PROPERTY_OUTPUT_MODE});
            return false;
        }

        String type = this.properties.get(PROPERTY_OUTPUT_TYPE);

        if (type.equalsIgnoreCase("collection")) {
            if (!this.properties.containsKey(PROPERTY_OUTPUT_COLLECTION_STATE)) {
                LOG.log(Level.WARNING, "{0} property missing",
                        PROPERTY_OUTPUT_COLLECTION_STATE);
                return false;
            }
        } else if (type.equalsIgnoreCase("individual")) {
            // No validation required
        } else {
            LOG.log(Level.WARNING, "Invalid value ''{0}'' provided for {1}",
                    new Object[]{type, PROPERTY_OUTPUT_TYPE});
            return false;
        }

        return true;
    }

    /**
     * Compiles a given template with the given attributes.
     *
     * @param template
     *          Template to compile
     * @param attributes
     *          Attributes to interpolate
     * @return Compiled template
     */
    private String compileTemplate(String template,
            Map<String, Object> attributes) {
        StringTemplate stringTemplate = new StringTemplate(template,
                DefaultTemplateLexer.class);
        for (String attr : attributes.keySet()) {
            stringTemplate.setAttribute(attr, attributes.get(attr));
        }
        return stringTemplate.toString();
    }

    /**
     * Saves the text output to a file.
     *
     * @param outputText
     *          Text to save
     */
    private void saveToDisk(String outputText) {
        String location = this.properties.get(PROPERTY_OUTPUT_FILE_LOCATION);
        String filename = this.properties.get(PROPERTY_OUTPUT_FILE_FILENAME);
        try {
            FileWriter fstream = new FileWriter(new File(location, filename));
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(outputText);
            out.close();
        } catch (Exception e) {//Catch exception if any
            LOG.log(Level.SEVERE, "Failed writing file to disk. {0}", e.
                    getMessage());
            LOG.log(Level.FINE, "", e);
        }
    }

    /**
     * Sends the text output to a file over FTP.
     *
     * @param outputText
     *          Text to send
     */
    private void sendToFtp(String outputText) {
        String uid = this.properties.get(PROPERTY_OUTPUT_FTP_USERNAME);
        String pwd = this.properties.get(PROPERTY_OUTPUT_FTP_PASSWORD);

        String address = this.properties.get(PROPERTY_OUTPUT_FTP_ADDRESS);
        int port = 21;
        try {
            port = Integer.valueOf(this.properties.get(
                    PROPERTY_OUTPUT_FTP_PORT));
        } catch (Exception ex) {
            LOG.log(Level.WARNING,
                    "Could not read FTP port from properties. Using port 21");
        }
        String location = this.properties.get(PROPERTY_OUTPUT_FTP_LOCATION);
        String filename = this.properties.get(PROPERTY_OUTPUT_FTP_FILENAME);

        LOG.log(Level.INFO, "Uploading text output to {0}:{1}/{2}/{3}",
                new Object[]{address, port, location, filename});

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(address, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LOG.log(Level.SEVERE, "Could not connect to FTP server: {0}",
                        reply);
                return;
            }

            if (!ftpClient.login(uid, pwd)) {
                LOG.log(Level.SEVERE,
                        "Could not login to FTP server ({0}) with given credentials.",
                        address);
                return;
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            if (ftpClient.storeFile(location + "/" + filename,
                    new ByteArrayInputStream(outputText.getBytes()))) {
                LOG.log(Level.INFO, "Transfer complete");
            } else {
                LOG.log(Level.WARNING, "Transfer incomplete");
            }


        } catch (SocketException ex) {
            LOG.log(Level.SEVERE, "Could not transfer file.", ex.getMessage());
            LOG.log(Level.FINE, "", ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not transfer file.", ex.getMessage());
            LOG.log(Level.FINE, "", ex);
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not disconnect from FTP.", ex.
                        getMessage());
                LOG.log(Level.FINE, "", ex);
            }
        }
    }

    private void addProperty(String PROPERTY_OUTPUT_MODE) {
        availableProperties.put(bundle.getString(PROPERTY_OUTPUT_MODE),
                PROPERTY_OUTPUT_MODE);
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }
}

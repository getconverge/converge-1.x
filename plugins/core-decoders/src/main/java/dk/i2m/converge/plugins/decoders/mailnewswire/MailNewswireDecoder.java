/*
 * Copyright (C) 2010 - 2013 Interactive Media Manangement
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.decoders.mailnewswire;

import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.utils.FileUtils;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * Decoder for the Daily Mail newswire. Daily Mail is delivered via e-mail in a
 * ZIP file containing structured plain text.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.NewswireDecoder
public class MailNewswireDecoder implements NewswireDecoder {

    /**
     * IMAP transport. Possible value for {@link MailNewswireDecoder#TRANSPORT}.
     */
    public static final String TRANSPORT_IMAP = "imap";
    /**
     * IMAPS transport (secure). Possible value for
     * {@link MailNewswireDecoder#TRANSPORT}.
     */
    public static final String TRANSPORT_IMAPS = "imaps";
    /**
     * How is the newswire fetched. For now only IMAP is supported.
     */
    public static final String TRANSPORT = "TRANSPORT";
    /**
     * IMAP server name or IP address.
     */
    public static final String TRANSPORT_IMAP_SERVER = "TRANSPORT.IMAP.SERVER";
    /**
     * IMAP server port.
     */
    public static final String TRANSPORT_IMAP_PORT = "TRANSPORT.IMAP.PORT";
    /**
     * IMAP user name.
     */
    public static final String TRANSPORT_IMAP_USERNAME = "TRANSPORT.IMAP.USERNAME";
    /**
     * IMAP password.
     */
    public static final String TRANSPORT_IMAP_PASSWORD = "TRANSPORT.IMAP.PASSWORD";
    /**
     * IMAP folder containing the newswire mails.
     */
    public static final String TRANSPORT_IMAP_FOLDER_NEWSWIRE = "TRANSPORT.IMAP.FOLDER.NEWSWIRE";
    /**
     * IMAP folder to move the newswire once processed.
     */
    public static final String TRANSPORT_IMAP_FOLDER_PROCESSED = "TRANSPORT.IMAP.FOLDER.PROCESSED";
    /**
     * Should IMAP mails be deleted after being processed or moved to the
     * processed directory.
     */
    public static final String TRANSPORT_IMAP_DELETE_PROCESSED = "TRANSPORT.IMAP.DELETE_PROCESSED";
    private static final Logger LOG = Logger.getLogger(MailNewswireDecoder.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.decoders.mailnewswire.Messages");
    private Map<String, String> availableProperties = null;
    private Calendar releaseDate = new GregorianCalendar(2013, Calendar.FEBRUARY, 6, 21, 33);
    private String tempDirectory = "";

    /**
     * Creates a new instance of {@link MailNewswireDecoder}.
     */
    public MailNewswireDecoder() {
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

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(bundle.getString(TRANSPORT), TRANSPORT);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_SERVER), TRANSPORT_IMAP_SERVER);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_PORT), TRANSPORT_IMAP_PORT);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_USERNAME), TRANSPORT_IMAP_USERNAME);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_PASSWORD), TRANSPORT_IMAP_PASSWORD);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_FOLDER_NEWSWIRE), TRANSPORT_IMAP_FOLDER_NEWSWIRE);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_FOLDER_PROCESSED), TRANSPORT_IMAP_FOLDER_PROCESSED);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_DELETE_PROCESSED), TRANSPORT_IMAP_DELETE_PROCESSED);
        }
        return this.availableProperties;
    }

    @Override
    public void decode(PluginContext ctx, NewswireService newswire) {
        //tempDirectory = ctx.getWorkingDirectory();
        Map<String, Calendar> filesToProcess = new HashMap<String, Calendar>();

        StringBuilder workingDirectory = new StringBuilder(ctx.getWorkingDirectory());
        workingDirectory.append(File.separator);
        workingDirectory.append(newswire.getId());
        workingDirectory.append(File.separator);
        tempDirectory = workingDirectory.toString();

        Map<String, String> properties = newswire.getPropertiesMap();

        if (properties.containsKey(TRANSPORT)) {
            String transport = properties.get(TRANSPORT);

            if (transport.equalsIgnoreCase(TRANSPORT_IMAP) || transport.equalsIgnoreCase(TRANSPORT_IMAPS)) {
                String host = properties.get(TRANSPORT_IMAP_SERVER);
                String port = properties.get(TRANSPORT_IMAP_PORT);
                String username = properties.get(TRANSPORT_IMAP_USERNAME);
                String password = properties.get(TRANSPORT_IMAP_PASSWORD);
                String folder_newswire = properties.get(TRANSPORT_IMAP_FOLDER_NEWSWIRE);
                String folder_processed = properties.get(TRANSPORT_IMAP_FOLDER_PROCESSED);
                boolean deleteAfterProcess = Boolean.parseBoolean(properties.get(TRANSPORT_IMAP_DELETE_PROCESSED));

                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                if (LOG.isLoggable(Level.FINEST)) {
                    session.setDebug(true);
                }

                javax.mail.Store store;
                try {
                    store = session.getStore(transport);
                    store.connect(host, Integer.valueOf(port), username, password);

                    Folder folder = store.getFolder(folder_newswire);

                    folder.open(Folder.READ_WRITE);

                    for (Message msg : folder.getMessages()) {
                        Calendar msgSent = Calendar.getInstance();
                        if (msg.getSentDate() != null) {
                            msgSent.setTime(msg.getSentDate());
                        } else if (msg.getReceivedDate() != null) {
                            msgSent.setTime(msg.getReceivedDate());
                        }

                        NewswireItem item = new NewswireItem();
                        item.setDate(msgSent);
                        item.setNewswireService(newswire);
                        item.setTitle(msg.getSubject());

                        try {
                            long id = ((UIDFolder) folder).getUID(msg);
                            item.setExternalId(String.valueOf(id));
                        } catch (ClassCastException ex) {
                            LOG.log(Level.WARNING, ex.getMessage());
                            item.setExternalId("");
                        }
                        item.setUrl("");

                        Address[] recipients = msg.getFrom();
                        if (recipients != null) {
                            InternetAddress sender = (InternetAddress) recipients[0];
                            if (sender.getPersonal() != null && !sender.getPersonal().trim().isEmpty()) {
                                item.setAuthor(sender.getPersonal());
                            } else {
                                item.setAuthor(sender.toString());
                            }
                        }

                        LOG.log(Level.INFO, "Processing mail from {0} with subject ''{1}''", new Object[]{item.getAuthor(), msg.getSubject()});
                        if (msg.getContent() instanceof Multipart) {
                            Multipart multipart = (Multipart) msg.getContent();
                            processMultipart(item, multipart);
                        } else {
                            String content = msg.getContent().toString();
                            content = StringEscapeUtils.escapeHtml(content);
                            content = content.replaceAll(System.getProperty("line.separator"), "<br/>");
                            item.addContent(content);

                            item.setSummary(StringUtils.abbreviate(content, 400).replaceAll(System.getProperty("line.separator"), " "));
                        }

                        boolean deleteMsg = true;

                        if (!deleteAfterProcess) {
                            try {
                                Folder processedFolder = store.getFolder(folder_processed);
                                processedFolder.open(Folder.READ_WRITE);
                                processedFolder.appendMessages(new Message[]{msg});
                                processedFolder.close(true);
                            } catch (FolderNotFoundException fnfex) {
                                deleteMsg = false;
                                LOG.log(Level.WARNING, "Could not open folder ''{0}''", new Object[]{folder_processed});
                            }
                        }

                        if (deleteMsg) {
                            msg.setFlag(Flags.Flag.DELETED, true);
                        }
                        item = ctx.createNewswireItem(item);
                        try {
                            ctx.index(item);
                        } catch (SearchEngineIndexingException seie) {
                            LOG.log(Level.SEVERE, seie.getMessage());
                            LOG.log(Level.FINEST, "", seie);
                        }
                    }
                    folder.close(true);
                    store.close();
                } catch (NoSuchProviderException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINEST, "", ex);
                } catch (MessagingException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINEST, "", ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINEST, "", ex);
                }
            } else {
                LOG.log(Level.SEVERE, "Unknown transport ''{0}''", transport);
            }
        } else {
            LOG.log(Level.SEVERE, "Property '" + TRANSPORT + "' is missing");
        }
    }

    private static byte[] getAttachment(InputStream input) throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        BufferedInputStream bis = new BufferedInputStream(input);
        int aByte;
        while ((aByte = bis.read()) != -1) {
            bos.write(aByte);
        }
        bos.flush();
        bos.close();
        bis.close();
        return fos.toByteArray();
    }

    private void readStories(Map<String, NewswireItem> foundItems, ZipFile zf) {
        Enumeration<? extends ZipEntry> files = zf.entries();
        while (files.hasMoreElements()) {
            ZipEntry ze = files.nextElement();

            if (foundItems.containsKey(FileUtils.getFilename(ze.getName()))) {
                NewswireItem item = foundItems.get(FileUtils.getFilename(ze.getName()));
                String section = FileUtils.getFolder(ze.getName());
                item.setTitle(section + " - "
                        + WordUtils.capitalizeFully(item.getTitle().
                        toLowerCase()));

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        item.addContent(StringEscapeUtils.escapeHtml(line));
                        item.addContent("<br/>");
                    }
                    br.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINEST, "", ex);
                } finally {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage());
                        LOG.log(Level.FINEST, "", ex);
                    }
                }
            } else {
                LOG.log(Level.WARNING,
                        "Ignoring {0} as it was not detected in the schedule",
                        ze.getName());
            }
        }

    }

    private void processMultipart(NewswireItem item, Multipart multipart) {
        try {
            LOG.log(Level.FINEST, "Processing {0} with {1} parts",
                    new Object[]{multipart.getContentType(), multipart.getCount()});
            for (int i = 0, n = multipart.getCount(); i < n; i++) {
                Part part = multipart.getBodyPart(i);

                if (part.getContent() instanceof Multipart) {
                    LOG.log(Level.FINEST, "Part is multipart");
                    processMultipart(item, (Multipart) part.getContent());
                }

                String disposition = part.getDisposition();
                LOG.log(Level.FINEST, "Disposition {0} Content Type {1}",
                        new Object[]{disposition, part.getContentType()});

                if (part.getContentType().startsWith("text/plain")) {
                    String content = (String) part.getContent();
                    content = StringEscapeUtils.escapeHtml(content);
                    content = content.replaceAll(System.getProperty(
                            "line.separator"), "<br/>");
                    item.addContent(content);
                } else if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT) || (disposition.equalsIgnoreCase(Part.INLINE))))) {
                    NewswireItemAttachment attachment =
                            new NewswireItemAttachment();
                    attachment.setNewswireItem(item);
                    attachment.setContentType(part.getContentType());
                    attachment.setFilename(part.getFileName());
                    attachment.setData(getAttachment(part.getInputStream()));
                    attachment.setSize(part.getSize());
                    item.getAttachments().add(attachment);
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        } catch (MessagingException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }

    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }
}

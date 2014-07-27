/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
 *  Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;
import org.scannotation.archiveiterator.StreamIterator;

/**
 * Singleton responsible for discovering available {@link Plugin}s.
 *
 * @author Allan Lykke Christensen
 */
public final class PluginManager {

    private static PluginManager instance = null;

    private static final Logger LOG = Logger.getLogger(PluginManager.class.getName());

    private final Map<String, NewswireDecoder> newswireDecoders = new HashMap<String, NewswireDecoder>();

    private final Map<String, WorkflowAction> workflowActions = new HashMap<String, WorkflowAction>();

    private final Map<String, EditionAction> outletActions = new HashMap<String, EditionAction>();

    private final Map<String, WorkflowValidator> workflowValidators = new HashMap<String, WorkflowValidator>();

    private final Map<String, CatalogueHook> catalogueActions = new HashMap<String, CatalogueHook>();

    private final Map<String, NewsItemAction> newsItemActions = new HashMap<String, NewsItemAction>();

    /**
     * Private constructor for the singleton {@link PluginManager}.
     */
    private PluginManager() {
        discover();
    }

    /**
     * Gets an instance of the {@link PluginManager}.
     *
     * @return Instance of the {@link PluginManager}
     */
    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public Map<String, Plugin> getPlugins() {
        Map<String, Plugin> plugins = new LinkedHashMap<String, Plugin>();
        plugins.putAll(newswireDecoders);
        plugins.putAll(workflowActions);
        plugins.putAll(outletActions);
        plugins.putAll(workflowValidators);
        plugins.putAll(catalogueActions);
        plugins.putAll(newsItemActions);
        return plugins;
    }

    public Map<String, NewswireDecoder> getNewswireDecoders() {
        return newswireDecoders;
    }

    public Map<String, WorkflowAction> getWorkflowActions() {
        return workflowActions;
    }

    public Map<String, EditionAction> getOutletActions() {
        return outletActions;
    }

    public Map<String, WorkflowValidator> getWorkflowValidators() {
        return workflowValidators;
    }

    public Map<String, CatalogueHook> getCatalogueActions() {
        return catalogueActions;
    }

    public Map<String, NewsItemAction> getNewsItemActions() {
        return newsItemActions;
    }

    public int discover() {
        return discover(ClasspathUrlFinder.findClassBase(getClass()));
    }

    /**
     * Discovers available plug-ins.
     *
     * @return Number of plug-ins discovered
     */
    public int discover(URL url) {
        LOG.log(Level.INFO, "Discovering plug-in in {0}", url.toString());

        Filter filter = new Filter() {
            @Override
            public boolean accepts(String filename) {
                LOG.log(Level.INFO, "Include {0}", filename);
                if (filename.endsWith(".class")) {
                    if (filename.startsWith("/")) {
                        filename = filename.substring(1);
                    }
                    LOG.log(Level.INFO, "Yes - Check {0}", filename);
                    return true;
                }
                return false;
            }
        };

        try {
            StreamIterator it = IteratorFactory.create(url, filter);

            InputStream stream;
            while ((stream = it.next()) != null) {
            }
        } catch (IOException ex) {

        }

        int discoveredPlugins = 0;
        AnnotationDB db = new AnnotationDB();

//        try {
//            URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
//            URL[] preClassPaths = cl.getURLs();
//
//            List<URL> postClassPaths = new ArrayList<URL>();
//            for (URL url : preClassPaths) {
//                URL newURL;
//                if (url.toString().startsWith("/")) {
//                    newURL = new URL("file:" + url.toString());
//                } else {
//                    newURL = url;
//                }
//
//                try {
//                    newURL.openStream();
//                    postClassPaths.add(newURL);
//                } catch (FileNotFoundException fnfe) {
//                    LOG.log(Level.FINEST, "{0} was not found", newURL.toString());
//                }
//            }
        //db.scanArchives(postClassPaths.toArray(new URL[postClassPaths.size()]));
        try {
            db.scanArchives(url);
            LOG.log(Level.INFO, "{0} different annotations found", db.getAnnotationIndex().size());
            for (String key : db.getAnnotationIndex().keySet()) {
                LOG.log(Level.INFO, "Annotation: {0} of {1} found", new Object[]{db.getAnnotationIndex().get(key).size(), key});
            }

            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.NewswireDecoder.class, newswireDecoders);
            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.WorkflowAction.class, workflowActions);
            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.OutletAction.class, outletActions);
            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.WorkflowValidator.class, workflowValidators);
            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.CatalogueAction.class, catalogueActions);
            discoveredPlugins += discoverPlugins(db, dk.i2m.converge.core.annotations.NewsItemAction.class, newsItemActions);
            LOG.log(Level.INFO, "{0} {0, choice, 0#plugins|1#plugin|2#plugins} discovered", discoveredPlugins);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not discover plug-ins. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }

        return discoveredPlugins;
    }

    private int discoverPlugins(AnnotationDB db, Class type, Map registry) {
        registry.clear();
        Set<String> entities = db.getAnnotationIndex().get(type.getName());

        if (entities == null) {
            LOG.log(Level.INFO, "No {0} plug-ins found in classpath", type.getName());
        } else {
            for (String clazz : entities) {
                LOG.log(Level.INFO, "Plug-in ''{0}'' found", clazz);
                try {
                    Class c = Class.forName(clazz);
                    Plugin plugin = (Plugin) c.newInstance();
                    registry.put(plugin.getName(), plugin);
                } catch (ClassNotFoundException e) {
                    LOG.log(Level.SEVERE, e.getMessage());
                    LOG.log(Level.FINEST, "", e);
                } catch (InstantiationException e) {
                    LOG.log(Level.SEVERE, e.getMessage());
                    LOG.log(Level.FINEST, "", e);
                } catch (IllegalAccessException e) {
                    LOG.log(Level.SEVERE, e.getMessage());
                    LOG.log(Level.FINEST, "", e);
                }
            }
        }
        return registry.size();
    }
}

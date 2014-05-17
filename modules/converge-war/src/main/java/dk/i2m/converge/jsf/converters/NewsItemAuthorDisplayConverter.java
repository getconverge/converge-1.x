/*
 *  $URL$
 *  $Rev$
 *  $Date$
 *  $Id$
 *  Copyright 2009 Interactive Media Management.
 */
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.content.NewsItem;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Display {@link Converter} for showing a comma separated list of
 * {@link NewsItem} authors.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemAuthorDisplayConverter implements Converter {

    private static final Log log = LogFactory.getLog(
            NewsItemAuthorDisplayConverter.class);

    public Object getAsObject(FacesContext ctx, UIComponent component,
            String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAsString(FacesContext ctx, UIComponent component,
            Object value) {
        if (!(value instanceof NewsItem)) {
            log.error("Object is not an instance of NewsItem but " + value.
                    getClass().getCanonicalName());
            return "";
        }

        NewsItem newsItem = (NewsItem) value;

        if (newsItem == null) {
            log.error("Object is null");
            return "";
        }

        return "NOT YET IMPLEMENTED"; //NewsItemHelper.formatAuthors(newsItem);
    }
}

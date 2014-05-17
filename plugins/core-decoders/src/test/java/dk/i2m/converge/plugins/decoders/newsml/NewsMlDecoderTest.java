/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.plugins.decoders.newsml;

import org.junit.Ignore;
import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.newswire.NewswireItem;
import java.util.List;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.newswire.NewswireServiceProperty;
import dk.i2m.converge.core.plugin.PluginContext;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Simple unit test for the NewsML decoder.
 *
 * @author Allan Lykke Christensen
 */
public class NewsMlDecoderTest {

    @Test
    @Ignore
    public void testPlainDecode() throws Exception {
        String newsDir = "src/test/resources/dk/i2m/converge/plugins/decoders/newsml/v1_0/plain";
        final int numResults = 3;

        PluginContext mockCtx = createMock(PluginContext.class);
        expect(mockCtx.getWorkingDirectory()).andReturn(newsDir).anyTimes();
        expect(mockCtx.findOrCreateContentTag(anyObject(String.class))).andReturn(new ContentTag()).anyTimes();
        expect(mockCtx.findNewswireItemsByExternalId(anyObject(String.class))).andReturn(Collections.EMPTY_LIST).times(numResults);
        expect(mockCtx.createNewswireItem(anyObject(NewswireItem.class))).andReturn(new NewswireItem()).times(numResults);

        checkOrder(mockCtx, false);
        replay(mockCtx);

        NewsMLDecoder decoder = new NewsMLDecoder();
        NewswireService service = new NewswireService();
        service.setId(999L);
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_LOCATION.name(), newsDir));
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION.name(), newsDir));
//        List<NewswireItem> items = decoder.decode(mockCtx, service);

//        assertEquals(numResults, items.size());
    }

    @Test
    @Ignore
    public void testImageDecode() throws Exception {
        String newsDir = "src/test/resources/dk/i2m/converge/plugins/decoders/newsml/v1_0/images";
        final int numResults = 2;

        PluginContext mockCtx = createMock(PluginContext.class);
        expect(mockCtx.getWorkingDirectory()).andReturn(newsDir).anyTimes();
        expect(mockCtx.findOrCreateContentTag(anyObject(String.class))).andReturn(new ContentTag()).anyTimes();
        expect(mockCtx.findNewswireItemsByExternalId(anyObject(String.class))).andReturn(Collections.EMPTY_LIST).times(numResults);
        expect(mockCtx.createNewswireItem(anyObject(NewswireItem.class))).andReturn(new NewswireItem()).times(numResults);

        checkOrder(mockCtx, false);
        replay(mockCtx);

        NewsMLDecoder decoder = new NewsMLDecoder();
        NewswireService service = new NewswireService();
        service.setId(999L);
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_LOCATION.name(), newsDir));
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION.name(), newsDir));
    //    List<NewswireItem> items = decoder.decode(mockCtx, service);

//        assertEquals(numResults, items.size());
    }

    @Test
    @Ignore
    public void testGraphicsDecode() throws Exception {
        String newsDir = "src/test/resources/dk/i2m/converge/plugins/decoders/newsml/v1_0/graphics";
        final int numResults = 1;

        PluginContext mockCtx = createMock(PluginContext.class);
        expect(mockCtx.getWorkingDirectory()).andReturn(newsDir).anyTimes();
        expect(mockCtx.findOrCreateContentTag(anyObject(String.class))).andReturn(new ContentTag()).anyTimes();
        expect(mockCtx.findNewswireItemsByExternalId(anyObject(String.class))).andReturn(Collections.EMPTY_LIST).times(numResults);
        expect(mockCtx.createNewswireItem(anyObject(NewswireItem.class))).andReturn(new NewswireItem()).times(numResults);

        checkOrder(mockCtx, false);
        replay(mockCtx);

        NewsMLDecoder decoder = new NewsMLDecoder();
        NewswireService service = new NewswireService();
        service.setId(999L);
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_LOCATION.name(), newsDir));
        service.getProperties().add(new NewswireServiceProperty(service, NewsMLDecoder.Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION.name(), newsDir));
  //      List<NewswireItem> items = decoder.decode(mockCtx, service);

  //      assertEquals(numResults, items.size());
    }
}

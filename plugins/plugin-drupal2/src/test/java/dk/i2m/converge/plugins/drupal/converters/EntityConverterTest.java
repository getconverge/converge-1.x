/*
 * Copyright (C) 2016 Allan Lykke Christensen
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
package dk.i2m.converge.plugins.drupal.converters;

import dk.i2m.converge.plugins.drupal.entities.NodeEntity;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class EntityConverterTest {

    @Test
    public void entityConverter_initializedNodeEntityWithValues_returnMapOfFieldsAndValues() {
        // Arrange
        EntityConverter entityConverter = new EntityConverter();
        NodeEntity entity = getInitializedNodeEntityWithValues();
        Map<String, String> expResult = new HashMap<String, String>();
        expResult.put("nid", entity.getId().toString());
        expResult.put("title", entity.getTitle());
        expResult.put("promote", entity.getPromote().toString());
        expResult.put("changed", entity.getChanged().toString());
        expResult.put("comment", entity.getComment().toString());
        expResult.put("created", entity.getCreated().toString());
        expResult.put("language", entity.getLanguage());
        expResult.put("status", entity.getStatus().toString());
        expResult.put("sticky", entity.getSticky().toString());
        expResult.put("tnid", entity.getTnid().toString());
        expResult.put("translate", entity.getTranslate().toString());
        expResult.put("type", entity.getType());
        expResult.put("uid", entity.getUid().toString());
        expResult.put("vid", entity.getVid().toString());

        // Act
        Map<String, String> result = entityConverter.convert(entity);

        // Assert
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult, result);
    }

    private NodeEntity getInitializedNodeEntityWithValues() {
        NodeEntity entity = new NodeEntity();
        entity.setId(1L);
        entity.setTitle("Title of the node");
        entity.setPromote(2);
        entity.setChanged(3L);
        entity.setComment(4);
        entity.setCreated(5L);
        entity.setLanguage("English");
        entity.setStatus(6);
        entity.setSticky(7);
        entity.setTnid(8L);
        entity.setTranslate(9);
        entity.setType("article");
        entity.setUid(10L);
        entity.setVid(11L);
        return entity;
    }

}

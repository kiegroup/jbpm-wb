package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemObjectModelFactoryTest {

    private ItemObjectModelFactory itemObjectModelFactory = new ItemObjectModelFactory();

    @Test
    public void testNewObjectItemModel() {
        final ItemObjectModel model = itemObjectModelFactory.newItemObjectModel("Value");

        assertEquals("Value", model.getValue());
        assertEquals("mvel", model.getResolver());
        assertTrue(model.getParameters().isEmpty());
    }

    @Test
    public void testNewNamedObjectItemModel() {
        final ItemObjectModel model = itemObjectModelFactory.newItemObjectModel("Name", "Value");

        assertEquals("Name", model.getName());
        assertEquals("Value", model.getValue());
        assertEquals("mvel", model.getResolver());
        assertTrue(model.getParameters().isEmpty());
    }
}
package org.imixs.workflow.jee.ejb;

import org.imixs.workflow.ItemCollection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** 
 * Test class for itemCollection object
 * 
 * @author rsoika
 * 
 */
public class TestItem { 

	@Test
	@Category(org.imixs.workflow.ItemCollection.class)
	public void testItemCollection() {
		ItemCollection item = new ItemCollection();
		item.replaceItemValue("txtTitel", "Hello");
		Assert.assertEquals(item.getItemValueString("txttitel"), "Hello");
	}

	@Test
	@Category(org.imixs.workflow.ItemCollection.class)
	public void testRemoveItem() {
		ItemCollection item = new ItemCollection();
		item.replaceItemValue("txtTitel", "Hello");
		Assert.assertEquals(item.getItemValueString("txttitel"), "Hello");
		Assert.assertTrue(item.hasItem("txtTitel"));
		item.removeItem("TXTtitel");
		Assert.assertFalse(item.hasItem("txtTitel"));
		Assert.assertEquals(item.getItemValueString("txttitel"), "");
	}

}

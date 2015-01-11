package org.imixs.workflow.jee.ejb;

import java.util.Collection;

import javax.naming.InitialContext;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.exceptions.ModelException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.appserv.security.ProgrammaticLogin;

/**
 * Test class for ModelService
 * 
 * @author rsoika
 * 
 */
public class TestModelService extends AbstractTestService {

	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testService() {

		Assert.assertNotNull(modelService);

	}

	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testModelversion() {

		try {
			String sModel = null;
			sModel = modelService.getLatestVersion();

			Assert.assertEquals("1.0.0", sModel);

			Collection<ItemCollection> col = modelService
					.getActivityEntityListByVersion(10, sModel);

			Assert.assertTrue(col.size() > 0);
		} catch (ModelException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}

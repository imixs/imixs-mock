package org.imixs.workflow.jee.ejb;

import java.util.List;
import java.util.Vector;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test class for EntityService. This test tries to lookup the EntityService
 * remote from a local GlassFish instance.
 * 
 * To run the test the imixs-workflow-web-sample-0.0.7 need to be deployed
 * before. Also in Eclipse IDE you need to add the gf_client.jar into the
 * projects class path! The gf_client.jar is located in your Glassfish
 * Installation at:
 * 
 * $GLASSFISH_HOME/glassfish/lib/gf-client.jar
 * 
 * See the following blog entry for more details
 * http://www.imixs.org/roller/ralphsjavablog/entry/junit_and_glassfish_3_1
 * 
 * 
 * Before you can run the test successful make also sure that
 * imixs-workflow-web-sample-0.0.7 is deployed on the GlassFish instance!
 * 
 * 
 * 
 * @author rsoika
 * @version 3.1.0 - tested with GlassFish 3.1.2
 */
public class TestEntityService extends AbstractTestService {

	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testService() {

		Assert.assertNotNull(entityService);

		logger.info("Setup sucessfull");

	}

	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testSave() {
		String sUID = null;

		Assert.assertNotNull(entityService);

		em.getTransaction().begin();
		logger.info("#---Transaction 1 BEGIN ----#");

		ItemCollection itemcol = createTestItemCollection(Thread
				.currentThread().getStackTrace());
		String sName = itemcol.getItemValueString("txtName");
		itemcol.replaceItemValue("$WriteAccess", "Anna");
		try {
			itemcol = entityService.save(itemcol);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals(sName, itemcol.getItemValueString("txtNAME"));
		sUID = itemcol.getItemValueString("$Uniqueid");
		removeList.add(sUID);

		if ("".equals(sUID))
			Assert.fail();

		logger.info("$UniqueID=" + sUID);
		em.getTransaction().commit();
		logger.info("#---Transaction 1 END ----#");

		// tranaction 2
		em.getTransaction().begin();
		logger.info("#---Transaction 2 BEGIN ----#");

		itemcol = entityService.load(sUID);
		Assert.assertEquals(sName, itemcol.getItemValueString("txtNAME"));

		logger.info("$Version=" + itemcol.getItemValueInteger("$Version"));

		em.getTransaction().commit();
		logger.info("#---Transaction 2 END ----#");

		// tranaction 3
		em.getTransaction().begin();
		logger.info("#---Transaction 3 BEGIN ----#");

		itemcol = entityService.load(sUID);

		Assert.assertEquals(1, itemcol.getItemValueInteger("$Version"));

		try {
			itemcol = entityService.save(itemcol);
			itemcol = entityService.save(itemcol);
			itemcol = entityService.save(itemcol);
			itemcol = entityService.save(itemcol);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			Assert.fail();
		}

		// version no 5 - because of 4 additional saves
		Assert.assertEquals(5, itemcol.getItemValueInteger("$Version"));

		em.getTransaction().commit();
		logger.info("#---Transaction 3 END ----#");

		// tranaction 4
		em.getTransaction().begin();
		logger.info("#---Transaction 4 BEGIN ----#");

		itemcol = entityService.load(sUID);

		// still version = 5
		Assert.assertEquals(5, itemcol.getItemValueInteger("$Version"));

		em.getTransaction().commit();
		logger.info("#---Transaction 4 END ----#");

	}

	/**
	 * This Test verfies the version number controlled by the JPA /
	 * EntityService
	 */
	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testVersionNumber() {
		String sUID = null;
		Integer iVersion = -1;

		Assert.assertNotNull(entityService);

		em.getTransaction().begin();
		logger.info("#---Transaction 1 BEGIN ----#");

		ItemCollection itemcol = createTestItemCollection(Thread
				.currentThread().getStackTrace());
		itemcol.replaceItemValue("$WriteAccess", "Anna");
		try {
			itemcol = entityService.save(itemcol);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			Assert.fail();
		}
		em.getTransaction().commit();

		sUID = itemcol.getItemValueString("$Uniqueid");
		removeList.add(sUID);
		iVersion = itemcol.getItemValueInteger("$Version");

		logger.info("$Version=" + iVersion);
		Assert.assertTrue(1 == iVersion);

		// now load the entity
		itemcol = entityService.load(sUID);
		logger.info("$Version=" + itemcol.getItemValueInteger("$Version"));

		em.getTransaction().begin();
		itemcol = entityService.load(sUID);
		logger.info("$Version=" + itemcol.getItemValueInteger("$Version"));
		em.getTransaction().commit();

		itemcol = entityService.load(sUID);
		logger.info("$Version=" + itemcol.getItemValueInteger("$Version"));

	}

	/**
	 * Issue #166.
	 * 
	 * Szenario:
	 * 
	 * 2 workitems. 1st created in transaction 1
	 * 
	 * both have the same index fields!!!
	 * 
	 * 2nd transcation 1st worktiem is saved , 2nd workitem is created and saved
	 * 
	 * Result: index of 1st workitem lost???
	 * 
	 */
	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testIssue166() {

		ItemCollection workitem1 = null;
		ItemCollection workitem2 = null;

		String sUID = null;

		Assert.assertNotNull(entityService);

		/* Create first workitem */

		em.getTransaction().begin();
		logger.info("#---Transaction 1 BEGIN ----#");

		workitem1 = createTestItemCollection(Thread.currentThread()
				.getStackTrace());
		String sName = workitem1.getItemValueString("txtName");
		workitem1.replaceItemValue("_subject", "1st workitem");
		workitem1.replaceItemValue("type", "workitem");
		try {
			workitem1 = entityService.save(workitem1);

		} catch (AccessDeniedException e) {

			e.printStackTrace();
			Assert.fail();
		}

		em.getTransaction().commit();
		sUID = workitem1.getItemValueString(EntityService.UNIQUEID);
		removeList.add(sUID);
		logger.info("workitem1 UID=" + sUID);
		logger.info("#---Transaction 1 END ----#");

		// search workitem
		String sQuery = "SELECT entity FROM Entity entity "
				+ "JOIN entity.textItems AS t "
				+ "  WHERE entity.type='workitem' "
				+ "  AND t.itemName = 'txtname' " + "  AND t.itemValue = '"
				+ sName + "' " + " ORDER BY entity.modified DESC";

		List<ItemCollection> result = entityService.findAllEntities(sQuery, 0,
				-1);

		Assert.assertEquals(1, result.size());

		ItemCollection testEntity = result.get(0);
		Assert.assertEquals(sUID,
				testEntity.getItemValueString(EntityService.UNIQUEID));

		// now create new workitem and save both in one transaction

		em.getTransaction().begin();
		logger.info("#---Transaction 2 BEGIN ----#");

		workitem2 = createTestItemCollection(Thread.currentThread()
				.getStackTrace());
		workitem2.replaceItemValue("_subject", "2st workitem");
		workitem2.replaceItemValue("type", "workitem");

		try {
			workitem1 = entityService.load(sUID);

			loginAsManager();
			// load 1st
			workitem1 = entityService.load(sUID);
			workitem1.replaceItemValue("_subject", "1st workitem modified");

			workitem1 = entityService.save(workitem1);

			loginAsAuthor();

			workitem2 = entityService.save(workitem2);
			removeList
					.add(workitem2.getItemValueString(EntityService.UNIQUEID));
		} catch (AccessDeniedException e) {

			e.printStackTrace();
			Assert.fail();
		}

		em.getTransaction().commit();
		logger.info("#---Transaction 2 END ----#");

		// search workitem

		result = entityService.findAllEntities(sQuery, 0, -1);

		Assert.assertEquals(2, result.size());

	}

	/**
	 * Issue #189
	 * 
	 * This test verifies the save method when a complex data structure is
	 * stored.
	 * 
	 * In some strange case this leads into a situation when the entity is
	 * committed twice by the enityManager. When the entity is detached after
	 * the ItemCollecition was constructed everything works fine. Otherwise an
	 * optimistic locking exception is thrown by the EntiyManager.
	 * 
	 * 
	 * This test tests if a complex data structure can be saved correctly.
	 * 
	 * 
	 */
	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testIssue189() {
		String sUID = null;
		Integer iVersion = -1;

		Assert.assertNotNull(entityService);

		em.getTransaction().begin();
		logger.info("#---Transaction 1 BEGIN ----#");

		ItemCollection itemcol = createTestItemCollection(Thread
				.currentThread().getStackTrace());

		// add a complex data structure (file data with byte array) into the
		// itemCollection.
		byte[] empty = { 0 };
		itemcol.addFile(empty, "test.txt", "");

		// test 1st save in one transaction
		try {
			itemcol = entityService.save(itemcol);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			Assert.fail();
		}
		em.getTransaction().commit();
		logger.info("#---Transaction 1 END ----#");

		// expected version number = 1
		sUID = itemcol.getItemValueString("$Uniqueid");
		removeList.add(sUID);
		iVersion = itemcol.getItemValueInteger("$Version");
		logger.info("after 1st save in one trasaction - $Version=" + iVersion);
		Assert.assertEquals(new Integer(1), iVersion);

		// now load the entity - again the version number need to be 1!!!
		itemcol = entityService.load(sUID);
		iVersion = itemcol.getItemValueInteger("$Version");
		logger.info("after load                - $Version=" + iVersion);
		Assert.assertEquals(new Integer(1), iVersion);

		// now lets test 2nd save in one transaction
		em.getTransaction().begin();
		logger.info("#---Transaction 2 BEGIN ----#");
		try {
			itemcol = entityService.save(itemcol);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			Assert.fail();
		}
		em.getTransaction().commit();
		logger.info("#---Transaction 2 END ----#");

		// expected version number = 2
		iVersion = itemcol.getItemValueInteger("$Version");
		logger.info("after 1st save in one trasaction - $Version=" + iVersion);
		Assert.assertEquals(new Integer(2), iVersion);

		// now load the entity - again the version number need to be 2!!!
		itemcol = entityService.load(sUID);
		iVersion = itemcol.getItemValueInteger("$Version");
		logger.info("after load                - $Version=" + iVersion);
		Assert.assertEquals(new Integer(2), iVersion);

	}

	/**
	 * Issue #59.
	 * 
	 * Szenario:
	 * 
	 * 2 workitems. both created in transaction 1, workitem2 points to workitem1
	 * 
	 * then in a new transaction workitem2 updated and 2 times saved and
	 * workitem 1 one time saved
	 * 
	 * test the $uniqueidref and type of workitem2
	 * 
	 */
	@Test
	@Category(org.imixs.workflow.jee.ejb.EntityServiceRemote.class)
	public void testIssue59() {

		ItemCollection workitem1 = null;
		ItemCollection workitem2 = null;

		String sUID1 = null;
		String sUID2 = null;

		Assert.assertNotNull(entityService);

		/* Create workitems */

		em.getTransaction().begin();
		logger.info("#---Transaction 1 BEGIN ----#");

		try {

			workitem1 = createTestItemCollection(Thread.currentThread()
					.getStackTrace());
			String sName = workitem1.getItemValueString("txtName");
			workitem1.replaceItemValue("_subject", "1st workitem");
			workitem1.replaceItemValue("type", "workitem");
			workitem1.replaceItemValue(EntityService.UNIQUEID,
					WorkflowKernel.generateUniqueID());
			workitem2 = createTestItemCollection(Thread.currentThread()
					.getStackTrace());

			workitem2.replaceItemValue("_subject", "1st workitem");
			workitem2.replaceItemValue("type", "workitemlob");
			workitem2.replaceItemValue(EntityService.UNIQUEID,
					WorkflowKernel.generateUniqueID());
			workitem2.replaceItemValue(EntityService.UNIQUEIDREF,
					workitem1.getItemValueString(EntityService.UNIQUEID));

			workitem1 = entityService.save(workitem1);
			workitem2 = entityService.save(workitem2);

		} catch (AccessDeniedException e) {

			e.printStackTrace();
			Assert.fail();
		}

		em.getTransaction().commit();
		sUID1 = workitem1.getItemValueString(EntityService.UNIQUEID);
		removeList.add(sUID1);
		logger.info("workitem1 UID=" + sUID1);
		sUID2 = workitem2.getItemValueString(EntityService.UNIQUEID);
		removeList.add(sUID2);
		logger.info("workitem2 UID=" + sUID2);

		logger.info("#---Transaction 1 END ----#");

		// 2nd Transaction..

		em.getTransaction().begin();
		logger.info("#---Transaction 2 BEGIN ----#");

		workitem1 = entityService.load(sUID1);
		workitem2 = entityService.load(sUID2);

		workitem1.replaceItemValue("somefield", "somedata");

		workitem2.replaceItemValue("type", "workitemlob");
		workitem2.replaceItemValue(EntityService.UNIQUEIDREF,
				workitem1.getItemValueString(EntityService.UNIQUEID));

		// save...

		workitem1 = entityService.save(workitem1);
		workitem2 = entityService.save(workitem2);

		List v1 = workitem2.getItemValue("type");
		List v2 = workitem2.getItemValue(EntityService.UNIQUEIDREF);

		Assert.assertEquals(1, v1.size());
		Assert.assertEquals(1, v2.size());

		em.getTransaction().commit();
		logger.info("#---Transaction 2 END ----#");

		// 3nd Transaction..verify....

		em.getTransaction().begin();
		logger.info("#---Transaction 3 BEGIN ----#");

		workitem1 = entityService.load(sUID1);
		workitem2 = entityService.load(sUID2);

		v1 = workitem2.getItemValue("type");
		v2 = workitem2.getItemValue(EntityService.UNIQUEIDREF);

		Assert.assertEquals(1, v1.size());
		Assert.assertEquals(1, v2.size());

		Assert.assertEquals(
				workitem1.getItemValueString(EntityService.UNIQUEID),
				workitem2.getItemValueString(EntityService.UNIQUEIDREF));

		em.getTransaction().commit();
		logger.info("#---Transaction 3 END ----#");

	}

	/**
	 * This method creats a ItemCollection containing a txtName with the current
	 * method name and stores the name into the itemNameList vector. This vector
	 * is used t remove the entities on @After
	 * 
	 * The method name will be getStackTrace so 1st
	 * 
	 * @return
	 */
	private ItemCollection createTestItemCollection(
			StackTraceElement[] stacktrace) {
		ItemCollection itemcol = new ItemCollection();

		StackTraceElement et = stacktrace[1];
		String sName = "Test-Case: " + et.getMethodName();

		itemcol.replaceItemValue("txtName", sName);

		return itemcol;
	}

	/**
	 * Login with manager access
	 */
	private void loginAsManager() {
		programmaticLogin.logout();
		programmaticLogin.login("Manfred", "manfred".toCharArray());
	}

	/**
	 * Login with author access
	 */
	private void loginAsAuthor() {
		programmaticLogin.logout();
		programmaticLogin.login("Anna", "anna".toCharArray());
	}

}

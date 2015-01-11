package org.imixs.workflow.jee.ejb;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.appserv.security.ProgrammaticLogin;

/**
 * Test class can be used as the basis for all integration service test classes.
 * 
 * See maven doc for information how to setup the environment.
 * 
 * 
 * @author rsoika
 * @version 1.0.0 
 */
public class AbstractTestService {
	 EntityManagerFactory emFactory;
	 EntityManager em;
	 ProgrammaticLogin programmaticLogin = null;
	 EntityServiceRemote entityService = null;
	 ModelServiceRemote modelService = null;
	 WorkflowServiceRemote workflowService=null;

	
	static Logger logger = Logger.getLogger(AbstractTestService.class
			.getName());
	// imixs-jsf-example-3.2.0-SNAPSHOT
	static String ENTITYSERVICE_JNDI_NAME = "java:global/imixs-jsf-example-3.2.0-SNAPSHOT/EntityService!org.imixs.workflow.jee.ejb.EntityServiceRemote";
	static String MODELSERVICE_JNDI_NAME = "java:global/imixs-jsf-example-3.2.0-SNAPSHOT/ModelService!org.imixs.workflow.jee.ejb.ModelServiceRemote";
	static String WORKFLOWSERVICE_JNDI_NAME = "java:global/imixs-jsf-example-3.2.0-SNAPSHOT/WorkflowService!org.imixs.workflow.jee.ejb.WorkflowServiceRemote";

	static String AUTH_CONF_PATH = "~/eclipse_44/imixs-workflow/imixs-workflow-test/src/test/resources/auth.conf";

	List<String> removeList = null; // uniqueids to be removed after a
											// test

	@Before
	public void setup() {

		try {

			// setup programmatic login for GlassFish 3
			System.setProperty("java.security.auth.login.config",
					AUTH_CONF_PATH);
			programmaticLogin = new ProgrammaticLogin();
			loginAsManager();
			InitialContext ic = new InitialContext();

			emFactory = Persistence
					.createEntityManagerFactory("org.imixs.workflow.jee.jpa");
			em = emFactory.createEntityManager();

			entityService = (EntityServiceRemote) ic
					.lookup(ENTITYSERVICE_JNDI_NAME);
			
			modelService = (ModelServiceRemote) ic
					.lookup(MODELSERVICE_JNDI_NAME);
			
			workflowService = (WorkflowServiceRemote) ic
					.lookup(WORKFLOWSERVICE_JNDI_NAME);

			removeList = new Vector<String>();

		} catch (Exception e) {
			e.printStackTrace();
			entityService = null;
		}
	}

	/**
	 * This method removes all created workitems
	 */
	@After
	public void tearDown() {
		if (removeList != null && removeList.size() > 0) {
			String sIDList = "";
			for (String aName : removeList) {
				sIDList = sIDList + "'" + aName + "',";
			}
			sIDList = sIDList.substring(0, sIDList.length() - 1);

			loginAsManager();

			// search workitem
			String sQuery = "SELECT entity FROM Entity entity "
					+ "  WHERE entity.id IN (" + sIDList + ") ";

			List<ItemCollection> result = entityService.findAllEntities(sQuery,
					0, -1);
			em.getTransaction().begin();
			for (ItemCollection entity : result) {

				logger.info("#---Transaction 2 BEGIN ----#");
				try {
					entityService.remove(entity);
				} catch (AccessDeniedException e) {
					logger.severe("Error TearDown - unable to remove entity: "
							+ entity.getItemValueString(EntityService.UNIQUEID));
				}

				logger.info("#---Transaction 2 END ----#");

			}
			em.getTransaction().commit();

		}
		removeList = new Vector<String>();

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

package org.imixs.example;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.WorkflowMockEnvironment;
import org.imixs.workflow.engine.WorkflowService;
import org.imixs.workflow.engine.plugins.OwnerPlugin;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is an example of a jUnit Test using the Imixs WorkflowMockEnvironment
 * 
 * @author rsoika
 */
public class TestBPMN {

	protected ItemCollection workitem;
	protected ItemCollection event;
	protected WorkflowMockEnvironment workflowEnvironment;

	@BeforeEach
	public void setUp() throws PluginException, ModelException {
		Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);
		workflowEnvironment = new WorkflowMockEnvironment();
		workflowEnvironment.setUp();
	}

	/**
	 * Simple Test
	 */
	@Test
	public void testBasic() {
		workflowEnvironment.loadBPMNModel("/bpmn/basic.bpmn");

		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_subject", "Hello World");

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(11001, workitem.getTaskID());

			// test $readaccess
			Assert.assertTrue(workitem.getItemValueString(WorkflowService.READACCESS).isEmpty());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Simple Test OwnerPlugin
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testBasicOwner() {
		workflowEnvironment.loadBPMNModel("/bpmn/basic_owner.bpmn");

		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_subject", "Hello World");

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(1100, workitem.getTaskID());
			Assert.assertEquals("manfred", workitem.getItemValue(OwnerPlugin.OWNER, String.class));

			// test $owner
			List<String> owners = workitem.getItemValue("$owner");
			Assert.assertTrue(owners.contains("manfred"));

			// test $readaccess
			Assert.assertTrue(workitem.getItemValueString(WorkflowService.READACCESS).isEmpty());

			// test $writeaccess
			List<String> writeAccess = workitem.getItemValue(WorkflowService.WRITEACCESS);
			Assert.assertTrue(writeAccess.contains("manfred"));
			Assert.assertTrue(writeAccess.contains("{process:Finance:assist}"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}

package org.imixs.example.modeling;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.WorkflowMockEnvironment;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is an example of a jUnit Test testing a conditional event with the Imixs
 * WorkflowMockEnvironment
 * 
 * @see issue #299
 * @author rsoika
 */
public class TestConditionalEvent1 {

	protected ItemCollection workitem;
	protected ItemCollection event;
	protected WorkflowMockEnvironment workflowEnvironment;

	@BeforeEach
	public void setUp() throws PluginException, ModelException {
		Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);
		workflowEnvironment = new WorkflowMockEnvironment();
		workflowEnvironment.setUp();
	}

	@Test
	public void testSimpleConditionalEvent() {
		workflowEnvironment.loadBPMNModel("/bpmn/conditional_event1.bpmn");

		// test _budget > 100
		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_budget", 200);

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(1100, workitem.getTaskID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// test _budget < 100
		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_budget", 50);

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(1200, workitem.getTaskID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}
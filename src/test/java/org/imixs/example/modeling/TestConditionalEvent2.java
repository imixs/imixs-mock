package org.imixs.example.modeling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.WorkflowMockEnvironment;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is an example of a jUnit Test testing a conditional event with the Imixs
 * WorkflowMockEnvironment
 * 
 * @see issue #299
 * @author rsoika
 */
public class TestConditionalEvent2 {
	final static String MODEL_PATH = "/conditional_event2.bpmn";
	final static String MODEL_VERSION = "1.0.0";

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
		workflowEnvironment.loadBPMNModel("/bpmn/conditional_event2.bpmn");

		// test _budget > 100
		workitem = new ItemCollection();
		workitem.model(MODEL_VERSION).task(1000).event(10);
		workitem.replaceItemValue("_budget", 500);

		try {
			// in this case the budget will be computed by a business rule (RulePlugin)
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			assertNotNull(workitem);
			// test budget
			assertEquals(500, workitem.getItemValueInteger("_budget"));
			// test conditional event
			assertEquals(1100, workitem.getTaskID());

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
package org.imixs.example.modeling;

import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.ModelPluginMock;
import org.imixs.workflow.engine.WorkflowMockEnvironment;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

/**
 * This is an example of a jUnit Test testing a conditional event with the Imixs
 * WorkflowMockEnvironment
 * 
 * @see issue #299
 * @author rsoika
 */
public class TestConditionalEvent2 {
	WorkflowMockEnvironment workflowMockEnvironment;

	final static String MODEL_PATH = "/conditional_event2.bpmn";
	final static String MODEL_VERSION = "1.0.0";

	ItemCollection workitem = null;

	@Before
	public void setup() throws PluginException, ModelException {

		// initialize @Mock annotations....
		MockitoAnnotations.initMocks(this);

		workflowMockEnvironment = new WorkflowMockEnvironment();
		workflowMockEnvironment.setModelPath(MODEL_PATH);
		workflowMockEnvironment.setup();

		Assert.assertNotNull(workflowMockEnvironment.getModel());

		// mock session context of plugin
		Principal principal = Mockito.mock(Principal.class);
		when(principal.getName()).thenReturn("manfred");

		Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);
		// skip mail plugin
		try {
			workflowMockEnvironment.getModelService().addModel(new ModelPluginMock(workflowMockEnvironment.getModel(),
					"org.imixs.workflow.engine.plugins.ApplicationPlugin","org.imixs.workflow.engine.plugins.RulePlugin"));
		} catch (ModelException e) {
			e.printStackTrace();
		}

	} 
	
	@Test
	public void testSimpleConditionalEvent() {
		
		// test _budget > 100
		workitem = new ItemCollection();
		workitem.model(MODEL_VERSION).task(1000).event(10);
		workitem.replaceItemValue("_budget", 0);

		try {
			// in this case the budget will be computed by a business rule (RulePlugin)
			workitem = workflowMockEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			// test budget
			Assert.assertEquals(500, workitem.getItemValueInteger("_budget"));
			// test conditional event
			Assert.assertEquals(1100, workitem.getTaskID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		
	
	}

}
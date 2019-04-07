package org.imixs.example.modeling;

import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.ModelPluginMock;
import org.imixs.workflow.engine.WorkflowMockEnvironment;
import org.imixs.workflow.exceptions.AdapterException;
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
public class TestConditionalEvent1 {
	WorkflowMockEnvironment workflowMockEnvironment;

	final static String MODEL_PATH = "/conditional_event1.bpmn";
	final static String MODEL_VERSION = "1.0.0";

	ItemCollection workitem = null;

	@Before
	public void setup() throws PluginException, ModelException, AdapterException {

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
					"org.imixs.workflow.engine.plugins.ApplicationPlugin"));
		} catch (ModelException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSimpleConditionalEvent() {
		
		// test _budget > 100
		workitem = new ItemCollection();
		workitem.model(MODEL_VERSION).task(1000).event(10);
		workitem.replaceItemValue("_budget", 200);

		try {
			workitem = workflowMockEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(1100, workitem.getTaskID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		
		
		// test _budget < 100
		workitem = new ItemCollection();
		workitem.model(MODEL_VERSION).task(1000).event(10);
		workitem.replaceItemValue("_budget", 50);

		try {
			workitem = workflowMockEnvironment.getWorkflowService().processWorkItem(workitem);

			Assert.assertNotNull(workitem);
			Assert.assertEquals(1200, workitem.getTaskID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}
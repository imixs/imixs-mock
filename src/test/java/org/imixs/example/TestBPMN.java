package org.imixs.example;

// Import the JUnit 5 Assertions class
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.MockWorkflowEnvironment;
import org.imixs.workflow.engine.WorkflowService;
import org.imixs.workflow.engine.adapters.AccessAdapter;
import org.imixs.workflow.engine.plugins.OwnerPlugin;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * This is an example of a jUnit Test using the Imixs WorkflowMockEnvironment
 * 
 * @author rsoika
 */
// @ExtendWith(MockitoExtension.class)
// @MockitoSettings(strictness = Strictness.WARN)
public class TestBPMN {

	protected ItemCollection workitem;
	protected ItemCollection event;
	protected MockWorkflowEnvironment workflowEnvironment;

	@InjectMocks
	protected AccessAdapter accessAdapter;

	@BeforeEach
	public void setUp() throws PluginException, ModelException {
		// Ensures that @Mock and @InjectMocks annotations are processed
		MockitoAnnotations.openMocks(this);
		Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);
		workflowEnvironment = new MockWorkflowEnvironment();

		// Setup Environment
		workflowEnvironment.setUp();

		// register AccessAdapter Mock
		workflowEnvironment.registerAdapter(accessAdapter);
		accessAdapter.setWorkflowService(workflowEnvironment.getWorkflowService());

	}

	/**
	 * Simple Test
	 */
	@Test
	public void testBasic() {
		workflowEnvironment.loadBPMNModelFromFile("/bpmn/basic.bpmn");

		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_subject", "Hello World");

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);
			assertNotNull(workitem);
			assertEquals(1100, workitem.getTaskID());

			// test $readaccess
			assertTrue(workitem.getItemValueString(WorkflowService.READACCESS).isEmpty());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	/**
	 * Simple Test OwnerPlugin
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testBasicOwner() {
		workflowEnvironment.loadBPMNModelFromFile("/bpmn/basic_owner.bpmn");

		workitem = new ItemCollection();
		workitem.model("1.0.0").task(1000).event(10);
		workitem.replaceItemValue("_subject", "Hello World");

		try {
			workitem = workflowEnvironment.getWorkflowService().processWorkItem(workitem);

			assertNotNull(workitem);
			assertEquals(1100, workitem.getTaskID());
			assertEquals("manfred", workitem.getItemValue(OwnerPlugin.OWNER, String.class));

			// test $owner
			List<String> owners = workitem.getItemValue("$owner");
			assertTrue(owners.contains("manfred"));

			// test $readaccess
			assertTrue(workitem.getItemValueString(WorkflowService.READACCESS).isEmpty());

			// test $writeaccess
			List<String> writeAccess = workitem.getItemValue(WorkflowService.WRITEACCESS);
			assertTrue(writeAccess.contains("manfred"));
			assertTrue(writeAccess.contains("{process:Finance:assist}"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}

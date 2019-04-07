# Imixs-Mock

Imixs-Mock is a testing library for mocking the Imixs Workflow engine.

Mocking is primarily used in unit testing. An object under test may have dependencies on other (complex) objects. To isolate the behavior of the object you want to replace the other objects by mocks that simulate the behavior of the real objects. This is useful if the real objects are impractical to incorporate into the unit test.

In short, mocking is creating objects that simulate the behavior of real objects.

## How to Use

To use the library in your own project just add the following maven dependencies:


	<dependency>
		<groupId>org.imixs.workflow</groupId>
		<artifactId>imixs-mock</artifactId>
		<version>4.4.1</version>
		<scope>test</scope>
	</dependency>
	<!-- JUnit Tests -->
	<dependency>
		<groupId>junit</groupId>
		<artifactId>junit</artifactId>
		<version>4.8.1</version>
		<scope>test</scope>
	</dependency>
	<dependency>
		<groupId>org.mockito</groupId>
		<artifactId>mockito-all</artifactId>
		<version>1.9.5</version>
		<scope>test</scope>
	</dependency>

**Note:** Take a look into the [Release Notes](RELEASENOTES) to check the compatibility list for your Imixs-Workflow versiion.		
	
## Junit Tests

In a JUnit Test class you can initialize the Imixs-Workflow Mock environment:


	public class TestMyWorkflow {
		WorkflowMockEnvironment workflowMockEnvironment;
	
		final static String MODEL_PATH = "/mymodel.bpmn";
		final static String MODEL_VERSION = "1.0.0";
	
		ItemCollection workitem = null;
	
		// setup the workflow envirnment
		@Before
		public void setup() throws PluginException, ModelException {
	
			// initialize @Mock annotations....
			MockitoAnnotations.initMocks(this);
	
			workflowMockEnvironment = new WorkflowMockEnvironment();
			workflowMockEnvironment.setModelPath(MODEL_PATH);
			workflowMockEnvironment.setup();
	
			// test model...
			Assert.assertNotNull(workflowMockEnvironment.getModel());
		}
		.....
	}

You can find a full example of a jUnit Test [here](https://github.com/imixs/imixs-mock/blob/master/src/test/java/org/imixs/example/TestBPMN.java)

### Test Your Model

After you have setup the workflow Mock you can write test methods to test specific model situations.
In your test method you simulate a Task and an Event in the following way:

	@Test
	public void testSimple() {

		workitem = new ItemCollection();
		workitem.model(MODEL_VERSION).task(2100).event(10);
		workitem.replaceItemValue("_subject", "Hello World");

		try {
			workitem = workflowMockEnvironment.getWorkflowService().processWorkItem(workitem);
		} catch (AccessDeniedException | ProcessingErrorException | PluginException | ModelException e) {
			Assert.fail();
		}

	}



### Testing Specific Plugins Only

You can overwrite the plug-in list defined by your model to test only specific plug-ins. This can be useful to reduce the complexity in a mock environment

	// test only specific Plugins.....
	try {
		workflowMockEnvironment.getModelService()
				.addModel(new ModelPluginMock(workflowMockEnvironment.getModel(),
						"org.imixs.marty.plugins.ApproverPlugin",
						"org.imixs.workflow.engine.plugins.RulePlugin"));
	} catch (ModelException e) {
		e.printStackTrace();
	}


### Different Model Locations in Maven

In a Maven multi-module project your models are typical placed outside your java module (e.g. in the parent project). In this case you can define the location of test resources with the following additional configuraition in your pom.xml: 


	...
	<build>
	  ....
	  <testResources>
			<testResource>
				<directory>${project.basedir}/../src/workflow</directory>
			</testResource>
	  </testResources>
	</build>

With this configuration your bpmn models are loaded from the parent module. 


### Simulate User Login

You can easily simulate a specific user in you test class by 



	.....
	Principal principal = Mockito.mock(Principal.class);
	when(workflowMockEnvironment.getWorkflowService().getUserName()).thenReturn("my.userid");
	....
	
	
### Set Log Level

To set the log level in a unit test you can simply change the standard logger:

	
	Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);
	  
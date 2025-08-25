# Imixs-Mock

[![Build Status](https://travis-ci.org/imixs/imixs-mock.svg?branch=master)](https://travis-ci.org/imixs/imixs-mock)
[![Join the chat at https://gitter.im/imixs/imixs-workflow](https://badges.gitter.im/imixs/imixs-workflow.svg)](https://gitter.im/imixs/imixs-workflow?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License](https://img.shields.io/badge/license-GPL-blue.svg)](https://github.com/imixs/imixs-mock/blob/master/LICENSE)

Imixs-Mock is a testing library for mocking the Imixs Workflow engine.

Mocking is primarily used in unit testing. An object under test may have dependencies on other (complex) objects. To isolate the behavior of the object you want to replace the other objects by mocks that simulate the behavior of the real objects. This is useful if the real objects are impractical to incorporate into the unit test.

In short, mocking is creating objects that simulate the behavior of real objects.

## How to Use

To use the library in your own project just add the following maven dependencies:

```xml
....
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <jakarta.version>10.0.0</jakarta.version>
    <org.imixs.workflow.version>6.1.0</org.imixs.workflow.version>
    <microprofile.version>6.0</microprofile.version>
    <microprofile-metrics.version>4.0</microprofile-metrics.version>
    <!-- test dependencies -->
    <junit.jupiter.version>5.9.2</junit.jupiter.version>
    <mockito.version>5.8.0</mockito.version>
    <org.imixs.mock.version>6.1.0</org.imixs.mock.version>
  </properties>
  ....
  <build>
    <plugins>
            ..........
      <!-- use JDK settings for compiling -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>11</source>
          <target>11</target>
        </configuration>
      </plugin>
      <!-- Testing JUnit 5 -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
      </plugin>
    </plugins>
    ...........
    <testResources>
      <testResource>
        <directory>${basedir}/src/test/resources</directory>
      </testResource>
    </testResources>
    <dependencies>
    <!-- Imixs Workflow -->
      <dependency>
          <groupId>org.imixs.workflow</groupId>
          <artifactId>imixs-workflow-core</artifactId>
          <version>${org.imixs.workflow.version}</version>
      </dependency>
      <!-- Jakarta EE -->
      <dependency>
		<groupId>jakarta.platform</groupId>
		<artifactId>jakarta.jakartaee-api</artifactId>
		<version>${jakarta.version}</version>
		<scope>provided</scope>
      </dependency>
      <!-- JUnit 5 Dependencies -->
      <dependency>
		<groupId>org.junit.jupiter</groupId>
		<artifactId>junit-jupiter-api</artifactId>
		<version>${junit.jupiter.version}</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.junit.jupiter</groupId>
		<artifactId>junit-jupiter-engine</artifactId>
		<version>${junit.jupiter.version}</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.mockito</groupId>
		<artifactId>mockito-core</artifactId>
		<version>${mockito.version}</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.mockito</groupId>
		<artifactId>mockito-junit-jupiter</artifactId>
		<version>${mockito.version}</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.eclipse.parsson</groupId>
		<artifactId>jakarta.json</artifactId>
		<version>1.1.1</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.glassfish.jaxb</groupId>
		<artifactId>jaxb-runtime</artifactId>
		<version>3.0.0</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>jakarta.xml.bind</groupId>
		<artifactId>jakarta.xml.bind-api</artifactId>
		<version>3.0.0</version>
		<scope>test</scope>
      </dependency>
      <dependency>
		<groupId>org.imixs.workflow</groupId>
		<artifactId>imixs-mock</artifactId>
		<version>${org.imixs.mock.version}</version>
		<scope>test</scope>
      </dependency>
  </dependencies>
  ......
```

**Note:** Take a look into the [Release Notes](RELEASENOTES.md) to check the compatibility list for your Imixs-Workflow versiion.

## Junit Tests

In a JUnit Test class you can initialize the Imixs-Workflow Mock environment:

```java
    public class TestMyWorkflow {
    	WorkflowMockEnvironment workflowMockEnvironment;

    	final static String MODEL_PATH = "/mymodel.bpmn";
    	final static String MODEL_VERSION = "1.0.0";
    	ItemCollection workitem = null;

		@InjectMocks
		protected RulePlugin rulePlugin;

    	// setup the workflow envirnment
    	@BeforeEach
    	public void setup() throws PluginException, ModelException {

    		// initialize @Mock annotations....
    		MockitoAnnotations.openMocks(this);

    		workflowMockEnvironment = new WorkflowMockEnvironment();
    		workflowMockEnvironment.setModelPath(MODEL_PATH);
    		workflowMockEnvironment.setup();

			workflowMockEnvironment.registerPlugin(rulePlugin);

    		// load a model...
			workflowMockEnvironment.loadBPMNModelFromFile(MODEL_PATH);
			BPMNModel model = workflowMockEnvironment.getModelManager().getModel(MODEL_VERSION);
    		Assert.assertNotNull(model);
    	}
    	.....
    }
```

You can find a full example of a jUnit Test [here](https://github.com/imixs/imixs-mock/blob/master/src/test/java/org/imixs/example/TestBPMN.java)

### Access a BPMNModel Instance

The workflow mock provides you with methods to access a BPMNModel instance through the ModelManager. You can use this model instance to load or explore model elements:

```java
	// load a event
	BPMNModel model = workflowMockEnvironment.getModelManager().getModel(MODEL_VERSION);
	event = workflowMockEnvironment.getModelManager().findEventByID(model, TASK_ID, EVENT_ID);
```

### Test Your Model

After you have setup the workflow Mock you can write test methods to test specific model situations.
In your test method you simulate a Task and an Event in the following way:

```java
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
```

### Testing Specific Plugins Only

You can overwrite the plug-in list defined by your model to test only specific plug-ins. This can be useful to reduce the complexity in a mock environment

```java
    // test only specific Plugins.....
	List<String> newPlugins = Arrays.asList(
		"org.imixs.workflow.engine.plugins.RulePlugin",
		"org.imixs.workflow.engine.plugins.ResultPlugin",
		"com.MyPlugin");
	workflowMockEnvironment.updatePluginDefinition(model, newPlugins);
```

### Different Model Locations in Maven

In a Maven multi-module project your models are typical placed outside your java module (e.g. in the parent project). In this case you can define the location of test resources with the following additional configuration in your `pom.xml`:

```xml
    ...
    <build>
      ....
      <testResources>
    		<testResource>
    			<directory>${basedir}/workflow</directory>
    		</testResource>
      </testResources>
    </build>
```

With this configuration your bpmn models are loaded from the parent module.

### Simulate User Login

You can easily simulate a specific user in you test class by

```java
    .....
    Principal principal = Mockito.mock(Principal.class);
    when(workflowMockEnvironment.getWorkflowService().getUserName()).thenReturn("my.userid");
    ....
```

### Set Log Level

To set the log level in a unit test you can simply change the standard logger:

    Logger.getLogger("org.imixs.workflow.*").setLevel(Level.FINEST);

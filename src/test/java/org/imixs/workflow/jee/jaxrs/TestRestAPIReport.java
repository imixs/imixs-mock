package org.imixs.workflow.jee.jaxrs;

import javax.ws.rs.core.Response;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.services.rest.RestClient;
import org.imixs.workflow.xml.XMLItemCollection;
import org.imixs.workflow.xml.XMLItemCollectionAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for imixs-jax-rs
 * 
 * @author rsoika
 * 
 */
public class TestRestAPIReport {

	static String USERID = "Manfred";
	static String PASSWORD = "manfred";

	@Test
	public void testPostReportData() {

		RestClient restClient = new RestClient();

		restClient.setCredentials(USERID, PASSWORD);

		String uri = "http://localhost:8080/workflow/rest/report";

		ItemCollection report = new ItemCollection();
		report.replaceItemValue("txtName", "test");

		XMLItemCollection xmlReport;
		try {
			xmlReport = XMLItemCollectionAdapter.putItemCollection(report);
			int httpResult=restClient.postEntity(uri, xmlReport);
			
			// expected result 204
			Assert.assertEquals(204, httpResult);
			
		} catch (Exception e) {

			e.printStackTrace();
			Assert.fail();
		}

		
	}
}

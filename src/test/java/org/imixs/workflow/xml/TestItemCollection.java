package org.imixs.workflow.xml;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.imixs.workflow.ItemCollection;

/**
 * Test class for item object
 * 
 * @author rsoika
 * 
 */
public class TestItemCollection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			// create an simple ItemCollection with some data....
			ItemCollection itemCol=new ItemCollection();
			itemCol.replaceItemValue("txtTitel", "Hello world");
			itemCol.replaceItemValue("numAge", 40);
			itemCol.replaceItemValue("keyVisible", true);
			
			// convert the ItemCollection into a XMLItemcollection...
			XMLItemCollection xmlItemCollection= XMLItemCollectionAdapter.putItemCollection(itemCol);
		
			// marshal the Object into an XML Stream....
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(XMLItemCollection.class);
			Marshaller m=context.createMarshaller();
			m.marshal(xmlItemCollection,writer);
			System.out.println(writer.toString());
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}

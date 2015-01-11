package org.imixs.workflow.xml;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Test class for item object
 * 
 * @author rsoika
 * 
 */
public class TestItem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			XMLItem item = new XMLItem();
			item.setName("txtTitle");
			item.setValue(new Object[] { "Hello world","Hugo",true });

			StringWriter writer = new StringWriter();

			JAXBContext context = JAXBContext.newInstance(XMLItem.class);
			Marshaller m=context.createMarshaller();
			m.marshal(item,writer);
			
			System.out.println(writer.toString());
			
			
		} catch (JAXBException e) {

			e.printStackTrace();
		}

	}

}

package org.imixs.workflow.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * prints the xml schema file (.xsd)
 * 
 * @author rsoika
 * 
 */
public class SchemaWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			File baseDir = new File(".");

			JAXBContext context = JAXBContext.newInstance(XMLItem.class,
					XMLItemCollection.class, EntityCollection.class);
			context.generateSchema(new SchemaResolver());

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}

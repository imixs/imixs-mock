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
 * prints the xml schema
 * 
 * @author rsoika
 * 
 */
public class SchemaResolver  extends SchemaOutputResolver {
	    public Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException {
	        return new StreamResult(new File("~/","schema.xsd"));
	    }
	

}


package org.cf.restgateway.edms;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.restgateway.ServiceEndpointDefn;
import org.cf.restgateway.ServiceEndpointLocator;
import org.cf.restgateway.edms.controller.EDMSRestGatewayController;

@Configuration
public class EDMSJaxb2Marshaller {
	
	private static final String CONTEXT_PATH = "org.cf.restgateway.edms.wsdl";

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath(CONTEXT_PATH);
		return marshaller;
	}	
	
	public void setMarshallers(WebServiceGatewaySupport webserviceClient) {
		webserviceClient.setMarshaller(marshaller());
		webserviceClient.setUnmarshaller(marshaller());
	}
}
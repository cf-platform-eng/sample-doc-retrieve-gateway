
package org.cf.restgateway.edms;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import org.cf.restgateway.ServiceEndpointDefn;
import org.cf.restgateway.ServiceEndpointLocator;

@Configuration
public class EDMSRetrieveContentConfiguration {

	private static final String SERVICE_NAME = "EDMSRetreiveInterface";
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.cf.restgateway.edms.wsdl");
		return marshaller;
	}

	@Bean
	public EDMSRetrieveContentClient eDMSRetrieveContentClient(Jaxb2Marshaller marshaller) {
		
		// Local testing
		String uri = "http://document-service.10.244.0.34.xip.io/soap/RetrieveService";
		
		String username = "";
		String password = "";
		String certName = "";
		String certFormat = "";
		String certLocation = "";
		
				
				
		EDMSRetrieveContentClient client = new EDMSRetrieveContentClient();

		ServiceEndpointLocator serviceEndpointLocator = new ServiceEndpointLocator();
		ServiceEndpointDefn serviceEndpointDefn = serviceEndpointLocator.lookupServiceEndpointDefnByName(SERVICE_NAME);
		
		
		if (serviceEndpointDefn != null) {
			System.out.println("Defn Found!!:" + serviceEndpointDefn);

			// Override the uri based on the service uri endpoint defined in VCAP_SERVICES
			uri = serviceEndpointDefn.getUri();
			username = serviceEndpointDefn.getUsername();
			//... get password, cert, ... and any other info required to invoke the remote service...
			
		}

		client.setDefaultUri(uri);

		

		/*
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

		MessageFactory msgFactory;
		try {
			msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

		SaajSoapMessageFactory newSoapMessageFactory = new SaajSoapMessageFactory(msgFactory);
		webServiceTemplate.setMessageFactory(newSoapMessageFactory);
		webServiceTemplate.setDefaultUri(uri);

		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.setWebServiceTemplate(webServiceTemplate);
		*/

		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

}

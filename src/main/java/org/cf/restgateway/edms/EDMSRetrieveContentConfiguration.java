
package org.cf.restgateway.edms;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class EDMSRetrieveContentConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.cf.restgateway.edms.wsdl");
		return marshaller;
	}

	@Bean
	public EDMSRetrieveContentClient eDMSRetrieveContentClient(Jaxb2Marshaller marshaller) {
		EDMSRetrieveContentClient client = new EDMSRetrieveContentClient();

		// Local testing
		String uri = "http://document-service.10.244.0.34.xip.io/soap/RetrieveService";
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

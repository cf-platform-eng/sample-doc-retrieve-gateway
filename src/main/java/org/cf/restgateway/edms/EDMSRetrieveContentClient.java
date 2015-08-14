
package org.cf.restgateway.edms;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.restgateway.ServiceEndpointDefn;
import org.cf.restgateway.ServiceEndpointLocator;
import org.cf.restgateway.edms.controller.EDMSRestGatewayController;
import org.cf.restgateway.edms.controller.EDMSRetrieverRequest;
import org.cf.restgateway.edms.wsdl.EDMSRequestHeader;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveRequestType;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveResponseType;
import org.cf.restgateway.edms.wsdl.ObjectFactory;
import org.cf.restgateway.edms.wsdl.EDMSRequestHeader.UserInformation;


public class EDMSRetrieveContentClient extends WebServiceGatewaySupport {

	Log log = LogFactory.getLog(EDMSRetrieveContentClient.class);

	// Edit the service name as necessary.
	// The service was created using service + plan name: hence 'EDMSRetreiveInterface-basic'
	private static final String SERVICE_NAME = "EDMSRetreiveInterface-basic";
	
	// Local testing
	private static final String DEFAULT_URI = "http://document-service.10.244.0.34.xip.io/soap/RetrieveService";	


	public String getCsnId() {
		return csnId;
	}

	public void setCsnId(String csnId) {
		this.csnId = csnId;
	}


	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getCorrId() {
		return corrId;
	}

	public void setCorrId(String corrId) {
		this.corrId = corrId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Autowired
	private ApplicationContext context;
	
	private String csnId; 
	private String contentId;
	private String corrId;
	private String userId;


	public XMLGregorianCalendar convertStringToXmlGregorian(String dateString)
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("CMT"));

			String date = sdf.format(new Date());
			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);

			return xmlCal;
		} catch (Exception e) {
			// Optimize exception handling
			System.out.print(e.getMessage());
			return null;
		}
	}

	public EDMSRetrieveContentClient() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EDMSRetrieveContentClient(EDMSRetrieverRequest requestPayload) {
		this( 	
				requestPayload.getCsnId(),
				requestPayload.getContentId(), 
				requestPayload.getCorrId(), 
				requestPayload.getUserId()
			);
	}
	
	
	public EDMSRetrieveContentClient(String csnId, String contentId, String corrId, String userId) {
		super();
		this.csnId = csnId;
		this.contentId = contentId;
		this.corrId = corrId;
		this.userId = userId;
		
		// Reset the service endpoint before the call... based on service binding
		resetServiceEndpointUsingServiceBindings();
	}
	
	public void resetServiceEndpointUsingServiceBindings() {

		String uri = DEFAULT_URI;
		String username = "";
		String password = "";
		String certName = "";
		String certFormat = "";
		String certLocation = "";
		
		log.info("Trying to override service endpoint using service binding, looking for service: " + SERVICE_NAME);
		ServiceEndpointLocator serviceEndpointLocator = new ServiceEndpointLocator();
		ServiceEndpointDefn serviceEndpointDefn = serviceEndpointLocator.lookupServiceEndpointDefnByName(SERVICE_NAME);
		
		
		if (serviceEndpointDefn != null) {
			System.out.println("Defn Found!!:" + serviceEndpointDefn);
	
			// Override the uri based on the service uri endpoint defined in VCAP_SERVICES
			uri = serviceEndpointDefn.getUri();
			username = serviceEndpointDefn.getUsername();
			//... get password, cert, ... and any other info required to invoke the remote service...
			
		}
		
		log.info("Setting service endpoint for service: " + SERVICE_NAME + " to: " + uri);
		this.setDefaultUri(uri);
	}
	

	public void setMarshallers(EDMSJaxb2Marshaller configurator) {
				
		this.setMarshaller(configurator.marshaller());
		this.setUnmarshaller(configurator.marshaller());
	}

	public EDMSRetrieveResponseType retrieveContent() {

		ObjectFactory of = new ObjectFactory();

		EDMSRetrieveRequestType request = of.createEDMSRetrieveRequestType();

		EDMSRequestHeader header = of.createEDMSRequestHeader();
		request.setEDMSRequestHeader(header);
		header.setConsumerSourceName(csnId);
		header.setCorrelationId(corrId);
		header.setVerboseCode(true);

		UserInformation userInfo = of.createEDMSRequestHeaderUserInformation();
		userInfo.setUserId(userId);
		header.setUserInformation(userInfo);


		header.setRequestTimestamp(convertStringToXmlGregorian(new Date().toString()));
		request.setContentId(contentId);

		JAXBElement<EDMSRetrieveRequestType> requestPayload = of.createEDMSRetrieveRequest(request);

		System.out.println();
		System.out.println("Requesting Content with Id " + contentId + " using request payload:" + requestPayload);


		try {
			JAXBContext ctx = JAXBContext.newInstance(EDMSRetrieveRequestType.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(requestPayload, System.out);
		}
		catch (Exception
				e) {

			e.printStackTrace();
		}

		//EDMSRetrieveResponseType response = (EDMSRetrieveResponseType) getWebServiceTemplate().marshalSendAndReceive(
		JAXBElement jaxbResponse = (JAXBElement)getWebServiceTemplate().marshalSendAndReceive(

				requestPayload,
				new SoapActionCallback(
						"EDMS/Retrieve"
						));


		EDMSRetrieveResponseType edmsResponse = (EDMSRetrieveResponseType)jaxbResponse.getValue();
		System.out.println("Got response : " + edmsResponse);

		try {
			JAXBContext ctx = JAXBContext.newInstance(EDMSRetrieveResponseType.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			System.out.println("Raw response payload:");
			marshaller.marshal(jaxbResponse, System.out);
		}
		catch (Exception
				e) {
			e.printStackTrace();
			
			//catch exception
		}

		return edmsResponse;
	}

}

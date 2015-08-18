
package org.cf.restgateway.edms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.restgateway.ServiceEndpointDefn;
import org.cf.restgateway.ServiceEndpointLocator;
import org.cf.restgateway.edms.controller.EDMSRetrieverRequest;
import org.cf.restgateway.edms.wsdl.EDMSRequestHeader;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveRequestType;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveResponseType;
import org.cf.restgateway.edms.wsdl.ObjectFactory;
import org.cf.restgateway.edms.wsdl.EDMSRequestHeader.UserInformation;

@Configuration
@Scope("prototype")
public class EDMSRetrieveContentClient extends WebServiceGatewaySupport {

	Log log = LogFactory.getLog(EDMSRetrieveContentClient.class);

	// Edit the service name as necessary.
	private static final String SERVICE_NAME = "EDMSRetreiveInterface";
	
	// Local testing
	private static final String DEFAULT_URI = "http://document-service.10.244.0.34.xip.io/soap/RetrieveService";	

	@Autowired
	Cloud cloud;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	ServiceEndpointLocator serviceEndpointLocator;

	private String csnId; 
	private String contentId;
	private String corrId;
	private String userId;
	
	private EDMSRetrieverRequest retrieverRequest;

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

	public void setRequestPayload(EDMSRetrieverRequest requestPayload) {
		retrieverRequest = requestPayload;
	}
	
	
	public void setRequestPayload(String csnId, String contentId, String corrId, String userId) {
		
		retrieverRequest = new EDMSRetrieverRequest(csnId,
													contentId,
													corrId,
													userId);
	}
	
	public void resetServiceEndpointUsingServiceBindings() {

		String uri = DEFAULT_URI;
		String username = "";
		String password = "";
		String certName = "";
		String certFormat = "";
		String certLocation = "";
		
		log.debug("Trying to override service endpoint using service binding,"
					+ "looking for service: " + SERVICE_NAME);
		
		Map<String, ServiceEndpointDefn> boundServiceEndpointDefsMap = serviceEndpointLocator.lookupServiceDefinitionsByCategory(SERVICE_NAME);

		// If there are multiple services bound all in the same service category/bucket, return the first one.
		// This can occur in cases like we have a service broker that returns SSL Certs for various services but puts them all under the same service category
		// But for the case of service registry, there would be only one service bound under a category,
		// like EDMS category would have only one instance of a service (based on plan or space).
		
		ServiceEndpointDefn serviceEndpointDefn = null;
		if (boundServiceEndpointDefsMap.size() > 0 ) {
			serviceEndpointDefn = boundServiceEndpointDefsMap.entrySet().iterator().next().getValue();
		}
		
		if (serviceEndpointDefn != null) {
			log.debug("Defn Found!!:" + serviceEndpointDefn);
	
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

	public EDMSRetrieveResponseType retrieveContent()  throws Exception {

		// Reset the service endpoint before the call... based on service binding
		resetServiceEndpointUsingServiceBindings();
		
		ObjectFactory of = new ObjectFactory();

		EDMSRetrieveRequestType request = of.createEDMSRetrieveRequestType();

		EDMSRequestHeader header = of.createEDMSRequestHeader();
		request.setEDMSRequestHeader(header);
		header.setConsumerSourceName(retrieverRequest.getCsnId());
		header.setCorrelationId(retrieverRequest.getCorrId());
		header.setVerboseCode(true);

		UserInformation userInfo = of.createEDMSRequestHeaderUserInformation();
		userInfo.setUserId(retrieverRequest.getUserId());
		header.setUserInformation(userInfo);

		header.setRequestTimestamp(convertStringToXmlGregorian(new Date().toString()));
		request.setContentId(retrieverRequest.getContentId());

		JAXBElement<EDMSRetrieveRequestType> requestPayload = of.createEDMSRetrieveRequest(request);
		log.debug("Requesting Content with Id " + contentId + " using request payload");

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

		JAXBElement jaxbResponse = (JAXBElement)getWebServiceTemplate().marshalSendAndReceive(

				requestPayload,
				new SoapActionCallback(
						"EDMS/Retrieve"
						));

		EDMSRetrieveResponseType edmsResponse = (EDMSRetrieveResponseType)jaxbResponse.getValue();
		
		try {
			JAXBContext ctx = JAXBContext.newInstance(EDMSRetrieveResponseType.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			System.out.println("Response payload:");
			marshaller.marshal(jaxbResponse, System.out);
		} catch (Exception e) {
			//catch exception			
			log.error("Problem with reading EDMSRetrieveContent SOAP Response: "							
							+ edmsResponse
							+ ", error: " + e.getMessage() );
			
			e.printStackTrace();
			throw e;
		}

		return edmsResponse;
	}

}

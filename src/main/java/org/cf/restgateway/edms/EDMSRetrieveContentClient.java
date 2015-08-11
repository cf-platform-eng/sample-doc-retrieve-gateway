
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
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import org.cf.restgateway.edms.wsdl.EDMSRequestHeader;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveRequestType;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveResponseType;
import org.cf.restgateway.edms.wsdl.ObjectFactory;
import org.cf.restgateway.edms.wsdl.EDMSRequestHeader.UserInformation;


public class EDMSRetrieveContentClient extends WebServiceGatewaySupport {

	@Autowired
	private ApplicationContext context;

	public void disableCertificateValidation() {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				} };


		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) { return true; }

		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {}

	}

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



	public EDMSRetrieveResponseType getEDMSContent(String csn, String contentId, String corrId, String userId) {

		ObjectFactory of = new ObjectFactory();


		disableCertificateValidation();

		EDMSRetrieveRequestType request = of.createEDMSRetrieveRequestType();

		EDMSRequestHeader header = of.createEDMSRequestHeader();
		request.setEDMSRequestHeader(header);
		header.setConsumerSourceName(csn);
		header.setCorrelationId(corrId);
		header.setVerboseCode(true);

		UserInformation userInfo = of.createEDMSRequestHeaderUserInformation();
		userInfo.setUserId(userId);
		header.setUserInformation(userInfo);


		header.setRequestTimestamp(convertStringToXmlGregorian(new Date().toString()));
		request.setContentId(contentId);

		Object o = of.createEDMSRetrieveRequest(request);

		System.out.println();
		System.out.println("Requesting Content with Id " + contentId + " using request payload:" + o);


		try {
			JAXBContext ctx = JAXBContext.newInstance(EDMSRetrieveRequestType.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(o, System.out);
		}
		catch (Exception
				e) {

			e.printStackTrace();
		}

		//EDMSRetrieveResponseType response = (EDMSRetrieveResponseType) getWebServiceTemplate().marshalSendAndReceive(
		Object response = getWebServiceTemplate().marshalSendAndReceive(

				o,
				new SoapActionCallback(
						"EDMS/Retrieve"
						));


		JAXBElement jaxbResponse = (JAXBElement)response;

		EDMSRetrieveResponseType edmsResponse = (EDMSRetrieveResponseType)jaxbResponse.getValue();
		System.out.println("Got response : " + edmsResponse);

		try {
			JAXBContext ctx = JAXBContext.newInstance(EDMSRetrieveResponseType.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			System.out.println("Raw response payload:");
			marshaller.marshal(response, System.out);
		}
		catch (Exception
				e) {

			//catch exception
		}

		return edmsResponse;
	}

}

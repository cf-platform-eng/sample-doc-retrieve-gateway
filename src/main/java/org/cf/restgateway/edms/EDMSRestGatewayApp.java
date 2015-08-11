
package org.cf.restgateway.edms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class EDMSRestGatewayApp {



	/*
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(EDMSRetrieveContentConfiguration.class, args);

		EDMSRetrieveContentClient eDMSRetrieveContentClient = ctx.getBean(EDMSRetrieveContentClient.class);

		String csn = "AP_AMS";
		String contentId = "57ff0c86-f947-4331-8aef-f63f03fbc3ea";
		String corrId =  "Pivotal5";
		String userId = "drayb";

		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

		Object response = eDMSRetrieveContentClient.getEDMSContent(csn, contentId, corrId, userId);
		//eDMSRetrieveContentClient.(response);
	}
	*/


	@Bean
	public Cloud cloud() {
		//Cloud cloud = new CloudFactory().getCloud();
		//return cloud;

		return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(EDMSRestGatewayApp.class, args);
	}
}


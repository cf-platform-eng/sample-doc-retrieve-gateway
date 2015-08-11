package org.cf.restgateway.edms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.cf.restgateway.edms.EDMSRetrieveContentClient;
import org.cf.restgateway.edms.wsdl.EDMSRetrieveResponseType;

import java.util.*;


@RestController
public class EDMSRestGatewayController {

	Log log = LogFactory.getLog(EDMSRestGatewayController.class);

	@Autowired
	Cloud cloud;

	@Autowired
	private ApplicationContext context;

	@RequestMapping(value = "/content/{csnId}/{corrId}/{userId}/{contentId}", method=RequestMethod.GET)
	public ResponseEntity<Object> getContent(
										@PathVariable("csnId")
										String csnId,
										@PathVariable("corrId")
										String corrId,
										@PathVariable("userId")
										String userId,
										@PathVariable("contentId")
										String contentId) {


		/*
		 *
		 *
		ApplicationContext ctx = SpringApplication.run(EDMSRetrieveContentConfiguration.class, args);

		EDMSRetrieveContentClient eDMSRetrieveContentClient = ctx.getBean(EDMSRetrieveContentClient.class);

		String csn = "asd";
		String contentId = "asdads-f947-4331-8aef-f63f03fbc3ea";
		String corrId =  "adsfa";
		String userId = "sdfasfas";

		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

		Object response = eDMSRetrieveContentClient.getEDMSContent(csn, contentId, corrId, userId);
		//eDMSRetrieveContentClient.(response);


		 */

		log.info("Got retrieve content request from CSN: "+ csnId
				+ ",\n from user: " + userId
				+ ",\n with correlation id: " + corrId
				+ ",\n for content: " + contentId);

		EDMSRetrieveContentClient eDMSRetrieveContentClient = context.getBean(EDMSRetrieveContentClient.class);


		EDMSRetrieveResponseType response = eDMSRetrieveContentClient.getEDMSContent(csnId, contentId, corrId, userId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}


}

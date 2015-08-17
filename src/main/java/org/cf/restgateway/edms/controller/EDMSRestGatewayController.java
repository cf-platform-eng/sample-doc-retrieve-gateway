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
import org.cf.restgateway.edms.EDMSJaxb2Marshaller;
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

	@RequestMapping(value = "/EDMSContent/{csnId}/{corrId}/{userId}/{contentId}", method=RequestMethod.GET)
	public ResponseEntity<Object> getContent(
										@PathVariable("csnId")
										String csnId,
										@PathVariable("corrId")
										String corrId,
										@PathVariable("userId")
										String userId,
										@PathVariable("contentId")
										String contentId) {
		log.info("Got retrieve content request from CSN: "+ csnId
				+ ",\n from user: " + userId
				+ ",\n with correlation id: " + corrId
				+ ",\n for content: " + contentId);

		
		
		
		EDMSRetrieveContentClient edmsRetrieveContentClient = new EDMSRetrieveContentClient(csnId, 
																	contentId, corrId, userId);
	
		try {
			return invokeEDMSRetrieveContent(edmsRetrieveContentClient);
		} catch(Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	
	@RequestMapping(method = RequestMethod.POST, value = "/EDMSContent")
	public ResponseEntity<Object> getContent(@RequestBody EDMSRetrieverRequest retrieveRequest) {

		/*
		 *
		 *
		ApplicationContext ctx = SpringApplication.run(EDMSRetrieveContentConfiguration.class, args);

		EDMSRetrieveContentClient eDMSRetrieveContentClient = ctx.getBean(EDMSRetrieveContentClient.class);

		String csn = "asd";
		String contentId = "asdads-f947-4331-8aef-f63f03fbc3ea";
		String corrId =  "adsfa";
		String userId = "sdfasfas";

		Object response = eDMSRetrieveContentClient.getEDMSContent(csn, contentId, corrId, userId);
		//eDMSRetrieveContentClient.(response);
		 */

		
		log.info("Got retrieve content request from CSN: "+ retrieveRequest.getCsnId()
				+ ",\n from user: " + retrieveRequest.getUserId()
				+ ",\n with correlation id: " + retrieveRequest.getCorrId()
				+ ",\n for content: " + retrieveRequest.getContentId());

		EDMSRetrieveContentClient edmsRetrieveContentClient = new EDMSRetrieveContentClient(retrieveRequest);
		
		try {
			return invokeEDMSRetrieveContent(edmsRetrieveContentClient);
		} catch(Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	private ResponseEntity<Object> invokeEDMSRetrieveContent(EDMSRetrieveContentClient client) 
			throws Exception {
		
		EDMSJaxb2Marshaller marshaller = context.getBean(EDMSJaxb2Marshaller.class);
		marshaller.setMarshallers(client);
		
		EDMSRetrieveResponseType xml_response = client.retrieveContent();
		EDMSRetrieverResponse json_response = new EDMSRetrieverResponse(xml_response);
		return new ResponseEntity<Object>(json_response, HttpStatus.OK);
	}

}

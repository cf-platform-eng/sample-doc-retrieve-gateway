package org.cf.restgateway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ServiceEndpointLocator {

	public static final String URI = "uri";
	public static final String VCAP_SERVICES = "VCAP_SERVICES";
	
	public static final String PLAN_NAME_FIELD = "plan";
	public static final String SERVICE_NAME_FIELD = "name";
	
	private static final Log log = LogFactory.getLog(ServiceEndpointLocator.class);
	
	//public String serviceBucketName = "service-discovery";
	
	Map<String, Map<String, ServiceEndpointDefn>> serviceRegistryMap = new Hashtable<String, Map<String, ServiceEndpointDefn>>();
	
	public ServiceEndpointLocator() {
		loadServiceDefinitions();
	}
	
	public Map<String, ServiceEndpointDefn> lookupServiceDefinitionsByCategory(String serviceCategoryName) {
		return serviceRegistryMap.get(serviceCategoryName);
	}
	
	public ServiceEndpointDefn lookupServiceEndpointDefnByName(String serviceCategoryName, String serviceName) {
		return serviceRegistryMap.get(serviceCategoryName).get(serviceName);
	}
	
	public ServiceEndpointDefn lookupServiceEndpointDefnByName(String serviceName) {
		for (String serviceBucketName: serviceRegistryMap.keySet()) {
			Map<String, ServiceEndpointDefn> serviceBucket = serviceRegistryMap.get(serviceBucketName);
			if (serviceBucket.containsKey(serviceName))
				return serviceBucket.get(serviceName);
			 
		}
		return null;
	}
	
	
	public void loadServiceDefinitions() {
		
		String vcapServiceDefn = System.getenv(VCAP_SERVICES);
		
		if (vcapServiceDefn == null) {
			log.error("VCAP_SERVICES env variable not found. No Service bound to application!!");
			return;
		}
		
		@SuppressWarnings("unchecked")
		
        HashMap<String, JSONArray> service_defns_map = null;
		
		try {
			service_defns_map = (HashMap<String, JSONArray>) new JSONParser().parse(vcapServiceDefn);
		} catch(ParseException pe) { 
			log.error("Error parsing VCAP_SERVICES env variable content: " + vcapServiceDefn);
			return;
		}
		
		for (String serviceBucketName: service_defns_map.keySet()) {
		
			JSONArray serviceDefns = service_defns_map.get(serviceBucketName);
		
			Map<String, ServiceEndpointDefn> associatedServiceDefns = createServiceEndpoint(serviceDefns);
			serviceRegistryMap.put(serviceBucketName, associatedServiceDefns);
			
		}
	}

	public Map<String, ServiceEndpointDefn> createServiceEndpoint(JSONArray serviceDefns) {
		
		Map<String, ServiceEndpointDefn> serviceEndpointMap = new Hashtable<String, ServiceEndpointDefn>();
		
		for(Object entry: serviceDefns) {
			ServiceEndpointDefn serviceEndpointDefn = new ServiceEndpointDefn();
			
			JSONObject serviceDefn = (JSONObject)entry;
			
			for (Object key: serviceDefn.keySet() )  {
				Object val = serviceDefn.get(key);
				
				if ("credentials".equals(key)) {
					parseCredentials(serviceEndpointDefn, (JSONObject)val);
				} else if ("label".equals(key)) {
					serviceEndpointDefn.setLabel((String)val);
				} else if ("tags".equals(key)) {
					serviceEndpointDefn.setTags((JSONArray)val);
				} else if ("name".equals(key)) {
					serviceEndpointDefn.setServiceName((String)val);
				} else if ("plan".equals(key)) {
					serviceEndpointDefn.setServicePlan((String)val);
				}
				
			}
			log.info("Created ServiceEndpointDefn: " + serviceEndpointDefn);
			serviceEndpointMap.put(serviceEndpointDefn.getServiceName(), serviceEndpointDefn);
			
		}

		return serviceEndpointMap;
	}
	
	public void parseCredentials(ServiceEndpointDefn serviceEndpointDefn, JSONObject credentialMap) {
		for (Object key: credentialMap.keySet() )  {
			Object val = credentialMap.get(key);
			
			if (URI.equals(key)) {
				serviceEndpointDefn.setUri((String)val);
			} else if ("name".equals(key)) {
				serviceEndpointDefn.setUsername((String)val);
			} else if ("password".equals(key)) {
				serviceEndpointDefn.setPassword((String)val);
			} else if ("certName".equals(key)) {
				serviceEndpointDefn.setCertName((String)val);
			} else if ("certLocation".equals(key)) {
				serviceEndpointDefn.setCertLocation((String)val);
			} else if ("certFormat".equals(key)) {
				serviceEndpointDefn.setCertFormat((String)val);
			} else {
				serviceEndpointDefn.addOtherData((String)key, val);
			}
		}
	}
	
}

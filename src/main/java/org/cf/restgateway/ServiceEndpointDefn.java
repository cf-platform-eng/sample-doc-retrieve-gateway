package org.cf.restgateway;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import org.json.simple.JSONArray;

public class ServiceEndpointDefn {

	private String serviceName;
	private String servicePlan;
	private String label;
	private Map<String, Object> otherInfo = new Hashtable<String, Object>();
	
	public void addOtherData(String name, Object val) {
		otherInfo.put(name, val);
	}
	
	public Map<String, Object> getOtherData() {
		return otherInfo;
	}
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	private String[] tags;
	
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public void setTags(JSONArray jsonArray) {
		this.tags = (String[]) jsonArray.toArray(new String[] {});
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServicePlan() {
		return servicePlan;
	}
	public void setServicePlan(String servicePlan) {
		this.servicePlan = servicePlan;
	}
	private String uri;
	private String username, password;
	private String certName;
	private String certLocation;
	private String certFormat;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCertName() {
		return certName;
	}
	public void setCertName(String certName) {
		this.certName = certName;
	}
	public String getCertLocation() {
		return certLocation;
	}
	public void setCertLocation(String certLocation) {
		this.certLocation = certLocation;
	}
	public String getCertFormat() {
		return certFormat;
	}
	public void setCertFormat(String certFormat) {
		this.certFormat = certFormat;
	}

	@Override
	public String toString() {
		return "ServiceEndpointDefn [serviceName=" + serviceName
				+ ", servicePlan=" + servicePlan + ", label=" + label
				+ ", otherInfo=" + otherInfo + ", tags="
				+ Arrays.toString(tags) + ", uri=" + uri + ", username="
				+ username + ", password=" + password + ", certName="
				+ certName + ", certLocation=" + certLocation + ", certFormat="
				+ certFormat + "]";
	}
	
	
}

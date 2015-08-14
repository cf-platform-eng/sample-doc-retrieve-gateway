package org.cf.restgateway.edms.controller;


import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cf.restgateway.edms.wsdl.EDMSRetrieveResponseType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;

public class EDMSRetrieverResponse {
	
    private String csn_id;
	private String corr_id;
	private String content_id;
	private long   file_length_number;
	private String fileName;
	private String fileEncoded;
	private String application_meta_data;
	private String revision_timestamp, creation_timestamp, store_timestamp;
	private String author_name, description, sensitivity_code; 

	
	public EDMSRetrieverResponse(EDMSRetrieveResponseType response) {
		super();
		if (response.getEDMSResponseHeader() != null) {
			this.csn_id = response.getEDMSResponseHeader().getConsumerSourceName();
			this.corr_id = response.getEDMSResponseHeader().getCorrelationId();
		}
		
		if (response.getEDMSRetrieveResponseDetail() != null) {
			
			this.content_id = response.getEDMSRetrieveResponseDetail().getContentId();
			this.file_length_number = response.getEDMSRetrieveResponseDetail().getFileLengthNumber();
			this.fileName = response.getEDMSRetrieveResponseDetail().getFileName();
			this.fileEncoded = response.getEDMSRetrieveResponseDetail().getFileEncoded();
			this.application_meta_data = response.getEDMSRetrieveResponseDetail().getApplicationMetaData();
			this.revision_timestamp = "" + response.getEDMSRetrieveResponseDetail().getRevisionTimestamp();
			this.creation_timestamp = "" + response.getEDMSRetrieveResponseDetail().getCreateTimestamp();
			this.store_timestamp = "" + response.getEDMSRetrieveResponseDetail().getStoreTimestamp();
			this.author_name = response.getEDMSRetrieveResponseDetail().getAuthorName();
			this.description = response.getEDMSRetrieveResponseDetail().getDescription();
			this.sensitivity_code = response.getEDMSRetrieveResponseDetail().getSensitivityCode();
		}
	}

	public String getCsnId() {
		return csn_id;
	}

	public String getCorrId() {
		return corr_id;
	}

	public String getAuthor() {
		return author_name;
	}

	public String getContentId() {
		return content_id;
	}

	public String getCsn_id() {
		return csn_id;
	}

	public void setCsn_id(String csn_id) {
		this.csn_id = csn_id;
	}

	public String getCorr_id() {
		return corr_id;
	}

	public void setCorr_id(String corr_id) {
		this.corr_id = corr_id;
	}

	public String getDescrp() {
		return description;
	}

	public void setDescrp(String descrp) {
		this.description = descrp;
	}

	public String getContent_id() {
		return content_id;
	}

	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}

	public long getFile_length_number() {
		return file_length_number;
	}

	public void setFile_length_number(long file_length_number) {
		this.file_length_number = file_length_number;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileEncoded() {
		return fileEncoded;
	}

	public void setFileEncoded(String fileEncoded) {
		this.fileEncoded = fileEncoded;
	}

	public String getApplication_meta_data() {
		return application_meta_data;
	}

	public void setApplication_meta_data(String application_meta_data) {
		this.application_meta_data = application_meta_data;
	}

	public String getRevision_timestamp() {
		return revision_timestamp;
	}

	public void setRevision_timestamp(String revision_timestamp) {
		this.revision_timestamp = revision_timestamp;
	}

	public String getCreation_timestamp() {
		return creation_timestamp;
	}

	public void setCreation_timestamp(String creation_timestamp) {
		this.creation_timestamp = creation_timestamp;
	}

	public String getStore_timestamp() {
		return store_timestamp;
	}

	public void setStore_timestamp(String store_timestamp) {
		this.store_timestamp = store_timestamp;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSensitivity_code() {
		return sensitivity_code;
	}

	public void setSensitivity_code(String sensitivity_code) {
		this.sensitivity_code = sensitivity_code;
	}

}

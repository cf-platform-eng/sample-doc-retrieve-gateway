package org.cf.restgateway.edms.controller;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;

public class EDMSRetrieverRequest {
	
    private final String csnId;
	private final String corrId;
	private final String userId;
	private final String contentId;
    
    EDMSRetrieverRequest(@JsonProperty("csn_id") String csnId, @JsonProperty("corr_id") String corrId,
                   @JsonProperty("user_id") String userId, @JsonProperty("content_id") String contentId) {
        Assert.notNull(csnId);
        Assert.notNull(contentId);
        Assert.notNull(userId);

        this.csnId = csnId;
        this.corrId = corrId;
        this.userId = userId;
        this.contentId = contentId;
    }

	public String getCsnId() {
		return csnId;
	}

	public String getCorrId() {
		return corrId;
	}

	public String getUserId() {
		return userId;
	}

	public String getContentId() {
		return contentId;
	}

	@Override
	public String toString() {
		return "EDMSRetrieverRequest [csnId=" + csnId + ", corrId=" + corrId
				+ ", userId=" + userId + ", contentId=" + contentId + "]";
	}

}

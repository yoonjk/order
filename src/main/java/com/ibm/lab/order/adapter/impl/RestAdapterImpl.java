package com.ibm.lab.order.adapter.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ibm.lab.order.adapter.ParticipantLink;
import com.ibm.lab.order.adapter.ParticipationRequest;
import com.ibm.lab.order.adapter.RestAdapter;

@Component
public class RestAdapterImpl implements RestAdapter {

	private static final Logger logger = LoggerFactory.getLogger(RestAdapterImpl.class);
	
	private RestTemplate restTemplate = new RestTemplate();
			 
	@Autowired
	private RetryTemplate retryTemplate;
	
	/**
	 * 정상처리 확정 
	 */
	public void confirmAll(List<ParticipantLink> participantLinks) {
		participantLinks.forEach(participantLink-> {
			try {
				retryTemplate.execute((RetryCallback<Void, RestClientException>) context -> {
					restTemplate.put(participantLink.getUri(), null);
					
					return null;
				});
			} catch(RestClientException e) {
				logger.error(String.format("Confirm Error[URI : %s]", participantLink.getUri().toString()), e);
			}
		});

	}

	/**
	 * 보상처리 
	 */
    public void cancelAll(List<ParticipantLink> participantLinks) {
        participantLinks.forEach(participantLink -> {
            try {
                retryTemplate.execute((RetryCallback<Void, RestClientException>) context -> {
                    restTemplate.delete(participantLink.getUri());
                    return null;
                });
            } catch (RestClientException e) {
            	logger.error(String.format("TCC - Cancel Error[URI : %s]", participantLink.getUri().toString()), e);
            }
        });
    }
	
	public List<ParticipantLink> doTry(List<ParticipationRequest> participationRequests) {
		List<ParticipantLink> participantLinks = new ArrayList<>();
		
		participationRequests.forEach(participantRequest -> {
			try {
				ParticipantLink participantLink = 
					retryTemplate.execute((RetryCallback<ParticipantLink, RestClientException>) context-> {
						 URI uri = URI.create(participantRequest.getUrl());
		                    RequestEntity<Map<String, Object>> request =
		                        RequestEntity.post(uri)
		                            .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
		                            .accept(MediaType.APPLICATION_JSON)
		                            .body(participantRequest.getRequestBody());

		                    ResponseEntity<ParticipantLink> response = restTemplate.exchange(request, ParticipantLink.class);
		                    logger.info(String.format("ParticipantLink URI :%s", response.getBody().getUri()));

		                    return response.getBody();
					});
				
				logger.info(String.format("Return ParticipantLink URI :%s", participantLink));
				participantLinks.add(participantLink);
			} catch(RestClientException e) {
				cancelAll(participantLinks);
				throw new RuntimeException(String.format("Try Error[URI:%s]", participantRequest.getUrl()),e);
				
			}
		});
		
		return participantLinks;
	}

}

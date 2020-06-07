package com.ibm.lab.order.adapter;

import java.net.URI;
import java.time.LocalDateTime;

import lombok.ToString;

@ToString 
public class ParticipantLink {
	private URI uri;
	
	private LocalDateTime expires;
	
	public ParticipantLink() {}
	
	public ParticipantLink(URI uri, LocalDateTime expires) {
		this.uri = uri;
		this.expires = expires;
	}
	
	public URI getUri() {
		return uri;
	}

	public LocalDateTime getExpires() {
		return expires;
	}

	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
}

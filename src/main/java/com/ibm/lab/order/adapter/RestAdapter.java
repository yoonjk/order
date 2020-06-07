package com.ibm.lab.order.adapter;

import java.util.List;

public interface RestAdapter {
	void confirmAll(List<ParticipantLink> participantLinks);
	void cancelAll(List<ParticipantLink> participantLinks);
	public List<ParticipantLink> doTry(List<ParticipationRequest> participationRequests);
}

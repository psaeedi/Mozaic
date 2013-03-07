/**
 * 
 */
package com.ahuralab.mozaic.app;

import java.util.List;


import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author pani
 *
 */
public class TwitterClient {
	
	private final Twitter twitter;
	
	public Twitter getTwitter() {
		return twitter;
	}

	public TwitterClient(Twitter twitter) {
		this.twitter = twitter;
	}
		
	public List<Status> getHomeTimeline(long sinceId) throws TwitterException {
		return twitter.getHomeTimeline(new Paging(sinceId));
	}
	
	public List<Status> getHomeTimeline() throws TwitterException {
		return twitter.getHomeTimeline(new Paging(1, 200));
	}
	
	public void sendTwitt(String message) throws TwitterException{
		twitter.updateStatus(message);
	}
	
	public void createFrienship(String userScreenName) throws TwitterException{
		twitter.createFriendship(userScreenName);
	}
	
	public void destroyFriendship(String userScreenName) throws TwitterException{
		twitter.destroyFriendship(userScreenName);
	}
	
	
}

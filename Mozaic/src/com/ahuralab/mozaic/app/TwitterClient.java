/**
 * 
 */
package com.ahuralab.mozaic.app;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author pani
 *
 */
public class TwitterClient {
	
	private final Twitter twitter;
	
	public TwitterClient(Twitter twitter) {
		this.twitter = twitter;
	}
	
	public List<Status> getTimeline() throws TwitterException {
		return twitter.getHomeTimeline();
	}
	
}

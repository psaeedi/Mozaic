/**
 * 
 */
package com.ahuralab.mozaic.db;


/**
 * @author pani
 *
 */
public class Message {

	private final long id;
	private final String content;
	private final String creationDate;
	private final int retweetCount;
	
	private final long userId;
	private final String userName;
	private final String userScreenName;
	private final String userLocation;
	private final String userDescription;
	private final String userHtmlImage;
	
	
	public Message(long id, String content, String creationDate,
			int retweetCount, long userId, String userName,
			String userScreenName, String userLocation, String userDescription, String userHtmlImage) {
		this.id = id;
		this.content = content;
		this.creationDate = creationDate;
		this.retweetCount = retweetCount;
		this.userId = userId;
		this.userName = userName;
		this.userScreenName = userScreenName;
		this.userLocation = userLocation;
		this.userDescription = userDescription;
		this.userHtmlImage = userHtmlImage;
	}


	public long getId() {
		return id;
	}


	public String getContent() {
		return content;
	}


	public String getCreationDate() {
		return creationDate;
	}


	public int getRetweetCount() {
		return retweetCount;
	}


	public long getUserId() {
		return userId;
	}


	public String getUserName() {
		return userName;
	}


	public String getUserScreenName() {
		return userScreenName;
	}


	public String getUserLocation() {
		return userLocation;
	}


	public String getUserDescription() {
		return userDescription;
	}
	
	public String getUserImage(){
		return userHtmlImage;
	}
	
	
}

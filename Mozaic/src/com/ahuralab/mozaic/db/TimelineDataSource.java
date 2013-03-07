/**
 * 
 */
package com.ahuralab.mozaic.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ahuralab.mozaic.app.TwitterClient;
import com.ahuralab.mozaic.auth.TwitterAuthenticator;

/**
 * We create a datbase for twitter timeline
 * 
 * @author pani
 * 
 */
public class TimelineDataSource {

	private TwitterDatabase dbTwitter;
	private TwitterClient twitterClient;

	private static final String[] ALL_COLUMNS = { TwitterDatabase.COLUMN_ID,
			TwitterDatabase.COLUMN_MESSAGEID, 
			TwitterDatabase.COLUMN_USERID,
			TwitterDatabase.COLUMN_TWITTERUSERNAME,
			TwitterDatabase.COLUMN_USERNAME,
			TwitterDatabase.COLUMN_USERLOCATION,
			TwitterDatabase.COLUMN_USERDESCRIPTION,
			TwitterDatabase.COLUMN_CONTENT, 
			TwitterDatabase.COLUMN_CREATEDATE,
			TwitterDatabase.COLUMN_RETWITTCOUNT,
			TwitterDatabase.COLUMN_USERIMAGE };

	private static final String TAG = TimelineDataSource.class.getSimpleName();

	public TimelineDataSource(Context context) {
		dbTwitter = new TwitterDatabase(context);
	}

	public List<Status> fetchAndStoreTimeline(Context context)
			throws TwitterException {
		long lastID = loadLastFetchedMessage();

		TwitterAuthenticator auth = new TwitterAuthenticator();
		Twitter twitter = auth.createTwitter(context);
		twitterClient = new TwitterClient(twitter);

		List<Status> listOfTimeline = null;
		if (lastID != -1) {
			listOfTimeline = twitterClient.getHomeTimeline(lastID);
		} else {
			listOfTimeline = twitterClient.getHomeTimeline();
		}
		breakDownAndStoreTimeline(listOfTimeline);
		// storeTimeLine(listOfTimeline);
		return listOfTimeline;
	}

	private long loadLastFetchedMessage() {
		// TODO return the last fetched id
		return -1;
	}

	private void breakDownAndStoreTimeline(List<Status> listOfTimeline) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbTwitter.getWritableDatabase();
		
		for (Status eachTimeline : listOfTimeline) {
			User eachUser = eachTimeline.getUser();
			String textT = eachTimeline.getText();
			long numberOfRetwitt = eachTimeline.getRetweetCount();
			Date createdDateUser = eachTimeline.getCreatedAt();
			String imageHtml = eachUser.getProfileImageURL();
			String descriptionUser = eachUser.getDescription();
			long idUser = eachUser.getId();
			String nameUser = eachUser.getName();
			String screenNameUser = eachUser.getScreenName();
			String locationUser = eachUser.getLocation();
			values.put(TwitterDatabase.COLUMN_USERID,
					eachTimeline.getId());
			values.put(TwitterDatabase.COLUMN_USERID, idUser);
			values.put(TwitterDatabase.COLUMN_TWITTERUSERNAME, nameUser);
			values.put(TwitterDatabase.COLUMN_USERNAME, screenNameUser);
			values.put(TwitterDatabase.COLUMN_USERLOCATION,
					locationUser);
			values.put(TwitterDatabase.COLUMN_USERDESCRIPTION,
					descriptionUser);
			values.put(TwitterDatabase.COLUMN_CONTENT, textT);
			values.put(TwitterDatabase.COLUMN_CREATEDATE,
					createdDateUser.toString());
			values.put(TwitterDatabase.COLUMN_RETWITTCOUNT,
					numberOfRetwitt);
			values.put(TwitterDatabase.COLUMN_USERIMAGE, imageHtml);
			db.insert(TwitterDatabase.TABLE_MESSAGES, null, values);
			Log.d(TAG, "Storing: " + screenNameUser);
		}
		dbTwitter.close();
	}

	public List<Message> readTimelineFromDB() {
		SQLiteDatabase db = dbTwitter.getReadableDatabase();
		Cursor cursor = db.query(TwitterDatabase.TABLE_MESSAGES, ALL_COLUMNS,
				null, null, null, null, null);
		List<Message> allTexts = new ArrayList<Message>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long id = cursor.getLong(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_MESSAGEID));
			String content = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_CONTENT));
			String creationDate = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_CREATEDATE));
			int retweetCount = cursor.getInt(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_RETWITTCOUNT));
			long userId = cursor.getLong(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_USERID));
			String userName = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_TWITTERUSERNAME));
			String userScreenName = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_USERNAME));
			String userLocation = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_USERLOCATION));
			String userDescription = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_USERDESCRIPTION));
			String userImage = cursor.getString(cursor
					.getColumnIndex(TwitterDatabase.COLUMN_USERIMAGE));

			allTexts.add(new Message(id, content, creationDate, retweetCount,
					userId, userName, userScreenName, userLocation,
					userDescription, userImage));
			Log.d(TAG, "Loading: " + id);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return allTexts;
	}

	public void deleteTimeline() {
		// TODO
	}

	public boolean startStoringDb() {

		return false;
	}

}

/**
 * 
 */
package com.ahuralab.mozaic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author pani
 * 
 */
public class TwitterDatabase extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "messages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MESSAGEID = "messageId";
	public static final String COLUMN_USERID = "_userId";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_TWITTERUSERNAME = "twitterUsername";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_USERLOCATION= "userLocation";
	public static final String COLUMN_USERDESCRIPTION = "userDescription";
	public static final String COLUMN_CREATEDATE = "date";
	public static final String COLUMN_RETWITTCOUNT = "retwitt";
	public static final String COLUMN_USERIMAGE = "profileimage";		
	
	private static final String DATABASE_NAME = "timeline.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" 
			+ COLUMN_ID + " Integer primary key autoincrement, " 
			+ COLUMN_MESSAGEID + " LONG UNSIGNED ,  "
			+ COLUMN_USERID + " LONG UNSIGNED ,  "
			+ COLUMN_TWITTERUSERNAME + " VARCHAR(255) NOT NULL  ,  "
			+ COLUMN_USERNAME + " VARCHAR(255) NOT NULL , "
			+ COLUMN_USERLOCATION + " VARCHAR(255) NOT NULL , "
			+ COLUMN_USERDESCRIPTION + " VARCHAR(255) NOT NULL , "
			+ COLUMN_CONTENT + " VARCHAR(1024) NOT NULL , "
			+ COLUMN_CREATEDATE  + " VARCHAR(255) NOT NULL ,  "
			+ COLUMN_RETWITTCOUNT  + " INTEGER UNSIGNED , "
			+ COLUMN_USERIMAGE + " VARCHAR(255) NOT NULL "
			+ ");";

	public TwitterDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TwitterDatabase.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		// TODO are you sure?
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}
}

/**
 * 
 */
package com.ahuralab.mozaic.app;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ahuralab.mozaic.db.TimelineDataSource;

/**
 * @author pani
 * 
 */
public class FetcherService extends Service {

	private static final String TAG = FetcherService.class.getSimpleName();

	// private List<Status> listOfTimeline = new ArrayList<Status>();

	private LoadTimelineTask loadTimeTask;
	private final IBinder mBinder = new MyBinder();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		FetcherService getService() {
			return FetcherService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("LocalService", "Received start id ");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences("WTF", 1);
		long lastFetchedTimed = sharedPreferences
				.getLong("last_fetchedtime", 0);

		if (lastFetchedTimed + 350000 < System.currentTimeMillis()) {
			// TODO we should send a message back to the caller
			loadTimeTask = new LoadTimelineTask();
			loadTimeTask.execute((Void) null);

			sharedPreferences.edit().putLong("last_fetchedtime",
					System.currentTimeMillis()).commit();
			

		}

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class LoadTimelineTask extends AsyncTask<Void, Void, List<Status>> {
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			TimelineDataSource timeline = new TimelineDataSource(
					getApplicationContext());
			try {
				List<twitter4j.Status> messages = timeline
						.fetchAndStoreTimeline(getApplicationContext());
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				// TODO send notification

				return messages;
			} catch (TwitterException e) {
				Log.e(TAG, "" + e.getMessage(), e);
			}
			return null;
		}
	}
}

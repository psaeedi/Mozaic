package com.ahuralab.mozaic;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ahuralab.mozaic.auth.TwitterAuthenticator;
import com.ahuralab.mozaic.auth.TwitterLoginActivity;


public class MainActivity extends Activity {

	private TextView welcomeView;
	private FetchUserTask mAuthTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		welcomeView = (TextView) findViewById(R.id.welcome_message);
		
		TwitterAuthenticator auth = new TwitterAuthenticator();
		if (!auth.isAuthenticated(this)) {
			Intent intent = new Intent(this, TwitterLoginActivity.class);
			startActivity(intent);
			return;
		} else {
			getUsername();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void getUsername() {
		if (mAuthTask != null) {
			return;
		}
		
		mAuthTask = new FetchUserTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class FetchUserTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			TwitterAuthenticator auth = new TwitterAuthenticator();
			Twitter twitter = auth.createTwitter(MainActivity.this);
			try {
				welcomeView.setText("Hello " + twitter.getScreenName());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

}

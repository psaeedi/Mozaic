package com.ahuralab.mozaic.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;

import com.ahuralab.mozaic.MainActivity;
import com.ahuralab.mozaic.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 * 
 * TODO add a web view for twitter login
 */
public class TwitterLoginActivity extends Activity {

	// UI references.
	private WebView myWebView;
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myWebView = (WebView) findViewById(R.id.webview);
		
		Intent intent = getIntent();		
		if (intent.getAction() != Intent.ACTION_VIEW) { 
			attemptLogin();
			return;
		} else {
			// Callback
		    Log.w("redirect-to-app", "going to save the key and secret");
			 
		    Uri uri = intent.getData();
		    TwitterAuthenticator twitterAuth = new TwitterAuthenticator();
		    twitterAuth.handleCallBack(uri, this);
		    finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		
		mAuthTask = new UserLoginTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			TwitterAuthenticator auth = new TwitterAuthenticator();
			String url = auth.getUrl(TwitterLoginActivity.this);
			//myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.loadUrl(url);
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

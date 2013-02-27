package com.ahuralab.mozaic.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ahuralab.mozaic.MainActivity;
import com.ahuralab.mozaic.R;
import com.ahuralab.mozaic.SettingsActivity;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 * 
 * TODO add a web view for twitter login
 */
public class TwitterLoginActivity extends Activity {

	// UI references.
	private WebView myWebView;
	private TwitterAuthenticator twitterAuth;
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myWebView = (WebView) findViewById(R.id.webview);
		
		twitterAuth = new TwitterAuthenticator();	
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Intent intent = getIntent();		
		if (intent.getAction() != Intent.ACTION_VIEW) { 
			attemptLogin();
		} else {
			/*
			// Callback
		    Log.w("redirect-to-app", "going to save the key and secret");	 
		    Uri uri = intent.getData();
		    twitterAuth.handleCallBack(uri, this.getApplicationContext());
		    intent = new Intent(this.getApplicationContext(), MainActivity.class);
		    startActivity(intent);*/
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	
	@Override
	public void onDestroy(){
		twitterAuth = null;
		super.onDestroy();
		
	}
	
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		else{
		    mAuthTask = new UserLoginTask();
		    mAuthTask.execute((Void) null);
		}
	}

	private class AuthVebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			final Uri uri = Uri.parse(url);
			final String oauthVerifier = uri.getQueryParameter("oauth_verifier");
			if (oauthVerifier != null) {
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected void onPostExecute(Void result) {
					    finish();
					}

					@Override
					protected Void doInBackground(Void... params) {
						// Callback
					    Log.w("redirect-to-app", "going to save the key and secret");	 
					    twitterAuth.handleCallBack(uri, TwitterLoginActivity.this.getApplicationContext());
						return null;
					}
					
				};
				task.execute();
			}

		}
		
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			String url = twitterAuth.getUrl(TwitterLoginActivity.this.getApplicationContext());
			//myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.setWebViewClient(new AuthVebViewClient());
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

package com.ahuralab.mozaic.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ahuralab.mozaic.R;
import com.ahuralab.mozaic.TimelineActivity;

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

		if (isNetworkAvailable() == false) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Network Error");
			builder.setMessage("There is no connection avaiable!");
		}
		setContentView(R.layout.activity_login);
		myWebView = (WebView) findViewById(R.id.webview);
		twitterAuth = new TwitterAuthenticator();
		//Context context = TwitterLoginActivity.this.getApplicationContext();
		/*if(!twitterAuth.isAuthenticated(context)){
			twitterAuth.createTwitter(context);
		}*/
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Context context = TwitterLoginActivity.this.getApplicationContext();
		if(!twitterAuth.isAuthenticated(context)){
			twitterAuth.createTwitter(context);
		}
		Intent intent = getIntent();		
		if (intent.getAction() != Intent.ACTION_VIEW) { 
			attemptLogin();
		} else {
			
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

	private class AuthWebViewClient extends WebViewClient {

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
		
		private String url;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			url = twitterAuth.getUrl(TwitterLoginActivity.this.getApplicationContext());
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			//myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.setWebViewClient(new AuthWebViewClient());
			myWebView.loadUrl(url);
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
}

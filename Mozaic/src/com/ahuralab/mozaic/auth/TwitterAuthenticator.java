/**
 * 
 */
package com.ahuralab.mozaic.auth;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * @author pani
 * 
 */
public class TwitterAuthenticator {

	// TODO change this callback in sth that the browser will not handle
	public static final String CALLBACKURL = "twitterapp://connect";
	public static final String consumerKey = "QPxhqFDEjZJ5sLppvPrA";
	public static final String consumerSecret = "tJFPhzO0ZxfwfXqOIfBEaXtXEkVc2Yb8TQQLkyAink";

	private final OAuthProvider httpOauthprovider = new DefaultOAuthProvider(
			"https://api.twitter.com/oauth/request_token",
			"https://api.twitter.com/oauth/access_token",
			"https://api.twitter.com/oauth/authorize");
	
	private final CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(
			consumerKey, consumerSecret);

	
	public void authenticate(Context context) {

		try {
			String authUrl = httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, CALLBACKURL);
			// TODO Instead of opening the browser we can display this in a web view
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
			context.startActivity(intent);
		} catch (Exception e) {
			Log.w("oauth fail", e);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public String getUrl(Context context) {
		try {
			return httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, CALLBACKURL);
		} catch (Exception e) {
			Log.w("oauth fail", e);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			return null;
		} 
	}
	
	public void handleCallBack(Uri uri, Context context) {

		if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

			String verifier = uri
					.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
			try {
				// this will populate token and token_secret in consumer

				httpOauthprovider.retrieveAccessToken(httpOauthConsumer,
						verifier);
				String userKey = httpOauthConsumer.getToken();
				String userSecret = httpOauthConsumer.getTokenSecret();

				// Save user_key and user_secret in user preferences and return
				SharedPreferences settings = context
						.getSharedPreferences("twitter_credential", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("user_key", userKey);
				editor.putString("user_secret", userSecret);
				editor.commit();

			} catch (Exception e) {
				Log.e("", "" + e.getMessage(), e);
			}
		} else {
			// Do something if the callback comes from elsewhere
		}
	}

	
	public Twitter createTwitter(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences("twitter_credential", 0);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(settings.getString("user_key", null))
				.setOAuthAccessTokenSecret(settings.getString("user_secret", null));
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}
	public boolean isAuthenticated(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences("twitter_credential", 0);
		String key = settings.getString("user_key", null);
		String secret = settings.getString("user_secret", null);
		
		return key != null && secret != null;
	}
}

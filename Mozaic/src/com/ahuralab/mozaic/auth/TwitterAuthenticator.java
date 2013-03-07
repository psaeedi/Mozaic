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
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

/**
 * @author pani
 * 
 */
public class TwitterAuthenticator {

	private static final String TWITTER_CREDENTIAL = "twitter_credential";
	// TODO change this callback in sth that the browser will not handle
	public static final String CALLBACKURL = "http://ahuralab.com/mozaic";//;"twitterapp://connect";
	public static final String consumerKey = "QPxhqFDEjZJ5sLppvPrA";
	public static final String consumerSecret = "tJFPhzO0ZxfwfXqOIfBEaXtXEkVc2Yb8TQQLkyAink";

	private final OAuthProvider httpOauthprovider = new DefaultOAuthProvider(
			"https://api.twitter.com/oauth/request_token",
			"https://api.twitter.com/oauth/access_token",
			"https://api.twitter.com/oauth/authorize");
	
	private final CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(
			consumerKey, consumerSecret);
	
	
	/*
	public void authenticate(Context context) {
		try {
			String authUrl = httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, CALLBACKURL);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
			context.startActivity(intent);
		} catch (Exception e) {
			Log.w("oauth fail", e);
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}*/

	public String getUrl(Context context) {
		try {
			return httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, CALLBACKURL);
		} catch (Exception e) {
			Log.w("oauth fail", e);
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			return null;
		} 
	}
	
	public void handleCallBack(Uri uri, Context context) {

		if (uri != null) {
			Log.d("", uri.toString());
			try {
				final String oauthVerifier = uri.getQueryParameter("oauth_verifier");
				httpOauthprovider.retrieveAccessToken(httpOauthConsumer, oauthVerifier);
				String userKey = httpOauthConsumer.getToken();
				String userSecret = httpOauthConsumer.getTokenSecret();

				// Save user_key and user_secret in user preferences and return
				SharedPreferences settings = context
						.getSharedPreferences(TWITTER_CREDENTIAL, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("user_key", userKey);
				editor.putString("user_secret", userSecret);
				editor.commit();
			} catch (Exception e) {
				Log.e("", "" + e.getMessage(), e);
			}
		} else {
			// Do something if the callback comes from elsewhere
			//return;
		}
	}

	
	public Twitter createTwitter(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(TWITTER_CREDENTIAL, 0);
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
				.getSharedPreferences(TWITTER_CREDENTIAL, 0);
		String key = settings.getString("user_key", null);
		String secret = settings.getString("user_secret", null);
		
		return key != null && secret != null;
	}
	
	public void twitterSignOut (Context context){
		SharedPreferences settings = context
				.getSharedPreferences(TWITTER_CREDENTIAL, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(consumerKey);
		editor.remove(consumerSecret);
		editor.commit();
	}
}

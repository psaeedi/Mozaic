/**
 * 
 */
package com.ahuralab.mozaic.app;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.ahuralab.mozaic.auth.TwitterAuthenticator;

import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

/**
 * @author pani
 *
 */
public class TwitterClientTestCase extends InstrumentationTestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		SharedPreferences settings = getInstrumentation().getTargetContext()
				.getSharedPreferences("twitter_credential", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user_key", "925505215-tlKphqlLdPNM6GBNkOGOdq2yE54yhL5JEz7GCi8z");
		editor.putString("user_secret", "YqES4sYFptLeCSIXGXFy42ScfLjIljTFe9A2oNd90");
		editor.commit();

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link com.ahuralab.mozaic.app.TwitterClient#getTimeline(java.util.Date)}.
	 * @throws TwitterException 
	 */
	public void testGetTimeline() throws TwitterException {
		TwitterAuthenticator auth = new TwitterAuthenticator();
		Twitter twitter =  auth.createTwitter(getInstrumentation().getTargetContext());
		TwitterClient client = new TwitterClient(twitter);
		List<Status> timeLine = client.getTimeline();
		assertEquals(timeLine.size() > 0, true);
	}

}

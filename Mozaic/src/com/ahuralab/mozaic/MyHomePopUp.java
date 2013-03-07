/**
 * 
 */
package com.ahuralab.mozaic;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.ahuralab.mozaic.TimelineActivity.SendTweetTask;
import com.ahuralab.mozaic.app.TwitterClient;
import com.ahuralab.mozaic.auth.TwitterAuthenticator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * @author pani
 * 
 */
public class MyHomePopUp extends Activity {
	
	private EditText view;
	private TwitterClient client;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myhomepop);
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.popup, null);
		
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view = (EditText) popupView.findViewById(R.id.editTextStatus);
		Button btnSendTwitt = (Button) popupView
				.findViewById(R.id.dismiss);
		
		
		btnSendTwitt.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SendTweetTask().execute();
				popupWindow.dismiss();
			}
		});
		popupWindow.setFocusable(true);
		//popupWindow.showAsDropDown(welcomeView, 50, -30);
	}


	public void onStart() {
		super.onStart();
	}
	
	public class SendTweetTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				TwitterAuthenticator auth = new TwitterAuthenticator();
				Twitter twitter = auth.createTwitter(MyHomePopUp.this.getApplicationContext());
				client = new TwitterClient(twitter);
				String status = view.getText().toString();
				client.sendTwitt(status);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			client = null;
		}

		@Override
		protected void onCancelled() {
			client = null;
		}
	}

}

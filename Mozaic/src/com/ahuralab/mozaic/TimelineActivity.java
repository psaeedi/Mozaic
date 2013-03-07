package com.ahuralab.mozaic;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;

import android.widget.TextView;

import com.ahuralab.mozaic.app.FetcherService;
import com.ahuralab.mozaic.app.TwitterClient;
import com.ahuralab.mozaic.auth.TwitterAuthenticator;
import com.ahuralab.mozaic.auth.TwitterLoginActivity;
import com.ahuralab.mozaic.db.Message;
import com.ahuralab.mozaic.db.TimelineDataSource;
import com.ahuralab.mozaic.util.SystemUiHider;
import com.loopj.android.image.SmartImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class TimelineActivity extends Activity {
	
	private GridView messageGrid;
	private FetchUserTask mAuthTask;
	private Button welcomeView;
	private Button btnLogOut;
	private Button btnReload;
	private EditText view;
	
	private TwitterClient client;

	final String mimeType = "text/html";
	final String encoding = "UTF-8";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		messageGrid = (GridView) findViewById(R.id.message_grid);
		welcomeView = (Button) findViewById(R.id.welcome_message);
		TwitterAuthenticator auth = new TwitterAuthenticator();
		if (!auth.isAuthenticated(this.getApplicationContext())) {
			Intent intent = new Intent(this.getApplicationContext(),
					TwitterLoginActivity.class);
			startActivity(intent);
		} else {
			startService(new Intent(TimelineActivity.this, FetcherService.class));
		}
	}

	public void onStart() {
		super.onStart();
		getUsername();
		new LoadTimelineTask().execute();

		btnReload = (Button) findViewById(R.id.btnReload);
		btnReload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new LoadTimelineTask().execute();
				// Just reload the content
			}
		});

		btnLogOut = (Button) findViewById(R.id.btnLogOut);
		btnLogOut.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TwitterAuthenticator auth = new TwitterAuthenticator();
				auth.twitterSignOut((TimelineActivity.this
						.getApplicationContext()));
				setContentView(R.layout.activity_login);
				Intent intent = new Intent(getApplicationContext(),
						TwitterLoginActivity.class);
				startActivity(intent);
			}
		});

		welcomeView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
		       /* layoutInflater.inflate(R.layout.popup, null);
				Intent intent = new Intent(getApplicationContext(),
						MyHomePopUp.class);
				startActivity(intent);*/
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
				popupWindow.showAsDropDown(welcomeView, 50, -30);
			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
			Intent intent = new Intent(this.getApplicationContext(),
					SettingsActivity.class);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Leaving Mozaic")
				.setMessage("Are you sure you want to exit Mozaic?")
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						TimelineActivity.super.onBackPressed();
					}
				})
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//TimelineActivity.super.onBackPressed();
						//do nothing
					}
				}).create().show();
	}

	public void getUsername() {
		if (mAuthTask != null) {
			return;
		}

		mAuthTask = new FetchUserTask();
		mAuthTask.execute((Void) null);
	}

	public class FetchUserTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			TwitterAuthenticator auth = new TwitterAuthenticator();
			Twitter twitter = auth.createTwitter(TimelineActivity.this
					.getApplicationContext());
			try {
				final String screenName = twitter.getScreenName();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						welcomeView.setText("Hello " + screenName + "!");
					}
				});
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

	private class LoadTimelineTask extends AsyncTask<Void, Void, List<Message>> {
		@Override
		protected List<Message> doInBackground(Void... params) {
			TimelineDataSource timeline = new TimelineDataSource(
					getApplicationContext());
			return timeline.readTimelineFromDB();
		}

		@Override
		protected void onPostExecute(List<Message> result) {
			super.onPostExecute(result);
			BaseAdapter adapter = new MozaicGridAdapter(result);
			messageGrid.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

	}
	
	public class SendTweetTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				TwitterAuthenticator auth = new TwitterAuthenticator();
				Twitter twitter = auth.createTwitter(TimelineActivity.this.getApplicationContext());
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

	private class MozaicGridAdapter extends BaseAdapter {

		private final List<Message> messages;

		public MozaicGridAdapter(List<Message> messages) {
			this.messages = messages;
		}

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO not sure about this
			return messages.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Message message = (Message) getItem(position);

			if (convertView == null) {
				// here we inflate the layout
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.mozaic_square, null);

				// TODO fill all the views
				TextView username = (TextView) v
						.findViewById(R.id.message_username);
				username.setText(message.getUserName());

				TextView location = (TextView) v
						.findViewById(R.id.message_location);
				location.setText(message.getUserLocation());

				TextView time = (TextView) v.findViewById(R.id.message_time);
				time.setText(message.getCreationDate());

				Button twitterusername = (Button) v
						.findViewById(R.id.message_twitterusername);
				twitterusername.setText(message.getUserScreenName());
				// TODO on listener open a popupview to 1- unfollowhim,
				// 2- retwitt its tweet

				SmartImageView twitteruserimage = (SmartImageView) v
						.findViewById(R.id.imageView1);
				twitteruserimage.setImageUrl(message.getUserImage());

				TextView content = (TextView) v
						.findViewById(R.id.message_content);
				content.setText(message.getContent());
			}

			return v;
		}

	}

}
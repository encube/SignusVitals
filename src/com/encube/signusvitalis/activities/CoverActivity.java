package com.encube.signusvitalis.activities;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.application.SignusVitalisApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

public class CoverActivity extends Activity {
	private SignusVitalisApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (SignusVitalisApplication) getApplication();
		if (!application.getSessionID().equals("none")) {
			finish();
			Intent intent = new Intent(CoverActivity.this,
					MainFragmentActivity.class);
			intent.putExtra("requestFromNotification", false);
			startActivity(intent);
		} else {
			setContentView(R.layout.activity_cover);
			new DelayTask().execute();
		}
	}

	private class DelayTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
//			SystemClock.sleep(3000); TODO change this
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
			Intent intent = new Intent(CoverActivity.this,
					SplashScreenActivity.class);
			startActivity(intent);
			super.onPostExecute(result);
		}
	}

}

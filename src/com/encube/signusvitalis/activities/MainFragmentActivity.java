package com.encube.signusvitalis.activities;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.alertdialog.MenuDialogAbout;
import com.encube.signusvitalis.alertdialog.MenuDialogHelp;
import com.encube.signusvitalis.alertdialog.MenuDialogSettings;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.ErrorCodes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragmentActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;
	private SignusVitalisApplication application;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean requestFromNotification = getIntent().getBooleanExtra(
				"requestFromNotification", false);
		setContentView(R.layout.fragment_activity_main);
		application = (SignusVitalisApplication) getApplication();

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		pd = new ProgressDialog(MainFragmentActivity.this);

		mTabHost.addTab(
				mTabHost.newTabSpec("home").setIndicator(
						createTab(this, "HOME")), HomeFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("patients list").setIndicator(
						createTab(this, "PATIENT LIST")),
				PatientListFragment.class, null);
		switch (application.getError()) {
		case UNSUPPORTED_ENCODING_EXCEPTION:
			Toast.makeText(
					this,
					"unsupported encoding client error while fetching patient data",
					Toast.LENGTH_SHORT).show();
			break;
		case CLIENT_PROTOCOL_EXCEPTION:
			Toast.makeText(this, "cannot reach the server", Toast.LENGTH_SHORT)
					.show();
			break;
		case JSON_EXCEPTION:
			Toast.makeText(this, "server error while fetching patient data",
					Toast.LENGTH_SHORT).show();
			break;
		case IO_EXCEPTION:
			Toast.makeText(this, "cannot reach the server", Toast.LENGTH_SHORT)
					.show();
			break;
		case EXCEPTION:
			Toast.makeText(this, "server error while fetching patient data",
					Toast.LENGTH_SHORT).show();
			break;
		case NONE:
			break;

		default:
			break;
		}
		if (requestFromNotification) {
			mTabHost.setCurrentTab(1);
		}
	}

	private View createTab(final Context context, final String title) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_widget,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tab_title);
		tv.setText(title);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_fragment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			new MenuDialogSettings(this, application).show();
			return true;

		case R.id.menu_about:
			new MenuDialogAbout(this).show();
			return true;

		case R.id.menu_help:
			new MenuDialogHelp(this).show();
			return true;
		case R.id.menu_log_out:
			new logout().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showErrorToast() {
		switch (application.getError()) {
		case CLIENT_PROTOCOL_EXCEPTION:
			Toast.makeText(MainFragmentActivity.this,
					"cannot reach the server", Toast.LENGTH_SHORT).show();
			break;
		case IO_EXCEPTION:
			Toast.makeText(MainFragmentActivity.this,
					"cannot reach the server", Toast.LENGTH_SHORT).show();
			break;
		case NONE:
			break;
		default:
			break;
		}

	}

	private class logout extends AsyncTask<Void, String, Void> {
		private boolean flag;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				flag = true;
				publishProgress("Logging out...");
				Log.d("logout", "onReleased");
				application.logout();
				Log.d("logout", "logged out");
			} catch (ClientProtocolException e) {
				flag = false;
				application.setError(ErrorCodes.CLIENT_PROTOCOL_EXCEPTION);
				e.printStackTrace();
			} catch (IOException e) {
				flag = false;
				application.setError(ErrorCodes.IO_EXCEPTION);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (flag) {
				MainFragmentActivity.this.finish();
				application.setMonitoredWard(-1);
				Intent intent = new Intent(MainFragmentActivity.this,
						SplashScreenActivity.class);
				startActivity(intent);
			} else {
				showErrorToast();
				application.startNotifierService();
				application.setLogIn(true);
				mTabHost.invalidate();
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}

		@Override
		protected void onPreExecute() {
			pd.setCancelable(false);
			publishProgress("Deactivating Services and Running Threads...");
			application.setLogIn(false);
			pd.show();
		}
	}

}

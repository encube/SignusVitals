package com.encube.signusvitalis.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.R;

import com.encube.signusvitalis.alertdialog.MenuDialogServerAddress;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.ErrorCodes;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {

	private boolean mIsBackButtonPressed;
	private SignusVitalisApplication application;
	private int myProgress;
	private ProgressBar pb;
	private String username;
	private String password;
	private Button loginButton;
	private Button registerButton;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private RelativeLayout loginForm;
	private TextView loading;
	private TextView login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		loginButton = (Button) findViewById(R.id.log_in_button);
		registerButton = (Button) findViewById(R.id.register_button);
		usernameEditText = (EditText) findViewById(R.id.username_edit_text);
		passwordEditText = (EditText) findViewById(R.id.password_edit_text);
		loginForm = (RelativeLayout) findViewById(R.id.loginForm);
		loading = (TextView) findViewById(R.id.loading);
		login = (TextView) findViewById(R.id.login_to_continue);

		application = (SignusVitalisApplication) getApplication();
		pb = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
		pb.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		loginForm.setVisibility(View.VISIBLE);
		login.setVisibility(View.VISIBLE);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (usernameEditText.getText().toString().length() == 0
						|| passwordEditText.getText().toString().length() == 0) {
					Toast.makeText(SplashScreenActivity.this,
							"please fill up username and password",
							Toast.LENGTH_SHORT).show();
				} else {
					username = usernameEditText.getText().toString();
					password = passwordEditText.getText().toString();
					new ShowProgressBarAsyncTask().execute();
				}
			}
		});
		registerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				Intent intent = new Intent(SplashScreenActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.server_address:
			new MenuDialogServerAddress(this, application).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showErrorToast() {
		switch (application.getError()) {
		case LOGIN_ERROR:
			Toast.makeText(this, "incorrect username or password",
					Toast.LENGTH_SHORT).show();
			break;
		case CLIENT_PROTOCOL_EXCEPTION:
			Toast.makeText(this, "cannot reach server", Toast.LENGTH_SHORT)
					.show();
			break;
		case UNSUPPORTED_ENCODING_EXCEPTION:
			Toast.makeText(this, "server error", Toast.LENGTH_SHORT).show();
			break;
		case IO_EXCEPTION:
			Toast.makeText(this, "cannot reach the server", Toast.LENGTH_SHORT)
					.show();
			break;
		case JSON_EXCEPTION:
			Toast.makeText(this, "server error", Toast.LENGTH_SHORT).show();
			break;
		case EXCEPTION:
			Toast.makeText(this, "unknown error", Toast.LENGTH_SHORT).show();
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}

	private class ShowProgressBarAsyncTask extends
			AsyncTask<Void, Integer, Void> {

		private boolean flag;

		@Override
		protected Void doInBackground(Void... params) {
			while (myProgress < 45) {
				myProgress++;
				publishProgress(myProgress);
				SystemClock.sleep(10);
			}
			try {
				flag = application.login(username, password);
				if (!flag) {
					application.setError(ErrorCodes.LOGIN_ERROR);
					myProgress = 100;
				} else {
					application.setError(ErrorCodes.NONE);
				}
			} catch (UnsupportedEncodingException e) {
				flag = false;
				myProgress = 100;
				application.setError(ErrorCodes.UNSUPPORTED_ENCODING_EXCEPTION);
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				flag = false;
				myProgress = 100;
				application.setError(ErrorCodes.CLIENT_PROTOCOL_EXCEPTION);
				e.printStackTrace();
			} catch (IOException e) {
				flag = false;
				myProgress = 100;
				application.setError(ErrorCodes.IO_EXCEPTION);
				e.printStackTrace();
			} catch (JSONException e) {
				flag = false;
				myProgress = 100;
				application.setError(ErrorCodes.JSON_EXCEPTION);
				e.printStackTrace();
			} catch (Exception e) {
				flag = false;
				myProgress = 100;
				application.setError(ErrorCodes.EXCEPTION);
				e.printStackTrace();
			}
			while (myProgress < 75) {
				myProgress++;
				publishProgress(myProgress);
				SystemClock.sleep(10);
			}
			if (flag) {
				try {
					application.fetchWards();
					application.fetchUserInformation();
					application.setError(ErrorCodes.NONE);
				} catch (UnsupportedEncodingException e) {
					myProgress = 100;
					application
							.setError(ErrorCodes.UNSUPPORTED_ENCODING_EXCEPTION);
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					myProgress = 100;
					application.setError(ErrorCodes.CLIENT_PROTOCOL_EXCEPTION);
					e.printStackTrace();
				} catch (IOException e) {
					myProgress = 100;
					application.setError(ErrorCodes.IO_EXCEPTION);
					e.printStackTrace();
				} catch (JSONException e) {
					myProgress = 100;
					application.setError(ErrorCodes.JSON_EXCEPTION);
					e.printStackTrace();
				} catch (Exception e) {
					myProgress = 100;
					application.setError(ErrorCodes.EXCEPTION);
					e.printStackTrace();
				}
			}
			while (myProgress < 100) {
				myProgress++;
				publishProgress(myProgress);
				SystemClock.sleep(10);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values[0] < 35) {
				loading.setText("Requesting Server Access...");
			} else if (values[0] < 75) {
				loading.setText("Gathering User Information...");
			} else if (values[0] < 90) {
				loading.setText("Starting Pulling Service...");
			} else {
				loading.setText("Finalizing ...");
			}
			pb.setProgress(values[0]);
			pb.setSecondaryProgress(values[0] + 1);
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!mIsBackButtonPressed) {
				if (flag) {
					Log.d("flag: ", flag + "");
					finish();

					Intent intent = new Intent(SplashScreenActivity.this,
							MainFragmentActivity.class);
					intent.putExtra("requestFromNotification", false);
					SplashScreenActivity.this.startActivity(intent);
				} else {
					Log.d("flag: ", flag + "");
					pb.setVisibility(View.GONE);
					loading.setVisibility(View.GONE);
					loginForm.setVisibility(View.VISIBLE);
					login.setVisibility(View.VISIBLE);
					showErrorToast();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			loginForm.setVisibility(View.GONE);
			login.setVisibility(View.GONE);
			myProgress = 0;
			pb.setSecondaryProgress(0);
			loading.setVisibility(View.VISIBLE);
			pb.setVisibility(View.VISIBLE);
		}
	}

}

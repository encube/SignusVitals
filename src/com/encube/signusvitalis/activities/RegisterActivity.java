package com.encube.signusvitalis.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.Ward;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText middleNameEditText;
	private EditText passwordEditText;
	private EditText confirmPasswordEditText;
	private RadioButton nurseRadioButton;
	private RadioButton physicianRadioButton;
	private Button registerButton;
	private TextView wardsSpinnerLabel;
	private Spinner wardSpinner;
	private SignusVitalisApplication application;
	private ArrayList<Ward> wards;
	private ArrayList<String> ward;
	private ArrayAdapter<String> adapter;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		application = (SignusVitalisApplication) getApplication();
		ward = new ArrayList<String>();
		wards = new ArrayList<Ward>();
		firstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
		lastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
		middleNameEditText = (EditText) findViewById(R.id.middle_name_edit_text);
		passwordEditText = (EditText) findViewById(R.id.password_edit_text);
		pd = new ProgressDialog(this);
		confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password_edit_text);
		nurseRadioButton = (RadioButton) findViewById(R.id.nurse_radio_button);
		physicianRadioButton = (RadioButton) findViewById(R.id.physician_radio_button);
		registerButton = (Button) findViewById(R.id.register_button);
		wardsSpinnerLabel = (TextView) findViewById(R.id.ward_text_view);
		wardSpinner = (Spinner) findViewById(R.id.ward_spinner);
		wardsSpinnerLabel.setVisibility(View.INVISIBLE);
		wardSpinner.setVisibility(View.INVISIBLE);
		nurseRadioButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new FetchWards().execute();
			}
		});
		physicianRadioButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				wardsSpinnerLabel.setVisibility(View.INVISIBLE);
				wardSpinner.setVisibility(View.INVISIBLE);
			}
		});
		registerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (firstNameEditText.getText().toString().length() == 0
						|| lastNameEditText.getText().toString().length() == 0
						|| passwordEditText.getText().toString().length() == 0
						|| middleNameEditText.getText().toString().length() == 0
						|| confirmPasswordEditText.getText().toString()
								.length() == 0
						|| (!physicianRadioButton.isChecked() && !nurseRadioButton
								.isChecked())) {
					Toast.makeText(RegisterActivity.this,
							"please fill all the fields provided",
							Toast.LENGTH_LONG).show();
				} else if (!passwordEditText.getText().toString()
						.equals(confirmPasswordEditText.getText().toString())) {
					Toast.makeText(RegisterActivity.this,
							"confirm password doesnt match", Toast.LENGTH_LONG)
							.show();

				} else {
					new Register().execute();
				}

			}
		});

	}

	private class FetchWards extends AsyncTask<Void, Void, Void> {
		private boolean flag;
		private String error = "error on fetching";

		@Override
		protected Void doInBackground(Void... params) {
			try {
				application.fetchWards();
				wards = application.getWards();
				for (Ward wrd : wards) {
					ward.add(wrd.getName());
				}
				flag = true;
			} catch (UnsupportedEncodingException e) {
				flag = false;
				error = "internal error";
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				error = "connection error";
				flag = false;
				e.printStackTrace();
			} catch (JSONException e) {
				error = "invalid server respond";
				flag = false;
				e.printStackTrace();
			} catch (IOException e) {
				error = "internal error";
				flag = false;
				e.printStackTrace();
			} catch (Exception e) {
				flag = false;
				error = "internal error";
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (flag) {
				adapter = new ArrayAdapter<String>(RegisterActivity.this,
						R.drawable.spinner_dropdown_textview, ward);
				wardSpinner.setAdapter(adapter);
				wardsSpinnerLabel.setVisibility(View.VISIBLE);
				wardSpinner.setVisibility(View.VISIBLE);
			} else {
				nurseRadioButton.setChecked(false);
				Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onPreExecute() {
			ward = new ArrayList<String>();
			pd.setMessage("fetching ward list");
			pd.show();
		}

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(RegisterActivity.this,
				SplashScreenActivity.class);
		finish();
		startActivity(intent);
	}

	private class Register extends AsyncTask<Void, Void, Void> {
		boolean flag;
		private String error = "Registration is incomplete";

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (nurseRadioButton.isChecked()) {
					flag = application.register(firstNameEditText.getText()
							.toString(), lastNameEditText.getText().toString(),
							middleNameEditText.getText().toString(), "NURSE",
							wards.get(wardSpinner.getSelectedItemPosition())
									.getId(), passwordEditText.getText()
									.toString());

				} else {
					flag = application.register(firstNameEditText.getText()
							.toString(), lastNameEditText.getText().toString(),
							middleNameEditText.getText().toString(),
							"PHYSICIAN", 0, passwordEditText.getText()
									.toString());
				}
				Log.d("registered", flag + "");
			} catch (ClientProtocolException e) {
				error = "connection error";
				flag = false;
				e.printStackTrace();
			} catch (JSONException e) {
				error = "invalid server respond";
				flag = false;
				e.printStackTrace();
			} catch (IOException e) {
				error = "internal error";
				flag = false;
				e.printStackTrace();
			} catch (Exception e) {
				error = "internal error";
				flag = false;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (flag) {
				Toast.makeText(RegisterActivity.this, "Registration complete",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RegisterActivity.this,
						SplashScreenActivity.class);
				finish();
				startActivity(intent);
			} else {
				Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT)
						.show();
				passwordEditText.setText("");
				confirmPasswordEditText.setText("");
			}
		}

		@Override
		protected void onPreExecute() {
			pd.setMessage("Registering...");
			pd.show();
		}
	}
}

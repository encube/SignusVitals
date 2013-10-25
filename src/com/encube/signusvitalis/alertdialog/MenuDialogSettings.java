package com.encube.signusvitalis.alertdialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.exception.IncorrectOldPassword;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MenuDialogSettings extends AlertDialog {
	private ProgressDialog pd;
	private SignusVitalisApplication application;
	private String currentPassword;
	private String newPassword;
	private Context context;

	public MenuDialogSettings(Context context,
			final SignusVitalisApplication application) {
		super(context);
		pd = new ProgressDialog(context);
		this.application = application;
		this.context = context;
		View view = getLayoutInflater().inflate(R.layout.menu_diaog_settings,
				null);
		setView(view);
		final ToggleButton notificationSwitch = (ToggleButton) view
				.findViewById(R.id.notification_switch);
		final EditText currentPasswordET = (EditText) view
				.findViewById(R.id.current_password);
		final EditText newPasswordET = (EditText) view
				.findViewById(R.id.new_password);
		final EditText confirmPasswordET = (EditText) view
				.findViewById(R.id.confirm_new_password);
		Button saveButton = (Button) view
				.findViewById(R.id.button_settings_save);
		notificationSwitch.setChecked(application.isNotification());
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				application.setNotificationActivated(notificationSwitch
						.isChecked());
				if (!currentPasswordET.getText().toString().equals("")
						&& (newPasswordET.getText().toString()
								.equals(confirmPasswordET.getText().toString()))) {
					currentPassword = currentPasswordET.getText().toString();
					newPassword = newPasswordET.getText().toString();
					new changePassAsyncTask().execute();
				} else
					dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private class changePassAsyncTask extends AsyncTask<Void, Void, Boolean> {
		private String error = "fail to update password";

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				return application.changePassword(currentPassword, newPassword);
			} catch (UnsupportedEncodingException e) {
				error = "server error";
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				error = "server error";
				e.printStackTrace();
			} catch (IOException e) {
				error = "server error";
				e.printStackTrace();
			} catch (JSONException e) {
				error = "server error";
				e.printStackTrace();
			} catch (IncorrectOldPassword e) {
				error = e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				error = "internal error";
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (!result)
				Toast.makeText(context, error, Toast.LENGTH_LONG).show();
			else {
				dismiss();
				Toast.makeText(context, "updating password succesful",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPreExecute() {
			pd.setMessage("updating password...");
			pd.show();
		}
	}
}

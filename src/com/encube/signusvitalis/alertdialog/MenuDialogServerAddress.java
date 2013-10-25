package com.encube.signusvitalis.alertdialog;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.application.SignusVitalisApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MenuDialogServerAddress extends AlertDialog {

	public MenuDialogServerAddress(Context context,
			final SignusVitalisApplication application) {
		super(context);
		View view = getLayoutInflater().inflate(
				R.layout.menu_dialog_server_address, null);
		setView(view);
		final EditText et = (EditText) view.findViewById(R.id.server_address);
		Button button = (Button) view.findViewById(R.id.save_server_address);
		et.setText(application.getServerAddress());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				application.setServerAddress(et.getText().toString());
				dismiss();
			}
		});
	}

}

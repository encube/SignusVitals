package com.encube.signusvitalis.alertdialog;

import com.encube.signusvitalis.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class MenuDialogHelp extends AlertDialog {

	public MenuDialogHelp(Context context) {
		super(context);
		View view = getLayoutInflater()
				.inflate(R.layout.menu_dialog_help, null);
		setView(view);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}

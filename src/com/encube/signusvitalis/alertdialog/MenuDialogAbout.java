package com.encube.signusvitalis.alertdialog;

import com.encube.signusvitalis.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class MenuDialogAbout extends AlertDialog {

	public MenuDialogAbout(Context context) {
		super(context);
		View view = getLayoutInflater().inflate(R.layout.menu_dialog_about,
				null);
		setView(view);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}

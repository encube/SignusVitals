package com.encube.signusvitalis.alertdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.activities.VitalsActivity;
import com.encube.signusvitalis.domain.Case;

public class MenuDialogPatientInformation extends AlertDialog {
	private Case aCase;

	public MenuDialogPatientInformation(final Context context, Case case_) {
		super(context);
		this.aCase = case_;
		View cus_menu = getLayoutInflater().inflate(
				R.layout.menu_dialog_patient_information, null);
		setView(cus_menu);
		Button vitalsButton = (Button) cus_menu.findViewById(R.id.vs_button);
		TextView patientName = (TextView) cus_menu
				.findViewById(R.id.patient_value);
		patientName.setText(aCase.getName());
		TextView bed = (TextView) cus_menu.findViewById(R.id.bed_value);
		bed.setText(aCase.getBedNumber() + "");
		TextView dateAdmitted = (TextView) cus_menu
				.findViewById(R.id.date_admitted_value);
		dateAdmitted.setText(aCase.getDate());
		TextView diagnosis = (TextView) cus_menu
				.findViewById(R.id.diagnosis_value);
		diagnosis.setText(aCase.getDiagnosis());

		vitalsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, VitalsActivity.class);
				intent.putExtra("name", aCase.getName());
				intent.putExtra("bed", aCase.getBedNumber());
				intent.putExtra("date", aCase.getDate());
				intent.putExtra("diagnosis", aCase.getDiagnosis());
				context.startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}

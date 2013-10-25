package com.encube.signusvitalis.alertdialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.activities.VitalsActivity;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.ErrorCodes;
import com.encube.signusvitalis.domain.VSRecord;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MenuDialogAddEditRecord extends AlertDialog {
	private ProgressDialog pd;
	private final EditText rR;
	private final EditText pR;
	private final EditText tR;
	private final EditText bP;
	private final String caseId;
	private final Context context;
	private VSRecord record;
	private SignusVitalisApplication application;
	private boolean add;

	public MenuDialogAddEditRecord(Context context, String caseId,
			VSRecord record, SignusVitalisApplication application) {
		super(context);
		View view = getLayoutInflater().inflate(
				R.layout.menu_dialog_record_add_edit, null);
		setView(view);
		rR = (EditText) view.findViewById(R.id.RR_edit_text);
		pR = (EditText) view.findViewById(R.id.PR_edit_text);
		tR = (EditText) view.findViewById(R.id.TR_edit_text);
		bP = (EditText) view.findViewById(R.id.BP_edit_text);

		this.context = context;
		this.application = application;
		this.caseId = caseId;
		this.record = record;
		add = true;
		Button recordButton = (Button) view.findViewById(R.id.record_button);

		if (this.record != null) {
			// EDIT
			rR.setText(record.getRespiratation() + "");
			tR.setText(record.getTemperature() + "");
			pR.setText(record.getPulse() + "");
			bP.setText(record.getBlood() + "");
			add = false;
		}

		rR.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!rR.getText().toString().equals("")) {
					if ((Double.parseDouble(rR.getText().toString()) < 61)
							&& (Double.parseDouble(rR.getText().toString()) > 19)) {
						rR.setTextColor(Color.parseColor("#FF343434"));
					} else
						rR.setTextColor(Color.parseColor("#FFCC0000"));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});

		tR.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!tR.getText().toString().equals("")) {
					if ((Double.parseDouble(tR.getText().toString()) < 43)
							&& (Double.parseDouble(tR.getText().toString()) > 30)) {
						tR.setTextColor(Color.parseColor("#FF343434"));
					} else
						tR.setTextColor(Color.parseColor("#FFCC0000"));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});

		pR.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!pR.getText().toString().equals("")) {
					if ((Double.parseDouble(pR.getText().toString()) < 181)
							&& (Double.parseDouble(pR.getText().toString()) > 59)) {
						pR.setTextColor(Color.parseColor("#FF343434"));
					} else
						pR.setTextColor(Color.parseColor("#FFCC0000"));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});

		recordButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Pattern pattern = Pattern
						.compile("^([0-9]{1,3})//([0-9]{1,3})$");
				Matcher matcher = pattern.matcher(bP.getText().toString());
				if (rR.getText().toString().equals("")
						|| tR.getText().toString().equals("")
						|| pR.getText().toString().equals("")
						|| bP.getText().toString().equals("")) {
					Toast.makeText(MenuDialogAddEditRecord.this.context,
							"Incomplete Form", Toast.LENGTH_SHORT).show();

				} else {
					double tRValue = 0;
					double pRValue = 0;
					double rRValue = 0;
					boolean tROnRange;
					boolean pROnRange;
					boolean rROnRange;
					boolean bPValid;
					boolean valid;
					try {
						tRValue = Double.parseDouble(tR.getText().toString());
						pRValue = Double.parseDouble(pR.getText().toString());
						rRValue = Double.parseDouble(rR.getText().toString());
						valid = true;
					} catch (NumberFormatException e) {
						valid = false;
					}
					tROnRange = (tRValue < 43 && tRValue > 30);
					pROnRange = (pRValue < 181 && pRValue > 59);
					rROnRange = (rRValue < 61 && rRValue > 19);
					bPValid = matcher.find();
					if (!valid && !bPValid) {
						Toast.makeText(MenuDialogAddEditRecord.this.context,
								"invalid inputs", Toast.LENGTH_SHORT).show();
					} else if (tROnRange && rROnRange && pROnRange) {
						new InsertTask().execute();
					} else {
						String text = "Value of ";
						if (!rROnRange)
							text += "Respiration ";
						if (!pROnRange)
							text += "Pulse ";
						if (!tROnRange)
							text += "Temperature ";
						text += "Out of Range.";
						Toast.makeText(MenuDialogAddEditRecord.this.context,
								text, Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private class InsertTask extends AsyncTask<Void, Void, Void> {

		private boolean flag;
		private String error;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (add) {
					flag = application.insertNewVSRecord(caseId, pR.getText()
							.toString(), rR.getText().toString(), tR.getText()
							.toString(), bP.getText().toString());
					error = "Cannot input New Case Record yet";
				} else {
					flag = application.editVSRecord(record.getId(), rR
							.getText().toString(), pR.getText().toString(), tR
							.getText().toString(), bP.getText().toString());
					error = "cannot edit the selected record";
				}
			} catch (UnsupportedEncodingException e) {
				error = ErrorCodes.UNSUPPORTED_ENCODING_EXCEPTION.toString();
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				error = ErrorCodes.CLIENT_PROTOCOL_EXCEPTION.toString();
				e.printStackTrace();
			} catch (IOException e) {
				error = ErrorCodes.IO_EXCEPTION.toString();
				e.printStackTrace();
			} catch (Exception e) {
				error = ErrorCodes.EXCEPTION.toString();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (flag) {
				((VitalsActivity) context).resumeTask();
				dismiss();
				Toast.makeText(context, "Successfully Recorded",
						Toast.LENGTH_SHORT).show();
			} else {
				((VitalsActivity) context).resumeTask();
				dismiss();
				Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			((VitalsActivity) context).pauseTask();
			pd = new ProgressDialog(context);
			pd.setCancelable(false);
			if (add) {
				pd.setMessage("Inserting Record...");
			} else {
				pd.setMessage("Editting Record...");
			}
			pd.show();
		}

	}

}

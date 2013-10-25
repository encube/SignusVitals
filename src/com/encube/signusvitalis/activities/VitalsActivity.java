package com.encube.signusvitalis.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.adapters.RecordListAdapter;
import com.encube.signusvitalis.alertdialog.MenuDialogAddEditRecord;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.VSRecord;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VitalsActivity extends Activity {

	private String caseId;
	private ListView vitalsList;
	private ImageView graphImage;
	private RecordListAdapter vsAdapter;
	private ArrayList<VSRecord> vsRecord;
	private TextView name;
	private TextView bed;
	private TextView date;
	private TextView diagnosis;
	private TextView label;
	private SignusVitalisApplication application;
	private LinearLayout topShadow;
	private LinearLayout bottomShadow;
	private boolean acitivityActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vitals);

		application = (SignusVitalisApplication) getApplication();

		caseId = getIntent().getStringExtra("caseId");

		vitalsList = (ListView) findViewById(R.id.vitals_entry_listview);
		label = (TextView) findViewById(R.id.vs_record_textview);
		graphImage = (ImageView) findViewById(R.id.graph_image);
		name = (TextView) findViewById(R.id.name_value);
		name.setText(getIntent().getStringExtra("name"));
		bed = (TextView) findViewById(R.id.bed_value);
		bed.setText(getIntent().getIntExtra("bed", 0) + "");
		date = (TextView) findViewById(R.id.date_admitted_value);
		date.setText(getIntent().getStringExtra("date"));
		diagnosis = (TextView) findViewById(R.id.diagnosis_value);
		diagnosis.setText(getIntent().getStringExtra("diagnosis"));
		topShadow = (LinearLayout) findViewById(R.id.top_shadow);
		bottomShadow = (LinearLayout) findViewById(R.id.bottom_shadow);
		topShadow.bringToFront();
		bottomShadow.bringToFront();
		graphImage.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(VitalsActivity.this,
						GraphActivity.class);

				intent.putExtra("record",
						(ArrayList<VSRecord>) vsRecord.clone());
				VitalsActivity.this.startActivity(intent);
			}
		});
		vsRecord = new ArrayList<VSRecord>();
		vsAdapter = new RecordListAdapter(this, vsRecord);
		vitalsList.setAdapter(vsAdapter);

		vitalsList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						if (application.getPosition().equals("NURSE")
								&& position == 0) {
							if (application.isOnDuty()) {
								acitivityActive = false;
								new MenuDialogAddEditRecord(
										VitalsActivity.this, caseId, null,
										application).show();
							} else {
								Toast.makeText(VitalsActivity.this,
										"you are currently off-duty",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							vsAdapter.setSelected(position);
						}
					}
				});
		label.setText("VITALS RECORD (fetching records)");
	}

	@Override
	protected void onPause() {
		acitivityActive = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		acitivityActive = true;
		new vitalsFetcher().execute();
		super.onResume();
	}

	public void resumeTask() {
		acitivityActive = true;
		new vitalsFetcher().execute();
	}

	public void pauseTask() {
		acitivityActive = false;
	}

	private class vitalsFetcher extends AsyncTask<Void, Void, Void> {
		private boolean flag;

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("active", acitivityActive + "");
			while (acitivityActive) {
				try {
					vsRecord = application.fetchCaseRecord(caseId);
					flag = true;
				} catch (UnsupportedEncodingException e) {
					flag = false;
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					flag = false;
					e.printStackTrace();
				} catch (IOException e) {
					flag = false;
					e.printStackTrace();
				} catch (JSONException e) {
					flag = false;
					e.printStackTrace();
				} catch (Exception e) {
					flag = false;
					e.printStackTrace();
				}
				publishProgress();
				SystemClock.sleep(5000);
			}
			Log.d("AT", "exits");
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			Log.d("Task", "running");
			if (flag) {
				vsAdapter.notifyDataSetChanged(vsRecord);
				label.setText("VITALS RECORD");
			} else {
				label.setText("VITALS RECORD (foul data - disconnected)");
			}
		}
	}
}

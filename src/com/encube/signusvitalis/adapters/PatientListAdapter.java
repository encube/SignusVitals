package com.encube.signusvitalis.adapters;

import java.util.ArrayList;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.activities.VitalsActivity;
import com.encube.signusvitalis.domain.Case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PatientListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Case> data;
	private ArrayList<Case> origData;
	private static LayoutInflater inflater = null;
	private int selected = -1;
	private boolean activateFadeIn;
	private CharSequence filterText = "";
	private String selectedRecordId;

	public PatientListAdapter(Activity a, ArrayList<Case> d) {
		activity = a;
		data = d;
		origData = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void filter(CharSequence text) {
		ArrayList<Case> filtered = new ArrayList<Case>();

		for (int i = 0; i < origData.size(); i++) {
			String name = origData.get(i).getName();
			if (name.toLowerCase().startsWith(text.toString().toLowerCase())) {
				filtered.add(origData.get(i));
			}
		}

		// set new (filterd) current list of items
		this.data = filtered;
		this.filterText = text;
		// notify ListView to Rebuild
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Animation animFadeIn = AnimationUtils.loadAnimation(activity,
				android.R.anim.fade_in);
		if (view == null)
			view = inflater.inflate(R.layout.row_patient_list, null);
		final Case case_ = data.get(position);
		LinearLayout buttons = (LinearLayout) view.findViewById(R.id.buttons);
		Button vsButton = (Button) view.findViewById(R.id.vs_button);
		TextView monitorIndicator = (TextView) view
				.findViewById(R.id.monitoring_indicator);
		TextView normalIndicator = (TextView) view
				.findViewById(R.id.assessment_indicator);
		TextView patientNameTextView = (TextView) view
				.findViewById(R.id.patient_name);
		TextView bedNumberTextView = (TextView) view
				.findViewById(R.id.bed_text_view);
		TextView dateTextView = (TextView) view
				.findViewById(R.id.date_text_view);
		TextView diagnosisTextView = (TextView) view
				.findViewById(R.id.diagnosis_text_view);

		if (case_.isMonitored()) {
			monitorIndicator.setText("MONITORED");
			monitorIndicator.setBackgroundResource(R.drawable.normal_bg);
		} else {
			monitorIndicator.setText("UNMONITORED");
			monitorIndicator.setBackgroundResource(R.drawable.critical_bg);
		}

		if (case_.isNormal()) {
			normalIndicator.setText("NORMAL");
			normalIndicator.setBackgroundResource(R.drawable.normal_bg);
		} else {
			normalIndicator.setText("ABNORMAL");
			normalIndicator.setBackgroundResource(R.drawable.critical_bg);
		}
		normalIndicator.invalidate();
		monitorIndicator.invalidate();

		if (selected == position) {
			if (activateFadeIn) {
				buttons.setAnimation(animFadeIn);
				diagnosisTextView.setAnimation(animFadeIn);
				dateTextView.setAnimation(animFadeIn);
			}
			buttons.setVisibility(View.VISIBLE);
			vsButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, VitalsActivity.class);
					intent.putExtra("caseId", case_.getCaseId());
					intent.putExtra("name", case_.getName());
					intent.putExtra("bed", case_.getBedNumber());
					intent.putExtra("date", case_.getDate());
					intent.putExtra("diagnosis", case_.getDiagnosis());
					activity.startActivity(intent);
				}
			});
			dateTextView.setText("Date admitted: " + case_.getDate());
			dateTextView.setVisibility(View.VISIBLE);
			diagnosisTextView.setText("Diagnosis: " + case_.getDiagnosis());
			diagnosisTextView.setVisibility(View.VISIBLE);
		} else {
			buttons.setVisibility(View.GONE);
			dateTextView.setVisibility(View.GONE);
			diagnosisTextView.setVisibility(View.GONE);
		}

		patientNameTextView.setText(case_.getName());
		bedNumberTextView.setText("Room: " + case_.getBedNumber());

		return view;
	}

	public void notifyDataSetChanged(ArrayList<Case> patients) {
		if (data.size() == 0 || patients.size() == 0 || selected == -1) {
			selected = -1;
			this.data = patients;
			this.origData = patients;
		} else {
			selectedRecordId = ((Case) getItem(this.selected)).getCaseId();
			this.data = patients;
			this.origData = patients;
			int index = 0;
			for (Case case_ : patients) {
				if (case_.getCaseId().equals(selectedRecordId)) {
					selected = index;
					break;
				}
				index++;
			}
		}
		activateFadeIn = false;
		if (!filterText.equals(""))
			filter(filterText);
		else
			notifyDataSetChanged();
	}

	public void setSelected(int selected) {
		activateFadeIn = true;
		this.selected = selected;
		notifyDataSetChanged();
	}
}

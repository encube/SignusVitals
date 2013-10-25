package com.encube.signusvitalis.activities;

import java.util.ArrayList;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.adapters.LogListAdapter;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.Ward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	private LogListAdapter adapter;
	private String name;
	private String status;
	private String position;
	private String shift;
	private String ward;
	private TextView label;
	private TextView nameTextView;
	private TextView statusTextView;
	private TextView positionTextView;
	private TextView wardTextView;
	private ListView logListView;
	private TextView shiftTextView;
	private LinearLayout topShadow;
	private LinearLayout bottomShadow;
	private LinearLayout shiftDivider;
	private LinearLayout statusIndicator;
	private LinearLayout roleDivider;
	private LinearLayout monitoredWardDivider;
	private Spinner monitoredWardButton;
	private LinearLayout shiftTextComponent;
	private SignusVitalisApplication application;
	private ArrayAdapter<String> monitoredWardAdapter;
	private ArrayList<String> wards;

	public static final String KEY_PATIENT = "patient";
	public static final String KEY_NURSE = "nurse";

	private IntentFilter filter = new IntentFilter("SERVICE_CYCLE_DONE");
	private BroadcastReceiver serviceSignalReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		application = (SignusVitalisApplication) getActivity().getApplication();
		name = application.getName();
		ward = application.getWard();
		position = application.getPosition();
		shift = application.getShift();
		if (application.isOnDuty()) {
			status = "On-Duty";
		} else {
			status = "Off-Duty";
		}

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		nameTextView = (TextView) view.findViewById(R.id.name);
		statusTextView = (TextView) view.findViewById(R.id.status);
		positionTextView = (TextView) view.findViewById(R.id.position);
		wardTextView = (TextView) view.findViewById(R.id.ward);
		logListView = (ListView) view.findViewById(R.id.logs_list_view);
		shiftTextView = (TextView) view.findViewById(R.id.time_shift);
		topShadow = (LinearLayout) view.findViewById(R.id.top_shadow);
		bottomShadow = (LinearLayout) view.findViewById(R.id.bottom_shadow);
		shiftDivider = (LinearLayout) view.findViewById(R.id.shift_divider);
		statusIndicator = (LinearLayout) view.findViewById(R.id.statusIndi);
		roleDivider = (LinearLayout) view.findViewById(R.id.role_divider);
		monitoredWardDivider = (LinearLayout) view
				.findViewById(R.id.ward_monitored_divider);
		monitoredWardButton = (Spinner) view
				.findViewById(R.id.ward_monitored_button);
		shiftTextComponent = (LinearLayout) view
				.findViewById(R.id.shift_text_component);
		label = (TextView) view.findViewById(R.id.label);

		if (!application.getPosition().equals("NURSE")) {
			wards = new ArrayList<String>();
			wards.add("all wards");
			for (Ward ward : application.getWards()) {
				wards.add(ward.getName());
			}

			monitoredWardAdapter = new ArrayAdapter<String>(
					HomeFragment.this.getActivity(),
					R.drawable.spinner_dropdown_textview, wards);
			monitoredWardButton.setAdapter(monitoredWardAdapter);

			wardTextView.setVisibility(View.INVISIBLE);
			shiftTextComponent.setVisibility(View.GONE);
			shiftDivider.setVisibility(View.GONE);
			shiftTextView.setVisibility(View.GONE);
			statusIndicator.setVisibility(View.GONE);
			roleDivider.setVisibility(View.GONE);
			monitoredWardButton
					.setSelection(application.getMonitoredWard() + 1);

		} else {
			monitoredWardDivider.setVisibility(View.GONE);
			monitoredWardButton.setVisibility(View.GONE);
		}

		wardTextView.setText(ward);
		nameTextView.setText(name);
		statusTextView.setText(status);
		shiftTextView.setText(shift);
		positionTextView.setText(position);
		topShadow.bringToFront();
		bottomShadow.bringToFront();

		adapter = new LogListAdapter(getActivity(), application.getLogs());
		logListView.setAdapter(adapter);
		logListView.setItemsCanFocus(false);
		logListView.setClickable(false);
		monitoredWardButton
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						application.setMonitoredWard(arg2 - 1);
						Log.d("test",
								"_______________________ "
										+ application.getMonitoredWard());
						adapter.notifyDataSetChanged(application.getLogs());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		serviceSignalReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("receiver", "updating ui");
				if (application.isFeedFetchingError()) {
					label.setText("ACTIVITY FEED (foul data - disconnected)");
				} else {
					Log.d("receiver", "connected");
					name = application.getName();
					ward = application.getWard();
					position = application.getPosition();
					shift = application.getShift();
					if (application.isOnDuty()) {
						status = "On-Duty";
					} else {
						status = "Off-Duty";
					}
					wardTextView.setText(ward);
					nameTextView.setText(name);
					statusTextView.setText(status);
					shiftTextView.setText(shift);
					positionTextView.setText(position);
					statusTextView.setText(status);
					label.setText("ACTIVITY FEED");
					adapter.notifyDataSetChanged(application.getLogs());
				}
			}
		};
		return view;
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(serviceSignalReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		getActivity().registerReceiver(serviceSignalReceiver, filter);
		super.onResume();
	}
}

package com.encube.signusvitalis.activities;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.adapters.PatientListAdapter;
import com.encube.signusvitalis.application.SignusVitalisApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PatientListFragment extends Fragment {

	private ListView listView;
	private PatientListAdapter patientaListAdapter;
	private EditText searchBox;
	private TextView label;
	public static final String KEY_PATIENT_NAME = "patient name";
	public static final String KEY_BED_NUMBER = "bed number";
	public static final String KEY_PATIENT_ID = "patient id";
	private SignusVitalisApplication application;

	private IntentFilter filter = new IntentFilter("SERVICE_CYCLE_DONE");
	private BroadcastReceiver serviceSignalReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		application = (SignusVitalisApplication) getActivity().getApplication();

		View view = inflater.inflate(R.layout.fragment_patient_list, container,
				false);

		listView = (ListView) view.findViewById(R.id.listView);
		label = (TextView) view.findViewById(R.id.label);
		searchBox = (EditText) view.findViewById(R.id.patient_list_search);
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int arg1, int arg2,
					int arg3) {
				PatientListFragment.this.patientaListAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		patientaListAdapter = new PatientListAdapter(getActivity(),
				application.getPatients());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				patientaListAdapter.setSelected(arg2);
			}
		});
		listView.setAdapter(patientaListAdapter);
		serviceSignalReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("receiver", "updating ui");
				if (application.isPatientListFetchingError()) {
					label.setText("PATIENT LIST (foul data - disconnected)");
				} else {
					label.setText("PATIENT LIST");
					patientaListAdapter.notifyDataSetChanged(application
							.getPatients());
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

	public void showErrorToast() {
		switch (application.getError()) {
		case CLIENT_PROTOCOL_EXCEPTION:
			Toast.makeText(getActivity(), "cannot reach server",
					Toast.LENGTH_SHORT).show();
			break;
		case UNSUPPORTED_ENCODING_EXCEPTION:
			Toast.makeText(getActivity(), "server error", Toast.LENGTH_SHORT)
					.show();
			break;
		case IO_EXCEPTION:
			Toast.makeText(getActivity(), "no network access",
					Toast.LENGTH_SHORT).show();
			break;
		case JSON_EXCEPTION:
			Toast.makeText(getActivity(), "server error", Toast.LENGTH_SHORT)
					.show();
			break;
		case EXCEPTION:
			Toast.makeText(getActivity(), "unknown error", Toast.LENGTH_SHORT)
					.show();
			break;
		case NONE:
			break;
		default:
			break;
		}
	}
}

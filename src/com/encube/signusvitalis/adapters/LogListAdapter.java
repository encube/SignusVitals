package com.encube.signusvitalis.adapters;

import java.util.ArrayList;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.domain.Log;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Log> data;
	private static LayoutInflater inflater = null;

	public LogListAdapter(Activity a, ArrayList<Log> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null)
			vi = inflater.inflate(R.layout.row_log_list, null);

		TextView logTv = (TextView) vi.findViewById(R.id.log);
		TextView timeTv = (TextView) vi.findViewById(R.id.time);

		Log log = data.get(position);

		logTv.setText(log.getText());
		timeTv.setText(log.getTime());
		return vi;
	}

	public void notifyDataSetChanged(ArrayList<Log> data) {
		this.data = data;
		notifyDataSetChanged();
	}
}

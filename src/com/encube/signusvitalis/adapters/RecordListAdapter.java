package com.encube.signusvitalis.adapters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.activities.VitalsActivity;
import com.encube.signusvitalis.alertdialog.MenuDialogAddEditRecord;
import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.VSRecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<VSRecord> data;
	private static LayoutInflater inflater = null;
	private int selected = -1;
	private SignusVitalisApplication application;
	private int adjuster;
	private boolean activateFadeIn;
	private ProgressDialog pd;
	private String selectedRecordId;

	@SuppressWarnings("static-access")
	public RecordListAdapter(Activity activity, ArrayList<VSRecord> data) {
		this.activity = activity;
		this.data = data;
		activateFadeIn = true;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.application = (SignusVitalisApplication) activity.getApplication();
		if (application.getPosition().equals("NURSE")) {
			adjuster = 1;
		} else {
			adjuster = 0;
		}
	}

	@Override
	public int getCount() {
		return data.size() + adjuster;
	}

	@Override
	public Object getItem(int arg0) {
		if (arg0 - adjuster == -1) {
			return null;
		}
		return data.get(arg0 - adjuster);
	}

	@Override
	public long getItemId(int arg0) {
		if (arg0 == 0) {
			return 0;
		}
		return arg0 - adjuster;
	}

	@Override
	public View getView(final int position, View ConvertView, ViewGroup parent) {
		View view = ConvertView;
		Animation animFadeIn = AnimationUtils.loadAnimation(activity,
				android.R.anim.fade_in);
		if (view == null)
			view = inflater.inflate(R.layout.row_record_list, null);
		TextView date = (TextView) view.findViewById(R.id.record_date_textview);
		ImageView lockImage = (ImageView) view.findViewById(R.id.lock_img);

		LinearLayout recordInfo = (LinearLayout) view
				.findViewById(R.id.record_info);
		TextView rR = (TextView) view.findViewById(R.id.RR);
		TextView pR = (TextView) view.findViewById(R.id.PR);
		TextView tR = (TextView) view.findViewById(R.id.TR);
		TextView bP = (TextView) view.findViewById(R.id.BP);
		TextView nurseName = (TextView) view.findViewById(R.id.nurseName);
		LinearLayout buttons = (LinearLayout) view.findViewById(R.id.buttons);
		Button edit = (Button) view.findViewById(R.id.edit_button);
		Button lock = (Button) view.findViewById(R.id.lock_button);
		pd = new ProgressDialog(activity);

		if (position == 0 && adjuster == 1) {
			recordInfo.setVisibility(View.GONE);
			buttons.setVisibility(View.GONE);
			date.setText("Add new...");
			lockImage.setImageDrawable(activity.getResources().getDrawable(
					R.drawable.cross));
		} else {
			final VSRecord record = data.get(position - adjuster);
			if (selected == position) {
				if (activateFadeIn)
					recordInfo.setAnimation(animFadeIn);
				rR.setText("Respiration: " + record.getRespiratation() + " cpm");
				pR.setText("Pulse: " + record.getPulse() + " bpm");
				tR.setText("Temperature: " + record.getTemperature() + " C");
				bP.setText("Blood Pressure: " + record.getBlood() + " mmHg");
				nurseName.setText("Nurse: " + record.getNurseName());
				recordInfo.setVisibility(View.VISIBLE);
			} else {
				recordInfo.setVisibility(View.GONE);
			}

			if (data.get(position - adjuster).isLocked()) {
				lockImage.setImageDrawable(activity.getResources().getDrawable(
						R.drawable.locked));
				buttons.setVisibility(View.GONE);
			} else {
				lockImage.setImageDrawable(activity.getResources().getDrawable(
						R.drawable.unlocked));
				if (selected == position
						&& data.get(position - adjuster).isUserEditable()
						&& application.getPosition().equals("NURSE")) {
					if (activateFadeIn)
						buttons.setAnimation(animFadeIn);
					buttons.setVisibility(View.VISIBLE);
				} else {
					buttons.setVisibility(View.GONE);
				}
			}
			date.setText(record.getDate());
			edit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new MenuDialogAddEditRecord(activity, record.getId(), data
							.get(position - adjuster), application).show();

				}
			});
			lock.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new lockTask().execute(record);
				}
			});
		}
		return view;
	}

	public void notifyDataSetChanged(ArrayList<VSRecord> records) {
		if (data.size() == 0 || records.size() == 0 || selected == -1) {
			selected = -1;
			this.data = records;
		} else {
			Log.d("selected", "" + this.selected);
			selectedRecordId = ((VSRecord) getItem(this.selected)).getId();
			this.data = records;
			int index = 0;
			for (VSRecord record : records) {
				if (record.getId().equals(selectedRecordId)) {
					selected = index + adjuster;
					break;
				}
				index++;
			}
		}
		activateFadeIn = false;
		notifyDataSetChanged();
	}

	public void setSelected(int selected) {
		activateFadeIn = true;
		this.selected = selected;
		notifyDataSetChanged();
	}

	private class lockTask extends AsyncTask<VSRecord, Void, Boolean> {

		private String error = "Unable to lock selected record";

		@Override
		protected Boolean doInBackground(VSRecord... params) {
			boolean lock = false;
			try {
				lock = application.lockVSRecord(params[0].getId());
			} catch (UnsupportedEncodingException e) {
				lock = false;
				error = "Server encoding problem";
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				lock = false;
				error = "Server unreachable";
				e.printStackTrace();
			} catch (IOException e) {
				lock = false;
				error = "Connection error";
				e.printStackTrace();
			} catch (JSONException e) {
				lock = false;
				error = "Invalid response from Server";
				e.printStackTrace();
			} catch (Exception e) {
				lock = false;
				error = "unknown error";
				e.printStackTrace();
			}
			return lock;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			((VitalsActivity) activity).resumeTask();
			pd.dismiss();
			if (result) {
				Toast.makeText(activity, "successfully locked",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPreExecute() {
			((VitalsActivity) activity).pauseTask();
			pd.setMessage("locking selected record...");
			pd.show();
		}

	}
}

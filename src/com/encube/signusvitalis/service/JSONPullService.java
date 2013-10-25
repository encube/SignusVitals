package com.encube.signusvitalis.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.encube.signusvitalis.application.SignusVitalisApplication;
import com.encube.signusvitalis.domain.Case;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class JSONPullService extends IntentService {
	private SignusVitalisApplication application;
	private ArrayList<String> unmonitoredCases;
	private ArrayList<String> temporary;
	private boolean issueSound;
	private int count;

	public JSONPullService() {
		super("JSONPullService");
		unmonitoredCases = new ArrayList<String>();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		application = (SignusVitalisApplication) getApplication();
		while (application.isLogIn()) {
			if (application.getPosition().equals("NURSE")) {
				try {
					application.fetchUserInformation();
					application.setUserInformationFetchingError(false);
				} catch (UnsupportedEncodingException e) {
					application.setUserInformationFetchingError(true);
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					application.setUserInformationFetchingError(true);
					e.printStackTrace();
				} catch (IOException e) {
					application.setUserInformationFetchingError(true);
					e.printStackTrace();
				} catch (JSONException e) {
					application.setUserInformationFetchingError(true);
					e.printStackTrace();
				} catch (Exception e) {
					application.setUserInformationFetchingError(true);
					e.printStackTrace();
				}
			}
			try {
				application.fetchPatients();
				application.setPatientListFetchingError(false);
			} catch (UnsupportedEncodingException e) {
				application.setPatientListFetchingError(true);
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				application.setPatientListFetchingError(true);
				e.printStackTrace();
			} catch (IOException e) {
				application.setPatientListFetchingError(true);
				e.printStackTrace();
			} catch (JSONException e) {
				application.setPatientListFetchingError(true);
				e.printStackTrace();
			} catch (Exception e) {
				application.setPatientListFetchingError(true);
				e.printStackTrace();
			}
			try {
				application.fetchFeed();
				application.setFeedFetchingError(false);
			} catch (UnsupportedEncodingException e) {
				application.setFeedFetchingError(true);
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				application.setFeedFetchingError(true);
				e.printStackTrace();
			} catch (IOException e) {
				application.setFeedFetchingError(true);
				e.printStackTrace();
			} catch (JSONException e) {
				application.setFeedFetchingError(true);
				e.printStackTrace();
			} catch (Exception e) {
				application.setFeedFetchingError(true);
				e.printStackTrace();
			}
			if (application.isNotification()) {
				issueSound = false;
				count = 0;
				temporary = new ArrayList<String>();
				for (Case case_ : application.getPatients()) {
					if (!case_.isMonitored()) {
						if (!unmonitoredCases.contains(case_.getName()
								+ " on bed " + case_.getBedNumber())) {
							issueSound = true;
						}
						count++;
						temporary.add(case_.getName() + " on bed "
								+ case_.getBedNumber());
					}
				}
				unmonitoredCases = (ArrayList<String>) temporary.clone();
				application.raiseNotification(count, unmonitoredCases,
						issueSound);
			}
			Log.d("service", "cycle");
			sendBroadcast(new Intent("SERVICE_CYCLE_DONE"));
			SystemClock.sleep(100);
		}
		Log.d("service", "stopped");
		application.cancelNotification();
		stopSelf();
	}
}

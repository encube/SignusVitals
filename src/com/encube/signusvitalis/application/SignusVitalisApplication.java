package com.encube.signusvitalis.application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.activities.MainFragmentActivity;
import com.encube.signusvitalis.domain.Case;
import com.encube.signusvitalis.domain.ErrorCodes;
import com.encube.signusvitalis.domain.Log;
import com.encube.signusvitalis.domain.VSRecord;
import com.encube.signusvitalis.domain.Ward;
import com.encube.signusvitalis.exception.IncorrectOldPassword;
import com.encube.signusvitalis.serverconnectionmodule.JSONRetreiver;
import com.encube.signusvitalis.serverconnectionmodule.LogoutRequest;
import com.encube.signusvitalis.service.JSONPullService;

public class SignusVitalisApplication extends Application {

	private static final String PATIENT_LIST_URL = "/VSRS/Server/patients.php?m&q=&t=";
	private static final String LOGIN_URL = "/VSRS/Server/session.php?m&login";
	private static final String LOGOUT_URL = "/VSRS/Server/session.php?m&logout";
	private static final String WARD_LIST_URL = "/VSRS/Server/info.php?wardlist";
	private static final String REGISTER_URL = "/VSRS/Server/register.php";
	private static final String USER_INFO_URL = "/VSRS/Server/session.php?m&info&s=";
	private static final String INSERT_RECORD_URL = "/VSRS/Server/vitals.php?insert";
	private static final String FEED_LIST_URL = "/VSRS/Server/feed.php?m&s=";
	private static final String VITALS_LIST_URL = "/VSRS/Server/vitals.php?list&c=";
	private static final String LOCK_RECORD_URL = "/VSRS/Server/vitals.php?edit&lock";
	private static final String EDIT_RECORD_URL = "/VSRS/Server/vitals.php?edit";
	private static final String CHANGE_PASSWORD_URL = "/VSRS/Server/password.php?update";

	private String serverAdress = "http://192.168.1.33";

	public static final String SESSION_ID = "sID";
	public static final String USERNAME = "u";
	public static final String PASSWORD = "p";
	public static final String TIME = "t";
	public static final String SESSION = "s";
	public static final String INFORMATION_ID = "case_id";
	public static final String INFORMATION_NAME = "patient_name";
	public static final String INFORMATION_DIAGNOSIS = "diagnosis";
	public static final String INFORMATION_BED_NUMBER = "bed_number";
	public static final String INFORMATION_DATE = "admitDate";
	public static final String INFORMATION_MONITOR = "hasMonitored";
	public static final String INFORMATION_NORMAL = "isAbnormal";
	public static final String USER_INFO_NAME = "name";
	public static final String USER_INFO_SHIFT = "shift";
	public static final String USER_INFO_WARD = "ward";
	public static final String USER_INFO_ON_DUTY = "on-duty";
	public static final String USER_INFO_POSITION = "position";
	public static final String WARD_ID = "wardID";
	public static final String WARD_NAME = "wardName";
	public static final String RESPONSE = "regStatus";
	public static final int NOTIFICATION_ID = 1111;

	private String sessionID;
	private String name;
	private boolean onDuty;
	private String position;
	private String ward;
	private String shift;
	private int monitoredWardIndex;
	private ArrayList<Case> cases;
	private ArrayList<Ward> wards;
	private ArrayList<Log> logs;
	private ErrorCodes error;
	private boolean notificationActivated;
	private boolean logIn;
	private boolean userInformationFetchingError;
	private boolean patientListFetchingError;
	private boolean feedFetchingError;

	@Override
	public void onCreate() {
		super.onCreate();
		this.userInformationFetchingError = false;
		this.patientListFetchingError = false;
		this.feedFetchingError = false;
		this.sessionID = "none";
		this.name = "none";
		this.ward = "none";
		this.position = "none";
		this.shift = "none";
		this.onDuty = false;
		this.error = ErrorCodes.NONE;
		this.cases = new ArrayList<Case>();
		this.logs = new ArrayList<Log>();
		this.wards = new ArrayList<Ward>();
		monitoredWardIndex = -1;
		this.notificationActivated = true;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public ErrorCodes getError() {
		return error;
	}

	public void setError(ErrorCodes error) {
		this.error = error;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Case> getPatients() {
		ArrayList<Case> temp = new ArrayList<Case>();
		if (getMonitoredWard() != -1) {
			for (Case cse : cases) {
				if (cse.getWardId() == getWards().get(getMonitoredWard())
						.getId()) {
					temp.add(cse);
				}
			}
			return temp;
		} else {
			return (ArrayList<Case>) cases.clone();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Ward> getWards() {
		return (ArrayList<Ward>) wards.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Log> getLogs() {
		ArrayList<Log> temp = new ArrayList<Log>();
		if (getMonitoredWard() != -1) {
			for (Log lg : logs) {
				if (lg.getWardId() == getWards().get(getMonitoredWard())
						.getId()) {
					temp.add(lg);
				}
			}
			return temp;
		} else {
			return (ArrayList<Log>) logs.clone();
		}
	}

	public int getMonitoredWard() {
		return this.monitoredWardIndex;
	}

	public void setMonitoredWard(int i) {
		this.monitoredWardIndex = i;
	}

	@SuppressWarnings("unchecked")
	public void fetchPatients() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, JSONException, Exception {
		ArrayList<Case> temporaryCases = new ArrayList<Case>();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(SESSION, getSessionID()));
		JSONArray jSONPatients = new JSONRetreiver().getJSONArrayFromPostUrl(
				serverAdress + PATIENT_LIST_URL, list);

		for (int i = 0; i < jSONPatients.length(); i++) {
			JSONObject jObj = jSONPatients.getJSONObject(i);

			Case patient = new Case(jObj.getString(INFORMATION_ID),
					jObj.getString(INFORMATION_NAME),
					jObj.getString(INFORMATION_DIAGNOSIS),
					jObj.getString(INFORMATION_DATE),
					jObj.getInt(INFORMATION_BED_NUMBER),
					jObj.getBoolean(INFORMATION_MONITOR),
					!jObj.getBoolean(INFORMATION_NORMAL),
					jObj.getInt("ward_id"));
			temporaryCases.add(patient);
		}
		cases = (ArrayList<Case>) temporaryCases.clone();
	}

	@SuppressLint("SimpleDateFormat")
	public boolean login(String username, String password)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, JSONException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		String date = format.format(new Date());
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(USERNAME, username));
		list.add(new BasicNameValuePair(PASSWORD, password));
		list.add(new BasicNameValuePair(TIME, date));

		JSONObject jSONUser = new JSONRetreiver().getJSONObjectFromPostUrl(
				serverAdress + LOGIN_URL, list);

		if (jSONUser.getBoolean("isAuth")) {
			setSessionID(jSONUser.getString(SESSION_ID));
			this.logIn = true;
			startNotifierService();
			return true;
		} else {
			return false;
		}
	}

	public void logout() throws ClientProtocolException, IOException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(SESSION, getSessionID()));
		new LogoutRequest().getJSONFromUrl(serverAdress + LOGOUT_URL, list);
		name = "none";
		position = "none";
		shift = "none";
		ward = "none";
		onDuty = false;
		logs = new ArrayList<Log>();
		cases = new ArrayList<Case>();
		wards = new ArrayList<Ward>();
		setSessionID("none");
	}

	public boolean register(String firstName, String lastName,
			String middleName, String position, int ward, String password)
			throws JSONException, ClientProtocolException, IOException,
			Exception {
		JSONObject jObj = new JSONObject();
		jObj.put("first_name", firstName);
		jObj.put("last_name", lastName);
		jObj.put("middle_name", middleName);
		jObj.put("rank", position);
		jObj.put("ward", ward);
		jObj.put("password", password);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("data", jObj.toString()));
		JSONObject resultJObj = new JSONRetreiver().getJSONObjectFromPostUrl(
				serverAdress + REGISTER_URL, list);
		return Boolean.parseBoolean(resultJObj.getString(RESPONSE));
	}

	public void fetchUserInformation() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, JSONException, Exception {
		JSONObject jObj = new JSONRetreiver()
				.getJSONObjectFromGetUrl(serverAdress + USER_INFO_URL
						+ getSessionID());
		name = jObj.getString(USER_INFO_NAME);
		position = jObj.getString(USER_INFO_POSITION);
		shift = jObj.getString(USER_INFO_SHIFT);
		ward = jObj.getString(USER_INFO_WARD);
		onDuty = Boolean.valueOf(jObj.getString(USER_INFO_ON_DUTY)
				.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public void fetchWards() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, JSONException, Exception {
		ArrayList<Ward> temporaryWards = new ArrayList<Ward>();
		JSONArray jSONWards = new JSONRetreiver().getJSONArrayFromPostUrl(
				serverAdress + WARD_LIST_URL, new ArrayList<NameValuePair>());
		for (int i = 0; i < jSONWards.length(); i++) {
			JSONObject jObj = jSONWards.getJSONObject(i);
			temporaryWards.add(new Ward(jObj.getInt(WARD_ID), jObj
					.getString(WARD_NAME)));
		}
		wards = (ArrayList<Ward>) temporaryWards.clone();
	}

	@SuppressWarnings("unchecked")
	public void fetchFeed() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, JSONException, Exception {
		JSONArray jSONFeeds = new JSONRetreiver()
				.getJSONArrayFromGetUrl(serverAdress + FEED_LIST_URL
						+ sessionID);
		ArrayList<Log> temporaryLog = new ArrayList<Log>();
		for (int i = 0; i < jSONFeeds.length(); i++) {
			JSONObject jObj = jSONFeeds.getJSONObject(i);
			temporaryLog.add(new Log(jObj.getString("feedid"), jObj
					.getString("text"), jObj.getString("timestamp"), jObj
					.getInt("wardid")));
		}
		logs = (ArrayList<Log>) temporaryLog.clone();
	}

	public boolean insertNewVSRecord(String caseId, String pR, String rR,
			String tR, String bP) throws UnsupportedEncodingException,
			ClientProtocolException, IOException, Exception {
		JSONObject jObj = new JSONObject();
		jObj.put("session_id", sessionID);
		jObj.put("case_id", caseId);
		jObj.put("temp", tR);
		jObj.put("rr", rR);
		jObj.put("bp", bP);
		jObj.put("pr", pR);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("data", jObj.toString()));

		JSONObject resultJObject = new JSONRetreiver()
				.getJSONObjectFromPostUrl(serverAdress + INSERT_RECORD_URL,
						list);

		return resultJObject.getBoolean("submit");
	}

	public boolean lockVSRecord(String recordId)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, JSONException, Exception {
		JSONObject input = new JSONObject();
		input.put("session_id", sessionID);
		input.put("vitals_id", recordId);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("data", input.toString()));
		JSONObject jObj = new JSONRetreiver().getJSONObjectFromPostUrl(
				serverAdress + LOCK_RECORD_URL, list);
		return jObj.getBoolean("lock");
	}

	public boolean editVSRecord(String recordId, String rR, String pR,
			String tR, String bP) throws UnsupportedEncodingException,
			ClientProtocolException, IOException, Exception {
		JSONObject input = new JSONObject();
		input.put("session_id", sessionID);
		input.put("vitals_id", recordId);
		input.put("temp", tR);
		input.put("rr", rR);
		input.put("bp", bP);
		input.put("pr", pR);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("data", input.toString()));
		JSONObject jObj = new JSONRetreiver().getJSONObjectFromPostUrl(
				serverAdress + EDIT_RECORD_URL, list);
		return jObj.getBoolean("edit");
	}

	public ArrayList<VSRecord> fetchCaseRecord(String caseId)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, JSONException, Exception {
		JSONArray recordsJSON = new JSONRetreiver()
				.getJSONArrayFromGetUrl(serverAdress + VITALS_LIST_URL + caseId
						+ "&s=" + sessionID);
		ArrayList<VSRecord> records = new ArrayList<VSRecord>();
		JSONObject jObj;
		for (int i = 0; i < recordsJSON.length(); i++) {
			jObj = (JSONObject) recordsJSON.get(i);
			records.add(new VSRecord(jObj.getString("vitalsID"), jObj
					.getString("temp"), jObj.getString("pr"), jObj
					.getString("rr"), jObj.getString("bp"), jObj
					.getString("timestamp"), jObj.getBoolean("isLock"), jObj
					.getBoolean("isEdit"), jObj.getString("nurse_name")));
		}
		return records;
	}

	public boolean changePassword(String oldPassword, String newPassword)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, JSONException, IncorrectOldPassword, Exception {
		JSONObject input = new JSONObject();
		input.put("sessionid", sessionID);
		input.put("oldpassword", oldPassword);
		input.put("newpassword", newPassword);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("i", input.toString()));
		JSONObject output = new JSONRetreiver().getJSONObjectFromPostUrl(
				serverAdress + CHANGE_PASSWORD_URL, list);
		if (!output.getBoolean("isOldCorrect")) {
			throw new IncorrectOldPassword();
		}

		return output.getBoolean("success");
	}

	public void startNotifierService() {
		if (isLogIn()) {
			Intent intent = new Intent(this, JSONPullService.class);
			startService(intent);
		}
	}

	public boolean isNotification() {
		return notificationActivated;
	}

	public void raiseNotification(int number, ArrayList<String> unmonitored,
			boolean issueSound) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Signus Vitalis")
				.setContentText("patient monitoring").setAutoCancel(true)
				.setNumber(number);
		NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
		style.setBigContentTitle(number + " unmonitored patient(s)");
		for (String p : unmonitored) {
			style.addLine(p);
		}
		mBuilder.setStyle(style);
		if (issueSound) {
			Uri alarmSound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);
		}
		Intent resultIntent = new Intent(this, MainFragmentActivity.class);
		resultIntent.putExtra("requestFromNotification", true);
		TaskStackBuilder taskBuilder = TaskStackBuilder
				.create(getApplicationContext());
		taskBuilder.addParentStack(MainFragmentActivity.class);
		taskBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = taskBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public void cancelNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	public String getServerAddress() {
		return serverAdress;
	}

	public void setServerAddress(String serverAdress) {
		this.serverAdress = serverAdress;
	}

	public String getName() {
		return name;
	}

	public String getPosition() {
		return position;
	}

	public String getWard() {
		return ward;
	}

	public String getShift() {
		return shift;
	}

	public boolean isOnDuty() {
		return onDuty;
	}

	public boolean isLogIn() {
		return logIn;
	}

	public void setNotificationActivated(boolean notificationActivated) {
		if (!notificationActivated) {
			cancelNotification();
		}
		this.notificationActivated = notificationActivated;
	}

	public void setLogIn(boolean logIn) {
		this.logIn = logIn;
	}

	public void setUserInformationFetchingError(boolean isError) {
		this.userInformationFetchingError = isError;
	}

	public boolean isUserInformationFetchingError() {
		return userInformationFetchingError;
	}

	public void setPatientListFetchingError(boolean isError) {
		this.patientListFetchingError = isError;
	}

	public boolean isPatientListFetchingError() {
		return patientListFetchingError;
	}

	public void setFeedFetchingError(boolean isError) {
		this.feedFetchingError = isError;
	}

	public boolean isFeedFetchingError() {
		return feedFetchingError;
	}
}

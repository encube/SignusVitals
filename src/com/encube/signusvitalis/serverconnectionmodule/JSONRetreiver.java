package com.encube.signusvitalis.serverconnectionmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONRetreiver {

	public JSONRetreiver() {
	}

	public JSONObject getJSONObjectFromPostUrl(String url,
			List<NameValuePair> list) throws UnsupportedEncodingException,
			ClientProtocolException, IOException, Exception, JSONException {

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(list));
		Log.d("url", httpPost + "");
		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();
		Log.d("result: ", json);
		jObj = new JSONObject(json);

		return jObj;
	}

	public JSONObject getJSONObjectFromGetUrl(String url)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, Exception, JSONException {

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();
		Log.d("result: ", json);
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			throw e;
		}

		return jObj;
	}

	public JSONArray getJSONArrayFromPostUrl(String url,
			List<NameValuePair> list) throws UnsupportedEncodingException,
			ClientProtocolException, IOException, Exception, JSONException {

		InputStream is = null;
		JSONArray jArr = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(list));
		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();
		Log.d("result: ", json);
		try {
			jArr = new JSONArray(json);
		} catch (JSONException e) {
			throw e;
		}

		return jArr;
	}

	public JSONArray getJSONArrayFromGetUrl(String url)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException, Exception, JSONException {

		InputStream is = null;
		JSONArray jArr = null;
		String json = "";

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();
		Log.d("result: ", json);
		try {
			jArr = new JSONArray(json);
		} catch (JSONException e) {
			throw e;
		}

		return jArr;
	}

}

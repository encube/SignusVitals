package com.encube.signusvitalis.serverconnectionmodule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class LogoutRequest {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public LogoutRequest() {

	}

	public void getJSONFromUrl(String url, List<NameValuePair> list)
			throws ClientProtocolException, IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(list));
		httpClient.execute(httpPost);
	}
}

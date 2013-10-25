package com.encube.signusvitalis.activities;

import java.util.ArrayList;

import com.encube.signusvitalis.R;
import com.encube.signusvitalis.domain.VSRecord;
import com.encube.signusvitalis.view.Graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GraphActivity extends Activity {

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_graph);
		Graph graph = (Graph) findViewById(R.id.graph);
		Intent intent = getIntent();
		ArrayList<VSRecord> record = (ArrayList<VSRecord>) intent
				.getSerializableExtra("record");
		graph.setPoints(record);
		graph.invalidate();
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

}

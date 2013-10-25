package com.encube.signusvitalis.view;

import java.util.ArrayList;
import java.util.Collections;

import com.encube.signusvitalis.domain.VSRecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Graph extends View {

	private Paint p;
	private float yInterval;
	private float xInterval;
	private float margin;
	private ArrayList<VSRecord> records;

	public Graph(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		records = new ArrayList<VSRecord>();
	}

	@Override
	synchronized protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		margin = (float) (getWidth() * .03);

		yInterval = (getHeight() - (margin * 2)) / 22;
		xInterval = (getWidth() - (margin * 2)) / 20;
		p.setAlpha(255);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);
		p.setColor(Color.BLACK);
		canvas.drawRect(margin - 1, margin - 1,
				(float) getWidth() - margin + 1, (float) getHeight() - margin
						+ 1, p);
		p.setColor(Color.WHITE);
		canvas.drawRect(margin, margin, (float) getWidth() - margin,
				(float) getHeight() - margin, p);
		p.setColor(Color.BLACK);
		p.setTextSize(18);
		canvas.drawLine(margin, margin + yInterval * 3, getWidth() - margin,
				margin + yInterval * 3, p);
		p.setColor(Color.parseColor("#FFAA4643"));
		canvas.drawText(
				"R",
				(float) (margin + (xInterval * 0.20)),
				(float) (yInterval + yInterval + yInterval + margin - (yInterval * 0.30)),
				p);
		p.setColor(Color.parseColor("#FF4572A7"));
		canvas.drawText(
				"P",
				(float) (margin + (xInterval * 0.20) + xInterval),
				(float) (yInterval + yInterval + yInterval + margin - (yInterval * 0.30)),
				p);
		p.setColor(Color.parseColor("#FF89A54E"));
		canvas.drawText(
				"T",
				(float) (margin + (xInterval * 0.20) + xInterval + +xInterval),
				(float) (yInterval + yInterval + yInterval + margin - (yInterval * 0.30)),
				p);
		p.setColor(Color.BLACK);
		for (float i = margin + yInterval * 3; i < getHeight() - margin; i += yInterval) {
			canvas.drawLine(margin + (xInterval * 3), i, getWidth() - margin,
					i, p);
		}
		for (float i = margin + xInterval; i < getWidth() - margin; i += xInterval) {
			canvas.drawLine(i, margin, i, getHeight() - margin, p);
		}
		p.setColor(Color.parseColor("#FFAA4643"));
		p.setTextSize(11);
		float y = (float) ((float) (getHeight() - margin) - (yInterval * 0.30));
		for (int i = 20; i <= 50; i += 10) {
			canvas.drawText(i + "", (float) (margin + (xInterval * 0.10)), y, p);
			y -= yInterval;
		}
		p.setColor(Color.parseColor("#FF4572A7"));
		y = (float) ((float) (getHeight() - margin) - (yInterval * 0.30) - (yInterval * 3));
		for (int i = 60; i <= 180; i += 20) {
			canvas.drawText(i + "",
					(float) (margin + (xInterval * 0.10) + xInterval), y, p);
			y -= (yInterval * 2);
		}
		p.setColor(Color.parseColor("#FF89A54E"));
		y = (float) ((float) (getHeight() - margin) - (yInterval * 0.30) - (yInterval * 11));
		for (int i = 35; i <= 42; i += 1) {
			canvas.drawText(i + "", (float) (margin + (xInterval * 0.10)
					+ xInterval + xInterval), y, p);
			y -= yInterval;
		}
		plotPoints(canvas);
	}

	@SuppressWarnings("unchecked")
	public void setPoints(ArrayList<VSRecord> records) {
		this.records = (ArrayList<VSRecord>) records.clone();
		while (this.records.size() > 17) {
			this.records.remove(this.records.size() - 1);
		}
		Collections.reverse(this.records);
	}

	public void plotPoints(Canvas canvas) {
		int scale;
		float previousXAxis = 0;
		float previousYAxis = 0;

		float xAxis = (xInterval * 3) + margin;
		float yAxis = (yInterval * 3) + margin;

		p.setColor(Color.BLACK);
		for (int i = 0; i < records.size(); i++) {
			canvas.save();
			canvas.rotate(270, (float) (xAxis + (xInterval * .70)),
					(float) (yAxis - (yInterval * .10)));
			canvas.drawText(records.get(i).getDate().substring(5, 16),
					(float) (xAxis + (xInterval * .70)),
					(float) (yAxis - (yInterval * .10)), p);
			canvas.restore();
			xAxis += xInterval;
		}

		xAxis = (xInterval * 3) + margin;
		p.setColor(Color.parseColor("#FFAA4643"));
		for (int i = 0; i < records.size(); i++) {
			Log.d("records", records.get(i).getTemperature() + " "
					+ records.get(i).getPulse() + " "
					+ records.get(i).getRespiratation());
			scale = (int) ((Float.parseFloat(records.get(i).getRespiratation()) - 20) / 10);
			yAxis = getHeight()
					- ((yInterval * scale)
							+ (yInterval * (((Float.parseFloat(records.get(i)
									.getRespiratation()) - 20) % 10) / 10)) + margin);
			p.setTextSize(12);
			canvas.drawText(records.get(i).getRespiratation(), xAxis - 7,
					yAxis - 5, p);
			p.setTextSize(18);
			canvas.drawCircle(xAxis, yAxis, 3, p);
			if (previousXAxis != 0) {
				canvas.drawLine(previousXAxis, previousYAxis, xAxis, yAxis, p);
			}
			previousXAxis = xAxis;
			previousYAxis = yAxis;
			xAxis += xInterval;
		}
		xAxis = (xInterval * 3) + margin;
		previousXAxis = 0;
		previousYAxis = 0;
		p.setColor(Color.parseColor("#FF4572A7"));
		for (int i = 0; i < records.size(); i++) {
			scale = (int) ((Float.parseFloat(records.get(i).getPulse()) - 30) / 10);
			yAxis = getHeight()
					- ((yInterval * scale)
							+ (yInterval * (((Float.parseFloat(records.get(i)
									.getPulse()) - 30) % 10) / 10)) + margin);
			p.setTextSize(12);
			canvas.drawText(records.get(i).getPulse(), xAxis - 7, yAxis - 5, p);
			p.setTextSize(18);
			canvas.drawCircle(xAxis, yAxis, 3, p);
			if (previousXAxis != 0) {
				canvas.drawLine(previousXAxis, previousYAxis, xAxis, yAxis, p);
			}
			previousXAxis = xAxis;
			previousYAxis = yAxis;
			xAxis += xInterval;
		}
		xAxis = (xInterval * 3) + margin;
		previousXAxis = 0;
		previousYAxis = 0;
		p.setColor(Color.parseColor("#FF89A54E"));
		for (int i = 0; i < records.size(); i++) {
			scale = (int) (Float.parseFloat(records.get(i).getTemperature()) - 24);
			yAxis = getHeight()
					- ((yInterval * scale)
							+ (yInterval * ((Float.parseFloat(records.get(i)
									.getTemperature()) - 24) % 1)) + margin);
			p.setTextSize(12);
			canvas.drawText(records.get(i).getTemperature(), xAxis - 7,
					yAxis - 5, p);
			p.setTextSize(18);
			canvas.drawCircle(xAxis, yAxis, 3, p);
			if (previousXAxis != 0) {
				canvas.drawLine(previousXAxis, previousYAxis, xAxis, yAxis, p);
			}
			previousXAxis = xAxis;
			previousYAxis = yAxis;
			xAxis += xInterval;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);
	}
}

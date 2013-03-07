/**
 * 
 */
package com.ahuralab.mozaic.app;

import com.ahuralab.mozaic.R;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author pani
 *
 */
public class MozaicSquare extends LinearLayout {

	public MozaicSquare(Context context) {
		super(context);
		 LayoutInflater inflater = LayoutInflater.from(context);
	     View newView = inflater.inflate(R.layout.activity_timeline, null, false);
	     this.addView(newView);

	}
}

package com.mmmgoodyes.PartyPal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Group_Viewer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group__viewer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group__viewer, menu);
		return true;
	}

}

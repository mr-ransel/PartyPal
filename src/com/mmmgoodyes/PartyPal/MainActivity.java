package com.mmmgoodyes.PartyPal;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void letsGetStarted(View view) {
		//setContentView(R.layout.main);

		Button mButton=(Button)findViewById(R.id.start_button);
		mButton.setBackgroundColor(Color.parseColor("#454545")); // custom color
		
		Intent intent = new Intent(this,Dashboard.class);
		startActivity(intent);
	}

}

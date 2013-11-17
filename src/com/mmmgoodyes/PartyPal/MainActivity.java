package com.mmmgoodyes.PartyPal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mmmgoodyes.PartyPal.Dashboard.RequestTask;
import com.mmmgoodyes.PartyPal.R.id;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	public String number;
	public String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		number = "";
		name = "";
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

		
		
		
				
				
				
							


			
		
		
	
		number = ((EditText)findViewById(R.id.Number)).getText().toString();
		name = ((EditText)findViewById(R.id.Name)).getText().toString();
		
		
		new RequestTask().execute("");
		
		Log.v("name",name);
		Log.v("number",number);
		
		if (number.length()==10 && name.length()>0) {
			
			intent.putExtra("number", number);
			intent.putExtra("name", name);
			
			startActivity(intent);

		}
	}

	class RequestTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... uri) {
	    	
			Button mButton=(Button)findViewById(R.id.start_button);
			//mButton.setBackgroundColor(getResources().getColor(R.color.DarkerGray)); // custom color

	    	
	        return "";
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
}

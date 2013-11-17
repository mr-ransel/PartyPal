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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class Group_Viewer extends Activity {

	public String name;
	public String number;
	public ArrayList<String> groups;
	public int groupindex;
	public JSONArray status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group__viewer);
		
		SharedPreferences settings = getPreferences(0);
		if (!settings.getBoolean("registered", false)) {
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
		} else {
			name = settings.getString("name", "");
			number = settings.getString("number", "");
		}
		groups = new ArrayList<String>();
		try {
			JSONArray json = new JSONArray(settings.getString("groups", "[]"));
			if (json != null) {
				for (int i = 0; i<json.length();i++) {
					groups.add(json.get(i).toString());
				}
			}
			if (json.length()<1) {
				Intent intent = new Intent(this,Dashboard.class);
				startActivity(intent);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		groupindex = 0;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group__viewer, menu);
		return true;
	}

	class QueryTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... index) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","get_group_status");
				data.put("groupphrase",groups.get(groupindex));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
	    	String json_string = data.toString();
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost(getResources().getString(R.string.request_URL));
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	List<NameValuePair> nvpairs = new ArrayList<NameValuePair>(1);
	        	nvpairs.add(new BasicNameValuePair("json",json_string));
	        	
	        	post.setEntity(new UrlEncodedFormEntity(nvpairs));
	        	
	            response = httpclient.execute(post);
	            
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            	
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                Log.v("http response",out.toString());
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	            	Log.v("http response failed",response.toString());
	                response.getEntity().getContent().close();
	                
	            }
	        } catch (ClientProtocolException e) {
	            //nope
	        } catch (IOException e) {
	            //nuh uh
	        }
	        return responseString;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	
	    	
	    	try {
				status = new JSONArray(result);
				Log.v("json",status.length()+"");
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
	    	
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
}

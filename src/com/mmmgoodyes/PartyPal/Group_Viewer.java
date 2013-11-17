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
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Group_Viewer extends Activity {

	public String name;
	public String number;
	public ArrayList<String> groups;
	public int groupindex;
	public JSONArray status;
	public CountDownTimer C;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group__viewer);
		number = "9416856455";
		
		groups = new ArrayList<String>();
		
		SharedPreferences settings = getPreferences(0);
		if (!settings.getBoolean("registered", false)) {
			Intent intent = new Intent(this,MainActivity.class);
			//startActivity(intent);
		} else {
			name = settings.getString("name", "");
			number = settings.getString("number", "");
		}
		
		new ListTask().execute("");
		
		groupindex = 0;
		Intent in = getIntent();
		String gn = in.getStringExtra("GroupName");
		number = in.getStringExtra("Number");
		name = in.getStringExtra("Name");
		new AsyncGroupAdder().execute(gn);
		groups.add(gn);
		
		for (int i = 0;i<groups.size();i++) {
			new QueryTask().execute("");
		}
		
		TextView t = (TextView)findViewById(R.id.GroupList);
		t.setText(groups.toString());
		
	}
	
	class QueryTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... uri) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","get_group");
				data.put("phonenumber", number);
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
	    	
	    	SharedPreferences settings = getPreferences(0);
	    	settings.edit().putString("groups", result).commit();
	    	int length;
	    	try {
				JSONArray json = new JSONArray(result);
				Log.v("json",json.length()+"");
				length = json.length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group__viewer, menu);
		return true;
	}

	class AsyncGroupAdder extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... group) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","add_groupmember");
				data.put("phonenumbers", new JSONArray().put(number));
				data.put("groupphrase",group[0]);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
	    	String json_string = data.toString();
	    	Log.v("json",json_string);
	    	
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
	                
	        		SharedPreferences settings = getPreferences(0);
	                
	                
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
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
	
	class ListTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... index) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","get_group");
				data.put("phonenumber",number);
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
				JSONArray json = new JSONArray(result);
				if (json != null) {
					for (int i = 0; i<json.length();i++) {
						groups.add(json.get(i).toString());
					}
				}
				if (json.length()<1) {
					go_to_dashboard();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
	    	
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}

	class CheckInTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... index) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","check_in");
				data.put("phonenumber",number);
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

	class CheckOutTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... index) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","remove_groupmember");
				data.put("phonenumbers",new JSONArray().put(number));
				data.put("groupphrase","*");
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

	
	public void check_in(View view) {
		new CheckInTask().execute("");
		if (C!=null) {C.cancel();}
		C = new CountDownTimer(3600000, 1000) {

		     public void onTick(long millisUntilFinished) {
		 		TextView t = (TextView)findViewById(R.id.CountDown);

		         t.setText(millisUntilFinished / 60000 + ":" + (millisUntilFinished / 1000 - millisUntilFinished%60000));
		     }

		     public void onFinish() {
			 		TextView t = (TextView)findViewById(R.id.CountDown);

		         t.setText("Check In!");
		     }
		  }.start();
	}
	
	public void check_out(View view) {
		new CheckOutTask().execute("");
		groups = new ArrayList<String>();
		Intent intent = new Intent(this,Dashboard.class);
		startActivity(intent);
	}
	
	public void go_to_dashboard() {
		Intent intent = new Intent(this,Dashboard.class);
		startActivity(intent);
	}
}

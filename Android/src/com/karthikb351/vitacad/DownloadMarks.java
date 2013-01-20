package com.karthikb351.vitacad;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DownloadMarks extends SherlockActivity {
	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	String regno,dob,captcha;
	boolean haveCap=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		settings=getSharedPreferences("vitacad", 0);
		regno=settings.getString("regno", " ");
		dob=settings.getString("dob", " ");
		LoadAttendanceTask currentTask=new LoadAttendanceTask();
		ArrayList<String> details = new ArrayList<String>();
		details.add(0,regno);
		details.add(1,dob);
		currentTask.execute(details);
	}
	void cancelled()
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", "cancelled");
		setResult(RESULT_CANCELED, returnIntent);        
		finish();
		
	}
	
	private class LoadAttendanceTask extends AsyncTask<ArrayList <String>, Void, String>
	{
		ProgressDialog pdia;
		
		protected void onPreExecute() {
			
		  	pdia = new ProgressDialog(DownloadMarks.this);
	        pdia.setMessage("Loading Marks");
	        pdia.setCancelable(true);
	        pdia.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
					cancelled();
					
				}
			});
	        pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pdia.show();
	  }
		@Override
		protected String doInBackground(ArrayList <String>... params) {
			// TODO Auto-generated method stub
			String res="";
			ArrayList <String> details=params[0];
			String urldisplay = "http://vitacademics.appspot.com/marks/"+details.get(0)+"/"+details.get(1);
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urldisplay);
				HttpResponse response;
				response = client.execute(request);
				res=EntityUtils.toString(response.getEntity());
				
				}
			
			catch (Exception e) {
				res="error";
				Log.e("Error", e.getMessage());
				e.printStackTrace();
				}
			if(isCancelled())
				return "cancelled";
			else
			return res;
		}
		
		protected void onPostExecute(String result) {

			if(!result.equals("cancelled"))	{
				
				Intent returnIntent = new Intent();
				if(result.equals("error")){
					Log.e("Error","erroring in atten");
					returnIntent.putExtra("result", result);
				 	setResult(RESULT_CANCELED,returnIntent);

				}
				else if(result.contains("timedout")){
					Log.e("Error","timedout");
					returnIntent.putExtra("result", result);
				 	setResult(RESULT_CANCELED,returnIntent);
				}
				else {
					returnIntent.putExtra("result", result);
				 	setResult(RESULT_OK,returnIntent);
					
				}
			}
			else
				cancelled();
			pdia.dismiss();
			finish();
				
		}
	}

}

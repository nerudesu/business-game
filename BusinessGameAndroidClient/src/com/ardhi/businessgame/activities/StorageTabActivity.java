package com.ardhi.businessgame.activities;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.GlobalServices;
import com.ardhi.businessgame.services.TimeSync;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class StorageTabActivity extends TabActivity {
	private ProgressDialog progressDialog;
	private ProgressBar progressCapacity;
	private TextView txtCapacity;
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private String data;
	private double capacity, fill;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_tab);
		
		zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        txtCapacity = (TextView)findViewById(R.id.txt_capacity);
		db = new DBAccess(this);
		progressCapacity = (ProgressBar)findViewById(R.id.progress_capacity);
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn);
        Intent i = new Intent(getApplicationContext(), GlobalServices.class);
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        
		progressDialog = ProgressDialog.show(this, "", "Checking user's storage..");
		new CheckUserStorage().execute();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		timeSync.setThreadWork(false);
		t.interrupt();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timeSync.setThreadWork(true);
		t = new Thread(timeSync);
		t.start();
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			timeSync.setGlobalServices(null);
			timeSync.setServiceBound(false);
		}
		
		public void onServiceConnected(ComponentName name, IBinder binder) {
			timeSync.setGlobalServices(((GlobalServices.MyBinder)binder).getService());
			timeSync.setServiceBound(true);
		}
	};
	
	@Override
	public Dialog onCreateDialog(int id){
		switch (id) {
		case 1:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textView = factory.inflate(R.layout.question_storage, null);
			return new AlertDialog.Builder(this)
				.setView(textView)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doPositiveClickDialog();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doNegativeClickDialog();
					}
				})
				.setCancelable(false)
				.create();

		default:
			break;
		}
		return null;
	}
	
	private void doPositiveClickDialog(){
		progressDialog = ProgressDialog.show(StorageTabActivity.this, "", "Building user's storage..");
		new BuildUserStorage().execute();
	}
	
	private void doNegativeClickDialog(){
		finish();
	}
	
	private void setLayout(){
		txtCapacity.setText((int)fill+"/"+(int)capacity);
		progressCapacity.setMax((int)capacity);
		progressCapacity.setProgress((int)fill);
		
		TabHost.TabSpec spec;
		Intent intent;
		
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(data).getAsJsonArray();
		
		intent = new Intent(this, StorageTabContentActivity.class);
		intent.putExtra("Tab", "Product");
		intent.putExtra("Data", new Gson().fromJson(array.get(0), String.class));
		spec = getTabHost().newTabSpec("Product").setIndicator("Product", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
        
        intent = new Intent(this, StorageTabContentActivity.class);
		intent.putExtra("Tab", "Equipment");
		intent.putExtra("Data", new Gson().fromJson(array.get(1), String.class));
		spec = getTabHost().newTabSpec("Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(intent);
        getTabHost().addTab(spec);
	}
	
	private class CheckUserStorage extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_CHECK_USER_STORAGE+"&user="+user.getName()+"&zone="+user.getZone());
				res = res.toString().replaceAll("\\n+", "");
				return res.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("No")){
				showDialog(1);
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				capacity = new Gson().fromJson(array.get(0), Double.class);
				fill = new Gson().fromJson(array.get(1), Double.class);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(new Gson().fromJson(array.get(2), String.class));
				tmp.add(new Gson().fromJson(array.get(3), String.class));
				data = new Gson().toJson(tmp);
				setLayout();
			}
		}
	}
	
	private class BuildUserStorage extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("user", user.getName()));
			postParameters.add(new BasicNameValuePair("zone", user.getZone()));
			postParameters.add(new BasicNameValuePair("action", CustomHttpClient.POST_BUILD_USER_STORAGE));
			try {
				String res = CustomHttpClient.executeHttpPost(CustomHttpClient.URL, postParameters);
				res = res.toString().replaceAll("\\n+", "");
				return res.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_LONG);
				finish();
			} else if(res == "No"){
				Toast.makeText(getApplicationContext(), "Error: Cannot build storage..", Toast.LENGTH_LONG);
				finish();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				db.addUserStorage(user.getZone(), res.toString());
				setLayout();
			}
		}
	}
}

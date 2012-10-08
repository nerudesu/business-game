package com.ardhi.businessgame.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.SystemService;
import com.ardhi.businessgame.services.TimeSync;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
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
	private double capacity, fill, price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_storage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
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
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(getApplicationContext(), SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Checking user's storage..");
    		new CheckUserStorage().execute();
        } else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tab_storage, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public Dialog onCreateDialog(int id){
		switch (id) {
		case 1:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textView = factory.inflate(R.layout.question_storage, null);
			TextView question = (TextView)textView.findViewById(R.id.question_storage);
			String text = getString(R.string.question_storage);
			question.setText(String.format(text, price));
			return new AlertDialog.Builder(this)
				.setView(textView)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() >= price)
							doPositiveClickDialog();
						else {
							Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_LONG).show();
							finish();
						}
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
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			timeSync.setGlobalServices(null);
			timeSync.setServiceBound(false);
		}
		
		public void onServiceConnected(ComponentName name, IBinder binder) {
			timeSync.setGlobalServices(((SystemService.MyBinder)binder).getService());
			timeSync.setServiceBound(true);
		}
	};
	
	private void doPositiveClickDialog(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(StorageTabActivity.this, "", "Building user's storage..");
			new BuildUserStorage().execute();
		}  else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
		}
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
        
        parser = null;
        array = null;
	}
	
	private class CheckUserStorage extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
//				return CommunicationService.get(CommunicationService.GET_CHECK_USER_STORAGE+"&user="+user.getName()+"&zone="+user.getZone());
				return CommunicationService.get(CommunicationService.GET_CHECK_USER_STORAGE+"&storage="+user.getStorages().get(user.getZone()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
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
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				boolean isAvailable = new Gson().fromJson(array.get(0), Boolean.class);
				
				if(isAvailable){
					capacity = new Gson().fromJson(array.get(1), Double.class);
					fill = new Gson().fromJson(array.get(2), Double.class);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(new Gson().fromJson(array.get(3), String.class));
					tmp.add(new Gson().fromJson(array.get(4), String.class));
					data = new Gson().toJson(tmp);
					
					parser = null;
					array = null;
					tmp = null;
					
					setLayout();
				} else {
					price = new Gson().fromJson(array.get(1), Double.class);
					showDialog(1);
				}
			}
		}
	}
	
	private class BuildUserStorage extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_BUILD_USER_STORAGE, postParameters);
			} catch (Exception e) {
				e.printStackTrace();
				res = null;
			}
			
			postParameters = null;
			
			return res;
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_LONG);
				finish();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("1")){
				Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray();
				android.util.Log.d("id", new Gson().fromJson(array.get(0), String.class));
				db.addUserStorage(user.getZone(), new Gson().fromJson(array.get(0), String.class));
				user.getStorages().put(user.getZone(), new Gson().fromJson(array.get(0), String.class));
				android.util.Log.d("id", user.getStorages().get(user.getZone()));
				
				user.setMoney(new Gson().fromJson(array.get(1), Double.class));
				db.updateUserData(user);
				
				capacity = new Gson().fromJson(array1.get(1), Double.class);
				fill = new Gson().fromJson(array1.get(2), Double.class);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(new Gson().fromJson(array1.get(3), String.class));
				tmp.add(new Gson().fromJson(array1.get(4), String.class));
				data = new Gson().toJson(tmp);
				
				parser = null;
				array = null;
				tmp = null;
				
				setLayout();
			}
		}
	}
}

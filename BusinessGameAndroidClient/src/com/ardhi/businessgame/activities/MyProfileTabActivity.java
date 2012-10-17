package com.ardhi.businessgame.activities;

import java.io.IOException;
import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.MessageAdapter;
import com.ardhi.businessgame.models.Message;
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
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MyProfileTabActivity extends TabActivity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ProgressDialog progressDialog;
	private ArrayList<Message> messages;
	private int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_myprofile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        pos = 0;
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
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
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Loading User's details..");
        	new LoadUserData().execute();
        } else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
        }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_myprofile, menu);
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
	
	private void setLayout(int pos){
		TabHost.TabSpec spec;
		
		getTabHost().setCurrentTab(0);
		getTabHost().clearAllTabs();
		
		spec = getTabHost().newTabSpec("My Profile").setIndicator("My Profile", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabMyProfile());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Private Message").setIndicator("Private Message", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabPrivateMessage());
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(pos);
	}
	
	private class TabMyProfile implements TabHost.TabContentFactory {
		private MyProfileTabActivity a;
		
		public TabMyProfile(){
			a = MyProfileTabActivity.this;
		}

		@Override
		public View createTabContent(String arg0) {
			LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.extended_myprofile, null);
			EditText userText = (EditText)v.findViewById(R.id.txt_user),
					email = (EditText)v.findViewById(R.id.txt_email),
					dob = (EditText)v.findViewById(R.id.txt_dob),
					about = (EditText)v.findViewById(R.id.txt_about),
					rep = (EditText)v.findViewById(R.id.txt_reputation),
					level = (EditText)v.findViewById(R.id.txt_level);
	        userText.setText(user.getName());
	        email.setText(user.getEmail());
	        dob.setText(user.getDob());
	        about.setText(user.getAbout());
	        rep.setText(""+user.getRep());
	        level.setText(""+user.getLevel());
			return v;
		}
		
	}
	
	private class TabPrivateMessage implements TabHost.TabContentFactory {
		private MyProfileTabActivity a;
		
		public TabPrivateMessage(){
			a = MyProfileTabActivity.this;
		}
		@Override
		public View createTabContent(String arg0) {
			ListView lv = new ListView(a);
			lv.setAdapter(new MessageAdapter(a, messages));
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
					a.pos = 1;
					String tmp = new Gson().toJson(messages.get(pos));
					Intent intent = new Intent(a, MessageActivity.class);
					intent.putExtra("Data", tmp);
					startActivity(intent);
				}
				
			});
			return lv;
		}
	}
	
	private class LoadUserData extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_USER_DATA+"&user="+user.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				
				messages = new ArrayList<Message>();
				
				for(int i=0;i<array1.size();i++){
					messages.add(new Gson().fromJson(array1.get(i), Message.class));
				}
				
				setLayout(pos);
			}
		}
	}
}

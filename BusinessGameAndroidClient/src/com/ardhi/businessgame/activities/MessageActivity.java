package com.ardhi.businessgame.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.Message;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.SystemService;
import com.ardhi.businessgame.services.TimeSync;
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressLint("NewApi")
public class MessageActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn, txtFrom, txtTime, txtMessage;
	private User user;
	private Handler h;
	private Thread t;
	private TimeSync timeSync;
	private ProgressDialog progressDialog;
	private Message message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        txtFrom = (EditText)findViewById(R.id.txt_from);
        txtTime = (EditText)findViewById(R.id.txt_time);
        txtMessage = (EditText)findViewById(R.id.txt_message);
        
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
        message = new Gson().fromJson(getIntent().getStringExtra("Data"), Message.class);
        
        if(message.isUnread()){
        	if(CommunicationService.isOnline(this)){
            	progressDialog = ProgressDialog.show(this, "", "Reading message..");
            	new MarkMessageAsRead().execute();
            } else {
            	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
            	finish();
            }
        } else setLayout();
        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message, menu);
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
	
	private void setLayout(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(message.getId().substring(2, 15)));
		SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss");
		
		txtFrom.setText(message.getSender());
		txtTime.setText(format.format(c.getTime()));
		txtMessage.setText(message.getMessage());
	}
	
	private class MarkMessageAsRead extends AsyncTask<String, Void, Object>{
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("id", message.getId());
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_MARK_MESSAGE_AS_READ, postParameters);
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
				Toast.makeText(MessageActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MessageActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MessageActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				setLayout();
			}
		}
	}
}

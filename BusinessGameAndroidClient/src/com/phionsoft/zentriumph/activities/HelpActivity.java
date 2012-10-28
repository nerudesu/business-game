package com.phionsoft.zentriumph.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

@SuppressLint("NewApi")
public class HelpActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ArrayList<Integer> layouts, titles;
	private int pos;
	private Button btnNext, btnPrev;
	private TextView txtHelpTitle;
	private ScrollView scrollHelpContent;
	private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        layouts = new ArrayList<Integer>();
        titles = new ArrayList<Integer>();
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        btnNext = (Button)findViewById(R.id.btn_next);
        btnPrev = (Button)findViewById(R.id.btn_prev);
        txtHelpTitle = (TextView)findViewById(R.id.txt_help_title);
        scrollHelpContent = (ScrollView)findViewById(R.id.scroll_help_content);
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        
        user = db.getUser();
        zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        titles.add(R.string.help_0_title);
        titles.add(R.string.help_1_title);
        titles.add(R.string.help_2_title);
        titles.add(R.string.help_3_title);
        titles.add(R.string.help_4_title);
        titles.add(R.string.help_5_title);
        titles.add(R.string.help_6_title);
        titles.add(R.string.help_7_title);
        titles.add(R.string.help_8_title);
        titles.add(R.string.help_9_title);
        titles.add(R.string.help_10_title);
        layouts.add(R.layout.extended_help_0);
        layouts.add(R.layout.extended_help_1);
        layouts.add(R.layout.extended_help_2);
        layouts.add(R.layout.extended_help_3);
        layouts.add(R.layout.extended_help_4);
        layouts.add(R.layout.extended_help_5);
        layouts.add(R.layout.extended_help_6);
        layouts.add(R.layout.extended_help_7);
        layouts.add(R.layout.extended_help_8);
        layouts.add(R.layout.extended_help_9);
        layouts.add(R.layout.extended_help_10);
        
        pos = 0;
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        txtHelpTitle.setText(titles.get(pos));
        scrollHelpContent.addView(inflater.inflate(layouts.get(pos), null));
        
        btnPrev.setOnClickListener(onClickHandler);
        btnPrev.setEnabled(false);
        btnNext.setOnClickListener(onClickHandler);
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
        getMenuInflater().inflate(R.menu.activity_help, menu);
        return true;
    }
    
    private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_next:
				pos++;
				if(!btnPrev.isEnabled())
					btnPrev.setEnabled(true);
		        if(pos == titles.size()-1)
		        	btnNext.setEnabled(false);
				break;
				
			case R.id.btn_prev:
				pos--;
				if(!btnNext.isEnabled())
					btnNext.setEnabled(true);
		        if(pos == 0)
		        	btnPrev.setEnabled(false);
		        break;

			default:
				
				break;
			}
			txtHelpTitle.setText(titles.get(pos));
			scrollHelpContent.removeAllViews();
	        scrollHelpContent.addView(inflater.inflate(layouts.get(pos), null));
		}
    };

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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

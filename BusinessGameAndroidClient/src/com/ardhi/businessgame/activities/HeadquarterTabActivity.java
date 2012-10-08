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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class HeadquarterTabActivity extends TabActivity {
	private ProgressDialog progressDialog;
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ArrayList<String> sectors, userSectors;
	private ArrayList<Integer> sectorsLvl;
	private double sector;
	private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_headquarter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
		db = new DBAccess(this);
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Loading Headquarter's data..");
			new LoadHeadquarterData().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.activity_tab_headquarter, menu);
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
	
	private AdapterView.OnItemClickListener onItemClickHandlerSector = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			if(userSectors.contains(parent.getItemAtPosition(pos).toString())){
				Toast.makeText(HeadquarterTabActivity.this, "You have sector "+parent.getItemAtPosition(pos).toString()+"'s blueprint", Toast.LENGTH_SHORT).show();
			} else {
				if(user.getLevel() < sectorsLvl.get(pos)){
					Toast.makeText(HeadquarterTabActivity.this, "Your level is lower than the sector's required level..", Toast.LENGTH_SHORT).show();
				} else {
					position = pos;
					dialog(1).show();
				}
			}
		}
	};
	
	private AlertDialog dialog(int d){
		final LayoutInflater factory;
		final View view;
		AlertDialog dialog = null;
		switch (d) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_sector_blueprint_buy, null);
				TextView txtSector = (TextView)view.findViewById(R.id.txt_sector),
						txtPrice = (TextView)view.findViewById(R.id.txt_price);
				txtSector.setText(sectors.get(position));
				txtPrice.setText((sector*(userSectors.size())*sectorsLvl.get(position))+" ZE");
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < (sector*(userSectors.size())*sectorsLvl.get(position)))
							Toast.makeText(HeadquarterTabActivity.this, "Insufficient money..", Toast.LENGTH_SHORT).show();
						else doPositiveClickDialogSector();
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.create();
				break;
		}
		return dialog;
	}
	
	private void doPositiveClickDialogSector() {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new BuySectorBlueprint().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setLayout(){
		TabHost.TabSpec spec;
		
		getTabHost().clearAllTabs();
		
		spec = getTabHost().newTabSpec("Sector Unlocked").setIndicator("Sector Unlocked", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabSectorUnlocked());
        getTabHost().addTab(spec);
        
		spec = getTabHost().newTabSpec("Contract List").setIndicator("Contract List", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new ContractList());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Pending Contract List").setIndicator("Pending Contract List", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new PendingContractList());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Private Message").setIndicator("Private Message", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new PrivateMessage());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Change Resident").setIndicator("Change Resident", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new ChangeResident());
        getTabHost().addTab(spec);
        
        if(userSectors.size() < 1)
        	Toast.makeText(this, "You are given one level 1 blueprint for free. Go get it now..", Toast.LENGTH_LONG).show();
        if(userSectors.size() == sectors.size())
        	Toast.makeText(this, "You own all the sector's blueprint..", Toast.LENGTH_LONG).show();
	}
	
	private class TabSectorUnlocked implements TabHost.TabContentFactory {
		Context c;
		
		public TabSectorUnlocked(){
			c = HeadquarterTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(c);
			lv.setAdapter(new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, sectors));
			lv.setOnItemClickListener(onItemClickHandlerSector);
			lv.setTextFilterEnabled(true);
			return lv;
		}
		
	}
	
	private class ContractList implements TabHost.TabContentFactory {
		Context c;
		
		public ContractList(){
			c = HeadquarterTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(c);
			return v;
		}
		
	}
	
	private class PendingContractList implements TabHost.TabContentFactory {
		Context c;
		
		public PendingContractList(){
			c = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			View v = new View(c);
			return v;
		}
		
	}
	
	private class PrivateMessage implements TabHost.TabContentFactory {
		Context c;
		
		public PrivateMessage(){
			c = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			View v = new View(c);
			return v;
		}
		
	}
	
	private class ChangeResident implements TabHost.TabContentFactory {
		Context c;
		
		public ChangeResident(){
			c = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			View v = new View(c);
			return v;
		}
		
	}
	
	private class LoadHeadquarterData extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_HEADQUARTER_DATA+"&user="+user.getName());
			} catch (Exception e) {
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
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
						array3 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray();
				sectors = new ArrayList<String>();
				sectorsLvl = new ArrayList<Integer>();
				userSectors = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					sectors.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				for(int i=0;i<array2.size();i++){
					sectorsLvl.add(new Gson().fromJson(array2.get(i), Integer.class));
				}
				
				for(int i=0;i<array3.size();i++){
					userSectors.add(new Gson().fromJson(array3.get(i), String.class));
				}
				
				sector = new Gson().fromJson(array.get(3), Double.class);
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				array3 = null;
				
				setLayout();
			}
		}
	}
	
	private class BuySectorBlueprint extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("sector", sectors.get(position));
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_BUY_SECTOR_BLUEPRINT, postParameters);
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
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("1")){
				Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("2")){
				Toast.makeText(getApplicationContext(), "Your level is lower than the sector's required level..", Toast.LENGTH_SHORT).show();
			} else {
				user.setMoney(user.getMoney()-(sector*(userSectors.size())*sectorsLvl.get(position)));
				if(user.getLevel() == sectorsLvl.get(position))
					user.setLevel(sectorsLvl.get(position)+1);
				db.updateUserData(user);
				userSectors.add(sectors.get(position));
				setLayout();
				
//				JsonParser parser = new JsonParser();
//				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
//						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
//						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
//						array3 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray();
//				sectors = new ArrayList<String>();
//				sectorsLvl = new ArrayList<Integer>();
//				userSectors = new ArrayList<String>();
//				for(int i=0;i<array1.size();i++){
//					sectors.add(new Gson().fromJson(array1.get(i), String.class));
//				}
//				
//				for(int i=0;i<array2.size();i++){
//					sectorsLvl.add(new Gson().fromJson(array2.get(i), Integer.class));
//				}
//				
//				for(int i=0;i<array3.size();i++){
//					userSectors.add(new Gson().fromJson(array3.get(i), String.class));
//				}
//				
//				sector = new Gson().fromJson(array.get(3), Double.class);
//				user.setMoney(new Gson().fromJson(array.get(4), Double.class));
//				db.updateUserData(user);
//				parser = null;
//				array = null;
//				array1 = null;
//				array2 = null;
//				array3 = null;
//				setLayout();
			}
		}
	}
}

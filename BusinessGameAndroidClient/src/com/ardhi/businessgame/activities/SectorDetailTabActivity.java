package com.ardhi.businessgame.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.InstallmentEmployeeAdapter;
import com.ardhi.businessgame.activities.adapters.InstallmentEquipmentAdapter;
import com.ardhi.businessgame.models.InstallmentEmployee;
import com.ardhi.businessgame.models.InstallmentEquipment;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class SectorDetailTabActivity extends TabActivity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ProgressDialog progressDialog;
	private String id,idE;
	private double efficiency,effectivity;
	private ArrayList<String> input,output;
	private ArrayList<Double> inputVal,outputVal;
	private ArrayList<InstallmentEmployee> employees;
	private ArrayList<InstallmentEquipment> equipments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_sector_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        id = getIntent().getStringExtra("id");
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Loading Sector's details..");
        	new LoadSectorDetails().execute();
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
        getMenuInflater().inflate(R.menu.activity_tab_sector_detail, menu);
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
	
	public AlertDialog dialog(int d,String idEquipment,String e){
		idE = idEquipment;
		final LayoutInflater factory;
		final View view;
		final EditText txt;
		AlertDialog dialog = null;
		switch (d) {
		case 1:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_equipment_detach, null);
			txt = (EditText)view.findViewById(R.id.txt_equipment);
			txt.setText(e);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogEquipment();
				}
				
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;

		case 2:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_employee_fire, null);
			txt = (EditText)view.findViewById(R.id.txt_employee);
			txt.setText(e);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogEmployee();
				}
				
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;
		}
		return dialog;
	}
	
	private void setLayout(int tab){
		TabHost.TabSpec spec;
		
		getTabHost().clearAllTabs();
		
		spec = getTabHost().newTabSpec("Sector Info").setIndicator("Sector Info", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabSectorInfo());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabEquipment());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Employee").setIndicator("Employee", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabEmployee());
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(tab);
	}
	
	private void doPositiveClickDialogEquipment(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new DetachEquipment().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogEmployee(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new FireEmployee().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class TabSectorInfo implements TabHost.TabContentFactory {
		private SectorDetailTabActivity a;
		
		public TabSectorInfo(){
			a = SectorDetailTabActivity.this;
		}

		@Override
		public View createTabContent(String arg0) {
			ScrollView layout = new ScrollView(a);
			TableLayout table = new TableLayout(a);
			TableRow row = new TableRow(a);
			TextView text;
			EditText edit;
			
			table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
			
			text = new TextView(a);
			text.setText("Efficiency : ");
			row.addView(text);
			
			edit = new EditText(a);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0x00000000);
			edit.setTextColor(0xffffffff);
			edit.setText(""+efficiency);
			row.addView(edit);
			
			table.addView(row);
			
			row = new TableRow(a);
			text = new TextView(a);
			text.setText("Effectivity : ");
			row.addView(text);
			
			edit = new EditText(a);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0x00000000);
			edit.setTextColor(0xffffffff);
			edit.setText(effectivity+"x");
			row.addView(edit);
			
			table.addView(row);
			
			row = new TableRow(a);
			text = new TextView(a);
			text.setText("");
			row.addView(text);
			
			edit = new EditText(a);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0x00000000);
			edit.setTextColor(0xffffffff);
			edit.setText("");
			row.addView(edit);
			
			table.addView(row);
			
			row = new TableRow(a);
			text = new TextView(a);
			text.setText("Input : ");
			row.addView(text);
			
			edit = new EditText(a);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0x00000000);
			edit.setTextColor(0xffffffff);
			edit.setText("");
			row.addView(edit);
			
			table.addView(row);
			
			for(int i=0;i<input.size();i++){
				row = new TableRow(a);
				text = new TextView(a);
				text.setText("     "+input.get(i)+" : ");
				row.addView(text);
				
				edit = new EditText(a);
				edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
				edit.setFocusable(false);
				edit.setBackgroundColor(0x00000000);
				edit.setTextColor(0xffffffff);
				edit.setText(inputVal.get(i)+" CBM");
				row.addView(edit);
				
				table.addView(row);
			}
			
			row = new TableRow(a);
			text = new TextView(a);
			text.setText("Output : ");
			row.addView(text);
			
			edit = new EditText(a);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0x00000000);
			edit.setTextColor(0xffffffff);
			edit.setText("");
			row.addView(edit);
			
			table.addView(row);
			
			for(int i=0;i<output.size();i++){
				row = new TableRow(a);
				text = new TextView(a);
				text.setText("     "+output.get(i)+" : ");
				row.addView(text);
				
				edit = new EditText(a);
				edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
				edit.setFocusable(false);
				edit.setBackgroundColor(0x00000000);
				edit.setTextColor(0xffffffff);
				edit.setText(outputVal.get(i)+" CBM");
				row.addView(edit);
				
				table.addView(row);
			}
			
			layout.addView(table);
			return layout;
		}
		
	}
	
	private class TabEquipment implements TabHost.TabContentFactory {
		private SectorDetailTabActivity a;
		
		public TabEquipment(){
			a = SectorDetailTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			ListView layout = new ListView(a);
			layout.setAdapter(new InstallmentEquipmentAdapter(a, equipments));
			layout.setTextFilterEnabled(true);
			return layout;
		}
		
	}
	
	private class TabEmployee implements TabHost.TabContentFactory {
		private SectorDetailTabActivity a;
		
		public TabEmployee(){
			a = SectorDetailTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			ListView layout = new ListView(a);
			layout.setAdapter(new InstallmentEmployeeAdapter(a, employees));
			layout.setTextFilterEnabled(true);
			return layout;
		}
		
	}
	
	private class LoadSectorDetails extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_INSTALLMENT_DETAILS+"&id="+id);
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
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
						array3 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray(),
						array4 = parser.parse(new Gson().fromJson(array1.get(2), String.class)).getAsJsonArray(),
						array5 = parser.parse(new Gson().fromJson(array1.get(3), String.class)).getAsJsonArray(),
						array6 = parser.parse(new Gson().fromJson(array1.get(4), String.class)).getAsJsonArray(),
						array7 = parser.parse(new Gson().fromJson(array1.get(5), String.class)).getAsJsonArray();
				
				efficiency = new Gson().fromJson(array1.get(0), Double.class);
				effectivity = new Gson().fromJson(array1.get(1), Double.class);
				input = new ArrayList<String>();
				inputVal = new ArrayList<Double>();
				output = new ArrayList<String>();
				outputVal = new ArrayList<Double>();
				
				for(int i=0;i<array4.size();i++){
					input.add(new Gson().fromJson(array4.get(i), String.class));
					inputVal.add(new Gson().fromJson(array5.get(i), Double.class));
				}
				
				for(int i=0;i<array6.size();i++){
					output.add(new Gson().fromJson(array6.get(i), String.class));
					outputVal.add(new Gson().fromJson(array7.get(i), Double.class));
				}
				
				equipments = new ArrayList<InstallmentEquipment>();
				for(int i=0;i<array2.size();i++){
					equipments.add(new Gson().fromJson(array2.get(i), InstallmentEquipment.class));
				}
				
				employees = new ArrayList<InstallmentEmployee>();
				for(int i=0;i<array3.size();i++){
					employees.add(new Gson().fromJson(array3.get(i), InstallmentEmployee.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				array3 = null;
				array4 = null;
				array5 = null;
				array6 = null;
				array7 = null;
				
				setLayout(0);
			}
		}
	}
	
	private class DetachEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("idEquipment", idE);
			postParameters.put("id", id);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_DETACH_EQUIPMENT, postParameters);
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
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				equipments = new ArrayList<InstallmentEquipment>();
				for(int i=0;i<array.size();i++){
					equipments.add(new Gson().fromJson(array.get(i), InstallmentEquipment.class));
				}
				getTabHost().setCurrentTab(0);
				setLayout(1);
			}
		}
	}
	
	private class FireEmployee extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("idEmployee", idE);
			postParameters.put("id", id);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_FIRE_EMPLOYEE, postParameters);
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
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				employees = new ArrayList<InstallmentEmployee>();
				for(int i=0;i<array.size();i++){
					employees.add(new Gson().fromJson(array.get(i), InstallmentEmployee.class));
				}
				getTabHost().setCurrentTab(0);
				setLayout(2);
			}
		}
	}
}

package com.ardhi.businessgame.activities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.InstallmentEmployeeAdapter;
import com.ardhi.businessgame.activities.adapters.InstallmentEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.InstallmentSupplyAdapter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
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
	private String id,idE,sectorType,currentSupply;
	private double efficiency,effectivity,tariff,totalKwh,currentKwh;
	private ArrayList<String> input,output,types,users,idSupplies;
	private ArrayList<Double> inputVal,outputVal,supplies,tariffs,availables;
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
	
	public AlertDialog dialog(int d,String id,String e){
		idE = id;
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
			
		case 3:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_cancel_supply, null);
			txt = (EditText)view.findViewById(R.id.txt_installment);
			txt.setText(e);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogInstallment();
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
        
        spec = getTabHost().newTabSpec("Energy Supply").setIndicator("Energy Supply", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabEnergySupply());
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
	
	private void doPositiveClickDialogInstallment(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new CancelSupplyInstallment().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateTariff(double newTariff){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new UpdateTariff().execute(""+newTariff);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateSupplyKwh(String id,double kwh){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new UpdateSupplyKwh().execute(id,""+kwh);
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
	
	private class TabEnergySupply implements TabHost.TabContentFactory {
		private SectorDetailTabActivity a;
		
		public TabEnergySupply() {
			a = SectorDetailTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = null;
			if(sectorType.equals("Petrol Power Plant")){
				v = inflater.inflate(R.layout.extended_sector_supply_list, null);
				final EditText txtPrice = (EditText)v.findViewById(R.id.txt_price_kwh),
						txtKwhNeed = (EditText)v.findViewById(R.id.txt_kwh_need);
				ListView lv = (ListView)v.findViewById(R.id.supply_list);
				Button btnSubmit = (Button)v.findViewById(R.id.btn_submit);
				
				txtPrice.setText(""+tariff);
				txtKwhNeed.setText(totalKwh+" KWH");
				lv.setAdapter(new InstallmentSupplyAdapter(a, idSupplies, types, users, supplies));
				lv.setTextFilterEnabled(true);
				btnSubmit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						updateTariff(Double.parseDouble(txtPrice.getText().toString()));
					}
				});
				
			} else {
				v = inflater.inflate(R.layout.extended_sector_supplier_choice, null);
				final Spinner spinSupply = (Spinner)v.findViewById(R.id.spin_supplier);
				final EditText txtUser = (EditText)v.findViewById(R.id.txt_user),
						txtPriceKwh = (EditText)v.findViewById(R.id.txt_price_kwh),
						txtKwhAvailable = (EditText)v.findViewById(R.id.txt_kwh_available),
						txtKwhNeed = (EditText)v.findViewById(R.id.txt_kwh_need);
				Button btnUpdate = (Button)v.findViewById(R.id.btn_update);
				int tmp = idSupplies.indexOf(currentSupply);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, users);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinSupply.setAdapter(adapter);
				if(tmp > -1)
					spinSupply.setSelection(tmp);
				
				txtKwhNeed.setText(""+currentKwh);
				
				spinSupply.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> spinner, View v, int i, long id) {
						txtUser.setText(users.get(i));
						txtPriceKwh.setText(tariffs.get(i)+" ZE");
						txtKwhAvailable.setText(availables.get(i)+" KWH");
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				
				btnUpdate.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						updateSupplyKwh(idSupplies.get(spinSupply.getSelectedItemPosition()), Double.parseDouble(txtKwhNeed.getText().toString()));
					}
				});
			}
			
			return v;
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
						array1 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				
				sectorType = new Gson().fromJson(array.get(0), String.class);
				efficiency = new Gson().fromJson(array.get(1), Double.class);
				effectivity = new Gson().fromJson(array.get(2), Double.class);
				input = new ArrayList<String>();
				inputVal = new ArrayList<Double>();
				output = new ArrayList<String>();
				outputVal = new ArrayList<Double>();
				
				for(int i=0;i<array1.size();i++){
					input.add(new Gson().fromJson(array1.get(i), String.class));
					inputVal.add(new Gson().fromJson(array2.get(i), Double.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(5), String.class)).getAsJsonArray();
				array2 = parser.parse(new Gson().fromJson(array.get(6), String.class)).getAsJsonArray();
				
				for(int i=0;i<array1.size();i++){
					output.add(new Gson().fromJson(array1.get(i), String.class));
					outputVal.add(new Gson().fromJson(array2.get(i), Double.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(7), String.class)).getAsJsonArray();
				equipments = new ArrayList<InstallmentEquipment>();
				for(int i=0;i<array1.size();i++){
					equipments.add(new Gson().fromJson(array1.get(i), InstallmentEquipment.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(8), String.class)).getAsJsonArray();
				employees = new ArrayList<InstallmentEmployee>();
				for(int i=0;i<array1.size();i++){
					employees.add(new Gson().fromJson(array1.get(i), InstallmentEmployee.class));
				}
				
				if(sectorType.equals("Petrol Power Plant")){
					totalKwh = 0;
					tariff = new Gson().fromJson(array.get(9), Double.class);
					
					array1 = parser.parse(new Gson().fromJson(array.get(10), String.class)).getAsJsonArray();
					types = new ArrayList<String>();
					for(int i=0;i<array1.size();i++){
						types.add(new Gson().fromJson(array1.get(i), String.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(11), String.class)).getAsJsonArray();
					users = new ArrayList<String>();
					for(int i=0;i<array1.size();i++){
						users.add(new Gson().fromJson(array1.get(i), String.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(12), String.class)).getAsJsonArray();
					supplies = new ArrayList<Double>();
					for(int i=0;i<array1.size();i++){
						supplies.add(new Gson().fromJson(array1.get(i), Double.class));
						totalKwh += new Gson().fromJson(array1.get(i), Double.class);
					}
					
					totalKwh = new BigDecimal(Double.valueOf(totalKwh)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
					
					array1 = parser.parse(new Gson().fromJson(array.get(13), String.class)).getAsJsonArray();
					idSupplies = new ArrayList<String>();
					for(int i=0;i<array1.size();i++){
						idSupplies.add(new Gson().fromJson(array1.get(i), String.class));
					}
				} else {					
					array1 = parser.parse(new Gson().fromJson(array.get(9), String.class)).getAsJsonArray();
					idSupplies = new ArrayList<String>();
					for(int i=0;i<array1.size();i++){
						idSupplies.add(new Gson().fromJson(array1.get(i), String.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(10), String.class)).getAsJsonArray();
					users = new ArrayList<String>();
					for(int i=0;i<array1.size();i++){
						users.add(new Gson().fromJson(array1.get(i), String.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(11), String.class)).getAsJsonArray();
					tariffs = new ArrayList<Double>();
					for(int i=0;i<array1.size();i++){
						tariffs.add(new Gson().fromJson(array1.get(i), Double.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(12), String.class)).getAsJsonArray();
					availables = new ArrayList<Double>();
					for(int i=0;i<array1.size();i++){
						availables.add(new Gson().fromJson(array1.get(i), Double.class));
					}
					
					currentKwh = new Gson().fromJson(array.get(13), Double.class);
					currentSupply = new Gson().fromJson(array.get(14), String.class);
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				
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
	
	private class UpdateTariff extends AsyncTask<String, Void, Object>{
		double tmp;
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("id", id);
			postParameters.put("tariff", params[0]);
			tmp = Double.parseDouble(params[0]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_UPDATE_TARIFF, postParameters);
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
				tariff = tmp;
				getTabHost().setCurrentTab(0);
				setLayout(3);
			}
		}
	}
	
	private class UpdateSupplyKwh extends AsyncTask<String, Void, Object>{
		double tmp;
		String tmps;
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("id", id);
			postParameters.put("idSupply", params[0]);
			postParameters.put("supply", params[1]);
			tmp = Double.parseDouble(params[1]);
			tmps = params[0];
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_UPDATE_SUPPLY_KWH, postParameters);
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
				currentKwh = tmp;
				currentSupply = tmps;
				getTabHost().setCurrentTab(0);
				setLayout(3);
			}
		}
	}
	
	private class CancelSupplyInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("idInstallment", idE);
			postParameters.put("id", id);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CANCEL_SUPPLY_INSTALLMENT, postParameters);
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
				int tmp = idSupplies.indexOf(idE);
				types.remove(tmp);
				users.remove(tmp);
				supplies.remove(tmp);
				idSupplies.remove(tmp);
				
				totalKwh = 0;
				for(int i=0;i<supplies.size();i++){
					totalKwh += supplies.get(i);
				}
				
				totalKwh = new BigDecimal(Double.valueOf(totalKwh)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
				
				getTabHost().setCurrentTab(0);
				setLayout(3);
			}
		}
	}
}

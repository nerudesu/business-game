package com.ardhi.businessgame.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.SectorAdapter;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressLint("NewApi")
public class SectorActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private Button btnNewInstallment;
	private User user;
	private Handler h;
	private Thread t;
	private TimeSync timeSync;
	private ProgressDialog progressDialog;
	private ArrayList<String> installment,zones,id,sectors;
	private ArrayList<Double> efficiency, costs;
	private ArrayList<Integer> effectivity;
	private double price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        btnNewInstallment = (Button)findViewById(R.id.btn_new_installment);
        
        user = db.getUser();
        zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        btnNewInstallment.setOnClickListener(new OnClickHandler());
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Checking installment owned..");
            new LoadInstallmentOwnedByUser().execute();
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
        getMenuInflater().inflate(R.menu.activity_sector, menu);
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
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			Toast.makeText(SectorActivity.this, SectorActivity.this.id.get(pos), Toast.LENGTH_SHORT).show();
			Intent i = new Intent(SectorActivity.this, SectorDetailTabActivity.class);
			i.putExtra("id",SectorActivity.this.id.get(pos));
			startActivity(i);
		}
	};
	
	private class OnClickHandler implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(CommunicationService.isOnline(SectorActivity.this)){
	        	progressDialog = ProgressDialog.show(SectorActivity.this, "", "Checking sector owned..");
	            new LoadSectorOwned().execute();
	        } else {
	        	Toast.makeText(SectorActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
	        }
		}
		
	}
	
	public AlertDialog dialog(int id){
		final LayoutInflater factory;
		final View view;
		final Spinner spinSector;
		final EditText txtPrice;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sectors);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		AlertDialog dialog = null;
		switch (id) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_installment_type, null);
				spinSector = (Spinner)view.findViewById(R.id.spin_sector);
				txtPrice = (EditText)view.findViewById(R.id.txt_price);
				spinSector.setAdapter(adapter);
				spinSector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> spinner, View v, int pos, long id) {
						txtPrice.setText(price+costs.get(pos)+" ZE");
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						
					}
				});
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() >= (price+costs.get(spinSector.getSelectedItemPosition())))
							createNewInstallment(spinSector.getSelectedItem().toString());
						else Toast.makeText(SectorActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
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
	
	private void setLayout(){
    	ListView lv = (ListView)findViewById(R.id.list_sector);
    	lv.setAdapter(new SectorAdapter(this, installment, zones, efficiency, effectivity));
    	lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onItemClickHandler);
    }
	
	private void createNewInstallment(String sector){
		if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Creating new installment..");
            new CreateNewInstallment().execute(sector);
        } else {
        	Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
        }
	}
	
	private class LoadSectorOwned extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_SECTOR_OWNED+"&user="+user.getName());
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
				Toast.makeText(SectorActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(SectorActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(SectorActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				sectors = new ArrayList<String>();
				costs = new ArrayList<Double>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				
				for(int i=0;i<array1.size();i++){
					sectors.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				for(int i=0;i<array2.size();i++){
					costs.add(new Gson().fromJson(array2.get(i), Double.class));
				}
				
				price = new Gson().fromJson(array.get(2), Double.class);
				
				dialog(1).show();
			}
		}
		
	}
	
	private class CreateNewInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			postParameters.put("type", params[0]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CREATE_NEW_INSTALLMENT, postParameters);
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
				Toast.makeText(SectorActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(SectorActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(SectorActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(SectorActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
			} else {
				id = new ArrayList<String>();
				installment = new ArrayList<String>();
				zones = new ArrayList<String>();
				efficiency = new ArrayList<Double>();
				effectivity = new ArrayList<Integer>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
						array3 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray(),
						array4 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray(),
						array5 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					id.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				for(int i=0;i<array2.size();i++){
					installment.add(new Gson().fromJson(array2.get(i), String.class));
				}
				
				for(int i=0;i<array3.size();i++){
					zones.add(new Gson().fromJson(array3.get(i), String.class));
				}
				
				for(int i=0;i<array4.size();i++){
					efficiency.add(new Gson().fromJson(array4.get(i), Double.class));
				}
				
				for(int i=0;i<array5.size();i++){
					effectivity.add(new Gson().fromJson(array5.get(i), Integer.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				array3 = null;
				array4 = null;
				array5 = null;
				
				setLayout();
			}
		}
	}
	
	private class LoadInstallmentOwnedByUser extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_INSTALLMENT_OWNED_BY_USER+"&user="+user.getName());
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
				Toast.makeText(SectorActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(SectorActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(SectorActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				id = new ArrayList<String>();
				installment = new ArrayList<String>();
				zones = new ArrayList<String>();
				efficiency = new ArrayList<Double>();
				effectivity = new ArrayList<Integer>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
						array3 = parser.parse(new Gson().fromJson(array.get(2), String.class)).getAsJsonArray(),
						array4 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray(),
						array5 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					id.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				for(int i=0;i<array2.size();i++){
					installment.add(new Gson().fromJson(array2.get(i), String.class));
				}
				
				for(int i=0;i<array3.size();i++){
					zones.add(new Gson().fromJson(array3.get(i), String.class));
				}
				
				for(int i=0;i<array4.size();i++){
					efficiency.add(new Gson().fromJson(array4.get(i), Double.class));
				}
				
				for(int i=0;i<array5.size();i++){
					effectivity.add(new Gson().fromJson(array5.get(i), Integer.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				array3 = null;
				array4 = null;
				array5 = null;
				
				setLayout();
			}
		}
	}
}

package com.phionsoft.zentriumph.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.activities.adapters.SectorAdapter;
import com.phionsoft.zentriumph.models.Installment;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

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
	private ArrayList<Installment> installments;
	private double buildCost,propCost;
	private String sector;

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
        sector = getIntent().getExtras().getString("sector");
        android.util.Log.d("sector", sector);
        buildCost = getIntent().getExtras().getDouble("buildCost");
        propCost = getIntent().getExtras().getDouble("propCost");
        installments = user.getInstallmentsBySector(sector);
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        setLayout();
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
        
//        if(CommunicationService.isOnline(this)){
//        	progressDialog = ProgressDialog.show(this, "", "Checking installment owned..");
//            new LoadInstallmentOwnedByUserFromSelectedType().execute();
//        } else {
//        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
//        	finish();
//        }
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
			Intent i = new Intent(SectorActivity.this, SectorDetailTabActivity.class);
			i.putExtra("id",installments.get(pos).getId());
			startActivity(i);
		}
	};
	
	private class OnClickHandler implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			dialog(1).show();
		}
		
	}
	
	public AlertDialog dialog(int id){
		final LayoutInflater factory;
		final View view;
		AlertDialog dialog = null;
		switch (id) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_installment_type, null);
				final EditText txtSector = (EditText)view.findViewById(R.id.txt_sector),
						txtBuildCost = (EditText)view.findViewById(R.id.txt_build_cost),
						txtPropCost = (EditText)view.findViewById(R.id.txt_prop_cost);
				
				txtSector.setText(sector);
				txtBuildCost.setText(buildCost+" ZE");
				txtPropCost.setText(propCost+" ZE");
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() >= (buildCost+propCost))
							createNewInstallment();
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
    	lv.setAdapter(new SectorAdapter(this, installments));
    	lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onItemClickHandler);
    }
	
	private void createNewInstallment(){
		if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Creating new installment..");
            new CreateNewInstallment().execute();
        } else {
        	Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
        }
	}
	
	private class CreateNewInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			postParameters.put("type", sector);
			
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
//				installments = new ArrayList<Installment>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				db.addUserInstallment(new Gson().fromJson(array.get(1), Installment.class));
				user = db.getUser();

//				for(int i=0;i<array.size();i++){
//					installments.add(new Gson().fromJson(array.get(i), Installment.class));
//				}
//				
//				parser = null;
//				array = null;
				
				setLayout();
			}
		}
	}
}

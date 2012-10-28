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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

@SuppressLint("NewApi")
public class PlayerInfoActivity extends Activity {
	private DBAccess db;
	private EditText zone, money, nextTurn, txtPlayer, txtEmail,txtDob,txtAbout,txtRep;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ProgressDialog progressDialog;
	private String player,email,dob,about;
	private long rep;
	private ArrayList<String> installments, outputs;
	private ArrayList<Integer> qualities;
	private ArrayList<ArrayList<Double>> prices;
	private ListView listSector;
	private Button btnSendPM, btnMakeContract;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        txtPlayer = (EditText)findViewById(R.id.txt_player);
        txtEmail = (EditText)findViewById(R.id.txt_email);
        txtDob = (EditText)findViewById(R.id.txt_dob);
        txtAbout = (EditText)findViewById(R.id.txt_about);
        txtRep = (EditText)findViewById(R.id.txt_rep);
        listSector = (ListView)findViewById(R.id.list_sector);
        btnSendPM = (Button)findViewById(R.id.btn_send_pm);
        btnMakeContract = (Button)findViewById(R.id.btn_make_contract);
        player = getIntent().getStringExtra("player");
        
        user = db.getUser();
        zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");

        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Loading Player's info..");
        	new LoadPlayerInfo().execute();
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
        getMenuInflater().inflate(R.menu.activity_player_info, menu);
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
	
	private AlertDialog dialog(int d){
		AlertDialog dialog = null;
		final LayoutInflater factory;
		final View view;
		switch (d) {
		case R.id.btn_send_pm:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_message, null);
			final EditText txtMessage = (EditText)view.findViewById(R.id.txt_message);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Send", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!txtMessage.getText().toString().equals(""))
						doPositiveClickDialogSendPM(txtMessage.getText().toString());
				}
				
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.create();
			break;
			
		case R.id.btn_make_contract:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_make_contract, null);
			final Spinner spinProduct = (Spinner)view.findViewById(R.id.spin_product),
					spinQuality = (Spinner)view.findViewById(R.id.spin_quality);
			final EditText txtQuantity = (EditText)view.findViewById(R.id.txt_quantity),
					txtPrice = (EditText)view.findViewById(R.id.txt_price),
					txtTurn = (EditText)view.findViewById(R.id.txt_turn);
			ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, outputs);
			ArrayAdapter<Integer> qualityAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, qualities);
			productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinProduct.setAdapter(productAdapter);
			spinQuality.setAdapter(qualityAdapter);
			spinProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> spinner, View v, int i, long id) {
					txtPrice.setText(""+prices.get(i).get(spinQuality.getSelectedItemPosition()));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			spinQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> spinner, View v, int i, long id) {
					txtPrice.setText(""+prices.get(spinProduct.getSelectedItemPosition()).get(i));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Make a Contract", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!txtQuantity.getText().toString().equals("") && !txtTurn.getText().toString().equals(""))
						doPositiveClickDialogMakeContract(spinProduct.getSelectedItem().toString(), spinQuality.getSelectedItem().toString(), txtQuantity.getText().toString(), txtPrice.getText().toString(), txtTurn.getText().toString());
				}
				
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.create();
			
			break;

		default:
			break;
		}
		return dialog;
	}
	
	private void setLayout(){
		txtPlayer.setText(player);
		txtEmail.setText(email);
		txtDob.setText(dob);
		txtAbout.setText(about);
		txtRep.setText(""+rep);
		listSector.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, installments));
		listSector.setTextFilterEnabled(true);
		btnSendPM.setOnClickListener(onClickHandler);
		btnMakeContract.setOnClickListener(onClickHandler);
	}
	
	private void doPositiveClickDialogSendPM(String message){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Sending the message..");
			new SendMessage().execute(message);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogMakeContract(String product, String quality, String quantity, String price, String turn) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Making a contract..");
			new MakeContract().execute(product, quality, quantity, price, turn);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private View.OnClickListener onClickHandler = new View.OnClickListener(){
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send_pm:
				dialog(v.getId()).show();
				break;

			case R.id.btn_make_contract:
				if(prices.isEmpty())
					Toast.makeText(PlayerInfoActivity.this, "This player have no sector", Toast.LENGTH_SHORT).show();
				else dialog(v.getId()).show(); 
				break;
			}
		}
		
	};

	private class LoadPlayerInfo extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_PLAYER_INFO+"&player="+player);
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
				Toast.makeText(PlayerInfoActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(PlayerInfoActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(PlayerInfoActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray(),
						array2;
				
				email = new Gson().fromJson(array.get(0), String.class);
				dob = new Gson().fromJson(array.get(1), String.class);
				about = new Gson().fromJson(array.get(2), String.class);
				rep = new Gson().fromJson(array.get(3), Long.class);
				
				installments = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					installments.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(5), String.class)).getAsJsonArray();
				outputs = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					outputs.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(6), String.class)).getAsJsonArray();
				qualities = new ArrayList<Integer>();
				for(int i=0;i<array1.size();i++){
					qualities.add(new Gson().fromJson(array1.get(i), Integer.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(7), String.class)).getAsJsonArray();
				ArrayList<Double> tmpPrices;
				prices = new ArrayList<ArrayList<Double>>();
				for(int i=0;i<array1.size();i++){
					tmpPrices = new ArrayList<Double>();
					array2 = array1.get(i).getAsJsonArray();
					for(int j=0;j<array2.size();j++){
						tmpPrices.add(new Gson().fromJson(array2.get(j), Double.class));
					}
					prices.add(tmpPrices);
					tmpPrices = null;
				}
				
				for(int i=0;i<outputs.size();i++){
					for(int j=0;j<qualities.size();j++){
						System.out.println(outputs.get(i)+" - "+qualities.get(j)+" at "+prices.get(i).get(j));
					}
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				
				setLayout();
			}
		}
	}
	
	private class SendMessage extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("sender", user.getName());
			postParameters.put("recipient", player);
			postParameters.put("message", params[0]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_SEND_MESSAGE, postParameters);
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
			} else {
				Toast.makeText(getApplicationContext(), "Message sent..", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class MakeContract extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("supplier", player);
			postParameters.put("product", params[0]);
			postParameters.put("quality", params[1]);
			postParameters.put("quantity", params[2]);
			postParameters.put("price", params[3]);
			postParameters.put("turn", params[4]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_MAKE_CONTRACT, postParameters);
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
				Toast.makeText(getApplicationContext(), "This player doesn't have a storage in this zone..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("2")){
				Toast.makeText(getApplicationContext(), "You don't have a storage in this zone.. Please built it first..", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Contract proposed..", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

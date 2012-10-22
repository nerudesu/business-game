package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.SectorAdapter;
import com.ardhi.businessgame.activities.adapters.StorageEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.StorageEquipmentOfferAdapter;
import com.ardhi.businessgame.activities.adapters.StorageProductAdapter;
import com.ardhi.businessgame.activities.adapters.StorageProductOfferAdapter;
import com.ardhi.businessgame.models.Installment;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.StorageEquipment;
import com.ardhi.businessgame.models.StorageProduct;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;

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
	private double capacity, fill, price;
	private ArrayList<StorageProduct> storageProducts;
	private ArrayList<MarketProduct> marketProducts;
	private ArrayList<StorageEquipment> storageEquipments;
	private ArrayList<MarketEquipment> marketEquipments;
	private ArrayList<Installment> installments;
	private ArrayList<String> marketZone;

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
        	progressDialog = ProgressDialog.show(this, "", "Checking user's storage..");
    		new CheckUserStorage().execute();
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
    
    public AlertDialog dialog(int d, final String id, double size){
    	AlertDialog dialog = null;
    	final LayoutInflater factory;
		final View view;
		final EditText txtPrice;
		final Spinner spinMarket;
		ArrayAdapter<String> adapter;
    	switch (d) {
		case 1:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_storage, null);
			TextView txtQuestion = (TextView)view.findViewById(R.id.question_storage);
			String text = getString(R.string.question_storage);
			txtQuestion.setText(String.format(text, price));
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(user.getMoney() >= price)
						doPositiveClickDialogBuildStorage();
					else {
						Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_LONG).show();
						finish();
					}
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setCancelable(false)
			.create();
			break;
			
		case 2:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_product_sell, null);
			final SeekBar s = (SeekBar)view.findViewById(R.id.seek_size);
			final TextView txtMin = (TextView)view.findViewById(R.id.txt_min),
					txtMax = (TextView)view.findViewById(R.id.txt_max),
					txtOffered = (TextView)view.findViewById(R.id.txt_offered),
					txtTotal = (TextView)view.findViewById(R.id.txt_total);
			txtPrice = (EditText)view.findViewById(R.id.txt_price);
			spinMarket = (Spinner)view.findViewById(R.id.spin_market);
			
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marketZone);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinMarket.setAdapter(adapter);
			txtMin.setText("0");
			txtMax.setText(""+size);
			txtOffered.setText("Offered : 0.0 CBM");
			txtTotal.setText("Total : 0 ZE");
			txtPrice.setText(""+price);
			s.setMax((int)(size*100));
			s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					txtOffered.setText("Offered : "+(double)progress/100+" CBM");
					txtTotal.setText("Total : "+new BigDecimal(Double.valueOf(((double)progress/100)*price)).setScale(2, BigDecimal.ROUND_HALF_EVEN)+" ZE");
				}
			});
			txtPrice.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					price=Double.parseDouble(txtPrice.getText().toString());
					txtTotal.setText("Total : "+new BigDecimal(Double.valueOf(((double)s.getProgress()/100)*price)).setScale(2, BigDecimal.ROUND_HALF_EVEN)+" ZE");
				}
				
				@Override
				public void beforeTextChanged(CharSequence cs, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable e) {
					// TODO Auto-generated method stub
					
				}
			});
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(s.getProgress() > 0){
						doPositiveClickDialogSellProduct(id, (double)s.getProgress()/100, spinMarket.getSelectedItem().toString());
					}
					txtOffered.setText("Offered : 0.0 CBM");
					txtTotal.setText("Total : 0 ZE");
					s.setProgress(0);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					txtOffered.setText("Offered : 0.0 CBM");
					txtTotal.setText("Total : 0 ZE");
					s.setProgress(0);
				}
			})
			.setCancelable(false)
			.create();
			break;

		case 3:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_equipment_sell, null);
			txtPrice = (EditText)view.findViewById(R.id.txt_price);
			txtPrice.setText(""+price);
			spinMarket = (Spinner)view.findViewById(R.id.spin_market);
			
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marketZone);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinMarket.setAdapter(adapter);
			txtPrice.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					price=Double.parseDouble(txtPrice.getText().toString());
				}
				
				@Override
				public void beforeTextChanged(CharSequence cs, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable e) {
					// TODO Auto-generated method stub
					
				}
			});
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogSellEquipment(id, spinMarket.getSelectedItem().toString());
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;
			
		case 4 :
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_equipment_attach_employee_hire, null);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setAdapter(new SectorAdapter(this, installments), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogAttachEquipment(id, installments.get(which).getId());
				}
			})
			.create();
			break;
			
		case 5:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question, null);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogCancelOfferProduct(id);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;
			
		case 6:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question, null);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doPositiveClickDialogCancelOfferEquipment(id);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;
			
		case 7:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_equipment_fix, null);
			txtPrice = (EditText)view.findViewById(R.id.txt_price);
			final EditText txtDesc = (EditText)view.findViewById(R.id.txt_desc);
			
			txtPrice.setText(price+" ZE");
			txtDesc.setText("95%");
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(user.getMoney() < price)
						Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_LONG).show();
					else doPositiveClickDialogFixEquipment(id);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			break;
		}
    	return dialog;
    }
    
    private void doPositiveClickDialogFixEquipment(String id){
    	if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new FixEquipment().execute(id);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
    }

    private void doPositiveClickDialogCancelOfferEquipment(String id) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new CancelOfferEquipment().execute(id);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
    
	private void doPositiveClickDialogCancelOfferProduct(String id) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new CancelOfferProduct().execute(id);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}

	private void doPositiveClickDialogAttachEquipment(String idEq, String idIns) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new AttachEquipmentToInstallment().execute(idEq,idIns);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
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
	
	public void showSellDialog(int d, String i, double s) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Get suggested price..");
			new GetSuggestedPrice().execute(""+d, i, ""+s);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showAttachDialog(String i){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Get available installment..");
			new LoadInstallmentOwnedByEquipment().execute(i);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showFixDialog(String i){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Calculating fix price..");
			new CalculateFixPrice().execute(i);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogBuildStorage(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(StorageTabActivity.this, "", "Building user's storage..");
			new BuildUserStorage().execute();
		}  else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
		}
	}
	
	private void doPositiveClickDialogSellProduct(String id, double offered, String zone) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			android.util.Log.d("zone", zone);
			new SellStorageProduct().execute(id, ""+offered, zone);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void doPositiveClickDialogSellEquipment(String id, String zone) {
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new SellStorageEquipment().execute(id, zone);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void setLayout(int pos){
		txtCapacity.setText((int)fill+"/"+(int)capacity);
		progressCapacity.setMax((int)capacity);
		progressCapacity.setProgress((int)fill);
		
		TabHost.TabSpec spec;
		getTabHost().setCurrentTab(0);
		getTabHost().clearAllTabs();

		spec = getTabHost().newTabSpec("Product").setIndicator("Product", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabStorageProduct());
        getTabHost().addTab(spec);

		spec = getTabHost().newTabSpec("Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabStorageEquipment());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Offered Product").setIndicator("Product", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabOfferedProduct());
        getTabHost().addTab(spec);

		spec = getTabHost().newTabSpec("Offered Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabOfferedEquipment());
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(pos);
	}
	
	private class TabStorageProduct implements TabHost.TabContentFactory {
		StorageTabActivity a;
		
		public TabStorageProduct() {
			a = StorageTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new StorageProductAdapter(a, storageProducts));
			lv.setTextFilterEnabled(true);
			return lv;
		}
	}
	
	private class TabStorageEquipment implements TabHost.TabContentFactory {
		StorageTabActivity a;
		
		public TabStorageEquipment() {
			a = StorageTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new StorageEquipmentAdapter(a, storageEquipments));
			lv.setTextFilterEnabled(true);
			return lv;
		}
	}
	
	private class TabOfferedProduct implements TabHost.TabContentFactory {
		StorageTabActivity a;
		
		public TabOfferedProduct() {
			a = StorageTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new StorageProductOfferAdapter(a, marketProducts));
			lv.setTextFilterEnabled(true);
			return lv;
		}
	}
	
	private class TabOfferedEquipment implements TabHost.TabContentFactory {
		StorageTabActivity a;
		
		public TabOfferedEquipment() {
			a = StorageTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new StorageEquipmentOfferAdapter(a, marketEquipments));
			lv.setTextFilterEnabled(true);
			return lv;
		}
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
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1;
				boolean isAvailable = new Gson().fromJson(array.get(0), Boolean.class);
				
				if(isAvailable){
					capacity = new Gson().fromJson(array.get(1), Double.class);
					fill = new Gson().fromJson(array.get(2), Double.class);

					array1 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray();
					storageProducts = null;
					storageProducts = new ArrayList<StorageProduct>();
					for(int i=0;i<array1.size();i++){
						storageProducts.add(new Gson().fromJson(array1.get(i), StorageProduct.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
					storageEquipments = null;
					storageEquipments = new ArrayList<StorageEquipment>();
					for(int i=0;i<array1.size();i++){
						storageEquipments.add(new Gson().fromJson(array1.get(i), StorageEquipment.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(5), String.class)).getAsJsonArray();
					marketProducts = null;
					marketProducts = new ArrayList<MarketProduct>();
					for(int i=0;i<array1.size();i++){
						marketProducts.add(new Gson().fromJson(array1.get(i), MarketProduct.class));
					}
					
					array1 = parser.parse(new Gson().fromJson(array.get(6), String.class)).getAsJsonArray();
					marketEquipments = null;
					marketEquipments = new ArrayList<MarketEquipment>();
					for(int i=0;i<array1.size();i++){
						marketEquipments.add(new Gson().fromJson(array1.get(i), MarketEquipment.class));
					}
					
					parser = null;
					array = null;
					array1 = null;
					
					setLayout(0);
				} else {
					price = new Gson().fromJson(array.get(1), Double.class);
					dialog(1, "", 0).show();
				}
			}
		}
	}
	
	private class GetSuggestedPrice extends AsyncTask<String, Void, Object>{
		int d;
		String id;
		double s;
		
		@Override
		protected Object doInBackground(String... params) {
			d = Integer.parseInt(params[0]);
			id = params[1];
			s = Double.parseDouble(params[2]);
			try {
				return CommunicationService.get(CommunicationService.GET_GET_SUGGESTED_PRICE+"&user="+user.getName()+"&id="+id);
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
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				price = new Gson().fromJson(array.get(0), Double.class);
				marketZone = null;
				marketZone = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					marketZone.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				
				dialog(d, id, s).show();
			}
		}
	}
	
	private class LoadInstallmentOwnedByEquipment extends AsyncTask<String, Void, Object>{
		String i;
		
		@Override
		protected Object doInBackground(String... params) {
			i = params[0];
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_INSTALLMENT_OWNED_BY_EQUIPMENT+"&user="+user.getName()+"&id="+i);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				installments = new ArrayList<Installment>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();

				for(int i=0;i<array.size();i++){
					installments.add(new Gson().fromJson(array.get(i), Installment.class));
				}
				
				parser = null;
				array = null;
				
				dialog(4, i, 0).show();
			}
		}
	}
	
	private class CalculateFixPrice extends AsyncTask<String, Void, Object>{
		String id;
		
		@Override
		protected Object doInBackground(String... params) {
			id = params[0];
			try {
				return CommunicationService.get(CommunicationService.GET_CALCULATE_FIX_PRICE+"&id="+id);
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
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal Error..", Toast.LENGTH_SHORT).show();
			} else {
				price = new Gson().fromJson(res.toString(), Double.class);
				dialog(7, id, 0).show();
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
				array1 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray();
				storageProducts = null;
				storageProducts = new ArrayList<StorageProduct>();
				for(int i=0;i<array1.size();i++){
					storageProducts.add(new Gson().fromJson(array1.get(i), StorageProduct.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				storageEquipments = null;
				storageEquipments = new ArrayList<StorageEquipment>();
				for(int i=0;i<array1.size();i++){
					storageEquipments.add(new Gson().fromJson(array1.get(i), StorageEquipment.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(5), String.class)).getAsJsonArray();
				marketProducts = null;
				marketProducts = new ArrayList<MarketProduct>();
				for(int i=0;i<array1.size();i++){
					marketProducts.add(new Gson().fromJson(array1.get(i), MarketProduct.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(6), String.class)).getAsJsonArray();
				marketEquipments = null;
				marketEquipments = new ArrayList<MarketEquipment>();
				for(int i=0;i<array1.size();i++){
					marketEquipments.add(new Gson().fromJson(array1.get(i), MarketEquipment.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				
				setLayout(0);
			}
		}
	}
	
	private class SellStorageProduct extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("productId", params[0]);
			postParameters.put("offer", params[1]);
			postParameters.put("price", ""+price);
			postParameters.put("zone", params[2]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_SELL_STORAGE_PRODUCT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(StorageTabActivity.this, "The offered product's size is bigger than available product", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("2")){
				Toast.makeText(StorageTabActivity.this, "The offered product's price is too low. You should offer it not less than 25% of the suggested price..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("3")){
				Toast.makeText(StorageTabActivity.this, "The offered product's price is too high. You should offer it not more than 25% of the suggested price..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				storageProducts = null;
				storageProducts = new ArrayList<StorageProduct>();
				for(int i=0;i<array1.size();i++){
					storageProducts.add(new Gson().fromJson(array1.get(i), StorageProduct.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				marketProducts = null;
				marketProducts = new ArrayList<MarketProduct>();
				for(int i=0;i<array1.size();i++){
					marketProducts.add(new Gson().fromJson(array1.get(i), MarketProduct.class));
				}
				
				setLayout(0);
			}
		}
	}
	
	private class SellStorageEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("equipmentId", params[0]);
			postParameters.put("price", ""+price);
			postParameters.put("marketZone", params[1]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_SELL_STORAGE_EQUIPMENT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				
				storageEquipments = null;
				storageEquipments = new ArrayList<StorageEquipment>();
				for(int i=0;i<array1.size();i++){
					storageEquipments.add(new Gson().fromJson(array1.get(i), StorageEquipment.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				marketEquipments = null;
				marketEquipments = new ArrayList<MarketEquipment>();
				for(int i=0;i<array1.size();i++){
					marketEquipments.add(new Gson().fromJson(array1.get(i), MarketEquipment.class));
				}
				
				setLayout(1);
			}
		}
	}
	
	private class AttachEquipmentToInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("idEquipment", params[0]);
			postParameters.put("idInstallment", params[1]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_ATTACH_EQUIPMENT_TO_INSTALLMENT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				
				storageEquipments = null;
				storageEquipments = new ArrayList<StorageEquipment>();
				for(int i=0;i<array.size();i++){
					storageEquipments.add(new Gson().fromJson(array.get(i), StorageEquipment.class));
				}
				
				setLayout(1);
			}
		}
	}
	
	private class CancelOfferProduct extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			postParameters.put("id", params[0]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CANCEL_OFFER_PRODUCT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				storageProducts = null;
				storageProducts = new ArrayList<StorageProduct>();
				for(int i=0;i<array1.size();i++){
					storageProducts.add(new Gson().fromJson(array1.get(i), StorageProduct.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				marketProducts = null;
				marketProducts = new ArrayList<MarketProduct>();
				for(int i=0;i<array1.size();i++){
					marketProducts.add(new Gson().fromJson(array1.get(i), MarketProduct.class));
				}
				
				setLayout(2);
			}
		}
	}
	
	private class CancelOfferEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			postParameters.put("id", params[0]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CANCEL_OFFER_EQUIPMENT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				
				storageEquipments = null;
				storageEquipments = new ArrayList<StorageEquipment>();
				for(int i=0;i<array1.size();i++){
					storageEquipments.add(new Gson().fromJson(array1.get(i), StorageEquipment.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				marketEquipments = null;
				marketEquipments = new ArrayList<MarketEquipment>();
				for(int i=0;i<array1.size();i++){
					marketEquipments.add(new Gson().fromJson(array1.get(i), MarketEquipment.class));
				}
				
				setLayout(3);
			}
		}
	}
	
	private class FixEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("id", params[0]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_FIX_EQUIPMENT, postParameters);
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
				Toast.makeText(StorageTabActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(getApplicationContext(), "Insufficient money..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				storageEquipments = null;
				storageEquipments = new ArrayList<StorageEquipment>();
				for(int i=0;i<array1.size();i++){
					storageEquipments.add(new Gson().fromJson(array1.get(i), StorageEquipment.class));
				}
				
				setLayout(1);
			}
		}
	}
}

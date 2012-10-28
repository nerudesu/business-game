package com.phionsoft.zentriumph.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.activities.adapters.ContractAdapter;
import com.phionsoft.zentriumph.activities.adapters.PendingContractAdapter;
import com.phionsoft.zentriumph.models.Contract;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

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
	private ArrayList<String> sectors, products, advertises, idAds,players;
	private ArrayList<Double> prices;
	private ArrayList<Integer> sectorsLvl;
	private ArrayList<Contract> contracts,pendingContracts;
	private double price,sales,raw,electricity,fixed,wage,operation,transport,retribution,advertisement,interest,depreciation,tax,
		cash,rawOnStorage = 0,equipmentOnStorage = 0,loan = 0,storage = 0,equipment = 0,sector;
	private int sectorPos,tabPos;

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
        tabPos = 0;
        
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
			progressDialog = ProgressDialog.show(this, "", "Loading Headquarter's data..");
			new LoadHeadquarterData().execute();
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
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
			if(user.getSectorBlueprints().containsKey(parent.getItemAtPosition(pos).toString())){
				Toast.makeText(HeadquarterTabActivity.this, "You have sector "+parent.getItemAtPosition(pos).toString()+"'s blueprint", Toast.LENGTH_SHORT).show();
			} else {
				if(user.getLevel() < sectorsLvl.get(pos)){
					Toast.makeText(HeadquarterTabActivity.this, "Your level is lower than the sector's required level..", Toast.LENGTH_SHORT).show();
				} else {
					sectorPos = pos;
					dialog(1,0,0,0,"").show();
				}
			}
		}
	};
	
	private AdapterView.OnItemClickListener onItemClickHandlerPlayer = new AdapterView.OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
    		if(players.get(pos).equals(user.getName()))
    			Toast.makeText(HeadquarterTabActivity.this, "This is your account", Toast.LENGTH_SHORT).show();
    		else {
    			tabPos = 6;
    			Intent i = new Intent(HeadquarterTabActivity.this, PlayerInfoActivity.class);
    			i.putExtra("player",players.get(pos));
    			startActivity(i);
    		}
		}
	};
	
	public AlertDialog dialog(int d, final int productPos, final int adsPos, final int turn, final String idContract){
		final LayoutInflater factory;
		final View view;
		AlertDialog dialog = null;
		switch (d) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_sector_blueprint_buy, null);
				TextView txtSector = (TextView)view.findViewById(R.id.txt_sector),
						txtPrice = (TextView)view.findViewById(R.id.txt_price);
				txtSector.setText(sectors.get(sectorPos));
				txtPrice.setText((price*(user.getSectorBlueprints().size())*sectorsLvl.get(sectorPos))+" ZE");
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < (price*(user.getSectorBlueprints().size())*sectorsLvl.get(sectorPos)))
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
				
			case 2:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_advertise, null);
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < prices.get(adsPos)*turn)
							Toast.makeText(HeadquarterTabActivity.this, "Insufficient money..", Toast.LENGTH_SHORT).show();
						else doPositiveClickDialogAdvertise(productPos, adsPos, turn);
					}
					
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.create();
				break;
				
			case R.id.btn_accept_confirm:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question, null);
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doPositiveClickDialogConfirmContract(idContract);
					}
					
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.create();
				break;
				
			case R.id.btn_cancel_reject :
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question, null);
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doPositiveClickDialogCancelRejectContract(idContract);
					}
					
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
	
	private void doPositiveClickDialogAdvertise(int productPos, int adsPos, int turn){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new AdvertiseProduct().execute(""+productPos,""+adsPos,""+turn);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogConfirmContract(String idContract){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new ConfirmContract().execute(idContract);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogCancelRejectContract(String idContract){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new CancelRejectContract().execute(idContract);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setLayout(){
		TabHost.TabSpec spec;
		
		getTabHost().setCurrentTab(0);
		getTabHost().clearAllTabs();
		
		spec = getTabHost().newTabSpec("Sectors").setIndicator("Sector Unlocked", null).setContent(new TabSectorUnlocked());
        getTabHost().addTab(spec);
        
		spec = getTabHost().newTabSpec("Contracts").setIndicator("Contract List", null).setContent(new TabContractList());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Pending Contracts").setIndicator("Pending Contract List", null).setContent(new TabPendingContractList());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Advertisements").setIndicator("Advertisement", null).setContent(new TabAdvertisement());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Financials").setIndicator("Financial", null).setContent(new TabFinancial());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Assets").setIndicator("Assets", null).setContent(new TabAssets());
        getTabHost().addTab(spec);
        
        spec = getTabHost().newTabSpec("Players").setIndicator("Players", null).setContent(new TabPlayers());
        getTabHost().addTab(spec);
        
        getTabHost().setCurrentTab(tabPos);
        
        if(user.getSectorBlueprints().size() < 1)
        	Toast.makeText(this, "You are given one level 1 blueprint for free. Go get it now..", Toast.LENGTH_LONG).show();
        if(user.getSectorBlueprints().size() == sectors.size())
        	Toast.makeText(this, "You own all the sector's blueprint..", Toast.LENGTH_LONG).show();
	}
	
	private class TabSectorUnlocked implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabSectorUnlocked(){
			a = HeadquarterTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new ArrayAdapter<String>(a, android.R.layout.simple_list_item_1, sectors));
			lv.setOnItemClickListener(onItemClickHandlerSector);
			lv.setTextFilterEnabled(true);
			return lv;
		}
		
	}
	
	private class TabContractList implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabContractList(){
			a = HeadquarterTabActivity.this;
		}

		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new ContractAdapter(a, contracts));
			lv.setTextFilterEnabled(true);
			return lv;
		}
		
	}
	
	private class TabPendingContractList implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabPendingContractList(){
			a = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new PendingContractAdapter(a, pendingContracts));
			lv.setTextFilterEnabled(true);
			return lv;
		}
		
	}
	
	private class TabAdvertisement implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabAdvertisement() {
			a = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.extended_advertisement, null);
			final Spinner spinProduct = (Spinner)v.findViewById(R.id.spin_product),
					spinAdsType = (Spinner)v.findViewById(R.id.spin_ads_type);
			final EditText txtTurn = (EditText)v.findViewById(R.id.txt_turn);
			Button btnAdvertise = (Button)v.findViewById(R.id.btn_advertise);
			
			ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, products),
					adsTypeAdapter = new ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, advertises);
			productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adsTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinProduct.setAdapter(productAdapter);
			spinAdsType.setAdapter(adsTypeAdapter);
			btnAdvertise.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!txtTurn.getText().toString().equals(""))
						dialog(2, spinProduct.getSelectedItemPosition(), spinAdsType.getSelectedItemPosition(), Integer.parseInt(txtTurn.getText().toString()), "").show();
				}
			});
			return v;
		}
	}
	
	private class TabFinancial implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabFinancial(){
			a = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.extended_financial, null);
			EditText txtSales = (EditText)v.findViewById(R.id.txt_sales),
					txtRaw = (EditText)v.findViewById(R.id.txt_raw_material),
					txtElectricity = (EditText)v.findViewById(R.id.txt_electricity),
					txtFixed = (EditText)v.findViewById(R.id.txt_fixed_cost),
					txtWage = (EditText)v.findViewById(R.id.txt_wage),
					txtOperation = (EditText)v.findViewById(R.id.txt_operation),
					txtTransport = (EditText)v.findViewById(R.id.txt_transport),
					txtRetribution = (EditText)v.findViewById(R.id.txt_retribution),
					txtAdvertisement = (EditText)v.findViewById(R.id.txt_advertisement),
					txtInterest = (EditText)v.findViewById(R.id.txt_bank_interest),
					txtDepreciation = (EditText)v.findViewById(R.id.txt_depreciation),
					txtProfitBeforeTax = (EditText)v.findViewById(R.id.txt_profit_before_tax),
					txtTax = (EditText)v.findViewById(R.id.txt_tax),
					txtProfit = (EditText)v.findViewById(R.id.txt_profit);
			
			txtSales.setText(sales+" ZE");
			txtRaw.setText(raw+" ZE");
			txtElectricity.setText(electricity+" ZE");
			txtFixed.setText(fixed+" ZE");
			txtWage.setText(wage+" ZE");
			txtOperation.setText(operation+" ZE");
			txtTransport.setText(transport+" ZE");
			txtRetribution.setText(retribution+" ZE");
			txtAdvertisement.setText(advertisement+" ZE");
			txtInterest.setText(interest+" ZE");
			txtDepreciation.setText(depreciation+" ZE");
			double tmp = sales+raw+electricity+fixed+wage+operation+transport+retribution+advertisement+interest+depreciation;
			txtProfitBeforeTax.setText(tmp+" ZE");
			if(tmp > 0)
				tax *= tmp*-1;
			else tax = 0;
			txtTax.setText(tax+" ZE");
			txtProfit.setText((tmp+tax)+" ZE");
			return v;
		}
		
	}
	
	private class TabAssets implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabAssets(){
			a = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			LayoutInflater inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.extended_asset, null);
			EditText txtCash = (EditText)v.findViewById(R.id.txt_cash),
				txtRawOnStorage = (EditText)v.findViewById(R.id.txt_raw_material),
				txtEquipmentOnStorage = (EditText)v.findViewById(R.id.txt_equipment_on_storage),
				txtLoan = (EditText)v.findViewById(R.id.txt_loan),
				txtStorage = (EditText)v.findViewById(R.id.txt_storage),
				txtEquipment = (EditText)v.findViewById(R.id.txt_equipment_attached),
				txtSector = (EditText)v.findViewById(R.id.txt_sector),
				txtTotal = (EditText)v.findViewById(R.id.txt_total_assets);
			
			txtCash.setText(cash+" ZE");
			txtRawOnStorage.setText(rawOnStorage+" ZE");
			txtEquipmentOnStorage.setText(equipmentOnStorage+" ZE");
			txtLoan.setText(loan+" ZE");
			txtStorage.setText(storage+" ZE");
			txtEquipment.setText(equipment+" ZE");
			txtSector.setText(sector+" ZE");
			txtTotal.setText((cash+rawOnStorage+equipmentOnStorage+loan+storage+equipment+sector)+" ZE");
			return v;
		}
	}
	
	private class TabPlayers implements TabHost.TabContentFactory {
		HeadquarterTabActivity a;
		
		public TabPlayers() {
			a = HeadquarterTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ListView lv = new ListView(a);
			lv.setAdapter(new ArrayAdapter<String>(a, android.R.layout.simple_list_item_1, players));
			lv.setOnItemClickListener(onItemClickHandlerPlayer);
			lv.setTextFilterEnabled(true);
			return lv;
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
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();
				sectors = new ArrayList<String>();
				sectorsLvl = new ArrayList<Integer>();
				contracts = new ArrayList<Contract>();
				pendingContracts = new ArrayList<Contract>();
				for(int i=0;i<array1.size();i++){
					sectors.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					sectorsLvl.add(new Gson().fromJson(array1.get(i), Integer.class));
				}
				
				price = new Gson().fromJson(array.get(2), Double.class);
				
				array1 = parser.parse(new Gson().fromJson(array.get(3), String.class)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					contracts.add(new Gson().fromJson(array1.get(i), Contract.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					pendingContracts.add(new Gson().fromJson(array1.get(i), Contract.class));
				}
				
				sales = new Gson().fromJson(array.get(5), Double.class);
				raw = new Gson().fromJson(array.get(6), Double.class);
				electricity = new Gson().fromJson(array.get(7), Double.class);
				fixed = new Gson().fromJson(array.get(8), Double.class);
				wage = new Gson().fromJson(array.get(9), Double.class);
				operation = new Gson().fromJson(array.get(10), Double.class);
				transport = new Gson().fromJson(array.get(11), Double.class);
				retribution = new Gson().fromJson(array.get(12), Double.class);
				advertisement = new Gson().fromJson(array.get(13), Double.class);
				interest = new Gson().fromJson(array.get(14), Double.class);
				depreciation = new Gson().fromJson(array.get(15), Double.class);
				tax = new Gson().fromJson(array.get(16), Double.class);
				cash = new Gson().fromJson(array.get(17), Double.class);
				rawOnStorage = new Gson().fromJson(array.get(18), Double.class);
				equipmentOnStorage = new Gson().fromJson(array.get(19), Double.class);
				loan = new Gson().fromJson(array.get(20), Double.class);
				storage = new Gson().fromJson(array.get(21), Double.class);
				equipment = new Gson().fromJson(array.get(22), Double.class);
				sector = new Gson().fromJson(array.get(23), Double.class);
				
				array1 = parser.parse(new Gson().fromJson(array.get(24), String.class)).getAsJsonArray();
				products = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					products.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(25), String.class)).getAsJsonArray();
				idAds = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					idAds.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(26), String.class)).getAsJsonArray();
				advertises = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					advertises.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(27), String.class)).getAsJsonArray();
				prices = new ArrayList<Double>();
				for(int i=0;i<array1.size();i++){
					prices.add(new Gson().fromJson(array1.get(i), Double.class));
				}
				
				array1 = parser.parse(new Gson().fromJson(array.get(28), String.class)).getAsJsonArray();
				players = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					players.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				
				setLayout();
			}
		}
	}
	
	private class BuySectorBlueprint extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("sector", sectors.get(sectorPos));
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
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				
				db.addUserSectorBlueprintCost(sectors.get(sectorPos), new Gson().fromJson(array.get(1), String.class), new Gson().fromJson(array.get(2), Double.class));
				if(user.getLevel() == sectorsLvl.get(sectorPos))
					user.setLevel(sectorsLvl.get(sectorPos)+1);
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				user = db.getUser();
				
//				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
//				if(user.getLevel() == sectorsLvl.get(sectorPos))
//					user.setLevel(sectorsLvl.get(sectorPos)+1);
//				userSectors.add(sectors.get(sectorPos));
				setLayout();
			}
		}
	}
	
	private class AdvertiseProduct extends AsyncTask<String, Void, Object>{
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("product", products.get(Integer.parseInt(params[0])));
			postParameters.put("ads", idAds.get(Integer.parseInt(params[1])));
			postParameters.put("turn", params[2]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_ADVERTISE_PRODUCT, postParameters);
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
			} else {
				user.setMoney(new Gson().fromJson(res.toString(), Double.class));
				db.updateUserData(user);
				Toast.makeText(getApplicationContext(), "The selected product has been advertised..", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class ConfirmContract extends AsyncTask<String, Void, Object>{
		private String id;
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("id", params[0]);
			id = params[0];
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CONFIRM_CONTRACT, postParameters);
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
				for(Contract pendingContract : pendingContracts){
					if(pendingContract.getId().equals(id)){
						pendingContracts.remove(pendingContract);
						contracts.add(pendingContract);
						break;
					} else continue;
				}
				
				tabPos = 2;
				setLayout();
			}
		}
	}
	
	private class CancelRejectContract extends AsyncTask<String, Void, Object>{
		private String id;
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("id", params[0]);
			postParameters.put("user", user.getName());
			id = params[0];
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_CANCEL_REJECT_CONTRACT, postParameters);
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
				for(Contract contract : contracts){
					if(contract.getId().equals(id)){
						contracts.remove(contract);
						tabPos = 1;
						break;
					} else continue;
				}
				
				for(Contract pendingContract : pendingContracts){
					if(pendingContract.getId().equals(id)){
						pendingContracts.remove(pendingContract);
						tabPos = 2;
						break;
					} else continue;
				}
				
				setLayout();
			}
		}
	}
}

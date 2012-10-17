package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.MarketEmployeeAdapter;
import com.ardhi.businessgame.activities.adapters.MarketEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.MarketProductAdapter;
import com.ardhi.businessgame.activities.adapters.SectorAdapter;
import com.ardhi.businessgame.models.Installment;
import com.ardhi.businessgame.models.MarketEmployee;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class MarketTabContentActivity extends Activity {
	private DBAccess db;
	private User user;
	private LinearLayout lin;
	private String tab, data, id, name, installmentType;
	private double size, price, durability, operational, total;
	private int quality;
	private ProgressDialog progressDialog;
	private ArrayList<Installment> installments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lin = new LinearLayout(this);
		db = new DBAccess(this);
		user = db.getUser();
		tab = getIntent().getStringExtra("Tab");
		data = getIntent().getStringExtra("Data");
		
		lin.setOrientation(LinearLayout.VERTICAL);
		lin.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		lin.setId(100);
		setContentView(lin);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lin = (LinearLayout)findViewById(100);
		lin.removeAllViews();
		setLayout();
	}
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
    		switch (pos) {
			case 0:
				doClickBundleDefault();
				break;

			default:
				break;
			}
		}
	};
	
	public AlertDialog dialog(String i, int d, String n, double si, double p, double dur, int q, double ops){
		id = i;
		name = n;
		size = si;
		price = p;
		durability = dur;
		quality = q;
		operational = ops;
		final LayoutInflater factory;
		final View view;
		final TextView txtPrice,txtOperational;
		final RatingBar rateQuality;
		AlertDialog dialog = null;
		switch (d) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_product_buy, null);
				final SeekBar s = (SeekBar)view.findViewById(R.id.seek_size);
				final TextView txtMin = (TextView)view.findViewById(R.id.txt_min),
						txtMax = (TextView)view.findViewById(R.id.txt_max),
						txtPicked = (TextView)view.findViewById(R.id.txt_picked),
						txtTotal = (TextView)view.findViewById(R.id.txt_total);
				txtMin.setText("0");
				txtMax.setText(""+size);
				txtPicked.setText("Picked : 0.0 CBM");
				txtTotal.setText("Total : 0 ZE");
				s.setMax((int)(size*100));
				s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						txtPicked.setText("Picked : "+(double)progress/100+" CBM");
						txtTotal.setText("Total : "+new BigDecimal(Double.valueOf(((double)progress/100)*price)).setScale(2, BigDecimal.ROUND_HALF_EVEN)+" ZE");
					}
				});
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(s.getProgress() > 0){
							if(user.getMoney() < ((double)s.getProgress()/100)*price){
								Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
							} else {
								doPositiveClickDialog(MarketTabContentActivity.this.id,(double)s.getProgress()/100);
							}
						}
						txtPicked.setText("Picked : 0.0 CBM");
						txtTotal.setText("Total : 0 ZE");
						s.setProgress(0);
						MarketTabContentActivity.this.id = "";
						MarketTabContentActivity.this.size = 0;
						MarketTabContentActivity.this.price = 0;
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtPicked.setText("Picked : 0.0 CBM");
						txtTotal.setText("Total : 0 ZE");
						s.setProgress(0);
						MarketTabContentActivity.this.id = "";
						MarketTabContentActivity.this.size = 0;
						MarketTabContentActivity.this.price = 0;
					}
					
				})
				.setCancelable(false)
				.create();
				break;
			case 2:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_equipment_buy, null);
				final TextView txtEquipment = (TextView)view.findViewById(R.id.txt_equipment),
						txtDurability = (TextView)view.findViewById(R.id.txt_durability);
				txtPrice = (TextView)view.findViewById(R.id.txt_price);
				txtOperational = (TextView)view.findViewById(R.id.txt_operational);
				rateQuality = (RatingBar)view.findViewById(R.id.rate_quality);
				txtEquipment.setText(name);
				txtDurability.setText(""+durability);
				txtOperational.setText(operational+" ZE");
				rateQuality.setRating(quality);
				txtPrice.setText(price+" ZE");
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Buy", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < price){
							Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
						} else if(user.getStorages().get(user.getZone()) == null){
							Toast.makeText(MarketTabContentActivity.this, "You have no storage in market's zone. Please build it first...", Toast.LENGTH_LONG).show();
						} else {
							doPositiveClickDialogEquipment(MarketTabContentActivity.this.id);
						}
						
						MarketTabContentActivity.this.id = "";
						MarketTabContentActivity.this.name = "";
						MarketTabContentActivity.this.quality = 0;
						MarketTabContentActivity.this.durability = 0;
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MarketTabContentActivity.this.id = "";
						MarketTabContentActivity.this.name = "";
						MarketTabContentActivity.this.quality = 0;
						MarketTabContentActivity.this.durability = 0;
					}
					
				})
				.create();
				break;
			case 3:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_employee_hire, null);
				final TextView txtEmployee = (TextView)view.findViewById(R.id.txt_employee);
				txtPrice = (TextView)view.findViewById(R.id.txt_price);
				txtOperational = (TextView)view.findViewById(R.id.txt_operational);
				rateQuality = (RatingBar)view.findViewById(R.id.rate_quality);
				txtEmployee.setText(name);
				txtOperational.setText(operational+" ZE");
				rateQuality.setRating(quality);
				txtPrice.setText(price+" ZE");
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Hire", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < price){
							Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
						} else {
							doPositiveClickDialogEmployee();
						}
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MarketTabContentActivity.this.id = "";
						MarketTabContentActivity.this.name = "";
						MarketTabContentActivity.this.quality = 0;
						MarketTabContentActivity.this.durability = 0;
					}
					
				})
				.create();
				break;
			case 4:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_equipment_attach_employee_hire, null);
				dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setAdapter(new SectorAdapter(this, installments), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(user.getMoney() < price){
							Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
						} else {
							doPositiveClickDialogEmployee(which);
						}
					}
				})
				.create();
				break;
		}
		return dialog;
	}
	
	public AlertDialog bundleDialog(int d){
		AlertDialog dialog = null;
		final LayoutInflater factory;
		final View view;
		final EditText txtSector,txtPrice;
		switch (d) {
		case 0:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_equipment_attach_employee_hire, null);
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setAdapter(new SectorAdapter(this, installments), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doQueryTotalBundle(which);
				}
				
			})
			.create();
			break;
			
		case 11:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_bundle_query, null);
			txtSector = (EditText)view.findViewById(R.id.txt_sector);
			txtPrice = (EditText)view.findViewById(R.id.txt_price);
			
			txtSector.setText(installmentType);
			txtPrice.setText(total+" ZE");
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Yes, buy and install now!", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(user.getMoney() < total){
						Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
					} else {
						doPositiveClickDialogInstallment();
					}
				}
				
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
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
	
	private void doPositiveClickDialog(String id, double picked){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new BuyMarketProduct().execute(id,""+picked);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogEquipment(String id){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new BuyMarketEquipment().execute(id);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogEmployee(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Get available installment..");
			new LoadInstallmentOwnedByUser().execute(""+0);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doPositiveClickDialogEmployee(int pos){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new HireEmployeeToInstallment().execute(installments.get(pos).getId());
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void doPositiveClickDialogInstallment(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new BuyBundleEquipmentEmployee().execute();
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void doClickBundleDefault(){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Get available installment..");
			new LoadInstallmentOwnedByUser().execute(""+1);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doQueryTotalBundle(int pos){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Querying total price..");
			id = installments.get(pos).getId();
			new QueryTotalBundle().execute(installments.get(pos).getId());
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void setLayout(){
		lin = (LinearLayout)findViewById(100);
		lin.removeAllViews();
		
		ListView lv = new ListView(this);
		JsonParser parser = new JsonParser();
		JsonArray array;
		if(tab.equals("Product")){
			array = parser.parse(data).getAsJsonArray();
			ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
			for(int i=0;i<array.size();i++){
				products.add(new Gson().fromJson(array.get(i), MarketProduct.class));
			}
			lv.setAdapter(new MarketProductAdapter(this, products, user.getName()));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Equipment")){
			array = parser.parse(data).getAsJsonArray();
			ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
			for(int i=0;i<array.size();i++){
				equipments.add(new Gson().fromJson(array.get(i), MarketEquipment.class));
			}
			lv.setAdapter(new MarketEquipmentAdapter(this, equipments, user.getName()));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Employee")){
			array = parser.parse(data).getAsJsonArray();
			ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
			for(int i=0;i<array.size();i++){
				employees.add(new Gson().fromJson(array.get(i), MarketEmployee.class));
			}
			lv.setAdapter(new MarketEmployeeAdapter(this, employees));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Bundle")){
			lv.setAdapter(ArrayAdapter.createFromResource(this, R.array.options_bundle, android.R.layout.simple_list_item_1));
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(onItemClickHandler);
		}
		
		lv.setId(200);
		lin.addView(lv);
		
		parser = null;
		array = null;
	}
	
	private class LoadInstallmentOwnedByUser extends AsyncTask<String, Void, Object>{
		int check;
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				check = Integer.parseInt(params[0]);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				installments = new ArrayList<Installment>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray();

				for(int i=0;i<array1.size();i++){
					installments.add(new Gson().fromJson(array1.get(i), Installment.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				
				switch (check) {
				case 0:
					dialog(id, 4, name, 0, price, 0, quality, operational).show();
					break;
					
				case 1:
					bundleDialog(0).show();

				default:
					break;
				}
			}
		}
	}
	
	private class QueryTotalBundle extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_QUERY_TOTAL_BUNDLE+"&installment="+params[0]);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				installmentType = new Gson().fromJson(array.get(0), String.class);
				total = new Gson().fromJson(array.get(1), Double.class);
				bundleDialog(11).show();
			}
		}
	}
	
	private class BuyMarketProduct extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("productId", params[0]);
			postParameters.put("picked", params[1]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_BUY_MARKET_PRODUCT, postParameters);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(MarketTabContentActivity.this, "Your requested product is no longer available. Please refresh the market first before pick another item..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("2")){
				Toast.makeText(MarketTabContentActivity.this, "Your requested product's size is larger than available stock. Please refresh the market first before pick another item..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("3")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("4")){
				Toast.makeText(MarketTabContentActivity.this, "You have no storage in market's zone. Please build it first..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("5")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient storage..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				data = new Gson().fromJson(array.get(1), String.class);
				setLayout();
			}
		}
		
	}
	
	private class BuyMarketEquipment extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("equipmentId", params[0]);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_BUY_MARKET_EQUIPMENT, postParameters);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(MarketTabContentActivity.this, "Your requested product is no longer available. Please refresh the market first before pick another item..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("2")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("3")){
				Toast.makeText(MarketTabContentActivity.this, "You have no storage in market's zone. Please build it first..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("4")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient storage..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				data = new Gson().fromJson(array.get(1), String.class);
				setLayout();
			}
		}
	}
	
	private class HireEmployeeToInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("zone", user.getZone());
			postParameters.put("idInstallment", params[0]);
			postParameters.put("idEmployee", id);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_HIRE_EMPLOYEE_TO_INSTALLMENT, postParameters);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(MarketTabContentActivity.this, "Your requested employee is no longer available. Please refresh the market first before pick another employee..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("2")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				user.setMoney(new Gson().fromJson(array.get(0), Double.class));
				db.updateUserData(user);
				
				data = new Gson().fromJson(array.get(1), String.class);
				setLayout();
			}
		}
	}
	
	private class BuyBundleEquipmentEmployee extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("installment", id);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_BUY_BUNDLE_EQUIPMENT_EMPLOYEE, postParameters);
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
				Toast.makeText(MarketTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(MarketTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(MarketTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(MarketTabContentActivity.this, "Insufficient money..", Toast.LENGTH_LONG).show();
			} else {
				user.setMoney(new Gson().fromJson(res.toString(), Double.class));
				db.updateUserData(user);
				Toast.makeText(MarketTabContentActivity.this, "The bundle have installed into your installment..", Toast.LENGTH_LONG).show();
			}
		}
	}
}

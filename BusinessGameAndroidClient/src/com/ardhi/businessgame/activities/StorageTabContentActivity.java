package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.SectorAdapter;
import com.ardhi.businessgame.activities.adapters.StorageEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.StorageProductAdapter;
import com.ardhi.businessgame.models.Installment;
import com.ardhi.businessgame.models.StorageEquipment;
import com.ardhi.businessgame.models.StorageProduct;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StorageTabContentActivity extends Activity {
	private ProgressDialog progressDialog;
	private DBAccess db;
	private User user;
	private LinearLayout lin;
	private String tab, data, id;
	private double size, price;
	private ArrayList<String> marketZone;
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
	
	public void showMyDialog(String i, double s, int d) {
		if(CommunicationService.isOnline(this)){
			id = i;
			size = s;
			progressDialog = ProgressDialog.show(this, "", "Get suggested price..");
			new GetSuggestedPrice().execute(""+i,""+d);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showAttachDialog(String i, String type){
		id = i;
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Get available installment..");
			new LoadInstallmentOwnedByEquipment().execute(type);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	public AlertDialog dialog(int id){
		final LayoutInflater factory;
		final View view;
		final EditText txtPrice;
		final Spinner spinMarket;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marketZone);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		AlertDialog dialog = null;
		switch (id) {
			case 1:
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_product_sell, null);
				final SeekBar s = (SeekBar)view.findViewById(R.id.seek_size);
				final TextView txtMin = (TextView)view.findViewById(R.id.txt_min),
						txtMax = (TextView)view.findViewById(R.id.txt_max),
						txtOffered = (TextView)view.findViewById(R.id.txt_offered),
						txtTotal = (TextView)view.findViewById(R.id.txt_total);
				txtPrice = (EditText)view.findViewById(R.id.txt_price);
				spinMarket = (Spinner)view.findViewById(R.id.spin_market);
				spinMarket.setAdapter(adapter);
				txtMin.setText("0");
				txtMax.setText(""+size);
				txtOffered.setText("Offered : 0.0 CBM");
				txtTotal.setText("Total : 0 ZE");
//				android.util.Log.d("tes set", ""+price);
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
							doPositiveClickProduct((double)s.getProgress()/100, spinMarket.getSelectedItem().toString());
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
			case 2 :
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_equipment_sell, null);
				txtPrice = (EditText)view.findViewById(R.id.txt_price);
				txtPrice.setText(""+price);
//				android.util.Log.d("tes set", ""+price);
				spinMarket = (Spinner)view.findViewById(R.id.spin_market);
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
						doPositiveClickEquipment(spinMarket.getSelectedItem().toString());
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setCancelable(false)
				.create();
				break;
			case 3 :
				factory = LayoutInflater.from(this);
				view = factory.inflate(R.layout.question_equipment_attach_employee_hire, null);
//				ListView lv = (ListView)view.findViewById(R.id.list_sector);
//		    	lv.setAdapter(new SectorAdapter(this, installment, zones, efficiency, effectivity));
//		    	lv.setTextFilterEnabled(true);
//		        lv.setOnItemClickListener(onItemClickHandler);
		        dialog = new AlertDialog.Builder(this)
				.setView(view)
				.setAdapter(new SectorAdapter(this, installments), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(CommunicationService.isOnline(StorageTabContentActivity.this)){
							progressDialog = ProgressDialog.show(StorageTabContentActivity.this, "", "Processing..");
							android.util.Log.d("idEq", StorageTabContentActivity.this.id);
							android.util.Log.d("idIns", installments.get(which).getId());
							new AttachEquipmentToInstallment().execute(installments.get(which).getId());
						} else {
				    		Toast.makeText(StorageTabContentActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
				    	}
					}
				})
				.create();
				break;
			default : 
				break;
		}
		return dialog;
	}
	
//	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
//    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
//			Toast.makeText(StorageTabContentActivity.this, StorageTabContentActivity.this.idInstallment.get(pos), Toast.LENGTH_SHORT).show();
//			if(CommunicationService.isOnline(StorageTabContentActivity.this)){
//				progressDialog = ProgressDialog.show(StorageTabContentActivity.this, "", "Processing..");
//				new AttachEquipment().execute();
//			} else {
//	    		Toast.makeText(StorageTabContentActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
//	    	}
//		}
//	};
	
	private void doPositiveClickProduct(double offered, String marketZone){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new SellStorageProduct().execute(""+offered,marketZone);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void doPositiveClickEquipment(String marketZone){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new SellStorageEquipment().execute(marketZone);
		} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private void setLayout(){
		lin = (LinearLayout)findViewById(100);
		lin.removeAllViews();
		
		ListView lv = new ListView(this);
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(data).getAsJsonArray();
		if(tab.equals("Product")){
			ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
			for(int i=0;i<array.size();i++){
				products.add(new Gson().fromJson(array.get(i), StorageProduct.class));
			}
			lv.setAdapter(new StorageProductAdapter(this, products));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Equipment")){
			ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
			for(int i=0;i<array.size();i++){
				equipments.add(new Gson().fromJson(array.get(i), StorageEquipment.class));
			}
			lv.setAdapter(new StorageEquipmentAdapter(this, equipments));
			lv.setTextFilterEnabled(true);
		}
		
		lv.setId(200);
		lin.addView(lv);
		
		parser = null;
		array = null;
	}
	
	private class GetSuggestedPrice extends AsyncTask<String, Void, Object>{
		int dialog;
		
		@Override
		protected Object doInBackground(String... params) {
			dialog = Integer.parseInt(params[1]);
			try {
				return CommunicationService.get(CommunicationService.GET_GET_SUGGESTED_PRICE+"&user="+user.getName()+"&id="+params[0]);
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
				marketZone = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					marketZone.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				
				dialog(dialog).show();
			}
		}
	}
	
	private class LoadInstallmentOwnedByEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_INSTALLMENT_OWNED_BY_EQUIPMENT+"&user="+user.getName()+"&equipment_type="+params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(StorageTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
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
				
				dialog(3).show();
			}
		}
	}
	
	private class SellStorageProduct extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
//			postParameters.put("user", user.getName());
//			postParameters.put("zone", user.getZone());
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("productId", id);
			postParameters.put("offer", params[0]);
			postParameters.put("price", ""+price);
			postParameters.put("marketZone", params[1]);
			
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
				Toast.makeText(StorageTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				data = res.toString();
				setLayout();
			}
		}
	}
	
	private class SellStorageEquipment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
//			postParameters.put("user", user.getName());
//			postParameters.put("zone", user.getZone());
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("equipmentId", id);
			postParameters.put("price", ""+price);
			postParameters.put("marketZone", params[0]);
			
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
				Toast.makeText(StorageTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				data = res.toString();
				setLayout();
			}
		}
	}
	
	private class AttachEquipmentToInstallment extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
//			postParameters.put("user", user.getName());
//			postParameters.put("zone", user.getZone());
			postParameters.put("storage", user.getStorages().get(user.getZone()));
			postParameters.put("idInstallment", params[0]);
			postParameters.put("idEquipment", id);
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
				Toast.makeText(StorageTabContentActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(StorageTabContentActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(StorageTabContentActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else {
				data = res.toString();
				setLayout();
			}
		}
	}
}

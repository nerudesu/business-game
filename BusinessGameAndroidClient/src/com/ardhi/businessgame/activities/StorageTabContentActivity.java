package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.StorageEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.StorageProductAdapter;
import com.ardhi.businessgame.models.StorageEquipment;
import com.ardhi.businessgame.models.StorageProduct;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case 1:
				LayoutInflater factory = LayoutInflater.from(this);
				final View view = factory.inflate(R.layout.question_product_sell, null);
				final SeekBar s = (SeekBar)view.findViewById(R.id.seek_size);
				final TextView txtMin = (TextView)view.findViewById(R.id.txt_min),
						txtMax = (TextView)view.findViewById(R.id.txt_max),
						txtOffered = (TextView)view.findViewById(R.id.txt_offered),
						txtTotal = (TextView)view.findViewById(R.id.txt_total);
				final EditText txtPrice = (EditText)view.findViewById(R.id.txt_price);
				final Spinner spinMarket = (Spinner)view.findViewById(R.id.spin_market);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marketZone);
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
				return new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(s.getProgress() > 0){
							doPositiveClickDialog((double)s.getProgress()/100, spinMarket.getSelectedItem().toString());
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
			default : 
				break;
		}
		return null;
	}
	
	public void showMyDialog(String i, double s, int d) {
		id = i;
		size = s;
		progressDialog = ProgressDialog.show(this, "", "Get suggested price..");
		new GetSuggestedPrice().execute(""+i,""+d);
	}
	
	private void doPositiveClickDialog(double offered, String marketZone){
		progressDialog = ProgressDialog.show(this, "", "Processing..");
		new SellStorageProduct().execute(""+offered,marketZone);
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
	}
	
	private class GetSuggestedPrice extends AsyncTask<String, Void, Object>{
		int dialog;
		
		@Override
		protected Object doInBackground(String... params) {
			dialog = Integer.parseInt(params[1]);
			try {
				String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_GET_SUGGESTED_PRICE+"&user="+user.getName()+"&productId="+params[0]);
				res = res.toString().replaceAll("\\n+", "");
				return res.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
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
				android.util.Log.d("json", res.toString());
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				android.util.Log.d("json", array.get(1).toString());
				price = new Gson().fromJson(array.get(0), Double.class);
				marketZone = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					marketZone.add(new Gson().fromJson(array1.get(i), String.class));
				}
				showDialog(dialog);
			}
		}
	}
	
	private class SellStorageProduct extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("action", CustomHttpClient.POST_SELL_STORAGE_PRODUCT));
			postParameters.add(new BasicNameValuePair("user", user.getName()));
			postParameters.add(new BasicNameValuePair("zone", user.getZone()));
			postParameters.add(new BasicNameValuePair("productId", id));
			postParameters.add(new BasicNameValuePair("offer", params[0]));
			postParameters.add(new BasicNameValuePair("price", ""+price));
			postParameters.add(new BasicNameValuePair("marketZone", params[1]));
			try {
				String res = CustomHttpClient.executeHttpPost(CustomHttpClient.URL, postParameters);
				res = res.toString().replaceAll("\\s+", "");
				return res;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
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
				android.util.Log.d("json", res.toString());
				setLayout();
			}
		}
	}
}

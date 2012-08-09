package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.adapters.MarketEmployeeAdapter;
import com.ardhi.businessgame.activities.adapters.MarketEquipmentAdapter;
import com.ardhi.businessgame.activities.adapters.MarketProductAdapter;
import com.ardhi.businessgame.models.MarketEmployee;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class MarketTabContentActivity extends Activity {
	private DBAccess db;
	private User user;
	private LinearLayout lin;
	private String tab, data, id;
	private double size, price;
	private ProgressDialog progressDialog;
	
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
				final View view = factory.inflate(R.layout.question_product_buy, null);
				final SeekBar s = (SeekBar)view.findViewById(R.id.seek_size);
				final TextView txtMin = (TextView)view.findViewById(R.id.txt_min),
						txtMax = (TextView)view.findViewById(R.id.txt_max),
						txtPicked = (TextView)view.findViewById(R.id.txt_picked),
						txtTotal = (TextView)view.findViewById(R.id.txt_total);
				txtMin.setText("0");
				txtMax.setText(""+size);
				txtPicked.setText("Picked : 0.0 CBM");
				txtTotal.setText("Total : 0 ZE");
				android.util.Log.d("size in real dialog", ""+size);
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
				return new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(s.getProgress() > 0){
							if(user.getMoney() < ((double)s.getProgress()/100)*price){
								Toast.makeText(MarketTabContentActivity.this, "Insufficient funds...", Toast.LENGTH_LONG).show();
							} else {
								doPositiveClickDialog((double)s.getProgress()/100);
							}
						}
						txtPicked.setText("Picked : 0.0 CBM");
						txtTotal.setText("Total : 0 ZE");
						s.setProgress(0);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						txtPicked.setText("Picked : 0.0 CBM");
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
	
	public void showMyDialog(String i, int d, double s, double p){
		id = i;
		size = s;
		price = p;
		showDialog(d);
	}
	
	private void doPositiveClickDialog(double picked){
		progressDialog = ProgressDialog.show(this, "", "Processing..");
		new BuyMarketProduct().execute(""+picked);
	}
	
	private void setLayout(){
		lin = (LinearLayout)findViewById(100);
		lin.removeAllViews();
		
		ListView lv = new ListView(this);
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(data).getAsJsonArray();
		if(tab.equals("Product")){
			ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
			for(int i=0;i<array.size();i++){
				products.add(new Gson().fromJson(array.get(i), MarketProduct.class));
			}
			lv.setAdapter(new MarketProductAdapter(this, products, user.getName()));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Equipment")){
			ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
			for(int i=0;i<array.size();i++){
				equipments.add(new Gson().fromJson(array.get(i), MarketEquipment.class));
			}
			lv.setAdapter(new MarketEquipmentAdapter(this, equipments, user.getName()));
			lv.setTextFilterEnabled(true);
		} else if(tab.equals("Employee")){
			ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
			for(int i=0;i<array.size();i++){
				employees.add(new Gson().fromJson(array.get(i), MarketEmployee.class));
			}
			lv.setAdapter(new MarketEmployeeAdapter(this, employees));
			lv.setTextFilterEnabled(true);
		}
		
		lv.setId(200);
		lin.addView(lv);
	}
	
	private class BuyMarketProduct extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("action", CustomHttpClient.POST_BUY_MARKET_PRODUCT));
			postParameters.add(new BasicNameValuePair("user", user.getName()));
			postParameters.add(new BasicNameValuePair("zone", user.getZone()));
			postParameters.add(new BasicNameValuePair("productId", id));
			postParameters.add(new BasicNameValuePair("picked", params[0]));
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
				data = res.toString();
				setLayout();
			}
		}
		
	}
}

package com.ardhi.businessgame.activities;

import java.util.ArrayList;
import java.util.Iterator;

import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HeadquarterTabContentActivity extends Activity {
	private ArrayList<String> sectors, userSectors;
	private ProgressDialog progressDialog;
	private DBAccess db;
	private User user;
	private LinearLayout lin;
	private String tab;
	private int sector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lin = new LinearLayout(this);
		db = new DBAccess(this);
		user = db.getUser();
		tab = getIntent().getStringExtra("Tab");
		
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
		
		if(tab.equals("Sector Unlocked")){
			progressDialog = ProgressDialog.show(this, "", "Checking user's sector..");
			new CheckUserSector().execute();
		} else if(tab.equals("Change Resident")){
			
		}
	}
	
	private void setLayout(){
		lin = (LinearLayout)findViewById(100);
		
		if(tab.equals("Sector Unlocked")){
			Log.d("tab sector unlock", "kesini gak");
			ListView lv = new ListView(this);
			lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sectors));
			lv.setOnItemClickListener(onItemClickHandler);
			lv.setTextFilterEnabled(true);
			lin.addView(lv);
		} else if(tab.equals("Sector Unlocked")){
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(HeadquarterTabContentActivity.this, android.R.layout.simple_spinner_item, equipmentNames);
//			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			Spinner spinEquipment = new Spinner(this);
//			LinearLayout linHo = new LinearLayout(this);
//			TextView text = new TextView(this);
//			
//			linHo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//			linHo.setOrientation(LinearLayout.HORIZONTAL);
//			text.setText("Filter : ");
//			spinEquipment.setAdapter(adapter);
//			spinEquipment.setOnItemSelectedListener(onItemSelectedHandler);
//			
//			linHo.addView(text);
//			linHo.addView(spinEquipment);
//			lin.addView(linHo);
		}
	}
	
	private class CheckUserSector extends AsyncTask<String, Void, Object>{
		@Override
		protected Object doInBackground(String... params) {
			try {
				String res = CustomHttpClient.executeHttpGet(CustomHttpClient.URL+CustomHttpClient.GET_CHECK_USER_SECTOR+"&user="+user.getName());
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
						array1 = parser.parse(array.get(0).toString()).getAsJsonArray(),
						array2 = parser.parse(array.get(1).toString()).getAsJsonArray(),
						array3 = parser.parse(array.get(2).toString()).getAsJsonArray();
				sectors = new ArrayList<String>();
				userSectors = new ArrayList<String>();
				Iterator<JsonElement> i = array1.iterator();
				int x = 0;
				while(i.hasNext()){
					sectors.add(new Gson().fromJson(array1.get(x), String.class));
					x++;
					i.next();
				}
				
				i = array2.iterator();
				x = 0;
				while(i.hasNext()){
					userSectors.add(new Gson().fromJson(array2.get(x), String.class));
					x++;
					i.next();
				}
				
				String tmp = new Gson().fromJson(array3.get(0), String.class);
				sector = Integer.parseInt(tmp);
				
				setLayout();
			}
		}
	}
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			if(userSectors.contains(parent.getItemAtPosition(pos).toString())){
				Toast.makeText(HeadquarterTabContentActivity.this, "You have acquired sector "+parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(HeadquarterTabContentActivity.this, "You haven't acquired sector "+parent.getItemAtPosition(pos).toString()+"\nPrice to unlock : "+(sector*(userSectors.size()+1)), Toast.LENGTH_SHORT).show();
			}
		}
	};
}

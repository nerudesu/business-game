package com.ardhi.businessgame.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class RegisterActivity extends Activity {
	private EditText user, pass, pass_re, email;
    private Button btnRegister;
    private Spinner spinDate, spinMonth, spinYear, spinZone;
    private ArrayList<String> zoneList;
    private ProgressDialog progressDialog;
    private DBAccess db;

	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        user = (EditText)findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        pass_re = (EditText)findViewById(R.id.pass_re);
        email = (EditText)findViewById(R.id.email);
        btnRegister = (Button)findViewById(R.id.btn_register);
        spinDate = (Spinner)findViewById(R.id.spin_date);
        spinMonth = (Spinner)findViewById(R.id.spin_month);
        spinYear = (Spinner)findViewById(R.id.spin_year);
        spinZone = (Spinner)findViewById(R.id.spin_zone);
        db = new DBAccess(this);
        
        btnRegister.setOnClickListener(onClickHandler);
        spinMonth.setOnItemSelectedListener(onSpinnerSelect);
        spinYear.setOnItemSelectedListener(onSpinnerSelect);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Please wait..");
            new GetEntireZone().execute();
        } else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register, menu);
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
    
    private void loadZone() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, zoneList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinZone.setAdapter(adapter);
	}
    
    private AdapterView.OnItemSelectedListener onSpinnerSelect = new AdapterView.OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
			if(parent.getId() == R.id.spin_month){
				if(parent.getItemAtPosition(pos).toString().equals("Jan")
						|| parent.getItemAtPosition(pos).toString().equals("Mar")
						|| parent.getItemAtPosition(pos).toString().equals("May")
						|| parent.getItemAtPosition(pos).toString().equals("Jul")
						|| parent.getItemAtPosition(pos).toString().equals("Aug")
						|| parent.getItemAtPosition(pos).toString().equals("Oct")
						|| parent.getItemAtPosition(pos).toString().equals("Dec")){
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date31, android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinDate.setAdapter(adapter);
				} else if(parent.getItemAtPosition(pos).toString().equals("Apr")
						|| parent.getItemAtPosition(pos).toString().equals("Jun")
						|| parent.getItemAtPosition(pos).toString().equals("Sep")
						|| parent.getItemAtPosition(pos).toString().equals("Nov")){
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date30, android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinDate.setAdapter(adapter);
				} else if(parent.getItemAtPosition(pos).toString().equals("Feb")){
					if(spinYear.getItemAtPosition(spinYear.getSelectedItemPosition()).toString().equals("1988")
							|| spinYear.getItemAtPosition(spinYear.getSelectedItemPosition()).toString().equals("1992")){
						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date29, android.R.layout.simple_spinner_item);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinDate.setAdapter(adapter);
					} else {
						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date28, android.R.layout.simple_spinner_item);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinDate.setAdapter(adapter);
					}
				}
			} else if(parent.getId() == R.id.spin_year){
				if(spinMonth.getItemAtPosition(spinMonth.getSelectedItemPosition()).equals("Feb")){
					if(parent.getItemAtPosition(pos).toString().equals("1988")
							|| parent.getItemAtPosition(pos).toString().equals("1992")){
						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date29, android.R.layout.simple_spinner_item);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinDate.setAdapter(adapter);
					} else {
						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.date28, android.R.layout.simple_spinner_item);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinDate.setAdapter(adapter);
					}
				}
			}
		}

		public void onNothingSelected(AdapterView<?> parent) {
			Toast.makeText(RegisterActivity.this, "Please fill it appropriate to your bio.", Toast.LENGTH_SHORT).show();
		}
    };
    
    private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			if(v.getId() == R.id.btn_register){
				if(user.getText().toString().equals("") || pass.getText().toString().equals("") || pass_re.getText().toString().equals("") || email.getText().toString().equals("")){
					Toast.makeText(RegisterActivity.this, "You must fill all field.", Toast.LENGTH_SHORT).show();
				} else {
					if(pass.getText().toString().equals(pass_re.getText().toString())){
						if(CommunicationService.isOnline(RegisterActivity.this)){
							progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Registering..");
							new RegisterUser().execute();
						} else {
							Toast.makeText(RegisterActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(RegisterActivity.this, "Password and Password (retype) did not match", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
    };
    
    private class GetEntireZone extends AsyncTask<String, Void, Object>{
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_GET_ENTIRE_ZONE);
			} catch (IOException e) {
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
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				zoneList = new ArrayList<String>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray();
				Iterator<JsonElement> i = array.iterator();
				int x = 0;
				while(i.hasNext()){
					zoneList.add(new Gson().fromJson(array.get(x), String.class));
					x++;
					i.next();
				}
				parser = null;
				array = null;
				i = null;
				
				loadZone();
			}
		}
    }
    
    private class RegisterUser extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getText().toString());
			postParameters.put("pass", pass.getText().toString());
			postParameters.put("email", email.getText().toString());
			postParameters.put("dob", spinMonth.getSelectedItem().toString()+" "+spinDate.getSelectedItem().toString()+" "+spinYear.getSelectedItem().toString());
			postParameters.put("zone", spinZone.getSelectedItem().toString());
			try {
				return CommunicationService.post(CommunicationService.POST_REGISTER_USER, postParameters);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(RegisterActivity.this, "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
				pass.setText("");
				pass_re.setText("");
			} else if(res.toString().equals("-1")){
				Toast.makeText(RegisterActivity.this, "Server is not ready..", Toast.LENGTH_SHORT).show();
				pass.setText("");
				pass_re.setText("");
			} else if(res.equals("0")){
				Toast.makeText(RegisterActivity.this, "Username is already exist", Toast.LENGTH_SHORT).show();
				user.setText("");
				pass.setText("");
				pass_re.setText("");
				progressDialog.dismiss();
			} else {
				db.addUser(new User(user.getText().toString(), email.getText().toString(), spinMonth.getSelectedItem().toString()+" "+spinDate.getSelectedItem().toString()+" "+spinYear.getSelectedItem().toString(), "This is me", "", 0.00, 0, spinZone.getSelectedItem().toString(), new HashMap<String, String>(), 0));
				Intent intent = new Intent(RegisterActivity.this, MainBusinessGameActivity.class);
				startActivity(intent);
			}
		}
    }

}

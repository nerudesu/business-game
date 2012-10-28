package com.phionsoft.zentriumph.activities;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.SavedUser;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;

public class LoginActivity extends Activity {
	private AsyncTask<String, Void, Object> task;
	private EditText user, pass;
    private Button btnLogin, btnRegister;
    private CheckBox accRe, autoLog;
    private DBAccess db;
    private ProgressDialog progressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        user = (EditText)findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnRegister = (Button)findViewById(R.id.btn_register);
        accRe = (CheckBox)findViewById(R.id.check_acc_re);
        autoLog = (CheckBox)findViewById(R.id.check_auto_log);
        
        btnLogin.setOnClickListener(onClickHandler);
        btnRegister.setOnClickListener(onClickHandler);
        
        db = new DBAccess(this);
        db.deleteUserData();
        
        SavedUser sUser = db.getSavedUserData();
        
        accRe.setOnCheckedChangeListener(onCheckedChangeHandler);
        
        if(sUser != null){
        	user.setText(sUser.getUser());
        	pass.setText(sUser.getPass());
        	accRe.setChecked(true);
        	autoLog.setEnabled(true);
        	autoLog.setChecked(sUser.isAutoLog());
        	if(autoLog.isChecked()){
        		if(CommunicationService.isOnline(this)){
        			progressDialog = ProgressDialog.show(this, "", "Please wait..");
            		progressDialog.setCancelable(true);
            		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    					
    					@Override
    					public void onCancel(DialogInterface dialog) {
    						task.cancel(true);
    					}
    				});
            		task = new Login(); 
    				task.execute();
        		} else {
					Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
				}
        	}
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(progressDialog != null)
    		progressDialog.dismiss();
    	
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	db.deleteUserData();
    }
    
    private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			if(v.getId() == R.id.btn_login){
				if(user.getText().toString().equals("") || pass.getText().toString().equals(""))
					Toast.makeText(LoginActivity.this, "You must fill username and password first..", Toast.LENGTH_SHORT).show();
				else {
					if(CommunicationService.isOnline(LoginActivity.this)){
						progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait..");
						progressDialog.setCancelable(true);
		        		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								task.cancel(true);
							}
						});
						task = new Login(); 
						task.execute();
					} else {
						Toast.makeText(LoginActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
					}
				}
			} else if(v.getId() == R.id.btn_register){
				startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			}
		}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    
    private CompoundButton.OnCheckedChangeListener onCheckedChangeHandler = new CompoundButton.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked){
				if(user.getText().toString().equals("") || pass.getText().toString().equals("")){
					Toast.makeText(LoginActivity.this, "You must fill username and password first..", Toast.LENGTH_SHORT).show();
					accRe.setChecked(false);
				} else {
					db.deleteSavedUserData();
					db.saveUserData(new SavedUser(user.getText().toString(), pass.getText().toString(), autoLog.isChecked()));
					autoLog.setEnabled(isChecked);
				}
			} else {
				db.deleteSavedUserData();
				autoLog.setChecked(isChecked);
				autoLog.setEnabled(isChecked);
			}
			
		}
    	
    };
    
    private class Login extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			db.deleteSavedUserData();
			if(accRe.isChecked()){
				db.saveUserData(new SavedUser(user.getText().toString(), pass.getText().toString(), autoLog.isChecked()));
			}
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getText().toString());
			postParameters.put("pass", pass.getText().toString());
			try {
				postParameters.put("ver", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+"");
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				postParameters.put("ver", "failed");
			}
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_LOGIN, postParameters);
			} catch (IOException e) {
				e.printStackTrace();
				res = null;
			}
			
			postParameters = null;
			
			return res;
		}
    	
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			Toast.makeText(LoginActivity.this, "Canceled..", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			android.util.Log.d("canceled flag?", "oh no");
			if(res == null){
				Toast.makeText(LoginActivity.this, "No response from server..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(LoginActivity.this, "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(LoginActivity.this, "Wrong password..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(LoginActivity.this, "Username not exist..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("2")){
				Toast.makeText(LoginActivity.this, "Please upgrade your apps first..", Toast.LENGTH_SHORT).show();
			} else {
				if(db.addUser(new Gson().fromJson(res.toString(), User.class))){
					startActivity(new Intent(LoginActivity.this, MainBusinessGameActivity.class));
				}
			}
		}

    }
}

package com.ardhi.businessgame.activities;

import java.io.IOException;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.SavedUser;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

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
        if(sUser != null){
        	user.setText(sUser.getUser());
        	pass.setText(sUser.getPass());
        	accRe.setChecked(true);
        	autoLog.setChecked(sUser.isAutoLog());
        	if(autoLog.isChecked()){
        		progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait..");
				new Login().execute();
        	}
        }
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	db.deleteUserData();
    }
    
    private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			if(v.getId() == R.id.btn_login){
				if(CommunicationService.isOnline(LoginActivity.this)){
					progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait..");
					new Login().execute();
				} else {
					Toast.makeText(LoginActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
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
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(LoginActivity.this, "No response from server..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(LoginActivity.this, "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(LoginActivity.this, "Wrong password..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(LoginActivity.this, "Username not exist..", Toast.LENGTH_SHORT).show();
			} else {
				android.util.Log.d("gson", res.toString());
				if(db.addUser(new Gson().fromJson(res.toString(), User.class))){
					startActivity(new Intent(LoginActivity.this, MainBusinessGameActivity.class));
				}
			}
		}

    }
}

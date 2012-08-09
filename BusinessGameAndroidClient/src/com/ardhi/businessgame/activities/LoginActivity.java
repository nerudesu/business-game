package com.ardhi.businessgame.activities;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.SavedUser;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CustomHttpClient;
import com.ardhi.businessgame.services.DBAccess;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
        setContentView(R.layout.login);
        
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
				progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait..");
				new Login().execute();
			} else if(v.getId() == R.id.btn_register){
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
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
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("action", CustomHttpClient.POST_LOGIN));
			postParameters.add(new BasicNameValuePair("user", user.getText().toString()));
			postParameters.add(new BasicNameValuePair("pass", pass.getText().toString()));
			try {
				String res = CustomHttpClient.executeHttpPost(CustomHttpClient.URL, postParameters);
				res = res.toString().replaceAll("\\n+", "");
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
				Toast.makeText(LoginActivity.this, "No response from server..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(LoginActivity.this, "Wrong password..", Toast.LENGTH_SHORT).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(LoginActivity.this, "Username not exist..", Toast.LENGTH_SHORT).show();
			} else {
				if(db.addUser(new Gson().fromJson(res.toString(), User.class))){
					Intent intent = new Intent(LoginActivity.this, MainBusinessGameActivity.class);
					startActivity(intent);
				}
			}
		}

    }
}
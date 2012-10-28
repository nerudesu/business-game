package com.phionsoft.zentriumph.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.models.User;
import com.phionsoft.zentriumph.services.CommunicationService;
import com.phionsoft.zentriumph.services.DBAccess;
import com.phionsoft.zentriumph.services.SystemService;
import com.phionsoft.zentriumph.services.TimeSync;

@SuppressLint("NewApi")
public class BankLobbyActivity extends Activity {
	private ListView lv;
	private DBAccess db;
	private EditText zone, money, nextTurn;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ProgressDialog progressDialog;
	private double loan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banklobby);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        lv = (ListView)findViewById(R.id.options_bank);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onItemClickHandler);
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        
        user = db.getUser();
        zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
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
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bank_lobby, menu);
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
	
	private AdapterView.OnItemClickListener onItemClickHandler = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			Toast.makeText(BankLobbyActivity.this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
			switch (pos) {
			case 0:
				startActivity(new Intent(BankLobbyActivity.this, BankProposalTabActivity.class));
				break;
				
			case 1:
				if(CommunicationService.isOnline(BankLobbyActivity.this)){
					progressDialog = ProgressDialog.show(BankLobbyActivity.this, "", "Processing..");
					new GetBorrowedMoney().execute();
				} else Toast.makeText(BankLobbyActivity.this, "Device is offline..", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};
	
	private AlertDialog dialog(int d){
		AlertDialog dialog = null;
		final LayoutInflater factory;
		final View view;
		switch (d) {
		case 1:
			factory = LayoutInflater.from(this);
			view = factory.inflate(R.layout.question_pay_borrow, null);
			final EditText txtLoan = (EditText)view.findViewById(R.id.txt_loan),
					txtMoney = (EditText)view.findViewById(R.id.txt_money);
			
			txtLoan.setText(loan+" ZE");
			txtMoney.setText("0");
			dialog = new AlertDialog.Builder(this)
			.setView(view)
			.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(user.getMoney() >= (Double.parseDouble(txtMoney.getText().toString())))
						doPositiveClickDialogPayBorrowedMoney(Double.parseDouble(txtMoney.getText().toString()));
					else Toast.makeText(BankLobbyActivity.this, "Insufficient funds..", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setCancelable(false)
			.create();
			
			break;

		default:
			break;
		}
		return dialog;
	}
	
	private void doPositiveClickDialogPayBorrowedMoney(double money){
		if(CommunicationService.isOnline(this)){
			progressDialog = ProgressDialog.show(this, "", "Processing..");
			new PayBorrowedMoney().execute(""+money);
		} else {
			Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class GetBorrowedMoney extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_GET_BORROWED_MONEY+"&user="+user.getName());
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
				Toast.makeText(BankLobbyActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(BankLobbyActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(BankLobbyActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				Toast.makeText(BankLobbyActivity.this, "You have no loan..", Toast.LENGTH_LONG).show();
			} else {
				loan = new Gson().fromJson(res.toString(), Double.class);
				
				dialog(1).show();
			}
		}
	}
	
	private class PayBorrowedMoney extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("pay", params[0]);
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_PAY_BORROWED_MONEY, postParameters);
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
				Toast.makeText(BankLobbyActivity.this, "No response from server. Try again later.", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("-1")){
				Toast.makeText(BankLobbyActivity.this, "Server is not ready..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("0")){
				Toast.makeText(BankLobbyActivity.this, "Internal error..", Toast.LENGTH_LONG).show();
			} else if(res.toString().equals("1")){
				
			}
		}
	}
}

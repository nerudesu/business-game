package com.phionsoft.zentriumph.maps;

import com.phionsoft.zentriumph.services.CommunicationService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class MyActionResolver implements ActionResolver{
	private Handler h;
	private Context c;
	private ProgressDialog progressDialog;
	
	public MyActionResolver(Context con){
		h = new Handler();
		c = con;
	}

	@Override
	public void startAct(Class<?> activity, int flags) {
		Intent i = new Intent(c, activity);
		i.setFlags(flags);
		c.startActivity(i);
	}
	
	@Override
	public void startAct(Class<?> activity, int flags, Bundle b) {
		Intent i = new Intent(c, activity);
		i.setFlags(flags);
		i.putExtras(b);
		c.startActivity(i);
	}
	
	@Override
	public void startAct(Class<?> activity, Bundle b) {
		Intent i = new Intent(c, activity);
		i.putExtras(b);
		c.startActivity(i);
	}

	@Override
	public void startAct(Class<?> activity) {
		c.startActivity(new Intent(c, activity));
	}
	
	@Override
	public void showToast(final String text) {
		h.post(new Runnable() {
			
			public void run() {
				Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void startProgressDialog(final String text) {
		h.post(new Runnable() {
			public void run() {
				progressDialog = ProgressDialog.show(c, "", text);
			}
		});
		
	}

	@Override
	public void stopProgressDialog() {
		h.post(new Runnable() {
			public void run() {
				progressDialog.dismiss();
			}
		});
	}
	
	public class LoadSectorOwned extends AsyncTask<String, Void, Object>{
    	@Override
		protected Object doInBackground(String... params) {
    		startProgressDialog("Loading");
    		String tmp = null;
			try {
				tmp =  CommunicationService.get(CommunicationService.GET_LOAD_SECTOR_OWNED+"&user="+params[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				tmp = null;
			}
			
			stopProgressDialog();
			return tmp;
		}
    }
}

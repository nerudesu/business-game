package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.SectorDetailTabActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InstallmentSupplierAdapter extends BaseAdapter{

	private SectorDetailTabActivity act;
	private ArrayList<String> idInstallment,users;
	private ArrayList<Double> tariffs,availables;
	private static LayoutInflater inflater = null;
	
	public InstallmentSupplierAdapter(SectorDetailTabActivity a, ArrayList<String> i, ArrayList<String> u, ArrayList<Double> t, ArrayList<Double> av) {
		act = a;
		idInstallment = i;
		users = u;
		tariffs = t;
		availables = av;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return idInstallment.size();
	}

	public Object getItem(int pos) {
		return pos;
	}

	public long getItemId(int pos) {
		return pos;
	}

	public View getView(int pos, View newView, ViewGroup parent) {
		View v = newView;
		if(newView == null){
			v = inflater.inflate(R.layout.listrow_installment_supplier, null);
		}
		TextView txtUser = (TextView)v.findViewById(R.id.txt_user),
				txtPrice = (TextView)v.findViewById(R.id.txt_price),
				txtAvailable = (TextView)v.findViewById(R.id.txt_available);
		
		txtUser.setText(users.get(pos));
		txtPrice.setText(tariffs.get(pos)+" ZE per KWH");
		txtAvailable.setText(availables.get(pos)+" KWH available");
		
		return v;
	}
}

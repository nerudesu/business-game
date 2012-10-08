package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.SectorDetailTabActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class InstallmentSupplyAdapter extends BaseAdapter{

	private SectorDetailTabActivity act;
	private ArrayList<String> idInstallment,types,users;
	private ArrayList<Double> supplies;
	private static LayoutInflater inflater = null;
	
	public InstallmentSupplyAdapter(SectorDetailTabActivity a, ArrayList<String> i, ArrayList<String> t, ArrayList<String> u, ArrayList<Double> s) {
		act = a;
		idInstallment = i;
		types = t;
		users = u;
		supplies = s;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return types.size();
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
			v = inflater.inflate(R.layout.listrow_installment_supply, null);
		}
		TextView txtType = (TextView)v.findViewById(R.id.txt_type),
				txtUser = (TextView)v.findViewById(R.id.txt_user);
		Button btnCancelSupply = (Button)v.findViewById(R.id.btn_cancel_supply);
		
		txtType.setText(types.get(pos));
		txtUser.setText(users.get(pos)+" ("+supplies.get(pos)+" KWH)");
		btnCancelSupply.setOnClickListener(new OnClickHandler(idInstallment.get(pos), types.get(pos)+"/"+users.get(pos)));
		
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id,em;
		
		public OnClickHandler(String i,String e){
			id = i;
			em = e;
		}
		
		@Override
		public void onClick(View v) {
			act.dialog(3, id, em).show();
		}
	}
}

package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.HeadquarterTabActivity;
import com.ardhi.businessgame.models.Contract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ContractAdapter extends BaseAdapter{

	private HeadquarterTabActivity act;
	private ArrayList<Contract> contracts;
	private static LayoutInflater inflater = null;
	
	public ContractAdapter(HeadquarterTabActivity a, ArrayList<Contract> c) {
		act = a;
		contracts = c;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return contracts.size();
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
			v = inflater.inflate(R.layout.listrow_contract, null);
		}
		TextView txtProduct = (TextView)v.findViewById(R.id.txt_product),
				txtUser = (TextView)v.findViewById(R.id.txt_user),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		Button btnCancelReject = (Button)v.findViewById(R.id.btn_cancel_reject);
		
		Contract contract = contracts.get(pos);
		
		txtProduct.setText(contract.getProduct());
		txtUser.setText("Transfer "+contract.getContractType()+" "+contract.getUser()+" (Zone : "+contract.getZone()+")");
		txtSize.setText("Size : "+contract.getSize()+" CBM, Price : "+contract.getPrice()+" ZE");
		rateQuality.setRating(contract.getQuality());
		btnCancelReject.setOnClickListener(new OnClickHandler(contract.getId()));
		
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id;
		
		public OnClickHandler(String i) {
			id = i;
		}
		@Override
		public void onClick(View v) {
			act.dialog(v.getId(), 0, 0, 0, id).show();
		}
		
	}
}

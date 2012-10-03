package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.MarketTabContentActivity;
import com.ardhi.businessgame.models.MarketEquipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class MarketEquipmentAdapter extends BaseAdapter{

	private MarketTabContentActivity act;
	private ArrayList<MarketEquipment> equipments;
	private static LayoutInflater inflater = null;
	private String user;
	
	public MarketEquipmentAdapter(MarketTabContentActivity a, ArrayList<MarketEquipment> e, String u) {
		act = a;
		equipments = e;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		user = u;
	}
	
	public int getCount() {
		return equipments.size();
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
			v = inflater.inflate(R.layout.listrow_market_equipment, null);
		}
		TextView txtEquipment = (TextView)v.findViewById(R.id.txt_equipment),
				txtUser = (TextView)v.findViewById(R.id.txt_user),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		ProgressBar progressDurability = (ProgressBar)v.findViewById(R.id.progress_durability);
		Button btnBuy = (Button)v.findViewById(R.id.btn_buy);
		
		MarketEquipment equipment = equipments.get(pos);
		
		txtEquipment.setText(equipment.getEquipment());
		txtUser.setText("Seller : "+equipment.getUser()+", Price : "+equipment.getPrice());
		txtSize.setText("Size : "+equipment.getSize());
		rateQuality.setRating(equipment.getQuality());
		progressDurability.setProgress((int)equipment.getDurability());
		btnBuy.setOnClickListener(new OnClickHandler(equipment.getId(), equipment.getEquipment(), equipment.getPrice(), equipment.getDurability(), equipment.getQuality(), equipment.getOperational()));
		btnBuy.setClickable(!user.equals(equipment.getUser()));
		btnBuy.setEnabled(!user.equals(equipment.getUser()));
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id, name;
		private double price, durability, operational;
		private int quality;
		
		public OnClickHandler(String i, String n, double p, double d, int q, double ops){
			id = i;
			name = n;
			price = p;
			durability = d;
			quality = q;
			operational = ops;
		}
		
		@Override
		public void onClick(View v) {
			act.dialog(id, 2, name, 0, price, durability, quality, operational).show();
		}
	}
}

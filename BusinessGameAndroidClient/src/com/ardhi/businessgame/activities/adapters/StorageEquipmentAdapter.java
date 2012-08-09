package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.StorageEquipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class StorageEquipmentAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<StorageEquipment> equipments;
	private static LayoutInflater inflater = null;
	
	public StorageEquipmentAdapter(Context c, ArrayList<StorageEquipment> e) {
		context = c;
		equipments = e;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			v = inflater.inflate(R.layout.listrow_storage_equipment, null);
		}
		TextView txtEquipment = (TextView)v.findViewById(R.id.txt_equipment),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		ProgressBar progressDurability = (ProgressBar)v.findViewById(R.id.progress_durability);
		Button btnSell = (Button)v.findViewById(R.id.btn_sell);
		
		StorageEquipment equipment = equipments.get(pos);
		
		txtEquipment.setText(equipment.getEquipment());
		txtSize.setText("Size : "+equipment.getSize());
		rateQuality.setRating(equipment.getQuality());
		progressDurability.setProgress((int)equipment.getDurability());
		btnSell.setOnClickListener(new OnClickHandler(equipment.getId()));
		btnSell.setClickable(!equipment.isOffer());
		btnSell.setEnabled(!equipment.isOffer());
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id;
		
		public OnClickHandler(String i){
			id = i;
		}
		
		@Override
		public void onClick(View v) {
			android.util.Log.d("Equipment Adapter", "Button with id list "+id+" pressed");
		}
	}
}

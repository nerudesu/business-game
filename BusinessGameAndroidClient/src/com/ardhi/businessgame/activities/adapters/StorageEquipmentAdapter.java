package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.StorageTabContentActivity;
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

	private StorageTabContentActivity act;
	private ArrayList<StorageEquipment> equipments;
	private static LayoutInflater inflater = null;
	
	public StorageEquipmentAdapter(StorageTabContentActivity a, ArrayList<StorageEquipment> e) {
		act = a;
		equipments = e;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		Button btnSell = (Button)v.findViewById(R.id.btn_sell),
				btnAttach = (Button)v.findViewById(R.id.btn_attach);
		
		StorageEquipment equipment = equipments.get(pos);
		
		txtEquipment.setText(equipment.getEquipment());
		txtSize.setText("Size : "+equipment.getSize());
		rateQuality.setRating(equipment.getQuality());
		progressDurability.setProgress((int)equipment.getDurability());
		btnSell.setOnClickListener(new OnClickHandlerSell(equipment.getId()));
		btnAttach.setOnClickListener(new OnClickHandlerAttach(equipment.getId(),equipment.getEquipment()));
		return v;
	}
	
	private class OnClickHandlerSell implements View.OnClickListener{
		private String id;
		
		public OnClickHandlerSell(String i){
			id = i;
		}
		
		@Override
		public void onClick(View v) {
			act.showMyDialog(id, 0, 2);
		}
	}
	
	private class OnClickHandlerAttach implements View.OnClickListener{
		private String id, type;
		
		public OnClickHandlerAttach(String i,String t){
			id = i;
			type = t;
		}

		@Override
		public void onClick(View v) {
			act.showAttachDialog(id,type);
		}
		
	}
}

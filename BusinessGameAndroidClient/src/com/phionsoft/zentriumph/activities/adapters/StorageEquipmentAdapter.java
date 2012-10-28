package com.phionsoft.zentriumph.activities.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.phionsoft.zentriumph.R;
import com.phionsoft.zentriumph.activities.StorageTabActivity;
import com.phionsoft.zentriumph.models.StorageEquipment;

public class StorageEquipmentAdapter extends BaseAdapter{

	private StorageTabActivity act;
	private ArrayList<StorageEquipment> equipments;
	private static LayoutInflater inflater = null;
	
	public StorageEquipmentAdapter(StorageTabActivity a, ArrayList<StorageEquipment> e) {
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
		
		ImageView img = (ImageView)v.findViewById(R.id.img);
		TextView txtEquipment = (TextView)v.findViewById(R.id.txt_equipment),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		ProgressBar progressDurability = (ProgressBar)v.findViewById(R.id.progress_durability);
		Button btnSell = (Button)v.findViewById(R.id.btn_sell),
				btnAttach = (Button)v.findViewById(R.id.btn_attach),
				btnFix = (Button)v.findViewById(R.id.btn_fix);
		
		StorageEquipment equipment = equipments.get(pos);
		
		img.setImageResource(equipment.getDraw());
		txtEquipment.setText(equipment.getEquipment());
		txtSize.setText("Size : "+equipment.getSize());
		rateQuality.setRating(equipment.getQuality());
		progressDurability.setProgress((int)equipment.getDurability());
		btnSell.setOnClickListener(new OnClickHandlerSell(equipment.getId()));
		btnAttach.setOnClickListener(new OnClickHandlerAttach(equipment.getId()));
		btnFix.setClickable((equipment.getDurability() < 95));
		btnFix.setEnabled((equipment.getDurability() < 95));
		btnFix.setOnClickListener(new OnClickHandlerFix(equipment.getId()));
		return v;
	}
	
	private class OnClickHandlerSell implements View.OnClickListener{
		private String id;
		
		public OnClickHandlerSell(String i){
			id = i;
		}
		
		@Override
		public void onClick(View v) {
			act.showSellDialog(3, id, 0);
		}
	}
	
	private class OnClickHandlerAttach implements View.OnClickListener{
		private String id;
		
		public OnClickHandlerAttach(String i){
			id = i;
		}

		@Override
		public void onClick(View v) {
			act.showAttachDialog(id);
		}
		
	}
	
	private class OnClickHandlerFix implements View.OnClickListener{
		private String id;
		
		public OnClickHandlerFix(String i){
			id = i;
		}

		@Override
		public void onClick(View v) {
			act.showFixDialog(id);
		}
		
	}
}

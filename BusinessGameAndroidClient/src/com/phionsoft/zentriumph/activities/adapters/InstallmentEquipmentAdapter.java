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
import com.phionsoft.zentriumph.activities.SectorDetailTabActivity;
import com.phionsoft.zentriumph.models.InstallmentEquipment;

public class InstallmentEquipmentAdapter extends BaseAdapter{

	private SectorDetailTabActivity act;
	private ArrayList<InstallmentEquipment> equipments;
	private static LayoutInflater inflater = null;
	
	public InstallmentEquipmentAdapter(SectorDetailTabActivity a, ArrayList<InstallmentEquipment> e) {
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
			v = inflater.inflate(R.layout.listrow_installment_equipment, null);
		}
		ImageView img = (ImageView)v.findViewById(R.id.img);
		TextView txtEquipment = (TextView)v.findViewById(R.id.txt_equipment),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		ProgressBar progressDurability = (ProgressBar)v.findViewById(R.id.progress_durability);
		Button btnDetach = (Button)v.findViewById(R.id.btn_detach);
		
		InstallmentEquipment equipment = equipments.get(pos);
		
		img.setImageResource(equipment.getDraw());
		txtEquipment.setText(equipment.getEquipment());
		txtSize.setText("Size : "+equipment.getSize());
		rateQuality.setRating(equipment.getQuality());
		progressDurability.setProgress((int)equipment.getDurability());
		btnDetach.setOnClickListener(new OnClickHandler(equipment.getId(),equipment.getEquipment()));
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id,eq;
		
		public OnClickHandler(String i,String e){
			id = i;
			eq = e;
		}
		
		@Override
		public void onClick(View v) {
			act.dialog(1, id, eq).show();
		}
	}
}

package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.Installment;

public class SectorAdapter extends BaseAdapter{

	private Activity act;
	private ArrayList<Installment> installments;
	private static LayoutInflater inflater = null;
	
	public SectorAdapter(Activity a, ArrayList<Installment> i) {
		act = a;
		installments = i;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return installments.size();
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
			v = inflater.inflate(R.layout.listrow_sector_owned, null);
		}
		ImageView img = (ImageView)v.findViewById(R.id.img);
		TextView txtSector = (TextView)v.findViewById(R.id.txt_sector),
				txtZone = (TextView)v.findViewById(R.id.txt_zone),
				txtEfficiency = (TextView)v.findViewById(R.id.txt_efficiency),
				txtEffectivity = (TextView)v.findViewById(R.id.txt_effectivity);
		
		Installment installment = installments.get(pos);
		
		img.setImageResource(installment.getDraw());
		txtSector.setText(installment.getInstallment());
		txtZone.setText("Zone : "+installment.getZone());
		txtEfficiency.setText("Efficiency : "+installment.getEfficiency());
		txtEffectivity.setText("Effectivity : "+installment.getEffectivity()+"x");
		return v;
	}
}
